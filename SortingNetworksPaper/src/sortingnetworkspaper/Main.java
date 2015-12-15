package sortingnetworkspaper;

import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int nbChannels = getAnswerFromUser("Amount of channels?", 7);
        int upperBound = getAnswerFromUser("Expected upperBound?", 16);

        if (nbChannels <= 1 || nbChannels > 16) {
            System.err.println("Algorithm/Datastructures can only handle 2-16 channels.");
            return;
        }

        if (upperBound < 1) {
            System.err.println("Less than 1 comparator makes no sense.");
        }

        int nbCase = 1;
        int cCase = 0;
        long begin;
        long total = 0;
        long caseTime = 0;
        short[] result = null;
        while (nbCase > cCase) {
            begin = System.nanoTime();
            //Processor processor = new ParallelProcessor((short) nbChannels, upperBound);
            Processor processor = new SingleProcessor((short) nbChannels, upperBound);
            result = processor.process();
            
            cCase++;
            caseTime = System.nanoTime() - begin;
            System.out.println("Took " + caseTime + " ns");
            total += caseTime;
            
        }
        System.out.println(Arrays.toString(result));
        System.out.println("Total: " + total + " avg: " + total / nbCase + " ns");
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
}
