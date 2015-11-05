package tester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import networklib.*;

/**
 *
 * @author Mathias
 */
public class Tester1 {

    public static long time; //DEBUG - TIMINGS

    /**
     * Test if a network is sorted when giving a certain input and a certain
     * index to construct all possible permutations from that index.
     *
     * Note: When giving an input consisting of only zero's and index =
     * input.length -1 it will check all permutations possible and return if it
     * is a sorting network or not.
     *
     * @param network The network to check for s
     * @param input The input that is constructed so far to check with.
     * @param index The index for the phase of the construction.
     * @return Whether or not the network sorts all inputs that are constructed
     * provided with input and the index.
     */
    private static boolean isSorted(Network network, Bit[] input, int index) {
        if (index >= 0) {
            //Set 0
            input[index].setValue(0);
            if (!isSorted(network, input, index - 1)) {
                return false;
            }
            //Set 1
            input[index].setValue(1);
            if (!isSorted(network, input, index - 1)) {
                return false;
            }
        } else {
            return Misc.isSorted(network.getOutput(input));
        }
        return true;
    }
    
    /**
     * Check if the {@link Network} provided is a sorting network. This is done
     * by propagating every possible combination trough the channel and
     * comparators and testing for non-sorted outputs.
     *
     * @param nbChannels Ignored, used for polymorphism.
     * @param network The {@link Network} to check.
     * @return Whether the {@link Network} is a sorting network a.k.a gives a
     * sorted output for every input.
     * @see isSortingNetwork(Network)
     */
    public boolean isSortingNetwork(int nbChannels, Network network) {
        return isSortingNetwork(network);
    }

    /**
     * Check if the {@link Network} provided is a sorting network. This is done
     * by propagating every possible combination trough the channel and
     * comparators and testing for non-sorted outputs.
     *
     * @param network The {@link Network} to check.
     * @return Whether the {@link Network} is a sorting network aka gives a
     * sorted output for every input.
     */
    public static boolean isSortingNetwork(Network network) {
        /* Create input */
        int nbChannels = network.getNbChannels();
        Bit[] input = new Bit[nbChannels];
        for (int i = 0; i < nbChannels; i++) {
            input[i] = new Bit(0);
        }

        return isSorted(network, input, input.length - 1);

        /*
         Old code - could be useful later on.
         ArrayList<Bit[]> list = new ArrayList<>();
         Misc.printBin(list, input, nbChannels);
         System.out.println(list);
         for (Bit[] elem : list) {
         System.out.println(Arrays.toString(elem));
         if (!Misc.isSorted(network.getOutput(elem))) {
         return false;
         }
         }
         return true;*/
    }
    
    /**
     * Parse all networks from a file with the given inputPath. This will load
     * all networks from the file and append a 's' when the network is a sorting
     * network. 'u' otherwise.
     * @param inputPath 
     */
    public void processFile(String inputPath) {
        processFile(inputPath, null);
    }

    /**
     * Parse all networks from a file with the given inputPath. This will load
     * all networks from the file and append a 's' when the network is a sorting
     * network. 'u' otherwise.
     *
     * @param inputPath The path to the file where to load from.
     * @param outputPath The path where to save the output to.
     */
    public void processFile(String inputPath, String outputPath) {
        String line;
        Network network;
        BufferedWriter bw;
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) { 
            File outputFile = (outputPath == null) ? new File(inputPath + "2") : new File(outputPath);
            bw = new BufferedWriter(new FileWriter(outputFile));
            while ((line = br.readLine()) != null) {
                network = Network.stringToNetwork(line);
                
                long begin = System.nanoTime(); //DEBUG - TIMINGS
                boolean isSortNetwork = isSortingNetwork(network);
                time += (System.nanoTime() - begin); //DEBUG - TIMINGS
                
                if (isSortNetwork) {
                    line = line + " s";
                    bw.write(line);
                    bw.newLine();
                } else {
                    line = line + " u";
                    bw.write(line);
                    bw.newLine();
                }
            }
            bw.close();
            br.close();
            if (outputPath == null && new File(inputPath).delete()) {
                outputFile.renameTo(new File(inputPath));
            }
        } catch (FileNotFoundException exc) {
            System.err.println("File not found exception: " + exc.toString());
        } catch (IOException exc) {
            System.err.println("IO Exception" + exc.toString());
        }
    }
    
    /**
     * Get the Network from a String.
     * @param sNetwork The network in string format.
     * @return The Network found.
     * @see Network#stringToNetwork(java.lang.String)
     */
    public Network parseNetwork(String sNetwork) {
        return Network.stringToNetwork(sNetwork);
    }
    
}
