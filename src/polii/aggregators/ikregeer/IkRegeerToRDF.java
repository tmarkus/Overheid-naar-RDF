package polii.aggregators.ikregeer;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import polii.aggregators.ikregeer.dao.KamervraagListItem;
import virtuoso.sesame2.driver.VirtuosoRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import eu.ltfll.wp6.utils.GenericSesameQueryExecutor;

public class IkRegeerToRDF {


	private Gson gson;
	private String apikey;

	public IkRegeerToRDF()
	{
		Properties configFile = new Properties();
		/*
		try {
			configFile.load(this.getClass().getClassLoader().getResourceAsStream("/settings.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		gson = new GsonBuilder().create();
		apikey = "?apikey=e4de79149beddb449617e49be8fbb9e4a49fb987";
	}

	/**
	 * Get the json string from some URL
	 * @param url
	 * @return
	 */

	private String getJson(String url)
	{
		//get url from ikregeer
		//from: http://wiki.apache.org/HttpComponents/QuickStart
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpResponse response = null;
		HttpEntity entity = null;

		HttpPost httpost = new HttpPost(url);

		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entity = response.getEntity();

		//System.out.println("Login form get: " + response.getStatusLine());

		//parse json
		try {
			//entity.consumeContent();

			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			String data = writer.toString();

			return data;
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	public void updateParliament()
	{
		//store all the names of parliament members
		Repository myRepository =  new VirtuosoRepository("jdbc:virtuoso://192.168.1.30:1111","dba","dba");
		ValueFactory vf = myRepository.getValueFactory();

		Resource[] context = new Resource[1];
		context[0] = vf.createURI("http://thomasmarkus.nl/context/test");
		
		try {
		
			RepositoryConnection con = myRepository.getConnection();

			String url = "http://api.ikregeer.nl/samenstelling/tweedekamer/2010-04-11" + apikey;
			String jsonString = getJson(url);

			Type collectionType = new TypeToken<LinkedList<HashMap<String, String>>>(){}.getType();
			LinkedList<HashMap<String, String>> members = gson.fromJson(jsonString, collectionType);  

			for(HashMap<String, String> member : members)
			{
				//System.out.println(item.getDocumentId());
				
				String firstname = member.get("voornaam");
				String lastname = member.get("achternaam");
				String party = member.get("partij");
				
				URI member_uri = new URIImpl("http://thomasmarkus.nl/politiek/kamerleden/" + firstname + "_" + lastname.replace(" ", "_") + "_" + party.replace(" ", "_"));
				URI party_uri = new URIImpl("http://rdf.thomasmarkus.nl/politiek/partijen/" + party.replace(" ", "_"));
				//add some rdf to the repository based on the list item
				
				//document title
				con.add(member_uri, vf.createURI(FOAF.firstName.toString()), vf.createLiteral(firstname), context);
				con.add(member_uri, vf.createURI(FOAF.surname.toString()), vf.createLiteral(lastname), context);
				con.add(member_uri, vf.createURI(RDFS.member.toString()), party_uri, context);

				//execute a sparql query to check if the data is stored in the repository or not
				//GenericSesameQueryExecutor.runSparqlQuery(myRepository.getConnection(), "");

			}

		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}

		
		
		//firstname
		//lastname
		//party
		
	}
	
	/**
	 * Get new information through the ikregeer API and add it to the repository
	 */

	public void updateDocuments()
	{
		Repository myRepository =  new VirtuosoRepository("jdbc:virtuoso://192.168.1.30:1111","dba","dba");
		ValueFactory vf = myRepository.getValueFactory();

		Resource[] context = new Resource[1];
		context[0] = vf.createURI("http://thomasmarkus.nl/context/test");
		
		try {
		
			RepositoryConnection con = myRepository.getConnection();

			String url = "http://api.ikregeer.nl/zoek/kamervragen/5" + apikey;
			String jsonString = getJson(url);

			KamervraagListItem[] kamervragenList = gson.fromJson(jsonString, KamervraagListItem[].class);  


			for(KamervraagListItem item : kamervragenList)
			{
				//System.out.println(item.getDocumentId());
				System.out.println("Getting new data about document from: " + item.getDocumentJSON());
				
				String documentJSONString = getJson(item.getDocumentJSON().stringValue() + apikey);
				
				Type collectionType = new TypeToken<ArrayList<ArrayList<HashMap<String,String>>>>(){}.getType();
				
				LinkedList<LinkedList<HashMap<String,String>>> documents = gson.fromJson(documentJSONString, collectionType);
				
				String title = documents.get(0).get(0).get("bibliografische_omschrijving");
				String id = documents.get(0).get(0).get("naam");
				String document_url = documents.get(0).get(0).get("document_url");
				String datum_publicatie = documents.get(0).get(0).get("datum_publicatie");
				String vindplaats = documents.get(0).get(0).get("vindplaats");
				
				//System.out.println(documents.get(0).get(0).keySet());
				
				//add some rdf to the repository based on the list item
				
				//document title
				con.add(vf.createURI(document_url), vf.createURI(DC.identifier.toString()), vf.createLiteral(id), context);
				con.add(vf.createURI(document_url), vf.createURI(DC.description.toString()), vf.createLiteral(title), context);
				con.add(vf.createURI(document_url), vf.createURI(DCTerms.available.toString()), vf.createLiteral(datum_publicatie), context);
				con.add(vf.createURI(document_url), vf.createURI(DCTerms.bibliographicCitation.toString()), vf.createLiteral(vindplaats), context);
				
				System.out.println(DC.identifier);
				

				//execute a sparql query to check if the data is stored in the repository or not
				//GenericSesameQueryExecutor.runSparqlQuery(myRepository.getConnection(), "");

			}

		} catch (RepositoryException e1) {
			e1.printStackTrace();
		}
	}
}
