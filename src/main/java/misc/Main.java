package misc;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.GraphUtil;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;


public class Main {

	public static void main(String[] args) throws RDFParseException, RepositoryException, IOException {
		final File resultfile1 = new File("results/query_1.result");
		
		final File resultfile3 = new File("results/query_3.result");
		
		RepositoryManager repositoryManager = new RemoteRepositoryManager("http://localhost:7200");
		repositoryManager.init();
		TreeModel graph = new TreeModel();
		InputStream config = Main.class.getResourceAsStream("/repo-defaults.ttl");
		RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
		rdfParser.setRDFHandler(new StatementCollector(graph));
		rdfParser.parse(config, RepositoryConfigSchema.NAMESPACE);
		config.close();
		Resource repositoryNode = GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);
		RepositoryConfig repositoryConfig = RepositoryConfig.create(graph, repositoryNode);
		repositoryManager.removeRepository("SailSensor-repo");
		repositoryManager.addRepositoryConfig(repositoryConfig);
		
		Repository repository = repositoryManager.getRepository("SailSensor-repo");
		RepositoryConnection repositoryConnection = repository.getConnection();
		InputStream str1 = Main.class.getResourceAsStream("/final.owl");
		repositoryConnection.add(str1, "urn:base", RDFFormat.RDFXML);

		String OverlapingActivities_query = "PREFIX sa:<http://www.sensoractivities.org/>\r\n" + 
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"select ?act1 ?start1 ?end1 ?act2 ?start2 ?end2 where {\r\n" + 
				"    ?act1 rdf:type sa:activity;\r\n" + 
				"          sa:start ?start1;\r\n" + 
				"          sa:end ?end1.\r\n" + 
				"    ?act2 rdf:type sa:activity;\r\n" + 
				"          sa:start ?start2;\r\n" + 
				"          sa:end ?end2.\r\n" + 
				"    filter(( ?start1<=?start2 && ?start2<?end1 && ?end1<?end2)|| (?start2<=?start1 && ?start1<?end2 && ?end2<?end1 )  )  .\r\n" + 
				"    filter(?act1 != ?act2)\r\n" + 
				"}";
		String UpdatedActivities_query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX sa:<http://www.sensoractivities.org/>\r\n" + 
				"PREFIX : <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"\r\n" + 
				"select ?s ?start ?end where { \r\n" + 
				"	?s rdf:type sa:activity .\r\n" + 
				"    ?s sa:end ?end;\r\n" + 
				"       sa:start ?start\r\n" + 
				"} \r\n" + 
				"";
		String ModifyActivities_query = "PREFIX sa:<http://www.sensoractivities.org/>\r\n" + 
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"PREFIX : <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
				"DELETE {\r\n" + 
				"    ?act2 sa:end ?end2 .\r\n" + 
				"    ?act1a sa:end ?end1a .\r\n" + 
				"}	\r\n" + 
				"INSERT {\r\n" + 
				"    ?act2 sa:end ?start1.\r\n" + 
				"    ?act1a sa:end ?start2a\r\n" + 
				"}WHERE {\r\n" + 
				"    ?act1 rdf:type sa:activity;\r\n" + 
				"          sa:end ?end1;\r\n" + 
				"          sa:start ?start1.\r\n" + 
				"    ?act2 sa:start ?start2;\r\n" + 
				"          rdf:type sa:activity;\r\n" + 
				"          sa:end ?end2.\r\n" + 
				"    ?act1a rdf:type sa:activity;\r\n" + 
				"           sa:end ?end1a;\r\n" + 
				"           sa:start ?start1a.\r\n" + 
				"    ?act2a sa:start ?start2a;\r\n" + 
				"           rdf:type sa:activity;\r\n" + 
				"           sa:end ?end2a.\r\n" + 
				"    filter((?start2<=?start1 && ?start1<?end2 && ?end2<?end1 &&?act1 != ?act2)) .\r\n" + 
				"    filter(( ?start1a<=?start2a && ?start2a<?end1a && ?end1a<?end2a &&?act1a != ?act2a)) .\r\n" + 
				"}";
		
		TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, OverlapingActivities_query);
		TupleQueryResult tupleQueryResult = tupleQuery.evaluate();

		
		
		
		try {
			FileWriter filewriter = new FileWriter(resultfile1);
			filewriter.write("-----Overlapping activities-----\n\n");
			while (tupleQueryResult.hasNext()) {
				BindingSet bindingSet = tupleQueryResult.next();
				filewriter.write("\n");
				for (Binding binding : bindingSet) {
					
					String name = binding.getName();
					Value value = binding.getValue();
					System.out.println(name + " = " + value);
					filewriter.write(name + " = " + value + "\n");
				}
			}
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TupleQuery tupleQuery1 = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, ModifyActivities_query);
		tupleQuery1.evaluate();

		TupleQuery tupleQuery2 = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, UpdatedActivities_query);
		TupleQueryResult tupleQueryResult2 = tupleQuery2.evaluate();
		try {
			FileWriter filewriter = new FileWriter(resultfile3);
			filewriter.write("----Updated Activities----\r\n" + 
					"\n\n");
			while (tupleQueryResult2.hasNext()) {
				BindingSet bindingSet = tupleQueryResult2.next();
				
				filewriter.write("\n");
				for (Binding binding : bindingSet) {
					
					String name = binding.getName();
					Value value = binding.getValue();
//					System.out.println(name + " = " + value);
					filewriter.write(name + " = " + value + "\n");
				}
			}
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		repositoryConnection.close();
		repository.shutDown();
		repositoryManager.shutDown();

	}

}
