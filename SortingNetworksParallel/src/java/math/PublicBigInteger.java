package java.math;

/**
 *
 * @author Mathias
 */
public class PublicBigInteger extends MutableBigInteger {
    
    public PublicBigInteger(PublicBigInteger i) {
        super(i);
    }
    
    public PublicBigInteger() {
        super();
    }
    
    public PublicBigInteger(BigInteger i) {
        super(i);
    }
    
    public PublicBigInteger(int i) {
        super(i);
    }
    
    public void add(PublicBigInteger i) {
        super.add(i);
    }
    
}
