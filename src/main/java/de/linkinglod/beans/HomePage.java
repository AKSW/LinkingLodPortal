package de.linkinglod.beans;

import java.text.NumberFormat;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

import de.linkinglod.service.LLProp;

/**
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class HomePage {

	private Integer numFrameworks;
	private Integer numMappings;
	private Integer numDatasets;
	private Integer numLinkTypes;
	private Integer numLinks;
	private Integer numTriples;
	
	private String endpoint = LLProp.getString("TripleStore.endpoint");
	private String graph = LLProp.getString("TripleStore.graph");

	public String getNumFrameworks() {
		String query = "select (count(*) as ?c) where {" +
				"?x a <http://www.linklion.org/ontology#LDFramework> ." +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		numFrameworks = results.next().getLiteral("c").getInt();
		return NumberFormat.getInstance().format(numFrameworks);
	}


	public String getNumMappings() {
		String query = "select (count(*) as ?c) where {" +
				"?x a <http://www.linklion.org/ontology#Mapping> ." +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		numMappings = results.next().getLiteral("c").getInt();
		return NumberFormat.getInstance().format(numMappings);
	}


	public String getNumDatasets() {
		String query = "select (count(*) as ?c) where {" +
				"?x a <http://rdfs.org/ns/void#Dataset> ." +
				"?x <http://rdfs.org/ns/void#uriSpace> ?uri ." +
				"FILTER isURI(?uri)" +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		numDatasets = results.next().getLiteral("c").getInt();
		return NumberFormat.getInstance().format(numDatasets);
	}


	public String getNumLinks() {
		String query = "select (count(*) as ?c) where {" +
				"?x a <http://www.linklion.org/ontology#Link> ." +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		numLinks = results.next().getLiteral("c").getInt();
		return NumberFormat.getInstance().format(numLinks);
	}

	public String getNumLinkTypes() {
		String query = "select (count(distinct ?o) as ?c) where {" +
				"?x a <http://www.linklion.org/ontology#Link> ." +
				"?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ?o ." +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		numLinkTypes = results.next().getLiteral("c").getInt();
		return NumberFormat.getInstance().format(numLinkTypes);
	}


	public String getNumTriples() {
		String query = "select (count(*) as ?c) where {" +
				"?s ?p ?o ." +
				"}";
		Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
		ResultSet results = qexec.execSelect();
		numTriples = results.next().getLiteral("c").getInt();
		return NumberFormat.getInstance().format(numTriples);
	}

}