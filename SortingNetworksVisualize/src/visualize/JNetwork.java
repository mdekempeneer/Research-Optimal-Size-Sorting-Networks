package visualize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JComponent;
import networklib.Comparator;
import networklib.Network;

/**
 * A {@link JNetwork} composes a {@link Network} while extending the
 * {@link JComponent} to be able to use with {@link JPanels}.
 *
 * @author Admin
 */
public class JNetwork extends JComponent {

    private Network network;

    /**
     * The dimension of the image of the JNetwork.
     */
    private final Dimension imgDim;

    /* Variables for painting */
    //The position where the channels begin painting (upper left)
    public static final Point CHANNELS_BEGIN = new Point(20, 20);
    //The x-space between comparators.
    public static final short SPACE_PER_COMP = 20;
    //The y-space between channels.
    public static final short SPACE_PER_CHANNEL = 30;
    //The size of the oval used at the endings of a comparator line.
    private static final short SIZE_COMP_POINT = 4;

    /**
     * Create a {@link JNetwork} composing the given {@link Network}.
     *
     * @param network The {@link Network} this composes.
     */
    public JNetwork(Network network) {
        this.network = network;

        int height = JNetwork.SPACE_PER_CHANNEL * network.getNbChannels();
        int width = JNetwork.SPACE_PER_COMP * (network.getNbComparators() + 2);
        this.imgDim = new Dimension(width, height);
    }

    /**
     * Create a {@link JNetwork} that composes a {@link Network} with given
     * amount of channels and {@link Comparator} capacity.
     *
     * @param nbChannels The amount of channels.
     * @param nbComp The maximum amount of {@link Comparator}s.
     */
    public JNetwork(int nbChannels, int nbComp) {
        this(new Network(nbChannels, nbComp));
    }

    /**
     * Get the {@link Network} this composes.
     *
     * @return The current {@link Network}.
     */
    public Network getNetwork() {
        return this.network;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int nbChannels = network.getNbChannels();
        int nbComp = network.getNbComparators();
        ArrayList<Comparator> compList = network.getComparators();

        // Paint Channels
        for (int channel = 0; channel < nbChannels; channel++) {
            Point begin = (Point) CHANNELS_BEGIN.clone();
            begin.translate(0, channel * SPACE_PER_CHANNEL);
            int length = SPACE_PER_COMP * (nbComp + 2); //2 For begin & end space.

            g.drawLine(begin.x, begin.y, begin.x + length, begin.y);
        }

        //Paint Comparators
        short halfPointSpace = SIZE_COMP_POINT / 2;
        for (int comp = 1; comp <= compList.size(); comp++) {
            Point begin = (Point) CHANNELS_BEGIN.clone();
            begin.translate(comp * SPACE_PER_COMP, (compList.get(comp - 1).getChannel1() - 1) * SPACE_PER_CHANNEL);
            Point end = (Point) CHANNELS_BEGIN.clone();
            end.translate(comp * SPACE_PER_COMP, (compList.get(comp - 1).getChannel2() - 1) * SPACE_PER_CHANNEL);

            //Draw Line
            g.setColor(Color.RED);
            g.drawLine(begin.x, begin.y, end.x, end.y);

            //Draw EndPoints
            g.setColor(Color.RED);
            g.fillOval(begin.x - halfPointSpace, begin.y - halfPointSpace, SIZE_COMP_POINT, SIZE_COMP_POINT);
            g.fillOval(end.x - halfPointSpace, end.y - halfPointSpace, SIZE_COMP_POINT, SIZE_COMP_POINT);
        }
    }

    /**
     * Return a {@link BufferedImage} which has a {@link JNetwork} painted on.
     *
     * @param isSorted Whether this network is a sorting network.
     *
     * @return A BufferedImage containing the JNetwork's image.
     */
    public BufferedImage getpaintedJNetwork(boolean isSorted) {
        BufferedImage bi = new BufferedImage(imgDim.width-16, imgDim.height-16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.translate(-16, -16);
        if (isSorted) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(0, 0, imgDim.width, imgDim.height);
        g.setColor(Color.BLACK);
        paintComponent(g);

        return bi;
    }
}
