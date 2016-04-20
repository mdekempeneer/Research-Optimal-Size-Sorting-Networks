package sortingnetworksparallel.memory;

/**
 *
 * @author Mathias
 */
public class Counter {

    //MutableBigIntegerCustom test;
    //PublicBigInteger test;
    public long uniqueCounter_abs;
    public long redundantCounter_abs;
    public long kLengthCounter_abs;
    public long pLengthCounter_abs;
    public long lLengthCounter_abs;
    public long emptyPosCounter_abs;
    public long networkPermCounter_abs;

    public synchronized void addCounter(Counter counter) {

        uniqueCounter_abs += counter.uniqueCounter_abs;
        redundantCounter_abs += counter.redundantCounter_abs;
        kLengthCounter_abs += counter.kLengthCounter_abs;
        pLengthCounter_abs += counter.pLengthCounter_abs;
        lLengthCounter_abs += counter.lLengthCounter_abs;
        emptyPosCounter_abs += counter.emptyPosCounter_abs;
        networkPermCounter_abs += counter.networkPermCounter_abs;
    }

    public void clear() {
        uniqueCounter_abs = 0;
        redundantCounter_abs = 0;
        kLengthCounter_abs = 0;
        pLengthCounter_abs = 0;
        lLengthCounter_abs = 0;
        emptyPosCounter_abs = 0;
        networkPermCounter_abs = 0;
    }

}
