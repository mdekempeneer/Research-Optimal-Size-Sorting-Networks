package sortingnetworkspaper;

import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;

/**
 *
 *
 * @author Admin
 */
public interface Processor {
    
    public short[] process();
    
    public int getNbChannels();
    
    public ObjectBigArrayBigList<short[][]> getN();
    
    public void processData(short[][] data, short newComp);
    
    public void addToNewN(ObjectBigArrayBigList<short[][]> partN);
    
}
