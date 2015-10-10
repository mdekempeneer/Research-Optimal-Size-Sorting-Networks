package tester;

import java.util.Arrays;
import networklib.*;

/**
 *
 * @author Mathias
 */
public class Main {

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

        /* Loop trough combinations */
        for (int i = -1; i < nbChannels - 1; i++) {
            //Flip the next bit in line.
            if (i != -1) {
                input[i].setValue(1);
            }

            //Perform test.
            if (!Misc.isSorted(network.getOutput(input))) {
                System.out.println("Sort failed for " + Arrays.toString(input));
                return false;
            }
            //Inner loop; performs on inner bits.
            for (int j = i + 1; j < nbChannels; j++) {
                input[j].setValue(1);
                //Perform test.
                if (!Misc.isSorted(network.getOutput(input))) {
                    System.out.println("Sort failed for " + Arrays.toString(input));
                    return false;
                }
                //reset the bit changed.
                input[j].setValue(0);
            }
        }
        return true;
    }

    /**
     * Tests the sorting property of a given {@link Network} .
     * @param args -f filePath if the network is stored in a file. <br>-n n k (a,b)(c,d) for input as arguments.</br>
     */
    public static void main(String[] args) {
        Network network = null;

        /* Retrieve Network */
        if (args.length >= 1) {
            if (args[0].equals("-f")) {
                //File
                String line = Misc.readFile(args[1]);
                network = Network.stringToNetwork(line);
            } else if (args[0].equals("-n")) {
                //Args
                int nbChannel = Integer.parseInt(args[1]);
                int nbComp = Integer.parseInt(args[2]);
                network = Network.stringToNetwork(nbChannel, nbComp, args[3]);
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
}
