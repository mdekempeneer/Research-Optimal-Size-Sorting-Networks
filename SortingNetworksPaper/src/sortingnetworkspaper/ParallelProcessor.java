package sortingnetworkspaper;

import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * For information on permutations: http://alexbowe.com/popcount-permutations/
 * http://graphics.stanford.edu/~seander/bithacks.html#NextBitPermutation
 *
 * @author Admin
 */
public class ParallelProcessor implements Processor {

    private final short nbChannels;
    private final int upperBound;
    private ObjectBigArrayBigList<short[][]> N;
    private ObjectBigArrayBigList<short[][]> newN;
    private final ThreadPoolExecutor executors;
    private final GenerateThread[] genWorkers;
    private final int nbThreads;
    private final short[] allPosComp;

    /**
     * Create a Processor which can, for a given nbChannels and upperBound find
     * a minimal sorting network if there is one with less than upperBound.
     *
     * @param nbChannels
     * @param upperBound
     */
    public ParallelProcessor(short nbChannels, int upperBound) {
        this.nbChannels = nbChannels;
        this.upperBound = upperBound;
        this.N = new ObjectBigArrayBigList();
        this.allPosComp = getAllPosComps();
        //nbThreads = Runtime.getRuntime().availableProcessors();
        nbThreads = 1;
        executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads);
        genWorkers = new GenerateThread[nbThreads];
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
        short[][] inputs = getOriginalInputs(upperBound);
        short nbComp = 1;
        initiateGenWorkers();
        firstTimeGenerate(inputs);

        while (N.size64() > 1 && nbComp < upperBound) {
            //    long begin = System.nanoTime();
            generate(nbComp);
            //    System.out.println("Tussentijd: " + (System.nanoTime() - begin));
            prune();
            nbComp++;
        }
        executors.shutdown();
        return null;
    }

    /**
     * Initialize the GenerateWorkers ({@link GenerateThread}). This uses 'this'
     * so shouldn't be used in the constructor.
     */
    private void initiateGenWorkers() {
        for (int i = 0; i < nbThreads; i++) {
            genWorkers[i] = new GenerateThread(this, allPosComp);
        }
    }

    /**
     * Add all networks consisting of 1 comparator to N.
     */
    private void firstTimeGenerate(short[][] inputs) {
        int maxX = ((1 << (nbChannels - 1)) | 1);
        int maxShifts = nbChannels - 2; //# opschuiven
        int number;
        int outerShift;

        /* For all comparators */
        for (short comp : allPosComp) {
            //new Network (via clone)
            short[][] data = inputs.clone();
            //Fill
            data[0] = new short[upperBound];
            data[0][0] = comp;
            processData(data, comp);

            N.add(data);
        }
//        for (number = 3; number <= maxX; number = (number << 1) - 1, maxShifts--) { //x*2 - 1
//            comp = (short) number;
//            for (outerShift = 0; outerShift <= maxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
//                //new Network (via clone)
//                short[][] data = inputs.clone();
//                //Fill
//                data[0] = new short[upperBound];
//                data[0][0] = comp;
//                processData(data, comp);
//
//                N.add(data);
//            }
//        }
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
        ShortOpenHashSet set = new ShortOpenHashSet();

        for (int i = 1; i < data.length; i++) { //For all #1'en
            set.clear();
            for (int j = 0; j < data[i].length; j++) {
                set.add(swapCompare(data[i][j], newComp)); //Add comp(output)
            }
            data[i] = set.toShortArray();
        }
    }

    /**
     *
     * @param nbComp The index of the comparator (data[0]) to start working on.
     */
    private void generate(short nbComp) {
        /* Setup environment */
        newN = new ObjectBigArrayBigList(); //TODO: Clear instead of new?
        updateIndicesAndCompCount(nbComp);

        /* Start Generate work */
        for (GenerateThread gen : genWorkers) {
            executors.execute(gen);
        }

        /* Wait for all to be finished */
        synchronized (this) { //TODO: Test.
            while (executors.getTaskCount() != executors.getCompletedTaskCount()) { //TODO: Use getActiveCount()
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
            }
 //           System.out.print(executors.getTaskCount() + " ");
            //           System.out.print(executors.getCompletedTaskCount() + " ");
            //           System.out.println(N.size64());
        }

        /* Point to new reference */
        N = newN;
    }

    private void prune() {

    }

    /**
     * Add the networks from partN to the newN list in a synchronized way.
     * Notifies this.
     *
     * @param partN The list of networks from where to add to.
     */
    @Override
    public synchronized void addToNewN(ObjectBigArrayBigList<short[][]> partN) {
        newN.addAll(partN); //TODO: System.arraycopy ??
        this.notify();
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
     * Update the indices for all GenerateThreads (genWorker) and sets the
     * nbComp to compCount. Assures all genWorkers are working in an almost
     * equal sized set and work on the compCount's comperator.
     */
    private void updateIndicesAndCompCount(short compCount) {
        long setLength = (long) Math.ceil(N.size64() / nbThreads);
        long prevEnd = 0;
        long start;
        long end;

        for (GenerateThread genWorker : genWorkers) {
            start = prevEnd;
            end = Math.min(start + setLength, N.size64());
            prevEnd = end;

            genWorker.setIndex(start, end);
            genWorker.setNbComp(compCount);
        }
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
            //System.out.println(Integer.toBinaryString(value)); -- DEBUG!
            t = value | (value - 1);
            value = (t + 1) | (((~t & -~t) - 1) >> (Integer.numberOfTrailingZeros(value) + 1));
            index++;
        } while (value < max);

        //TODO: Test if array is full (length correctly) if(index != result.length) problem.
        return result;
    }

    private short[] getAllPosComps() {
        short[] result = new short[nbChannels * (nbChannels - 1) / 2];
        int maxX = ((1 << (nbChannels - 1)) | 1);
        int maxShifts = nbChannels - 2; //# opschuiven
        short comp;
        int number;
        int outerShift;
        int index = 0;

        /* For all comparators */
        for (number = 3; number <= maxX; number = (number << 1) - 1, maxShifts--) { //x*2 - 1
            comp = (short) number;
            for (outerShift = 0; outerShift <= maxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                result[index] = comp;
                index++;
            }
        }
        return result;
    }

    /**
     * Get a deep clone of data.
     *
     * @param data The data to deep clone.
     * @return A deep clone performed on the given data.
     */
    /*private short[][] cloneData(short[][] data) {
     short[][] result = new short[data.length][];
     for (int index = 0; index < result.length; index++) {
     result[index] = cloneShortArr(data[index]);
     }
     return result;
     }*/
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
}
