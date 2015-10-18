package tester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import networklib.*;

/**
 *
 * @author Mathias
 */
public class Tester {

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
            //System.out.println("Testing " + Arrays.toString(input));
            return Misc.isSorted(network.getOutput(input));
        }
        return true;
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
     * Tests the sorting property of a given {@link Network} .
     *
     * @param args -f filePath if the network is stored in a file. <br>-n n k
     * (a,b)(c,d) for input as arguments.</br>
     */
    public static void main(String[] args) {
        Network network = null;

        /* Retrieve Network */
        if (args.length >= 2) {
            if (args[0].equals("-f")) {
                //File
                parseNetworks(args[1]);

            } else if (args[0].equals("-n")) {
                //Args
                int nbChannel = Integer.parseInt(args[1]);
                int nbComp = Integer.parseInt(args[2]);
                network = Network.stringToNetwork(nbChannel, nbComp, args[3]);
            }
        } else {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = jfc.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                /* Parse Networks */
                long begin = System.nanoTime(); //DEBUG - TIMINGS
                parseNetworks(jfc.getSelectedFile().getAbsolutePath());
                long end = System.nanoTime(); //DEBUG - TIMINGS
                System.out.println("Test took " +  (end-begin) + " nanoseconds."); //DEBUG - TIMINGS
            } else {
                System.out.println("Failed chosing a file.");
            }
        }

        /* Test Network */
        if (network != null) {
            if (isSortingNetwork(network)) {
                System.out.println("Sorting network.");
            } else {
                System.out.println("Not a sorting network.");
            }
        }
    }

    /**
     * Parse all networks from a file with the given inputPath. This will load
     * all networks from the file and append a 's' when the network is a sorting
     * network. 'u' otherwise.
     *
     * @param inputPath The path to the file where to load from.
     */
    private static void parseNetworks(String inputPath) {
        String line;
        Network network;
        BufferedWriter bw;
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            File outputFile = new File(inputPath + "2");
            bw = new BufferedWriter(new FileWriter(outputFile));
            while ((line = br.readLine()) != null) {
                network = Network.stringToNetwork(line);
                if (isSortingNetwork(network)) {
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
            if (new File(inputPath).delete()) {
                outputFile.renameTo(new File(inputPath));
            }
        } catch (FileNotFoundException exc) {
            System.err.println("File not found exception " + inputPath);
        } catch (IOException exc) {
            System.err.println("IO Exception" + exc.toString());
        }
    }
}
