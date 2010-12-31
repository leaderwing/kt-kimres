
package org.me.VNKIMService;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.creole.gazetteer.Gazetteer;
import gate.util.GateException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import javax.servlet.*;
public class AnnieER
{
	private static SerialAnalyserController annieController;
	private static boolean gateInited = false;
	//public static String serverpath = "C:\\Tomcat6\\webapps\\QuerytoCG\\";
	
	public static void initGate(ServletContext ctx) throws ServletException, GateException, IOException 
	{
		gateInited=true;
		//initialise the GATE library
		if ( ctx == null)
		{
			File gh = new File("C:/gate-3.1");
			
			Gate.setGateHome(gh);
			Gate.init();
			String gateHome = "file:///C:/J/VNKIMService/web/WEB-INF";

			Gate.getCreoleRegister().registerDirectories(new URL(gateHome+"/gate-plugins/ANNIE"));
			gateInited = true;
		}
		else
		{
			//serverpath = ctx.getRealPath("/") + "/";
			if (gateInited == false)
			{
				Gate.setGateHome(new File(ctx.getRealPath("/WEB-INF")));
				Gate.setPluginsHome(new File(ctx.getRealPath("/WEB-INF/gate-plugins")));
				Gate.setSiteConfigFile(new File(ctx.getRealPath("/WEB-INF/site-gate.xml")));
				Gate.setUserConfigFile(new File(ctx.getRealPath("/WEB-INF/user-gate.xml")));
				
				Gate.init();	
				gateInited = true;	
				Gate.getCreoleRegister().registerDirectories(ctx.getResource("/WEB-INF/gate-plugins/ANNIE"));
			}	
		}
	}

	
	public static void initAnnie() throws ServletException, Exception 
	{	 
	    // create a serial analyser controller to run ANNIE with
	    annieController = (SerialAnalyserController) Factory.createResource(
		        "gate.creole.SerialAnalyserController", Factory.newFeatureMap(),
		        Factory.newFeatureMap(), "ANNIE_" + Gate.genSym());
	    // load each PR as defined in ANNIEConstants
	    for(int i = 0; i < ANNIEConstants.PR_NAMES.length; i++) 
	    {
		      FeatureMap params = Factory.newFeatureMap(); // use default parameters     
		      ProcessingResource pr = (ProcessingResource) Factory.createResource(ANNIEConstants.PR_NAMES[i], params);
		      if(ANNIEConstants.PR_NAMES[i].equals("gate.creole.gazetteer.DefaultGazetteer"))
		      {
		       	  Gazetteer gaz = (Gazetteer)pr;
		       	  gaz.setCaseSensitive(Boolean.FALSE);
		      }
		      //add the PR to the pipeline controller
		      annieController.add(pr);      		      
	    } // for each ANNIE PR    
		    
		//Load Pronoun-Coreference module
		FeatureMap params = Factory.newFeatureMap(); // use default parameters
		ProcessingResource coref = (ProcessingResource)Factory.createResource("gate.creole.coref.Coreferencer", params);
		annieController.add(coref); 
	} // initAnnie()
	
	
	public static Corpus createCopus(String content) throws Exception
	{
		Corpus result = Factory.newCorpus("Test corpust");
		Document doc = Factory.newDocument(content);
		result.add(doc);
		
		return result;		  		
	}
	

	public static void getEntities(ServletContext ctx, String orgContent, QueryBuffer output) throws ServletException, Exception
	{
		if ( gateInited == false)
		{
			initGate(ctx);
			initAnnie();
		}
		
		Corpus corpus = createCopus(orgContent);
		annieController.setCorpus(corpus);
		annieController.execute();
		Document doc = (Document) corpus.get(0);		
		AnnotationSet globalAnnotSet = doc.getAnnotations();
		//GetPOSTag(globalAnnotSet, orgContent, output);
		GetUEandRelations(globalAnnotSet, orgContent, output);
	}
	
	public static String getWordFollow(String orgContent, int pos)
	{
		String remain = "";
		if (pos < orgContent.length() )
			remain = orgContent.substring(pos+1);
		remain = remain.trim();
		String wordfollow = "";
		if ( remain.compareToIgnoreCase("")!=0)
		{
			String wordfollowlist[] = remain.split(" ");
			if (wordfollowlist.length >=1)
				wordfollow = wordfollowlist[0];
			  wordfollow = wordfollow.replace('?', ' ');
			  wordfollow = wordfollow.replace('.', ' ');
			  wordfollow = wordfollow.trim();
		}
		
		return wordfollow;
	}
	
	public static String getWordBefore(String orgContent, int pos)
	{
		String remain = "";
		if (pos > 0 )
			remain = orgContent.substring(0, pos-1);
		remain = remain.trim();
		String wordbefore = "";
		if ( remain.compareToIgnoreCase("")!=0)
		{
			String wordbeforelist[] = remain.split(" ");
			if (wordbeforelist.length >=1)
				wordbefore = wordbeforelist[wordbeforelist.length-1];
			  wordbefore = wordbefore.replace('?', ' ');
			  wordbefore = wordbefore.replace('.', ' ');
			  wordbefore = wordbefore.trim();
		}
		
		return wordbefore;
	}

	public static void GetPOSTag(AnnotationSet globalAnnotSet, String orgContent, QueryBuffer output) throws  Exception
	{		
		Iterator it = globalAnnotSet.iterator();
		Annotation currAnnot;
		while(it.hasNext())
		{			
			currAnnot = (Annotation) it.next();

			if ( currAnnot != null && currAnnot.getFeatures().get("category") != null)
			{
				String entity = orgContent.substring(currAnnot.getStartNode().getOffset().intValue(), currAnnot.getEndNode().getOffset().intValue());
				String category = currAnnot.getFeatures().get("category").toString();
			
				//output.InsertItem(entity, className, classType, currAnnot.getStartNode().getOffset(), currAnnot.getEndNode().getOffset(), "G", wordfollow, wordbefore);
			}
		}
	}
	
	public static void GetUEandRelations(AnnotationSet globalAnnotSet, String orgContent, QueryBuffer output) throws  Exception
	{		
		Iterator it = globalAnnotSet.iterator();
		Annotation currAnnot;
		while(it.hasNext())
		{			
			currAnnot = (Annotation) it.next();
			
			if ( currAnnot != null && currAnnot.getFeatures().get("majorType") != null)
			{
				String entity = orgContent.substring(currAnnot.getStartNode().getOffset().intValue(), currAnnot.getEndNode().getOffset().intValue());
				String wordfollow = getWordFollow(orgContent, currAnnot.getEndNode().getOffset().intValue());
				String wordbefore = getWordBefore(orgContent, currAnnot.getStartNode().getOffset().intValue());
				
				String className = "";
				if ( currAnnot.getFeatures().get("minorType") != null)
				{					
					className = currAnnot.getFeatures().get("minorType").toString();
				}
				else
				{
					className = currAnnot.getFeatures().get("majorType").toString();
				}
				
				String classType = "IE";
				if (className.compareTo("UE")==0)
				{
					classType = "UE";
					className = "UE_" + entity;
				}
				else if(className.substring(0, 2).compareTo("UE")==0)
				{
					classType = "UE";				
				}
				else if (className.compareTo("RW")==0)
					classType = "RW";
				else if (className.compareTo("CONJ")==0)
					classType = "CONJ";
					
				
				//if (className.compareToIgnoreCase("UE_Where")==0)
					//className = "UE_Location";
				//else if (className.compareToIgnoreCase("UE_When")==0)
					//className = "UE_Date";
				
				output.InsertItem(entity, className, classType, currAnnot.getStartNode().getOffset(), currAnnot.getEndNode().getOffset(), "G", wordfollow, wordbefore);
			}
		}
	}
	
}
