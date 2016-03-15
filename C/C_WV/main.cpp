/* 
 * File:   main.cpp
 * Author: Admin
 *
 * Created on March 11, 2016, 1:21 PM
 */

#include <cstdlib>
#include <string>
#include <iostream>
#include <stdio.h>
#include <math.h>
#include <list>

#define trailingZeros(x) __builtin_ctz(x)
#define leadingZeros(x) __builtin_clz(x)
#define bitCount(x) __builtin_popcount(x)

using namespace std;

/* Type def */
typedef unsigned short lshort;
typedef lshort_array** networkRef; //TODO: Change to shared_ptr

//Array of shorts

typedef struct lshort_array {
    size_t length;
    lshort* shorts;
} lshort_array;

/**
 * Initialize (allocate) an array of lshorts.
 * @param N The amount of lshorts.
 * @return A reference to the beginning of this structure.
 */
lshort_array* initialiaze_lshort_array(size_t N) {
    lshort_array* arr = (lshort_array*) malloc(sizeof (lshort_array) + N * sizeof (lshort))
    arr->shorts = (lshort*) (arr + 1);

    return arr;
}

networkRef* initialize_network() {
    return (networkRef*) malloc(sizeof(lshort_array) * NB_CHANNELS);
}







/* Variables */
const lshort NB_CHANNELS = 8;
const lshort UPPERBOUND = 19;

const int INNERSIZE = 256;
const double PERC_THREADS = 1.0;

//WorkPool workPool;
const int MAX_OUTER_COMPARATOR = ((1 << (NB_CHANNELS - 1)) | 1);
const int MAX_SHIFTS = NB_CHANNELS - 2;
lshort allOnesList[NB_CHANNELS];



std::list<network> firstTimeGenerate(network defaultNetwork);
void processData(network data, lshort newComp);
void fillAllOnesList();
lshort swapCompare(lshort input, lshort comp);
lshort** getOriginalInputs();
lshort* getPermutations(short nbOnes, short maxBits);
int factorial(int n);

/*
 * 
 */
int main(int argc, char* argv[]) {

    //Setup
    fillAllOnesList();
    network defaultNetwork = getOriginalInputs();

    //execute


    //areas = { {1,2,0,0}, {3,4,0,0}, {5,6,0,0}, {7,8,9,10}};

    /*printf("The size of areas (int[]): %ld\n",
            sizeof (areas));*/

    //delete[] areas;

    //cout << "Hello";
    printf("Hello world \n");
    return 0;
}

/**
 * Get the output of the comparator comp given the input.
 *
 * @param input The input to give the comparator.
 * @param comp The comparator to get the output from.
 * @return The result by switching the bits in the input according to comp.
 */
lshort swapCompare(lshort input, lshort comp) {
    int pos1 = 31 - leadingZeros(comp);
    int pos2 = trailingZeros(comp);
    //(input >> pos1) & 1 = first (front bit)
    //(input >> pos2) & 1 = 2nd (back bit)
    return (((input >> pos1) & 1) <= ((input >> pos2) & 1)) ? input : (short) (input ^ comp); // TADAM!!!
}


/* ------------------------- Setup ------------------------ */

/**
 * Generate a list of all possible networks with 1 comparator.
 *
 * @param defaultNetwork A network that is considered default. This means it
 * has all possible outputs.
 * @return A list of all possible networks with 1 comparator.
 */
std::list<network> firstTimeGenerate(network defaultNetwork) {
    int capacity = (NB_CHANNELS * (NB_CHANNELS - 1)) / 2;
    std::list<network> networkList; //networkList.push_front()
    int cMaxShifts = MAX_SHIFTS;

    /* For all comparators */
    for (lshort number = 3; number <= MAX_OUTER_COMPARATOR; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
        lshort comp = number;
        for (int outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
            //new Network (via clone)
            network data = defaultNetwork.clone();

            //Fill
            data[0] = new short[2];
            data[0][0] = comp;
            processData(data, comp, 1);
            processW(data, comp, 1);

            networkList.push_front(data);
        }
    }

    return networkList;
}

/**
 * Processes the data for the new comparator. Adding the comparator to the
 * data[0] is assumed to be done already.
 *
 * @param data The network
 * @param newComp The comparator to process the data on.
 * @param startIndex The outerIndex of where to start. (= 1 will cover
 * everything.)
 */
void processData(network data, lshort newComp, int startIndex) {
    lshort* processed;
    bool found;

    for (int nbOnes = startIndex; nbOnes < NB_CHANNELS; nbOnes++) {
        processed = new short[data[nbOnes].length];

        int counter = 0;
        bool foundNew = false;

        for (int innerIndex = 0; innerIndex < data[nbOnes].length; innerIndex++) {
            short oldValue = data[nbOnes][innerIndex];
            short value = swapCompare(oldValue, newComp);
            if (value != oldValue) {
                foundNew = true;
            }
            found = false;
            for (int i = counter - 1; i >= 0; i--) {
                if (processed[i] == value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                processed[counter++] = value;
            }
        }

        if (foundNew) { //CAUTION! Don't do this 'shared array' with writing lists to disk.
            lshort result = new short[counter];
            System.arraycopy(processed, 0, result, 0, result.length);
            data[nbOnes] = result;
        }
    }

}

void processData(network data, lshort newComp) {
    processData(data, newComp, 1);
}

/**
 * Create an array of nbChannels elements equal to allOnes. allOnes = (1
 * &lt&lt nbChannels) - 1
 *
 * @param nbChannels The amount of the elements and the amount of 1's.
 * @return The created array.
 */
void fillAllOnesList() {
    lshort allOnes = (1 << NB_CHANNELS) - 1;
    allOnesList = new lshort[NB_CHANNELS];
    std::fill(allOnesList, allOnesList + NB_CHANNELS, allOnes);
}

/**
 * Get all original inputs excluding the already sorted ones.
 *
 * @param upperBound
 * @return range from 2 to (2^nbChannels-1) excluding all sorted (binary)
 * ones.
 */
lshort** getOriginalInputs() {

    /* 
     data[0] Comparators
     data[1] holds outputs with 1 '1's.
     data[2] holds outputs with 2 '1's.
     ...
     data[n] nbChannels holds W(C,x,k) info.
     */
    lshort** data = new lshort*[NB_CHANNELS + 1];
    data[0] = new lshort[2]; //Comparators.
    data[NB_CHANNELS] = new lshort[(NB_CHANNELS - 1) << 2];
    register int wIndexCounter;

    for (int nbOnes = 1; nbOnes < NB_CHANNELS; nbOnes++) {
        data[nbOnes] = getPermutations(((1 << nbOnes) - 1), NB_CHANNELS);
        wIndexCounter = (nbOnes - 1) << 2;

        data[NB_CHANNELS][wIndexCounter] = ((1 << NB_CHANNELS) - 1);
        data[NB_CHANNELS][wIndexCounter + 1] = NB_CHANNELS;
        data[NB_CHANNELS][wIndexCounter + 2] = ((1 << NB_CHANNELS) - 1);
        data[NB_CHANNELS][wIndexCounter + 3] = NB_CHANNELS;
    }
    return data;
}

/**
 * Get all permutations possible using the start and the max amount of bits.
 *
 * @param start The start value. Normally a value with all 1's on the right
 * side.
 * @param maxBits The maximum amount of bits available. (nbChannels)
 * @return All permutations starting with start that are smaller than
 * (2^maxBits)-1
 */
lshort* getPermutations(short nbOnes, short maxBits) {

    lshort start = ((1 << nbOnes) - 1);

    //Calculate length
    int beginNbOnes = bitCount(start); //=bitCount
    float temp = factorial(maxBits) / factorial(beginNbOnes); //TODO: Could get more efficient
    temp /= factorial(maxBits - beginNbOnes);
    int length = ceil(temp);


    //Variables
    lshort* result = new lshort[length];
    lshort value = start;
    int max = (1 << maxBits) - 1;
    lshort t;
    int index = 0;

    //Get all permutations.
    do {
        result[index] = value;
        t = value | (value - 1);
        value = (t + 1) | (((~t & -~t) - 1) >> (trailingZeros(value) + 1));
        index++;
    } while (value < max);

    return result;
}

/**
 * Get n!
 * @param
 * @return n! 
 */
int factorial(int n) {
    return (n == 1 || n == 0) ? 1 : factorial(n - 1) * n;
}



