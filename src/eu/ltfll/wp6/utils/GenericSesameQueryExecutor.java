package eu.ltfll.wp6.utils;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class GenericSesameQueryExecutor {

	
	private static String server = "http://augur.wu-wien.ac.at:8080/wp62/sesame"; 
	
	/**
	 * Generic function to execute a serql query against the given repository
	 * @param repository
	 * @param query
	 * @return
	 */
	
	public static TupleQueryResult runQuery(String repository, String query)
	{
		
		//create connection
			Repository myRepository = new HTTPRepository(server, repository);
			try {
				RepositoryConnection con = myRepository.getConnection();
				
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL, query);
				TupleQueryResult queryResult =  tupleQuery.evaluate();
				return queryResult;
				
				
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
			} catch (MalformedQueryException e) {
				e.printStackTrace();
				return null;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	/**
	 * Generic function to execute a sparql query against the given repository
	 * @param repository
	 * @param query
	 * @return
	 */
	
	public static TupleQueryResult runSparqlQuery(RepositoryConnection con, String query)
	{
		//create connection
			try {
				
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
				TupleQueryResult queryResult =  tupleQuery.evaluate();
				return queryResult;
				
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
			} catch (MalformedQueryException e) {
				e.printStackTrace();
				return null;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	/**
	 * Generic function to execute a sparql ASK query against the given repository
	 * @param repository
	 * @param query
	 * @return
	 */
	
	public static boolean runSparqlASKQuery(RepositoryConnection con, String query)
	{
		//create connection
		try {
			BooleanQuery askQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, query); 
			return  askQuery.evaluate();
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
}
