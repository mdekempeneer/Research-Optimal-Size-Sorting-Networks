/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tester;

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

        for (int i = 0; i < nbChannels; i++) {
            input[i].setValue(1); //TODO 000 moeten we checken

            //test j = i+1
            
            for (int j = i + 2; j < nbChannels; j++) {
                input[j - 1].setValue(0);
                input[j].setValue(1);

                System.out.println("");
                for (Bit b : input) {
                    System.out.print(b.getValue());
                }

                Bit[] output = network.getOutput(input);
                if (Misc.isSorted(output) == false) {
                    return false;
                }

            }
        }

        return true;

    }

    public static void main(String[] args) {
        Network network = null;
        BitSet input = new BitSet(5);
        input.set(2, true);
        System.out.println(input.get(2));

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

        }
    }
}
