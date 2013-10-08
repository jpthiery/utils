package org.lmarin.commons.utils.neo4j.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class TestGraphDbFactory extends AbstractFactoryBean<GraphDatabaseService> {

	private final Log log = LogFactory.getLog(TestGraphDbFactory.class);

	public TestGraphDbFactory() {
		super();
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
		return new TestGraphDatabaseFactory().newImpermanentDatabase();
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
