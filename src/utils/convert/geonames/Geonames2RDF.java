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

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;

import polii.base.VirtuosoConnector;
import polii.interpreter.placename.PlacenameToURI;

public class Geonames2RDF extends VirtuosoConnector {

	protected final static String geobase = "http://sws.geonames.org/";
	protected final static String wgs84_pos = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
	public Geonames2RDF() throws RepositoryException {
		super();
	}

	/**
	 * @param args
	 * @throws RepositoryException 
	 */
	public static void main(String[] args) throws RepositoryException {

		//test the placename extraction
		new PlacenameToURI().getPlacenameURIs("Dit is een test in Doorn en niet in Wijk bij Duurstede.");
	
		//store all of the geonames in the rdf-store with the proper format
		new Geonames2RDF().storeGeonames2RDF();
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
	
	/**
	 * Read all the filenames in a geonames file and store them in the RDF store
	 * @throws RepositoryException
	 */
	
	public void storeGeonames2RDF() throws RepositoryException
	{
		//read the file and convert all of the fields to RDF statements and add them to the repository (look at the existing spec)
		try {
		    File aFile = new File("/Users/tmarkus/Downloads/nl/NL.txt");  
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
		      try {
		        String line = null; //not declared within while loop

		        while (( line = input.readLine()) != null){
		          String[] splitted = line.split("\t");

		          URI uri = vf.createURI(geobase + splitted[0]);
		          Value name = vf.createLiteral(splitted[1]);
		          Value latitude = vf.createLiteral(splitted[4]);
		          Value longtitude = vf.createLiteral(splitted[5]);
		          
		          con.add(uri, vf.createURI(geobase + "name"), name, context);
		          con.add(uri, vf.createURI(wgs84_pos + "lat"), latitude, context);
		          con.add(uri, vf.createURI(wgs84_pos + "long"), longtitude, context);
		        
		          System.out.println("Stored: " + name);
		        }
		      }
		      finally {
		        input.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
	}
}
