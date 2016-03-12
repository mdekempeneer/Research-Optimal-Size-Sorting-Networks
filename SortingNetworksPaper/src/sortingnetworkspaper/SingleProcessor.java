package sortingnetworkspaper;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sortingnetworkspaper.memory.ObjArrayList;

/**
 *
 * @author Admin
 */
public class SingleProcessor {

    private final short nbChannels;
    private final int upperBound;
    private final int maxOuterComparator;
    private final int maxShifts;
    //private final byte[] identityElement;
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
//      private long permCount = 0;

    /* IO */
    private boolean shouldSave;
    private final String savePath;

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than or equal to
     * upperBound.
     *
     * @param nbChannels The amount of channels for the networks.
     * @param upperBound The maximum amount of comparators to use.
     * @param saveFlag Whether to save the previous prune.
     */
    public SingleProcessor(short nbChannels, int upperBound, String savePath) {
        this.nbChannels = nbChannels;
        this.upperBound = upperBound;
        this.maxOuterComparator = ((1 << (nbChannels - 1)) | 1);
        this.maxShifts = nbChannels - 2;
        //this.identityElement = getIdentityElement((byte) nbChannels);
        this.allOnesList = getAllOnesList((byte) nbChannels);
        this.allMinusOneList = getAllMinusOneList((byte) nbChannels);

        this.savePath = savePath;
        this.shouldSave = false;
    }

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than or equal to
     * upperBound.
     *
     * @param nbChannels The amount of channels for the networks.
     * @param upperBound The maximum amount of comparators to use.
     */
    public SingleProcessor(short nbChannels, int upperBound) {
        this(nbChannels, upperBound, "");
    }

    public short[] process(ObjArrayList<short[][]> oldList, int startIndex, ObjArrayList<short[][]> newList, short nbComp) {
        //finish old
        ObjArrayList<short[][]> NList = continueCycle(oldList, startIndex, newList, nbComp);
        newList = null;
        oldList = null;

        do {
            long prev = System.currentTimeMillis();
            NList = performCycle(NList, nbComp);
            long took = System.currentTimeMillis() - prev;
            nbComp++;
            System.out.println("Performed cycle w " + nbComp + " comps and size " + NList.size() + " took " + took + " ms");

            //System.out.println("permCount: " + permCount);
            //permCount = 0;
            //this.printInputs(N.get(0)[0]);
            //System.out.println(N.size64());
        } while (NList.size() > 1 && nbComp < upperBound);

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
        //TODO: Replace firstTimeGenerate & Prune with just the network (1 2) ??
        ObjArrayList<short[][]> NList = firstTimeGenerate(getOriginalInputs(upperBound));
        prune(NList);
        //System.out.println(N.size64());

        short nbComp = 1;
        do {
            long prev = System.currentTimeMillis();
            NList = performCycle(NList, nbComp);
            long took = System.currentTimeMillis() - prev;
            nbComp++;
            System.out.println("Performed cycle w " + nbComp + " comps and size " + NList.size() + " took " + took + " ms");

            //System.out.println("permCount: " + permCount);
            //permCount = 0;
            //this.printInputs(N.get(0)[0]);
            //System.out.println(N.size64());
        } while (NList.size() > 1 && nbComp < upperBound);

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
     * Add all networks consisting of 1 comparator to N.
     */
    private ObjArrayList<short[][]> firstTimeGenerate(short[][] inputs) {
        ObjArrayList<short[][]> list = new ObjArrayList();
        int cMaxShifts = maxShifts;
        short comp;
        int number;
        int outerShift;

        /* For all comparators */
        for (number = 3; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                //new Network (via clone)
                short[][] data = inputs.clone();
                //Fill
                data[0] = new short[2];
                data[0][0] = comp;
                processData(data, comp, 1);
                processW(data, comp, 1);

                list.add(data);
            }
        }

        return list;
    }

    /**
     * Processes the data for the new comparator. Adding the comparator to the
     * data[0] is assumed to be done already.
     *
     * @param data The network
     * @param newComp The comparator to process the data on.
     * @param startIndex The index of where to start. (= 1 will cover
     * everything.)
     */
    public void processData(short[][] data, short newComp, int startIndex) {
        //1 - HashSet
//        ShortOpenHashSet set = new ShortOpenHashSet();
        //2 - ArrayList
//        ShortArrayList arr;
        //3 - Array
        short[] processed;
        boolean found;

        for (int nbOnes = startIndex; nbOnes < nbChannels; nbOnes++) {
            //1 - HashSet
//            set.clear();

            //2 - ArrayList
//            arr = new ShortArrayList();
            //3 - Array
            processed = new short[data[nbOnes].length];
            int counter = 0;
            boolean foundNew = false;

            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                short oldValue = data[nbOnes][innerIndex];
                short value = swapCompare(oldValue, newComp);
                if (value != oldValue) {
                    foundNew = true;
                }
                //1 - HashSet
//                set.add(value);

                //2 - ArrayList
//                if (!arr.contains(value)) {
//                    arr.add(value);
//                }
                //3 - Array
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

            //1 - HashSet
//            data[nbOnes] = set.toShortArray();
            //2 - ArrayList
//            data[nbOnes] = arr.toShortArray();
            //3 - Array
            if (foundNew) { //CAUTION! Don't do this 'shared array' with writing lists to disk.
                short[] temp = new short[counter];
                System.arraycopy(processed, 0, temp, 0, counter);
                data[nbOnes] = temp;
            }
        }

    }

    private ObjArrayList<short[][]> continueCycle(final ObjArrayList<short[][]> networkList, final int startIndex, ObjArrayList<short[][]> newList, final short nbComps) {
        int n = networkList.size();
        int nb = 250;

        for (int index = startIndex; index < n; index += nb) {
            ObjArrayList<short[][]> prunedList = generate(networkList, index, nb, nbComps);
            prune(prunedList);

            prune(newList, prunedList);
            newList.addList(prunedList);

            if (shouldSave) {
                save(networkList, index + nb, newList, nbComps);
                System.exit(0);
            }
        }

        return newList;
    }

    private ObjArrayList<short[][]> performCycle(ObjArrayList<short[][]> networkList, short nbComps) {
        ObjArrayList<short[][]> newList = new ObjArrayList();
        int n = networkList.size();
        int nb = 250;

        for (int index = 0; index < n; index += nb) {
            ObjArrayList<short[][]> prunedList = generate(networkList, index, nb, nbComps);
            prune(prunedList);

            prune(newList, prunedList);
            newList.addList(prunedList);

            if (shouldSave) {
                save(networkList, index + nb, newList, nbComps);
                System.exit(0);
            }
        }

        return newList;
    }

    /**
     *
     * @param networkList The list to generate
     * @param startIndex The index of where the first network is taken from
     * networkList.
     * @param length The amount of networks to use from networkList.
     * @param nbComp The index of the comparator to add.
     * @return
     */
    private ObjArrayList<short[][]> generate(ObjArrayList<short[][]> networkList, int startIndex, int length, short nbComp) {
        int n = Math.min(networkList.size(), startIndex + length);
        ObjArrayList<short[][]> list = new ObjArrayList();

        for (int i = startIndex; i < n; i++) {
            short[][] network = networkList.get(i);
            /* Setup environment */
            int cMaxShifts;
            short comp;
            int number;
            int outerShift;

            /* Start Generate work */
            int prevComp = network[0][nbComp - 1];
            int prevCompMZ = prevComp >> Integer.numberOfTrailingZeros(prevComp);

            for (number = 3, cMaxShifts = maxShifts; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
                comp = (short) number;
                int compMZ = comp >> Integer.numberOfTrailingZeros(comp);

                for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer

                    //if(prevComp != comp && (shared || prevComp < comp)) {
                    if (((prevComp & comp) != 0 && prevComp != comp) || (prevCompMZ < compMZ || (compMZ == prevCompMZ && prevComp < comp))) {
                        //new Network (via clone)
                        //if (!isRedundantComp(network, comp)) {
                        int index = getChangeIndex(network, comp);
                        if (index != -1) { //!= redundant
                            short[][] data = network.clone();
                            //Fill comp
                            data[0] = new short[nbComp + 1];
                            System.arraycopy(network[0], 0, data[0], 0, nbComp);
                            data[0][nbComp] = comp;

                            //processData(data, comp);
                            //processW(data, comp);
                            processData(data, comp, index);
                            processW(data, comp, index);

                            list.add(data);
                        }/* else {
                         redundantCounter++;
                         }*/

                    }/* else {
                     uniqueCounter++;
                     }*/

                }
            }
        }

        return list;
    }

    private ObjArrayList<short[][]> generate(short[][] network, short nbComp) {
        /* Setup environment */
        ObjArrayList<short[][]> list = new ObjArrayList();
        int cMaxShifts;
        short comp;
        int number;
        int outerShift;

        /* Start Generate work */
 /* For all comparators */
        int prevComp = network[0][nbComp - 1];
        int prevCompMZ = prevComp >> Integer.numberOfTrailingZeros(prevComp);

        for (number = 3, cMaxShifts = maxShifts; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            int compMZ = comp >> Integer.numberOfTrailingZeros(comp);

            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer

                //if(prevComp != comp && (shared || prevComp < comp)) {
                if (((prevComp & comp) != 0 && prevComp != comp) || (prevCompMZ < compMZ || (compMZ == prevCompMZ && prevComp < comp))) {
                    //new Network (via clone)
                    //if (!isRedundantComp(network, comp)) {
                    int index = getChangeIndex(network, comp);
                    if (index != -1) { //!= redundant
                        short[][] data = network.clone();
                        //Fill comp
                        data[0] = new short[nbComp + 1];
                        System.arraycopy(network[0], 0, data[0], 0, nbComp);
                        data[0][nbComp] = comp;

                        //processData(data, comp);
                        //processW(data, comp);
                        processData(data, comp, index);
                        processW(data, comp, index);

                        list.add(data);
                    }/* else {
                     redundantCounter++;
                     }*/

                }/* else {
                 uniqueCounter++;
                 }*/

            }
        }
        return list;
    }

    /**
     * Generate networks. Adds all possible comparators to every network in N
     * and replaces N with the result. (Redundant comparators are neglected,
     * isRedundantComp)
     *
     * @param nbComp The index of the comparator (data[0][nbComp]) to start
     * working on.
     * @see #isRedundantComp(short[][], short)
     */
//    private void generate(short nbComp) {
//        /* Setup environment */
//        newN = new ObjArrayList();
//        int cMaxShifts;
//        short comp;
//        int number;
//        int outerShift;
//        ObjectListIterator<short[][]> iter = N.iterator();
//
//        /* Start Generate work */
// /* For all comparators */
//        while (iter.hasNext()) {
//            short[][] network = iter.next();
//            int prevComp = network[0][nbComp - 1];
//            int prevCompMZ = prevComp >> Integer.numberOfTrailingZeros(prevComp);
//
//            for (number = 3, cMaxShifts = maxShifts; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
//                comp = (short) number;
//                int compMZ = comp >> Integer.numberOfTrailingZeros(comp);
//
//                for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
//
//                    //if(prevComp != comp && (shared || prevComp < comp)) {
//                    if (((prevComp & comp) != 0 && prevComp != comp) || (prevCompMZ < compMZ || (compMZ == prevCompMZ && prevComp < comp))) {
//                        //new Network (via clone)
//                        //if (!isRedundantComp(network, comp)) {
//                        int index = getChangeIndex(network, comp);
//                        if (index != -1) { //!= redundant
//                            short[][] data = network.clone();
//                            //Fill comp
//                            data[0] = new short[nbComp + 1];
//                            System.arraycopy(network[0], 0, data[0], 0, nbComp);
//                            data[0][nbComp] = comp;
//
//                            //processData(data, comp);
//                            //processW(data, comp);
//                            processData(data, comp, index);
//                            processW(data, comp, index);
//
//                            newN.add(data);
//                        }/* else {
//                         redundantCounter++;
//                         }*/
//
//                    }/* else {
//                     uniqueCounter++;
//                     }*/
//
//                }
//            }
//        }
//
//        /* Point to new reference */
//        N = newN;
//    }
    private void prune(ObjArrayList<short[][]> networkList, ObjArrayList<short[][]> subList) {
        ObjectListIterator<short[][]> iter = networkList.iterator();

        while (iter.hasNext()) {
            short[][] network2 = iter.next();
            ObjectListIterator<short[][]> innerIter = subList.iterator();

            while (innerIter.hasNext()) {
                short[][] network = innerIter.next();

                if (subsumes(network, network2)) {
                    iter.remove();
                    break;
                } else if (subsumes(network2, network)) {
                    innerIter.remove();
                }
            }
        }
    }

    /**
     * Prune the current N by removing what does not prevent us from getting 1
     * minimal sorting network for the given amount of channels.
     */
    private void prune(ObjArrayList<short[][]> list) {
        ObjectListIterator<short[][]> iter;
        for (int index = 0; index < list.size() - 1; index++) { //TODO: klopt list.size() -1 wel?

            iter = list.listIterator(index + 1);

            short[][] network1 = list.get(index);

            while (iter.hasNext()) {
                short[][] network2 = iter.next();

                if (subsumes(network1, network2)) {
                    iter.remove();
                } else if (subsumes(network2, network1)) {
                    list.remove(index);
                    index--;
                    break;
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
 /* Permute & Check W */
        //TODO: Check if that is true (line below)!
        //Only checking the permutation who are valid for lemma 6.

//        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
//            /* Permute W */
//            int P1 = 0;
//            int L1 = 0;
//
//            for (int pIndex = permutor.length-1; pIndex >= 0; pIndex--) {
//                P1 <<= 1;
//                L1 <<= 1;
//                P1 |= ((network1[nbChannels][(nbOnes - 1) << 2] >> permutor[pIndex]) & 1);
//                L1 |= ((network1[nbChannels][(nbOnes << 2) - 2] >> permutor[pIndex]) & 1);
//            }
//
//            //Test      
//            if (((network2[nbChannels][(nbOnes - 1) << 2] ^ ((1 << nbChannels) - 1)) & P1) != 0
//                    || ((network2[nbChannels][(nbOnes << 2) - 2] ^ ((1 << nbChannels) - 1)) & L1) != 0) {
//                return false;
//            }
//        }

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
        return checkAllRelevantPermutations(network1, network2, Ps,
                0, new byte[nbChannels], 0);

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
            //currOuterIndex = Which outer index we're working with.
            //takenNumbers = bv index 0 een 1 = getal 1 is al genomen.

            while (currOuterIndex >= 0 && currOuterIndex <= lastOuterIndex) {
                if (indices[currOuterIndex] + 1 >= Ps[currOuterIndex].length) { // last Inner Index reached.
                    //reset inner
                    //takenNumbers -= (1 << currPerm[currOuterIndex]); //'untake' the number.
                    takenNumbers &= (allOnes - (1 << currPerm[currOuterIndex])); //TODO: should be the same as above.
                    currPerm[currOuterIndex] = -1;
                    indices[currOuterIndex] = -1;

                    //work on previous index.
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

                        //work on previous index.
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
     * TODO: Possible to optimize
     *
     * @param n
     * @return
     */
    public static long factorial(int n) {
        long result = 1;

        for (int i = n; n > 1; n--) {
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
    private short[] getAllComps() {
        short[] result = new short[nbChannels * (nbChannels - 1) / 2];
        int cMaxShifts;
        short comp;
        int number;
        int outerShift;
        int index = 0;

        /* For all comparators */
        for (number = 3, cMaxShifts = maxShifts; number <= maxOuterComparator; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                result[index] = comp;
                index++;
            }
        }
        return result;
    }

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
     * not, the first index of changing outputArr will be returned.
     *
     * @param data The date before the comp would be added.
     * @param comp The comp that would be added.
     * @return -1 If the comparator is redundant else a number 0 &lt= x &lt
     * nbChannels
     */
    private int getChangeIndex(short[][] data, short comp) {
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                short output = data[nbOnes][innerIndex];
                if (output != swapCompare(output, comp)) {
                    return nbOnes;
                }
            }
        }
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
    }

    private void save(ObjArrayList<short[][]> oldList, int nextStartIndex, ObjArrayList<short[][]> newList, short nbComps) {
        if (savePath != null && !savePath.equals("")) {
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(new FileOutputStream(this.savePath));
                oos.writeObject(oldList);
                oos.writeInt(nextStartIndex);
                oos.writeObject(newList);
                oos.writeShort(nbComps);

            } catch (IOException ex) {
                Logger.getLogger(SingleProcessor.class
                        .getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (oos != null) {
                    try {
                        oos.close();

                    } catch (IOException ex) {
                        Logger.getLogger(SingleProcessor.class
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
