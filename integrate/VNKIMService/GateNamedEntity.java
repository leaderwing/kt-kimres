package org.me.VNKIMService;
import gate.*;
//import gate.creole.ontology.*;
import gate.util.GateException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.*;

public class GateNamedEntity{
	private static boolean gateInited = false;
	//private static Ontology  ontology;
	private static gate.creole.gazetteer.OntoGazetteerImpl gaze;
	public static String serverpath = "";

	public static String GetEntityandRelationWord(ServletContext ctx, String orgContent, QueryBuffer output) throws ServletException, GateException, IOException, Exception
	{
		String st="";
		{
			if (gateInited == false)
			{
				serverpath = ctx.getRealPath("/") + "/";
                String lexpath = ctx.getRealPath("/");
				Gate.setGateHome(new File(ctx.getRealPath("/WEB-INF")));
				Gate.setPluginsHome(new File(ctx.getRealPath("/WEB-INF/gate-plugins")));
				Gate.setSiteConfigFile(new File(ctx.getRealPath("/WEB-INF/site-gate.xml")));
				Gate.setUserConfigFile(new File(ctx.getRealPath("/WEB-INF/user-gate.xml")));

				Gate.init();
				gateInited = true;
				Gate.getCreoleRegister().registerDirectories(ctx.getResource("/WEB-INF/gate-plugins/ANNIE"));

				File ontoHome = new File(Gate.getPluginsHome(),"Ontology_Tools");
				Gate.getCreoleRegister().registerDirectories (ontoHome.toURI().toURL());

				FeatureMap params = Factory.newFeatureMap();
				params.put("caseSensitive", Boolean.FALSE);
				params.put("gazetteerName", "gate.creole.gazetteer.DefaultGazetteer");
				params.put("listsURL", new File(ctx.getRealPath("/WEB-INF/gate-plugins/ANNIE/resources/gazetteer/listsVN.def")).toURI().toURL());
				params.put("mappingURL", new File(ctx.getRealPath("/WEB-INF/gate-plugins/ANNIE/resources/gazetteer/map.def")).toURI().toURL());
				gaze=(gate.creole.gazetteer.OntoGazetteerImpl)Factory.createResource
							("gate.creole.gazetteer.OntoGazetteerImpl", params);

//                                String lex = SesameUtils.getAllLex();
//                                ConnectServers.OutputSeRql(lex,lexpath + "WEB-INF/gate-plugins/ANNIE/resources/gazetteer/ukVNentity_cap.lst");

			}
		}

		orgContent=orgContent.concat(" ");
		orgContent= orgContent.replace("?", " ?");
//D		orgContent= orgContent.replace("s' ", "s 's ");
		output.query=orgContent;
		gate.Document doc = Factory.newDocument(orgContent);
		gaze.setDocument(doc);
		gaze.execute();
		AnnotationSet defaultAnnotations=doc.getAnnotations();
		AnnotationSet lookups=defaultAnnotations.get("Lookup");
		Iterator<Annotation> it = lookups.iterator();
		Annotation currAnnot;
		st=orgContent;
		st=st+lookups.size();
		while(it.hasNext())
		{
			currAnnot = it.next();
			FeatureMap f = currAnnot.getFeatures();
			String wordfollow = GateNamedEntity.getWordFollow(orgContent, currAnnot.getEndNode().getOffset().intValue());
            String wordbefore = GateNamedEntity.getWordBefore(orgContent, currAnnot.getStartNode().getOffset().intValue());
            String entity = orgContent.substring(currAnnot.getStartNode().getOffset().intValue(), currAnnot.getEndNode().getOffset().intValue());

			if(f.containsKey("ontology"))
			{
				String gclass = f.get("class").toString();
		    	try {
					ItemType item=output.InsertItem(entity,gclass, "IE",currAnnot.getStartNode().getOffset() ,
							currAnnot.getEndNode().getOffset() , "K", wordfollow, wordbefore);
					if(item.className.trim().compareToIgnoreCase("JobPosition")==0)
					{
						item.classType="UE";
						item.className = "UE_JobPosition";
						item.progreg="G";
						item.quantifier=entity;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
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

				if (className.compareTo("IE")==0)
				{
					classType = "IE";
					className=ProcessingXML.findIEfromDic(entity);
					output.InsertItem(entity,className, "IE",currAnnot.getStartNode().getOffset() ,
							currAnnot.getEndNode().getOffset() , "K", wordfollow, wordbefore);
				}
				else
				{
					if (className.compareTo("RW")==0)
						classType = "RW";
					else
					if (className.compareTo("UE")==0)
					{
						classType = "UE";
						className=ProcessingXML.findUEfromDic(entity);
                                                if (className.length()==0)
                                                //    className = SesameUtils.getClasslex(entity);
						if(className.length()==0)
							className = "UE_" + entity;
					}
					else if(className.substring(0, 2).compareTo("UE")==0)
					{
						classType = "UE";
					}
					else if (className.compareTo("CONJ")==0)
						classType = "CONJ";
					else if (className.compareTo(Constants.QUANTITATIVE_ADJ)==0){
						wordfollow = GateNamedEntity.getWordFollow(orgContent, currAnnot.getEndNode().getOffset().intValue());
                                                if (wordfollow.equalsIgnoreCase("nhất")){
                                                    className = Constants.SUPERLATIVE_QUANTITATIVE_ADJ;
                                                    classType=Constants.SUPERLATIVE_QUANTITATIVE_ADJ;
                                                }else
                                                    classType=Constants.QUANTITATIVE_ADJ;
					}
					else if (className.compareTo(Constants.QUANLITATIVE_ADJ)==0){
						wordfollow = GateNamedEntity.getWordFollow(orgContent, currAnnot.getEndNode().getOffset().intValue());
                                                if (wordfollow.equalsIgnoreCase("nhất")){
                                                    className = Constants.SUPERLATIVE_QUANTITATIVE_ADJ;
                                                    classType=Constants.SUPERLATIVE_QUANTITATIVE_ADJ;
                                                }else
                                                    classType=Constants.QUANLITATIVE_ADJ;
					}
					else if (className.compareTo(Constants.SUPERLATIVE_QUANTITATIVE_ADJ)==0){
						classType=Constants.SUPERLATIVE_QUANTITATIVE_ADJ;
                                        }


					//if (className.compareToIgnoreCase("UE_Where")==0)
						//className = "UE_Location";
					//else if (className.compareToIgnoreCase("UE_When")==0)
						//className = "UE_Date";

					try {
						output.InsertItem(entity, className, classType, currAnnot.getStartNode().getOffset(), currAnnot.getEndNode().getOffset(), "G", wordfollow, wordbefore);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			st=st+"</br>"+currAnnot.toString();
		}

		int count=0;
		for(int i=0; i < output.length; i++)
		{
			if (output.getItem(i).classType.compareTo("RW")==0)
			{
				count=count+1;
			}
		}
		return st;
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
        
        public static boolean hasRelation(QueryBuffer output){
            for (int i=0; i<output.length; i++)
                if (output.getItem(i).classType.compareToIgnoreCase("RW")==0)
                return true;
            return false;
        }
}
