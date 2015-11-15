package sortingnetworkspaper;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.combination.simple.SimpleCombinationGenerator;

/**
 *
 * For information on permutations: http://alexbowe.com/popcount-permutations/
 * http://graphics.stanford.edu/~seander/bithacks.html#NextBitPermutation
 *
 * @author Admin
 */
public class Processor {

    short nbChannels;

    public Processor(short nbChannels) {
        this.nbChannels = nbChannels;
    }

    public short[] process(int upperBound) {
        short[][] inputs = getOriginalInputs(upperBound);

        return null;
    }
    
    public void generate() {
        
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
        //TODO: data[0] should probably hold comparators so far.
        /* 
         data[0] holds the lengths of the other shorts.
         data[1] holds outputs with 1 '1's.
         data[2] holds outputs with 2 '1's.
         ...
         */
        short[][] data = new short[nbChannels][]; //TODO: nbChannels-1; 1111 niet nuttig
        data[0] = new short[upperBound];
        
        for (int numberOfOnes = 1; numberOfOnes < nbChannels; numberOfOnes++) {
            data[numberOfOnes] = getPermutations((short) ((1 << numberOfOnes) - 1), nbChannels);
        }

        return data;
    }

    /**
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
