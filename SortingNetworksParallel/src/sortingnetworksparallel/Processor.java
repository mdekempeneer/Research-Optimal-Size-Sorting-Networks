package sortingnetworksparallel;

import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sortingnetworksparallel.memory.ObjArrayList;

/**
 *
 * @author Admin
 */
public class Processor {

    private final short nbChannels;
    private final int upperBound;

    private final WorkPool workPool;
    private final int maxOuterComparator;
    private final int maxShifts;
    private final int[] allOnesList;
    private final byte[] allMinusOneList;

    //Statistics
//    private static long uniqueCounter = 0;
//    private static long redundantCounter = 0;
//    private static long kLengthCounter = 0;
//    private static long pLengthCounter = 0;
//    private static long lLengthCounter = 0;
//    private static long emptyPosCounter = 0;
//    private static long networkPermCounter = 0;
//    private long permCount = 0;

    /* IO */
    public boolean shouldSave;
    private final String savePath;

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than or equal to
     * upperBound.
     *
     * @param nbChannels The amount of channels for the networks.
     * @param upperBound The maximum amount of comparators to use.
     * @param savePath The absolute path where to save the file.
     * @param innerSize The amount of the inner size of the lists for each
     * thread.
     * @param percThreads The percentage of usage of the threads.
     */
    public Processor(short nbChannels, int upperBound, String savePath, int innerSize, double percThreads) {
        this.nbChannels = nbChannels;
        this.upperBound = upperBound;
        this.maxOuterComparator = ((1 << (nbChannels - 1)) | 1);
        this.maxShifts = nbChannels - 2;
        this.allOnesList = getAllOnesList((byte) nbChannels);
        this.allMinusOneList = getAllMinusOneList((byte) nbChannels);

        this.savePath = savePath;
        this.shouldSave = false;

        this.workPool = new WorkPool(this, nbChannels, innerSize, percThreads);
    }

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than or equal to
     * upperBound.
     *
     * @param nbChannels The amount of channels for the networks.
     * @param upperBound The maximum amount of comparators to use.
     * @param innerSize he amount of the inner size of the lists for each
     * thread.
     * @param percThreads The percentage of usage of the threads.
     */
    public Processor(short nbChannels, int upperBound, int innerSize, double percThreads) {
        this(nbChannels, upperBound, "", innerSize, percThreads);
    }

    public short[] process(ObjArrayList<short[][]> oldL, int startIndex, ObjArrayList<short[][]> newL, short nbComp) {
        /* Initialize inputs */
        long begin = System.currentTimeMillis();
        ObjArrayList<short[][]> NList = workPool.performCycle(oldL, startIndex, newL, nbComp);
        oldL = null;
        newL = null;
        long took = System.currentTimeMillis() - begin;
        nbComp++;
        System.out.println("Cycle complete with " + nbComp + " comps and size " + NList.size() + " took " + took + " ms");

        //cycle
        do {
            begin = System.currentTimeMillis();
            NList = workPool.performCycle(NList, nbComp);
            took = System.currentTimeMillis() - begin;
            nbComp++;

            System.out.println("Cycle complete with " + nbComp + " comps and size " + NList.size() + " took " + took + " ms");

            /*if (shouldSave) {
             System.out.println("Saving Data");
             save(NList, nbComp);
             System.exit(0);
             }*/
 /*//Tests if list only contains non subsumable.
             System.out.println("Testing if all pruned");
             if (innerPruneTest(NList)) {
             NList.fixNulls();
             NList.trim();
             System.out.println("[ERROR]: Found unpruned" + NList.size());
             }*/
        } while (NList.size() > 1 && nbComp < upperBound);

        workPool.shutDown();

        //System.out.println("#Unique " + uniqueCounter + "; #Redundant " + redundantCounter);
        //System.out.println("#kLengthCounter " + kLengthCounter + "; #pLengthCounter " + pLengthCounter + "; #lLengthCounter " + lLengthCounter);
        //System.out.println("#emptyPosCounter " + emptyPosCounter);
        //System.out.println("#networkPermCounter " + networkPermCounter);

        /* Return result */
        if (NList.size() >= 1) {
            return NList.get(0)[0];
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public short[] process() {
        /* Initialize inputs */
        ObjArrayList<short[][]> NList = firstTimeGenerate(getOriginalInputs(upperBound));
        innerPrune(NList);
        NList.fixNulls();
        NList.trim();
        short nbComp = 1;

        do {
            long begin = System.currentTimeMillis();
            NList = workPool.performCycle(NList, nbComp);
            long took = System.currentTimeMillis() - begin;
            nbComp++;

            System.out.println("Cycle complete with " + nbComp + " comps and size " + NList.size() + " took " + took + " ms");

            /*if (shouldSave) {
             System.out.println("Saving Data");
             save(NList, nbComp);
             }*/
 /*//Tests if list only contains non subsumable.
             System.out.println("Testing if all pruned");
             if (innerPruneTest(NList)) {
             NList.fixNulls();
             NList.trim();
             System.out.println("[ERROR]: Found unpruned" + NList.size());
             }*/
        } while (NList.size() > 1 && nbComp < upperBound);

        workPool.shutDown();

        //System.out.println("#Unique " + uniqueCounter + "; #Redundant " + redundantCounter);
        //System.out.println("#kLengthCounter " + kLengthCounter + "; #pLengthCounter " + pLengthCounter + "; #lLengthCounter " + lLengthCounter);
        //System.out.println("#emptyPosCounter " + emptyPosCounter);
        //System.out.println("#networkPermCounter " + networkPermCounter);

        /* Return result */
        if (NList.size() >= 1) {
            return NList.get(0)[0];
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
//    public short[] process(ObjArrayList<short[][]> loadedN) {
//
//        /* Initialize inputs */
//        N = loadedN;
//        prune();
//        System.out.println("Finished pruning loaded data.");
//        //Calc nbComp
//        short nbComp = 0;
//        short[][] network = N.get(0);
//        for (short i = 0; i < network[0].length; i++) {
//            if (network[0][i] == 0) {
//                nbComp = i;
//                break;
//            }
//        }
//        System.out.println("Finished 0-" + (nbComp - 1) + ", started at " + nbComp);
//
//        /* Process N */
//        do {
//            generate(nbComp);
//            prune();
//            nbComp++;
//
//            //System.out.println("permCount: " + permCount);
//            //permCount = 0;
//            //this.printInputs(N.get(0)[0]);
//            //System.out.println(N.size64());
//        } while (N.size() > 1 && nbComp < upperBound);
//
//        /* Return result */
//        if (N.size() >= 1) {
//            return N.get(0)[0];
//        } else {
//            return null;
//        }
//    }
    /**
     * Generate a list of all possible networks with 1 comparator.
     *
     * @param defaultNetwork A network that is considered default. This means it
     * has all possible outputs.
     * @return A list of all possible networks with 1 comparator.
     */
    private ObjArrayList<short[][]> firstTimeGenerate(short[][] defaultNetwork) {
        int capacity = (nbChannels * (nbChannels - 1)) / 2;
        ObjArrayList<short[][]> networkList = new ObjArrayList(capacity);
        int cMaxShifts = maxShifts;
        short comp;
        int number;
        int outerShift;

        /* For all comparators */
        for (number = 3; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
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
    public void processData(short[][] data, short newComp, int startIndex) {
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
     * @see #isRedundantComp(short[][], short)
     */
    public ObjectArrayList<short[][]> generate(final ObjArrayList<short[][]> networkList, final int startIndex, final int length, final short nbComp) {
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
     * Generate networks by expanding the given network with all possible
     * comparators. (Redundant comparators are neglected, see isRedundantComp)
     *
     * @param network The network to expand from.
     * @param nbComp The outerIndex of the comparator (data[0][nbComp]) to start
     * working on.
     * @return A list of generated networks, expanded by all possible
     * comparators to the given network.
     * @see #isRedundantComp(short[][], short)
     */
    public ObjectArrayList<short[][]> generate(short[][] network, short nbComp) {
        /* Setup environment */
        ObjectArrayList<short[][]> result = new ObjectArrayList();
        int cMaxShifts;
        short comp;
        int number;
        int outerShift;

        /* Start Generate work */
 /* For all comparators */
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

    public boolean innerPruneTest(AbstractObjectList<short[][]> networkList) {
        boolean found = false;

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
                            found = true;
                        } else if (subsumes(network2, network1)) {
                            networkList.remove(index);
                            index--;
                            found = true;
                            break;
                        }
                    }
                }
            }
        }

        return found;
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
     * @param skipSize The networks with outerIndex >= networkIndex and &lt
     * skipSize+networkIndex aren't checked.
     *
     */
    public void prune(ObjArrayList<short[][]> networkList, final int networkIndex, final int skipSize) {
        int bound = networkList.size();
        
        for (int outerIndex = 0; outerIndex < bound; outerIndex++) {
            if (outerIndex != networkIndex) {
                short[][] network2 = networkList.get(outerIndex);

                if (network2 != null) {

                    for (int i = 0; i < skipSize; i++) { //for all in the innerPrune
                        int innerIndex = networkIndex + i;
                        short[][] network = networkList.get(innerIndex);
                        if (network != null) { //else already removed.

                            if (subsumes(network, network2)) {
                                if (networkList.get(innerIndex) != null) { //recheck
                                    networkList.remove(outerIndex);
                                }
                                break;
                            } else if (subsumes(network2, network)) {
                                networkList.remove(innerIndex);
                                //break;
                            }

                        }
                    }
                }
            } else {
                outerIndex += skipSize - 1;
            }
        }
        
        /* The same but using the updated size */
        for (int outerIndex = bound; outerIndex < networkList.size(); outerIndex++) {
            if (outerIndex != networkIndex) {
                short[][] network2 = networkList.get(outerIndex);

                if (network2 != null) {

                    for (int i = 0; i < skipSize; i++) { //for all in the innerPrune
                        int innerIndex = networkIndex + i;
                        short[][] network = networkList.get(innerIndex);
                        if (network != null) { //else already removed.

                            if (subsumes(network, network2)) {
                                if (networkList.get(innerIndex) != null) { //recheck
                                    networkList.remove(outerIndex);
                                }
                                break;
                            } else if (subsumes(network2, network)) {
                                networkList.remove(innerIndex);
                                //break;
                            }

                        }
                    }
                }
            } else {
                outerIndex += skipSize - 1;
            }
        }
    }
//}

    /**
     * Check whether the output of network1 is a part of or equal to the output
     * of network2.
     *
     * @param network1 The first network.
     * @param network2 The second network.
     * @return Whether outputs(network1) is equal to or part of
     * outputs(network2).
     */
    private boolean isValidPermutation(short[][] network1, short[][] network2) {
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

    private boolean isValidPermutation(short[][] network1, short[][] network2, byte[] permutor) {
        /*  Reduce work: Lemma 6:
         C1 subsumes C2 => P(w(C1, x, k)) C= w(C2, x, k)
         */

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
                //}

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
    private boolean subsumes(short[][] network1, short[][] network2) {
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            if (network1[nbOnes].length > network2[nbOnes].length) {
                //kLengthCounter++;
                return false;
            }
        }

        int maxIndex = (nbChannels - 1) << 2;
        for (int index = 1; index < maxIndex;) {
            if (network1[nbChannels][index] > network2[nbChannels][index]) {
                //pLengthCounter++;
                return false;
            }
            index += 2;
            if (network1[nbChannels][index] > network2[nbChannels][index]) {
                //lLengthCounter++;
                return false;
            }
            index += 2;
        }

        /*for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
         if (network1[nbChannels][(nbOnes << 2) - 3] > network2[nbChannels][(nbOnes << 2) - 3]) {
         //if (getLengthOfW(network1, 0, nbOnes) > getLengthOfW(network2, 0, nbOnes)) {
         return false;
         }
         if (network1[nbChannels][(nbOnes << 2) - 1] > network2[nbChannels][(nbOnes << 2) - 1]) {
         //if (getLengthOfW(network1, 1, nbOnes) > getLengthOfW(network2, 1, nbOnes)) {
         return false;
         }
         }*/
        if (isValidPermutation(network1, network2)) {
            //System.err.println("It was true");
            return true;
        }

        //networkPermCounter++;
        return existsAValidPerm(network1, network2);

        //TODO: Old code - checks ALL permutations.
//        byte[] currPerm = new byte[this.identityElement.length];
//        System.arraycopy(this.identityElement, 0, currPerm, 0, nbChannels);
//
//        while ((currPerm = Permute.getNextPermutation(currPerm)) != null) {
//            if (isValidPermutation(network1, network2, currPerm)) {
//                return true;
//            }
//        }
//
//        return false;
    }

    /**
     * Check if a valid permutation exists. It creates a potential list of
     * permutations that might work by using the network information (P and L)
     * and iterates trying these permutations to find a correct one.
     *
     * @param network1 The
     * @param network2
     * @return
     */
    public boolean existsAValidPerm(short[][] network1, short[][] network2) {
        //Create a list full of allOnes - on every Position can be every number.
        int allOnes = (1 << nbChannels) - 1;
        int[] posList = new int[nbChannels];
        System.arraycopy(this.allOnesList, 0, posList, 0, nbChannels);

        //Compute positions.
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            int L2 = network2[nbChannels][(nbOnes << 2) - 2]; //L for network 2.
            int P2 = network2[nbChannels][(nbOnes - 1) << 2]; //P for network 2.

            int revLPos = allOnes ^ network1[nbChannels][(nbOnes << 2) - 2]; //Positions of 0 for L.
            int revPPos = allOnes ^ network1[nbChannels][(nbOnes - 1) << 2]; //Positions of 0 for P.

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

        //return testPossiblePermutations(network1, network2, Ps);
    }

    public boolean testPossiblePermutations(short[][] network1, short[][] network2, byte[][] Ps) {
        int allOnes = (1 << nbChannels) - 1; //TODO: Delete if not used.
        int lastOuterIndex = nbChannels - 1;
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

    public boolean checkAllRelevantPermutations(short[][] network1, short[][] network2, byte[][] Ps, int currIndex, byte[] soFar, int posTaken) {
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
     * @param upperBound
     * @return range from 2 to (2^nbChannels-1) excluding all sorted (binary)
     * ones.
     */
    public short[][] getOriginalInputs(int upperBound) {
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
    private static short swapCompare(short input, short comp) {
        int pos1 = 31 - Integer.numberOfLeadingZeros(comp);
        int pos2 = Integer.numberOfTrailingZeros(comp);
        //(input >> pos1) & 1 = first (front bit)
        //(input >> pos2) & 1 = 2nd (back bit)
        return (((input >> pos1) & 1) <= ((input >> pos2) & 1)) ? input : (short) (input ^ comp);// TADAM!!!
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
            //System.out.println(Integer.toBinaryString(value)); //-- DEBUG!
            t = value | (value - 1);
            value = (t + 1) | (((~t & -~t) - 1) >> (Integer.numberOfTrailingZeros(value) + 1));
            index++;
        } while (value < max);

        return result;
    }

    /**
     * Get an array of all possible comparators.
     *
     * @return The array of all possible comparators.
     */
//    private short[] getAllComps() {
//        short[] result = new short[nbChannels * (nbChannels - 1) / 2];
//        int cMaxShifts;
//        short comp;
//        int number;
//        int outerShift;
//        int outerIndex = 0;
//
//        /* For all comparators */
//        for (number = 3, cMaxShifts = maxShifts; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
//            comp = (short) number;
//            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
//                result[outerIndex] = comp;
//                outerIndex++;
//            }
//        }
//        return result;
//    }
    /**
     * Get the Identity element for the permutation of nbChannels.
     *
     * @param nbChannels The amount of channels of the network.
     * @return The Identity element which for a permutation returns the same as
     * the input.
     */
    //TODO: Remove death code.
    /*private static byte[] getIdentityElement(byte nbChannels) {
     byte[] result = new byte[nbChannels];
     for (byte i = 0; i < nbChannels; i++) {
     result[i] = i;
     }
     return result;
     }*/
    /**
     * Create an array of nbChannels elements equal to allOnes. allOnes = (1
     * &lt&lt nbChannels) - 1
     *
     * @param nbChannels The amount of the elements and the amount of 1's.
     * @return The created array.
     */
    private static int[] getAllOnesList(byte nbChannels) {
        int allOnes = (1 << nbChannels) - 1;
        int[] list = new int[nbChannels];

        for (int i = 0; i < list.length; i++) {
            list[i] = allOnes;
        }
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
     * Check if the given comp is redundant given the data present.
     *
     * @param data The date before the comp would be added.
     * @param comp The comp that would be added.
     * @return True if adding the comparator does not change any output. False
     * otherwise.
     */
//    private boolean isRedundantComp(short[][] data, short comp) {
//        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
//            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
//                short output = data[nbOnes][innerIndex];
//                if (output != swapCompare(output, comp)) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
    /**
     * Check if the given comp is redundant given the data present. If it is
     * not, the first outerIndex of changing outputArr will be returned.
     *
     * @param data The date before the comp would be added.
     * @param comp The comp that would be added.
     * @return -1 If the comparator is redundant else a number 0 &lt= x &lt
     * nbChannels
     */
    private int getChangeIndex(short[][] data, short comp) {
        
        //for all W( k=0)
        //gesorteerd => alle 1 'en rechts => geen 0 rechts => een 0 rechts
        //een 0 op de plaats van de channel voor k = het tegengestelde van het verwachte = goed.
        
        //TODO: If one uses sorted channel, also redundant.
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                short output = data[nbOnes][innerIndex];
                if (output != swapCompare(output, comp)) {
                    return nbOnes;
                }
            }
        }
        
        //test if this happens else we could just do else { return 1 }
        return -1;
    }

    /*  Reduce work: Lemma 6:
     C1 subsumes C2 => P(w(C1, x, k)) C= w(C2, x, k)
     */
    public boolean checkPermutationPartOf(short[][] network1, short[][] network2) {
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
     * TODO
     *
     * @param data
     * @param comp
     * @param startIndex
     */
    public void processW(short[][] data, short comp, int startIndex) {
        short[] wResult = new short[data[nbChannels].length];

        int wIndexCounter;
        boolean foundL;
        boolean foundP;

        if (startIndex != 1) {
            System.arraycopy(data[nbChannels], 0, wResult, 0, (startIndex - 1) << 2);
        }

        for (int nbOnes = startIndex; nbOnes < nbChannels; nbOnes++) {
            wIndexCounter = (nbOnes - 1) << 2;

            int oldP = data[nbChannels][wIndexCounter];
            int oldL = data[nbChannels][wIndexCounter + 2];

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

    /**
     * Set shouldSave to true.
     */
    public void initiateSave() {
        this.shouldSave = true;
        workPool.shutDownAndSave();
    }

    private void save(ObjArrayList<short[][]> NList, short nbComp) {
        if (savePath != null && !savePath.equals("")) {
            ObjectOutputStream oos = null;
            try {
                //oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.savePath)));
                oos = new ObjectOutputStream(new FileOutputStream(this.savePath));
                oos.writeObject(NList);
                oos.writeShort(nbComp);

            } catch (IOException ex) {
                Logger.getLogger(Processor.class
                        .getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (oos != null) {
                    try {
                        oos.close();

                    } catch (IOException ex) {
                        Logger.getLogger(Processor.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public void save(ObjArrayList<short[][]> oldL, int startIndex, ObjArrayList<short[][]> newL, short nbComp) {
        if (savePath != null && !savePath.equals("")) {
            ObjectOutputStream oos = null;
            try {
                //oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.savePath)));
                oos = new ObjectOutputStream(new FileOutputStream(this.savePath));
                oos.writeObject(oldL);
                oos.writeInt(startIndex);
                oos.writeObject(newL);
                oos.writeShort(nbComp);

            } catch (IOException ex) {
                Logger.getLogger(Processor.class
                        .getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (oos != null) {
                    try {
                        oos.close();

                    } catch (IOException ex) {
                        Logger.getLogger(Processor.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public void processData(short[][] data, short newComp) {
        processData(data, newComp, 1);
    }
}
