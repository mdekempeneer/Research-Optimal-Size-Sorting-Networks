package sortingnetworksparallel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import sortingnetworksparallel.memory.ObjArrayList;

/**
 * A Pool that when given a task will hand over the tasks to multiple threads.
 *
 * @author Admin
 */
//TODO: Don't forget to shutDown the executor when the application is finished!!!!!
public class WorkPool {

    private final Processor processor;
    private final short nbChannels;
    private final ThreadPoolExecutor executor;

    /**
     *
     * @param processor The {@link Processor} that uses this. Methods of this
     * {@link Processor} will be used.
     */
    public WorkPool(Processor processor, short nbChannels) {
        this.processor = processor;
        this.nbChannels = nbChannels;

        int nbThreads = Runtime.getRuntime().availableProcessors();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads);

        System.out.println("Will be using " + nbThreads + " threads");
    }

    //TODO: Will modifying N (remove instead of only iterating over be a memory boost?
    /**
     * Get a pruned list by Performing a generate&Prune cycle on N. N will not
     * be modified.
     *
     * @param N The list to perform a generate & prune cycle on.
     * @param nbComp One more than the amount of comparators the networks in N
     * currently have.
     * @return A pruned list.
     */
    public ObjArrayList<short[][]> performCycle(ObjArrayList<short[][]> N, short nbComp) {
        ObjectListIterator<short[][]> oldIter = N.listIterator();
        long capacity = N.size() * ((nbChannels * (nbChannels-1))/2);
        if(capacity > Integer.MAX_VALUE) {
            System.out.println("[WARNING]: Ensured capacity (" + capacity + ") exceeds Integer.MAX_VALUE. Hopefully we didn't need that much.");
        }
        ObjArrayList<short[][]> resultN = new ObjArrayList((int) Math.min(capacity, Integer.MAX_VALUE));
        CountDownLatch latch = new CountDownLatch(N.size());

        //Perform generate & prune for every old network.
        while (oldIter.hasNext()) {
            short[][] network = oldIter.next();

            //Give task to thread
            executor.execute(() -> {
                ObjectArrayList<short[][]> prunedList = processor.generate(network, nbComp);
                processor.innerPrune(prunedList);

                ObjectListIterator<short[][]> innerIter = prunedList.listIterator();

                while (innerIter.hasNext()) {
                    //Add newNetwork
                    short[][] newNetwork = innerIter.next();
                    int networkIndex = resultN.add(newNetwork, null); //gives -1 if no spot was found.
                    processor.prune(resultN, networkIndex);
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException E) {
            System.out.println("An interruptException happened in the workPool.");
        }
        
        //Adjust sizes.
        resultN.fixNulls();
        resultN.trim();

        return resultN;
    }
    
    public void shutDown() {
        executor.shutdownNow();
    }

}
