class Processor {
#include <bitset>

    using namespace std;

private:
    short nbChannels;
    int upperBound;
    int innerSize;

    double percThreads = 0.75;

    //WorkPool workPool;
    int maxOuterComparator;
    int maxShifts;
    int allOnesList[];
    //byte allMinusOneList[];

public:

    Processor(short nbChannels, int upperBound, int innerSize);

    /*
     * Start processing and return a sorting network if there is any with maximum upperBound comparators.
     */
    short* process();

    /**
     * Generate a list of all possible networks with 1 comparator.
     *
     * @param defaultNetwork A network that is considered default. This means it
     * has all possible outputs.
     * @return A list of all possible networks with 1 comparator.
     */
    unsigned short firstTimeGenerate[][](unsigned short x);


};

Processor::Processor(short nbChannels, int upperBound, int innerSize) {
    this->nbChannels = nbChannels;
    this->upperBound = upperBound;
    this->innerSize = innerSize;
    this->maxOuterComparator = ((1 << (nbChannels - 1)) | 1);
    this->maxShifts = nbChannels - 2;

    allOnesList = int[nbChannels];
    int allOnes = (1 << nbChannels) - 1;
    std::fill(allOnesList, allOnesList + nbChannels, allOnes);
}

//TODO: Implement

short* Processor::process() {
    /* Initialize inputs */
    short x[10];


    std::bitset<16> comparator[40];


    return x;
}

/*unsigned short*** firstTimeGenerate(unsigned short defaultNetwork[][]) {
    unsigned short* test[][];

}*/













