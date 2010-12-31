package org.me.VNKIMService;

import java.util.Iterator;
import java.io.*;


import com.ontotext.kim.client.*;
import com.ontotext.kim.client.corpora.CorporaAPI;
import com.ontotext.kim.client.corpora.KIMAnnotation;
import com.ontotext.kim.client.corpora.KIMAnnotationSet;
import com.ontotext.kim.client.corpora.KIMDocument;
import com.ontotext.kim.client.corpora.KIMFeatureMap;
import com.ontotext.kim.client.model.FeatureConstants;
import com.ontotext.kim.client.semanticannotation.SemanticAnnotationAPI;
import com.ontotext.kim.client.semanticrepository.SemanticRepositoryAPI;



//class Named Entity Regcognization

public class KIMNER 
{
	
	public static KIMService serviceKim = null;
	public static CorporaAPI apiCorpora = null;
	public static SemanticAnnotationAPI apiSemAnn = null;
	private static SemanticRepositoryAPI apiSesame = null;
	  
	public static boolean ConnectToKimService() throws Exception 
	{
		serviceKim = GetService.from();

		if ( serviceKim != null)
		{
			apiCorpora = serviceKim.getCorporaAPI();
			apiSemAnn = serviceKim.getSemanticAnnotationAPI();
			apiSesame = serviceKim.getSemanticRepositoryAPI();			
			return true;
		}
		return false;
	}

	public static KIMDocument createDocFromText(String content) throws Exception 
	{
	    return apiCorpora.createDocument(content, true);
	}

	public static KIMDocument createDocFromFile(String path) throws Exception 
	{
		FileInputStream fin = new FileInputStream(path);
		String content = "";		
		int totalbytes = fin.available();
		for(int i=0; i < totalbytes; i++)
			content += (char) fin.read();
		fin.close();
	    return apiCorpora.createDocument(content, true);
	}
	
	public static KIMDocument annotateDoc(String content) throws Exception 
	{
		KIMDocument kdoc = createDocFromText(content);
		return apiSemAnn.execute(kdoc);
	}

	public static KIMDocument annotateDoc(KIMDocument kdoc) throws Exception 
	{
		return apiSemAnn.execute(kdoc);
	}

	public static String GetAnnotateXML(String content) throws Exception 
	{	
		String output = "";
		if ( ConnectToKimService())
		{
			KIMDocument kdoc = createDocFromText(content);
			kdoc = annotateDoc(kdoc);
			output = kdoc.toXML();
		}
		else
		{
			output = "Can not connect KIM Server";
		}
		return output;
	}
	
	public static void GetNamedEntity(String query, QueryBuffer buffer) throws Exception
	{
		if ( ConnectToKimService())
		{
			KIMDocument kdoc = createDocFromText(query);
			kdoc = annotateDoc(kdoc);
		    KIMAnnotationSet kimASet = kdoc.getAnnotations();
		    Iterator annIterator = kimASet.iterator();
		    while (annIterator.hasNext()) 
		    {
		      KIMAnnotation kimAnnotation = (KIMAnnotation) annIterator.next();
		      //spit(" = [ Annotation ] : " + kimAnnotation.getId() + "]");
		      KIMFeatureMap kimFeatures = kimAnnotation.getFeatures();
		      if (kimFeatures != null) 
		      {
		    	  String orgname = (String)kimFeatures.get(FeatureConstants.FEATURE_ORIGINAL_NAME);		    	 
		    	  String Id = (String) kimFeatures.get(FeatureConstants.CLASS);
		    	  String className = Id.substring(Id.indexOf('#')+1);
		    	  
		    	  String wordfollow = AnnieER.getWordFollow(query, kimAnnotation.getEndOffset());
		    	  String wordbefore = AnnieER.getWordBefore(query, kimAnnotation.getStartOffset());
		    	  if (className.compareToIgnoreCase("CalendarMonth") != 0 && className.compareToIgnoreCase("TimeInterval") != 0)
		    		  buffer.InsertItem(orgname, className, "IE", kimAnnotation.getStartOffset(), kimAnnotation.getEndOffset(), "K", wordfollow, wordbefore);
		      }
		    }
		}

	}
	
}
