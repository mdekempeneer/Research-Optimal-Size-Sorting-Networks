package generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import networklib.Comparator;

/**
 *
 * Makes all possible combinations of all comparators G forming a Unique
 * parallel form. This is done by aborting generation when the current subset
 * should be swapped to create the unique parallel form.
 * {@link isSwapRequired(Comparator[], int, int)}.
 * 
 * Channels with the same comparator more than twice in a row are excluded as well.
 *
 * Define a Unique parallel form as the network that can be constructed out of a
 * network by Swapping the parallel comparators so that the comparator with the
 * lowest channel1 stays on the left of the "parallel partner comparator".
 *
 * @author Admin
 */
public class Generator2 {

    private final int nbChannels;
    private final int nbComp;
    private final String outputPath;
    private BufferedWriter bw = null;

    /**
     * Create a {@link Generator} which can write all possible {@link Network}
     * combinations with given amount of channels and comparators.
     * Excluding all 'doubles' and only providing "The unique parallel" forms.
     *
     * @param nbChannels The amount of channels of the {@link Network}.
     * @param nbComp The amount of {@link Comparator}s of the {@link Network}.
     * @param outputPath The path to the file to write the output to.
     */
    public Generator2(int nbChannels, int nbComp, String outputPath) {
        this.nbChannels = nbChannels;
        this.nbComp = nbComp;
        this.outputPath = outputPath;
    }

    /**
     * Generate all possible networks with the provided amount of channels and
     * comparators and write it to a file.
     *
     */
    public void generate() {
        Comparator[] comps = new Comparator[nbComp];

        long begin = System.nanoTime(); //DEBUG - TIMING
        //Iterate  over all comparator combinations
        for (int number1 = 1; number1 <= nbChannels - 1; number1++) {
            for (int number2 = number1 + 1; number2 <= nbChannels; number2++) {
                generate_sub(comps, new Comparator((short) number1, (short) number2), 0);
            }
        }
        long end = System.nanoTime(); //DEBUG - TIMING
        System.out.println("Generate took " + (end - begin) + " nanoseconds."); //DEBUG - TIMING
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Generator2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * The subroutine of {@link Generator#generate}
     *
     * @param list The current array of comparators.
     * @param comp The {@link Comparator} to add to the list.
     * @param current The current index where to add the comp to.
     */
    private void generate_sub(Comparator[] list, Comparator comp, int current) {
        int max = list.length;

        /* Abort doubles */
        if (current >= 1 && comp.isEqual(list[current-1])) {
            return;
        }

        /* If a swap is required, there exists a parallel "The unique" 
         equivalent so abort creation. */
        if (current >= 1 && isSwapRequired(list[current-1], comp)) {
            return;
        }

        if (current < max) {
            Comparator[] newList = cloneCompList(list);
            newList[current] = comp;

            //Continue adding
            if (current + 1 < max) {

                //Iterate over all comparator combinations
                //both fors combined make a foreach(comp : compCombinations)
                for (int i = 1; i <= nbChannels - 1; i++) {
                    for (int j = i + 1; j <= nbChannels; j++) {

                        generate_sub(newList, new Comparator((short) i, (short) j), current + 1);
                    }
                }
            } else {
                writeList(newList);
            }
        }
    }

    /**
     *
     * @param list
     */
    public void writeList(Comparator[] list) {
        StringBuilder sb = new StringBuilder();
        sb.append(nbChannels);
        sb.append(" ");
        sb.append(nbComp);
        sb.append(" ");
        for (Comparator comp : list) {
            sb.append(comp.toString());
        }

        try {
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(outputPath));
            }
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(Generator2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Deep-clone the given list.
     *
     * @param list The list to clone.
     * @return The deep-clone of list.
     */
    public static Comparator[] cloneCompList(Comparator[] list) {
        Comparator[] result = new Comparator[list.length];

        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {
                result[i] = (Comparator) list[i].clone();
            } else {
                return result;
            }
        }

        return result;
    }

    /**
     *
     * Check if the array of comparator's is unique and thus does not require a
     * swap to be unique.
     *
     * @param comp The array of {@link Comparator}s to check whether unique.
     * @param lastIndex The index of the last "item" that has an element. So
     * comp[lastIndex+n] (n > 0) will be considered null.
     * @return Whether or not the array of comparators is unique. (no parallel
     * swap required).
     */
    /*public boolean isUniqueParallelForm(Comparator[] comp, int lastIndex) {
     for (int i = 0; i < lastIndex - 1; i++) {
     if (swap(comp, i, i + 1)) {
     return false;
     }
     }
     return true;
     }*/
    
    /**
     * Check if a swap is required to get The unique parallel form. Swap is
     * required when both don't share a channel and the
     * comp1.getChannel1() &gt comp2.getChannel1().
     */
    private boolean isSwapRequired(Comparator comp1, Comparator comp2) {
        //Check sharing of comparator.
        if (comp1.getChannel1() == comp2.getChannel1()
                || comp1.getChannel1() == comp2.getChannel2()
                || comp1.getChannel2() == comp2.getChannel1()
                || comp1.getChannel2() == comp2.getChannel2()) {
            return false;
        }

        //Check if the first comparator channel1 < second comparator channel1.
        return comp1.getChannel1() > comp2.getChannel1();
    }
}
