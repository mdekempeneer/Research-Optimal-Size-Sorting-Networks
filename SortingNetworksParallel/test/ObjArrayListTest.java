
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Test;
import static org.junit.Assert.*;
import sortingnetworksparallel.memory.ObjArrayList;

/**
 *
 * @author Admin
 */
public class ObjArrayListTest {

    public ObjArrayListTest() {
    }

    int current = 0;

    public int getCurrentAndAdd() {
        return current++;
    }

    @Test
    public void testAdd() {

        ThreadPoolExecutor executor;
        int nbThreads = Runtime.getRuntime().availableProcessors() - 2;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads);

        int capacity = 100000;
        ObjArrayList<int[]> resultN = new ObjArrayList(capacity);
        CountDownLatch latch = new CountDownLatch(capacity);

        //Give task to thread
        while (current < capacity) {
            int t = current++;
            executor.execute(new Runnable() {
                public void run() {
                    int networkIndex = resultN.add(new int[]{t}, null); //gives -1 if no spot was found.`
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException E) {
            System.out.println("An interruptException happened in the workPool.");
        }

        executor.shutdownNow();

        for (int i = 0; i < capacity; i++) {

            if (!contains(resultN, i)) {
                System.err.println("Couldn't find " + i);
            }
            assertTrue(contains(resultN, i));
        }
    }

    private boolean contains(ObjArrayList<int[]> list, int k) {
        for (int i = 0; i < list.size()+1; i++) {
            if (list.get(i)[0] == k) {
                return true;
            }
        }

        return false;
    }

}
