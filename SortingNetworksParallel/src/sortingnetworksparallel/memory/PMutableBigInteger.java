package sortingnetworksparallel.memory;

/**
 * A class used to represent multiprecision integers that makes efficient use of
 * allocated space by allowing a number to occupy only part of an array so that
 * the arrays do not have to be reallocated as often. When performing an
 * operation with many iterations the array used to hold a number is only
 * reallocated when necessary and does not have to be the same size as the
 * number it represents. A mutable number allows calculations to occur on the
 * same number without having to create a new number for every step of the
 * calculation as occurs with BigIntegers.
 *
 * @see BigInteger
 * @author Michael McCloskey
 * @author Timothy Buktu
 * @since 1.3
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PMutableBigInteger {

    /**
     * This mask is used to obtain the value of an int as if it were unsigned.
     */
    public final static long LONG_MASK = 0xffffffffL;

    /**
     * Holds the magnitude of this MutableBigInteger in big endian order. The
     * magnitude may start at an offset into the value array, and it may end
     * before the length of the value array.
     */
    public int[] value;

    /**
     * The number of ints of the value array that are currently used to hold the
     * magnitude of this MutableBigInteger. The magnitude starts at an offset
     * and offset + intLen may be less than value.length.
     */
   public  int intLen;

    /**
     * The offset into the value array where the magnitude of this
     * MutableBigInteger begins.
     */
    public  int offset = 0;

    // Constants
    /**
     * MutableBigInteger with one element value array with the value 1. Used by
     * BigDecimal divideAndRound to increment the quotient. Use this constant
     * only when the method is not going to modify this object.
     */
    public static final PMutableBigInteger ONE = new PMutableBigInteger(1);

    /**
     * The minimum {@code intLen} for cancelling powers of two before dividing.
     * If the number of ints is less than this threshold, {@code divideKnuth}
     * does not eliminate common powers of two from the dividend and divisor.
     */
    public static final int KNUTH_POW2_THRESH_LEN = 6;

    /**
     * The minimum number of trailing zero ints for cancelling powers of two
     * before dividing. If the dividend and divisor don't share at least this
     * many zero ints at the end, {@code divideKnuth} does not eliminate common
     * powers of two from the dividend and divisor.
     */
    public static final int KNUTH_POW2_THRESH_ZEROS = 3;

    // Constructors
    /**
     * The default constructor. An empty MutableBigInteger is created with a one
     * word capacity.
     */
    public PMutableBigInteger() {
        value = new int[1];
        intLen = 0;
    }

    /**
     * Construct a new MutableBigInteger with a magnitude specified by the int
     * val.
     */
    public PMutableBigInteger(int val) {
        value = new int[1];
        intLen = 1;
        value[0] = val;
    }

    /**
     * Construct a new MutableBigInteger with the specified value array up to
     * the length of the array supplied.
     */
    public PMutableBigInteger(int[] val) {
        value = val;
        intLen = val.length;
    }

    /**
     * Construct a new MutableBigInteger with a magnitude equal to the specified
     * BigInteger.
     */
    /*PMutableBigInteger(BigInteger b) {
        intLen = b.mag.length;
        value = Arrays.copyOf(b.mag, intLen);
    }*/
    /**
     * Construct a new MutableBigInteger with a magnitude equal to the specified
     * MutableBigInteger.
     */
    public PMutableBigInteger(PMutableBigInteger val) {
        intLen = val.intLen;
        value = Arrays.copyOfRange(val.value, val.offset, val.offset + intLen);
    }

    /**
     * Makes this number an {@code n}-int number all of whose bits are ones.
     * Used by Burnikel-Ziegler division.
     *
     * @param n number of ints in the {@code value} array
     * @return a number equal to {@code ((1<<(32*n)))-1}
     */
    private void ones(int n) {
        if (n > value.length) {
            value = new int[n];
        }
        Arrays.fill(value, -1);
        offset = 0;
        intLen = n;
    }

    /**
     * Internal helper method to return the magnitude array. The caller is not
     * supposed to modify the returned array.
     */
    private int[] getMagnitudeArray() {
        if (offset > 0 || value.length != intLen) {
            return Arrays.copyOfRange(value, offset, offset + intLen);
        }
        return value;
    }

    /**
     * Convert this MutableBigInteger to a BigInteger object.
     */
    public BigInteger toBigInteger(int sign) {
        if (intLen == 0 || sign == 0) {
            return BigInteger.ZERO;
        }
        
        int[] mag = getMagnitudeArray();
        
        ByteBuffer newBb = ByteBuffer.allocate(mag.length*4);
        newBb.asIntBuffer().put(mag);
        byte[] byteArray = newBb.array();

        return new BigInteger(byteArray);
    }

    /**
     * Converts this number to a nonnegative {@code BigInteger}.
     */
    public BigInteger toBigInteger() {
        normalize();
        return toBigInteger(isZero() ? 0 : 1);
    }

    /**
     * Convert this MutableBigInteger to BigDecimal object with the specified
     * sign and scale.
     */
    /*BigDecimal toBigDecimal(int sign, int scale) {
        if (intLen == 0 || sign == 0)
            return BigDecimal.zeroValueOf(scale);
        int[] mag = getMagnitudeArray();
        int len = mag.length;
        int d = mag[0];
        // If this MutableBigInteger can't be fit into long, we need to
        // make a BigInteger object for the resultant BigDecimal object.
        if (len > 2 || (d < 0 && len == 2))
            return new BigDecimal(new BigInteger(mag, sign), INFLATED, scale, 0);
        long v = (len == 2) ?
            ((mag[1] & LONG_MASK) | (d & LONG_MASK) << 32) :
            d & LONG_MASK;
        return BigDecimal.valueOf(sign == -1 ? -v : v, scale);
    }*/
    /**
     * This is for internal use in converting from a MutableBigInteger object
     * into a long value given a specified sign. returns INFLATED if value is
     * not fit into long
     */
    /*long toCompactValue(int sign) {
        if (intLen == 0 || sign == 0)
            return 0L;
        int[] mag = getMagnitudeArray();
        int len = mag.length;
        int d = mag[0];
        // If this MutableBigInteger can not be fitted into long, we need to
        // make a BigInteger object for the resultant BigDecimal object.
        if (len > 2 || (d < 0 && len == 2))
            return INFLATED;
        long v = (len == 2) ?
            ((mag[1] & LONG_MASK) | (d & LONG_MASK) << 32) :
            d & LONG_MASK;
        return sign == -1 ? -v : v;
    }*/
    /**
     * Clear out a MutableBigInteger for reuse.
     */
    public void clear() {
        offset = intLen = 0;
        for (int index = 0, n = value.length; index < n; index++) {
            value[index] = 0;
        }
    }

    /**
     * Set a MutableBigInteger to zero, removing its offset.
     */
    public void reset() {
        offset = intLen = 0;
    }

    /**
     * Compare the magnitude of two MutableBigIntegers. Returns -1, 0 or 1 as
     * this MutableBigInteger is numerically less than, equal to, or greater
     * than <tt>b</tt>.
     */
    public final int compare(PMutableBigInteger b) {
        int blen = b.intLen;
        if (intLen < blen) {
            return -1;
        }
        if (intLen > blen) {
            return 1;
        }

        // Add Integer.MIN_VALUE to make the comparison act as unsigned integer
        // comparison.
        int[] bval = b.value;
        for (int i = offset, j = b.offset; i < intLen + offset; i++, j++) {
            int b1 = value[i] + 0x80000000;
            int b2 = bval[j] + 0x80000000;
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Returns a value equal to what
     * {@code b.leftShift(32*ints); return compare(b);} would return, but
     * doesn't change the value of {@code b}.
     */
    private int compareShifted(PMutableBigInteger b, int ints) {
        int blen = b.intLen;
        int alen = intLen - ints;
        if (alen < blen) {
            return -1;
        }
        if (alen > blen) {
            return 1;
        }

        // Add Integer.MIN_VALUE to make the comparison act as unsigned integer
        // comparison.
        int[] bval = b.value;
        for (int i = offset, j = b.offset; i < alen + offset; i++, j++) {
            int b1 = value[i] + 0x80000000;
            int b2 = bval[j] + 0x80000000;
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Compare this against half of a MutableBigInteger object (Needed for
     * remainder tests). Assumes no leading unnecessary zeros, which holds for
     * results from divide().
     */
    public final int compareHalf(PMutableBigInteger b) {
        int blen = b.intLen;
        int len = intLen;
        if (len <= 0) {
            return blen <= 0 ? 0 : -1;
        }
        if (len > blen) {
            return 1;
        }
        if (len < blen - 1) {
            return -1;
        }
        int[] bval = b.value;
        int bstart = 0;
        int carry = 0;
        // Only 2 cases left:len == blen or len == blen - 1
        if (len != blen) { // len == blen - 1
            if (bval[bstart] == 1) {
                ++bstart;
                carry = 0x80000000;
            } else {
                return -1;
            }
        }
        // compare values with right-shifted values of b,
        // carrying shifted-out bits across words
        int[] val = value;
        for (int i = offset, j = bstart; i < len + offset;) {
            int bv = bval[j++];
            long hb = ((bv >>> 1) + carry) & LONG_MASK;
            long v = val[i++] & LONG_MASK;
            if (v != hb) {
                return v < hb ? -1 : 1;
            }
            carry = (bv & 1) << 31; // carray will be either 0x80000000 or 0
        }
        return carry == 0 ? 0 : -1;
    }

    /**
     * Return the index of the lowest set bit in this MutableBigInteger. If the
     * magnitude of this MutableBigInteger is zero, -1 is returned.
     */
    private final int getLowestSetBit() {
        if (intLen == 0) {
            return -1;
        }
        int j, b;
        for (j = intLen - 1; (j > 0) && (value[j + offset] == 0); j--)
            ;
        b = value[j + offset];
        if (b == 0) {
            return -1;
        }
        return ((intLen - 1 - j) << 5) + Integer.numberOfTrailingZeros(b);
    }

    /**
     * Return the int in use in this MutableBigInteger at the specified index.
     * This method is not used because it is not inlined on all platforms.
     */
    private final int getInt(int index) {
        return value[offset + index];
    }

    /**
     * Ensure that the MutableBigInteger is in normal form, specifically making
     * sure that there are no leading zeros, and that if the magnitude is zero,
     * then intLen is zero.
     */
    public final void normalize() {
        if (intLen == 0) {
            offset = 0;
            return;
        }

        int index = offset;
        if (value[index] != 0) {
            return;
        }

        int indexBound = index + intLen;
        do {
            index++;
        } while (index < indexBound && value[index] == 0);

        int numZeros = index - offset;
        intLen -= numZeros;
        offset = (intLen == 0 ? 0 : offset + numZeros);
    }

    /**
     * If this MutableBigInteger cannot hold len words, increase the size of the
     * value array to len words.
     */
    private final void ensureCapacity(int len) {
        if (value.length < len) {
            value = new int[len];
            offset = 0;
            intLen = len;
        }
    }

    /**
     * Convert this MutableBigInteger into an int array with no leading zeros,
     * of a length that is equal to this MutableBigInteger's intLen.
     */
    public int[] toIntArray() {
        int[] result = new int[intLen];
        for (int i = 0; i < intLen; i++) {
            result[i] = value[offset + i];
        }
        return result;
    }

    /**
     * Sets the int at index+offset in this MutableBigInteger to val. This does
     * not get inlined on all platforms so it is not used as often as originally
     * intended.
     */
    public void setInt(int index, int val) {
        value[offset + index] = val;
    }

    /**
     * Sets this MutableBigInteger's value array to the specified array. The
     * intLen is set to the specified length.
     */
    public void setValue(int[] val, int length) {
        value = val;
        intLen = length;
        offset = 0;
    }

    /**
     * Sets this MutableBigInteger's value array to a copy of the specified
     * array. The intLen is set to the length of the new array.
     */
    public void copyValue(PMutableBigInteger src) {
        int len = src.intLen;
        if (value.length < len) {
            value = new int[len];
        }
        System.arraycopy(src.value, src.offset, value, 0, len);
        intLen = len;
        offset = 0;
    }

    /**
     * Sets this PMutableBigInteger's value array to a copy of the specified
     * array. The intLen is set to the length of the specified array.
     */
    public void copyValue(int[] val) {
        int len = val.length;
        if (value.length < len) {
            value = new int[len];
        }
        System.arraycopy(val, 0, value, 0, len);
        intLen = len;
        offset = 0;
    }

    /**
     * Returns true iff this PMutableBigInteger has a value of one.
     */
    public boolean isOne() {
        return (intLen == 1) && (value[offset] == 1);
    }

    /**
     * Returns true iff this PMutableBigInteger has a value of zero.
     */
    public boolean isZero() {
        return (intLen == 0);
    }

    /**
     * Returns true iff this PMutableBigInteger is even.
     */
    public boolean isEven() {
        return (intLen == 0) || ((value[offset + intLen - 1] & 1) == 0);
    }

    /**
     * Returns true iff this PMutableBigInteger is odd.
     */
    public boolean isOdd() {
        return isZero() ? false : ((value[offset + intLen - 1] & 1) == 1);
    }

    /**
     * Returns true iff this PMutableBigInteger is in normal form. A
     * PMutableBigInteger is in normal form if it has no leading zeros after the
     * offset, and intLen + offset <= value.length.
     */
    public boolean isNormal() {
        if (intLen + offset > value.length) {
            return false;
        }
        if (intLen == 0) {
            return true;
        }
        return (value[offset] != 0);
    }

    /**
     * Returns a String representation of this PMutableBigInteger in radix 10.
     */
    public String toString() {
        BigInteger b = toBigInteger(1);
        return b.toString();
    }

    /**
     * Right shift this PMutableBigInteger n bits, where n is less than 32.
     * Assumes that intLen > 0, n > 0 for speed
     */
    private final void primitiveRightShift(int n) {
        int[] val = value;
        int n2 = 32 - n;
        for (int i = offset + intLen - 1, c = val[i]; i > offset; i--) {
            int b = c;
            c = val[i - 1];
            val[i] = (c << n2) | (b >>> n);
        }
        val[offset] >>>= n;
    }

    /**
     * Left shift this PMutableBigInteger n bits, where n is less than 32.
     * Assumes that intLen > 0, n > 0 for speed
     */
    private final void primitiveLeftShift(int n) {
        int[] val = value;
        int n2 = 32 - n;
        for (int i = offset, c = val[i], m = i + intLen - 1; i < m; i++) {
            int b = c;
            c = val[i + 1];
            val[i] = (b << n) | (c >>> n2);
        }
        val[offset + intLen - 1] <<= n;
    }

    /**
     * Discards all ints whose index is greater than {@code n}.
     */
    private void keepLower(int n) {
        if (intLen >= n) {
            offset += intLen - n;
            intLen = n;
        }
    }

    /**
     * Adds the contents of two PMutableBigInteger objects.The result is placed
     * within this PMutableBigInteger. The contents of the addend are not
     * changed.
     */
    public void add(PMutableBigInteger addend) {
        int x = intLen;
        int y = addend.intLen;
        int resultLen = (intLen > addend.intLen ? intLen : addend.intLen);
        int[] result = (value.length < resultLen ? new int[resultLen] : value);

        int rstart = result.length - 1;
        long sum;
        long carry = 0;

        // Add common parts of both numbers
        while (x > 0 && y > 0) {
            x--;
            y--;
            sum = (value[x + offset] & LONG_MASK)
                    + (addend.value[y + addend.offset] & LONG_MASK) + carry;
            result[rstart--] = (int) sum;
            carry = sum >>> 32;
        }

        // Add remainder of the longer number
        while (x > 0) {
            x--;
            if (carry == 0 && result == value && rstart == (x + offset)) {
                return;
            }
            sum = (value[x + offset] & LONG_MASK) + carry;
            result[rstart--] = (int) sum;
            carry = sum >>> 32;
        }
        while (y > 0) {
            y--;
            sum = (addend.value[y + addend.offset] & LONG_MASK) + carry;
            result[rstart--] = (int) sum;
            carry = sum >>> 32;
        }

        if (carry > 0) { // Result must grow in length
            resultLen++;
            if (result.length < resultLen) {
                int temp[] = new int[resultLen];
                // Result one word longer from carry-out; copy low-order
                // bits into new result.
                System.arraycopy(result, 0, temp, 1, result.length);
                temp[0] = 1;
                result = temp;
            } else {
                result[rstart--] = 1;
            }
        }

        value = result;
        intLen = resultLen;
        offset = result.length - resultLen;
    }

    /**
     * Adds the value of {@code addend} shifted {@code n} ints to the left. Has
     * the same effect as {@code addend.leftShift(32*ints); add(addend);} but
     * doesn't change the value of {@code addend}.
     */
    public void addShifted(PMutableBigInteger addend, int n) {
        if (addend.isZero()) {
            return;
        }

        int x = intLen;
        int y = addend.intLen + n;
        int resultLen = (intLen > y ? intLen : y);
        int[] result = (value.length < resultLen ? new int[resultLen] : value);

        int rstart = result.length - 1;
        long sum;
        long carry = 0;

        // Add common parts of both numbers
        while (x > 0 && y > 0) {
            x--;
            y--;
            int bval = y + addend.offset < addend.value.length ? addend.value[y + addend.offset] : 0;
            sum = (value[x + offset] & LONG_MASK)
                    + (bval & LONG_MASK) + carry;
            result[rstart--] = (int) sum;
            carry = sum >>> 32;
        }

        // Add remainder of the longer number
        while (x > 0) {
            x--;
            if (carry == 0 && result == value && rstart == (x + offset)) {
                return;
            }
            sum = (value[x + offset] & LONG_MASK) + carry;
            result[rstart--] = (int) sum;
            carry = sum >>> 32;
        }
        while (y > 0) {
            y--;
            int bval = y + addend.offset < addend.value.length ? addend.value[y + addend.offset] : 0;
            sum = (bval & LONG_MASK) + carry;
            result[rstart--] = (int) sum;
            carry = sum >>> 32;
        }

        if (carry > 0) { // Result must grow in length
            resultLen++;
            if (result.length < resultLen) {
                int temp[] = new int[resultLen];
                // Result one word longer from carry-out; copy low-order
                // bits into new result.
                System.arraycopy(result, 0, temp, 1, result.length);
                temp[0] = 1;
                result = temp;
            } else {
                result[rstart--] = 1;
            }
        }

        value = result;
        intLen = resultLen;
        offset = result.length - resultLen;
    }

    /**
     * Like {@link #addShifted(PMutableBigInteger, int)} but {@code this.intLen}
     * must not be greater than {@code n}. In other words, concatenates
     * {@code this} and {@code addend}.
     */
    void addDisjoint(PMutableBigInteger addend, int n) {
        if (addend.isZero()) {
            return;
        }

        int x = intLen;
        int y = addend.intLen + n;
        int resultLen = (intLen > y ? intLen : y);
        int[] result;
        if (value.length < resultLen) {
            result = new int[resultLen];
        } else {
            result = value;
            Arrays.fill(value, offset + intLen, value.length, 0);
        }

        int rstart = result.length - 1;

        // copy from this if needed
        System.arraycopy(value, offset, result, rstart + 1 - x, x);
        y -= x;
        rstart -= x;

        int len = Math.min(y, addend.value.length - addend.offset);
        System.arraycopy(addend.value, addend.offset, result, rstart + 1 - y, len);

        // zero the gap
        for (int i = rstart + 1 - y + len; i < rstart + 1; i++) {
            result[i] = 0;
        }

        value = result;
        intLen = resultLen;
        offset = result.length - resultLen;
    }

    /**
     * Adds the low {@code n} ints of {@code addend}.
     */
    void addLower(PMutableBigInteger addend, int n) {
        PMutableBigInteger a = new PMutableBigInteger(addend);
        if (a.offset + a.intLen >= n) {
            a.offset = a.offset + a.intLen - n;
            a.intLen = n;
        }
        a.normalize();
        add(a);
    }

    /**
     * Subtracts the smaller of this and b from the larger and places the result
     * into this PMutableBigInteger.
     */
    int subtract(PMutableBigInteger b) {
        PMutableBigInteger a = this;

        int[] result = value;
        int sign = a.compare(b);

        if (sign == 0) {
            reset();
            return 0;
        }
        if (sign < 0) {
            PMutableBigInteger tmp = a;
            a = b;
            b = tmp;
        }

        int resultLen = a.intLen;
        if (result.length < resultLen) {
            result = new int[resultLen];
        }

        long diff = 0;
        int x = a.intLen;
        int y = b.intLen;
        int rstart = result.length - 1;

        // Subtract common parts of both numbers
        while (y > 0) {
            x--;
            y--;

            diff = (a.value[x + a.offset] & LONG_MASK)
                    - (b.value[y + b.offset] & LONG_MASK) - ((int) -(diff >> 32));
            result[rstart--] = (int) diff;
        }
        // Subtract remainder of longer number
        while (x > 0) {
            x--;
            diff = (a.value[x + a.offset] & LONG_MASK) - ((int) -(diff >> 32));
            result[rstart--] = (int) diff;
        }

        value = result;
        intLen = resultLen;
        offset = value.length - resultLen;
        normalize();
        return sign;
    }

    /**
     * Subtracts the smaller of a and b from the larger and places the result
     * into the larger. Returns 1 if the answer is in a, -1 if in b, 0 if no
     * operation was performed.
     */
    private int difference(PMutableBigInteger b) {
        PMutableBigInteger a = this;
        int sign = a.compare(b);
        if (sign == 0) {
            return 0;
        }
        if (sign < 0) {
            PMutableBigInteger tmp = a;
            a = b;
            b = tmp;
        }

        long diff = 0;
        int x = a.intLen;
        int y = b.intLen;

        // Subtract common parts of both numbers
        while (y > 0) {
            x--;
            y--;
            diff = (a.value[a.offset + x] & LONG_MASK)
                    - (b.value[b.offset + y] & LONG_MASK) - ((int) -(diff >> 32));
            a.value[a.offset + x] = (int) diff;
        }
        // Subtract remainder of longer number
        while (x > 0) {
            x--;
            diff = (a.value[a.offset + x] & LONG_MASK) - ((int) -(diff >> 32));
            a.value[a.offset + x] = (int) diff;
        }

        a.normalize();
        return sign;
    }

    /**
     * Multiply the contents of two PMutableBigInteger objects. The result is
     * placed into PMutableBigInteger z. The contents of y are not changed.
     */
    void multiply(PMutableBigInteger y, PMutableBigInteger z) {
        int xLen = intLen;
        int yLen = y.intLen;
        int newLen = xLen + yLen;

        // Put z into an appropriate state to receive product
        if (z.value.length < newLen) {
            z.value = new int[newLen];
        }
        z.offset = 0;
        z.intLen = newLen;

        // The first iteration is hoisted out of the loop to avoid extra add
        long carry = 0;
        for (int j = yLen - 1, k = yLen + xLen - 1; j >= 0; j--, k--) {
            long product = (y.value[j + y.offset] & LONG_MASK)
                    * (value[xLen - 1 + offset] & LONG_MASK) + carry;
            z.value[k] = (int) product;
            carry = product >>> 32;
        }
        z.value[xLen - 1] = (int) carry;

        // Perform the multiplication word by word
        for (int i = xLen - 2; i >= 0; i--) {
            carry = 0;
            for (int j = yLen - 1, k = yLen + i; j >= 0; j--, k--) {
                long product = (y.value[j + y.offset] & LONG_MASK)
                        * (value[i + offset] & LONG_MASK)
                        + (z.value[k] & LONG_MASK) + carry;
                z.value[k] = (int) product;
                carry = product >>> 32;
            }
            z.value[i] = (int) carry;
        }

        // Remove leading zeros from product
        z.normalize();
    }

    /**
     * Multiply the contents of this PMutableBigInteger by the word y. The
     * result is placed into z.
     */
    void mul(int y, PMutableBigInteger z) {
        if (y == 1) {
            z.copyValue(this);
            return;
        }

        if (y == 0) {
            z.clear();
            return;
        }

        // Perform the multiplication word by word
        long ylong = y & LONG_MASK;
        int[] zval = (z.value.length < intLen + 1 ? new int[intLen + 1]
                : z.value);
        long carry = 0;
        for (int i = intLen - 1; i >= 0; i--) {
            long product = ylong * (value[i + offset] & LONG_MASK) + carry;
            zval[i + 1] = (int) product;
            carry = product >>> 32;
        }

        if (carry == 0) {
            z.offset = 1;
            z.intLen = intLen;
        } else {
            z.offset = 0;
            z.intLen = intLen + 1;
            zval[0] = (int) carry;
        }
        z.value = zval;
    }

    /**
     * This method is used for division of an n word dividend by a one word
     * divisor. The quotient is placed into quotient. The one word divisor is
     * specified by divisor.
     *
     * @return the remainder of the division is returned.
     *
     */
    int divideOneWord(int divisor, PMutableBigInteger quotient) {
        long divisorLong = divisor & LONG_MASK;

        // Special case of one word dividend
        if (intLen == 1) {
            long dividendValue = value[offset] & LONG_MASK;
            int q = (int) (dividendValue / divisorLong);
            int r = (int) (dividendValue - q * divisorLong);
            quotient.value[0] = q;
            quotient.intLen = (q == 0) ? 0 : 1;
            quotient.offset = 0;
            return r;
        }

        if (quotient.value.length < intLen) {
            quotient.value = new int[intLen];
        }
        quotient.offset = 0;
        quotient.intLen = intLen;

        // Normalize the divisor
        int shift = Integer.numberOfLeadingZeros(divisor);

        int rem = value[offset];
        long remLong = rem & LONG_MASK;
        if (remLong < divisorLong) {
            quotient.value[0] = 0;
        } else {
            quotient.value[0] = (int) (remLong / divisorLong);
            rem = (int) (remLong - (quotient.value[0] * divisorLong));
            remLong = rem & LONG_MASK;
        }
        int xlen = intLen;
        while (--xlen > 0) {
            long dividendEstimate = (remLong << 32)
                    | (value[offset + intLen - xlen] & LONG_MASK);
            int q;
            if (dividendEstimate >= 0) {
                q = (int) (dividendEstimate / divisorLong);
                rem = (int) (dividendEstimate - q * divisorLong);
            } else {
                long tmp = divWord(dividendEstimate, divisor);
                q = (int) (tmp & LONG_MASK);
                rem = (int) (tmp >>> 32);
            }
            quotient.value[intLen - xlen] = q;
            remLong = rem & LONG_MASK;
        }

        quotient.normalize();
        // Unnormalize
        if (shift > 0) {
            return rem % divisor;
        } else {
            return rem;
        }
    }

    /**
     * Returns a {@code PMutableBigInteger} containing {@code blockLength} ints
     * from {@code this} number, starting at {@code index*blockLength}.<br/>
     * Used by Burnikel-Ziegler division.
     *
     * @param index the block index
     * @param numBlocks the total number of blocks in {@code this} number
     * @param blockLength length of one block in units of 32 bits
     * @return
     */
    private PMutableBigInteger getBlock(int index, int numBlocks, int blockLength) {
        int blockStart = index * blockLength;
        if (blockStart >= intLen) {
            return new PMutableBigInteger();
        }

        int blockEnd;
        if (index == numBlocks - 1) {
            blockEnd = intLen;
        } else {
            blockEnd = (index + 1) * blockLength;
        }
        if (blockEnd > intLen) {
            return new PMutableBigInteger();
        }

        int[] newVal = Arrays.copyOfRange(value, offset + intLen - blockEnd, offset + intLen - blockStart);
        return new PMutableBigInteger(newVal);
    }

    /**
     * @see BigInteger#bitLength()
     */
    long bitLength() {
        if (intLen == 0) {
            return 0;
        }
        return intLen * 32L - Integer.numberOfLeadingZeros(value[offset]);
    }

    private static void copyAndShift(int[] src, int srcFrom, int srcLen, int[] dst, int dstFrom, int shift) {
        int n2 = 32 - shift;
        int c = src[srcFrom];
        for (int i = 0; i < srcLen - 1; i++) {
            int b = c;
            c = src[++srcFrom];
            dst[dstFrom + i] = (b << shift) | (c >>> n2);
        }
        dst[dstFrom + srcLen - 1] = c << shift;
    }

    /**
     * A primitive used for division by long. Specialized version of the method
     * divadd. dh is a high part of the divisor, dl is a low part
     */
    private int divaddLong(int dh, int dl, int[] result, int offset) {
        long carry = 0;

        long sum = (dl & LONG_MASK) + (result[1 + offset] & LONG_MASK);
        result[1 + offset] = (int) sum;

        sum = (dh & LONG_MASK) + (result[offset] & LONG_MASK) + carry;
        result[offset] = (int) sum;
        carry = sum >>> 32;
        return (int) carry;
    }

    /**
     * This method is used for division by long. Specialized version of the
     * method sulsub. dh is a high part of the divisor, dl is a low part
     */
    private int mulsubLong(int[] q, int dh, int dl, int x, int offset) {
        long xLong = x & LONG_MASK;
        offset += 2;
        long product = (dl & LONG_MASK) * xLong;
        long difference = q[offset] - product;
        q[offset--] = (int) difference;
        long carry = (product >>> 32)
                + (((difference & LONG_MASK)
                > (((~(int) product) & LONG_MASK))) ? 1 : 0);
        product = (dh & LONG_MASK) * xLong + carry;
        difference = q[offset] - product;
        q[offset--] = (int) difference;
        carry = (product >>> 32)
                + (((difference & LONG_MASK)
                > (((~(int) product) & LONG_MASK))) ? 1 : 0);
        return (int) carry;
    }

    /**
     * Compare two longs as if they were unsigned. Returns true iff one is
     * bigger than two.
     */
    private boolean unsignedLongCompare(long one, long two) {
        return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE);
    }

    /**
     * This method divides a long quantity by an int to estimate qhat for two
     * multi precision numbers. It is used when the signed value of n is less
     * than zero. Returns long value where high 32 bits contain remainder value
     * and low 32 bits contain quotient value.
     */
    static long divWord(long n, int d) {
        long dLong = d & LONG_MASK;
        long r;
        long q;
        if (dLong == 1) {
            q = (int) n;
            r = 0;
            return (r << 32) | (q & LONG_MASK);
        }

        // Approximate the quotient and remainder
        q = (n >>> 1) / (dLong >>> 1);
        r = n - q * dLong;

        // Correct the approximation
        while (r < 0) {
            r += dLong;
            q--;
        }
        while (r >= dLong) {
            r -= dLong;
            q++;
        }
        // n - q*dlong == r && 0 <= r <dLong, hence we're done.
        return (r << 32) | (q & LONG_MASK);
    }

    /**
     * Calculate GCD of a and b interpreted as unsigned integers.
     */
    static int binaryGcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        if (a == 0) {
            return b;
        }

        // Right shift a & b till their last bits equal to 1.
        int aZeros = Integer.numberOfTrailingZeros(a);
        int bZeros = Integer.numberOfTrailingZeros(b);
        a >>>= aZeros;
        b >>>= bZeros;

        int t = (aZeros < bZeros ? aZeros : bZeros);

        while (a != b) {
            if ((a + 0x80000000) > (b + 0x80000000)) {  // a > b as unsigned
                a -= b;
                a >>>= Integer.numberOfTrailingZeros(a);
            } else {
                b -= a;
                b >>>= Integer.numberOfTrailingZeros(b);
            }
        }
        return a << t;
    }

    /**
     * Returns the multiplicative inverse of val mod 2^32. Assumes val is odd.
     */
    static int inverseMod32(int val) {
        // Newton's iteration!
        int t = val;
        t *= 2 - val * t;
        t *= 2 - val * t;
        t *= 2 - val * t;
        t *= 2 - val * t;
        return t;
    }

}
