package generator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import networklib.Comparator;

/**
 * Makes all possible combinations of all possible comparators (= all possible
 * combinations of channels).
 *
 * The idea: 0 0's between; n-2 shifts 000011 = 3 000110 001100 011000 110000
 *
 * 1 0's between; n-3 shifts 000101 = 5 001010 010100 101000
 *
 * 2 0's between; n-4 shifts 001001 = 9 010010 100100
 *
 * 3 0's between; n-5 shifts 010001 = 17 100010
 *
 * 4 0's between: n-6 shifts 100001 = 33
 *
 * @author Admin
 */
public class Generator3 {

    private final int nbChannels;
    private final int nbComp;
    private final String outputPath;
    private DataOutputStream dos = null;

    /**
     * Create a {@link Generator3} which can write all possible {@link Network}
     * combinations with given amount of channels and comparators.
     *
     * @param nbChannels The amount of channels of the {@link Network}.
     * @param nbComp The amount of {@link Comparator}s of the {@link Network}.
     * @param outputPath The path to the file to write the output to.
     */
    public Generator3(int nbChannels, int nbComp, String outputPath) {
        this.nbChannels = nbChannels;
        this.nbComp = nbComp;
        this.outputPath = outputPath;
    }

    /**
     * Generate all possible networks with the provided amount of channels and
     * comparators and write it to a file.
     *
     */
    public void generate() {
        short[] network = new short[nbComp];
        int index = 0;

        long begin = System.nanoTime(); //DEBUG - TIMING
        int maxX = ((1 << (nbChannels - 1)) | 1); // | 1 of + 1 ??? TODO test
        short acc = 2; //n-acc shifts

        //Iterate  over all comparator combinations
        for (int x = 3; x <= maxX; x = (x << 1) - 1) { //x = number we shift.
            int maxShifts = nbChannels - acc;
            for (short nShift = 0; nShift <= maxShifts; nShift++) {
                short comp = (short) (x << nShift);
                generate_sub(nbChannels, maxX, network, comp, index);
            }
            acc++;
        }

        long end = System.nanoTime(); //DEBUG - TIMING
        System.out.println("Generate took " + (end - begin) + " nanoseconds."); //DEBUG - TIMING

        this.closeOutputStream();
    }

    /**
     * The subroutine of {@link Generator#generate}
     *
     * @param nbChannels The amount of channels.
     * @param network The comparators, forming a network.
     * @param comp The comparator which we have to add.
     * @param index The current index to which to add the comp.
     */
    private void generate_sub(int nbChannels, int maxX, short[] network, short comp, int index) {
        int max = network.length;
        network[index] = comp;
        int acc = 2; //n-acc opschuiven
        int maxShifts;
        short nextComp;
        int nextIndex = index+1;

        //Continue adding
        if (nextIndex < max) {
            //Iterate over all comparator combinations
            for (int x = 3; x <= maxX; x = (x << 1) - 1) { //x*2 - 1
                maxShifts = nbChannels - acc;
                acc++;
                for (short nShift = 0; nShift <= maxShifts; nShift++) { //shift n-2, n-3, ... keer
                    nextComp = (short) (x << nShift);
                    generate_sub(nbChannels, maxX, network, nextComp, nextIndex);
                }
            }
        } else {
            writeNetwork(nbChannels, network);
        }
    }

    //TODO: Instead of saving shorts; save the last nbChannel bits of the short.
    /**
     * Write the given network to the outputPath. format: Int(nbChannels),
     * Int(network.length), Short(network[0]), Short(network[1]),...
     *
     * @param nbChannels The amount of channels of the network.
     * @param network The network.
     */
    public void writeNetwork(int nbChannels, short[] network) {
        try {
            if (dos == null) {
                dos = new DataOutputStream(new FileOutputStream(new File(this.outputPath)));
            }

            dos.writeInt(nbChannels);
            dos.writeInt(network.length); //#Comperators
            for (short comp : network) {
                dos.writeShort(comp);
            }
        } catch (IOException ex) {
            Logger.getLogger(Generator3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Close the {@link OutputStream} used for saving.
     */
    public void closeOutputStream() {
        try {
            if (dos != null) {
                dos.flush();
                dos.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Generator3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
