package org.me.VNKIMService;
// $ANTLR 2.7.6 (2005-12-22): "QueryGrammar.g" -> "QueryGrammarParser.java"$

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class QueryGrammarParser extends antlr.LLkParser       implements QueryGrammarParserTokenTypes
 {

protected QueryGrammarParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public QueryGrammarParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected QueryGrammarParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public QueryGrammarParser(TokenStream lexer) {
  this(lexer,2);
}

public QueryGrammarParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void query() throws RecognitionException, TokenStreamException {
		
		
		if ((LA(1)==IE||LA(1)==UE) && (LA(2)==EOF)) {
			entity();
		}
		else if ((LA(1)==IE||LA(1)==UE) && ((LA(2) >= RW && LA(2) <= UE))) {
			entity();
			querycomp();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
	}
	
	public final void entity() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case IE:
		{
			match(IE);
			break;
		}
		case UE:
		{
			match(UE);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void querycomp() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case IE:
		case UE:
		{
			entity();
			break;
		}
		case RW:
		{
			match(RW);
			query();
			break;
		}
		default:
			if ((LA(1)==CONJ) && (LA(2)==IE||LA(2)==UE)) {
				match(CONJ);
				query();
			}
			else if ((LA(1)==CONJ) && (LA(2)==RW)) {
				match(CONJ);
				match(RW);
				query();
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
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
