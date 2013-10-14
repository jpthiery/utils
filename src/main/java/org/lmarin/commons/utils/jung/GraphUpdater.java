package org.lmarin.commons.utils.jung;

import java.util.List;

import javax.swing.SwingUtilities;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;

/**
 * Allow to update Graph model in good conditions using {@link Relaxer}, running in EDT.
 * @author Jean-Pascal THIERY
 *
 * @param <V> Vertice type
 * @param <E> Edge type
 */
public class GraphUpdater<V, E> {

	private final Graph<V, E> graph;

	private final AbstractLayout<V, E> layout;

	private final Relaxer relaxer;

	public GraphUpdater(Graph<V, E> graph, AbstractLayout<V, E> layout, Relaxer relaxer) {
		super();
		if (graph == null) {
			throw new IllegalArgumentException("graph can't be null.");
		}
		if (layout == null) {
			throw new IllegalArgumentException("layout can't be null.");
		}
		if (relaxer == null) {
			throw new IllegalArgumentException("relaxer can't be null.");
		}

		this.graph = graph;
		this.layout = layout;
		this.relaxer = relaxer;
	}

	/**
	 * Execute a bulk of update on graph model.
	 * @param commands List of command to done on one shot.
	 */
	public final void executeUpdate(final List<GraphCommandUpdater<V, E>> commands) {
		Runnable cmd = new Runnable() {
			@Override
			public void run() {

				layout.lock(true);
				relaxer.pause();

				for (GraphCommandUpdater<V, E> cmd : commands) {
					cmd.execute(graph);
				}

				layout.initialize();
				relaxer.resume();
				layout.lock(false);

			}
		};

		if (SwingUtilities.isEventDispatchThread()) {
			cmd.run();
		} else {
			SwingUtilities.invokeLater(cmd);
		}
	}

}
