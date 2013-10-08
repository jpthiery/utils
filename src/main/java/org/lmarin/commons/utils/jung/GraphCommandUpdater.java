package org.lmarin.commons.utils.jung;

import edu.uci.ics.jung.graph.Graph;

public interface GraphCommandUpdater<V, E> {
	
	public void execute(Graph<V, E> graph);
	
}