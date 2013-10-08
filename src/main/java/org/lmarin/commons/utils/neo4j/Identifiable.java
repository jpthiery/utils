package org.lmarin.commons.utils.neo4j;

public interface Identifiable<I> {

	I getIdentifier();
	
	void setIdentifier(I value);
	
}
