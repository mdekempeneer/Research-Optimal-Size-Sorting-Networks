package networklib;

import tester.Bit;

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
        this.nbChannel1 = (nbChannel1 < nbChannel2) ? nbChannel1 : nbChannel2;
        this.nbChannel2 = (nbChannel1 < nbChannel2) ? nbChannel2 : nbChannel1;
    }

    /**
     * Get the number of the first channel this covers.
     *
     * @return The number of channel1, the lowest channelnumber.
     */
    public short getChannel1() {
        return this.nbChannel1;
    }

    /**
     * Get the number of the second channel this covers.
     *
     * @return The number of channel2, the highest channelnumber.
     */
    public short getChannel2() {
        return this.nbChannel2;
    }

    /**
     * Swap the bits of channel1 and channel2 if necessary.
     * making bit of channel1 less than or equal to channel2.
     * @param input The input we 'sort' in.
     */
    public void swap(Bit[] input) {
        Bit bit1 = input[nbChannel1 - 1];
        Bit bit2 = input[nbChannel2 - 1];
        if(bit1.getValue() > bit2.getValue()) {
            int tmp = bit1.getValue();
            bit1.setValue(bit2.getValue());
            bit2.setValue(tmp);
        }
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
