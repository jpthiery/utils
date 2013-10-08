package org.lmarin.commons.utils.neo4j;

import org.lmarin.commons.utils.neo4j.annotation.Identifier;

public class BeanTestIdentifier {

	@Identifier
	private Long id;

	public BeanTestIdentifier() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
