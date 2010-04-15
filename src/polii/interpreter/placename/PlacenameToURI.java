package polii.interpreter.placename;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import utils.convert.geonames.Geonames2RDF;

public class PlacenameToURI {

	private static final Map<String, Integer> placeNames = Geonames2RDF.getNames();
	private final static String geobase = "http://sws.geonames.org/";
	
	public Set<URI> getPlacenameURIs(String text)
	{
		
		List<String> placeNamesList = new LinkedList<String>(placeNames.keySet());
		Collections.sort(placeNamesList, new ComparatorStringLength());
		Collections.reverse(placeNamesList);
		
		Set<URI> locationURIs = new HashSet<URI>();
		
		for(String name : placeNamesList)
		{
			String regex = "(.*" + name+"[\\. \\?!;]+.*)|(.* " + name + ".*)"; 
			if (text.matches(regex))
			{
				locationURIs.add(new URIImpl(geobase + placeNames.get(name) + "/"));
				System.out.println("Found place: " + name);
			
				text = text.replace(name, ""); //remove from text to prevent a rematch
			}
		}
	
		return locationURIs;
	}
	
	
}
