package org.lmarin.commons.utils.neo4j;

import org.junit.Assert;
import org.junit.Test;

public class BeanIdentifierAdapterTest {

	@Test
	public void adapterTest() {
		
		BeanTestIdentifier bean = new BeanTestIdentifier();
		
		Identifiable<Long> proxy = AnnotedBeanIdentfiableHandler.createIdentifiable(bean);
		
		Long value = Long.valueOf(30L);
		proxy.setIdentifier(value);
		
		Assert.assertEquals(value, proxy.getIdentifier());
		
	}
	
}
 