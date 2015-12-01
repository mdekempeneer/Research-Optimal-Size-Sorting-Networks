package sortingnetworkspaper;

/**
 * Implementation of
 * http://en.wikipedia.org/wiki/Permutation#Systematic_generation_of_all_permutations
 * Algorithm to effeciently generate permutations of a sequence until all
 * possiblities are exhausted
 *
 * @author Admin
 */
public class Permute {

    /**
     *
     * Get The network permuted with the next permutation.
     *
     * @param permutor The array of shorts used to permute. (elements of {0, ..,
     * nbChannels-1})
     * @param data The network to permute.
     *
     * @return The permuted network data.
     */
    public static short[][] getPermutedData(byte[] permutor, short[][] data) {
        short[][] permData = data.clone();

        for (int nbOnes = 1; nbOnes < data.length; nbOnes++) {
            permData[nbOnes] = new short[data[nbOnes].length];

            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                int output = 0;

                /* Compute permuted */
                for (byte permIndex : permutor) {
                    output <<= 1;
                    output |= ((data[nbOnes][innerIndex] >> permIndex) & 1);
                }

                permData[nbOnes][innerIndex] = (short) output;
            }
        }

        return permData;
    }

    /**
     * Can't be instantiated.
     */
    private Permute() {
    }

    /**
     * Get the next permutation given the current permutation. The current
     * permutation will be altered in-place, clone beforehand if required.
     * Returns null when there is no next permutation.
     *
     * @param currPerm The current permutation
     * @return null if there is no next permutation, the next permutation
     * otherwise.
     */
    public static byte[] getNextPermutation(byte[] currPerm) {
        int i, j, l;

        //get maximum index j for which arr[j+1] > arr[j]
        for (j = currPerm.length - 2; j >= 0; j--) {
            if (currPerm[j + 1] > currPerm[j]) {
                break;
            }
        }

        //has reached it's lexicographic maximum value, No more permutations left 
        if (j == -1) {
            return null;
        }

        //get maximum index l for which arr[l] > arr[j]
        for (l = currPerm.length - 1; l > j; l--) {
            if (currPerm[l] > currPerm[j]) {
                break;
            }
        }

        //Swap arr[i],arr[j]
        byte swap = currPerm[j];
        currPerm[j] = currPerm[l];
        currPerm[l] = swap;

        //reverse array present after index : j+1 
        for (i = j + 1; i < currPerm.length; i++) {
            if (i > currPerm.length - i + j) {
                break;
            }
            swap = currPerm[i];
            currPerm[i] = currPerm[currPerm.length - i + j];
            currPerm[currPerm.length - i + j] = swap;
        }

        return currPerm;
    }

    //TODO: Schrijf Tester voor Permute
    /*    public static void main(String[] args) {
     short[] test_arr = {0, 1, 2, 3, 4, 5, 6, 7, 8};

     long begin = System.nanoTime();
     Permute test = new Permute(test_arr);
     while (true) {
     //System.out.println(test.get_next().toString());
     if (!test.next_permutation()) {
     break;
     }
     }
     System.out.println(System.nanoTime() - begin);
     System.out.println(test_arr);
     }
     */
}
