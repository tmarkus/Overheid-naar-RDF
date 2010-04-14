package polii.aggregators.bekendmakingen;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.XSD;

import polii.exceptions.ParliamentMemberNotFound;
import polii.interpreter.politician.LastNameToPolitician;
import virtuoso.sesame2.driver.VirtuosoRepository;

public class Bekendmakingen {

	public static String base = "https://zoek.officielebekendmakingen.nl/";
	private static String propertyBase = "http://politiek.thomasmarkus.nl/ontology/properties/";
	private Repository myRepository;
	private ValueFactory vf;
	private static final Resource[] context = new Resource[1];
	private RepositoryConnection con;
	private Tidy parser;
	
	
	public Bekendmakingen() throws RepositoryException
	{
		myRepository =  new VirtuosoRepository("jdbc:virtuoso://192.168.1.30:1111","dba","dba");
		vf = myRepository.getValueFactory();
		context[0] = vf.createURI("http://thomasmarkus.nl/context/test");
		con = myRepository.getConnection();

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

	/**
	 * Retrieve documents for the past week by default
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws JaxenException
	 * @throws RepositoryException
	 */
	
	public void update() throws URISyntaxException, IOException, SAXException, XPathExpressionException, JaxenException, RepositoryException
	{
		//create date object representing NOW
		DateTime now = new DateTime();
		
		// create date object representing 1 week or something back
		DateTime past = new DateTime();
		past = past.minusWeeks(1);
		
		update(past, now);
	}
	
	/**
	 * Retrieve documents in the specified date range
	 * @param startDate
	 * @param endDate
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws JaxenException
	 * @throws RepositoryException
	 */

	public void update(DateTime startDate, DateTime endDate) throws URISyntaxException, IOException, SAXException, XPathExpressionException, JaxenException, RepositoryException
	{
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

		boolean getNextPage = true;
		int page_nr = 0;

		while(getNextPage)
		{
			page_nr++;
			String url = "https://zoek.officielebekendmakingen.nl/zoeken/resultaat/?zkt=Uitgebreid&pst=ParlementaireDocumenten&dpr=AnderePeriode&spd="+startDate.toString(fmt)+"&epd="+endDate.toString(fmt)+"&kmr=EersteKamerderStatenGeneraal|TweedeKamerderStatenGeneraal|VerenigdeVergaderingderStatenGeneraal&sdt=KenmerkendeDatum&par=AanhangselvandeHandelingen|Kamervragenzonderantwoord&dst=Opgemaakt|Opgemaakt+na+onopgemaakt&isp=true&pnr=1&rpp=10&_page="+page_nr+"&sorttype=1&sortorder=4";

			System.out.println(url);

			InputStream in = new URL(url).openStream();

			Tidy parser = new Tidy(); // obtain a new Tidy instance
			parser.setShowWarnings(false);
			parser.setShowErrors(0);
			parser.setQuiet(true);
			parser.setMakeClean(true);
			Document doc = parser.parseDOM(in, null);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr  = xpath.compile("//a[@class='hyperlink']");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++) {
				String link = nodes.item(i).getAttributes().getNamedItem("href").getNodeValue();

				//extract ID from URL
				Pattern p = Pattern.compile("/(.*).html");
				Matcher m = p.matcher(link);

				String xml_link = null;

				if (m.find()) {
					xml_link = base + m.group(1) + ".xml";
				}

				//call processDocument XML
				if (xml_link != null) System.out.println(xml_link);
				processKamervraagXML(xml_link);
			}
		
			//stop requesting pages if there are no more useable hyperlinks on it
			if (nodes.getLength() == 0) {
				System.out.println("No more useable hyperlinks found! Stopping...");
				getNextPage = false;
			}
		
		}
	}

	@SuppressWarnings("unchecked")
	private void processKamervraagXML(String link) throws MalformedURLException, IOException, XPathExpressionException, JaxenException, RepositoryException
	{
		InputStream in = new URL(link).openStream();
		Document doc = parser.parseDOM(in, null);

		//extract kamervraagnummer
		DOMXPath xpath = new DOMXPath("//kamervraagnummer");
		Node number = (Node) xpath.selectSingleNode(doc);
		String kamervraagnummer = number.getFirstChild().getNodeValue();
		String ikregeer_id = "http://ikregeer.nl/document/V" + kamervraagnummer; 
		URI ikregeer_uri = vf.createURI(ikregeer_id);
		
		//welke achternamen zijn er allemaal?
		xpath = new DOMXPath("//kamervraagomschrijving/naam/achternaam");
		List<Node> lastnames = xpath.selectNodes(doc);

		LastNameToPolitician convertor = new LastNameToPolitician();

		for(Node node : lastnames)
		{
			System.out.println(node.getFirstChild().getNodeValue());
			try {
				URI member_uri = convertor.getPolicicianByLastName(node.getFirstChild().getNodeValue());
				con.add(ikregeer_uri, vf.createURI(propertyBase + "asksQuestion"), member_uri, context);
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (ParliamentMemberNotFound e) {
				System.out.println("Name could not be resolved");
			}
		}

		//onderwerp
		xpath = new DOMXPath("//kamervraagonderwerp");
		List<Node> subjects = xpath.selectNodes(doc);

		for(Node node : subjects)
		{
			String subject = node.getFirstChild().getNodeValue();
			con.add(ikregeer_uri, vf.createURI(DC_11.description.toString()), vf.createLiteral(subject), context);
		}

		xpath = new DOMXPath("//tekstregel[@inhoud='kameraanduiding']");
		Node kamer = (Node) xpath.selectSingleNode(doc);
		String kameraanduiding = kamer.getFirstChild().getNodeValue();
		con.add(ikregeer_uri, vf.createURI(propertyBase + "Orgaan"), vf.createLiteral(kameraanduiding), context);
		
		xpath = new DOMXPath("//tekstregel[@inhoud='documenttype']");
		Node type = (Node) xpath.selectSingleNode(doc);
		String typeDocument = type.getFirstChild().getNodeValue();
		con.add(ikregeer_uri, vf.createURI(DC.type.toString()), vf.createLiteral(typeDocument), context);
		
		xpath = new DOMXPath("//meta[@name='OVERHEIDop.datumIndiening']");
		Node indiening = (Node) xpath.selectSingleNode(doc);
		String indieningDatum = indiening.getAttributes().getNamedItem("content").getNodeValue();
		con.add(ikregeer_uri, vf.createURI(DCTerms.dateSubmitted.toString()), vf.createLiteral(indieningDatum, XSD.date.toString()), context);
		
		xpath = new DOMXPath("//meta[@name='DCTERMS.available']");
		Node available = (Node) xpath.selectSingleNode(doc);
		String availableDatum = available.getAttributes().getNamedItem("content").getNodeValue();
		con.add(ikregeer_uri, vf.createURI(DCTerms.available.toString()), vf.createLiteral(availableDatum, XSD.date.toString()), context);
		
		System.out.println("------------------------------------");
	}
}
