/**
 * MIT License
 *
 * Copyright (c) 2015-2016 Mathias DEKEMPENEER, Vincent DERKINDEREN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sortingnetworksparallel;

import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Arrays;
import sortingnetworksparallel.memory.NullArray;

/**
 * Processes the given amount of channels and the maximum amount of comparators
 * (upperbound) and returns a sorting network of optimal size when the
 * upperbound is high enough.
 *
 * @author Mathias Dekempeneer and Vincent Derkinderen
 * @version 1.0
 */
public class Processor {

    private final short nbChannels;
    private final int upperBound;

    private final WorkPool workPool;
    private final int maxOuterComparator;
    private final int maxShifts;
    private final int[] allOnesList;
    private final byte[] allMinusOneList;

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than or equal to
     * upperBound.
     *
     * @param nbChannels The amount of channels for the networks.
     * @param upperBound The maximum amount of comparators to use.
     * @param innerSize The amount of the inner size of the lists for each
     * thread.
     * @param percThreads The percentage of usage of the threads.
     */
    public Processor(final short nbChannels, final int upperBound, final int innerSize, final double percThreads) {
        this.nbChannels = nbChannels;
        this.upperBound = upperBound;
        this.maxOuterComparator = ((1 << (nbChannels - 1)) | 1);
        this.maxShifts = nbChannels - 2;
        this.allOnesList = getAllOnesList((byte) nbChannels);
        this.allMinusOneList = getAllMinusOneList((byte) nbChannels);

        this.workPool = new WorkPool(this, nbChannels, innerSize, percThreads);
    }

    /**
     * For a given nbChannels and upperBound find a minimal sorting network if
     * there is one with less than or equal to upperBound.
     *
     * @return The found optimal size sorting network.
     */
    public short[] process() {
        long initMem = Runtime.getRuntime().totalMemory();
        /* Initialize inputs */
        NullArray NList = firstTimeGenerate(getOriginalInputs());
        innerPrune(NList);
        NList.fixNulls();
        NList.trim();
        short nbComp = 1;

        do {
            final long begin = System.currentTimeMillis();
            NList = workPool.performCycle(NList, nbComp);
            final long took = System.currentTimeMillis() - begin;
            nbComp++;

            System.gc();
            long mem = (Runtime.getRuntime().totalMemory() - initMem) / 1000;
            System.out.println("Cycle complete with " + nbComp + " comps and size " + NList.size() + "(" + mem + " kB)" + " took " + took + " ms");
        } while (NList.size() > 1 && nbComp < upperBound);

        workPool.shutDown();

        /* Return result */
        if (NList.size() >= 1) {
            return NList.get(0)[0];
        } else {
            return null;
        }
    }

    /**
     * Generate a list of all possible networks with 1 comparator.
     *
     * @param defaultNetwork A network that is considered default. This means it
     * has all possible outputs.
     * @return A list of all possible networks with 1 comparator.
     */
    private NullArray firstTimeGenerate(short[][] defaultNetwork) {
        final int capacity = (nbChannels * (nbChannels - 1)) / 2;
        NullArray networkList = new NullArray(capacity);
        int cMaxShifts = maxShifts;
        short comp;

        /* For all comparators */
        for (int number = 3; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            for (int outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                //new Network (via clone)
                short[][] data = defaultNetwork.clone();
                //Fill
                data[0] = new short[2];
                data[0][0] = comp;
                processData(data, comp, 1);
                processW(data, comp, 1);

                networkList.add(data);
            }
        }

        return networkList;
    }

    /**
     * Processes the data for the new comparator. Adding the comparator to the
     * data[0] is assumed to be done already.
     *
     * @param data The network
     * @param newComp The comparator to process the data on.
     * @param startIndex The outerIndex of where to start. (= 1 will cover
     * everything.)
     */
    public void processData(short[][] data, final short newComp, final int startIndex) {
        short[] processed;
        boolean found;

        for (int nbOnes = startIndex; nbOnes < nbChannels; nbOnes++) {
            processed = new short[data[nbOnes].length];
            int counter = 0;
            boolean foundNew = false;

            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                short oldValue = data[nbOnes][innerIndex];
                short value = swapCompare(oldValue, newComp);
                if (value != oldValue) {
                    foundNew = true;
                }
                found = false;
                for (int i = counter - 1; i >= 0; i--) {
                    if (processed[i] == value) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    processed[counter++] = value;
                }
            }

            if (foundNew) { //CAUTION! Don't do this 'shared array' with writing lists to disk.
                short[] result = new short[counter];
                System.arraycopy(processed, 0, result, 0, result.length);
                data[nbOnes] = result;
            }
        }
    }

    /**
     * Generate networks by expanding the given networks with all possible
     * comparators. (Redundant comparators are neglected, see isRedundantComp)
     *
     * This assumes networkLists back-array contains no nulls upto size().
     *
     * @param networkList The list that contains the networks to expand.
     * @param startIndex The index of the first network in the list to expand.
     * @param length The amount of networks to expand. (startIndex to
     * startIndex+length-1)
     * @param nbComp The outerIndex of the comparator (data[0][nbComp]) to start
     * working on.
     * @return A list of generated networks, expanded by all possible
     * comparators to the given network.
     * @see #getChangeIndex(short[][], short)
     */
    public ObjectArrayList<short[][]> generate(final NullArray networkList, final int startIndex, final int length, final short nbComp) {
        /* Setup environment */
        ObjectArrayList<short[][]> result = new ObjectArrayList();
        int cMaxShifts;
        short comp;
        int number;
        int outerShift;

        /* Start Generate work */
        int maxIndex = Math.min(startIndex + length, networkList.size());
        for (int i = startIndex; i < maxIndex; i++) {
            short[][] network = networkList.get(i);

            int prevComp = network[0][nbComp - 1];
            int prevCompMZ = prevComp >> Integer.numberOfTrailingZeros(prevComp); // e.g 001010 -> 00101

            for (number = 3, cMaxShifts = maxShifts; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
                comp = (short) number;
                int compMZ = comp >> Integer.numberOfTrailingZeros(comp);

                for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                    if (((prevComp & comp) != 0 && prevComp != comp) || (prevCompMZ < compMZ || (compMZ == prevCompMZ && prevComp < comp))) {
                        int index = getChangeIndex(network, comp);
                        if (index != -1) { //!= redundant
                            short[][] data = network.clone();
                            //Fill comp
                            data[0] = new short[nbComp + 1];
                            System.arraycopy(network[0], 0, data[0], 0, nbComp);
                            data[0][nbComp] = comp;

                            processData(data, comp, index);
                            processW(data, comp, index);

                            result.add(data);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Prune N by removing every network that is being subsumed by another
     * network. This operation is not concurrent. N should not be modified/read
     * while this operation is ongoing.
     *
     * @param networkList The list of networks to prune in.
     */
    public void innerPrune(AbstractObjectList<short[][]> networkList) {
        ObjectListIterator<short[][]> iter;
        for (int index = 0; index < networkList.size() - 1; index++) {
            short[][] network1 = networkList.get(index);
            if (network1 != null) {
                iter = networkList.listIterator(index + 1);

                while (iter.hasNext()) {
                    short[][] network2 = iter.next();
                    if (network2 != null) {
                        if (subsumes(network1, network2)) {
                            iter.remove();
                        } else if (subsumes(network2, network1)) {
                            networkList.remove(index);
                            index--;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Prune in the given list of networks by using subsumes with the given
     * network located on networkList.get(networkIndex). When the same network
     * is found in the network list it will be ignored. When the network is to
     * be removed by the subsumes rule it will remove it from the networkList
     * and stop execution even before the whole list is iterated over.
     *
     * @param networkList The list of networks to prune on.
     * @param networkIndex The outerIndex of the network located in the
     * networkList used to perform subsumes with (in 2 directions).
     * @param skipSize The networks with outerIndex &gt;= networkIndex and &lt;
     * skipSize+networkIndex aren't checked.
     *
     */
    public void prune(NullArray networkList, final int networkIndex, final int skipSize) {
        short[][] buffered = null;
        short[][] before = null;

        for (int outerIndex = 0; outerIndex < networkIndex; outerIndex++) {
            short[][] network2 = (buffered != null) ? buffered : networkList.get(outerIndex);
            buffered = null;

            if (network2 != null && network2.length != 1) {
                before = network2;

                for (int i = 0; i < skipSize; i++) { //for all in the innerPrune
                    int innerIndex = networkIndex + i;
                    short[][] network = networkList.get(innerIndex);
                    if (network != null && network.length != 1) { //else already removed.

                        if (subsumes(network, network2)) {
                            networkList.remove(outerIndex);
                            break;
                        } else if (subsumes(network2, network)) {
                            networkList.remove(innerIndex);
                        }

                    }
                }

            } else if (network2 == null) {
                if (before == null || before.length != 1) {
                    int index = outerIndex + 1;
                    buffered = null;
                    /* find next non null */
                    while (index < networkIndex && (buffered = networkList.get(index)) == null) {
                        index++;
                    }

                    /* get # null in a row behind the first null*/
                    short difference = (short) (index - outerIndex - 1); //TODO: possible overload when null seq > 65000...
                    /* Store cNull */
                    if (difference > 0) {
                        short[][] cNull = new short[1][1];
                        cNull[0][0] = difference;
                        networkList.set(outerIndex, cNull);
                        before = cNull;
                        outerIndex += difference;
                    }

                } else { //length == 1
                    before[0][0]++;
                }
            } else { //Cnull
                int skip = network2[0][0]; //Amount of nulls after this.
                outerIndex += skip;
                if (before != null && before.length == 1) { //came from cNull
                    before[0][0] += skip + 1;
                } else {
                    before = network2;
                }
            }
        }
    }

    /**
     * Check whether the output of network1 is a part of or equal to the output
     * of network2.
     *
     * @param network1 The first network.
     * @param network2 The second network.
     * @return Whether outputs(network1) is equal to or part of
     * outputs(network2).
     */
    private boolean isValidPermutation(final short[][] network1, final short[][] network2) {
        /*  Reduce work: Lemma 6:
         C1 subsumes C2 => P(getLengthOfW(C1, x, k)) C= getLengthOfW(C2, x, k)
         */
        if (!checkPermutationPartOf(network1, network2)) {
            return false;
        }

        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            for (short output : network1[nbOnes]) {
                boolean found = false;

                for (short output2 : network2[nbOnes]) {
                    if (output == output2) {
                        found = true;
                        break;
                    }
                }

                if (found == false) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check whether the permutation(output of network1) is a part of or equal
     * to the output of network2.
     *
     * @param network1 The first network.
     * @param network2 The second network.
     * @param permutor The permutation to use on network1
     * @return Whether permutor(outputs(network1)) is equal to or part of
     * outputs(network2).
     */
    private boolean isValidPermutation(final short[][] network1, final short[][] network2, final byte[] permutor) {
        /* Permute & Check outputs */
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            for (int innerIndex = 0; innerIndex < network1[nbOnes].length; innerIndex++) {
                int output = 0;
                boolean found = false;

                /* Compute permuted */
                for (int pIndex = permutor.length - 1; pIndex >= 0; pIndex--) {
                    output <<= 1;
                    output |= ((network1[nbOnes][innerIndex] >> permutor[pIndex]) & 1);
                }

                /* Check if output is partof network2 */
                for (short output2 : network2[nbOnes]) {
                    if (output == output2) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if network1 subsumes network2. Subsumes: E(permutation p):
     * p(outputs(network1)) part of or equal to outputs(network2). "Outputs of
     * network1 can be converted (permutation) to or as part of the outputs of
     * network2."
     *
     * @param network1 The first network as part of network1 subsumes? network2
     * @param network2 The second network as part of network1 subsumes? network2
     * @return Whether network1 subsumes network2.
     */
    private boolean subsumes(final short[][] network1, final short[][] network2) {
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            if (network1[nbOnes].length > network2[nbOnes].length) {
                return false;
            }
        }

        int maxIndex = (nbChannels - 1) << 2;
        for (int index = 1; index < maxIndex;) {
            if (network1[nbChannels][index] > network2[nbChannels][index]) {
                return false;
            }
            index += 2;
            if (network1[nbChannels][index] > network2[nbChannels][index]) {
                return false;
            }
            index += 2;
        }

        if (isValidPermutation(network1, network2)) {
            return true;
        }

        return existsAValidPerm(network1, network2);
    }

    /**
     * Check if a valid permutation exists. It creates a potential list of
     * permutations that might work by using the network information (P and L)
     * and iterates trying these permutations to find a correct one.
     *
     * @param network1 The first network as part of network1 subsumes? network2
     * @param network2 The second network as part of network1 subsumes? network2
     * @return Whether there exists a valid permutation by which
     * p(outputs(network1)) C= outputs(network2)
     */
    public boolean existsAValidPerm(final short[][] network1, final short[][] network2) {
        //Create a list full of allOnes - on every Position can be every number.
        final int allOnes = (1 << nbChannels) - 1;
        int[] posList = new int[nbChannels];
        System.arraycopy(this.allOnesList, 0, posList, 0, nbChannels);

        //Compute positions.
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            int L2 = network2[nbChannels][(nbOnes << 2) - 2]; //L for network 2.
            int P2 = network2[nbChannels][(nbOnes - 1) << 2]; //P for network 2.

            int revLPos = allOnes ^ network1[nbChannels][(nbOnes << 2) - 2]; //Positions of 0 for L.
            int revPPos = allOnes ^ network1[nbChannels][(nbOnes - 1) << 2]; //Positions of 1 for P.

            //TODO: Can we shorten result = revOnePos & revZeroPos by inline + logic?
            //and combine the two if's in the process.
            //int result = revLPos & revPPos;
            //Fill posList;
            if (L2 != allOnes || P2 != allOnes) {
                for (int i = 0; i < posList.length; i++) {
                    int mask = 1 << i;
                    if ((mask & L2) == 0) {
                        posList[i] &= revLPos;
                    }
                    if ((mask & P2) == 0) {
                        posList[i] &= revPPos;
                    }

                    //If possible positions are ever empty -> return false.
                    if (posList[i] == 0) {
                        //emptyPosCounter++;
                        return false;
                    }
                }
            }
        }

        /* If at certain position only 1 number possible,
           claim the number and remove from other positions.
         */
        for (int i = 0; i < posList.length; i++) {
            int value = posList[i];
            if (Integer.bitCount(value) == 1) {
                for (int j = 0; j < posList.length; j++) {
                    if (((value & posList[j]) != 0) && (i != j)) {
                        posList[j] -= value;
                        if (posList[j] == 0) {
                            return false;
                        }
                    }
                }
            }
        }

        /* check if every position is used.*/
        int checkAll = allOnes;
        for (int i = 0; i < posList.length; i++) {
            checkAll &= (~posList[i] & allOnes);
        }
        if (checkAll != 0) {
            return false;
        }

        /* Convert posList bit structure to bytes for permutations. */
        byte[][] Ps = new byte[nbChannels][];

        //TODO: Can this structure be improved?
        for (int i = 0; i < Ps.length; i++) {
            byte[] tempP = new byte[nbChannels]; //don't know initial capacity required.
            int countLengthPos = 0;
            int currP = posList[i];

            //Retrieve possible numbers from the bit form (currP).
            for (byte permIndex = 0; permIndex < nbChannels; permIndex++) {
                if ((1 << permIndex & currP) != 0) {// mask & posList[i] == 1 op die positie.
                    tempP[countLengthPos++] = permIndex;
                }
            }

            Ps[i] = new byte[countLengthPos]; //trim down to appropriate sizes.
            System.arraycopy(tempP, 0, Ps[i], 0, countLengthPos);
        }

        //Check all permutations of the given positions.
        return checkAllRelevantPermutations(network1, network2, Ps, 0, new byte[nbChannels], 0);

        // Iterative method to check the permutations
        //return testPossiblePermutations(network1, network2, Ps);
    }

    /**
     * Iterative method to check all permutations of the permutation table Ps.
     *
     * @param network1 The first network as part of network1 subsumes? network2
     * @param network2 The second network as part of network1 subsumes? network2
     * @param Ps The permutation table
     * @return Whether or not there exists a permutation such that network1
     * subsumes network2.
     */
    public boolean testPossiblePermutations(short[][] network1, short[][] network2, byte[][] Ps) {
        final int allOnes = (1 << nbChannels) - 1; //TODO: Delete if not used.
        final int lastOuterIndex = nbChannels - 1;
        int currOuterIndex = 0;
        int takenNumbers = 0;

        byte[] currPerm = new byte[nbChannels];
        byte[] indices = new byte[nbChannels];

        System.arraycopy(allMinusOneList, 0, currPerm, 0, currPerm.length);
        System.arraycopy(allMinusOneList, 0, indices, 0, indices.length);

        boolean ended = false;

        do {
            //currOuterIndex = Which outer outerIndex we're working with.
            //takenNumbers = bv outerIndex 0 een 1 = getal 1 is al genomen.

            while (currOuterIndex >= 0 && currOuterIndex <= lastOuterIndex) {
                if (indices[currOuterIndex] + 1 >= Ps[currOuterIndex].length) { // last Inner Index reached.
                    //reset inner
                    //takenNumbers -= (1 << currPerm[currOuterIndex]); //'untake' the number.
                    takenNumbers &= (allOnes - (1 << currPerm[currOuterIndex])); //TODO: should be the same as above.
                    currPerm[currOuterIndex] = -1;
                    indices[currOuterIndex] = -1;

                    //work on previous outerIndex.
                    currOuterIndex--;

                    //'untake' prev.
                    if (currOuterIndex >= 0) {
                        //takenNumbers &= (allOnes - (1 << currPerm[currOuterIndex])); //TODO: should be the same as above.
                    }
                } else {

                    //set next
                    int innerIndex = indices[currOuterIndex];
                    innerIndex++;
                    int lastInnerIndex = Ps[currOuterIndex].length - 1;
                    byte newNumber = Ps[currOuterIndex][innerIndex];

                    //get the right innerIndex for the next valid number.
                    while ((takenNumbers & (1 << newNumber)) != 0 && innerIndex < lastInnerIndex) { //Taken && <= lastIndex
                        newNumber = Ps[currOuterIndex][++innerIndex];
                    }

                    if ((takenNumbers & (1 << newNumber)) == 0) { //found next valid inner number.
                        //untake
                        if (currPerm[currOuterIndex] != -1) {
                            takenNumbers &= (allOnes - (1 << currPerm[currOuterIndex]));

                        }

                        //Set the new valid number.
                        indices[currOuterIndex] = (byte) innerIndex;
                        currPerm[currOuterIndex] = newNumber;
                        takenNumbers ^= (1 << newNumber);

                        //start next outerIndex.
                        currOuterIndex++;
                    } else { //reached the end and no valid found.
                        //reset inner
                        if (currPerm[currOuterIndex] != -1) {
                            takenNumbers &= (allOnes - (1 << currPerm[currOuterIndex])); //'untake' the number.
                            currPerm[currOuterIndex] = -1;
                            indices[currOuterIndex] = -1;
                        }

                        //work on previous outerIndex.
                        currOuterIndex--;
                    }
                }
            }

            //Found valid.
            if (currOuterIndex > lastOuterIndex) {
                if (isValidPermutation(network1, network2, currPerm)) {
                    return true;
                }
                currOuterIndex = currPerm.length - 1;
            } else {
                ended = true;
            }

        } while (!ended);
        return false;
    }

    /**
     * Recursive method to check all permutations of the permutation table Ps.
     *
     * @param network1 The first network as part of network1 subsumes? network2
     * @param network2 The second network as part of network1 subsumes? network2
     * @param Ps The permutation table
     * @param currIndex Used by the recursive algorithm, when calling this
     * method you should use 0
     * @param soFar The permutation so far, when calling this method you should
     * use new byte[nbChannels]
     * @param posTaken The positions already taken, when calling this method you
     * should use 0
     *
     * @return Whether or not there exists a permutation such that network1
     * subsumes network2
     */
    public boolean checkAllRelevantPermutations(final short[][] network1, final short[][] network2, byte[][] Ps, int currIndex, byte[] soFar, int posTaken) {
        if (currIndex == nbChannels - 1) {
            //Reached last permNumber.
            for (byte p : Ps[currIndex]) {

                if ((posTaken & (1 << p)) == 0) { //Check if not already exists.
                    soFar[currIndex] = p;
                    //test if valid perm and stop if it is.
                    //permCount++;
                    if (isValidPermutation(network1, network2, soFar)) {
                        return true;
                    }
                }
            }
            return false; //Reached end of last permNumber and no valid perm found.
        } else {
            for (byte p : Ps[currIndex]) {
                if ((posTaken & (1 << p)) == 0) { //Check if not already exists.
                    soFar[currIndex] = p;
                    int newPosTaken = (posTaken | (1 << p));
                    //Will stop iteration when a valid is found already.
                    if (checkAllRelevantPermutations(network1, network2, Ps, currIndex + 1, soFar, newPosTaken)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get all original inputs excluding the already sorted ones.
     *
     * @return range from 2 to (2^nbChannels-1) excluding all sorted (binary)
     * ones.
     */
    public short[][] getOriginalInputs() {
        /* 
         data[0] holds the lengths of the other shorts.
         data[1] holds outputs with 1 '1's.
         data[2] holds outputs with 2 '1's.
         ...
         data[n] nbChannels holds W(C,x,k) info.
         */
        short[][] data = new short[nbChannels + 1][];
        data[0] = new short[2];
        data[nbChannels] = new short[(nbChannels - 1) << 2];
        int wIndexCounter;

        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            data[nbOnes] = getPermutations((short) ((1 << nbOnes) - 1), nbChannels);
            wIndexCounter = (nbOnes - 1) << 2;

            data[nbChannels][wIndexCounter] = (short) ((1 << nbChannels) - 1);
            data[nbChannels][wIndexCounter + 1] = nbChannels;
            data[nbChannels][wIndexCounter + 2] = (short) ((1 << nbChannels) - 1);
            data[nbChannels][wIndexCounter + 3] = nbChannels;
        }

        return data;
    }

    /**
     * Get the output of the comparator comp given the input.
     *
     * @param input The input to give the comparator.
     * @param comp The comparator to get the output from.
     * @return The result by switching the bits in the input according to comp.
     */
    private static short swapCompare(final short input, final short comp) {
        int pos1 = 31 - Integer.numberOfLeadingZeros(comp);
        int pos2 = Integer.numberOfTrailingZeros(comp);
        //(input >> pos1) & 1 = first (front bit)
        //(input >> pos2) & 1 = 2nd (back bit)
        return (((input >> pos1) & 1) <= ((input >> pos2) & 1)) ? input : (short) (input ^ comp);
    }

    /**
     * Calculates the factorial of a given number.
     *
     * @param n The number of which the factorial has to be calculated
     * @return n!
     */
    public static long factorial(int n) {
        long result = 1;

        for (; n > 1; n--) {
            result *= n;
        }

        return result;
    }

    /**
     * Get all permutations possible using the start and the max amount of bits.
     *
     * @param start The start value. Normally a value with all 1's on the right
     * side.
     * @param maxBits The maximum amount of bits available. (nbChannels)
     * @return All permutations starting with start that are smaller than
     * (2^maxBits)-1
     */
    public static short[] getPermutations(short start, short maxBits) {
        //Calculate length
        int beginNbOnes = Integer.bitCount(start);
        float temp = factorial(maxBits) / factorial(beginNbOnes); //TODO: Could get more efficient
        temp /= factorial(maxBits - beginNbOnes);
        int length = (int) Math.ceil(temp);

        //Variables
        short[] result = new short[length];
        int value = start;
        int max = (1 << maxBits) - 1;
        int t;
        int index = 0;

        //Get all permutations.
        do {
            result[index] = (short) value;
            t = value | (value - 1);
            value = (t + 1) | (((~t & -~t) - 1) >> (Integer.numberOfTrailingZeros(value) + 1));
            index++;
        } while (value < max);

        return result;
    }

    /**
     * Create an array of nbChannels elements equal to allOnes. allOnes = (1
     * &lt&lt nbChannels) - 1
     *
     * @param nbChannels The amount of the elements and the amount of 1's.
     * @return The created array.
     */
    private static int[] getAllOnesList(final byte nbChannels) {
        final int allOnes = (1 << nbChannels) - 1;
        int[] list = new int[nbChannels];

        Arrays.fill(list, allOnes);
        return list;
    }

    /**
     * Create an array of nbChannels elements equal to -1.
     *
     * @param nbChannels The amount of the elements in the array.
     * @return The created array.
     */
    private static byte[] getAllMinusOneList(byte nbChannels) {
        byte[] list = new byte[nbChannels];
        Arrays.fill(list, (byte) -1);
        return list;
    }

    /**
     * Check if the given comp is redundant given the data present. If it is
     * not, the first outerIndex of changing outputArr will be returned. This
     * will return the amount of 1's of which a output set has changed.
     *
     * @param data The date before the comp would be added.
     * @param comp The comp that would be added.
     * @return -1 If the comparator is redundant else a number 1 &lt= x &lt
     * nbChannels
     */
    private int getChangeIndex(final short[][] data, final short comp) {
        //for all W( k=0)
        //gesorteerd => alle 1 'en rechts => geen 0 rechts => een 0 rechts
        //een 0 op de plaats van de channel voor k = het tegengestelde van het verwachte = goed.
        //TODO: If one uses sorted channel, also redundant.
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                final short output = data[nbOnes][innerIndex];
                if (output != swapCompare(output, comp)) {
                    return nbOnes;
                }
            }
        }
        return -1;
    }

    /**
     * Reduce work: Lemma 6: C1 subsumes C2 =&gt; P(w(C1, x, k)) C= w(C2, x, k)
     *
     * @param network1 C1
     * @param network2 C2
     * @return Whether or not network1 is maybe part of network2 according to
     * lemma 6
     */
    public boolean checkPermutationPartOf(final short[][] network1, final short[][] network2) {
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            int P1 = network1[nbChannels][(nbOnes - 1) << 2];
            int L1 = network1[nbChannels][(nbOnes << 2) - 2];
            int P2 = network2[nbChannels][(nbOnes - 1) << 2];
            int L2 = network2[nbChannels][(nbOnes << 2) - 2];

            //Test
            if (((P2 ^ ((1 << nbChannels) - 1)) & P1) != 0
                    || ((L2 ^ ((1 << nbChannels) - 1)) & L1) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Change the w(data, x, k) according to the new comparator and the already
     * existing data.
     *
     * @param data The network to update
     * @param comp The comparator which was added
     * @param startIndex The index where the first change should be made
     */
    public void processW(short[][] data, final short comp, final int startIndex) {
        short[] wResult = new short[data[nbChannels].length];

        int wIndexCounter;
        boolean foundL;
        boolean foundP;

        if (startIndex != 1) {
            System.arraycopy(data[nbChannels], 0, wResult, 0, (startIndex - 1) << 2);
        }

        for (int nbOnes = startIndex; nbOnes < nbChannels; nbOnes++) {
            wIndexCounter = (nbOnes - 1) << 2;

            final int oldP = data[nbChannels][wIndexCounter];
            final int oldL = data[nbChannels][wIndexCounter + 2];

            int P = (comp ^ ((1 << nbChannels) - 1)) & oldP;
            int L = (comp ^ ((1 << nbChannels) - 1)) & oldL;

            foundP = (oldP == P);
            foundL = (oldL == L);

            for (short output : data[nbOnes]) {
                if (!foundL) {
                    L = L | (output & comp);
                    if ((L & comp) == comp) {
                        foundL = true;
                    }
                }

                if (!foundP) {
                    P = P | ((output ^ ((1 << nbChannels) - 1)) & comp);
                    if ((P & comp) == comp) {
                        foundP = true;
                    }
                }

                /* Break; found both */
                if (foundP && foundL) {
                    break;
                }
            }
            wResult[wIndexCounter] = (short) P;
            wResult[wIndexCounter + 1] = (short) Integer.bitCount(P);
            wResult[wIndexCounter + 2] = (short) L;
            wResult[wIndexCounter + 3] = (short) Integer.bitCount(L);
        }
        data[nbChannels] = wResult;
    }
}
