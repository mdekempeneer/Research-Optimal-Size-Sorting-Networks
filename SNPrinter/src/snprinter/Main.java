package snprinter;

import java.util.Arrays;

/**
 *
 * @author Admin
 */
public class Main {

    /**
     * NB_CHANNELS COMP1,COMP2,COMP3,COMP4,...
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        nbChannels = Short.parseShort(args[0]);
        /* Parse arg 1 */
        String[] temp = args[1].split(",");
        input1 = new short[temp.length];

        for (int i = 0; i < temp.length; i++) {
            input1[i] = Short.parseShort(temp[i]);
        }
        /* Parse arg 2 */
        if (args.length >= 3) {
            temp = args[2].split(",");
            input2 = new short[temp.length];

            for (int i = 0; i < temp.length; i++) {
                input2[i] = Short.parseShort(temp[i]);
            }
        }
        /* Action */
        if (args.length >= 3) {
            data1 = process(input1);
            System.out.println("next network");
            data2 = process(input2);
            printPermList(data1, data2);
        } else if (input1.length >= 1) {
            process(input1);
        }

    }

    private static short[] input1;
    private static short[] input2;
    private static short[][] data1;
    private static short[][] data2;
    private static short nbChannels;

    public static short[][] process(short[] input) {
        short[][] defaultNetwork = getOriginalInputs(0);

        short[][] data = defaultNetwork.clone();

        for (int i = 0; i < input.length; i++) {
            short comp = input[i];

            short[] temp = new short[i + 1];
            System.arraycopy(data[0], 0, temp, 0, data[0].length);
            temp[i] = input[i];

            data[0] = temp;
            processData(data, comp, 1);
            processW(data, comp, 1);

            printData(data);
        }
        return data;
    }

    /* --- Printing --- */
    public static void printData(short[][] data) {
        System.out.println("Data: ");
        for (int i = 1; i < data.length; i++) {
            System.out.println(printBinary(data[i]));
        }
        System.out.println("");
    }

    public static String printBinary(short[] data) {
        StringBuilder str = new StringBuilder();
        str.append("[");
        for (int i = 0; i < data.length; i++) {
            String number = String.format("%" + nbChannels +  "s", Integer.toBinaryString(data[i])).replace(' ', '0');
            //String = Integer.toBinaryString(data[i])
            str.append(number).append(",");
        }
        str.deleteCharAt(str.length() - 1);
        str.append("]");
        return str.toString();
    }

    /* --- Permutations --- */
    public static void printPermList(short[][] network1, short[][] network2) {
        //Create a list full of allOnes - on every Position can be every number.
        int allOnes = (1 << nbChannels) - 1;
        int[] posList = new int[nbChannels];
        System.arraycopy(Main.getAllOnesList(nbChannels), 0, posList, 0, nbChannels);

        //Compute positions.
        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            int L2 = network2[nbChannels][(nbOnes << 2) - 2]; //L for network 2.
            int P2 = network2[nbChannels][(nbOnes - 1) << 2]; //P for network 2.

            int revLPos = allOnes ^ network1[nbChannels][(nbOnes << 2) - 2]; //Positions of 0 for L.
            int revPPos = allOnes ^ network1[nbChannels][(nbOnes - 1) << 2]; //Positions of 1 for P.

            //TODO: Can we shorten result = revOnePos & revZeroPos by inline + logic?
            //and combine the two if's in the process.
            //int result = revLPos & revPPos;
            //Fill posList;
            if (L2 != allOnes || P2 != allOnes) {
                for (int i = 0; i < posList.length; i++) {
                    int mask = 1 << i;
                    if ((mask & L2) == 0) {
                        posList[i] &= revLPos;
                    }
                    if ((mask & P2) == 0) {
                        posList[i] &= revPPos;
                    }

                    //If possible positions are ever empty -> return false.
                    if (posList[i] == 0) {
                        //emptyPosCounter++;
                        return;
                    }
                }
            }
        }

        /* If at certain position only 1 number possible,
           claim the number and remove from other positions.
         */
        for (int i = 0; i < posList.length; i++) {
            int value = posList[i];
            if (Integer.bitCount(value) == 1) {
                for (int j = 0; j < posList.length; j++) {
                    if (((value & posList[j]) != 0) && (i != j)) {
                        posList[j] -= value;
                        if (posList[j] == 0) {
                            return;
                        }
                    }
                }
            }
        }

        /* Convert posList bit structure to bytes for permutations. */
        byte[][] Ps = new byte[nbChannels][];

        //TODO: Can this structure be improved?
        for (int i = 0; i < Ps.length; i++) {
            byte[] tempP = new byte[nbChannels]; //don't know initial capacity required.
            int countLengthPos = 0;
            int currP = posList[i];

            //Retrieve possible numbers from the bit form (currP).
            for (byte permIndex = 0; permIndex < nbChannels; permIndex++) {
                if ((1 << permIndex & currP) != 0) {// mask & posList[i] == 1 op die positie.
                    tempP[countLengthPos++] = permIndex;
                }
            }

            Ps[i] = new byte[countLengthPos]; //trim down to appropriate sizes.
            System.arraycopy(tempP, 0, Ps[i], 0, countLengthPos);
        }

        //Print all permutations.
        for (int j = 0; j < Ps.length; j++) {
            System.out.println("On place " + j + ": " + Arrays.toString(Ps[j]));
        }
    }

    /**
     * Create an array of nbChannels elements equal to allOnes. allOnes = (1
     * &lt&lt nbChannels) - 1
     *
     * @param nbChannels The amount of the elements and the amount of 1's.
     * @return The created array.
     */
    private static int[] getAllOnesList(short nbChannels) {
        int allOnes = (1 << nbChannels) - 1;
        int[] list = new int[nbChannels];

        for (int i = 0; i < list.length; i++) {
            list[i] = allOnes;
        }
        return list;
    }

    /* --- Generation --- */
    /**
     * Get all original inputs excluding the already sorted ones.
     *
     * @param upperBound unused
     * @return range from 2 to (2^nbChannels-1) excluding all sorted (binary)
     * ones.
     */
    public static short[][] getOriginalInputs(int upperBound) {
        /* 
         data[0] holds the lengths of the other shorts.
         data[1] holds outputs with 1 '1's.
         data[2] holds outputs with 2 '1's.
         ...
         data[n] nbChannels holds W(C,x,k) info.
         */
        short[][] data = new short[nbChannels + 1][];
        data[0] = new short[1];
        data[nbChannels] = new short[(nbChannels - 1) << 2];
        int wIndexCounter;

        for (int nbOnes = 1; nbOnes < nbChannels; nbOnes++) {
            data[nbOnes] = getPermutations((short) ((1 << nbOnes) - 1), nbChannels);
            wIndexCounter = (nbOnes - 1) << 2;

            data[nbChannels][wIndexCounter] = (short) ((1 << nbChannels) - 1);
            data[nbChannels][wIndexCounter + 1] = nbChannels;
            data[nbChannels][wIndexCounter + 2] = (short) ((1 << nbChannels) - 1);
            data[nbChannels][wIndexCounter + 3] = nbChannels;
        }

        return data;
    }

    /**
     * Get all permutations possible using the start and the max amount of bits.
     *
     * @param start The start value. Normally a value with all 1's on the right
     * side.
     * @param maxBits The maximum amount of bits available. (nbChannels)
     * @return All permutations starting with start that are smaller than
     * (2^maxBits)-1
     */
    public static short[] getPermutations(short start, short maxBits) {
        //Calculate length
        int beginNbOnes = Integer.bitCount(start);
        float temp = factorial(maxBits) / factorial(beginNbOnes); //TODO: Could get more efficient
        temp /= factorial(maxBits - beginNbOnes);
        int length = (int) Math.ceil(temp);

        //Variables
        short[] result = new short[length];
        int value = start;
        int max = (1 << maxBits) - 1;
        int t;
        int index = 0;

        //Get all permutations.
        do {
            result[index] = (short) value;
            //System.out.println(Integer.toBinaryString(value)); //-- DEBUG!
            t = value | (value - 1);
            value = (t + 1) | (((~t & -~t) - 1) >> (Integer.numberOfTrailingZeros(value) + 1));
            index++;
        } while (value < max);

        return result;
    }

    /**
     * Calculates the factorial of a given number.
     *
     * @param n The number of which the factorial has to be calculated
     * @return n!
     */
    public static long factorial(int n) {
        long result = 1;

        for (; n > 1; n--) {
            result *= n;
        }

        return result;
    }

    /**
     * TODO
     *
     * @param data
     * @param comp
     * @param startIndex
     */
    public static void processW(short[][] data, short comp, int startIndex) {
        short[] wResult = new short[data[nbChannels].length];

        int wIndexCounter;
        boolean foundL;
        boolean foundP;

        if (startIndex != 1) {
            System.arraycopy(data[nbChannels], 0, wResult, 0, (startIndex - 1) << 2);
        }

        for (int nbOnes = startIndex; nbOnes < nbChannels; nbOnes++) {
            wIndexCounter = (nbOnes - 1) << 2;

            int oldP = data[nbChannels][wIndexCounter];
            int oldL = data[nbChannels][wIndexCounter + 2];

            int P = (comp ^ ((1 << nbChannels) - 1)) & oldP;
            int L = (comp ^ ((1 << nbChannels) - 1)) & oldL;

            foundP = (oldP == P);
            foundL = (oldL == L);

            for (short output : data[nbOnes]) {
                if (!foundL) {
                    L = L | (output & comp);
                    if ((L & comp) == comp) {
                        foundL = true;
                    }
                }

                if (!foundP) {
                    P = P | ((output ^ ((1 << nbChannels) - 1)) & comp);
                    if ((P & comp) == comp) {
                        foundP = true;
                    }
                }

                /* Break; found both */
                if (foundP && foundL) {
                    break;
                }
            }
            wResult[wIndexCounter] = (short) P;
            wResult[wIndexCounter + 1] = (short) Integer.bitCount(P);
            wResult[wIndexCounter + 2] = (short) L;
            wResult[wIndexCounter + 3] = (short) Integer.bitCount(L);
        }
        data[nbChannels] = wResult;
    }

    /**
     * Processes the data for the new comparator. Adding the comparator to the
     * data[0] is assumed to be done already.
     *
     * @param data The network
     * @param newComp The comparator to process the data on.
     * @param startIndex The outerIndex of where to start. (= 1 will cover
     * everything.)
     */
    public static void processData(short[][] data, short newComp, int startIndex) {
        short[] processed;
        boolean found;

        for (int nbOnes = startIndex; nbOnes < nbChannels; nbOnes++) {
            processed = new short[data[nbOnes].length];
            int counter = 0;
            boolean foundNew = false;

            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                short oldValue = data[nbOnes][innerIndex];
                short value = swapCompare(oldValue, newComp);
                if (value != oldValue) {
                    foundNew = true;
                }
                found = false;
                for (int i = counter - 1; i >= 0; i--) {
                    if (processed[i] == value) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    processed[counter++] = value;
                }
            }

            if (foundNew) { //CAUTION! Don't do this 'shared array' with writing lists to disk.
                short[] result = new short[counter];
                System.arraycopy(processed, 0, result, 0, result.length);
                data[nbOnes] = result;
            }
        }

    }

    /**
     * Get the output of the comparator comp given the input.
     *
     * @param input The input to give the comparator.
     * @param comp The comparator to get the output from.
     * @return The result by switching the bits in the input according to comp.
     */
    private static short swapCompare(short input, short comp) {
        int pos1 = 31 - Integer.numberOfLeadingZeros(comp);
        int pos2 = Integer.numberOfTrailingZeros(comp);
        //(input >> pos1) & 1 = first (front bit)
        //(input >> pos2) & 1 = 2nd (back bit)
        return (((input >> pos1) & 1) <= ((input >> pos2) & 1)) ? input : (short) (input ^ comp);// TADAM!!!
    }

}
