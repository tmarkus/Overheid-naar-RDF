package polii.aggregators.ikregeer.dao;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class KamervraagListItem {

	private String document_id;
	private String bibliografische_omschrijving;
	private String document_url;
	private String document_json;
	private String document_pdf;
	
	public String getDocumentId()
	{
		return document_id;
	}
	
	public String getBibliografischeOmschrijving()
	{
		return bibliografische_omschrijving;
	}
	
	public URI getDocumentURL()
	{
		return new URIImpl(document_url);
	}

	public URI getDocumentJSON()
	{
		return new URIImpl(document_json);
	}

	public URI getDocumentPDF()
	{
		return new URIImpl(document_pdf);
	}

}
