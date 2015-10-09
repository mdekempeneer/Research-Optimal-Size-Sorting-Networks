/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tester;

/**
 *
 * @author Mathias
 */
public class Bit {
    
    private boolean value;
    
    /**
     * Create a bit
     * @param value true if 1, false if 0. 
     */
    public Bit(boolean value) {
        this.value = value;
    }
    
    /**
     * Get the value of the bit.
     * @return 1 if true, 0 if false.
     */
    public int getValue() {
        return (value) ? 1 : 0;
    }
    
    /**
     * Set a bit value
     * @param value true if 1 and false if 0.
     */
    public void setValue(boolean value) {
        this.value = value;
    }
    
    /**
     * Set a bit value
     * @param value 0 if value is 0 and 1 otherwise.
     */
    public void setValue(int value) {
        this.value = (value != 0); 
    }
}
