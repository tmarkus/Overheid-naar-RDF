package polii.aggregators.moties;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.hp.hpl.jena.sparql.util.StringUtils;

import polii.base.VirtuosoConnector;
import polii.interpreter.party.PartyNames;
import utils.xml.CommonXMLUtils;

/**
 * In stemming komt: "de motie-Polderman c.s. (21501-32, nr. 319)."
 * Ik constateer dat deze motie met algemene stemmen is aangenomen
 * 
 * In stemming komt de motie-Karabulut (31143, nr. 78).
 * Ik constateer dat de aanwezige leden van de fracties van de SP, GroenLinks en de PvdD voor deze motie hebben gestemd en die van de overige fracties ertegen, zodat zij is verworpen.
 *  
 * Aan de orde zijn de stemmingen in verband met het wetsvoorstel Wijziging van de Wet op het voortgezet onderwijs en de Wet op het hoger onderwijs en wetenschappelijk onderzoek in verband met het uitbreiden van de mogelijkheden om tot leraar te worden benoemd in het voortgezet onderwijs (32270) 
 * In stemming komt het wetsvoorstel.
 * 
 * In stemming komt het gewijzigde amendement-Jasper van Dijk c.s. (stuk nr. 11).
 * 
 * In stemming komt de motie-Dibi (32270, nr. 9).
 * Ik constateer dat de aanwezige leden van de fracties van de SP, GroenLinks en de PvdD voor deze motie hebben gestemd en die van de overige fracties ertegen, zodat zij is verworpen.
 * 
 * 
 * TODO: Hoofdelijk stemmen kunnen parsen
 */



public class MotieStemmerExtractor extends VirtuosoConnector
{

	private Tidy parser;
	
	public MotieStemmerExtractor() throws RepositoryException {
		super();
	
		//parse XML
		parser = new Tidy(); // obtain a new Tidy instance
		parser.setShowWarnings(false);
		parser.setShowErrors(0);
		parser.setQuiet(true);
		parser.setMakeClean(true);
		parser.setXmlTags(true);
		parser.setXmlOut(true);
		parser.setInputEncoding("UTF-8");
	}

	
	public void update() throws MalformedURLException, IOException
	{
		//extract list of stemmingen over moties
		 //URL opzoeken
		
		
		// vraag van ieder document de tekst op
		   //bestaande methode recyclen
		String link = "https://zoek.officielebekendmakingen.nl/h-tk-20092010-72-6141.xml";
		InputStream in = new URL(link).openStream();
		Document doc = parser.parseDOM(in, null);

		String text = CommonXMLUtils.getFullText(doc);
		
		//extraheer mogelijke patronen van de vorm:
		Pattern motieTitle = Pattern.compile("In stemming komt.*?(.*?)\\.( +?[A-Z]{1}|^)(.*?) gestemd");
		Matcher matcher = motieTitle.matcher(text);
		
		
		final int START = 0;
		final int END = 1;

		Map<ArrayList<Integer>,VoteResult> voteresults = new HashMap<ArrayList<Integer>,VoteResult>();  
		
		while(matcher.find())
		{
			ArrayList<Integer> match = new ArrayList<Integer>();
			match.add(matcher.start(1));
			match.add(matcher.end(3));
			
			VoteResult vote = new VoteResult();
			vote.setSubject(text.substring(matcher.start(1), matcher.end(1)));
			voteresults.put(match, vote);
			
			//System.out.println(text.substring(matcher.start(3), matcher.end(3)));    //lijst met partijnamen
		}
		
		//build regular expression for the party names
		Matcher partyNames = Pattern.compile(StringUtils.join("|", PartyNames.partyShortNames())).matcher(text);
		for(Entry<ArrayList<Integer>,VoteResult> regionvote : voteresults.entrySet())
		{
			partyNames.reset(text);
			partyNames.region(regionvote.getKey().get(START), regionvote.getKey().get(END));
			
			while(partyNames.find())
			{
				regionvote.getValue().addYesVote(text.substring(partyNames.start(), partyNames.end()));
				//System.out.println(text.substring(partyNames.start(), partyNames.end()));
			}
			System.out.println(regionvote.getValue().getYesVotes());
		
			//add all the parties that voted NO automatically
			regionvote.getValue().addNoVotersByDefault(PartyNames.partyShortNames());
		}
		
		//process all the voting results!
		for(VoteResult votes : voteresults.values())
		{
			//TODO: convert the subject into a URI  (hint: dossiernummer + nr + suffix ? )
			String subject = votes.getSubject();
			
			for(String yesVoteShortPartyName : votes.getYesVotes())
			{
				//resolve the short party name to a URI
				
				//store the positive vote result
			}
		
			for(String noVoteShortPartyName : votes.getYesVotes())
			{
				//resolve the short party name to a URI
				
				//store the negative vote result
			}
		}
	}
}
