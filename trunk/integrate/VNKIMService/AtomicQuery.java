/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.VNKIMService;

public class AtomicQuery {
	private QueryBuffer query;
	private RelationType nextReal;

	public QueryBuffer getQuery() {
		return query;
	}

	public void setQuery(QueryBuffer query) {
		this.query = query;
	}

	public RelationType getNextReal() {
		return nextReal;
	}

	public void setNextReal(RelationType nextReal) {
		this.nextReal = nextReal;
	}
}

