package org.lmarin.commons.utils.jung;

import edu.uci.ics.jung.graph.Graph;

/**
 * Command which modify Graph model.
 * @author Jean-Pascal THIERY
 *
 * @param <V> Vertice type
 * @param <E> Edge type
 * @see GraphUpdater
 */
public interface GraphCommandUpdater<V, E> {
	
	public void execute(Graph<V, E> graph);
	
}