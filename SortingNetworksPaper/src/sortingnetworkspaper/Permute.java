package sortingnetworkspaper;

public class Permute {

    /**
     * Implementation of
     * http://en.wikipedia.org/wiki/Permutation#Systematic_generation_of_all_permutations
     * Algorithm to effeciently generate permutations of a sequence until all
     * possiblities are exhausted *
     */
    private final short[] arrIdxs;
    private final short[] arr; //TODO change to byte/short
    private final short length;

    /**
     *
     * Get The network permuted with the next permutation.
     *
     * @param data The network to permute.
     * 
     * @return The permuted network data.
     */
    public short[] get_next(short[][] data) {
        short[] ret = new short[arr.length];
        for (int idx = 0; idx < arrIdxs.length; idx++) {
            ret[idx] = arr[arrIdxs[idx]]; //Permute integer based array indexes, which can be used to get permuted array in return
        }

        short[][] permData = data.clone();

        for (int nbOnes = 1; nbOnes < data.length; nbOnes++) {
            for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
                int output = 0;
                permData[nbOnes] = new short[data[nbOnes].length];

                /* Compute permuted */
                for (short permIndex : ret) {
                    output <<= 1;
                    output |= ((data[nbOnes][innerIndex] >> permIndex) & 1);
                }

                permData[nbOnes][innerIndex] = (short) output;
            }
        }

        return ret;
    }

    /**
     *
     * @param arr
     */
    public Permute(short[] arr) {
        this(arr, (short) arr.length);
    }

    /**
     *
     * @param arr The array [1,2,3...]
     * @param length The length of the array.
     */
    public Permute(short[] arr, short length) {
        this.arr = new short[arr.length];
        System.arraycopy(arr, 0, this.arr, 0, arr.length);
        arrIdxs = new short[arr.length];
        this.length = length;

        //Set indexes lexicographically minimal value;
        for (short i = 0; i < this.length; i++) {
            this.arrIdxs[i] = i;
        }
    }

    public boolean next_permutation() {
        int i, j, l;

        //get maximum index j for which arr[j+1] > arr[j]
        for (j = this.length - 2; j >= 0; j--) {
            if (arrIdxs[j + 1] > arrIdxs[j]) {
                break;
            }
        }

        //has reached it's lexicographic maximum value, No more permutations left 
        if (j == -1) {
            return false;
        }

        //get maximum index l for which arr[l] > arr[j]
        for (l = this.length - 1; l > j; l--) {
            if (arrIdxs[l] > arrIdxs[j]) {
                break;
            }
        }

        //Swap arr[i],arr[j]
        short swap = arrIdxs[j];
        arrIdxs[j] = arrIdxs[l];
        arrIdxs[l] = swap;

        //reverse array present after index : j+1 
        for (i = j + 1; i < arrIdxs.length; i++) {
            if (i > arrIdxs.length - i + j) {
                break;
            }
            swap = arrIdxs[i];
            arrIdxs[i] = arrIdxs[arrIdxs.length - i + j];
            arrIdxs[arrIdxs.length - i + j] = swap;
        }

        return true;
    }

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
