package sortingnetworksparallel;

import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author Mathias Dekempeneer and Vincent Derkinderen
 * @version 1.0
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int nbChannels = 8;
        int upperBound = 19;
        int innerSize = 128;
        double percThreads = 1;

        if (args.length == 0) {
            nbChannels = getAnswerFromUser("Amount of channels?", nbChannels);
            upperBound = getAnswerFromUser("Expected upperBound?", upperBound);
        } else {
            nbChannels = Integer.parseInt(args[0]);
            upperBound = Integer.parseInt(args[1]);

            if (args.length == 4) {
                innerSize = Integer.parseInt(args[2]);
                percThreads = Double.parseDouble(args[3]);
            }
        }

        if (nbChannels <= 1 || nbChannels > 16) {
            System.err.println("Algorithm/Datastructures can only handle 2-16 channels.");
            return;
        }

        if (upperBound < 1) {
            System.err.println("Less than 1 comparator makes no sense.");
        }

        /* Start */
        long begin;
        long caseTime = 0;
        short[] result = null;
        begin = System.nanoTime();

        //Init
        processor = new Processor((short) nbChannels, upperBound, innerSize, percThreads);

        //Process
        result = processor.process();

        caseTime = System.nanoTime() - begin;
        System.out.println("Took " + caseTime + " ns");
        System.out.println(Arrays.toString(result));
    }

    /**
     * Asks the user for the number of channels in the network.
     *
     * @param question The question to ask the user.
     * @param def The default value.
     * @return The amount of channels. If the user declined or forced an error
     * it results in -1.
     */
    private static int getAnswerFromUser(String question, Object def) {
        String output = JOptionPane.showInputDialog(question, def);
        int result;
        try {
            result = Integer.parseInt(output);
        } catch (Exception ex) {
            result = -1;
        }

        return result;
    }


    private static Processor processor;
}
