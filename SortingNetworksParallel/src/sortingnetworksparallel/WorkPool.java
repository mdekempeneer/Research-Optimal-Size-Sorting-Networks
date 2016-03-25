package sortingnetworksparallel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import sortingnetworksparallel.memory.Counter;
import sortingnetworksparallel.memory.ObjArrayList;

/**
 * A Pool that when given a task will hand over the tasks to multiple threads.
 *
 * @author Admin
 */
public class WorkPool {

    private final Processor processor;
    private final ThreadPoolExecutor executor;
    private final int nbComps;
    private boolean shouldSave = false;
    private CountDownLatch latch;
    private final int INNER_SIZE;

    /**
     *
     * @param processor The {@link Processor} that uses this. Methods of this
     * {@link Processor} will be used.
     * @param nbChannels The amount of channels used in the network.
     * @param innerSize //TODO
     * @param percThreads //TODO
     */
    public WorkPool(Processor processor, short nbChannels, int innerSize, double percThreads) {
        this.processor = processor;
        this.nbComps = (nbChannels * (nbChannels - 1)) / 2;
        this.INNER_SIZE = innerSize;

        int nbThreads = Runtime.getRuntime().availableProcessors();
        nbThreads = (int) (nbThreads * percThreads);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads);

        System.out.println("Will be using " + nbThreads + " threads");
    }

    /**
     * Get a pruned list by Performing a generate&Prune cycle on N. N will not
     * be modified.
     *
     * @param oldL //TODO
     * @param startIndex //TODO
     * @param resultN //TODO
     * @param nbComp One more than the amount of comparators the networks in N
     * currently have.
     * @return A pruned list.
     */
    public ObjArrayList<short[][]> performCycle(final ObjArrayList<short[][]> oldL, int startIndex, final ObjArrayList<short[][]> resultN, final short nbComp) {
        final int nb = INNER_SIZE;
        latch = new CountDownLatch((int) Math.ceil((oldL.size() - startIndex) / (double) nb));
        resultN.ensureCapacity(oldL.size() * nbComps);

        final AtomicInteger doneIndex = new AtomicInteger();

        //Perform generate & prune for every batch of old networks.
        for (int index = startIndex; index < oldL.size(); index += nb) {
            final int startIndexT = index;

            //Give task to thread
            executor.execute(new Runnable() {

                Counter counter = new Counter();

                @Override
                public void run() {
                    ObjectArrayList<short[][]> prunedList = processor.generate(oldL, startIndexT, nb, nbComp, counter);
                    processor.innerPrune(prunedList, counter);

                    int networkIndex = resultN.add(prunedList);
                    processor.prune(resultN, networkIndex, prunedList.size(), counter);

                    //Processor.initCounter.addCounter(counter);
//                    Processor.countList.add(counter);
                    Processor.result.addCounter(counter);

                    latch.countDown();
                    doneIndex.getAndAdd(nb);
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException E) {
            System.out.println("An interruptException happened in the workPool.");
        }

        if (shouldSave) {
            processor.save(oldL, doneIndex.get(), resultN, nbComp);
            System.exit(0);
        }

        //Adjust sizes.
        resultN.fixNulls();
        resultN.trim();

        return resultN;
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
    public ObjArrayList<short[][]> performCycle(final ObjArrayList<short[][]> N, final short nbComp) {
        long capacity = N.size() * nbComps;
        if (capacity > Integer.MAX_VALUE) {
            System.out.println("[WARNING]: Ensured capacity (" + capacity + ") exceeds Integer.MAX_VALUE. Hopefully we didn't need that much.");
        }
        final ObjArrayList<short[][]> resultN = new ObjArrayList((int) Math.min(capacity, Integer.MAX_VALUE));
        final int nb = 64;
        latch = new CountDownLatch((int) Math.ceil(N.size() / (double) nb));

        final AtomicInteger doneIndex = new AtomicInteger();

        //Perform generate & prune for every batch of old networks.
        for (int index = 0; index < N.size(); index += nb) {
            final int startIndex = index;

            //Give task to thread
            executor.execute(new Runnable() {

                Counter counter = new Counter();

                @Override
                public void run() {
                    ObjectArrayList<short[][]> prunedList = processor.generate(N, startIndex, nb, nbComp, counter);
                    processor.innerPrune(prunedList, counter);

                    int networkIndex = resultN.add(prunedList);
                    processor.prune(resultN, networkIndex, prunedList.size(), counter);

                    //Processor.initCounter.addCounter(counter);
//                    Processor.countList.add(counter);
                    Processor.result.addCounter(counter);

                    latch.countDown();
                    doneIndex.getAndAdd(nb);
                }

            });
        }

        try {
            latch.await();
        } catch (InterruptedException E) {
            System.out.println("An interruptException happened in the workPool.");
        }

        if (shouldSave) {
            processor.save(N, doneIndex.get(), resultN, nbComp);
            System.exit(0);
        }

        //Adjust sizes.
        resultN.fixNulls();
        resultN.trim();

        return resultN;
    }

    /**
     * TODO
     */
    public void shutDownAndSave() {
        executor.shutdownNow();
        shouldSave = true;

        try {
            executor.awaitTermination(2, TimeUnit.HOURS);
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkPool.class.getName()).log(Level.SEVERE, null, ex);
        }

        long r = latch.getCount();
        for (long i = 0; i < r; i++) {
            latch.countDown();
        }
    }

    /**
     * TODO
     */
    public void shutDown() {
        executor.shutdownNow();
    }

}
