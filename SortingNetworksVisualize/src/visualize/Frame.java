package visualize;

import java.awt.CardLayout;
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

    private final Dimension FRAME_DIM = new Dimension(1000, 500);

    /**
     * Create a {@link JFrame} holding a certain {@link JNetwork} to display.
     *
     * @param jNetwork The {@link JNetwork} to display.
     */
    public Frame(JNetwork jNetwork) {
        this.jNetwork = jNetwork;
        initComponents();
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
        this.setSize(FRAME_DIM);
    }

}
