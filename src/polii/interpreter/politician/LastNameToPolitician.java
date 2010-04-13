package polii.interpreter.politician;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import polii.exceptions.ParliamentMemberNotFound;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

import eu.ltfll.wp6.utils.GenericSesameQueryExecutor;

import virtuoso.sesame2.driver.VirtuosoRepository;

public class LastNameToPolitician {

	private Repository myRepository;
	private ValueFactory vf;
	private static final Resource[] context = new Resource[1];
	private RepositoryConnection con;
	
	public LastNameToPolitician() throws RepositoryException
	{
		myRepository =  new VirtuosoRepository("jdbc:virtuoso://192.168.1.30:1111","dba","dba");
		vf = myRepository.getValueFactory();
		context[0] = vf.createURI("http://thomasmarkus.nl/context/test");
		con = myRepository.getConnection();
	}
	
	/**
	 * Try to retrieve a politician URI by just considering the lastname
	 * @param lastname
	 * @return
	 * @throws ParliamentMemberNotFound
	 */
	
	public URI getPolicicianByLastName(String lastname) throws ParliamentMemberNotFound
	{
		Query query = new Query();
		query.setQuerySelectType();
		query.setLimit(2);
		query.addResultVar("uri");
		Node var = Node.createVariable("uri");
		Node lastnameVar = Node.createVariable("lastname");
		
		ElementTriplesBlock triple = new ElementTriplesBlock();
		triple.addTriple(new Triple(var,Node.createURI(FOAF.surname.toString()),lastnameVar));

		Expr filterExpression = new E_Regex(new ExprVar(lastnameVar), lastname, "i"); 
		ElementFilter filter = new ElementFilter(filterExpression);
		
		ElementGroup body = new ElementGroup();
		body.addElement(triple);
		body.addElement(filter);
		query.setQueryPattern(body);
		
		TupleQueryResult result = GenericSesameQueryExecutor.runSparqlQuery(con, query.toString());
		
		int count = 0;
		URI member = null;
		
		try {
			while(result.hasNext())
			{
				count += 1;
				member = vf.createURI(result.next().getValue("uri").stringValue());
			}
		} catch (QueryEvaluationException e) {
			throw new ParliamentMemberNotFound();
		}
		
		if (count != 1) throw new ParliamentMemberNotFound();
		return member;
	}
	
}
