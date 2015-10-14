package tester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JFileChooser;
import networklib.*;

/**
 *
 * @author Mathias
 */
public class Tester {

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

            //Inner loop; performs on inner bits.
            for (int j = i + 1; j < nbChannels; j++) {
                input[j].setValue(1);
                System.out.println(Arrays.toString(input));
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
                parseNetworks(jfc.getSelectedFile().getAbsolutePath());
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
