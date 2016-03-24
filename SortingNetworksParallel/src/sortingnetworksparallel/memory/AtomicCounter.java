package sortingnetworksparallel.memory;

import java.math.PublicBigInteger;

/**
 *
 * @author Mathias
 */
public class AtomicCounter {
    
    PublicBigInteger test;

    public AtomicBigInteger uniqueCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger uniqueCounter_abs = new AtomicBigInteger("0");

    public AtomicBigInteger redundantCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger redundantCounter_abs = new AtomicBigInteger("0");

    public AtomicBigInteger kLengthCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger kLengthCounter_abs = new AtomicBigInteger("0");

    public AtomicBigInteger pLengthCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger pLengthCounter_abs = new AtomicBigInteger("0");

    public AtomicBigInteger lLengthCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger lLengthCounter_abs = new AtomicBigInteger("0");

    public AtomicBigInteger emptyPosCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger emptyPosCounter_abs = new AtomicBigInteger("0");

    public AtomicBigInteger networkPermCounter_rel = new AtomicBigInteger("0");
    public AtomicBigInteger networkPermCounter_abs = new AtomicBigInteger("0");

    /*public void addCounter(Counter counter) {
        uniqueCounter_rel.addAndGet(counter.uniqueCounter_rel);
        uniqueCounter_abs.addAndGet(counter.uniqueCounter_abs);

        redundantCounter_rel.addAndGet(counter.redundantCounter_rel);
        redundantCounter_abs.addAndGet(counter.redundantCounter_abs);

        kLengthCounter_rel.addAndGet(counter.kLengthCounter_rel);
        kLengthCounter_abs.addAndGet(counter.lLengthCounter_abs);

        pLengthCounter_rel.addAndGet(counter.pLengthCounter_rel);
        pLengthCounter_abs.addAndGet(counter.pLengthCounter_abs);

        lLengthCounter_rel.addAndGet(counter.lLengthCounter_rel);
        lLengthCounter_abs.addAndGet(counter.lLengthCounter_abs);

        emptyPosCounter_rel.addAndGet(counter.emptyPosCounter_rel);
        emptyPosCounter_abs.addAndGet(counter.emptyPosCounter_abs);

        networkPermCounter_rel.addAndGet(counter.networkPermCounter_rel);
        networkPermCounter_abs.addAndGet(counter.networkPermCounter_abs);
    }*/

}
