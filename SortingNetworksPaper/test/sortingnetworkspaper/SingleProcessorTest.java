package sortingnetworkspaper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class SingleProcessorTest {

    public SingleProcessorTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testProcessW() {
        int upperBound = 5;
        short nbChannels = 4;

        short[] comp = {3, 6, 3};
        short[] first = {15, 4, 15, 4,
            15, 4, 15, 4,
            15, 4, 15, 4};
        short[] second = {15, 4, 13, 3,
            15, 4, 15, 4,
            14, 3, 15, 4};
        short[] third = {15, 4, 11, 3,
            15, 4, 11, 3,
            12, 2, 15, 4};
        short[] forth = {15, 4, 9, 2,
            14, 3, 11, 3,
            12, 2, 15, 4};

        SingleProcessor processor = new SingleProcessor(nbChannels, upperBound);
        short[][] data = processor.getOriginalInputs(upperBound);

        /* Check */
        for (int i = 0; i < (nbChannels - 1) * 4; i++) {
            assertEquals(first[i], data[nbChannels][i]);
        }

        /* Compute */
        processor.processData(data, comp[0]);
        processor.processW(data, comp[0]);

        /* Check */
        for (int i = 0; i < (nbChannels - 1) * 4; i++) {
            assertEquals(second[i], data[nbChannels][i]);
        }

        /* Compute */
        processor.processData(data, comp[1]);
        processor.processW(data, comp[1]);

        /* Check */
        for (int i = 0; i < (nbChannels - 1) * 4; i++) {
            assertEquals(third[i], data[nbChannels][i]);
        }

        /* Compute */
        processor.processData(data, comp[2]);
        processor.processW(data, comp[2]);

        /* Check */
        for (int i = 0; i < (nbChannels - 1) * 4; i++) {
            assertEquals(forth[i], data[nbChannels][i]);
        }


        /*
         P = 1111; L = 1111 = 15
         P = 1111; L = 1111
         P = 1111; L = 1111

         ———— 3
         P = 1111; L = 1101 = 13
         P = 1111; L = 1111
         P = 1110 = 14; L = 1111

         ———— 6
         P = 1111; L = 1011 = 11
         P = 1111; L = 1111
         P = 1100 = 12; L = 1111

         ———— 3
         P = 1111; L = 1001 = 9
         P = 1111; L = 1111
         P = 1100 = 12; L = 1111
         */
    }

    @Test
    public void testGetProcessInput() {
        int upperBound = 5;
        short nbChannels = 4;

        short[] first = {15, 4, 15, 4,
            15, 4, 15, 4,
            15, 4, 15, 4};

        SingleProcessor processor = new SingleProcessor(nbChannels, upperBound);
        short[][] data = processor.getOriginalInputs(upperBound);

        assertEquals(data[nbChannels].length, first.length);
    }
}
