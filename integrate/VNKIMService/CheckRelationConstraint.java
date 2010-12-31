package org.me.VNKIMService;

import org.w3c.dom.*;
import org.apache.xerces.parsers.*;

class CheckRelationConstraint
{
	public static boolean checkValidRelation(String relation, int dir, TripleType querytriple) throws Exception
	{
		String path = GateNamedEntity.serverpath + "ENConstraints.xml";
		boolean match = false;
		boolean match1 = false;
		boolean match2 = false;
		boolean result1 = false;
		boolean result2 = false;
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();		
		NodeList constraintset = doc.getElementsByTagName("constraint");
		
		for(int i=0; i < constraintset.getLength(); i++)
		{			
			Element constraint = (Element) constraintset.item(i);
			String rel = constraint.getAttribute("value");
			if ( rel.compareToIgnoreCase(relation)==0)
			{
				match = true;
				String objClass = constraint.getAttribute("objclass");
				String subjClass = constraint.getAttribute("subjclass");
				String qsubjClass = querytriple.subject.className;
				if (qsubjClass.length() >3 && qsubjClass.substring(0, 2).compareToIgnoreCase("UE")==0)
					qsubjClass = qsubjClass.substring(3);
				String qobjClass = querytriple.object.className;				
				if (qobjClass.length() >3 && qobjClass.substring(0, 2).compareToIgnoreCase("UE")==0)
					qobjClass = qobjClass.substring(3);
				
				if (dir == 2) //swap
				{
					String tmp = qsubjClass;
					qsubjClass = qobjClass;
					qobjClass =  tmp;
				}
				
				if (objClass.compareTo("")!=0)
				{
					match1 = true;
					if (objClass.compareToIgnoreCase(qobjClass)==0)
						result1 = true;
					else
					{
						result1 = checkChildClass(qobjClass, objClass);
						if(!result1)result1 = checkChildClass(objClass, qobjClass);
					}
				}

				if (subjClass.compareTo("")!=0)
				{
					match2 = true;
					if (subjClass.compareToIgnoreCase(qsubjClass)==0)
						result2 = true;
					else
						result2 = checkChildClass(qsubjClass, subjClass);
				}
				break;
			}
			
		}		
		
		if (match)
		{
			if (match1 && match2 )
				return result1 && result2;
			else if (match1)
				return result1;
			else if (match2)
				return result2;
			else
				return true;
		}
		
		return true;
	}
	
	public static boolean checkAliasValid(String class1, String class2) throws Exception
	{
		class1=class1.replace("UE_", "");
		class2=class2.replace("UE_", "");
		if ( class1.compareToIgnoreCase(class2)==0)
			return true;
		String path = GateNamedEntity.serverpath + "constraints.xml";
		boolean result = checkChildClass2(class1,class2);
		if(!result)result = checkChildClass2(class2,class1);
		result=true;
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();		
		NodeList relset = doc.getElementsByTagName("AliasContraint");
		for(int i=0; i < relset.getLength(); i++)
		{
			Element relationship = (Element) relset.item(i);
			String pclass1 = relationship.getAttribute("class1");
			String pclass2 = relationship.getAttribute("class2");	
			if((checkChildClass(class1,pclass1)&checkChildClass(class2,pclass2))
				||(checkChildClass(class1,pclass2)&checkChildClass(class2,pclass1)))
			{
				String value = relationship.getAttribute("value");
				if(value.compareToIgnoreCase("false")==0)
				{
					result=false;
				}
				else
				{
					result=true;
				}
				break;
			}
		}		
		return result;		
	}
	
	public static boolean checkChildClass2(String elementclass, String parentclass) throws Exception
	{
		if(parentclass.compareToIgnoreCase("")==0)
			return true;
		return checkChildClass(elementclass,parentclass);
	}
	public static boolean checkChildClass(String elementclass, String parentclass) throws Exception
	{
//                if (elementclass.compareToIgnoreCase("Noparent")==0) return false;
		elementclass=elementclass.replace("UE_", "");
		parentclass=parentclass.replace("UE_", "");
		if ( elementclass.compareToIgnoreCase(parentclass)==0)
			return true;		
		String path = GateNamedEntity.serverpath + "ENConstraints.xml";
		boolean result = false;
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();		
		NodeList relset = doc.getElementsByTagName("relationship");
		for(int i=0; i < relset.getLength(); i++)
		{
			Element relationship = (Element) relset.item(i);
			String pclass = relationship.getAttribute("parentclass");
			
			String[] valuelist = parentclass.split(",");
			for(int j=0; j < valuelist.length; j++)
			{			 
				if (valuelist[j].trim().compareToIgnoreCase(pclass)==0)
				{
					if ( checkValue(relationship.getAttribute("value"),elementclass))
					{
						return true;
					}
				}
			}
		}
/*                String parent = FindParent(elementclass);
                String[] listparent = parent.split(", ");
                for (int k=0; k<listparent.length; k++)
                    if (checkChildClass(listparent[k], parentclass)) return true;*/
		return result;
	}

	public static String FindParent(String elementclass) throws Exception
	{
		String path = GateNamedEntity.serverpath + "constraints.xml";
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		NodeList relset = doc.getElementsByTagName("relationship");
		for(int i=0; i < relset.getLength(); i++)
		{
			Element relationship = (Element) relset.item(i);
                        String val = relationship.getAttribute("value");
                        if (checkValue(val, elementclass)){
                            String result = relationship.getAttribute("parentclass");
                            return result;
                        }
		}
		return "Noparent";
	}

	public static boolean checkCombineEntity(ItemType entity1, String stBetween, ItemType entity2) throws Exception
	{
		String class1=entity1.className.replace("UE_", "");
		String class2=entity2.className.replace("UE_", "");
		
		String path = GateNamedEntity.serverpath + "constraints.xml";
		boolean result = false;
		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();	
		NodeList relset = doc.getElementsByTagName("CombineContraint");
		for(int i=0; i < relset.getLength(); i++)
		{
			Element relationship = (Element) relset.item(i);
			String pclass1 = relationship.getAttribute("class1");
			String pclass2 = relationship.getAttribute("class2");
			String pvalue1 = relationship.getAttribute("evalue1");
			String pvalue2 = relationship.getAttribute("evalue2");
			String value = relationship.getAttribute("value");
			if((checkChildClass2(class1,pclass1)&checkChildClass2(class2,pclass2))
				||(checkChildClass2(class1,pclass2)&checkChildClass2(class2,pclass1)))
			{
				if((checkValue(pvalue1,entity1.value)&checkValue(pvalue2,entity2.value))
						||(checkValue(pvalue2,entity1.value)&checkValue(pvalue1,entity2.value)))
					{
						if(checkValue(value, stBetween))
						{
							return true;
						}
					}
			}
		}		
		return result;		
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
}