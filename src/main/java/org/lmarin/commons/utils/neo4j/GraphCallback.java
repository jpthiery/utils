package org.lmarin.commons.utils.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * 
 * @author Jean-Pascal THIERY
 *
 * @param <T>
 */
public interface GraphCallback<T> {

	/**
	 * 
	 * @param graphService
	 * @return
	 */
	public T execute(GraphDatabaseService graphService); 
	
}
