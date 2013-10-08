package org.lmarin.commons.utils.neo4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

public class Neo4jSamplerTest {

	private GraphDatabaseService graph;
	
	@Before
	public void setup() {
		System.out.println("Starting");
		graph = new TestGraphDatabaseFactory().newImpermanentDatabase();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				stop();
			}
		}));
		System.out.println("Started");

	}
	
	@Test
	public void runTest() {

		GraphTemplate graphTemplate = new GraphTemplate(graph);

		String createQuery = "CREATE (nodeA {NAME:'FRED'}), (nodeB {NAME:'GEGE'}) , (nodeA)-[:FRIEND {AGE:10}]->(nodeB)";

		graphTemplate.executeCypher(createQuery);

		String queryNode = "MATCH n WHERE n.NAME='FRED' RETURN n";

		ExecutionResult result = graphTemplate.executeCypher(queryNode); //

//		Iterator<Node> n_column = result.columnAs("n");

//		final Node nodeA = n_column.next();

		String queryPath = "MATCH p=(n)-[r:FRIEND*]->(m) WHERE n.NAME='FRED' AND ALL(x IN r WHERE x.AGE > 2) return p";
		result = graphTemplate.executeCypher(queryPath);
		System.out.println(result.dumpToString());
	}

	@After
	public void stop() {
		if (graph != null) {
			System.out.println("Stopping");
			graph.shutdown();
			graph = null;
			System.out.println("Stopped");
		}
	}

}
