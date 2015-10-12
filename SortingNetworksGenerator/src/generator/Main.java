package generator;

import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import networklib.Comparator;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            int nbChannels = Integer.parseInt(args[0]);
            int nbComp = Integer.parseInt(args[1]);
            String outputPath;

            /* JFileChooser to choose the outputPath */
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = jfc.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                /* Generate */
                outputPath = jfc.getSelectedFile().getAbsolutePath();
                Generator gen = new Generator(nbChannels, nbComp, outputPath);
                gen.generate();
            } else {
                System.out.println("Failed chosing a file.");
            }
        } else if (args.length == 3) {
            int nbChannels = Integer.parseInt(args[0]);
            int nbComp = Integer.parseInt(args[1]);
            String outputPath = args[2];

            /* Generate */
            Generator gen = new Generator(nbChannels, nbComp, outputPath);
            gen.generate();
        } else {
            System.out.println("Format is: n k");
        }
    }
}