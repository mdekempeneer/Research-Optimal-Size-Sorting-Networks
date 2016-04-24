
import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import sortingnetworksparallel.memory.NullArray;

/**
 *
 * @author Admin
 */
public class TestNullArray {

    @Before
    public void setUp() {
        elementsCount = 0;
        int nb = 1000000;
        a = new NullArray(nb);

        Random rand = new Random();
        for (int i = 0; i < nb - 2; i++) {
            int random = rand.nextInt(3);

            if (random == 0) { //null
                short[][] box = null;
                a.add(box);
            } else if (random == 1) { //value
                a.add(new short[2][]);
                elementsCount++;
            } else if (random == 2) { //cNull
                short random2 = (short) (1 + rand.nextInt(10));
                short[][] cNull = new short[1][1];
                a.add(cNull);

                random2 = (short) Math.min(random2, nb - a.size() - 1);
                cNull[0][0] = random2;

                short[][] box = null;
                for (int j = 1; j <= random2; j++) {
                    a.add(box);
                    i++;
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            int random = rand.nextInt(2);
            if (random == 0) {
                short[][] box = null;
                a.add(box);
            } else {
                short[][] box = new short[2][];
                a.add(box);
                elementsCount++;
            }
        }

        clone = a.clone();
        assertEquals(a.size(), nb);
    }

    int elementsCount;
    NullArray a;
    NullArray clone;

    @Test
    public void testNullArray() {
        check();
        iterateOver();
        checkAll(elementsCount);
    }

    private void iterateOver() {
        NullArray networkList = a;
        int bound = networkList.size();
        short[][] buffered = null;
        short[][] before = null;

        for (int outerIndex = 0; outerIndex < bound; outerIndex++) {
            short[][] network2 = (buffered != null) ? buffered : networkList.get(outerIndex);
            buffered = null;

            if (network2 != null && network2.length != 1) {
                before = network2;
                //value
            } else if (network2 == null) {
                if (before == null || before.length != 1) {
                    int index = outerIndex + 1;
                    buffered = null;
                    /* find next non null */
                    while (index < bound && (buffered = networkList.get(index)) == null) {
                        index++;
                    }

                    /* get # null in a row behind the first null*/
                    int diff = index - outerIndex - 1;
                    if(diff > Short.MAX_VALUE) {
                        System.out.println("error " + diff);
                    }
                    short difference = (short) diff; //TODO: possible overload.
                    /* Store cNull */
                    if (difference > 0) {
                        short[][] cNull = new short[1][1];
                        cNull[0][0] = difference;
                        networkList.set(outerIndex, cNull);
                        before = cNull;
                        outerIndex += difference;
                    }
                    /*} else if(before == null) {*/

                } else { //length == 1
                    before[0][0]++;
                }
            } else { //Cnull
                int skip = network2[0][0]; //Amount of nulls after this.
                outerIndex += skip;
                if (before != null && before.length == 1) { //came from cNull
                    before[0][0] += skip + 1;
                } else {
                    before = network2;
                }
            }
        }
    }

    private void checkAll(int elements) {
        int count = 0;
        int error = 0; //found null.
        int errorE = 0; //skipped elements

        for (int i = 0; i < a.size(); i++) {
            short[][] ele = a.get(i);

            if (ele == null) { //null
                if (i - 1 >= 0 && a.get(i - 1) == null) {
                    error++;
                } else if (i + 1 < a.size() && a.get(i + 1) == null) {
                    error++;
                }
            } else if (ele.length == 1) { //cNull
                int skipSize = ele[0][0];

                for (int j = 1; j <= skipSize; j++) {
                    short[][] elem = a.get(i + j);
                    if (elem != null && elem.length != 1) {
                        System.out.println("didn't spot " + (i + j));
                        errorE++;
                    }
                }

                i += skipSize;
            } else { //value
                count++;
            }

        }
        System.out.println(count + " " + error + " " + errorE);
        assertEquals(elements, count);
        assertEquals(0, error);
        assertEquals(0, errorE);
    }
    
    private void check() {
        int errorE = 0; //skipped elements

        for (int i = 0; i < a.size(); i++) {
            short[][] ele = a.get(i);

            if (ele == null) { //null
            } else if (ele.length == 1) { //cNull
                int skipSize = ele[0][0];

                for (int j = 1; j <= skipSize; j++) {
                    short[][] elem = a.get(i + j);
                    if (elem != null && elem.length != 1) {
                        System.out.println("didn't spot " + (i + j));
                        errorE++;
                    }
                }

                i += skipSize;
            } else { //value
            }

        }
        assertEquals(0, errorE);
    }

}
