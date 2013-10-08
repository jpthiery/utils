package org.lmarin.commons.utils.neo4j;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * 
 * @author Jean-Pascal THIERY
 *
 */
public class GraphTemplate {

	private final GraphDatabaseService graph;

	private final ThreadLocal<ExecutionEngine> engine = new ThreadLocal<ExecutionEngine>() {
		protected ExecutionEngine initialValue() {
			return new ExecutionEngine(graph);
		};
	};

	public GraphTemplate(GraphDatabaseService graph) {
		super();
		if (graph == null) {
			throw new IllegalArgumentException("graph can't be null.");
		}
		this.graph = graph;
	}

	public <T> T execute(GraphCallback<T> callback) {
		T res = null;

		Transaction tx = graph.beginTx();
		try {

			res = callback.execute(graph);
			tx.success();

		} finally {
			tx.finish();
		}

		return res;
	}

	public ExecutionResult executeCypher(String query, Map<String, Object> params) {

		return engine.get().execute(query, params);

	}

	public ExecutionResult executeCypher(String query) {

		return engine.get().execute(query);

	}

}
