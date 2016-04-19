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
#define NETWORK_LENGTH NB_CHANNELS + 1

using namespace std;

/* Type def */
typedef unsigned short lshort;

/* Variables */
const lshort NB_CHANNELS = 8;
const lshort UPPERBOUND = 19;

const int INNERSIZE = 256;
const double PERC_THREADS = 1.0;

//Array of shorts

typedef struct lshort_array {
    size_t length;
    lshort* shorts;
} lshort_array;

//typedef lshort_array** networkRef;
typedef std::shared_ptr<lshort_array>* networkRef;

/**
 * Initialize (allocate) an array of lshorts.
 * @param N The amount of lshorts.
 * @return A reference to the beginning of this structure.
 */
shared_ptr<lshort_array> initialize_lshort_array(size_t N) {
    lshort_array* arr = (lshort_array*) malloc(sizeof (lshort_array) + N * sizeof (lshort));
    arr->shorts = (lshort*) (arr + 1);
    shared_ptr<lshort_array> ptr1(arr);
    return ptr1;
}

networkRef initialize_network() {
    //shared_ptr<lshort_array> ptr1((lshort_array*) (malloc(sizeof (lshort_array) * NB_CHANNELS)));
    shared_ptr<lshort_array>* net = ((shared_ptr<lshort_array>*)malloc(sizeof (shared_ptr<lshort_array>) * NETWORK_LENGTH));
    //return (networkRef*) malloc(sizeof (lshort_array) + sizeof (networkRef) * NB_CHANNELS);

    return net;
}

//WorkPool workPool;
const int MAX_OUTER_COMPARATOR = ((1 << (NB_CHANNELS - 1)) | 1);
const int MAX_SHIFTS = NB_CHANNELS - 2;
lshort* allOnesList;



std::list<networkRef> firstTimeGenerate(networkRef defaultNetwork);
void processData(networkRef data, lshort newComp);
void processData(networkRef data, lshort newComp, int startIndex);
void fillAllOnesList();
lshort swapCompare(lshort input, lshort comp);
networkRef getOriginalInputs();
shared_ptr<lshort_array> getPermutations(short nbOnes, short maxBits);
int factorial(int n);
networkRef clone(networkRef network);
void processW(networkRef data, lshort comp, int startIndex);

int main(int argc, char* argv[]) {

    //Setup
    fillAllOnesList();
    networkRef defaultNetwork = getOriginalInputs();

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
std::list<networkRef> firstTimeGenerate(networkRef defaultNetwork) {
    int capacity = (NB_CHANNELS * (NB_CHANNELS - 1)) / 2;
    std::list<networkRef> networkList; //networkList.push_front()
    int cMaxShifts = MAX_SHIFTS;

    /* For all comparators */
    for (lshort number = 3; number <= MAX_OUTER_COMPARATOR; number = (number << 1) - 1, cMaxShifts--) { //x*2 - 1
        lshort comp = number;
        for (int outerShift = 0; outerShift <= cMaxShifts; outerShift++, comp <<= 1) { //shift n-2, n-3, ... keer
            //new Network (via clone)
            networkRef data = clone(defaultNetwork);

            //Fill
            data[0].get()->shorts[0] = comp;
            processData(data, comp, 1);
            processW(data, comp, 1);

            networkList.push_front(data);
        }
    }

    return networkList;
}

networkRef clone(networkRef network) {
    networkRef clone = initialize_network();
    for (int i = 0; i < NETWORK_LENGTH; i++) {
        int length = network[i].get()->length;
        clone[i] = initialize_lshort_array(length);
        memcpy(clone[i].get()->shorts, network[i].get()->shorts, length * sizeof (lshort));
    }
    return clone;
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
void processData(networkRef data, lshort newComp, int startIndex) {
    shared_ptr<lshort_array> processed;
    bool found;

    for (int nbOnes = startIndex; nbOnes < NB_CHANNELS; nbOnes++) {
        int length = data[nbOnes].get()->length;
        processed = initialize_lshort_array(length);

        int counter = 0;
        bool foundNew = false;

        for (int innerIndex = 0; innerIndex < length; innerIndex++) {
            lshort oldValue = data[nbOnes].get()->shorts[innerIndex];
            lshort value = swapCompare(oldValue, newComp);
            if (value != oldValue) {
                foundNew = true;
            }
            found = false;
            for (int i = counter - 1; i >= 0; i--) {
                if (processed.get()->shorts[i] == value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                processed.get()->shorts[counter++] = value;
            }
        }

        if (foundNew) { //CAUTION! Don't do this 'shared array' with writing lists to disk.
            shared_ptr<lshort_array> result = initialize_lshort_array(counter);
            memcpy(result.get()->shorts, processed.get()->shorts, counter * sizeof (lshort));
            data[nbOnes] = result;
        }
    }

}

void processData(networkRef data, lshort newComp) {
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
    allOnesList = (lshort*) malloc(NB_CHANNELS * sizeof (lshort));
    std::fill(allOnesList, allOnesList + NB_CHANNELS, allOnes);
}

/**
 * Get all original inputs excluding the already sorted ones.
 *
 * @param upperBound
 * @return range from 2 to (2^nbChannels-1) excluding all sorted (binary)
 * ones.
 */
networkRef getOriginalInputs() {

    /* 
     data[0] Comparators
     data[1] holds outputs with 1 '1's.
     data[2] holds outputs with 2 '1's.
     ...
     data[n] nbChannels holds W(C,x,k) info.
     */
    networkRef data = initialize_network();

    //networkRef data = new lshort*[NB_CHANNELS + 1]; //TODO
    data[0] = initialize_lshort_array(2); //Comparators
    data[NB_CHANNELS] = initialize_lshort_array((NB_CHANNELS - 1) << 2);
    register int wIndexCounter;

    for (int nbOnes = 1; nbOnes < NB_CHANNELS; nbOnes++) {
        data[nbOnes] = getPermutations(((1 << nbOnes) - 1), NB_CHANNELS);
        wIndexCounter = (nbOnes - 1) << 2;

        data[NB_CHANNELS].get()->shorts[wIndexCounter] = ((1 << NB_CHANNELS) - 1);
        data[NB_CHANNELS].get()->shorts[wIndexCounter + 1] = NB_CHANNELS;
        data[NB_CHANNELS].get()->shorts[wIndexCounter + 2] = ((1 << NB_CHANNELS) - 1);
        data[NB_CHANNELS].get()->shorts[wIndexCounter + 3] = NB_CHANNELS;
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
shared_ptr<lshort_array> getPermutations(short nbOnes, short maxBits) {
    lshort start = ((1 << nbOnes) - 1);

    //Calculate length
    int beginNbOnes = bitCount(start); //=bitCount
    float temp = factorial(maxBits) / factorial(beginNbOnes); //TODO: Could get more efficient
    temp /= factorial(maxBits - beginNbOnes);
    int length = ceil(temp);

    //Variables
    shared_ptr<lshort_array> arr = initialize_lshort_array(length);

    lshort* result = arr.get()->shorts;
    lshort value = start;
    int max = (1 << maxBits) - 1;
    lshort t;
    int index = 0;

    //Get all permutations.
    //TODO SEGMENTATION FAULT ???
    do {
        result[index] = value;
        t = value | (value - 1);
        value = (t + 1) | (((~t & -~t) - 1) >> (trailingZeros(value) + 1));
        index++;
    } while (value < max);

    return arr;
}

/**
 * Get n!
 * 
 * @param n The parameter
 * @return n! 
 */
int factorial(int n) {
    return (n == 1 || n == 0) ? 1 : factorial(n - 1) * n;
}

/**
 * TODO
 *
 * @param data
 * @param comp
 * @param startIndex
 */
void processW(networkRef data, lshort comp, int startIndex) {
    shared_ptr<lshort_array> wResult = initialize_lshort_array(data[NB_CHANNELS].get()->length);

    int wIndexCounter;
    bool foundL;
    bool foundP;

    if (startIndex != 1) {
        memcpy(wResult.get()->shorts, data[NB_CHANNELS].get()->shorts, ((startIndex - 1) << 2) * sizeof (lshort));
    }

    for (int nbOnes = startIndex; nbOnes < NB_CHANNELS; nbOnes++) {
        wIndexCounter = (nbOnes - 1) << 2;

        int oldP = data[NB_CHANNELS].get()->shorts[wIndexCounter];
        int oldL = data[NB_CHANNELS].get()->shorts[wIndexCounter + 2];

        int P = (comp ^ ((1 << NB_CHANNELS) - 1)) & oldP;
        int L = (comp ^ ((1 << NB_CHANNELS) - 1)) & oldL;

        foundP = (oldP == P);
        foundL = (oldL == L);

        for (int i = 0; i < data[nbOnes].get()->length; i++) {
            lshort output = data[nbOnes].get()->shorts[i];

            if (!foundL) {
                L = L | (output & comp);
                if ((L & comp) == comp) {
                    foundL = true;
                }
            }

            if (!foundP) {
                P = P | ((output ^ ((1 << NB_CHANNELS) - 1)) & comp);
                if ((P & comp) == comp) {
                    foundP = true;
                }
            }

            /* Break; found both */
            if (foundP && foundL) {
                break;
            }
        }
        wResult.get()->shorts[wIndexCounter] = (lshort) P;
        wResult.get()->shorts[wIndexCounter + 1] = (lshort) bitCount(P);
        wResult.get()->shorts[wIndexCounter + 2] = (lshort) L;
        wResult.get()->shorts[wIndexCounter + 3] = (lshort) bitCount(L);
    }
    data[NB_CHANNELS] = wResult;
}