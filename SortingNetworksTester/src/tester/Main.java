/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tester;

import java.util.Arrays;
import java.util.BitSet;
import networklib.Misc;
import networklib.Network;

/**
 *
 * @author Mathias
 */
public class Main {

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

    public static void main(String[] args) {
        Network network = null;

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

        if (network != null) {

            if (isSortingNetwork(network)) {
                System.out.println("Sorting network.");
            } else {
                System.out.println("Not a sorting network.");
            }

        }
    }
}
