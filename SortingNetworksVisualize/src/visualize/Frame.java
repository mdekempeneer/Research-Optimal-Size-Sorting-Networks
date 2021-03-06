package visualize;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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
    private JNetwork jNetwork;
    private Dimension networkDim;

    public Frame() {
        initComponents();
    }

    /**
     * Set a new current {@link JNetwork} and adjust dimensions.
     *
     * @param jNetwork The new jNetwork to display.
     */
    public final void setJNetwork(JNetwork jNetwork, boolean sorted) {
        if (this.jNetwork != null) {
            this.remove(this.jNetwork);
        }
        if (sorted) {
            this.getContentPane().setBackground(Color.YELLOW);
        } else {
            this.getContentPane().setBackground(null);
        }
        this.jNetwork = jNetwork;
        this.add(this.jNetwork);

        updateDimensions();
        jNetwork.revalidate();
        repaint();
    }

    /**
     * Update the image and frame dimensions using the current network.
     */
    private void updateDimensions() {
        if (jNetwork != null) {
            int channels = jNetwork.getNetwork().getNbChannels();
            int comps = jNetwork.getNetwork().getNbComparators();

            int height = JNetwork.SPACE_PER_CHANNEL * channels;
            int width = JNetwork.SPACE_PER_COMP * (comps + 2);

            height += 2 * JNetwork.CHANNELS_BEGIN.y + 50;
            width += 2 * JNetwork.CHANNELS_BEGIN.x + 50;
            this.networkDim = new Dimension(width, height);

            this.setSize(networkDim);
        }
    }

    /**
     * Create a {@link JFrame} holding a certain {@link JNetwork} to display.
     *
     * @param jNetwork The {@link JNetwork} to display.
     * @param isSorted Whether the network is a sorting network.
     */
    public Frame(JNetwork jNetwork, boolean isSorted) {
        initComponents();
        setJNetwork(jNetwork, isSorted);
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

        //Finish layout
        pack();
        this.setSize(new Dimension(800, 500));
    }

    /**
     * Get the currently used {@link JNetwork}.
     * @return The {@link JNetwork} currently in use.
     */
    public JNetwork getJNetwork() {
        return this.jNetwork;
    }

}
