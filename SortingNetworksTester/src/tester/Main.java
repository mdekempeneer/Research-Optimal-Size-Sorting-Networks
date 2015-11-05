package tester;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Admin
 */
public class Main {
    
    public static void main(String[] args) {
        Tester2 tester = new Tester2();
        
        if(args.length >= 2) {
            int nbChannels = Integer.parseInt(args[0]);
            tester.isSortingNetwork(nbChannels, tester.parseNetwork(args[1]));
        } else {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File(System.getProperty("user.home")));

            int result = jfc.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION) { //Open File
                String inputPath = jfc.getSelectedFile().getAbsolutePath();
                
                result = jfc.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION) { //Save File
                    String outputPath = jfc.getSelectedFile().getAbsolutePath();
                    tester.processFile(inputPath, outputPath); //Test
                } else {
                    tester.processFile(inputPath); //Test
                }
            }
        }
        System.out.println("Tests accumulated to " + tester.time + " ns.");
    }
    
}
