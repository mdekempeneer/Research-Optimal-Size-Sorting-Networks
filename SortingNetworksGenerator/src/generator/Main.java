package generator;

import networklib.Comparator;

/**
 *
 * @author Admin
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 2) {
            int nbChannels = Integer.parseInt(args[0]);
            int nbComp = Integer.parseInt(args[1]);
            
            generate(nbChannels, nbComp);
            
            
        } else {
            System.out.println("Format is: n k");
        }
    }
    
    public static void generate(int channels, int max) {
        Comparator[] comps = new Comparator[max];
        
        //Iterate  over all comparator combination
        for (int number1 = 1; number1 <= channels-1; number1++) {
            for (int number2 = number1+1; number2 <= channels; number2++) {
            generate_sub(comps, new Comparator(number1, number2), 0, channels);
        }
        }
    }
    
    private static void generate_sub(Comparator[] list, Comparator comp, int current, int nbChannels) {
        int max = list.length;
        if (current < max) {
            Comparator[] newList = cloneCompList(list);
        }
    }
}
