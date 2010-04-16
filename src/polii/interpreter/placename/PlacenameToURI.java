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
		
		text = removeCommonSentences(text); 
		
		List<String> placeNamesList = new LinkedList<String>(placeNames.keySet());
		Collections.sort(placeNamesList, new ComparatorStringLength());
		Collections.reverse(placeNamesList);
		
		Set<URI> locationURIs = new HashSet<URI>();
		
		//System.out.println(text);
		
		for(String name : placeNamesList)
		{
			String regex = "($|.*[\\. \\?!;]+)" + name+ "[\\. \\?!;]+.*"; 
			//System.out.println(regex);
			
			if (text.matches(regex))
			{
				locationURIs.add(new URIImpl(geobase + placeNames.get(name) + "/"));
				System.out.println("Found place: " + name);
			
				text = text.replace(name, ""); //remove from text to prevent a rematch
			}
		}
	
		return locationURIs;
	}

	/**
	 * Method to remove common sentences which prevent proper location detection
	 * @return
	 */
	
	public static String removeCommonSentences(String text)
	{
		text = text.replace("Bent u ", "");
		text = text.replace("Wie heeft ", "");
		text = text.replace("Nederland ", ""); //duh
		text = text.replace("Postbank", ""); 
		text = text.replace("Aan wie ", "");	text = text.replace("aan wie ", "");
		text = text.replace("Partij voor de Dieren", "");
		text = text.replace("Acht u ", "");
		
		return text;
	}
}
