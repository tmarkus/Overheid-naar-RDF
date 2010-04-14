package polii.aggregators.ikregeer;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.xpath.XPathExpressionException;

import org.jaxen.JaxenException;
import org.openrdf.repository.RepositoryException;
import org.xml.sax.SAXException;

import polii.aggregators.bekendmakingen.Bekendmakingen;

public class IkRegeerRunner {

	/**
	 * @param args
	 * @throws RepositoryException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws JaxenException 
	 * @throws XPathExpressionException 
	 */
	public static void main(String[] args) throws RepositoryException, XPathExpressionException, JaxenException, URISyntaxException, IOException, SAXException {

		IkRegeerToRDF aggregator = new IkRegeerToRDF();
		Bekendmakingen bekenmakingen = new Bekendmakingen();
		
		bekenmakingen.update();
		
		//aggregator.updateDocuments();
		//aggregator.updateParliament();
		
	}

}
