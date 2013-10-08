package org.lmarin.commons.utils.neo4j.spring;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class GraphDbFactory extends AbstractFactoryBean<GraphDatabaseService> {

	private final Log log = LogFactoryImpl.getLog(GraphDbFactory.class);
	
	private final String path;	
	
	public GraphDbFactory(String path) {
		super();
		if (StringUtils.isNotBlank(path)) {
			throw new IllegalArgumentException("path can't be empty.");
		}
		this.path = path;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Class<?> getObjectType() {
		return GraphDatabaseService.class;
	}

	@Override
	protected GraphDatabaseService createInstance() throws Exception {
		return new GraphDatabaseFactory().newEmbeddedDatabase(path);
	}

	@Override
	public void destroy() throws Exception {
		GraphDatabaseService graphDb = getObject();
		if (graphDb != null) {
			graphDb.shutdown();
			if (log.isTraceEnabled()) {
				log.trace("Shutdown graph database.");
			}
		}
		super.destroy();
	}


}
