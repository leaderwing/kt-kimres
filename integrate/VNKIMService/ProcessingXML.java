package org.me.VNKIMService;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ProcessingXML {

    public static String getAdjectiveTypeInOntology(ItemType adj, ItemType entity) throws Exception {
        String path = GateNamedEntity.serverpath + "QLADic.xml";

        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        NodeList entryset = doc.getElementsByTagName("entry");
        for (int i = 0; i < entryset.getLength(); i++) {
            Element entry = (Element) entryset.item(i);
            String ADJvalue = entry.getAttribute("ADJvalue");
            if (checkValue(ADJvalue, adj.value)) {
                String ADJrelation = entry.getAttribute("ADJrelation");
                if (ADJrelation != null && !ADJrelation.isEmpty()) {
                    return "Relation#" + ADJrelation;
                }

                String Eclass = entry.getAttribute("Eclass");
                if (Eclass == null || Eclass.isEmpty() || !checkValue(Eclass, entity.className.replaceFirst("UE_", ""))) {
                    continue;
                }

                if (adj.classType.equalsIgnoreCase("SQLA") || adj.wordbefore.equalsIgnoreCase("most")) {
                    String Suppclass = entry.getAttribute("MOSTclass");
                    if (Suppclass == null || Suppclass.isEmpty()) {
                        continue;
                    }
                    return "Class#" + Suppclass;
                } else if (adj.wordbefore.equalsIgnoreCase("least")) {
                    String Suppclass = entry.getAttribute("LEASTclass");
                    if (Suppclass == null || Suppclass.isEmpty()) {
                        continue;
                    }
                    return "Class#" + Suppclass;
                } else if (adj.classType.equalsIgnoreCase("QLA")) {
                    String Subclass = entry.getAttribute("SUBclass");
                    if (Subclass == null || Subclass.isEmpty()) {
                        continue;
                    }
                    return "Class#" + Subclass;
                }
            }
        }
        String[] temp = adj.value.split(" ");
        String adjname = "";
        if (adj.wordbefore.equalsIgnoreCase("most")) {
            adjname = "Most";
        } else if (adj.wordbefore.equalsIgnoreCase("least")) {
            adjname = "Least";
        }
        for (int i = 0; i < temp.length; i++) {
            if (!temp[i].trim().isEmpty())
                adjname += temp[i].substring(0, 1).toUpperCase() + temp[i].substring(1).toLowerCase();
        }
        return "Class#(" + adjname + ")" + entity.className.replaceFirst("UE_", "");
    }

	public static Element findElement(NodeList nodelist, String nodename)
	{
		Element output = null;
		for(int i=0; i < nodelist.getLength(); i++)
		{
			Node tmp = nodelist.item(i);
                        // compareToIgnoreCase return 0 if equal, ignore Case
			if (tmp.getNodeName().compareToIgnoreCase(nodename)==0)
			{
				output = (Element) tmp;
				break;
			}
		}
		return output;
	}
	public static String ReadFile(String stFile) throws Exception
	{
		String path = stFile;
		String st="";
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList consequentcol = doc.getElementsByTagName("consequent");
		for(int j=0; j < consequentcol.getLength(); j++)
		{
			Node consequent = consequentcol.item(j);
			String nodename = consequent.getNodeName();
			if (nodename.compareToIgnoreCase("consequent")==0)
			{
				Element entry = findElement(consequent.getChildNodes(), "relation");
				st=st + findElement(consequent.getChildNodes(), "relation").getAttribute("value").toString();
				//+"  "+entry.getAttribute("type").toString()
				//+"  "+entry.getParentNode().getParentNode().getAttributes().item(1).getNodeValue().toString();
				st=st+"</br>";
			}
		}
		/*NodeList entryset = doc.getElementsByTagName("q");
		for(int i=0; i < entryset.getLength(); i++)
		{
			Element entry = (Element) entryset.item(i);
			if(entry.getAttribute("type").toString().compareTo("OTHER")!=0)
			{
				st=st
				//+ entry.getAttribute("id").toString()
				//+"  "+entry.getAttribute("type").toString()
				+"  "+entry.getParentNode().getParentNode().getAttributes().item(1).getNodeValue().toString();
				st=st+"</br>";
			}
		}*/
		return st;
	}
	public static String findUEfromDic(String UE_key) throws Exception
	{
		String path = GateNamedEntity.serverpath + "UEVNDic.xml";

		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList entryset = doc.getElementsByTagName("entry");
		for(int i=0; i < entryset.getLength(); i++)
		{
			Element entry = (Element) entryset.item(i);
			String UEvalue = entry.getAttribute("UEvalue");
			if (checkValue(UEvalue, UE_key))
			{
				return "UE_"+entry.getAttribute("UEclass");
			}
		}
		return "";
	}
	public static String findREfromDic(String RE_key) throws Exception
	{
		String path = GateNamedEntity.serverpath + "testReDic.xml";
		String result = "";
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList entryset = doc.getElementsByTagName("entry");
		for(int i=0; i < entryset.getLength(); i++)
		{
			Element entry = (Element) entryset.item(i);
			String REvalue = entry.getAttribute("Revalue");
			if (checkValue(REvalue, RE_key))
			{
				result += entry.getAttribute("Reclass")+",";
			}
		}
                if (!result.equalsIgnoreCase("")) result = result.substring(0, result.length()-1);
		return result;
	}
	public static String findIEfromDic(String UE_key) throws Exception
	{
		String path = GateNamedEntity.serverpath + "IEDic.xml";

		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList entryset = doc.getElementsByTagName("entry");
		for(int i=0; i < entryset.getLength(); i++)
		{
			Element entry = (Element) entryset.item(i);
			String UEvalue = entry.getAttribute("IEvalue");
			if (checkValue(UEvalue, UE_key))
			{
				return entry.getAttribute("IEclass");
			}
		}
		return "";
	}
	public static boolean checkValue(String listvalue, String value)
	{
		if (listvalue.compareToIgnoreCase("")==0)
			return true;

		String[] valuelist = listvalue.split(",");
		for(int i=0; i < valuelist.length; i++)
		{
			if (valuelist[i].trim().compareToIgnoreCase(value)==0)
				return true;
		}

		return false;
	}

	public static boolean IsChildClass(String elementclass, String parentclass) throws Exception
	{
		if(parentclass.compareToIgnoreCase("")==0)
			return true;
		return CheckRelationConstraint.checkChildClass(elementclass,parentclass);
	}
	public static boolean IsChildClassList(String elementclass, String parentclasslist) throws Exception
	{
		if(parentclasslist.compareToIgnoreCase("")==0)
			return true;
		String[] valuelist = parentclasslist.split(",");
		for(int i=0; i < valuelist.length; i++)
		{
			if (CheckRelationConstraint.checkChildClass(elementclass,valuelist[i]))
				return true;
		}
		return false;
	}
	public static boolean checkItem(Element orgitem, ItemType item) throws Exception
	{
		String value = orgitem.getAttribute("value");
		String className = orgitem.getAttribute("className");
		String classtype = orgitem.getAttribute("classType");
		String wordfollow = orgitem.getAttribute("wordfollow");
		String wordbefore = orgitem.getAttribute("wordbefore");
		String itemClass = item.className;
		if (itemClass.length() >3 && itemClass.substring(0, 2).compareTo("UE")==0)
			itemClass = itemClass.substring(3);
		if (checkValue(value, item.value) && checkValue(wordfollow, item.wordfollow) && checkValue(wordbefore, item.wordbefore) &&  checkValue(classtype, item.classType)
				&& ( checkValue(className, itemClass) || IsChildClass(itemClass, className)))
			return true;

		return false;
	}
	public static boolean checkPremiseWithEntityandRelation(Node premise, ItemType entity, ItemType relation, ItemType agent) throws Exception
	{
		String subjectclass = findElement(premise.getChildNodes(), "subject").getAttribute("className");
		String relationword =  findElement(premise.getChildNodes(), "relation").getAttribute("value");
		String objectclass =  findElement(premise.getChildNodes(), "object").getAttribute("className");

		//if(subjectclass.compareToIgnoreCase("")==0) return false;
		if(relationword.compareToIgnoreCase("")==0) return false;
		//if(objectclass.compareToIgnoreCase("")==0) return false;

		if(!checkValue(relationword,relation.value)) return false;

		if(IsChildClassList(entity.className,subjectclass))
		{
			if(objectclass.compareToIgnoreCase("")==0) return false;
			agent.className=objectclass.trim().split(",")[0].trim();
			agent.value=agent.className;
			agent.quantifier="?";
			agent.delete=false;
			return true;
		}

		if(IsChildClassList(entity.className,objectclass))
		{
			if(subjectclass.compareToIgnoreCase("")==0) return false;
			agent.className=subjectclass.trim().split(",")[0].trim();
			agent.value=agent.className;
			agent.quantifier="?";
			agent.delete=false;
			return true;
		}

		return false;
	}
	public static String checkPremiseWith2Entity(Node premise, ItemType entity1, ItemType entity2) throws Exception
	{
		String st="";
		String subjectclass = findElement(premise.getChildNodes(), "subject").getAttribute("className");
		String relationword =  findElement(premise.getChildNodes(), "relation").getAttribute("value");
		String objectclass =  findElement(premise.getChildNodes(), "object").getAttribute("className");

		if(subjectclass.compareToIgnoreCase("")==0) return st;
		if(relationword.compareToIgnoreCase("")==0) return st;
		if(objectclass.compareToIgnoreCase("")==0) return st;

		if(IsChildClass(entity1.className,subjectclass)&IsChildClass(entity2.className,objectclass))
		{
			st = relationword.trim().split(",")[0].trim();
			return st;
		}
		if(IsChildClass(entity2.className,subjectclass)&IsChildClass(entity1.className,objectclass))
		{
			st = relationword.trim().split(",")[0].trim();
			return st;
		}
		return st;
	}
	public static void PreProcess(QueryBuffer buffer) throws Exception
	{
		if(buffer.length<2) return;

		ItemType firstUE = null;
		ItemType secondUE = null;

		if(buffer.getItem(0).className.compareToIgnoreCase("UE_Agent")==0)
		{
			for(int i=0;i<buffer.length;i++)
			{
				if(buffer.getItem(i).classType.compareToIgnoreCase("RW")!=0)
				{
					if(buffer.getItem(i).classType.compareToIgnoreCase("UE")==0)
					{
						firstUE = buffer.getItem(i);
						for(int j=i+1;j<buffer.length;j++)
						{
							if(buffer.getItem(j).classType.compareToIgnoreCase("RW")!=0)
							{
								if(buffer.getItem(j).classType.compareToIgnoreCase("UE")==0)
								{
									secondUE = buffer.getItem(j);
									if(firstUE.className.compareToIgnoreCase(secondUE.className)==0)
									{
										firstUE.delete=true;
										return;
									}
								}
								break;
							}
						}
					}
					break;
				}
			}
		}
//		if(buffer.query.toLowerCase().startsWith("where"))
//		{
//			if(buffer.getItem(buffer.length-1).classType.compareToIgnoreCase("UE")==0)
//			{
//				buffer.getItem(0).delete=true;
//			}
//			buffer.getItem(0).value="Location";
//			if(buffer.getItem(2)!=null)
//			{
//				if(buffer.getItem(2).classType.compareToIgnoreCase("UE")==0)
//				{
//					if(buffer.getItem(2).value.compareToIgnoreCase("Location")==0) buffer.getItem(2).delete=true;
//				}
//			}
/*			if(buffer.getItem(buffer.length-1).classType.compareToIgnoreCase("RW")!=0)
			{
				/*if(buffer.getItem(1).classType.compareToIgnoreCase("RW")==0)
				{
					buffer.getItem(1).value="in";
				}
				else* /
					buffer.InsertBeforeItem(0, "á»Ÿ", "RW", "RW");
			}*/

//		}
	}
	public static void FindClassofAgent(QueryBuffer buffer) throws Exception
	{
		PreProcess(buffer);
		if(buffer.length==0) return;
		int count=0;
		for(int i=0; i < buffer.length; i++)
		{
			if ((buffer.getItem(i).classType.compareTo("UE")==0)||
					( buffer.getItem(i).classType.compareTo("IE")==0))
			{
				if (!buffer.getItem(i).delete)
					count=count+1;
			}
		}

		if(buffer.getItem(0).className.compareToIgnoreCase("UE_Agent")==0
            && (buffer.getItem(0).value.compareToIgnoreCase("What")==0
            || buffer.getItem(0).value.compareToIgnoreCase("Who")==0))
		{
			ItemType agent=buffer.getItem(0);
			ItemType firstIE=null;
			for(int i=1;i<buffer.length;i++)
			{
				firstIE=buffer.getItem(i);
				if(firstIE.classType.compareToIgnoreCase("RW")!=0)
				{
					if(firstIE.classType.compareToIgnoreCase("UE")==0)
					{
						if(count<3) return;
						agent.delete=true;
						return;//thuc the dau tien khong phai IE
					}
					if(firstIE.classType.compareToIgnoreCase("IE")==0)
					{
						break;
					}
					if(i==buffer.length-1) return;
				}
			}
			if(firstIE==null) return;
			if(firstIE.delete) return;
			ItemType lastRW=buffer.getItem(buffer.length-1);
			if(lastRW.classType.compareToIgnoreCase("RW")!=0)
			{
				if(count<3) return;
				agent.delete=true;
				return;
			}

//			String path = GateNamedEntity.serverpath + "ENTransformrules.xml";
//
//			DOMParser parser = new DOMParser();
//			parser.parse(path);
//			Document doc = parser.getDocument();
//			agent.delete=true;
//			NodeList ruleset = doc.getElementsByTagName("rule");
//			for(int i=0; i < ruleset.getLength(); i++)
//			{
//				Element rule = (Element) ruleset.item(i);
//				Node premiselist = ProcessingXML.findElement(rule.getChildNodes(), "premiselist");
//				if (premiselist != null)
//				{
//					NodeList premisecol = premiselist.getChildNodes();
//					for(int j=0; j < premisecol.getLength(); j++)
//					{
//						Node premise = premisecol.item(j);
//						if (premise.getNodeName().compareToIgnoreCase("premise")==0)
//						{
//							if(ProcessingXML.checkPremiseWithEntityandRelation(premise,firstIE,lastRW,agent))
//							{
//								return;
//							}
//						}
//					}
//
//				}
//			}
		}
	}
public static ItemType GetItemBetween2Item(ItemType item1, ItemType item2, QueryBuffer buffer) throws Exception
	{
		int index1=-1;
		int index2=-1;
		for(int i=0; i<buffer.length; i++)
		{
			if(buffer.getItem(i)==item1)
			{
				index1=i;
			}
			if(buffer.getItem(i)==item2)
			{
				index2=i;
			}
		}
		if(index1==-1) return null;
		if(index2==-1) return null;
		int min=index1;
		if(index2<index1) min=index2;
		int max=index2;
		if(index2<index1) max=index1;
		if(max-min!=2) return null;
		return buffer.getItem(min+1);
	}
	public static String GetStringBetween2Item(ItemType item1, ItemType item2, QueryBuffer buffer) throws Exception
	{
		if(item2.start<=item1.end) return "error";
		return buffer.query.substring((int)item1.end, (int)item2.start);
	}
	public static boolean checkPremise(Node premise, TripleType querytriple) throws Exception
	{
		Element subject = findElement(premise.getChildNodes(), "subject");
		Element relation =  findElement(premise.getChildNodes(), "relation");
		Element object =  findElement(premise.getChildNodes(), "object");

		//subject class the same as object class
		String subjclass = subject.getAttribute("className");
		if (subjclass.compareToIgnoreCase("objclass")==0)
		{
			String objclass = querytriple.object.className;
			if (objclass.length() >3 && objclass.substring(0, 2).compareTo("UE")==0)
				objclass = objclass.substring(3);
			if(IsChildClass(querytriple.subject.className,objclass))
				subject.setAttribute("className", objclass);
		}
		//object class the same as subject class
		String objclass = object.getAttribute("className");
		if (objclass.compareToIgnoreCase("subjclass")==0)
		{
			subjclass = querytriple.subject.className;
			if (subjclass.length() >3 && subjclass.substring(0, 2).compareTo("UE")==0)
				subjclass = subjclass.substring(3);
			if(IsChildClass(querytriple.object.className,subjclass))
				object.setAttribute("className", subjclass);
		}

		if (checkItem(subject, querytriple.subject) && checkItem(relation, querytriple.rel)
				&& checkItem(object, querytriple.object))
		{
			querytriple.subject.var = subject.getAttribute("var");
			querytriple.rel.var = relation.getAttribute("var");
			querytriple.object.var = object.getAttribute("var");
			return true;
		}

		return false;
	}
	public static void processConsequent(Node consequent, TripleType querytriple) throws Exception
	{
		Element subject = findElement(consequent.getChildNodes(), "subject");
		Element relation = findElement(consequent.getChildNodes(), "relation");
		Element object = findElement(consequent.getChildNodes(), "object");

		String subjclassName = subject.getAttribute("className");
		if (subjclassName.compareTo("")!=0)
		{
			querytriple.subject.className = subjclassName; // override new class
		}

		String subjquantifier = subject.getAttribute("quantifier");
		if (subjquantifier.compareTo("")!=0)
		{
			if (subjquantifier.compareToIgnoreCase("objvalue") == 0)
			{
				querytriple.subject.quantifier = querytriple.object.value;
				querytriple.subject.className = querytriple.object.className.replace("UE_", "");
				querytriple.subject.classType = "IE";
			}
			else
				querytriple.subject.quantifier = subjquantifier; // override new class


		}

		String subjdel = subject.getAttribute("delete");
		if (subjdel.compareTo("yes")==0)
		{
			querytriple.subject.delete = true; // override new class
		}

		String objclassName = object.getAttribute("className");
		if (objclassName.compareTo("")!=0)
		{
			querytriple.object.className = objclassName; // override new class
		}

		String objquantifier = object.getAttribute("quantifier");
		if (objquantifier.compareTo("")!=0)
		{
			if (objquantifier.compareToIgnoreCase("subjvalue")==0)
			{
				querytriple.object.quantifier = querytriple.subject.value;
				querytriple.subject.classType = "IE";
			}
			else
				querytriple.object.quantifier = objquantifier; // override new class
		}

		String objdel = object.getAttribute("delete");
		if (objdel.compareTo("yes")==0)
		{
			querytriple.object.delete = true;
		}

		String reldel = relation.getAttribute("delete");
		if (reldel.compareTo("yes")==0)
		{
			querytriple.rel.delete = true;
		}
	}
	public static void ProcessForAlias(QueryBuffer buffer) throws Exception
	{
		int cntE=0;
		for(int i=0; i < buffer.length; i++)
		{
			if ( buffer.getItem(i).classType.compareTo("RW")!=0)
			{
				if(! buffer.getItem(i).delete) cntE++;
			}
		}
		if(cntE>2)
		{
			for(int i=0; i < buffer.length; i++)
			{
				if((buffer.getItem(i).className.compareToIgnoreCase("UE_Alias")==0)
						&(buffer.getItem(i).value.compareToIgnoreCase("known as")!=0))
				{
					buffer.getItem(i).delete=true;
				}
			}
		}
	}
	public static void Combine2Entity(NodeList ruleset, TripleType querytriple) throws Exception
	{
		if(querytriple.rel.delete) return;
		if(querytriple.object.delete) return;
		if(querytriple.subject.delete) return;
		for(int k=0; k < ruleset.getLength(); k++)
		{
			Element rule = (Element) ruleset.item(k);
			String pri = rule.getAttribute("priority");
			if (Integer.valueOf(pri)==-1)
			{
				Node premiselist = findElement(rule.getChildNodes(), "premiselist");
				if (premiselist != null)
				{
					NodeList premisecol = premiselist.getChildNodes();
					for(int i=0; i < premisecol.getLength(); i++)
					{
						Node premise = premisecol.item(i);
						if (premise.getNodeName().compareToIgnoreCase("premise")==0)
						{
							if (checkPremise(premise, querytriple))
							{
								Node consequentlist = findElement(rule.getChildNodes(), "consequentlist");
								if (consequentlist != null)
								{
									NodeList consequentcol = consequentlist.getChildNodes();
									for(int j=0; j < consequentcol.getLength(); j++)
									{
										Node consequent = consequentcol.item(j);
										String nodename = consequent.getNodeName();
										if (nodename.compareToIgnoreCase("consequent")==0)
										{
											processConsequent(consequent, querytriple);
											return;
										}
									}
								}
							}
//                                                        else
//                                                            if (Intersection(querytriple)) return;
                                                        //dat else o day de tim giao tap hop
						}
					}
				}
			}
			else
			{
				break;
			}
		}
	}
        public static Boolean Intersection(TripleType querytriple) throws Exception
        {
/*            String result="";
            String firstset = InitCode.getRelationSetEntities(querytriple.object.className, querytriple.subject.className);
            String[] first = firstset.split(",");
            String secondset = findREfromDic(querytriple.rel.value);
            String[] second = secondset.split(",");
            for (int i=0; i<first.length;i++)
                for (int j=0;j<second.length;j++){
                    if (first[i].equalsIgnoreCase(second[j])) result+=first[i]+",";
                }
            if (!result.equalsIgnoreCase("")){
                result = result.substring(0, result.length()-1);
                String[] list = result.split(",");
                querytriple.subject.var = querytriple.subject.className;
                querytriple.rel.var=list[0];
                querytriple.rel.className=list[0];
                querytriple.object.var = querytriple.object.className;
                querytriple.relationName=list[0];
                return true;
            }*/
            return false;
        }
/*        public static String Inter(String object, String subject, String rel) throws Exception
        {
            String result="";
            String firstset = InitCode.getRelationSetEntities(object, subject);
            String[] first = firstset.split(",");
            String secondset = findREfromDic(rel);
            String[] second = secondset.split(",");
            for (int i=0; i<first.length;i++)
                for (int j=0;j<second.length;j++){
                    if (first[i].equalsIgnoreCase(second[j])) result+=first[i]+",";
                }
            if (!result.equalsIgnoreCase("")){
                result = result.substring(0, result.length()-1);
                String[] list = result.split(",");
            }
            return result;
        }*/
	public static void CombineEntitys(QueryBuffer buffer) throws Exception
	{
		int count=0;
		for(int i=0; i < buffer.length; i++)
		{
			if ((buffer.getItem(i).classType.compareTo("UE")==0)||
					( buffer.getItem(i).classType.compareTo("IE")==0))
			{
				if (!buffer.getItem(i).delete)
					count=count+1;
			}
		}
		if(count<3) return;
		for(int i=0; i < buffer.length-1; i++)
		{
			ItemType item1 = buffer.getItem(i);
			ItemType item2 = buffer.getItem(i+1);
			if(item2==null)
				break;
			if(	(item2.value.compareToIgnoreCase("where")==0)||
					(item2.value.compareToIgnoreCase("what")==0))
			{
				item2.delete=true;
			}
			if(!item1.delete&!item2.delete)
			{
				if ( item1.classType.compareTo("UE")==0 && item2.classType.compareTo("IE")==0)
				{
					if((GetStringBetween2Item(item1,item2,buffer).trim().length()==0))
					{
						if(IsChildClass(item2.className,item1.className))
						{
							item1.delete=true;
							//item2.value=item1.value;
							count=count-1;
						}
					}
				}
				if ( item1.classType.compareTo("IE")==0 && item2.classType.compareTo("UE")==0)
				{
					if((GetStringBetween2Item(item1,item2,buffer).trim().length()==0))
					{
						if(IsChildClass(item1.className,item2.className))
						{
							item2.delete=true;
							//item1.value=item2.value;
							count=count-1;
						}
					}
				}
				if ( item1.classType.compareTo("UE")==0 && item2.classType.compareTo("IE")==0)
				{
					if(((GetStringBetween2Item(item1,item2,buffer).replace('"', ' ').trim().compareTo(",")==0))
						||((GetStringBetween2Item(item1,item2,buffer).replace('"', ' ').trim().length()==0)))
					{
						if(IsChildClass(item2.className,item1.className))
						{
							item1.delete=true;
							//item2.value=item1.value;
							count=count-1;
						}
					}
				}

				if ( item1.classType.compareTo("UE")==0 && item2.classType.compareTo("UE")==0)
				{
					if((GetStringBetween2Item(item1,item2,buffer).trim().length()==0))
					{
						if(IsChildClass(item2.className,item1.className))
						{
							item1.delete=true;
							count=count-1;
						}
						else
						{
							if(IsChildClass(item1.className,item2.className))
							{
								item2.delete=true;
								count=count-1;
							}
						}
					}
				}
			}
		}
		if(count<3) return;
		String path = GateNamedEntity.serverpath + "ENTransformrules.xml";
		TripleType querytriple = new TripleType();
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		//orderRules(doc);
		NodeList ruleset = doc.getElementsByTagName("rule");
		for(int i=0;i<buffer.length-1;i++)
		{
			ItemType entity1=buffer.getItem(i);
			if((entity1.classType.compareToIgnoreCase("RW")!=0)&(!entity1.delete))
			{
				for(int j=i+1;j<buffer.length;j++)
				{
					ItemType entity2=buffer.getItem(j);
					if((entity2.classType.compareToIgnoreCase("RW")!=0)&(!entity2.delete))
					{
						for(int k=j-1;k>i;k--)
						{
							ItemType relation=buffer.getItem(k);
							if((relation.classType.compareToIgnoreCase("RW")==0)&(!relation.delete))
							{
								querytriple.subject=entity1;
								querytriple.rel=relation;
								querytriple.object=entity2;

								Combine2Entity(ruleset, querytriple);

								if(entity1.delete||entity2.delete)
								{
									count=count-1;
									if(count<3) return;
								}
								break;
							}
						}
						break;
					}
				}
			}
		}
	}
    
    public static void AddRealtion(QueryBuffer buffer) throws Exception {
        int count = 0;
        for (int i = 0; i < buffer.length; i++) {
            if ((buffer.getItem(i).classType.compareTo("UE") == 0) ||
                    (buffer.getItem(i).classType.compareTo("IE") == 0)) {
                if (!buffer.getItem(i).delete) {
                    count = count + 1;
                }
            }
        }

        for (int i = 0; i < buffer.length - 1; i++) {
            ItemType item1 = buffer.getItem(i);
            ItemType item2 = buffer.getItem(i + 1);

            /*

            if ( item1.className.compareTo("City")==0 && item2.className.compareTo("Province")==0)
            {

            if(GetStringBetween2Item(item1,item2,buffer).trim().compareTo("")==0)
            {
            buffer.InsertItem("fakeRW", "RW", "RW", item1.end+1, item1.end+2, "G", item1.value, item2.value);
            System.out.println(buffer.test());
            continue;
            }
            }

            if ( item1.className.compareTo("Country")==0 && item2.className.compareTo("UE_Province")==0)
            {
            if(GetStringBetween2Item(item1,item2,buffer).trim().compareTo("")==0)
            {
            buffer.InsertItem("fakeRW", "RW", "RW", item1.end+1, item1.end+2, "G", item1.value, item2.value);
            System.out.println(buffer.test());
            continue;
            }
            }
             */

            if ((item1.classType.compareTo("IE") == 0 || item1.classType.compareTo("UE") == 0) 
                    && (item2.classType.compareTo("IE") == 0 || item2.classType.compareTo("UE") == 0)
                    && (!item1.className.equalsIgnoreCase("UE_Agent"))) {

                if (GetStringBetween2Item(item1, item2, buffer).trim().compareTo("") == 0) {
                    buffer.InsertItem("fakeRW", "RW", "RW", item1.end + 1, item1.end + 2, "G", item1.value, item2.value);
                    System.out.println(buffer.test());
                    continue;
                }
            }
//			if (item1.classType.compareTo("RW")!=0 && item2.classType.compareTo("RW")!=0 && !item1.delete && !item2.delete)
            if (item1.classType.compareTo("RW")!=0
                    && item1.classType.compareTo(Constants.QUANLITATIVE_ADJ) != 0
                    && item1.classType.compareTo(Constants.QUANTITATIVE_ADJ) != 0
                    && item1.classType.compareTo(Constants.SUPERLATIVE_QUANLITATIVE_ADJ) != 0
                    && item1.classType.compareTo(Constants.SUPERLATIVE_QUANTITATIVE_ADJ) != 0
                    && item1.classType.compareTo(Constants.COMPARATIVE_QUANTITATIVE_ADJ) != 0
                    && item2.classType.compareTo("RW")!=0
                    && !item1.delete && !item2.delete) {

                if (GetStringBetween2Item(item1, item2, buffer).trim().length() == 0) {
                    String path = GateNamedEntity.serverpath + "ENTransformrules.xml";
                    //path = "ENTransformrules.xml";

                    DOMParser parser = new DOMParser();
                    parser.parse(path);
                    Document doc = parser.getDocument();
                    NodeList ruleset = doc.getElementsByTagName("rule");
                    boolean check = false;
                    for (int k = 0; k < ruleset.getLength(); k++) {
                        Element rule = (Element) ruleset.item(k);
                        Node premiselist = ProcessingXML.findElement(rule.getChildNodes(), "premiselist");
                        if (premiselist != null) {
                            NodeList premisecol = premiselist.getChildNodes();
                            for (int j = 0; j < premisecol.getLength(); j++) {
                                Node premise = premisecol.item(j);
                                if (premise.getNodeName().compareToIgnoreCase("premise") == 0) {
                                    String relationst = ProcessingXML.checkPremiseWith2Entity(premise, item1, item2).trim();
                                    if (relationst.length() != 0) {
                                        //hoan vi
                                        if ((item1.classType.compareTo("IE") == 0) ||
                                                (item2.classType.compareTo("IE") == 0)) {
                                            String sttmp = item2.wordbefore;
                                            item2.wordbefore = item1.wordbefore;
                                            item1.wordbefore = sttmp;
                                            sttmp = item2.wordfollow;
                                            item2.wordfollow = item1.wordfollow;
                                            item1.wordfollow = sttmp;

                                            long index = item2.start;
                                            item2.start = item1.start;
                                            item1.start = index;
                                            index = item2.end;
                                            item2.end = item1.end;
                                            item1.end = index;

                                            ItemType tmptype = buffer.buffer[i];
                                            buffer.buffer[i] = buffer.buffer[i + 1];
                                            buffer.buffer[i + 1] = tmptype;
                                        }



                                        check = true;
                                        buffer.InsertAfterItem(i, relationst, "RW", "RW");
                                        k = ruleset.getLength() + 1;

                                        //item2.delete=false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //if(!check) item2.delete=true;
                    if(!check)
					{
						if(count<3) return;
						if(buffer.getItem(i+1)==item2)
						{
							if ( item1.classType.compareTo("UE")==0 && item2.classType.compareTo("UE")==0)item1.delete=true;
							else item2.delete=true;
							count=count-1;
						}
					}
                }
            }
        }
    //ProcessingXML.ProcessForAlias(buffer);
    }


    /***************************************************************************
	 * Get className of IE entity from IEDic.xml
	 *
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public static String specifyMAX_MINfromDic(String entity) throws Exception {
		String path = GateNamedEntity.serverpath + "MaxMinDic.xml";

		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList entryset = doc
				.getElementsByTagName(Constants.MAX_MIN_ENTRY_TAG);
		for (int i = 0; i < entryset.getLength(); i++) {
			Element entry = (Element) entryset.item(i);
			String adjValue = entry
					.getAttribute(Constants.MAX_MIN_ADJVALUE_ATTR);
			if (checkValue(adjValue, entity)) {
				return entry.getAttribute(Constants.MAX_MIN_SUPADJTYPE_ATTR);
			}
		}
		return "";
	}

    public static String specifyOrderTypeFromDic(String entity) throws Exception {
		String path = GateNamedEntity.serverpath + "OrderTypeDic.xml";

		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList entryset = doc
				.getElementsByTagName(Constants.OT_ENTRY_TAG);
		for (int i = 0; i < entryset.getLength(); i++) {
			Element entry = (Element) entryset.item(i);
			String adjValue = entry
					.getAttribute(Constants.OT_ADJ_VALUE_ATTR);
			if (checkValue(adjValue, entity)) {
				return entry.getAttribute(Constants.OT_ORDER_TYPE_ATTR);
			}
		}
		return "";
	}

    public static String specifyComparativeRelFromDic(String cqta) throws Exception {

        String path = GateNamedEntity.serverpath + "ComparativeRelDic.xml";

        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        NodeList entryset = doc
                .getElementsByTagName(Constants.CR_ENTRY_TAG);
        for (int i = 0; i < entryset.getLength(); i++) {
            Element entry = (Element) entryset.item(i);
            String adjValue = entry
                    .getAttribute(Constants.CR_ADJ_VALUE_ATTR);
            if (checkValue(adjValue, cqta)) {
                return entry.getAttribute(Constants.CR_REL_ATTR);
            }
        }
        return "";

    }

    public static boolean checkExceptionRelations(String rel, String domain,
            String range) throws Exception {
        String path = GateNamedEntity.serverpath + "ExceptionRelDic.xml";

        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        NodeList entryset = doc
                .getElementsByTagName(Constants.ER_ENTRY_TAG);
        for (int i = 0; i < entryset.getLength(); i++) {
            Element entry = (Element) entryset.item(i);
            String relValue = entry
                    .getAttribute(Constants.ER_REL_VALUE_ATTR);
            if (checkValue(relValue, rel)) {
                if (domain.equalsIgnoreCase(range)) {
                    return true;
                }
            }
        }
        return false;

    }

	/*public static boolean FindRWwith2IE(Node premise, ItemType item1, ItemType item2) throws Exception
	{
		String subjectclass = findElement(premise.getChildNodes(), "subject").getAttribute("className");
		String relationword =  findElement(premise.getChildNodes(), "relation").getAttribute("value");
		String objectclass =  findElement(premise.getChildNodes(), "object").getAttribute("className");

		if(subjectclass.compareToIgnoreCase("")==0) return false;
		if(relationword.compareToIgnoreCase("")==0) return false;
		if(objectclass.compareToIgnoreCase("")==0) return false;

		if(!checkValue(relationword,relation.value)) return false;

		if(IsChildClass(entity.className,subjectclass))
		{
			agent.className=objectclass;
			agent.value=objectclass;
			agent.quantifier="?";
			agent.delete=false;
			return true;
		}

		if(IsChildClass(entity.className,objectclass))
		{
			agent.className=subjectclass;
			agent.value=subjectclass;
			agent.quantifier="?";
			agent.delete=false;
			return true;
		}

		return false;
	}
	public static void FindRWBetween2IE(QueryBuffer buffer) throws Exception
	{
		for(int i=0;i<buffer.length-1;i++)
		{
			ItemType item1=buffer.getItem(i);
			ItemType item2=buffer.getItem(i+1);
			if((item1.className.compareToIgnoreCase("IE")==0)&(item2.className.compareToIgnoreCase("IE")==0))
			{
				String path = GateNamedEntity.serverpath + "transformrules.xml";

				DOMParser parser = new DOMParser();
				parser.parse(path);
				Document doc = parser.getDocument();
				NodeList ruleset = doc.getElementsByTagName("rule");
				for(int j=0; j < ruleset.getLength(); j++)
				{
					Element rule = (Element) ruleset.item(j);
					Node premiselist = ProcessingXML.findElement(rule.getChildNodes(), "premiselist");
					if (premiselist != null)
					{
						NodeList premisecol = premiselist.getChildNodes();
						for(int k=0; k < premisecol.getLength(); k++)
						{
							Node premise = premisecol.item(j);
							if (premise.getNodeName().compareToIgnoreCase("premise")==0)
							{
								if(ProcessingXML.checkPremiseWithEntityandRelation(premise,firstIE,lastRW,agent))
								{
									return;
								}
							}
						}

					}
				}
			}
		}
	}	*/
}
