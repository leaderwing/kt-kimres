/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.VNKIMService;

/**
 *
 * @author Minh Dung
 */
public class TripleType {
	public ItemType subject = null;
	public ItemType rel = null;
	public ItemType object = null;
	public String relationName = "";
	public int direction = 1;//1: left->right 2: right->left
	public boolean isDelete = false;
}
