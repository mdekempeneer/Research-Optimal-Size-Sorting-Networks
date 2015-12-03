/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sortingnetworkspaper;

import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class SingleProcessor implements Processor {

    private final short nbChannels;
    private final int upperBound;

    private ObjectBigArrayBigList<short[][]> N;
    private ObjectBigArrayBigList<short[][]> newN;

    private final int maxX;
    private final int maxShifts;
    private final byte[] identityElement;

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than or equal to
     * upperBound.
     *
     * @param nbChannels The amount of channels for the networks.
     * @param upperBound The maximum amount of comparators to use.
     */
    public SingleProcessor(short nbChannels, int upperBound) {
        this.nbChannels = nbChannels;
        this.upperBound = upperBound;
        this.N = new ObjectBigArrayBigList();
        this.newN = new ObjectBigArrayBigList();
        this.maxX = ((1 << (nbChannels - 1)) | 1);
        this.maxShifts = nbChannels - 2;
        this.identityElement = getIdentityElement((byte) nbChannels);
    }

    /**
     * Get the amount of channels these networks have.
     *
     * @return The amount of channels these networks have.
     */
    @Override
    public int getNbChannels() {
        return nbChannels;
    }

    /**
     * Get the current 'main' N list.
     *
     * @return The list of networks before or right after the generate step.
     */
    @Override
    public ObjectBigArrayBigList<short[][]> getN() {
        return this.N;
    }

    /**
     *
     * @return
     */
    @Override
    public short[] process() {
        /* Initialize inputs */
        //TODO: Replace firstTimeGenerate & Prune with just the network (1 2) ??
        firstTimeGenerate(getOriginalInputs(upperBound));
        short nbComp = 1; //TODO: Move to global variables
        prune();
        //System.out.println(N.size64());

        do {
            generate(nbComp);
            prune();
            nbComp++;
            //System.out.println(N.size64());
        } while (N.size64() > 1 && nbComp < upperBound);

        /* Return result */
        if (N.size64() >= 1) {
            return N.get(0)[0];
        } else {
            return null;
        }
    }

    /**
     * Add all networks consisting of 1 comparator to N.
     */
    private void firstTimeGenerate(short[][] inputs) {
        int cMaxShifts = maxShifts;
        short comp;
        int number;
        int outerShift;

        /* For all comparators */
        for (number = 3; number <= maxX; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                //new Network (via clone)
                short[][] data = inputs.clone();
                //Fill
                data[0] = new short[upperBound];
                data[0][0] = comp;
                processData(data, comp);

                N.add(data);
            }
        }
    }

    /**
     * Processes the data for the new comparator. Adding the comparator to the
     * data[0] is assumed to be done already.
     *
     * @param data The network
     * @param newComp The comparator to process the data on.
     */
    @Override
    public void processData(short[][] data, short newComp) {
        //TODO: Test timings for ShortOpenHashSet, Arrays, ShortArrayList

        //1 - HashSet
//        ShortOpenHashSet set = new ShortOpenHashSet(); //TODO: Don't use HashSet. Time!
        //2 - ArrayList
//        ShortArrayList arr;
        //3 - Array
        short[] processed;
        int counter;
        boolean found;

        for (int nbOnes = 1; nbOnes < data.length; nbOnes++) {
            //1 - HashSet
//            set.clear();

            //2 - ArrayList
//            arr = new ShortArrayList();
            //3 - Array
            processed = new short[data[nbOnes].length];
            counter = 0;

            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                short value = swapCompare(data[nbOnes][innerIndex], newComp);
                //1 - HashSet
//                set.add(swapCompare(data[nbOnes][innerIndex], newComp));

                //2 - ArrayList
//                if (!arr.contains(value)) {
//                    arr.add(value);
//                }
                //3 - Array
                processed[innerIndex] = swapCompare(data[nbOnes][innerIndex], newComp);
                found = false;
                for (int i = counter - 1; i >= 0; i--) {
                    if (processed[i] == value) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    processed[counter] = value;
                    counter++;
                }
            }

            //1 & 2 - HashSet & ArrayList
//            data[nbOnes] = set.toShortArray();
            //3 - Array
            short[] temp = new short[counter];
            System.arraycopy(processed, 0, temp, 0, counter);
            data[nbOnes] = temp;
        }

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
    private void generate(short nbComp) {
        /* Setup environment */
        newN = new ObjectBigArrayBigList();
        int cMaxShifts;
        short comp;
        int number;
        int outerShift;
        ObjectBigListIterator<short[][]> iter = N.iterator();

        /* Start Generate work */
        /* For all comparators */
        while (iter.hasNext()) {
            short[][] network = iter.next();

            for (number = 3, cMaxShifts = maxShifts; number <= maxX; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
                comp = (short) number;
                for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                    //new Network (via clone)
                    //if ((nbComp == 0 || network[0][nbComp - 1] != comp) && !isRedundantComp(network, comp)) {
                    if (!isRedundantComp(network, comp)) {
                        short[][] data = network.clone();
                        //Fill
                        data[0] = data[0].clone();
                        data[0][nbComp] = comp;
                        processData(data, comp);

                        newN.add(data);
                    }
                }
            }
        }

        /* Point to new reference */
        N = newN;
    }

    /**
     * Prune the current N by removing what does not prevent us from getting 1
     * minimal sorting network for the given amount of channels.
     */
    private void prune() {
        ObjectBigListIterator<short[][]> iter;

        //System.out.println("Prunestap begin: " + N.size64());
        for (int index = 0; index < N.size64() - 1; index++) {
            iter = N.listIterator(index + 1);
            while (iter.hasNext()) {
                short[][] network2 = iter.next();

                if (subsumes(N.get(index), network2)) {
                    iter.remove();
                } else {
                    if (subsumes(network2, N.get(index))) {
                        N.remove(index);
                        index--;
                        break;
                    }
                }
            }
        }
        //System.out.println("Prunestap eind: " + N.size64());
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
         C1 subsumes C2 => P(w(C1, x, k)) C= w(C2, x, k)
         */

        for (int nbOnes = 1; nbOnes < network1.length; nbOnes++) {
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
        //TODO Check if performing isValidPermutation within subsumes (inline) is more efficient.
        /* First check: Lemma 4: 
         If E(k) such that the data1[k].length > data2[k].length => data1 NOT subesumes data2 
         */

        for (int nbOnes = 1; nbOnes < network1.length; nbOnes++) {
            if (network1[nbOnes].length > network2[nbOnes].length) {
                return false;
            }
        }

        /* Second check: Lemma 5:
         If for x = {0,1} and 0 < k <= n |w(C1, x, k)| > |w(C2, x, k)| => C1 NOT subesume C2
         */
        if (isValidPermutation(network1, network2)) {
            //TODO: Test if this is ever true; if not delete.
            //System.err.println("It was true");
            return true;
        }

        byte[] currPerm = new byte[this.identityElement.length];
        System.arraycopy(this.identityElement, 0, currPerm, 0, nbChannels);

        while ((currPerm = Permute.getNextPermutation(currPerm)) != null) {
            if (isValidPermutation(Permute.getPermutedData(currPerm, network1), network2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add the networks from partN to the newN list in a synchronized way.
     *
     * @param partN The list of networks from where to add to.
     */
    @Override
    public synchronized void addToNewN(ObjectBigArrayBigList<short[][]> partN) {
        newN.addAll(partN); //TODO: System.arraycopy ??
    }

    /**
     *
     * @param outputPath
     */
    public void process(String outputPath) {
        process(outputPath, null, false);
    }

    /**
     *
     * @param outputPath
     * @param logPath
     * @param log
     */
    public void process(String outputPath, String logPath, boolean log) {

    }

    /**
     * Get all original inputs excluding the already sorted ones.
     *
     * @return range from 2 to (2^nbChannels-1) excluding all sorted (binary)
     * ones.
     */
    private short[][] getOriginalInputs(int upperBound) {
        /* 
         data[0] holds the lengths of the other shorts.
         data[1] holds outputs with 1 '1's.
         data[2] holds outputs with 2 '1's.
         ...
         */
        short[][] data = new short[nbChannels][];
        data[0] = new short[upperBound];

        //TODO: Test faster?
//       int start = 0; 
//       for (int numberOfOnes = 1; numberOfOnes < nbChannels; numberOfOnes++) {
//            data[numberOfOnes] = getPermutations((short) (start = (start << 1) | 1), nbChannels);
//       }
        for (int numberOfOnes = 1; numberOfOnes < nbChannels; numberOfOnes++) {
            data[numberOfOnes] = getPermutations((short) ((1 << numberOfOnes) - 1), nbChannels);
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
    private static float factorial(int n) {
        float result = 1;
        while (n > 1) {
            result *= n;
            n--;
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

        //TODO: Test if array is full (length correctly) if(index != result.length) problem.
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
        for (number = 3, cMaxShifts = maxShifts; number <= maxX; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                result[index] = comp;
                index++;
            }
        }
        return result;
    }

    /**
     * Get a deep clone of the array.
     *
     * @param array The array to clone.
     * @return A deep clone of the given array.
     */
    private short[] cloneShortArr(short[] array) {
        short[] clone = new short[array.length];
        System.arraycopy(array, 0, clone, 0, clone.length); //Clone or arraycopy?
        return clone;
    }

    /**
     * Print all the inputs in the given list.
     *
     * @param inputs The inputs to print.
     */
    private void printInputs(short[] inputs) {
        for (short input : inputs) {
            System.out.println(input);
        }
    }

    /**
     * Get the Identity element for the permutation of nbChannels.
     *
     * @param nbChannels The amount of channels of the network.
     * @return The Identity element which for a permutation returns the same as
     * the input.
     */
    private static byte[] getIdentityElement(byte nbChannels) {
        byte[] result = new byte[nbChannels];
        for (byte i = 0; i < nbChannels; i++) {
            result[i] = i;
        }
        return result;
    }

    /**
     * Check if the given comp is redundant given the data present.
     *
     * @param data The date before the comp would be added.
     * @param comp The comp that would be added.
     * @return True if adding the comparator does not change any output. False
     * otherwise.
     */
    private boolean isRedundantComp(short[][] data, short comp) {
        for (int nbOnes = 1; nbOnes < data.length; nbOnes++) {
            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                if (data[nbOnes][innerIndex] != swapCompare(data[nbOnes][innerIndex], comp)) {
                    return false;
                }
            }
        }
        return true;
    }

}