/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.VNKIMService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import com.ontotext.kim.client.*;
import com.ontotext.kim.client.corpora.*;
import com.ontotext.kim.client.semanticannotation.*;
import com.ontotext.kim.client.inline.InlineConfig;
import com.ontotext.kim.client.model.FeatureConstants;
import com.ontotext.kim.client.model.WKBConstants;
import com.ontotext.kim.client.query.*;
import com.ontotext.kim.client.semanticrepository.*;
import com.ontotext.kim.client.coredb.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xml.sax.InputSource;

/**
 *
 * @author Mrt. Long
 */
class KIMAnno {

    String name = "";
    String value = "";
    String classname = "";
    String classtype = "";
    String sidname = "";
    String ID = "";
    int start;
    int end;
    String org = "";
    boolean isLookup = false;

    public String key() {
        return value;
    }
}

public class ENSearch {

    public static KIMService kim = null;
    public static SemanticRepositoryAPI apiSemanticRepository = null;

    public static void checkNewKnowledge(String instanceId){
		try {
            if (kim == null) kim = GetService.from("localhost", 1199);

            if (apiSemanticRepository == null) apiSemanticRepository =
                                                kim.getSemanticRepositoryAPI();

			System.out.println("[ Check Instance Properties ]");
			System.out.println("-- Instance ID: " + instanceId);

			//insert namespace
			String resourceUri = "http://www.ontotext.com/kim/2006/05/wkb#" + instanceId;

			Resource uriRes = new URIImpl(resourceUri);


			// get the properties of the instance
			SemanticQueryResult properties = apiSemanticRepository
			.evaluateSelectSeRQL("select distinct Property, Range "
					+ "from {<" + uriRes.toString() + ">} "
					+ "Property" + " {Range}");
			// count entities and fill a map with number and names
			String rel, range;

            for (SemanticQueryResultRow row : properties) {
//				rel = row.get(0).toString().split("#")[1];
//				range = row.get(1).toString().split("#")[1];
				rel = row.get(0).toString();
				range = row.get(1).toString();
				System.out.println("--> " + rel + ": " + range);
			}
		}
		catch (Exception e) {
			System.out.println("flksjdlf" + e.getMessage());
		}
	}

    public static void AnnotationsFeatures(String content) {
		try {
            if (kim == null) kim = GetService.from("localhost", 1199);
            KIMService serviceKim = kim;
            SemanticAnnotationAPI apiSemAnn = serviceKim.getSemanticAnnotationAPI();

            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
			KIMDocument kdoc = apiCorpora.createDocument(content.getBytes(), "UTF-8");

			System.out.println("[ KIMDocument is created by KIM v3 : ]");
			System.out.println("-- " + kdoc);
			kdoc = apiSemAnn.execute(kdoc, apiSemAnn.RUNNING_STRATEGY_DISABLE_INSTANCE_GENERATOR);
            System.out.println("-- " + kdoc);
			KIMAnnotationSet kimASet = kdoc.getAnnotations();
			System.out.println("[Annotation completed]");


				Iterator annIterator = kimASet.iterator();
				Map map = new HashMap();
				ArrayList arrlist = new ArrayList();

				while (annIterator.hasNext()) {
					KIMAnnotation kimAnnotation = (KIMAnnotation) annIterator
							.next();
					// System.out.println(kimAnnotation.getFeatures() + "  "
					// + kimAnnotation.getType());
					if (!map.containsKey(kimAnnotation.getStartOffset())) {
						Map submap = new HashMap();
						submap.put(kimAnnotation.getFeatures().get(
								FeatureConstants.INSTANCE), kimAnnotation);
						map.put(kimAnnotation.getStartOffset(), submap);
						arrlist.add(kimAnnotation.getStartOffset());
					} else {
						Map submap = (HashMap) (map.get(kimAnnotation
								.getStartOffset()));
						if (!submap.containsKey(kimAnnotation.getFeatures().get(
								FeatureConstants.INSTANCE)))
							submap.put(kimAnnotation.getFeatures().get(
									FeatureConstants.INSTANCE), kimAnnotation);
					}
				}

				Collections.sort(arrlist);
				int size = arrlist.size();
				String doc = "\n";

				for (int i = 0; i < size; i++) {
					Map submap = (HashMap) (map.get(arrlist.get(i)));
					Iterator submapIterator = submap.keySet().iterator();

					while (submapIterator.hasNext()) {
						String inst = submapIterator.next().toString();

						KIMAnnotation kimAnnotation = (KIMAnnotation) submap
								.get(inst);
						KIMFeatureMap kimFeatures = kimAnnotation.getFeatures();

						doc += "<"
								+ kimFeatures.get(FeatureConstants.CLASS)
										.toString().split("#", 2)[1]
								+ " startOffset=\""
								+ Integer.toString(kimAnnotation
										.getStartOffset())
								+ "\" endOffset=\""
								+ Integer
										.toString(kimAnnotation.getEndOffset())
								+ "\" inst=\""
								+ kimFeatures.get(FeatureConstants.INSTANCE)
										.toString();
						if (kimFeatures
								.get(FeatureConstants.FEATURE_ORIGINAL_NAME) != null)
							doc += "\" name=\""
									+ kimFeatures
											.get(
													FeatureConstants.FEATURE_ORIGINAL_NAME)
											.toString();
						doc += "\" />\n";

				System.out.println(doc);
			}
			}
			// ----------------------------------------------------------------------------------
		} catch (Exception e) {
            System.out.println("flksjdlf" + e.getMessage());
		}
	}

    public static String GetNamedEntity(String query, QueryBuffer output, String ambiguous) {
        String resultano = "";
        try {
            if (kim == null) kim = GetService.from("localhost", 1199);
            KIMService serviceKim = kim;

            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
            SemanticAnnotationAPI apiSemAnn = serviceKim.getSemanticAnnotationAPI();

            //KIMDocument kdoc = apiCorpora.createDocument(query, "UTF-8");
            KIMDocument kdoc = apiCorpora.createDocument(query.getBytes(), "UTF-8");

            kdoc = apiSemAnn.execute(kdoc);

                        System.out.println(kdoc.toString());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document anno = builder.parse(new InputSource(new StringReader(kdoc.toXML())));

            NodeList list = anno.getElementsByTagName("Annotation");

            Map map = new HashMap();
            ArrayList arrlist = new ArrayList();

            for (int i = 0, length = list.getLength(); i < length; i++) {
                KIMAnno annotation = new KIMAnno();

                Element entity = (Element) list.item(i);
                annotation.start = Integer.valueOf(entity.getAttribute("StartNode"));
                annotation.end = Integer.valueOf(entity.getAttribute("EndNode"));
                annotation.value = query.substring(annotation.start, annotation.end);
                NodeList features = entity.getElementsByTagName("Feature");
                for (int j = 0; j < features.getLength(); j++) {
                    Element feature = (Element) features.item(j);
                    String n = feature.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
                    String v = feature.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
                    if (n.compareToIgnoreCase("class") == 0) {
                        annotation.classname = v.split("#", 2)[1];
                    }
                    if (n.compareToIgnoreCase("inst") == 0) {
                        annotation.sidname = "http://www.ontotext.com/kim/2006/05/wkb#" + v.split("#", 2)[1];
                    }
                    if (n.compareToIgnoreCase("originalName") == 0) {
                        annotation.name = v;
                    }
                }

                if (!map.containsKey(annotation.start)) {
                    Map submap = new HashMap();
                    submap.put(annotation.sidname, annotation);
                    map.put(annotation.start, submap);
                    arrlist.add(annotation.start);
                } else {
                    Map submap = (HashMap) (map.get(annotation.start));
                    if (!submap.containsKey(annotation.sidname)) {
                        submap.put(annotation.sidname, annotation);
                    }
                }
            }

            Collections.sort(arrlist);
            int size = arrlist.size();

            String doc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<galleon_output><galleon_text><Annotations>";
            /*
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder bd = fact.newDocumentBuilder();
            Document doc = bd.newDocument();
            //doc.setXmlStandalone(true);

            Element galleon_output = doc.createElement("galleon_output");
            doc.appendChild(galleon_output);
            Element galleon_text = doc.createElement("galleon_text");
            galleon_output.appendChild(galleon_text);
            Element descript = doc.createElement("DESCRIPT");
            galleon_output.appendChild(descript);
            descript.appendChild(doc.createTextNode(""));
            Element url = doc.createElement("URL");
            galleon_output.appendChild(url);
            url.appendChild(doc.createTextNode(""));
            Element dir = doc.createElement("DIR");
            galleon_output.appendChild(dir);
            dir.appendChild(doc.createTextNode(""));
            Element rawdir = doc.createElement("RAWDIR");
            galleon_output.appendChild(rawdir);
            rawdir.appendChild(doc.createTextNode(""));

            Element annotations = doc.createElement("Annotations");
            galleon_text.appendChild(annotations);
             */

            for (int i = 0; i < size; i++) {
                Map submap = (HashMap) (map.get(arrlist.get(i)));
                Iterator submapIterator = submap.keySet().iterator();

                while (submapIterator.hasNext()) {
                    String inst = submapIterator.next().toString();

                    KIMAnno annotation = (KIMAnno) submap.get(inst);

                    if (ambiguous.contains(annotation.value) && (!ambiguous.contains(annotation.classname))) {
                        continue;
                    }
                    if ((annotation.classname.compareToIgnoreCase("CalendarMonth") == 0) || (annotation.classname.compareToIgnoreCase("TimeInterval") == 0) || (annotation.classname.compareToIgnoreCase("GeneralTerm") == 0)) {
                        continue;
                    }

                    String wordfollow = GateNamedEntity.getWordFollow(query, annotation.end);
                    String wordbefore = GateNamedEntity.getWordBefore(query, annotation.start);
                    output.InsertIE(annotation.name, annotation.value, annotation.classname, "IE", annotation.sidname, annotation.start, annotation.end, "K", wordfollow, wordbefore);

                    //             doc += "" + annotation.classname + " startOffset=\"" + Integer.toString(annotation.start) + "\" endOffset=\"" + Integer.toString(annotation.end) + "\" inst=\"" + annotation.sidname;
                    //             doc += "\" name=\"" + annotation.name;
                    //             doc += "\" /<br/>\n";
                    doc += "<" + annotation.classname + " startOffset=\"" + annotation.start + "\" endOffset=\"" + annotation.end + "\" inst=\"" + annotation.sidname;
                    doc += "\" name=\"" + annotation.name;
                    doc += "\" />";

                /*
                Element an = doc.createElement(annotation.classname);
                an.setAttribute("startOffset", Integer.toString(annotation.start));
                an.setAttribute("endOffset", Integer.toString(annotation.end));
                an.setAttribute("inst", annotation.sidname);
                if (annotation.name != null)
                an.setAttribute("name", annotation.name);
                annotations.appendChild(an);
                 */
                }
            }
            doc += "</Annotations></galleon_text></galleon_output>";

            resultano = doc;

        } catch (Exception e) {
            resultano += e.getMessage();
        }
        return resultano;
    }

    public static String GetUERW(String query, QueryBuffer output) {
        String resultano = "";
        try {
            if (kim == null) kim = GetService.from("localhost", 1199);
            KIMService serviceKim = kim;

            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
            // SemanticAnnotationAPI apiSemAnn =
            // serviceKim.getSemanticAnnotationAPI();
            SemanticAnnotationAPI apiSemAnn1 = serviceKim.getSemanticAnnotationAPI("mycondapp.gapp");

            KIMDocument kdoc = apiCorpora.createDocument(query.getBytes(), "UTF-8");
            kdoc = apiSemAnn1.execute(kdoc);

            //System.out.println(kdoc);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document anno = builder.parse(new InputSource(new StringReader(kdoc.toXML())));

            Map<Integer, KIMAnno> map = new HashMap<Integer, KIMAnno>();

            NodeList list = anno.getElementsByTagName("Annotation");
            String doc = "";
            int length = list.getLength();

            for (int i = 0; i < length; i++) {
                KIMAnno annotation;
                Element entity = (Element) list.item(i);
                //System.out.println(i);
                if ((entity.getAttribute("Type").compareToIgnoreCase("Lookup") != 0) && (entity.getAttribute("Type").compareToIgnoreCase("Token") != 0)) {
                    continue;
                }

                if (!map.containsKey(Integer.valueOf(entity.getAttribute("StartNode")))) {
                    annotation = new KIMAnno();
                    map.put(Integer.valueOf(entity.getAttribute("StartNode")), annotation);
                } else {
                    annotation = map.get(Integer.valueOf(entity.getAttribute("StartNode")));
                }

                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
                    annotation.isLookup = true;
                }

                annotation.start = Integer.valueOf(entity.getAttribute("StartNode"));
                annotation.end = Integer.valueOf(entity.getAttribute("EndNode"));
                annotation.value = query.substring(annotation.start,
                        annotation.end);
                annotation.name = query.substring(annotation.start, annotation.end);

                NodeList features = entity.getElementsByTagName("Feature");
                for (int j = 0; j < features.getLength(); j++) {
                    Element feature = (Element) features.item(j);

                    String n = feature.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
                    String v;
                    if (feature.getElementsByTagName("Value").item(0).getFirstChild() != null) {
                        v = feature.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
                    } else v = " ";
                    if (n.compareToIgnoreCase("majorType") == 0) {
                        if (v.compareToIgnoreCase("UE") == 0) {
                            annotation.classname = "UE_" + annotation.name;
                            annotation.classtype = "UE";
                        } else if (v.compareToIgnoreCase("IE") == 0) {
                            annotation.classtype = "IE";
                            annotation.classname = ProcessingXML.findIEfromDic(annotation.value);
                        } else if (v.compareToIgnoreCase("CONJ") == 0) {
                            annotation.classtype = "CONJ";
                            if (annotation.name.compareToIgnoreCase("or") == 0) annotation.classname = "UNION";
                            else if (annotation.name.compareToIgnoreCase("and") == 0) annotation.classname = "INTERSECT";
                            else annotation.classname = "MINUS";
                        } else if (v.compareToIgnoreCase("RW") != 0) {
                            annotation.classname = "UE_" + v;
                            annotation.classtype = "UE";
                        } else {
                            annotation.classname = v;
                            annotation.classtype = "RW";
                        }
                    }
                    if (n.compareToIgnoreCase("root") == 0) {
                        annotation.org = v;
                    }
                }

                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
                    String wordfollow = GateNamedEntity.getWordFollow(query, annotation.end);
                    String wordbefore = GateNamedEntity.getWordBefore(query, annotation.start);
                    if (annotation.classtype.compareToIgnoreCase("IE") == 0) output.InsertIE(annotation.name, annotation.value, annotation.classname, annotation.classtype, "http://www.ontotext.com/kim/2006/05/wkb#"+annotation.classname+"_"+annotation.value, annotation.start, annotation.end, "K", wordfollow, wordbefore);
                    else output.InsertItem(annotation.name, annotation.classname, annotation.classtype, annotation.start, annotation.end, "G", wordfollow, wordbefore);
                    doc += "" + annotation.classname + " startOffset=\"" + Integer.toString(annotation.start) + "\" endOffset=\"" + Integer.toString(annotation.end) + "\" name=\"" + annotation.name;
                    doc += "\" org=\"" + annotation.org;
                    doc += "\" /<br/>\n";
                }
            }
            resultano = doc;
        } catch (Exception e) {
            System.out.println("loi o day");
            e.printStackTrace();
            resultano += e.getMessage();
        }

        return resultano;
    }

    public static String getNS(
            String classname) {
        try {
            String path = GateNamedEntity.serverpath + "ns.xml";

            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder bd = fact.newDocumentBuilder();
            Document inpdoc = bd.parse(path);
            NodeList entryset = inpdoc.getElementsByTagName("namespace");
            for (int i = 0; i <
                    entryset.getLength(); i++) {
                Node entry = entryset.item(i);
                String values = ((Element) entry).getAttribute("classname");

                for (String value : values.split(", ")) {
                    if (value.compareToIgnoreCase(classname) == 0) {
                        return ((Element) entry).getAttribute("ns") + "#";
                    }

                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //return "http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#";
        return "http://proton.semanticweb.org/2006/05/protonu#";
    }

    public static String runSeRQL(
            String query) {
        String result = "";
        try {
            if (kim == null) kim = GetService.from("localhost", 1199);

            if (apiSemanticRepository == null) apiSemanticRepository =
                                                kim.getSemanticRepositoryAPI();

            SemanticQueryResult properties = apiSemanticRepository.evaluateSelectSeRQL(query);
            for (SemanticQueryResultRow row : properties) {
                String sidname = row.get(0).toString();
                sidname =
                        sidname.substring(sidname.indexOf("#") + 1);
                result +=
                        sidname + ":" + row.get(1).toString();// + ":" + TestQuery(row.get(0).toString());
                result +=
                        ";";
            }

        } catch (Exception e) {
            result += e.getMessage();
            e.printStackTrace();
        }

        return result;
    }

    public static String getLabel(
            String id) {
        try {
            String resourceUri = id;
            Resource uriRes = null;
            if (resourceUri.startsWith("_")) {
                // anonymous resource
                uriRes = new BNodeImpl(resourceUri);
            } else {
                uriRes = new URIImpl(resourceUri);
            }
// get the labels of the entities

//            SemanticQueryResult properties = apiSemanticRepository.evaluateSelectSeRQL("select distinct MainLabel " + "from {<" + uriRes.toString() + ">} <" + URIImpl.RDFS_LABEL.getURI() + "> {MainLabel}");
            // count entities and fill a map with number and names
 //           for (SemanticQueryResultRow row : properties) {
 //               return row.get(0).toString();
 //           }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public static String TestQuery(
            String id) {
        String result = "";
        try {
            String resourceUri = id;
            Resource uriRes = null;
            if (resourceUri.startsWith("_")) {
                // anonymous resource
                uriRes = new BNodeImpl(resourceUri);
            } else {
                uriRes = new URIImpl(resourceUri);
            }

            SemanticQueryResult properties = apiSemanticRepository.evaluateSelectSeRQL("select distinct Relation, MainLabel " + "from {<" + uriRes.toString() + ">} " + "Relation" + " {MainLabel}");

            for (SemanticQueryResultRow row : properties) {
                String ent = row.get(1).toString();
                String rel = row.get(0).toString().split("#")[1];

                if (rel.compareToIgnoreCase("label") == 0) {
                    System.out.println(ent);
                    continue;
                }


                if ((rel.compareToIgnoreCase("type") != 0) && (rel.compareToIgnoreCase("generatedBy") != 0)) {
                    if (ent.contains("http://")) {
                        ent = getLabel(ent);
                    }
                    result +=
                            rel + " - " + ent + "\\n";
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public static String GetAnnotation(String query, QueryBuffer output) {
        String resultano = "";
        try {

            
            if (kim == null) kim = GetService.from("localhost", 1199);
            KIMService serviceKim = kim;

            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
            if (apiSemanticRepository == null) apiSemanticRepository = kim.getSemanticRepositoryAPI();

            SemanticAnnotationAPI apiSemAnn1 = serviceKim.getSemanticAnnotationAPI("mycondapp.gapp");

            KIMDocument kdoc = apiCorpora.createDocument(query, true);
            kdoc = apiSemAnn1.execute(kdoc);

            
            //System.out.println(kdoc);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document anno = builder.parse(new InputSource(new StringReader(kdoc.toXML())));

            Map<Integer, KIMAnno> map = new HashMap<Integer, KIMAnno>();

            NodeList list = anno.getElementsByTagName("Annotation");
            String doc = "";
            int length = list.getLength();

            for (int i = 0; i < length; i++) {
                KIMAnno annotation;
                Element entity = (Element) list.item(i);
                //System.out.println(i);
                if ((entity.getAttribute("Type").compareToIgnoreCase("Lookup") != 0) && (entity.getAttribute("Type").compareToIgnoreCase("Token") != 0)) {
                    continue;
                }

                if (!map.containsKey(Integer.valueOf(entity.getAttribute("StartNode")))) {
                    annotation = new KIMAnno();
                    map.put(Integer.valueOf(entity.getAttribute("StartNode")), annotation);
                } else {
                    annotation = map.get(Integer.valueOf(entity.getAttribute("StartNode")));
                }

                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
                    annotation.isLookup = true;
                }

                annotation.start = Integer.valueOf(entity.getAttribute("StartNode"));
                annotation.end = Integer.valueOf(entity.getAttribute("EndNode"));
                annotation.value = query.substring(annotation.start,
                        annotation.end);
                annotation.name = query.substring(annotation.start, annotation.end);

                NodeList features = entity.getElementsByTagName("Feature");
                for (int j = 0; j < features.getLength(); j++) {
                    Element feature = (Element) features.item(j);

                    String n = feature.getElementsByTagName("Name").item(0)
                                                .getFirstChild().getNodeValue();
                    String v;

                    if (feature.getElementsByTagName("Value")
                                            .item(0).getFirstChild() != null) {
                        v = feature.getElementsByTagName("Value")
                                        .item(0).getFirstChild().getNodeValue();
                    } else v = " ";

                    if (n.compareToIgnoreCase("majorType") == 0) {

                        if (v.compareToIgnoreCase("UE") == 0) {

                            annotation.classname = "UE_" + annotation.name;
                            annotation.classtype = "UE";

                        } else if (v.compareToIgnoreCase("IE") == 0) {                            
                                                     
                            String SeRQL = "select ID, C "
                                    + "from {A} rdfs:label {L}, "
                                    + "{ID} sesame:directType {C}, "
                                    + "{ID} <http://proton.semanticweb.org"
                                    + "/2006/05/protons#hasAlias> {A} "
                                    + "where L like " 
                                    + "\"" + annotation.value + "\" ";

                            
                            SemanticQueryResult qr = apiSemanticRepository.evaluateSelectSeRQL(SeRQL);

//                            System.out.println("result:" + qr);

                            annotation.classtype = "IE";
                            if (qr.size() >=1) {
                                annotation.classname = qr.get(0).get(1)
                                                   .toString().split("#", 2)[1];
                                annotation.ID = qr.get(0).get(0).toString();
                                System.out.println(annotation.classname);
                                System.out.println(annotation.ID);
                            }
                            //else annotation.classname = ProcessingXML.findIEfromDic(annotation.value);

//                            resultano += "after query: " + " " + annotation.ID + " ";
                            
                        } else if (v.compareToIgnoreCase("CONJ") == 0) {

                            annotation.classtype = "CONJ";
                            if (annotation.name.compareToIgnoreCase("or") == 0)
                                annotation.classname = "UNION";
                            else if (annotation.name.compareToIgnoreCase("and")
                                                                        == 0)
                                annotation.classname = "INTERSECT";
                            else annotation.classname = "MINUS";

                        } else if (v.compareToIgnoreCase(Constants.SUPERLATIVE_QUANLITATIVE_ADJ) == 0) {
                            annotation.classname = Constants.SUPERLATIVE_QUANLITATIVE_ADJ;
                            annotation.classtype = Constants.SUPERLATIVE_QUANLITATIVE_ADJ;

                        } else if (v.compareToIgnoreCase(Constants.QUANLITATIVE_ADJ) == 0) {

                            annotation.classname = Constants.QUANLITATIVE_ADJ;
                            annotation.classtype = Constants.QUANLITATIVE_ADJ;

                        } else if (v.compareToIgnoreCase(Constants.QUANTITATIVE_ADJ) == 0) { //Dinh luong

                            String wordbefore = GateNamedEntity
                                    .getWordBefore(query, annotation.start);

                            if (wordbefore != null //CO SSI
								&& (wordbefore.toLowerCase().indexOf(
										Constants.MOST_STRING) >= 0
                                    || wordbefore.toLowerCase().indexOf(
                                        Constants.LEAST_STRING) >= 0)
                               ) {
                                // For long adjectives in superlative form
                                annotation.classtype = Constants.SUPERLATIVE_QUANTITATIVE_ADJ; //De sau nay xd top relation
                                annotation.classname = Constants.SUPERLATIVE_QUANTITATIVE_ADJ;
                                annotation.name = wordbefore + " "
                                                  + annotation.name; //GHEP "MOST" "LEAST" VAO ADJ
                                                                     //De sau nay xd top relation la max/min
                            } else { //KO CO SSI, chi la tinh tu binh thuong
                                annotation.classtype = Constants.QUANTITATIVE_ADJ;
                                annotation.classname = Constants.QUANTITATIVE_ADJ;
                            }

                        } else if (v.compareToIgnoreCase(Constants.SUPERLATIVE_QUANTITATIVE_ADJ) == 0) {  //dinh luong bat quy tac/ ngan

                            annotation.classtype = Constants.SUPERLATIVE_QUANTITATIVE_ADJ; //De sau nay xd top relation
                            annotation.classname = Constants.SUPERLATIVE_QUANTITATIVE_ADJ;

                        }else if (v.compareToIgnoreCase("RW") != 0) {

                            annotation.classname = "UE_" + v;
                            annotation.classtype = "UE";

                        } else {

                            annotation.classname = v;
                            annotation.classtype = "RW";

                        }

                    }

                    if (n.compareToIgnoreCase("root") == 0) {
                        annotation.org = v;
                    }

                }

                if (entity.getAttribute("Type")
                                        .compareToIgnoreCase("Lookup") == 0) {
                    String wordfollow = GateNamedEntity.getWordFollow(query,
                                                                annotation.end);
                    String wordbefore = GateNamedEntity.getWordBefore(query,
                                                                annotation.start);
                    if (annotation.classtype.compareToIgnoreCase("IE") == 0) {
                        output.InsertIE(annotation.name, annotation.value,
                        annotation.classname, annotation.classtype,
                        annotation.ID, annotation.start, annotation.end, "K",
                        wordfollow, wordbefore);
                    } else {
                        output.InsertItem(annotation.name, annotation.classname,
                        annotation.classtype, annotation.start, annotation.end,
                        "G", wordfollow, wordbefore);
                    }
                    doc += "" + annotation.classname + " startOffset=\"" +
                        Integer.toString(annotation.start) + "\" endOffset=\""
                        + Integer.toString(annotation.end) + "\" name=\""
                        + annotation.name;
                    doc += "\" org=\"" + annotation.org;
                    doc += "\" /<br/>\n";
                }
            }
            resultano = doc;
        } catch (Exception e) {
            System.out.println("Error here");
            e.printStackTrace();
            resultano += e.getMessage();
        }

        return resultano;
    }
}


///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.me.VNKIMService;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.impl.BNodeImpl;
//import org.openrdf.model.impl.URIImpl;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//
//
//import com.ontotext.kim.client.*;
//import com.ontotext.kim.client.corpora.*;
//import com.ontotext.kim.client.semanticannotation.*;
//import com.ontotext.kim.client.query.*;
//import com.ontotext.kim.client.semanticrepository.*;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import org.openrdf.model.vocabulary.RDFS;
//import org.xml.sax.InputSource;
//
///**
// *
// * @author Mrt. Long
// */
//class KIMAnno {
//
//    String name = "";
//    String value = "";
//    String classname = "";
//    String classtype = "";
//    String sidname = "";
//    int start;
//    int end;
//    String org = "";
//    boolean isLookup = false;
//
//    public String key() {
//        return value;
//    }
//}
//
//public class ENSearch {
//
//    public static KIMService kim;
//    public static SemanticRepositoryAPI apiSemanticRepository;
//
//    public static String GetNamedEntity(String query, QueryBuffer output, String ambiguous) {
//        String resultano = "";
//        try {
//
//            KIMService serviceKim = GetService.from("localhost", 1199);
//
//            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
//            SemanticAnnotationAPI apiSemAnn = serviceKim.getSemanticAnnotationAPI();
//
//            KIMDocument kdoc = apiCorpora.createDocument(query, true);
//            kdoc = apiSemAnn.execute(kdoc);
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document anno = builder.parse(new InputSource(new StringReader(kdoc.toXML())));
//
//            NodeList list = anno.getElementsByTagName("Annotation");
//
//            Map map = new HashMap();
//            ArrayList arrlist = new ArrayList();
//
//            for (int i = 0, length = list.getLength(); i < length; i++) {
//                KIMAnno annotation = new KIMAnno();
//
//                Element entity = (Element) list.item(i);
//                annotation.start = Integer.valueOf(entity.getAttribute("StartNode"));
//                annotation.end = Integer.valueOf(entity.getAttribute("EndNode"));
//                annotation.value = query.substring(annotation.start, annotation.end);
//                NodeList features = entity.getElementsByTagName("Feature");
//                for (int j = 0; j < features.getLength(); j++) {
//                    Element feature = (Element) features.item(j);
//                    String n = feature.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
//                    String v = feature.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
//                    if (n.compareToIgnoreCase("class") == 0) {
//                        annotation.classname = v.split("#", 2)[1];
//                    }
//                    if (n.compareToIgnoreCase("inst") == 0) {
//                        annotation.sidname = "http://www.ontotext.com/kim/2006/05/wkb#" + v.split("#", 2)[1];
//                    }
//                    if (n.compareToIgnoreCase("originalName") == 0) {
//                        annotation.name = v;
//                    }
//                }
//
//                if (!map.containsKey(annotation.start)) {
//                    Map submap = new HashMap();
//                    submap.put(annotation.sidname, annotation);
//                    map.put(annotation.start, submap);
//                    arrlist.add(annotation.start);
//                } else {
//                    Map submap = (HashMap) (map.get(annotation.start));
//                    if (submap.containsKey(annotation.sidname)) {
//                        submap.put(annotation.sidname, annotation);
//                    }
//                }
//            }
//
//            Collections.sort(arrlist);
//            int size = arrlist.size();
//
//            String doc = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>" +
//                    "<galleon_output><galleon_text><Annotations>";
//            /*
//            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
//            DocumentBuilder bd = fact.newDocumentBuilder();
//            Document doc = bd.newDocument();
//            //doc.setXmlStandalone(true);
//
//            Element galleon_output = doc.createElement("galleon_output");
//            doc.appendChild(galleon_output);
//            Element galleon_text = doc.createElement("galleon_text");
//            galleon_output.appendChild(galleon_text);
//            Element descript = doc.createElement("DESCRIPT");
//            galleon_output.appendChild(descript);
//            descript.appendChild(doc.createTextNode(""));
//            Element url = doc.createElement("URL");
//            galleon_output.appendChild(url);
//            url.appendChild(doc.createTextNode(""));
//            Element dir = doc.createElement("DIR");
//            galleon_output.appendChild(dir);
//            dir.appendChild(doc.createTextNode(""));
//            Element rawdir = doc.createElement("RAWDIR");
//            galleon_output.appendChild(rawdir);
//            rawdir.appendChild(doc.createTextNode(""));
//
//            Element annotations = doc.createElement("Annotations");
//            galleon_text.appendChild(annotations);
//             */
//
//            for (int i = 0; i < size; i++) {
//                Map submap = (HashMap) (map.get(arrlist.get(i)));
//                Iterator submapIterator = submap.keySet().iterator();
//
//                while (submapIterator.hasNext()) {
//                    String inst = submapIterator.next().toString();
//
//                    KIMAnno annotation = (KIMAnno) submap.get(inst);
//
//                    if (ambiguous.contains(annotation.value) && (!ambiguous.contains(annotation.classname))) {
//                        continue;
//                    }
//                    if ((annotation.classname.compareToIgnoreCase("CalendarMonth") == 0) || (annotation.classname.compareToIgnoreCase("TimeInterval") == 0) || (annotation.classname.compareToIgnoreCase("GeneralTerm") == 0)) {
//                        continue;
//                    }
//
//                    String wordfollow = GateNamedEntity.getWordFollow(query, annotation.end);
//                    String wordbefore = GateNamedEntity.getWordBefore(query, annotation.start);
//                    output.InsertIE(annotation.name, annotation.value, annotation.classname, "IE", annotation.sidname, annotation.start, annotation.end, "K", wordfollow, wordbefore);
//
//                    //             doc += "" + annotation.classname + " startOffset=\"" + Integer.toString(annotation.start) + "\" endOffset=\"" + Integer.toString(annotation.end) + "\" inst=\"" + annotation.sidname;
//                    //             doc += "\" name=\"" + annotation.name;
//                    //             doc += "\" /<br/>\n";
//                    doc += "<" + annotation.classname + " startOffset=\"" + annotation.start + "\" endOffset=\"" + annotation.end + "\" inst=\"" + annotation.sidname;
//                    doc += "\" name=\"" + annotation.name;
//                    doc += "\" />";
//
//                /*
//                Element an = doc.createElement(annotation.classname);
//                an.setAttribute("startOffset", Integer.toString(annotation.start));
//                an.setAttribute("endOffset", Integer.toString(annotation.end));
//                an.setAttribute("inst", annotation.sidname);
//                if (annotation.name != null)
//                an.setAttribute("name", annotation.name);
//                annotations.appendChild(an);
//                 */
//                }
//            }
//            doc += "</Annotations></galleon_text>" + "<DESCRIPT>" + "a" + "</DESCRIPT>" + "<URL>" + "a" + "</URL>" + "<DIR>" + "a" + "</DIR>" + "<RAWDIR>" + "a" + "</RAWDIR>" + "</galleon_output>";
//
//            resultano = doc;
//        } catch (Exception e) {
//            resultano += e.getMessage();
//        }
//        return resultano;
//    }
//
////    public static String GetUERW(String query, QueryBuffer output) {
////        String resultano = "";
////        try {
////
////            KIMService serviceKim = GetService.from("localhost", 1199);
////
////            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
////            // SemanticAnnotationAPI apiSemAnn =
////            // serviceKim.getSemanticAnnotationAPI();
////            SemanticAnnotationAPI apiSemAnn1 = serviceKim.getSemanticAnnotationAPI("mycondapp.gapp");
////
////            KIMDocument kdoc = apiCorpora.createDocument(query, "UTF-8");
////            kdoc = apiSemAnn1.execute(kdoc);
////
////            System.out.println(kdoc.toXML());
////            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
////            DocumentBuilder builder = factory.newDocumentBuilder();
////            Document anno = builder.parse(new InputSource(new StringReader(kdoc.toXML())));
////
////            Map<Integer, KIMAnno> map = new HashMap<Integer, KIMAnno>();
////
////            NodeList list = anno.getElementsByTagName("Annotation");
////            String doc = "";
////            for (int i = 0, length = list.getLength(); i < length; i++) {
////                KIMAnno annotation;
////                Element entity = (Element) list.item(i);
////
////                if ((entity.getAttribute("Type").compareToIgnoreCase("Lookup") != 0) && (entity.getAttribute("Type").compareToIgnoreCase("Token") != 0)) {
////                    continue;
////                }
////
////                if (!map.containsKey(Integer.valueOf(entity.getAttribute("StartNode")))) {
////                    annotation = new KIMAnno();
////                    map.put(Integer.valueOf(entity.getAttribute("StartNode")), annotation);
////                } else {
////                    annotation = map.get(Integer.valueOf(entity.getAttribute("StartNode")));
////                }
////
////                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
////                    annotation.isLookup = true;
////                }
////
////                annotation.start = Integer.valueOf(entity.getAttribute("StartNode"));
////                annotation.end = Integer.valueOf(entity.getAttribute("EndNode"));
////                annotation.value = query.substring(annotation.start,
////                        annotation.end);
////                annotation.name = query.substring(annotation.start, annotation.end);
////
////                NodeList features = entity.getElementsByTagName("Feature");
////                for (int j = 0; j < features.getLength(); j++) {
////                    Element feature = (Element) features.item(j);
////                    String n = feature.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
////                    String v = feature.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
////                    if (n.compareToIgnoreCase("majorType") == 0) {
////                        if (v.compareToIgnoreCase("UE") == 0) {
////                            annotation.classname = "UE_" + annotation.name;
////                            annotation.classtype = "UE";
////                        } else if (v.compareToIgnoreCase("RW") != 0) {
////                            annotation.classname = "UE_" + v;
////                            annotation.classtype = "UE";
////                        } else {
////                            annotation.classname = v;
////                            annotation.classtype = "RW";
////                        }
////                    }
////                    if (n.compareToIgnoreCase("root") == 0) {
////                        annotation.org = v;
////                    }
////                }
////
////                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
////                    String wordfollow = GateNamedEntity.getWordFollow(query, annotation.end);
////                    String wordbefore = GateNamedEntity.getWordBefore(query, annotation.start);
////                    output.InsertItem(annotation.name, annotation.classname, annotation.classtype, annotation.start, annotation.end, "G", wordfollow, wordbefore);
////                    doc += "" + annotation.classname + " startOffset=\"" + Integer.toString(annotation.start) + "\" endOffset=\"" + Integer.toString(annotation.end) + "\" name=\"" + annotation.name;
////                    doc += "\" org=\"" + annotation.org;
////                    doc += "\" /<br/>\n";
////                }
////            }
////            resultano = doc;
////        } catch (Exception e) {
////            resultano += e.getMessage();
////        }
////        return resultano;
////    }
//
//    public static String GetUERW(String query, QueryBuffer output) {
//        String resultano = "";
//        try {
//            if (kim == null) kim = GetService.from("localhost", 1199);
//            KIMService serviceKim = kim;
//
//            CorporaAPI apiCorpora = serviceKim.getCorporaAPI();
//            // SemanticAnnotationAPI apiSemAnn =
//            // serviceKim.getSemanticAnnotationAPI();
//            SemanticAnnotationAPI apiSemAnn1 = serviceKim.getSemanticAnnotationAPI("mycondapp.gapp");
//
//            KIMDocument kdoc = apiCorpora.createDocument(query, true);
//            kdoc = apiSemAnn1.execute(kdoc);
//
//            //System.out.println(kdoc);
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document anno = builder.parse(new InputSource(new StringReader(kdoc.toXML())));
//
//            Map<Integer, KIMAnno> map = new HashMap<Integer, KIMAnno>();
//
//            NodeList list = anno.getElementsByTagName("Annotation");
//            String doc = "";
//            int length = list.getLength();
//
//            for (int i = 0; i < length; i++) {
//                KIMAnno annotation;
//                Element entity = (Element) list.item(i);
//
//                if ((entity.getAttribute("Type").compareToIgnoreCase("Lookup") != 0) && (entity.getAttribute("Type").compareToIgnoreCase("Token") != 0)) {
//                    continue;
//                }
//
//                if (!map.containsKey(Integer.valueOf(entity.getAttribute("StartNode")))) {
//                    annotation = new KIMAnno();
//                    map.put(Integer.valueOf(entity.getAttribute("StartNode")), annotation);
//                } else {
//                    annotation = map.get(Integer.valueOf(entity.getAttribute("StartNode")));
//                }
//
//                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
//                    annotation.isLookup = true;
//                }
//
//                annotation.start = Integer.valueOf(entity.getAttribute("StartNode"));
//                annotation.end = Integer.valueOf(entity.getAttribute("EndNode"));
//                annotation.value = query.substring(annotation.start,
//                        annotation.end);
//                annotation.name = query.substring(annotation.start, annotation.end);
//
//                NodeList features = entity.getElementsByTagName("Feature");
//                for (int j = 0; j < features.getLength(); j++) {
//                    Element feature = (Element) features.item(j);
//
//                    String n = feature.getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
//                    String v;
//                    if (feature.getElementsByTagName("Value").item(0).getFirstChild() != null) {
//                        v = feature.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
//                    } else v = " ";
//                    if (n.compareToIgnoreCase("majorType") == 0) {
//                        if (v.compareToIgnoreCase("UE") == 0) {
//                            annotation.classname = "UE_" + annotation.name;
//                            annotation.classtype = "UE";
//                        } else if (v.compareToIgnoreCase("IE") == 0) {
//                            annotation.classtype = "IE";
//                            annotation.classname = ProcessingXML.findIEfromDic(annotation.value);
//                        } else if (v.compareToIgnoreCase("CONJ") == 0) {
//                            annotation.classtype = "CONJ";
//                            annotation.classname = "CONJ";
//                        } else if (v.compareToIgnoreCase("RW") != 0) {
//                            annotation.classname = "UE_" + v;
//                            annotation.classtype = "UE";
//                        } else {
//                            annotation.classname = v;
//                            annotation.classtype = "RW";
//                        }
//                    }
//                    if (n.compareToIgnoreCase("root") == 0) {
//                        annotation.org = v;
//                    }
//                }
//
//                if (entity.getAttribute("Type").compareToIgnoreCase("Lookup") == 0) {
//                    String wordfollow = GateNamedEntity.getWordFollow(query, annotation.end);
//                    String wordbefore = GateNamedEntity.getWordBefore(query, annotation.start);
//                    if (annotation.classtype.compareToIgnoreCase("IE") == 0) output.InsertIE(annotation.name, annotation.value, annotation.classname, annotation.classtype, "http://www.ontotext.com/kim/2006/05/wkb#"+annotation.classname+"_"+annotation.value, annotation.start, annotation.end, "K", wordfollow, wordbefore);
//                    else output.InsertItem(annotation.name, annotation.classname, annotation.classtype, annotation.start, annotation.end, "G", wordfollow, wordbefore);
//                    doc += "" + annotation.classname + " startOffset=\"" + Integer.toString(annotation.start) + "\" endOffset=\"" + Integer.toString(annotation.end) + "\" name=\"" + annotation.name;
//                    doc += "\" org=\"" + annotation.org;
//                    doc += "\" /<br/>\n";
//                }
//            }
//            resultano = doc;
//        } catch (Exception e) {
//            System.out.println("GetUERW.Exception:");
//            e.printStackTrace();
//            resultano += e.getMessage();
//        }
//
//        return resultano;
//    }
//
//    public static String getNS(String classname) {
//        try {
//            String path = GateNamedEntity.serverpath + "ns.xml";
//
//            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
//            DocumentBuilder bd = fact.newDocumentBuilder();
//            Document inpdoc = bd.parse(path);
//            NodeList entryset = inpdoc.getElementsByTagName("namespace");
//            for (int i = 0; i < entryset.getLength(); i++) {
//                Node entry = entryset.item(i);
//                String values = ((Element) entry).getAttribute("classname");
//
//                for (String value : values.split(", ")) {
//                    if (value.compareToIgnoreCase(classname) == 0) {
//                        return ((Element) entry).getAttribute("ns") + "#";
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return "http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#";
//    }
//
//    public static String runSeRQL(String query) {
//        String result = "";
//        try {
//            kim = GetService.from("localhost", 1199);
//
//            apiSemanticRepository = kim.getSemanticRepositoryAPI();
//
//            SemanticQueryResult properties = apiSemanticRepository.evaluateSelectSeRQL(query);
//            for (SemanticQueryResultRow row : properties) {
//                String sidname = row.get(0).toString();
//                sidname = sidname.substring(sidname.indexOf("#") + 1);
//                result += sidname + ":" + row.get(1).toString() + ":" + TestQuery(row.get(0).toString());
//                result += ";";
//			}
//        } catch (Exception e) {
//            result += e.getMessage();
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    public static String getLabel(String id) {
//		try {
//			String resourceUri = id;
//			Resource uriRes = null;
//			if (resourceUri.startsWith("_")) {
//				// anonymous resource
//				uriRes = new BNodeImpl(resourceUri);
//			} else {
//				uriRes = new URIImpl(resourceUri);
//			}
//			// get the labels of the entities
//			SemanticQueryResult properties = apiSemanticRepository
//					.evaluateSelectSeRQL("select distinct MainLabel "
//							+ "from {<" + uriRes.toString() + ">} <"
////							+ "rdfs:label" + "> {MainLabel}");
//                            + RDFS.LABEL.toString() + "> {MainLabel}");
//			// count entities and fill a map with number and names
//			for (SemanticQueryResultRow row : properties) {
//				return row.get(0).toString();
//			}
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		return "";
//	}
//
//	public static String TestQuery(String id) {
//		String result = "";
//        try {
//			String resourceUri = id;
//			// resourceUri =
//			// "http://www.ontotext.com/kim/2006/05/wkb#CountryAdj_T.202";
//			Resource uriRes = null;
//			if (resourceUri.startsWith("_")) {
//				// anonymous resource
//				uriRes = new BNodeImpl(resourceUri);
//			} else {
//				uriRes = new URIImpl(resourceUri);
//			}
//			// get the labels of the entities
//			SemanticQueryResult properties = apiSemanticRepository
//					.evaluateSelectSeRQL("select distinct Relation, MainLabel "
//							+ "from {<" + uriRes.toString() + ">} "
//							+ "Relation" + " {MainLabel}");
//			// count entities and fill a map with number and names
//			for (SemanticQueryResultRow row : properties) {
//				String ent = row.get(1).toString();
//				String rel = row.get(0).toString().split("#")[1];
//
//				if (rel.compareToIgnoreCase("label") == 0) {
//					System.out.println(ent);
//					continue;
//				}
//				if ((rel.compareToIgnoreCase("type") != 0)
//						&& (rel.compareToIgnoreCase("generatedBy") != 0)) {
//					if (ent.contains("http://"))
//						ent = getLabel(ent);
//					result += rel + " - " + ent + "\\n";
//				}
//			}
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//        return result;
//	}
//}
