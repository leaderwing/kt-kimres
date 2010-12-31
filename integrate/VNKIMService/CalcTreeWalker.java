// $ANTLR 2.7.6 (2005-12-22): "QueryGrammar.g" -> "CalcTreeWalker.java"$
package org.me.VNKIMService;

import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.NoViableAltException;



public class CalcTreeWalker extends antlr.TreeParser       implements QueryGrammarParserTokenTypes
 {
public CalcTreeWalker() {
	tokenNames = _tokenNames;
}

	public final float  expr(AST _t) throws RecognitionException {
		float r;
		
		AST i = null;
		
			float a,b;
			r=0;
		
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PLUS:
			{
				AST __t11 = _t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t11;
				_t = _t.getNextSibling();
				r = a+b;
				break;
			}
			case STAR:
			{
				AST __t12 = _t;
				match(_t,STAR);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t12;
				_t = _t.getNextSibling();
				r = a*b;
				break;
			}
			case INT:
			{
				i = (AST)_t;
				match(_t,INT);
				_t = _t.getNextSibling();
				r = (float)Integer.parseInt(i.getText());
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"RW",
		"CONJ",
		"IE",
		"UE",
		"WS",
		"PLUS",
		"STAR",
		"INT"
	};
	
	}
	
