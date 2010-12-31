/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.VNKIMService;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Minh Dung
 */
public class ListCG {
    private int MaxCG = 10;
	private List<QueryBuffer> listQB;
	private TopRelationType topRel; // COUNT, MIN, MAX
	private String query;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ListCG() {
		listQB = new ArrayList<QueryBuffer>();
		topRel = null;
	}

	public int getMaxCG() {
		return MaxCG;
	}

	public void setMaxCG(int maxCG) {
		MaxCG = maxCG;
	}

	public List<QueryBuffer> getListQB() {
		return listQB;
	}

	public void setListQB(List<QueryBuffer> listQB) {
		this.listQB = listQB;
	}

	public TopRelationType getTopRel() {
		return topRel;
	}

	public void setTopRel(TopRelationType nestedrel) {
		this.topRel = nestedrel;
	}
}
