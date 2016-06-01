# Research Optimal Size Sorting Networks

<DL>

<DT>Instructies</DT>
<i>('argName' : arg : comment)</i>
	
<DT>SortingNetworksParallel:</DT>
&ltn&gt &ltk&gt &ltinnerSize&gt &ltThreadPercentage&gt
<br>n : #channels
<br>k : #comparator upperBound
<br>innerSize : The amount of networks a thread takes to generate on.
<br>ThreadPercentage: The percentage of threads used. Used with the Java getAvailableProcessors().

<DT>SortingNetworksGenerator:</DT>
'JFileChooser' : &ltn&gt &ltk&gt : n = #channels, k = #comparators </br>
'path' : &ltn&gt &ltk&gt &ltpath&gt : n = #channels, k = #comparators, path = path to output saveFile

<DT>SortingNetworksTester:</DT>
'File' : -f &ltpath&gt : path = path to input loadFile (results append behind each network line.) </br>
'Network Arg' : -n &ltn&gt &ltk&gt (&ltchannel1_1&gt , &ltchannel2_1&gt)(&ltchannel1_2&gt,&ltchannel2_2&gt)... : n = #channels, n = #comparators, channel1_1 = first channel of comparator 1, channel2_1 = second channel of comparator 1,... </br>
'JFileChooser' : : Chose file to test using FileChooser

<DT>SortingNetworksVisualizer:</DT>
'File' : -f(p) &ltpath&gt : path = path to input loadFile, optional p = save image. </br>
'Network Arg' : -n(p) &ltn&gt &ltk&gt (&ltchannel1_1&gt, &ltchannel2_1&gt)(&ltchannel1_2&gt,&ltchannel2_2&gt)... : n = #channels, n = #comparators, channel1_1 = first channel of comparator 1, channel2_1 = second channel of comparator 1,..., optional p = save image. </br>

</DL>
