package visualize;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import networklib.Misc;
import networklib.Network;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     * Execute the program.
     *
     * @param args -fp filePath || -np n k (a,b)(c,d)
     */
    public static void main(String args[]) {
        /* Analyse Args */
        Network network = null;
        boolean isSorted;

        if (args.length >= 1) {
            if (args[0].startsWith("-f")) {
                //File
                String line = Misc.readFile(args[1]);
                network = Network.stringToNetwork(line);
            } else if (args[0].startsWith("-n")) {
                //Args
                int nbChannel = Integer.parseInt(args[1]);
                int nbComp = Integer.parseInt(args[2]);
                network = Network.stringToNetwork(nbChannel, nbComp, args[3]);
            }
        }
        
        /* Adjust for sorting networks */
        isSorted = (args.length >= 5 && args[4].toLowerCase().startsWith("s"));

        /* Initialize Frame */
        JNetwork jNetwork = new JNetwork(network);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Frame frame = new Frame(jNetwork, isSorted);
                frame.setVisible(true);

                if (args[0].contains("p")) {
                    try {
                        /* JFileChooser for output */
                        JFileChooser jfc = new JFileChooser();
                        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
                        
                        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            ImageIO.write(frame.getScreenShot(), "jpg", jfc.getSelectedFile());
                        } else {
                            System.out.println("Failed chosing a file.");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }

}
