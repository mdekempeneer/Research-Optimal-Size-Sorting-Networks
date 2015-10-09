package visualize;

import java.io.BufferedReader;
import java.io.FileReader;
import networklib.Network;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     * Execute the program.
     *
     * @param args
     */
    public static void main(String args[]) {
        /* Analyse Args */
        //TODO: Analyse Args (-f filePath || -n n k (a,b)(c,d)(
        if (args.length > 1) {
            
        }
        /* Parse & create Network */
        
        /*try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) { //TODO: Change to inputFile
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            if (line != null) {
                Parser.parse(line);
            }
        }*/

        //Test Input
        Network network = new Network(4, 8);
        network.addComparator(1, 2);
        network.addComparator(3, 4);
        network.addComparator(2, 3);
        network.addComparator(1, 2);
        network.addComparator(3, 4);

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
