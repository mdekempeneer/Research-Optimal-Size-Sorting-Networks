package sortingnetworksparallel.memory;

import java.math.BigInteger;
import java.math.MutableBigIntegerCustom;
import java.math.PublicBigInteger;

/**
 *
 * @author Mathias
 */
public class Counter {

    //MutableBigIntegerCustom test;
    //PublicBigInteger test;

    public PMutableBigInteger uniqueCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger uniqueCounter_abs = new PMutableBigInteger(0);

    public PMutableBigInteger redundantCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger redundantCounter_abs = new PMutableBigInteger(0);

    public PMutableBigInteger kLengthCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger kLengthCounter_abs = new PMutableBigInteger(0);
    

    public PMutableBigInteger pLengthCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger pLengthCounter_abs = new PMutableBigInteger(0);

    public PMutableBigInteger lLengthCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger lLengthCounter_abs = new PMutableBigInteger(0);

    public PMutableBigInteger emptyPosCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger emptyPosCounter_abs = new PMutableBigInteger(0);

    public PMutableBigInteger networkPermCounter_rel = new PMutableBigInteger(0);
    public PMutableBigInteger networkPermCounter_abs = new PMutableBigInteger(0);

    public void addCounter(Counter counter) {

        uniqueCounter_rel.add(counter.uniqueCounter_rel);
        uniqueCounter_abs.add(counter.uniqueCounter_abs);

        redundantCounter_rel.add(counter.redundantCounter_rel);
        redundantCounter_abs.add(counter.redundantCounter_abs);

        kLengthCounter_rel.add(counter.kLengthCounter_rel);
        kLengthCounter_abs.add(counter.kLengthCounter_abs);
        System.out.println(kLengthCounter_abs);

        pLengthCounter_rel.add(counter.pLengthCounter_rel);
        pLengthCounter_abs.add(counter.pLengthCounter_abs);

        lLengthCounter_rel.add(counter.lLengthCounter_rel);
        lLengthCounter_abs.add(counter.lLengthCounter_abs);

        emptyPosCounter_rel.add(counter.emptyPosCounter_rel);
        emptyPosCounter_abs.add(counter.emptyPosCounter_abs);

        networkPermCounter_rel.add(counter.networkPermCounter_rel);
        networkPermCounter_abs.add(counter.networkPermCounter_abs);
    }

}
