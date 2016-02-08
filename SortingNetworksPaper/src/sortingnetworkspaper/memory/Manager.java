package sortingnetworkspaper.memory;

import java.util.Arrays;

/**
 *
 * @author Admin
 */
public class Manager {

    private static ObjArrayList<short[][]>[] loadedIn;
    private static int[] loadedIndices;
    private static byte[] loadedByNb;

    /**
     * Create a Manager used for holding {@link ObjArrayList}. A maximum of
     * length lists are kept in memory, the rest is written to files.
     *
     * @param length The maximum amount of ObjArrayLists to keep loaded in.
     */
    public Manager(int length) {
        loadedIndices = new int[length];
        Arrays.fill(loadedIndices, -1);
        loadedByNb = new byte[length];
        loadedIn = new ObjArrayList[length];
    }

    /**
     * Get the {@link ObjArrayList} associated with index.
     *
     * @param index The index the wanted list carries.
     * @return The list associated with index.
     */
    public ObjArrayList<short[][]> getObjArrList(int index) {
        //Check if loaded
        int i;
        for (i = 0; i < loadedIndices.length; i++) {
            if (index == i) {
                break;
            }
        }

        if (i < loadedIndices.length) {
            //return Loaded
            loadedByNb[i]++;
            return loadedIn[i];
        } else {
            //Find empty spot
            ObjArrayList<short[][]> list = null;
            int j = findEmptySpot();

            if (j != -1) {
                //TODO: Readfile
                list = null; //read; serializable

                loadedIn[j] = list;
            } else {
                //free a spot
                j = findUnusedSpot();

                if (j != -1) {
                    //TODO: write old.

                    list = null; //TODO: Read new
                    loadedIn[j] = list;
                } else {
                    System.err.println("[ERROR]: Tried loading but no space.");
                }

            }

            //Load In
            loadedIndices[j] = index;
            loadedByNb[j] = 1;
            return loadedIn[j];
        }
    }

    /**
     * Frees the {@link ObjArrayList} associated with index. This will decrease
     * the number 'amount of resources using this list '.
     *
     * @param index The index of the list to free.
     */
    public void freeObjArrList(int index) {
        int i;
        for (i = 0; i < loadedIndices.length; i++) {
            if (i == index) {
                break;
            }
        }

        if (i < loadedIndices.length) {
            loadedByNb[i]--;
        } else {
            System.err.println("[ERROR]: Tried unloading non-loaded list " + index);
        }
    }

    /**
     * Find the index of an empty spot in the array.
     *
     * @return The index of the empty spot. When no index is found -1 will be
     * returned.
     */
    private int findEmptySpot() {
        for (int i = 0; i < loadedIn.length; i++) {
            if (loadedIn[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the index of a spot with an unused list.
     *
     * @return The index of the spot with the unused list. When no index is
     * found -1 will be returned.
     */
    private int findUnusedSpot() {
        for (int i = 0; i < loadedIn.length; i++) {
            if (loadedByNb[i] <= 0) {
                return i;
            }
        }

        return -1;
    }

}
