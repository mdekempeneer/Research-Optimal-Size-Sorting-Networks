package sortingnetworkspaper;

import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;

/**
 *
 * For information on permutations: http://alexbowe.com/popcount-permutations/
 * http://graphics.stanford.edu/~seander/bithacks.html#NextBitPermutation
 *
 * @author Admin
 */
public class Processor {

    private final short nbChannels;
    private final int upperBound;
    private ObjectBigArrayBigList<short[][]> N;

    
    /**
     * 
     * @param nbChannels
     * @param upperBound 
     */
    public Processor(short nbChannels, int upperBound) {
        this.nbChannels = nbChannels;
        this.upperBound = upperBound;
        this.N = new ObjectBigArrayBigList();
    }

    /**
     * 
     * @return 
     */
    public short[] process() {
        /* Initialize inputs */
        short[][] inputs = getOriginalInputs(upperBound);
        firstTimeGenerate(inputs);
        
        //While (upperBound niet bereikt en Size(N) != 1: GENERATE & PRUNE

        return null;
    }

    /**
     * Add all networks consisting of 1 comparator to N.
     */
    private void firstTimeGenerate(short[][] inputs) {
        int maxX = ((1 << (nbChannels - 1)) | 1);
        int maxShifts = nbChannels - 2; //# opschuiven
        short comp;
        int number;
        int outerShift;

        /* For all comparators */
        for (number = 3; number <= maxX; number = (number << 1) - 1, maxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= maxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
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
     */
    private void processData(short[][] data, short newComp) {
        ShortOpenHashSet set = new ShortOpenHashSet();
        
        for (int i = 1; i < data.length; i++) { //For all #1'en
            set.clear();
            for(int j = 0; j < data[i].length; j++) {
                set.add(swapCompare(data[i][j], newComp)); //Add comp(output)
            }
            data[i] = set.toShortArray();
        }
    }

    private void generate() {

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
        int beginNbOnes = Integer.bitCount(start);
        float temp = factorial(maxBits) / factorial(beginNbOnes); //TODO: Could get more efficient
        temp /= factorial(maxBits - beginNbOnes);

        int length = (int) Math.ceil(temp);
        short[] result = new short[length];

        int value = start;
        int max = (1 << maxBits) - 1;
        int t;
        int index = 0;
        do {
            result[index] = (short) value;
            System.out.println(Integer.toBinaryString(value));
            t = value | (value - 1);
            value = (t + 1) | (((~t & -~t) - 1) >> (Integer.numberOfTrailingZeros(value) + 1));
            index++;
        } while (value < max);
        return result;
    }

    /**
     * Get a deep clone of data.
     *
     * @param data The data to deep clone.
     * @return A deep clone performed on the given data.
     */
    private short[][] cloneData(short[][] data) {
        short[][] result = new short[data.length][];
        for (int index = 0; index < result.length; index++) {
            result[index] = cloneShortArr(data[index]);
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
        System.arraycopy(array, 0, clone, 0, clone.length);
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
}