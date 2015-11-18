package sortingnetworkspaper;

import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;

/**
 *
 * @author Mathias
 */
public class GenerateThread implements Runnable {

    private ObjectBigArrayBigList<short[][]> partN;
    private final Processor p;
    private final int maxX;
    private int maxShifts;
    private short nbComp;
    private long startIndex;
    private long endIndex;

    GenerateThread(Processor p) {
        partN = new ObjectBigArrayBigList();
        maxX = ((1 << (p.getNbChannels() - 1)) | 1);
        maxShifts = p.getNbChannels() - 2;
        this.p = p;
    }

    @Override
    public void run() {
        int number;
        int outerShift;
        int comp;
        ObjectBigArrayBigList<short[][]> N = p.getN();
        long index = startIndex;
        ObjectBigListIterator<short[][]> iter = N.listIterator(index);

        partN.clear();
        short[][] network;
        short[][] tempNetwork;
        while (iter.hasNext() && index < endIndex) { //For all networks
            network = iter.next();
            //For all comps
            for (number = 3; number <= maxX; number = (number << 1) - 1, maxShifts--) { //x*2 - 1
                comp = number;
                for (outerShift = 0; outerShift <= maxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
                    tempNetwork = network.clone();
                    tempNetwork[0] = network[0].clone();
                    tempNetwork[0][nbComp] = (short) comp; //TODO: 1 Cast better?
                    Processor.processData(tempNetwork, (short) comp);
                    partN.add(tempNetwork);
                }
            }
            index++;
        }
        p.addToNewN(partN);
        
        synchronized (p) {
            p.notify(); //TODO: Add within addToNewN to prevent another synchronized layer?
        }
    }

    /**
     * Set the amount of comparators in the network.
     *
     * @param nbComp The amount of comparators in the network.
     */
    public void setNbComp(short nbComp) {
        this.nbComp = nbComp;
    }

    public void setIndex(long startIndex, long endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public ObjectBigArrayBigList<short[][]> getPartN() {
        return this.partN;
    }
}
