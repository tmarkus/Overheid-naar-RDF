package utils.xml;

import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CommonXMLUtils {

	
	public static String getFullText(Node textNode)
	{
		String text = "";
		
		for(int i=0; i < textNode.getChildNodes().getLength(); i++)
		{
			text += textNode.getChildNodes().item(i).getNodeValue();
		}
		return text;
	}
	
	public static String getFullText(Document doc)
	{
		//extract full text from XML
		DOMXPath xpath = null;
		try {
			xpath = new DOMXPath("//*']");
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		List<Node> nodes = null;
		try {
			nodes = xpath.selectNodes(doc);
		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String textContents = "";
		for(Node node : nodes)
		{
			if (node.getFirstChild() != null ) textContents += node.getFirstChild().getNodeValue() + " ";
			else textContents += node.getNodeValue();
		}
	
		return textContents;
	}
	
	
	
}
