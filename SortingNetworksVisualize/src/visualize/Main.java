package visualize;

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
        /* Parse & create Network */
        //TODO: Parse 
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
