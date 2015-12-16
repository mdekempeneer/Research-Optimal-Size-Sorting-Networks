/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sortingnetworkspaper;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mathias
 */
public class PermuteTest {

    public PermuteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testLengthPermutationList() {
        int counter = 1;
        byte[] test_arr = {0, 1, 2, 3, 4};

        long begin = System.nanoTime();
        while((test_arr = Permute.getNextPermutation(test_arr)) != null) {
            counter++;
        }
        assertEquals(counter, SingleProcessor.factorial(5), 0);
    }
}
