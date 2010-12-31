package org.me.VNKIMService;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ProcessingEntity
{
	public static void ProcessEntity(QueryBuffer buffer) throws Exception
	{
		if (buffer.length < 2)
			return;
		for(int i=0; i < buffer.length-1; i++)
		{	
			if ( (buffer.getItem(i).classType.compareTo("UE")==0 || buffer.getItem(i).classType.compareTo("IE")==0) &&
				(buffer.getItem(i+1).classType.compareTo("UE")==0 || buffer.getItem(i+1).classType.compareTo("IE")==0))
			{
				ProcessItems(buffer.getItem(i), buffer.getItem(i+1));
			}				
		}
		DeleteEntity(buffer);
	}
	
	public static void DeleteEntity(QueryBuffer buffer)
	{
		for(int i=0; i < buffer.length; i++)
		{
			if (buffer.getItem(i).delete)
			{
				if ( i < buffer.length)
				{
					for(int j=i; j <buffer.length; j++)
					{
						buffer.setItem(j, j+1);
					}
					buffer.length--;					
				}
				else
				{
					buffer.length--;
				}
			}
		}
	}
	
	public static void ProcessItems(ItemType first_entity, ItemType second_entity) throws Exception
	{
		String path = GateNamedEntity.serverpath + "combiningrules.xml";
		
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		//orderRules(doc);
		NodeList ruleset = doc.getElementsByTagName("rule");		
		for(int i=0; i < ruleset.getLength(); i++)
		{			
			Element rule = (Element) ruleset.item(i);
			Node premise = findElement(rule.getChildNodes(), "premise");
			if (premise != null)
			{
				if (checkPremise(premise, first_entity, second_entity))
				{
					Node consequent = findElement(rule.getChildNodes(), "consequent");
					if (consequent != null)
					{
						processConsequent(premise, consequent, first_entity, second_entity);
						i++;
					}
					break;
				}
			}
		}		
		
	}
	
	
	
	public static boolean checkPremise(Node premise, ItemType first_entity, ItemType second_entity) throws Exception
	{
		Element rule_firstentity = findElement(premise.getChildNodes(), "firstentity");
		Element rule_secondentity =  findElement(premise.getChildNodes(), "secondentity");
		
		if (checkItem(rule_firstentity, first_entity) && checkItem(rule_secondentity, second_entity)) 
		{
			first_entity.var = rule_firstentity.getAttribute("var");
			second_entity.var = rule_secondentity.getAttribute("var");
			return true;
		}
		
		return false;
	}
	
	public static boolean checkItem(Element orgitem, ItemType item) throws Exception
	{		
		String value = orgitem.getAttribute("value");
		String className = orgitem.getAttribute("className");
		String classType = orgitem.getAttribute("classType");
		String itemClass = item.className;
		if (itemClass.length() >3 && itemClass.substring(0, 2).compareTo("UE")==0)
			itemClass = itemClass.substring(3);
		String itemclassType = item.classType;
		if (checkValue(value, item.value) && checkValue(className, itemClass) && checkValue(classType, itemclassType))
			return true;
		
		return false;
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
	
	public static void processConsequent(Node premise, Node consequent, ItemType first_entity, ItemType second_entity) throws Exception
	{
		Element rule_firstentity = findElement(consequent.getChildNodes(),  "firstentity");		
		Element rule_secondentity = findElement(consequent.getChildNodes(),  "secondentity");
		
		String rule_className = rule_firstentity.getAttribute("className");
		if (rule_className.compareTo("")!=0)
		{
			first_entity.className = rule_className; // override new class
		}

		String rule_quantifier = rule_firstentity.getAttribute("quantifier");
		if (rule_quantifier.compareTo("")!=0)
		{ 
			int index = rule_quantifier.indexOf("valueOf");
			if (index != -1)
			{
				String var = rule_quantifier.substring(index + 8, rule_quantifier.indexOf(")", index + 8));
				Element process_entity = findElementbyVar(premise.getChildNodes(), var);
				if (process_entity != null && process_entity.getNodeName().compareToIgnoreCase("firstentity")==0)
				{
					first_entity.quantifier = first_entity.value; 
				}
				else if (process_entity != null && process_entity.getNodeName().compareToIgnoreCase("secondentity")==0)
				{
					first_entity.quantifier = second_entity.value;
				}
				
			}
			else
				first_entity.quantifier = rule_quantifier; // override new class
		}

		String rule_delete = rule_firstentity.getAttribute("delete");
		if (rule_delete.compareTo("yes")==0)
		{
			first_entity.delete = true; // override new class
		}
		

		//second entity
		rule_className = rule_secondentity.getAttribute("className");
		if (rule_className.compareTo("")!=0)
		{
			second_entity.className = rule_className; // override new class
		}

		rule_quantifier = rule_secondentity.getAttribute("quantifier");
		if (rule_quantifier.compareTo("")!=0)
		{ 
			int index = rule_quantifier.indexOf("valueOf");
			if (index != -1)
			{
				String var = rule_quantifier.substring(index + 8, rule_quantifier.indexOf(")", index + 8));
				Element process_entity = findElementbyVar(premise.getChildNodes(), var);
				if (process_entity != null && process_entity.getNodeName().compareToIgnoreCase("firstentity")==0)
				{
					second_entity.quantifier = first_entity.value; 
				}
				else if (process_entity != null && process_entity.getNodeName().compareToIgnoreCase("secondentity")==0)
				{
					second_entity.quantifier = first_entity.value;
				}
				
			}
			else
				second_entity.quantifier = rule_quantifier; // override new class
		}

		rule_delete = rule_secondentity.getAttribute("delete");
		if (rule_delete.compareTo("yes")==0)
		{
			second_entity.delete = true; // override new class
		}
		
	}
	
	public static Element findElement(NodeList nodelist, String nodename) throws Exception
	{
		Element output = null;
		for(int i=0; i < nodelist.getLength(); i++)
		{
			Node tmp = nodelist.item(i);
			if (tmp.getNodeName().compareToIgnoreCase(nodename)==0)
			{
				output = (Element) tmp;
				break;
			}			
		}
		return output;
	}

	public static Element findElementbyVar(NodeList nodelist, String varname) throws Exception
	{
		Element output = null;

		for(int i=0; i < nodelist.getLength(); i++)
		{
			Node tmpnode = nodelist.item(i);
			if (tmpnode.getNodeName().compareToIgnoreCase("firstentity")==0 ||
				tmpnode.getNodeName().compareToIgnoreCase("secondentity")==0)
			{
				Element tmp = (Element) tmpnode;
				if (tmp.getAttribute("var").compareToIgnoreCase(varname)==0)
				{
					output = tmp;
					break;
				}
			}
		}
		return output;
	}


}