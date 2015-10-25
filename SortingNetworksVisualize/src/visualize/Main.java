package visualize;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import networklib.Network;

/**
 *
 * @author Admin
 */
public class Main {

    private static Frame frame;

    private static void setFrame(Frame frame) {
        Main.frame = frame;
    }

    /**
     * Execute the program.
     *
     * @param args -fp filePath || -np n k (a,b)(c,d)
     */
    public static void main(String args[]) {
        try {
            /* Create Frame */
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Frame frame = new Frame();
                    frame.setVisible(true);
                    Main.setFrame(frame);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
       

        /* Analyse Args */
        BufferedImage img = null;
        Network network = null;
        JNetwork jNetwork = null;
        boolean isSorted = false;

        long begin = 0;
        long end = 0;

        if (args.length >= 1) {
            if (args[0].startsWith("-f")) {
                /* Files - multiple networks */
                String line;
                String pathParent = "";
                String newPath;
                int counter = 1;

                /* Retrieve save file location */
                if (args[0].contains("p")) {
                    String path = askSavePath();
                    if (path == null) {
                        return;
                    }
                    pathParent = new File(path).getParent();
                }

                begin = System.nanoTime();  //DEBUGGER: Timer
                /* Iterate over all lines/networks */
                try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
                    while ((line = br.readLine()) != null) {
                        /* Check if sorted */
                        String[] splitLine = line.split(" ");
                        if (splitLine.length >= 4 && splitLine[3].toLowerCase().startsWith("s")) {
                            newPath = pathParent + "/s_" + counter + ".jpg";
                            isSorted = true;
                        } else if (splitLine.length >= 4 && splitLine[3].toLowerCase().startsWith("u")) {
                            newPath = pathParent + "/u_" + counter + ".jpg";
                            isSorted = false;
                        } else {
                            newPath = pathParent + "/" + counter + ".jpg";
                            isSorted = false;
                        }

                        /* Add Network and take a screenshot if required. */
                        network = Network.stringToNetwork(line);
                        jNetwork = new JNetwork(network);
                        frame.setJNetwork(new JNetwork(network), isSorted);

                        if (args[0].contains("p")) {
                            ImageIO.write(jNetwork.getpaintedJNetwork(isSorted), "jpg", new File(newPath));
                        }
                        counter++;
                    }

                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                end = System.nanoTime(); //DEBUGGER: Timer
            } else if (args[0].startsWith("-n")) {
                /* Arguments - 1 Network */
                int nbChannel = Integer.parseInt(args[1]);
                int nbComp = Integer.parseInt(args[2]);
                network = Network.stringToNetwork(nbChannel, nbComp, args[3]);
                jNetwork = new JNetwork(network);

                /* Check if Sorted */
                isSorted = args.length >= 5 && args[4].toLowerCase().startsWith("s");
                frame.setJNetwork(jNetwork, isSorted);
                
                /* Take Screenshot */
                if (args[0].contains("p")) {
                    String path = askSavePath();
                    try {
                        ImageIO.write(jNetwork.getpaintedJNetwork(isSorted), "jpg", new File(path));
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.out.println("Took:" + (end - begin) + " nanoseconds.");
    }

    /**
     * Uses the JFileChooser to ask a savePath.
     *
     * @return The savePath, null when canceled.
     */
    private static String askSavePath() {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("Failed chosing a file.");
        }

        return null;
    }

}
