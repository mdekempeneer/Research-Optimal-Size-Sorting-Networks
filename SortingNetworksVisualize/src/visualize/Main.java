package visualize;

import java.io.BufferedReader;
import java.io.FileReader;
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
     * @param args Analyse Args -f filePath || -n n k (a,b)(c,d)
     */
    public static void main(String args[]) {
        /* Analyse Args */
        Network network = null;
        
        if (args.length >= 1) {
            if (args[0].equals("-f")) {
                //File
               String line = Misc.readFile(args[1]);
               network = Network.stringToNetwork(line);
            }
            else if (args[0].equals("-n")) {
                //Args
                int nbChannel = Integer.parseInt(args[1]);
                int nbComp = Integer.parseInt(args[2]);
                network = Network.stringToNetwork(nbChannel, nbComp, args[3]);
            }
        }

        /* Initialize Frame */
        JNetwork jNetwork = new JNetwork(network);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frame(jNetwork).setVisible(true);
            }
        });

    }

}
