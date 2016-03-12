class Processor {
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
    short* process(int y);

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

short* Processor::process(int y) {
    /* Initialize inputs */
    short x[10];
    
    return x;
}