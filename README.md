# WV_SortingNetworks

<DL>
<DT>07/10/2015 - Bijeenkomst 1
	<DD>Te doen tegen de bijeenkomst:
		<UL>
		<LI>Paper lezen: http://arxiv.org/abs/1405.5754 [1]
		</UL>

	<DD>Te doen tegen de volgende bijeenkomst (14/10):
		<UL>
		<LI>Programma dat een visuele representatie van een netwerk "tekent".
		<LI>Programma dat test of een netwerk een Sorting Network is.
		<LI>Programma dat alle mogelijke netwerken voor n channels en k comparators genereert.
		<LI>Referenties uit paper [1] opzoeken en meebrengen naar de volgende bijeenkomst.
		<LI>Untangling procedure kennen/kunnen.
		<LI>Printout van alle mogelijke networks met n = 4, k = 5.
		</UL>

<DT>14/10/2015 - Bijeenkomst 2
	<DD>Te doen tegen de volgende bijeenkomst (21/10):
		<UL>
		<LI> Fix Tester bug; Het geeft unsorted als sorted.
		<LI> Meet tijd voor 1 test isSortingNetwork (en maal #theoretische networks bij brute force)
		<LI> Gooi dubbels weg. (2x zelfde comparator na elkaar = delete)
		<LI> Begrijpen bewijs paper p 5 bovenaan (zie ref [3] lemma 7).
		<LI> De 3 equivalentie-eigenschappen nagaan voor equivalentie (reflex, trans, symm); zie paper.
		<LI> Verbeteren van de image visualizer.
		</UL>
	<DD>Te doen tegen bijeenkomst 4 (28/10):
		<UL>
		<LI> Eerste ontwerp van de literatuurstudie presentatie
		</UL>
	
<DT>21/10/2015 - Bijeenkomst 3
	<DD>Te doen tegen de volgende bijeenkomst (28/10):
		<UL>
		<LI> Permutaties vinden voor overgangen van 3 sorted networks bij n=4.
		<LI> Primitieve datatypes gebruiken en minder objecten in de code.
		<LI> &lt,&gt (min, max) regels uitwerken.
		<LI> Beginnen aan de presentatie.
		</UL>

<DT>28/10/2015 - Bijeenkomst 4
	<DD>Te doen tegen de volgende bijeenkomst (06/11):
		<UL>
		<LI> Presentatie afwerken.
		<LI> Primitieve datatypes gebruiken en minder objecten in de code.
		</UL>

<DT>06/11/2015 - Bijeenkomst 5
	<DD>Te doen tegen de presentatie:
		<UL>
		<LI>Nakijken van de consistentie.
		<LI>Logische volgorde voor de slides bekijken.
		<LI>Aantal suggesties doorvoeren.
		</UL>
	<DD>Te doen tegen de volgende bijeenkomst (18/11):
		<UL>
		<LI>Generate en Prune algoritme uit de hoofdpaper implementeren.
		<LI>Boek van Knuth consulteren: het genereren van alle mogelijke sequenties van comparators.
		</UL>

<DT>09/11/2015 - Presentatie 1 (16.50-17.20)
	<DD>Als je met twee bent, zou je presentatie een 23-25 minuten moeten duren, dan is er ook nog 5-7 min om vragen te stellen (en te beantwoorden). Bedoeling is om een verhaal, een overzicht een inleiding tot het onderwerp te brengen. Daarbij mag je selecteren uit je papers ... Elke student volgt een viertal presentaties.

<DT>18/11/2015 - Bijeenkomst 6
	<DD>Te doen tegen de volgende bijeenkomst (25/11):
		<UL>
		<LI>Generate en Prune algoritme uit de hoofdpaper implementeren.
		</UL>
		
<DT>25/11/2015 - Bijeenkomst 7
	<DD>Te doen tegen de volgende bijeenkomst (07/12):
		<UL>
		<LI>Generate en Prune algoritme uit de hoofdpaper implementeren.
		<LI>Afwerken van de tweede presentatie in verband met de probleemstelling.
		</UL>
		
<DT>07/12/2015 - Presentatie 2 (16.10-16.25)
	<DD>Een korte voorstelling van je probleemstelling (wat wil je doen?, waarom? en hoe ga je het aanpakken en evalueren?)
	</br>Max 5 slides / 5 minuten, informeel en dan een open discussie.
	
<DT>07/12/2015 - Bijeenkomst 8
	<DD>Te doen tegen de volgende bijeenkomst (.../...)
		<UL>
		<LI>
		</UL>


<DT>Instructies
	<DD><i>('argName' : arg : comment)</i>

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
