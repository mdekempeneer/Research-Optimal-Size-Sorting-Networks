package sortingnetworksparallel;

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
import sortingnetworksparallel.memory.ObjArrayList;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int loadMode = JOptionPane.NO_OPTION;
        int saveMode = JOptionPane.NO_OPTION;
        
        int nbChannels = 8;
        int upperBound = 19;
        int innerSize = 64;
        double percThreads = 0.75;
        
        if (args.length == 0) {
            loadMode = getYesNoCancelFromUser("Resume from saved file?");
            saveMode = getYesNoCancelFromUser("Save on prune?");

            //Cancelled
            if (loadMode == JOptionPane.CANCEL_OPTION || saveMode == JOptionPane.CANCEL_OPTION
                    || loadMode == JOptionPane.CLOSED_OPTION || saveMode == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }

            nbChannels = getAnswerFromUser("Amount of channels?", nbChannels);
            upperBound = getAnswerFromUser("Expected upperBound?", upperBound);
        } else {
            nbChannels = Integer.parseInt(args[0]);
            upperBound = Integer.parseInt(args[1]);
            
            if(args.length == 4) {
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
        ObjArrayList<short[][]> oldL = null;
        int startIndex = 0;
        ObjArrayList<short[][]> newL = null;
        short nbComp = 0;
        if (loadMode == (JOptionPane.YES_OPTION)) {
            ObjectInputStream iis = null;
            try {
                iis = new ObjectInputStream(new BufferedInputStream(new FileInputStream(loadPath)));
                oldL = (ObjArrayList<short[][]>) iis.readObject();
                startIndex = iis.readInt();
                newL = (ObjArrayList<short[][]>) iis.readObject();
                nbComp = iis.readShort();
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
            if (oldL != null && newL != null) {
                System.out.println("Loaded " + oldL.size() + " networks w comp " + nbComp + " and new " + newL.size() + " and index " + startIndex);
            }
        }

        /* Clean start */
        long begin;
        long caseTime = 0;
        short[] result = null;
        begin = System.nanoTime();

        //Init
        if (savePath != null && !savePath.equals("")) {
            IOThread.start();
            processor = new Processor((short) nbChannels, upperBound, savePath, innerSize, percThreads);
        } else {
            processor = new Processor((short) nbChannels, upperBound, innerSize, percThreads);
        }

        //Process
        if (loadPath != null && !loadPath.equals("")) {
            result = processor.process(oldL, startIndex, newL, nbComp);
        } else {
            result = processor.process();
        }

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

    //TODO: Comment
    private static int getYesNoCancelFromUser(String question) {
        return JOptionPane.showConfirmDialog(null, question);
    }

    private static Processor processor;

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
            System.out.println("Saving process will start at the end of the current cycle.");
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
