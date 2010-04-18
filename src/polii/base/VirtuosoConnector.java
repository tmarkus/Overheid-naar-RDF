package polii.base;

import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import virtuoso.sesame2.driver.VirtuosoRepository;

public abstract class VirtuosoConnector {

	public static String base = "https://zoek.officielebekendmakingen.nl/";
	public static String propertyBase = "http://politiek.thomasmarkus.nl/ontology/properties/";
	public static String entityBase = "http://politiek.thomasmarkus.nl/ontology/entities/";
	
	protected Repository myRepository;
	protected ValueFactory vf;
	protected static final Resource[] context = new Resource[1];
	protected RepositoryConnection con;
	
	public VirtuosoConnector() throws RepositoryException
	{
		myRepository =  new VirtuosoRepository("jdbc:virtuoso://192.168.1.30:1111","dba","dba");
		vf = myRepository.getValueFactory();
		context[0] = vf.createURI("http://thomasmarkus.nl/context/test");
		con = myRepository.getConnection();
	}
}
