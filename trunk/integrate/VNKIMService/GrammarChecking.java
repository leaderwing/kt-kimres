package org.me.VNKIMService;
import java.io.*;

class GrammarChecking
{
	public static boolean CheckGrammar(String query) throws Exception
	{
	 	DataInputStream datainput = new DataInputStream(new ByteArrayInputStream(query.getBytes()));
		boolean result = false;
		try
		{
			QueryGrammarLexer lexer = new QueryGrammarLexer(datainput);
			QueryGrammarParser parser = new QueryGrammarParser(lexer);
			parser.query();
			result = true;
		}
		catch(Exception e) 
		{
			result = false;
		}
		return result;
	}
}
