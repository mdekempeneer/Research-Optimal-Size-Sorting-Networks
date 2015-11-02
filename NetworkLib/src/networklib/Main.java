package networklib;

import networklib.tools.BinaryToText;

/**
 *
 * @author Admin
 */
public class Main {
    
    /**
     * Test methods.
     * @param args 
     */
    public static void main(String[] args) {
        
        // Convert .bi file to .txt
        BinaryToText btt = BinaryToText.getBinaryToText();
        btt.convert();
        
        //
    }
    
}
