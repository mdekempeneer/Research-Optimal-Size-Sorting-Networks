package generator;

import networklib.Comparator;

/**
 *
 * @author Admin
 */
public class Generator {

    private final int nbChannels;
    private final int nbComp;
    private final String outputPath;

    /**
     * Create a {@link Generator} which can write all possible {@link Network}
     * combinations with given amount of channels and comparators.
     *
     * @param nbChannels The amount of channels of the {@link Network}.
     * @param nbComp The amount of {@link Comparator}s of the {@link Network}.
     * @param outputPath The path to the file to write the output to.
     */
    public Generator(int nbChannels, int nbComp, String outputPath) {
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

        //Iterate  over all comparator combinations
        for (int number1 = 1; number1 <= nbChannels - 1; number1++) {
            for (int number2 = number1 + 1; number2 <= nbChannels; number2++) {
                generate_sub(comps, new Comparator((short) number1, (short) number2), 0);
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
        for (Comparator comp : list) {
            sb.append(comp.toString());
        }

        System.out.println("Stored: " + sb.toString());
        
        /*
        TODO: Write to file
        */
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

}
