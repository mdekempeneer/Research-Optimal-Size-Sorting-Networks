package sortingnetworkspaper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import sortingnetworkspaper.memory.ObjArrayList;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int loadMode = getYesNoCancelFromUser("Resume from saved file?");
        int saveMode = getYesNoCancelFromUser("Save on prune?");

        //Cancelled
        if (loadMode == JOptionPane.CANCEL_OPTION || saveMode == JOptionPane.CANCEL_OPTION
                || loadMode == JOptionPane.CLOSED_OPTION || saveMode == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }

        int nbChannels = getAnswerFromUser("Amount of channels?", 7);
        int upperBound = getAnswerFromUser("Expected upperBound?", 16);
        if (nbChannels <= 1 || nbChannels > 16) {
            System.err.println("Algorithm/Datastructures can only handle 2-16 channels.");
            return;
        }

        if (upperBound < 1) {
            System.err.println("Less than 1 comparator makes no sense.");
        }

        //Load input
        String loadPath = null;
        if (loadMode == JOptionPane.YES_OPTION) {
            loadPath = askPath("load");
        }

        //Save input
        String savePath = null;
        if (saveMode == JOptionPane.YES_OPTION) {
            savePath = askPath("save");
        }

        /* Load start */
        ObjArrayList<short[][]> N = null;
        if (loadMode == (JOptionPane.YES_OPTION)) {
            N = getN(loadPath);
        }

        /* Clean start */
        int nbCase = 1;
        int cCase = 0;
        long begin;
        long total = 0;
        long caseTime = 0;
        short[] result = null;
        while (nbCase > cCase) {
            begin = System.nanoTime();

            //Init
            //Processor processor = new ParallelProcessor((short) nbChannels, upperBound);
            if (savePath != null && !savePath.equals("")) {
                IOThread.start();
                processor = new SingleProcessor((short) nbChannels, upperBound, savePath);
            } else {
                processor = new SingleProcessor((short) nbChannels, upperBound);
            }
            //Process
            if (loadPath != null && !loadPath.equals("")) {
                result = processor.process(N);
            } else {
                result = processor.process();
            }

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

    //TODO: Comment
    private static int getYesNoCancelFromUser(String question) {
        return JOptionPane.showConfirmDialog(null, question);
    }

    private static SingleProcessor processor;

    private static final Thread IOThread = new Thread(new Runnable() {

        @Override
        public void run() {
            int n = JOptionPane.showOptionDialog(null,
                    "Would you like to stop generate and prune?",
                    "Stop",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //do not use a custom Icon
                    new Object[]{"Yes"}, //the titles of buttons
                    "Yes"); //default button title

            if (processor != null) {
                processor.initiateSave();
            }
            System.out.println("Started saving process ");
        }
    });

    /**
     * Uses the JFileChooser to ask a savePath.
     *
     * @return The savePath, null when canceled.
     */
    private static String askPath(String type) {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (type.equals("load")) {
            if (jfc.showDialog(null, "load") == JFileChooser.APPROVE_OPTION) {
                return jfc.getSelectedFile().getAbsolutePath();
            } else {
                System.out.println("Failed chosing a file.");
            }
        } else {
            if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                return jfc.getSelectedFile().getAbsolutePath();
            } else {
                System.out.println("Failed chosing a file.");
            }
        }
        return null;
    }

    private static ObjArrayList<short[][]> getN(String loadPath) {
        ObjectInputStream iis = null;
        try {
            iis = new ObjectInputStream(new BufferedInputStream(new FileInputStream(loadPath)));
            return (ObjArrayList<short[][]>) iis.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (iis != null) {
                    iis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
