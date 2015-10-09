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
        int nbChannels = network.getNbChannels();
        BitSet input = new BitSet(nbChannels);
        for (int i = 0; i < nbChannels; i++) {
            
        }
        
        return false;
        
    }
    
    public static void main(String[] args) {
        Network network = null;
        BitSet input = new BitSet(5);
        input.set(2,true);
        System.out.println(input.get(2));
        
        if (args.length >= 1) {
            if (args[0].equals("-f")) {
                //File
               String line = Misc.readFile(args[1]);
               network = Network.stringToNetwork(line);
            }
            else if (args[0].equals("-n")) {
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
