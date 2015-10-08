package networklib;

import java.util.ArrayList;

/**
 * A {@link Network} consisting of a certain amount of channels and
 * {@link Comparator}s covering those channels.
 *
 * @author Admin
 */
public class Network {

    private int nbChannels;
    private int nbComp;

    private final ArrayList<Comparator> compList;

    /**
     * Create a {@link Network} with a certain amount of channels and
     * {@link Comparator}s.
     *
     * @param nbChannels The amount of channels.
     * @param nbComp The amount of {@link Comparator}s.
     */
    public Network(int nbChannels, int nbComp) {
        this.nbChannels = nbChannels;
        this.nbComp = nbComp;

        this.compList = new ArrayList<>(nbComp);
    }

    /**
     * Get the amount of {@link Comparator}s this {@link Network} CAN hold.
     *
     * @return The maximum amount of {@link Comparator}s.
     */
    public int getNbComparators() {
        return this.nbComp;
    }

    /**
     * Get the amount of channels this {@link Network} consists of.
     *
     * @return The current amount of channels.
     */
    public int getNbChannels() {
        return this.nbChannels;
    }

    /**
     * Get the {@link ArrayList} of {@link Comparator}s in this {@link Network}.
     * <b>Changes will effect this network.</b>
     * @return The {@link ArrayList} of current {@link Comparator}s.
     */
    public ArrayList<Comparator> getComparators() {
        return this.compList;
    }

    //TODO: Throw zeker een error bij het gebruiken van Comparators als de channel < channel1|channel2
    /**
     * Add the given {@link Comparator}s to this {@link Network} if it does not
     * exceed the capacity. The channel numbers of the {@link Comparator} should
     * be suitable for the amount of channels.
     *
     * @param comps The {@link Comparator}s to be added.
     * @see getNbComparators()
     */
    public void addComparator(Comparator... comps) {
        for (Comparator comp : comps) {
            if (compList.size() < nbComp) {
                compList.add(comp);
            } else {
                System.err.println("Comparator limit reached while trying to add more.");
                return;
            }
        }
    }

    /**
     * Add a new {@link Comparator} covering channel1 and channel2 to this
     * {@link Network}. channel1 & channel2 must be suitable for this
     * {@link Network}.
     *
     * @param channel1 The number of the first channel the {@link Comparator}
     * should cover.
     * @param channel2 The number of the second channel the {@link Comparator}
     * should cover.
     */
    public void addComparator(int channel1, int channel2) {
        addComparator(new Comparator((short) channel1, (short) channel2));
    }

    /**
     * Returns a string representation of this {@link Comparator}.
     *
     * @return The amount of channels followed by the amount of comparators and
     * a sequence of comparators.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNbChannels());
        sb.append(" ");
        sb.append(getNbComparators());
        sb.append(" ");
        for (Comparator comp : compList) {
            sb.append(comp.toString());
        }

        return sb.toString();
    }

}
