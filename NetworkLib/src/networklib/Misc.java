/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networklib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Admin
 */
public class Misc {

    /**
     * Checks if a bit list is sorted.
     *
     * @param input an array of {@link Bit}s to check
     * @return true if sorted
     */
    public static boolean isSorted(Bit[] input) {
        for (int i = 0; i < input.length - 1; i++) {
            if (input[i].getValue() > input[i + 1].getValue()) {
                return false;
            }
        }
        return true;
    }

    public static void printBin(ArrayList list, Bit[] soFar, int iterations) {
        if (iterations == 0) {
            list.add(soFar.clone());
            System.out.println(Arrays.toString(soFar)); //TODO remove 
        } else {
            // 0
            soFar[iterations - 1].setValue(0);
            printBin(list, soFar, iterations - 1);
            
            // 1
            soFar[iterations - 1].setValue(1);
            printBin(list, soFar, iterations - 1);
        }
    }

    /**
     * Read the first line of a given path.
     *
     * @param path The path to the file.
     * @return The first line read.
     */
    public static String readFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            if (line != null) {
                return line;
            }
        } catch (FileNotFoundException exc) {
            System.err.println("File not found exception " + path);
        } catch (IOException exc) {
            System.err.println("IO Exception" + exc.toString());
        }

        return null;
    }

}
