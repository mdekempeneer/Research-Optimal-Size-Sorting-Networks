
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

    @Test
    public void testAdd() {
        int current = 0;
        ThreadPoolExecutor executor;
        int nbThreads = Runtime.getRuntime().availableProcessors() - 2;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads);

        int capacity = 10000;
        ObjArrayList<int[]> resultN = new ObjArrayList(capacity);
        CountDownLatch latch = new CountDownLatch(capacity);

        //Give task to thread
        while (current < capacity) {
            int index = current++;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int networkIndex = resultN.add(new int[]{index}, null); //gives -1 if no spot was found.`
                    //System.out.println(index + " at " + networkIndex);
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
            assertTrue(resultN.get(i) != null);
            assertTrue(contains(resultN, i));
        }
    }

    private boolean contains(ObjArrayList<int[]> list, int k) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)[0] == k) {
                return true;
            }
        }

        return false;
    }

    @Test
    public void testAddMultiple() {
        int current = 0;
        ThreadPoolExecutor executor;
        int nbThreads = Runtime.getRuntime().availableProcessors() - 2;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads);

        int capacity = 100000;
        int conNumber = 50;
        ObjArrayList<int[]> resultN = new ObjArrayList(capacity);
        CountDownLatch latch = new CountDownLatch(capacity / conNumber);

        //Give task to thread
        while (current < capacity) {

            ObjectArrayList<int[]> numbers = new ObjectArrayList();
            for (int i = 0; i < conNumber; i++) {
                numbers.add(new int[]{current + i});
            }
            current += conNumber;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int networkIndex = resultN.add(numbers); //gives -1 if no spot was found.`
                    
                    for(int i = 0; i < numbers.size(); i++) {
                        assertTrue(resultN.get(networkIndex+i) == numbers.get(i));
                    }
                    //System.out.println(index + " at " + networkIndex);
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

        for (int i = 0; i < capacity; i += conNumber) {
            if (!containsMultiple(resultN, i, conNumber)) {
                System.err.println("Couldn't find " + i);
            }
            assertTrue(resultN.get(i) != null);
            assertTrue(contains(resultN, i));
        }
    }

    private boolean containsMultiple(ObjArrayList<int[]> list, int k, int conNumber) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)[0] == k) {
                for(int j = 1; j < conNumber; j++) {
                    assertTrue(list.get(i+j)[0] == k+j);
                }
                return true;
            }
        }

        return false;
    }

}
