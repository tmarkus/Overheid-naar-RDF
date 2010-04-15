package utils.convert.geonames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polii.interpreter.placename.PlacenameToURI;

public class Geonames2RDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new PlacenameToURI().getPlacenameURIs("Dit is een test in Doorn en niet in Wijk bij Duurstede");
		
		
	}

	public static Map<String, Integer> getNames()
	{
	
		Map<String, Integer> placeNames = new HashMap<String, Integer>();
		
		try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		    File aFile = new File("/Users/tmarkus/Downloads/nl/NL.txt");  
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
		      try {
		        String line = null; //not declared within while loop
		        /*
		        * readLine is a bit quirky :
		        * it returns the content of a line MINUS the newline.
		        * it returns null only for the END of the stream.
		        * it returns an empty String if two newlines appear in a row.
		        */
		        while (( line = input.readLine()) != null){
		          placeNames.put(line.split("\t")[1], new Integer(line.split("\t")[0]));
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		
		return placeNames;
	}
	
	
}
