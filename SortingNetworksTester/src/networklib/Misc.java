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
import tester.Bit;

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
        for (int i = 1; i < input.length; i++) {
            if (input[1].getValue() < input[0].getValue()) {
                return false;
            }
        }
        return true;
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
