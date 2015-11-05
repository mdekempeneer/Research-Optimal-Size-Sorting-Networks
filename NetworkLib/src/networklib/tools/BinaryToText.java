package networklib.tools;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import networklib.Comparator;

/**
 * Convert a Binary file to text file.
 * @author Admin
 */
public class BinaryToText {

    private final String inputPath;
    private final String outputPath;
    private DataInputStream dis;
    private BufferedWriter bw;

    /**
     *
     * @param inputPath
     */
    public BinaryToText(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void convert() {
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(inputPath))));

            while (dis.available() > 0) {
                int nbChannels = dis.readInt();
                int nbComps = dis.readInt();
                Comparator[] list = new Comparator[nbComps];
                for (int i = 0; i < nbComps; i++) {
                    short comp = dis.readShort();

                    int pos1 = 32 - Integer.numberOfLeadingZeros(comp & 0xFFFF);
                    int pos2 = Integer.numberOfTrailingZeros(comp & 0xFFFF) + 1;

                    list[i] = new Comparator((short) pos2, (short) pos1);
                }
                writeList(nbChannels, list);
            }

            closeStreams();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BinaryToText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BinaryToText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write the list to the outputFile.
     *
     * @param nbChannels
     * @param list
     */
    private void writeList(int nbChannels, Comparator[] list) {
        StringBuilder sb = new StringBuilder();
        sb.append(nbChannels);
        sb.append(" ");
        sb.append(list.length);
        sb.append(" ");
        for (Comparator comp : list) {
            sb.append(comp.toString());
        }

        try {
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(outputPath));
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(BinaryToText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Close all streams.
     */
    private void closeStreams() {
        try {
            if (bw != null) {
                bw.flush();
                bw.close();
            }
            if (dis != null) {
                dis.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(BinaryToText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create a new {@link BinaryToText} instance by using
     * {@link JFileChooser}s.
     *
     * @return The new BinaryToText instance, null if nothing was
     * selected/canceled.
     */
    public static BinaryToText getBinaryToText() {
        String inputPath;
        String outputPath;

        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = jfc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            inputPath = jfc.getSelectedFile().getAbsolutePath();

            jfc.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                outputPath = jfc.getSelectedFile().getAbsolutePath();

                return new BinaryToText(inputPath, outputPath);
            }
        }

        return null;
    }

}
