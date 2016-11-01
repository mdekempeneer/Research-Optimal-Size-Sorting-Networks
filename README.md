# Research Optimal Size Sorting Networks

## Paper: [Research Optimal Size Sorting Networks](https://github.com/mdekempeneer/Research-Optimal-Size-Sorting-Networks/blob/master/Paper/SortingNetworks_DekempeneerDerkinderen_finaal.pdf)
<DL>

<DT>Instructions</DT>
Arguments that should be used when executing the code.
<br><i>('argName' : arg : comment)</i>
	
<DT>SortingNetworksParallel:</DT>
&ltn&gt &ltk&gt &ltinnerSize&gt &ltThreadPercentage&gt
<br>n : #channels (e.g. 8)
<br>k : #comparator : The maximum amount of comparators to calculate to, the upperBound. (e.g. 19)
<br>innerSize : :The amount of networks a thread takes to generate on. (e.g. 256)
<br>ThreadPercentage : : The percentage of threads used, [0-1]. Used with the Java getAvailableProcessors(). (e.g. 1)
<br>
<br>Using the input it will output a comparator network. This network will be a sorting network of optimal size when the comparator upperBound is high enough. Any output will be in a bit format. (e.g. 5 = 0..0101 = comparator (1 3))

<DT>SortingNetworksGenerator:</DT>
<b>JFileChooser</b> : &ltn&gt &ltk&gt 
<br>n : #channels
<br>k : #comparators
<br><b>path</b> : &ltn&gt &ltk&gt &ltpath&gt 
<br>n : #channels
<br>k : #comparators
<br>path : path to output saveFile

<DT>SortingNetworksTester:</DT>
<b>File</b> : -f &ltpath&gt
<br>path : path to input loadFile (results append behind each network line.)
<br><b>Network Arg</b> : -n &ltn&gt comp1,comp2,comp3,...
<br>n : #channels
<br>k : #comparators,
<br>comp1 : first comparator. (e.g. 5 = 0...0101 = (1 3))
<br>comp2 : second comparator.
<br><b>JFileChooser</b> : : Chose file to test using FileChooser
<br>
<br>Will output true if the given network is a sorting network, false otherwise.

<DT>SortingNetworksVisualizer:</DT>
<b>File</b> : -fp &ltpath&gt / -f &ltpath&gt
<br>path : path to input loadFile, the p is optional = save image.
<br><b>Network Arg</b> : -n(p) &ltn&gt &ltk&gt (&ltchannel1_1&gt, &ltchannel2_1&gt)(&ltchannel1_2&gt,&ltchannel2_2&gt)...
<br>n : #channels
<br>k : #comparators
<br>channel1_1 : first channel of comparator 1
<br>channel2_1 : second channel of comparator 1
<br>Again, the p is optional = save image </br>

</DL>
