package networklib;

/**
 * A Comparator is used to cover 2 different channels and is able to compare the
 * value given over those 2 channels and sort them in an ascending order.
 *
 * @author Admin
 */
public class Comparator {

    private short nbChannel1;
    private short nbChannel2;

    /**
     * Create a Comparator covering 2 channels.
     *
     * @param nbChannel1 The number of the first channel this covers.
     * @param nbChannel2 The number of the second channel this covers.
     */
    public Comparator(short nbChannel1, short nbChannel2) {
        this.nbChannel1 = nbChannel1;
        this.nbChannel2 = nbChannel2;
    }

    /**
     * Get the number of the first channel this covers.
     *
     * @return The number of channel1.
     */
    public short getChannel1() {
        return this.nbChannel1;
    }

    /**
     * Get the number of the second channel this covers.
     *
     * @return The number of channel2.
     */
    public short getChannel2() {
        return this.nbChannel2;
    }

    /**
     * Returns a string representation of this {@link Comparator}.
     *
     * @return (&lt;channel1&gt;,&lt;channel2&gt;)
     */
    @Override
    public String toString() {
        return ("(" + getChannel1() + "," + getChannel2() + ")");
    }

}
