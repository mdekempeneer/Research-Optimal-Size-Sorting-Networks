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
     *
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
     * Parse the line to create a {@link Network}. The format of the line should
     * follow: <i>N K (a,b)(c,d)(g,k)</i> <br>With N being the amount of
     * channels. K being the amount of comparators Followed by the string
     * representation of K {@link Comparator}s.</br>
     *
     * @param line The line which to parse into a {@link Network}.
     * @return The created network.
     *
     * @throws IllegalArgumentException When line doesn't match the format.
     * @throws NumberFormatException When Integer.parseInt(String) fails to
     * parse the channel numbers.
     *
     * @see {@link Network#getNbChannels()}, {@link Network#getNbComparators()},
     * {@link Comparator#toString())}
     */
    public static Network stringToNetwork(String line) throws IllegalArgumentException, NumberFormatException {
        Network network;
        int nbChannels;
        int nbComp;
        String[] splitLine = line.split(" ");

        if (splitLine.length == 3) {
            /* Parse initial parameters N K */
            nbChannels = Integer.parseInt(splitLine[0]);
            nbComp = Integer.parseInt(splitLine[1]);
            network = stringToNetwork(nbChannels, nbComp, splitLine[2]);
        } else {
            throw new IllegalArgumentException("Invalid format for stringToNetwork. Format: N K (a,b)(c,d)(g,k)");
        }

        return network;
    }

    /**
     * Creates a {@link Network} by parsing the compLine and retrieving the
     * {@link Comparator}s. The format of the line should follow: <i>
     * (a,b)(c,d)(g,k)</i> . Which is the String representation of K
     * {@link Comparator}s.
     *
     * @param nbChannels The amount of channels in the {@link Network}.
     * @param nbComp The maximum amount of {@link Comparator} in the
     * {@link Network}.
     * @param compLine The String which contains the {@link Comparator}s.
     *
     * @return The created network.
     *
     * @throws IllegalArgumentException When line doesn't match the format.
     * @throws NumberFormatException When Integer.parseInt(String) fails to
     * parse the channel numbers.
     *
     * @see {@link Network#getNbChannels()}, {@link Network#getNbComparators()},
     * {@link Comparator#toString())}
     */
    public static Network stringToNetwork(int nbChannels, int nbComp, String compLine) throws IllegalArgumentException {
        Network network = new Network(nbChannels, nbComp);
        String[] splitComp = compLine.split("\\(");
        for (int i = 1; i < splitComp.length; i++) { //a,b)
            String comp = splitComp[i].replaceAll("\\)", "");
            String[] channel = comp.split(",");
            if (channel.length == 2) {
                int channel1 = Integer.parseInt(channel[0]);
                int channel2 = Integer.parseInt(channel[1]);
                network.addComparator(channel1, channel2);
            } else {
                throw new IllegalArgumentException("Invalid format for stringToNetwork. Format: N K (a,b)(c,d)(g,k)");
            }
        }

        return network;
    }

    /**
     * Returns a string representation of this {@link Comparator}. Example of 5
     * channels and 3 comparators: <i>5 3 (1,2)(2,3)(5,1)</i>
     *
     *
     * @return The amount of channels followed by the amount of comparators and
     * a sequence of comparators.
     * @see Comparator#toString()
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

    /**
     * Get the result of propagating the input trough this network.
     *
     * @param input The input that has to propagate trough the network.
     * @return The resulting output.
     */
    public Bit[] getOutput(Bit[] input) {
        Bit[] result = new Bit[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = new Bit(input[i].getValue());
        }

        for (Comparator comp : getComparators()) {
            comp.swap(result);
        }

        return result;
    }
}
