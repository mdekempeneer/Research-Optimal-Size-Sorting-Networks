# WV_SortingNetworks

<DL>

<DT>Instructies
	<DD><i>('argName' : arg : comment)</i>
	
		<DD><b>SortingNetworksParallel:</b></br> 
&ltn&gt &ltk&gt &ltinnerSize&gt &ltThreadPercentage&gt
<br>n : #channels
<br>k : #comparator upperBound
<br>innerSize : The amount of networks a thread takes to generate on.
<br>ThreadPercentage: The percentage of threads used. Used with the Java getAvailableProcessors().

	<DD><b>SortingNetworksGenerator:</b></br>
'JFileChooser' : &ltn&gt &ltk&gt : n = #channels, k = #comparators </br>
'path' : &ltn&gt &ltk&gt &ltpath&gt : n = #channels, k = #comparators, path = path to output saveFile

	<DD><b>SortingNetworksTester:</b></br>
'File' : -f &ltpath&gt : path = path to input loadFile (results append behind each network line.) </br>
'Network Arg' : -n &ltn&gt &ltk&gt (&ltchannel1_1&gt , &ltchannel2_1&gt)(&ltchannel1_2&gt,&ltchannel2_2&gt)... : n = #channels, n = #comparators, channel1_1 = first channel of comparator 1, channel2_1 = second channel of comparator 1,... </br>
'JFileChooser' : : Chose file to test using FileChooser

	<DD><b>SortingNetworksVisualizer:</b></br>
'File' : -f(p) &ltpath&gt : path = path to input loadFile, optional p = save image. </br>
'Network Arg' : -n(p) &ltn&gt &ltk&gt (&ltchannel1_1&gt, &ltchannel2_1&gt)(&ltchannel1_2&gt,&ltchannel2_2&gt)... : n = #channels, n = #comparators, channel1_1 = first channel of comparator 1, channel2_1 = second channel of comparator 1,..., optional p = save image. </br>

</DL>
