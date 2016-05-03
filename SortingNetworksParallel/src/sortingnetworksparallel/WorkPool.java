/**
 * MIT License
 *
 * Copyright (c) 2015-2016 Mathias DEKEMPENEER, Vincent DERKINDEREN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sortingnetworksparallel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import sortingnetworksparallel.memory.NullArray;

/**
 * A Pool that when given a task will hand over the tasks to multiple threads.
 *
 * @author Mathias Dekempeneer and Vincent Derkinderen
 * @version 1.0
 */
public class WorkPool {

    private final Processor processor;
    private final ThreadPoolExecutor executor;
    private final int nbComps;
    private CountDownLatch latch;
    private final int INNER_SIZE;

    /**
     * Create a workpool that can be used to assign work to a thread.
     *
     * @param processor The {@link Processor} that uses this. Methods of this
     * {@link Processor} will be used.
     * @param nbChannels The amount of channels used in the network.
     * @param innerSize The size of the list of networks the thread will
     * generate on.
     * @param percThreads The percentage (0-1) of the amount of threads obtained
     * by Runtime#getRuntime()#availableProcessors()
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
     * Get a pruned list by Performing a generate &amp; Prune cycle on N. N will
     * not be modified.
     *
     * @param N The list to perform a generate &amp; prune cycle on.
     * @param nbComp One more than the amount of comparators the networks in N
     * currently have.
     * @return A pruned list.
     */
    public NullArray performCycle(final NullArray N, final short nbComp) {
        long capacity = N.size() * nbComps;
        if (capacity > Integer.MAX_VALUE) {
            System.out.println("[WARNING]: Ensured capacity (" + capacity + ") exceeds Integer.MAX_VALUE. Hopefully we didn't need that much.");
        }
        final NullArray resultN = new NullArray((int) Math.min(capacity, Integer.MAX_VALUE));
        final int nb = INNER_SIZE;
        latch = new CountDownLatch((int) Math.ceil(N.size() / (double) nb));

        //Perform generate & prune for every batch of old networks.
        for (int index = 0; index < N.size(); index += nb) {
            final int startIndex = index;

            //Give task to thread
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    ObjectArrayList<short[][]> prunedList = processor.generate(N, startIndex, nb, nbComp);
                    processor.innerPrune(prunedList);

                    int networkIndex = resultN.add(prunedList);
                    processor.prune(resultN, networkIndex, prunedList.size());

                    latch.countDown();
                }

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

    /**
     * Shut down the executor.
     */
    public void shutDown() {
        executor.shutdownNow();
    }

}
