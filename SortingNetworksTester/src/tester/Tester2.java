package tester;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class Tester2 {
    
    public static long time;

    /**
     * Process the networks in the inputPath. Prints when one sorts.
     *
     * @param inputPath The file to check.
     */
    public void processFile(String inputPath) {
        processFile(inputPath, null);
    }

    /**
     * Processes the networks in the inputPath. Saves sorting networks into
     * outputPath. Prints sorting networks when outputPath is null.
     *
     * @param inputPath The path of the file to process.
     * @param outputPath The path of the file to save into.
     */
    public void processFile(String inputPath, String outputPath) {
        short[] network;
        boolean sorts;
        int nbChannels;
        int nbComps;
        DataInputStream dis;
        DataOutputStream dos;

        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(inputPath))));
            dos = (outputPath == null) ? null : new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(outputPath))));

            while (dis.available() > 0) {
                /* Grab Network */
                nbChannels = dis.readInt();
                nbComps = dis.readInt();
                network = new short[nbComps];

                for (int i = 0; i < nbComps; i++) {
                    network[i] = dis.readShort();
                }

                /* Test Network */
                long begin = System.nanoTime(); //DEBUG - TIMING
                sorts = isSortingNetwork(nbChannels, network);
                time += (System.nanoTime() - begin); //DEBUG - TIMING

                /* Save result */
                if (sorts) {
                    if (dos != null) {
                        writeNetwork(dos, nbChannels, network);
                    } else {
                        printShortNetwork(nbChannels, network);
                    }
                }
            }
            dis.close();
            if(dos != null) {
                dos.flush();
                dos.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Tester2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test if the given network is a sorting network.
     *
     * @param nbChannels The amount of channels.
     * @param network The comparators of the network.
     * @return True if network is a sorting network, false otherwise.
     */
    public boolean isSortingNetwork(int nbChannels, short[] network) {
        int finalInput = (1 << nbChannels) - 1;
        short currInput;
        int result;
        int nextSortedInput = 3;

        //test inputs 2..1111110
        for (short input = 2; input < finalInput; input++) {
            if(input == nextSortedInput) { //Already sorted | TODO: Weggegooid door TODO check here for sorted? (binnen for)
                nextSortedInput = (nextSortedInput << 1 ) | 1;
                continue;
            }
            
            currInput = input;
            result = (1 << Integer.bitCount(currInput)) - 1; //Kunnen ook look-up tabel bijhouden.
            
            for (short comp : network) {
                currInput = swapOptCompare(currInput, comp); //TODO: Test swapCompare vs Opt
                //TODO: Check here for sorted?
            }

            if (currInput != result) {
                //System.out.println("failed for " + input + " to " + currInput);
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param input
     * @param comp
     * @return
     */
    private static short swapCompare(short input, short comp) {
        int pos1 = 31 - Integer.numberOfLeadingZeros(comp & 0xFFFF);
        int pos2 = Integer.numberOfTrailingZeros(comp & 0xFFFF);

        int frontBit = (input >> pos1) & 1;// bit at pos1
        int backBit = (input >> pos2) & 1;// bit at pos2

        return (frontBit <= backBit) ? input : (short) (input ^ comp);
    }

    /**
     *
     * @param input
     * @param comp
     * @return
     */
    private static short swapOptCompare(short input, short comp) {
        int pos1 = 31 - Integer.numberOfLeadingZeros(comp & 0xFFFF);
        //TODO: Check & 0xFFFF in aparte int = 1x uitvoeren?
        int pos2 = Integer.numberOfTrailingZeros(comp & 0xFFFF);

        return (((input >> pos1) & 1) <= ((input >> pos2) & 1)) ? input : (short) (input ^ comp);// TADAM!!!
    }

    /**
     * Write the given network to the outputPath. format: Int(nbChannels),
     * Int(network.length), Short(network[0]), Short(network[1]),...
     *
     * @param dos The DataOutputStream used to write to.
     * @param nbChannels The amount of channels of the network.
     * @param network The network.
     */
    public void writeNetwork(DataOutputStream dos, int nbChannels, short[] network) {
        try {
            dos.writeInt(nbChannels);
            dos.writeInt(network.length); //#Comperators
            for (short comp : network) {
                dos.writeShort(comp);
            }
        } catch (IOException ex) {
            Logger.getLogger(Tester2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Print the given network.
     *
     * @param nbChannels
     * @param network
     */
    public static void printShortNetwork(int nbChannels, short[] network) {
        StringBuilder sb = new StringBuilder();
        sb.append(nbChannels);
        for (short comp : network) {
            sb.append(" ");
            sb.append(comp);
        }
        System.out.println(sb.toString());
    }

    /**
     * Get the network in short, from the string form.
     *
     * @param sNetwork The network in String form (comp1,comp2,comp3,...)
     * @return The comparators of the network in short form.
     */
    public short[] parseNetwork(String sNetwork) {
        String[] sComparators = sNetwork.split(",");
        short[] network = new short[sComparators.length];

        for (int i = 0; i < network.length; i++) {
            network[i] = Short.parseShort(sComparators[i]);
        }

        return network;
    }

}
