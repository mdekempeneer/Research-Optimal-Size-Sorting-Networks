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
        int nbChannels = getNbChannelsFromUser();
        int upperBound = 35; //TODO: Ask user.

        if (nbChannels <= 1 || nbChannels > 16) {
            System.err.println("Algorithm/Datastructures can only handle 2-16 channels.");
            return;
        }

        Processor processor = new Processor((short) nbChannels);
        processor.process(upperBound);
    }

    /**
     * Asks the user for the number of channels in the network.
     *
     * @return The amount of channels. If the user declined or forced an error
     * it results in -1.
     */
    private static int getNbChannelsFromUser() {
        String output = JOptionPane.showInputDialog("Amount of channels?", 4);
        int result;
        try {
            result = Integer.parseInt(output);
        } catch (Exception ex) {
            result = -1;
        }

        return result;
    }
}
