package visualize;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * The {@link Frame} gives a visual representation of a {@link Network} provided
 * by the associated {@link JNetwork}.
 *
 * @author Admin
 */
public class Frame extends JFrame {

    /**
     * The {@link JNetwork} providing the visual representation.
     */
    private final JNetwork jNetwork;
    private final Dimension networkDim;
    private final Dimension imgDim;

    /**
     * Create a {@link JFrame} holding a certain {@link JNetwork} to display.
     *
     * @param jNetwork The {@link JNetwork} to display.
     */
    public Frame(JNetwork jNetwork) {
        this.jNetwork = jNetwork;

        //Calculate dimension
        int channels = jNetwork.getNetwork().getNbChannels();
        int comps = jNetwork.getNetwork().getNbComparators();

        int height = JNetwork.SPACE_PER_CHANNEL * channels;
        int width = JNetwork.SPACE_PER_COMP * (comps + 2);
        this.imgDim = new Dimension(width, height);

        height += 2 * JNetwork.CHANNELS_BEGIN.y + 50;
        width += 2 * JNetwork.CHANNELS_BEGIN.x + 50;
        this.networkDim = new Dimension(width, height);

        initComponents();
    }

    /**
     * Create a {@link JFrame} holding a certain {@link JNetwork} to display.
     *
     * @param jNetwork The {@link JNetwork} to display.
     * @param isSorted Whether the network is a sorting network.
     */
    public Frame(JNetwork jNetwork, boolean isSorted) {
        this(jNetwork);
        if (isSorted) {
            this.getContentPane().setBackground(Color.YELLOW);
        }
    }

    /**
     * Initialize the components.
     */
    private void initComponents() {
        //Configure components
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VisualNetwork");

        //Add components
        this.setLayout(new CardLayout());
        this.add(jNetwork);

        //Finish layout
        pack();
        this.setSize(networkDim);
    }

    /**
     * Get a screenshot of the JFrame.
     *
     * @return The screenshot.
     */
    public BufferedImage getScreenShot() {
        BufferedImage bi = new BufferedImage(imgDim.width, imgDim.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.translate(-25, -40);
        this.paint(g);
        return bi;
    }

}
