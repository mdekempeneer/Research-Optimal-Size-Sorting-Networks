package sortingnetworkspaper;

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
        int nbChannels = getAnswerFromUser("Amount of channels?", 4);
        int upperBound = getAnswerFromUser("Expected upperBound?", 5);

        if (nbChannels <= 1 || nbChannels > 16) {
            System.err.println("Algorithm/Datastructures can only handle 2-16 channels.");
            return;
        }
        
        if(upperBound < 1) {
            System.err.println("Less than 1 comparator makes no sense.");
        }

        Processor processor = new Processor((short) nbChannels, upperBound);
        processor.process();
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
