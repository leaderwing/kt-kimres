/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.*;

import org.w3c.dom.*;
import org.apache.xerces.parsers.*;

/*
import sun.io.Converters;
import BayesianNetworks.*;
import BayesianInferences.*;
 */
class ProcessingQuery {

    public static boolean IsValid = true;
    public static boolean IncludeML = true;
    public static String debug = "";
	private static final String HAS_POSITION = "hasPosition";

	private static final String PERSON = "Person";

    public static void setML(boolean value) {
        IncludeML = value;
    }

   /**
	 * Find triple that matched with relationship of (subject, relation, object)
	 * or (object, relation, subject) and update relation to map with it.
	 *
	 * @param subject
	 * @param relation
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static TripleType CheckRE(ItemType subject, ItemType relation,
			ItemType object) throws Exception {
		QueryTriple tripleset = new QueryTriple();
		relation.relation = "";

		TripleType tmp = new TripleType();
		if (subject.delete
				|| subject.value.equalsIgnoreCase(Constants.HOW_MANY_STRING))
				//|| subject.value.equalsIgnoreCase(Constants.HOW_MUCH_STRING))
			return tmp;
		if (object.delete
				|| object.value.equalsIgnoreCase(Constants.HOW_MANY_STRING))
				//|| object.value.equalsIgnoreCase(Constants.HOW_MUCH_STRING))
			return tmp;
		if (relation.delete)
			return tmp;

		tmp.subject = subject;
		tmp.rel = relation;
		tmp.object = object;
		tripleset.tripleset[tripleset.length] = tmp;
		tripleset.length++;

		// Don't have relation between subject and object
		if (relation.relation.length() == 0) {
			// Find tripleset that matched with relationship of (subject,
			// relation, object) by rules and dictionary

			tripleset.currindex = 0;
			findRelationbyRules(tripleset, 0);
			//findRelationfromDic(tripleset);
		}

		if (relation.relation.length() == 0) {
			// Find tripleset that matched with relationship of (object,
			// relation, subject) by rules and dictionary

			tmp.subject = object;
			tmp.rel = relation;
			tmp.object = subject;
			// findRelationbyRules(tripleset, 1);
			if (relation.relation.length() == 0) {
				tripleset.currindex = 0;
				findRelationbyRules(tripleset, 0);
				//findRelationfromDic(tripleset);
			}
		}
		tmp.direction = relation.direction;
		tmp.relationName = relation.relation.replace(",", "");

		if (tmp.relationName.indexOf("hasAlias") >= 0) {
			if (tmp.relationName.compareToIgnoreCase("hasAlias") != 0) {
				tmp.relationName = tmp.relationName.replaceAll("hasAlias", "");
			}
		}

		return tmp;
	}

    public static boolean CheckValidRE(TripleType tmp1, QueryBuffer buffer) throws Exception {
        if (tmp1.rel != null) {
            ItemType item = ProcessingXML.GetItemBetween2Item(tmp1.subject, tmp1.object, buffer);
            if (item != null) {
                if (item.value.trim().compareToIgnoreCase("'s") == 0) {
                    ItemType tmptype = tmp1.subject;
                    tmp1.subject = tmp1.object;
                    tmp1.object = tmptype;
                    if (tmp1.direction == 1) {
                        tmp1.direction = 2;
                    } else {
                        tmp1.direction = 1;
                    }
                }
            }
        }
        if (true) {
            return true;
        }
        boolean check = true;
        if (tmp1.subject == null) {
            return false;
        }
        if (tmp1.object == null) {
            return false;
        }
        if (tmp1.rel == null) {
            return false;
        }
        if (tmp1.subject.isUsed) {
            return false;
        }
        if (tmp1.object.isUsed) {
            return false;
        }
        if (tmp1.rel.isUsed) {
            return false;
        }
        if (tmp1.object != null) {
            if (tmp1.object.className != null) {
                if (tmp1.object.className.substring(3).compareToIgnoreCase("Alias") == 0) {
                    return true;
                }
            }
        }
        if (tmp1.isDelete) {
            return false;
        }
        if (tmp1.rel != null) {
            String st = "ABC";
            if (tmp1.subject.end < tmp1.object.start) {
                st = ProcessingXML.GetStringBetween2Item(tmp1.subject, tmp1.object, buffer);
            }
            if (tmp1.object.end < tmp1.subject.start) {
                st = ProcessingXML.GetStringBetween2Item(tmp1.object, tmp1.subject, buffer);
            }
            if (st.trim().compareToIgnoreCase("'s") == 0) {
                ItemType tmptype = tmp1.subject;
                tmp1.subject = tmp1.object;
                tmp1.object = tmptype;
                if (tmp1.direction == 1) {
                    tmp1.direction = 2;
                } else {
                    tmp1.direction = 1;
                }
            }
            if (true) {
                return true;
            }
            if (tmp1.rel.value.compareToIgnoreCase("exp") == 0) {
                tmp1.isDelete = true;
                check = false;
                if ((tmp1.subject.classType.compareToIgnoreCase("IE") == 0) & (tmp1.object.classType.compareToIgnoreCase("IE") == 0)) {
                    for (int j = 0; j < buffer.length - 2; j++) {
                        if ((buffer.getItem(j) == tmp1.subject) & (buffer.getItem(j + 2) == tmp1.object)) {
                            if (buffer.getItem(j + 1) == tmp1.rel) {
                                tmp1.isDelete = false;
                                tmp1.object.isUsed = true;
                                check = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        /*if(tmp1.relationName.compareToIgnoreCase("hasAlias")==0)
        {
        for(int j=0;j<buffer.length;j++)
        {
        if(buffer.getItem(j)==tmp1.subject)
        {
        if(!tmp1.isDelete)
        {
        check=CheckRelationConstraint.checkAliasValid(tmp1.subject.className, tmp1.object.className);
        if(!check) tmp1.isDelete=true;
        }
        break;
        }
        }
        }*/
        return check;
    }

    public static QueryTriple ProcessQuery2Triple(QueryBuffer buffer) throws Exception {
        QueryTriple tripleset = new QueryTriple();
        if (buffer.length < 2) {
            return tripleset;
        }
        for (int i = 0; i < buffer.length; i++) {
            if (!buffer.getItem(i).delete) {
                if ((buffer.getItem(i).classType.compareTo("IE") == 0) || (buffer.getItem(i).classType.compareTo("UE") == 0)) {
                    for (int j = i + 1; j < buffer.length; j++) {
                        if (((buffer.getItem(j).classType.compareTo("IE") == 0) || (buffer.getItem(j).classType.compareTo("UE") == 0)) && (!buffer.getItem(j).delete)) {
                            boolean check = false;
                            for (int k = j - 1; k > i; k--) {
                                if (buffer.getItem(k).classType.compareTo("RW") == 0) {
                                    String oldValue = buffer.getItem(j).value;
                                    if ((buffer.getItem(j).classType.compareToIgnoreCase("UE") == 0) &&
                                            (buffer.getItem(j - 2).classType.compareToIgnoreCase("UE") == 0) &&
                                            (buffer.getItem(j - 2).delete)) {

                                        buffer.getItem(j).value = buffer.getItem(j - 2).value;
                                    }
                                    TripleType tmp = CheckRE(buffer.getItem(i), buffer.getItem(k), buffer.getItem(j));
                                    if (tmp.relationName.length() == 0) {
                                        tmp = InterRE(buffer.getItem(i), buffer.getItem(k), buffer.getItem(j));
                                    }
                                    buffer.getItem(j).value = oldValue;
                                    if ((tmp.relationName.length() != 0)) {
                                        tripleset.tripleset[tripleset.length] = tmp;
                                        tripleset.length++;

                                        //For RemoveTriple()
                                        if (buffer.getItem(i).classType.equalsIgnoreCase(Constants.UE)
                                            && (buffer.getItem(j).start - buffer.getItem(i).end > 1)) {

                                            //Save the previous nearest UE, except for which right before it
                                            buffer.getItem(j).preNearestUE = buffer.getItem(i);
                                            
                                        }
                                        
                                        check = true;
                                        debug += tmp.relationName + ";" + i + ";" + k + ";" + j + ";";

                                        break;
                                    }
                                }
                            }
                            if (!check) {
                                for (int k = j + 1; k < buffer.length; k++) {
                                    if (buffer.getItem(k).classType.compareTo("RW") == 0) {
                                        TripleType tmp = CheckRE(buffer.getItem(i), buffer.getItem(k), buffer.getItem(j));
                                        //debug += "toi day";
                                        if (tmp.relationName.length() == 0) {
                                            tmp = InterRE(buffer.getItem(i), buffer.getItem(k), buffer.getItem(j));
                                        }
                                        if ((tmp.relationName.length() != 0) && (CheckValidRE(tmp, buffer))) {
                                            //debug += tmp.relationName+"vao day sau";
                                            tripleset.tripleset[tripleset.length] = tmp;
                                            tripleset.length++;
                                            check = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!check) {
                                for (int k = i - 1; k >= 0; k--) {
                                    if (buffer.getItem(k).classType.compareTo("RW") == 0) {
                                        TripleType tmp = CheckRE(buffer.getItem(i), buffer.getItem(k), buffer.getItem(j));
                                        if (tmp.relationName.length() == 0) {
                                            tmp = InterRE(buffer.getItem(i), buffer.getItem(k), buffer.getItem(j));
                                        }
                                        if ((tmp.relationName.length() != 0) && (CheckValidRE(tmp, buffer))) {
                                            //debug = tmp.relationName+"vao day";
                                            tripleset.tripleset[tripleset.length] = tmp;
                                            tripleset.length++;
                                            check = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return tripleset;
    }

    public static void DeleteEntity(QueryBuffer buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer.getItem(i).delete) {
                if (i < buffer.length) {
                    for (int j = i; j < buffer.length; j++) {
                        buffer.setItem(j, j + 1);
                    }
                    buffer.length--;
                    i--;
                } else {
                    buffer.length--;
                    i--;
                }
            }
        }
    }

    public static void PreRecognizeEntity(QueryBuffer buffer) throws Exception {
        String query = buffer.query;
//		query = query.replace("'s", " 's");
        query.trim();
        /*if(query.toLowerCase().startsWith("what is"))
        {
        query=query.substring(7);
        }
        if(query.toLowerCase().startsWith("what"))
        {
        query=query.substring(4);
        }*/
        if (query.toLowerCase().startsWith("ai")) {
            query = "Person " + query.substring(3);
        //buffer.InsertItem("Person", "Person", "UE", 0, 2, "K",AnnieER.getWordFollow(query, 2), "");
        }
        if (query.toLowerCase().startsWith("á»Ÿ Ä‘Ã¢u lÃ ")) {
            query = "In Location " + query.substring(8);
        //buffer.InsertItem("Location", "Location", "UE", 0, 4, "K", AnnieER.getWordFollow(query, 4), "");
			/*buffer.InsertItem("of", "RW", "RW", 0, 4, "K",
        AnnieER.getWordFollow(query, 4), "Location");*/
        }
        if (query.toLowerCase().startsWith("á»Ÿ Ä‘Ã¢u")) {
            query = "In Location " + query.substring(5);
        //buffer.InsertItem("Location", "Location", "UE", 0, 4, "K", AnnieER.getWordFollow(query, 4), "");
			/*buffer.InsertItem("of", "RW", "RW", 0, 4, "K",
        AnnieER.getWordFollow(query, 4), "Location");*/
        }
        //query = StemmingWords(buffer, query);
        buffer.query = query;
    }

    public static void PreProcessQuery(QueryBuffer buffer) throws Exception {
        int cntE = 0;
        for (int i = 0; i < buffer.length - 1; i++) {
            ItemType item1 = buffer.getItem(i);
            ItemType item2 = buffer.getItem(i + 1);
            if (item1.classType.compareTo("IE") == 0 && item2.classType.compareTo("IE") == 0) {
                if (CheckRelationConstraint.checkChildClass(item1.className, "location") && CheckRelationConstraint.checkChildClass(item2.className, "location")) {
                    buffer.InsertAfterItem(i, "exp", "RW", "RW");
                }
            }
            if (item1.classType.compareTo("UE") == 0) {
                if ((item1.className.substring(3).compareToIgnoreCase(item2.className) == 0) & (item2.classType.compareTo("IE") == 0) &
                        (buffer.query.substring((int) item1.end, (int) item2.start)).replace('"', ' ').trim().compareTo(",") == 0) {
                    item1.delete = true;
                }
            }
            if (!item1.delete) {
                if (item1.classType.compareTo("RW") != 0) {
                    for (int j = i + 1; j < buffer.length; j++) {
                        item2 = buffer.getItem(j);
                        if (item2.classType.compareTo("RW") != 0) {
                            if (!((item1.classType.compareTo("UE") == 0) & (item2.classType.compareTo("UE") == 0))) {
                                if (!((item1.classType.compareTo("IE") == 0) & (item2.classType.compareTo("IE") == 0))) {
                                    {
                                        if (item2.start > item1.end) {
                                            String st = (buffer.query.substring((int) item1.end, (int) item2.start)).replace('"', ' ').trim();
                                            if (CheckRelationConstraint.checkCombineEntity(item1, st, item2)) {
                                                if ((item2.classType.compareTo("IE") == 0) & (item1.classType.compareTo("UE") == 0)) {
                                                    item2.delete = true;
                                                    item1.quantifier = item2.value;
                                                    item1.classType = "IE";
                                                } else {
                                                    if ((item1.classType.compareTo("IE") == 0) & (item2.classType.compareTo("UE") == 0)) {
                                                        item1.delete = true;
                                                        item2.quantifier = item1.value;
                                                        item2.classType = "IE";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            /*if((!item1.delete)&(item1.classType.compareTo("UE")==0))
            {
            for(int j=i+1;j<buffer.length; j++)
            {
            item2 = buffer.getItem(j);
            if (item2.classType.compareTo("IE")==0)
            {
            String st=(buffer.query.substring((int)item1.end, (int)item2.start)).replace('"', ' ').trim();
            if((item2.className.compareToIgnoreCase(item1.className.substring(3))==0)
            ||IsChildClass(item2.className, item1.className.substring(3)))
            {
            if((st.compareToIgnoreCase("whose name is")==0)||
            (st.compareToIgnoreCase("whose name are")==0)||
            (st.compareToIgnoreCase("name is")==0)||
            (st.compareToIgnoreCase("name are")==0)||
            (st.compareToIgnoreCase("called")==0)||
            (st.compareToIgnoreCase("'s name is")==0)||
            (st.compareToIgnoreCase("'s name are")==0)||
            (st.compareToIgnoreCase("is")==0)||
            (st.compareToIgnoreCase("are")==0)||
            (st.compareToIgnoreCase("that its name is")==0))
            {
            item2.delete=true;
            item1.className=item2.className;
            item1.quantifier=item2.value;
            item1.classType="IE";
            break;
            }
            }
            }
            }
            }*/

            if (item1.classType.compareTo("RW") != 0) {
                if (!item1.delete) {
                    cntE++;
                }
            }
        /*else if (item1.className.compareTo("CONJ")==0 && item2.classType.compareTo("IE")==0)
        {
        if (i-2 > 0 && buffer.getItem(i-2).classType.compareTo("RW")==0)
        buffer.InsertAfterItem(i, buffer.getItem(i-2).value, buffer.getItem(i-2).className, buffer.getItem(i-2).classType);
        }*/
        }
        if (buffer.length > 0) {
            if (buffer.getItem(buffer.length - 1).classType.compareTo("RW") != 0) {
                if (!buffer.getItem(buffer.length - 1).delete) {
                    cntE++;
                }
            }
        }
        if (cntE > 2) {
            for (int i = 0; i < buffer.length; i++) {
                if ((buffer.getItem(i).className.compareToIgnoreCase("UE_Alias") == 0) & (buffer.getItem(i).value.compareToIgnoreCase("known as") != 0)) {
                    buffer.getItem(i).delete = true;
                }
            }
        }
    }

    public static String StemmingWords(QueryBuffer buffer, String query) throws Exception {
        String path = GateNamedEntity.serverpath + "stemlist.xml";
        String output = "";
        buffer.wordlist = query.split(" ");
        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        NodeList entryset = doc.getElementsByTagName("stem");
        for (int i = 0; i < buffer.wordlist.length; i++) {
            String word = buffer.wordlist[i];
            for (int j = 0; j < entryset.getLength(); j++) {
                Element entry = (Element) entryset.item(j);
                String wordin = entry.getAttribute("wordin");
                String wordout = entry.getAttribute("wordout");
                if (wordin.compareToIgnoreCase(word) == 0) {
                    word = wordout;
                }
            }
            output += word + " ";
        }
        return output;

    }
/*
    public static String StemmingQuery(QueryBuffer buffer, String query) throws Exception {
        String output = "";
        buffer.wordlist = query.split(" ");
        buffer.stemlist = new String[buffer.wordlist.length];
        Stemming stem = new Stemming();
        for (int i = 0; i < buffer.wordlist.length; i++) {
            String stemw = stem.stripAffixes(buffer.wordlist[i]);
            output += stemw + " ";
            buffer.stemlist[i] = stemw + " ";
        }
        return output;
    }
*/
    public static void SpecifyQuantifier(QueryBuffer buffer) throws Exception {
        boolean check = false;
        for (int i = 0; i < buffer.length; i++) {
            ItemType tmp = buffer.getItem(i);
            if ((!tmp.delete) & (buffer.getItem(i).quantifier == null)) {
                if (tmp.className.length() == 0) {
                    tmp.className = Constants.UE_ENTITY;
                }
                if (tmp.className.length() > 2) {
                    if (tmp.className.substring(0, 2).compareTo(Constants.UE) == 0) {
                        if (!check) {
                            buffer.getItem(i).quantifier = Constants.QUESTION_SYMBOL;
                            if (buffer.query.toLowerCase().contains(
                                    Constants.HOW_MANY_STRING)) {
                                buffer.getItem(i).quantifier = Constants.QUESTION_SYMBOL;
                            }
                            if (((buffer.length - i) > 2)
                                 && (buffer.getItem(i + 1).value
                                                    .equalsIgnoreCase("and"))
                                 && (buffer.getItem(i + 2).className.contains("UE_"))) {
                                check = false;
                            } else {
                                check = true;
                            }
                        } else {
                            buffer.getItem(i).quantifier = "*";
                        }
                    } else {
                        buffer.getItem(i).quantifier = tmp.value;
                    }
                }
            }

        }
    }

    public static void ReSpecifyQuantifier(QueryBuffer buffer) throws Exception {
        boolean check = false;
        for (int i = 0; i < buffer.length; i++) {
            ItemType tmp = buffer.getItem(i);
            if ((tmp.totalrel == 0) && (tmp.classType.compareTo("RW") != 0)) {
                tmp.delete = true;
            }
            if (!tmp.delete) {
                if (tmp.className.length() > 2) {
                    if (tmp.value.trim().compareToIgnoreCase("population") == 0) {
                        tmp.className = "String";
                        tmp.quantifier = Constants.QUESTION_SYMBOL;
                    }

                    if (tmp.className.substring(0, 2).compareTo(Constants.UE) == 0) {
                        if (!check) {
                            buffer.getItem(i).quantifier = Constants.QUESTION_SYMBOL;
                            if (buffer.query.toLowerCase().contains(Constants.HOW_MANY_STRING)) {
                                //buffer.getItem(i).quantifier="{*}@?";
                            }
                            if (((buffer.length - i) > 2) && (buffer.getItem(i + 1).value.equalsIgnoreCase("and"))
                                    && (buffer.getItem(i + 2).className.substring(0, 2).compareTo("UE") == 0)) {
                                check = false;
                            } else if (buffer.getItem(i).totalrel > 0) {
                                check = true;
                            }
                        } else {
                            buffer.getItem(i).quantifier = "*";
                        }
                    }
                }
            }
        }
    }

    public static boolean IsValidTriples(QueryBuffer buffer) throws Exception {
        boolean result = true;
        if (buffer.length < 3) {
            result = false;
        }
        int i = 0;
        int count = 0;
        while (i < buffer.length) {
            if (buffer.getItem(i).classType.compareTo("CONJ") == 0) {
                i++;
                continue;
            }
            if (count % 2 == 0) {
                if (buffer.getItem(i).classType.compareTo("UE") != 0 && buffer.getItem(i).classType.compareTo("IE") != 0) {
                    result = false;
                }
            } else {
                if (buffer.getItem(i).classType.compareTo("RW") != 0) {
                    result = false;
                }
            }
            count++;
            i++;
        }
        return result;
    }

    public static void findRelationbyRules(QueryTriple tripleset, int option) throws Exception {
        while (tripleset.currindex < tripleset.length) {
            findTripleRelation(tripleset, option);
            if (tripleset.count > 0) {
                tripleset.currindex += tripleset.count;
            } else {
                tripleset.currindex++;
            }
            tripleset.count = 0;
        }
    }

    public static void orderRules(Document doc) {
        NodeList ruleset = doc.getElementsByTagName("rule");
        for (int i = 0; i < ruleset.getLength() - 1; i++) {
            Element tmp1 = (Element) ruleset.item(i).cloneNode(true);
            int priority1 = Integer.valueOf(tmp1.getAttribute("priority"));
            for (int j = i + 1; j < ruleset.getLength(); j++) {
                Element tmp2 = (Element) ruleset.item(j).cloneNode(true);
                int priority2 = Integer.valueOf(tmp2.getAttribute("priority"));

                if (priority1 > priority2) {
                    doc.getDocumentElement().replaceChild(tmp2, ruleset.item(i));
                    doc.getDocumentElement().replaceChild(tmp1, ruleset.item(j));
                    ruleset = doc.getElementsByTagName("rule");
                    int priority = priority1;
                    priority1 = priority2;
                    priority2 = priority;
                }
            }
        }
    }

    public static void findTripleRelation(QueryTriple tripleset, int option) throws Exception {
        String path = GateNamedEntity.serverpath + "ENTransformrules.xml";
        //path = "ENTransformrules.xml";

        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        //orderRules(doc);
        NodeList ruleset = doc.getElementsByTagName("rule");
        for (int i = 0; i < ruleset.getLength(); i++) {
            Element rule = (Element) ruleset.item(i);
            String pri = rule.getAttribute("priority");
            if (option == 1) {
                if (Integer.valueOf(pri) > -1) {
                    continue;
                }
            } else {
                if (Integer.valueOf(pri) == -1) {
                    continue;
                }
            }
            Node premiselist = findElement(rule.getChildNodes(), "premiselist");
            if (premiselist != null) {
                if (checkPremiselist(premiselist, tripleset)) {
                    Node consequentlist = findElement(rule.getChildNodes(), "consequentlist");
                    if (consequentlist != null) {
                        processConsequentlist(consequentlist, tripleset);
                       // Element temp = (Element) premiselist.get
                        System.out.println(rule.getAttribute("name"));
                    }
                    break;
                }
//                                else
//                                    if (Intersection(tripleset))
//                                       break;
            //else chen code giao

            }
        }
    }

    public static TripleType InterRE(ItemType subject, ItemType relation, ItemType object) throws Exception {
        String result = "";
        TripleType tmp = new TripleType();
        tmp.subject = subject;
        tmp.object = object;
        tmp.rel = relation;
        String subname = subject.className.replaceAll("UE_", "");
        subname = subname.replaceAll("IE_", "");
        String objname = object.className.replaceAll("UE_", "");
        objname = objname.replaceAll("IE_", "");
        String rel = relation.value.replaceAll("RW_", "");
        String firstset = ProcessingXML.findREfromDic(rel);
        result = InitCode.getRelationSetEntities(subname, objname, firstset);
        if (!result.equalsIgnoreCase("")) {
            result = RemoveParent(result);
            String[] list = result.split(",");
            if (list.length == 1) {
                tmp.direction = 1;
                tmp.relationName = list[0];
                return tmp;
            }
        } else {
            result = InitCode.getRelationSetEntities(objname, subname, firstset);
            if (!result.equalsIgnoreCase("")) {
                result = RemoveParent(result);
                String[] list = result.split(",");
                if (list.length == 1) {
                    tmp.direction = 2;
                    tmp.relationName = list[0];
                    return tmp;
                }
            }
        }
        return tmp;
    }

    public static TripleType InterRE2(ItemType subject, ItemType relation, ItemType object) throws Exception {
        String result = "";
        TripleType tmp = new TripleType();
        tmp.subject = subject;
        tmp.object = object;
        tmp.rel = relation;
        String subname = subject.className.replaceAll("UE_", "");
        subname = subname.replaceAll("IE_", "");
        String objname = object.className.replaceAll("UE_", "");
        objname = objname.replaceAll("IE_", "");
        String firstset = InitCode.getRelationSetEntities2(subname, objname);
        String[] first = firstset.split(",");
        String rel = relation.value.replaceAll("RW_", "");
        String secondset = ProcessingXML.findREfromDic(rel);
        String[] second = secondset.split(",");
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < second.length; j++) {
                if (first[i].equalsIgnoreCase(second[j])) {
                    result += first[i] + ",";
                }
            }
        }
        if (!result.equalsIgnoreCase("")) {
            result = result.substring(0, result.length() - 1);
            result = RemoveParent(result);
            String[] list = result.split(",");
            if (list.length == 1) {
                tmp.direction = 1;
                tmp.relationName = list[0];
                return tmp;
            }
        }
        firstset = InitCode.getRelationSetEntities2(objname, subname);
        first = firstset.split(",");
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < second.length; j++) {
                if (first[i].equalsIgnoreCase(second[j])) {
                    result += first[i] + ",";
                }
            }
        }
        if (!result.equalsIgnoreCase("")) {
            result = result.substring(0, result.length() - 1);
            result = RemoveParent(result);
            String[] list = result.split(",");
            if (list.length == 1) {
                tmp.direction = 2;
                tmp.relationName = list[0];
                return tmp;
            }
        }
        return tmp;
    }

    public static String RemoveParent(String input) throws Exception {
        String[] list = input.split(",");
        for (int i = 0; i < list.length; i++) {
            String parent = getParent(list[i]);
            String[] parentlist = parent.split(", ");
            for (int j = 0; j < parentlist.length; j++) {
                input = input.replaceAll(parentlist[j], "");
            }
        }
        while (input.contains(",,")) {
            input = input.replaceAll(",,", ",");
        }
        if (input.endsWith(",")) {
            input = input.substring(0, input.length() - 1);
        }
        if (input.startsWith(",")) {
            input = input.substring(1, input.length());
        }
        return input;
    }

    public static String getParent(String relation) throws Exception {
        String pclass = "";
        String path = GateNamedEntity.serverpath + "ENRelationParent.xml";
        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        NodeList relset = doc.getElementsByTagName("relationship");
        for (int i = 0; i < relset.getLength(); i++) {
            Element relationship = (Element) relset.item(i);
            String val = relationship.getAttribute("val");
            if (val.equalsIgnoreCase(relation)) {
                pclass = relationship.getAttribute("parentclass");
            }
        }
        return pclass;
    }

    public static void ProcessInter(String result, TripleType querytriple) throws Exception {
        String[] list = result.split(",");
        int direction = 1;
        querytriple.rel.InsertRelation(list[0], direction, querytriple);
    }

    public static void processConsequentlist(Node consequentlist, QueryTriple tripleset) throws Exception {
        NodeList consequentcol = consequentlist.getChildNodes();
        for (int i = 0; i < consequentcol.getLength(); i++) {
            Node consequent = consequentcol.item(i);
            String nodename = consequent.getNodeName();
            if (nodename.compareToIgnoreCase("consequent") == 0) {
                processConsequent(consequent, tripleset.getItem(tripleset.currindex + tripleset.count));
                tripleset.count++;
            }
        }
    }

    public static boolean checkPremiselist(Node premiselist, QueryTriple tripleset) throws Exception {
        int count = 0;
        NodeList premisecol = premiselist.getChildNodes();
        for (int i = 0; i < premisecol.getLength(); i++) {
            Node premise = premisecol.item(i);
            if (premise.getNodeName().compareToIgnoreCase("premise") == 0) {
                TripleType querytriple = tripleset.getItem(tripleset.currindex + count);
                if (checkPremise(premise, querytriple) == false) {
                    return false;
                }
                count++;
            }
        }

        return true;
    }

    public static boolean checkPremise(Node premise, TripleType querytriple) throws Exception {
        Element subject = findElement(premise.getChildNodes(), "subject");
        Element relation = findElement(premise.getChildNodes(), "relation");
        Element object = findElement(premise.getChildNodes(), "object");

        //subject class the same as object class
        String subjclass = subject.getAttribute("className");
        if (subjclass.compareToIgnoreCase("objclass") == 0) {
            if (!ProcessingXML.IsChildClass(querytriple.subject.className, querytriple.object.className)) {
                return false;
            }
            subject.setAttribute("className", querytriple.object.className);
        /*String objclass = querytriple.object.className;
        if (objclass.length() >3 && objclass.substring(0, 2).compareTo("UE")==0)
        objclass = subjclass.substring(3);
        subject.setAttribute("className", objclass);*/
        }
        //object class the same as subject class
        String objclass = object.getAttribute("className");
        if (objclass.compareToIgnoreCase("subjclass") == 0) {
            if (!ProcessingXML.IsChildClass(querytriple.object.className, querytriple.subject.className)) {
                return false;
            }
            object.setAttribute("className", querytriple.subject.className);
        /*subjclass = querytriple.subject.className;
        if (subjclass.length() >3 && subjclass.substring(0, 2).compareTo("UE")==0)
        subjclass = subjclass.substring(3);

        object.setAttribute("className", subjclass);*/
        }

        if (checkItem(subject, querytriple.subject) && checkItem(relation, querytriple.rel) && checkItem(object, querytriple.object)) {
            querytriple.subject.var = subject.getAttribute("var");
            querytriple.rel.var = relation.getAttribute("var");
            querytriple.object.var = object.getAttribute("var");
            return true;
        }

        if (checkItem(object, querytriple.subject) && checkItem(relation, querytriple.rel) && checkItem(subject, querytriple.object)) {
            querytriple.subject.var = object.getAttribute("var");
            querytriple.rel.var = relation.getAttribute("var");
            querytriple.object.var = subject.getAttribute("var");
            return true;
        }

        return false;
    }

    public static boolean checkValue(String listvalue, String value) {
        if (listvalue.compareToIgnoreCase("") == 0) {
            return true;
        }

        String[] valuelist = listvalue.split(",");
        for (int i = 0; i < valuelist.length; i++) {
            if (valuelist[i].trim().compareToIgnoreCase(value) == 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkItem(Element orgitem, ItemType item) throws Exception {
        String value = orgitem.getAttribute("value");
        String className = orgitem.getAttribute("className");
        String classtype = orgitem.getAttribute("classType");
        String wordfollow = orgitem.getAttribute("wordfollow");
        String wordbefore = orgitem.getAttribute("wordbefore");
        String itemClass = item.className;
        if ((item.value.trim().compareToIgnoreCase(",") == 0) && (value.contains("+c"))) return true;
        if (itemClass.length() > 3 && itemClass.substring(0, 2).compareTo("UE") == 0) {
            itemClass = itemClass.substring(3);
        }
        if (checkValue(value, item.value) && checkValue(wordfollow, item.wordfollow) && checkValue(wordbefore, item.wordbefore) && checkValue(classtype, item.classType) && (checkValue(className, itemClass) || IsChildClass(itemClass, className))) {
            return true;
        }

        return false;
    }

    public static boolean IsChildClass(String childClass, String parentClass) throws Exception {
        //user predefined function
        return CheckRelationConstraint.checkChildClass(childClass, parentClass);
    }

    public static Element findElement(NodeList nodelist, String nodename) {
        Element output = null;
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node tmp = nodelist.item(i);
            if (tmp.getNodeName().compareToIgnoreCase(nodename) == 0) {
                output = (Element) tmp;
                break;
            }
        }
        return output;
    }

    public static void processConsequent(Node consequent, TripleType querytriple) throws Exception {
        Element subject = findElement(consequent.getChildNodes(), "subject");
        Element relation = findElement(consequent.getChildNodes(), "relation");
        Element object = findElement(consequent.getChildNodes(), "object");
        int direction = 1;
        if (querytriple.subject.var.compareToIgnoreCase(object.getAttribute("var")) == 0 &&
                querytriple.object.var.compareToIgnoreCase(subject.getAttribute("var")) == 0) {
            subject = findElement(consequent.getChildNodes(), "object");
            object = findElement(consequent.getChildNodes(), "subject");
            direction = 2;
        }
        String subjclassName = subject.getAttribute("className");
        if (subjclassName.compareTo("") != 0) {
            querytriple.subject.className = subjclassName; // override new class
        }

        String subjquantifier = subject.getAttribute("quantifier");
        if (subjquantifier.compareTo("") != 0) {
            if (subjquantifier.compareToIgnoreCase("objvalue") == 0) {
                querytriple.subject.quantifier = querytriple.object.value;
                querytriple.subject.classType = "IE";
            } else {
                querytriple.subject.quantifier = subjquantifier; // override new class
            }

        }

        String subjdel = subject.getAttribute("delete");
        if (subjdel.compareTo("yes") == 0) {
            querytriple.subject.delete = true; // override new class
        }

        String objclassName = object.getAttribute("className");
        if (objclassName.compareTo("") != 0) {
            querytriple.object.className = objclassName; // override new class
        }

        String objquantifier = object.getAttribute("quantifier");
        if (objquantifier.compareTo("") != 0) {
            if (objquantifier.compareToIgnoreCase("subjvalue") == 0) {
                querytriple.object.quantifier = querytriple.subject.value;
                querytriple.object.classType = "IE";
            } else {
                querytriple.object.quantifier = objquantifier; // override new class
            }
        }

        String objdel = object.getAttribute("delete");
        if (objdel.compareTo("yes") == 0) {
            querytriple.object.delete = true;
        }

        String reldel = relation.getAttribute("delete");
        if (reldel.compareTo("yes") == 0) {
            querytriple.rel.delete = true;
        }

        String relvalue = relation.getAttribute("value");
        querytriple.rel.InsertRelation(relvalue, direction, querytriple);
    }

    public static void findRelationfromDic(QueryTriple tripleset) throws Exception {
        for (int i = 0; i < tripleset.length; i++) {
            TripleType querytriple = tripleset.getItem(i);
            ItemType relation = querytriple.rel;

            if (relation.className.compareTo("RW") == 0) {
                findTripleRelationfromDic(querytriple);
            }
        }
    }

    public static void findTripleRelationfromDic(TripleType querytriple) throws Exception {
        boolean result;
        String path = GateNamedEntity.serverpath + "relationdic.xml";

        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document doc = parser.getDocument();
        NodeList entryset = doc.getElementsByTagName("entry");
        for (int i = 0; i < entryset.getLength(); i++) {
            result = true;
            Element entry = (Element) entryset.item(i);
            String subjvalue = entry.getAttribute("subjvalue");
            String relvalue = entry.getAttribute("relvalue");
            String objvalue = entry.getAttribute("objvalue");

            /*if (subjvalue.compareTo("")!=0 && subjvalue.compareToIgnoreCase(querytriple.subject.value)!=0)
            result = false;
            if (relvalue.compareTo("")!=0 && relvalue.compareToIgnoreCase(querytriple.rel.value)!=0)
            result = false;
            if (objvalue.compareTo("")!=0 && objvalue.compareToIgnoreCase(querytriple.object.value)!=0)
            result = false;*/

            if (!checkValue(subjvalue, querytriple.subject.value)) {
                result = false;
            }
            if (!checkValue(relvalue, querytriple.rel.value)) {
                result = false;
            }
            if (!checkValue(objvalue, querytriple.object.value)) {
                result = false;
            }

            String subjclass = entry.getAttribute("subjclass");
            String objclass = entry.getAttribute("objclass");

            /*if (subjclass.compareTo("")!=0)
            {
            if ( subjclass.compareToIgnoreCase(querytriple.subject.className)!=0 && subjclass1.compareToIgnoreCase(querytriple.subject.className)!=0)
            result = false;

            }

            if (relclass.compareTo("")!=0 )
            {
            if (relclass.compareToIgnoreCase(querytriple.rel.className)!=0 && relclass1.compareToIgnoreCase(querytriple.rel.className)!=0 )
            result = false;
            }
            if (objclass.compareTo("")!=0)
            {
            if (objclass.compareToIgnoreCase(querytriple.object.className)!=0 && objclass1.compareToIgnoreCase(querytriple.object.className)!=0)
            result = false;
            }*/
            if (result) {
                result = CheckRelationConstraint.checkChildClass2(querytriple.subject.className, subjclass) &
                        CheckRelationConstraint.checkChildClass2(querytriple.object.className, objclass);
            }


            if (result) {
                String rel = entry.getAttribute("relation");
                int dir = Integer.parseInt(entry.getAttribute("dir"));

                querytriple.rel.InsertRelation(rel, dir, querytriple);
                return;
            }
        }
    }

    public static void findRelationfromTxtDic(QueryTriple tripleset) throws Exception {
        for (int i = 0; i < tripleset.length; i++) {
            TripleType querytriple = null;
            querytriple = tripleset.getItem(i);
            ItemType relation = querytriple.rel;

            if (relation.className.compareTo("RW") == 0) {
                findRelationfromERDicTxt(querytriple, "ERWDict.txt");
                findRelationfromREDicTxt(querytriple, "RWEDict.txt");
            }
        }
    }

    public static void findRelationfromERDicTxt(TripleType querytriple, String dicname) throws Exception {
        //example (capital, of) <- hasCapital

        String path = GateNamedEntity.serverpath + dicname;
        BufferedReader in = new BufferedReader(new FileReader(path));

        String dictentry;
        while ((dictentry = in.readLine()) != null) {
            String subject = dictentry.substring(dictentry.indexOf('(') + 1, dictentry.indexOf(','));
            String relwords = dictentry.substring(dictentry.indexOf(',') + 1, dictentry.indexOf(")"));
            subject = subject.trim();
            relwords = relwords.trim();
            if (querytriple.subject.value.compareToIgnoreCase(subject) == 0 && querytriple.rel.value.compareToIgnoreCase(relwords) == 0) {
                String relation;
                relation = dictentry.substring(dictentry.indexOf(")") + 1);
                relation = relation.trim();

                if (relation.compareTo("") != 0) {
                    String rel;
                    if (relation.indexOf("<-") != -1) {
                        rel = relation.substring(relation.indexOf("<-") + 2);
                        rel = rel.trim();
                        querytriple.rel.InsertRelation(rel, 2, querytriple);
                    } else {
                        rel = relation.substring(relation.indexOf("->") + 2);
                        rel = rel.trim();
                        querytriple.rel.InsertRelation(rel, 1, querytriple);
                    }
                }
            }
        }
        in.close();

    }

    public static void findRelationfromREDicTxt(TripleType querytriple, String dicname) throws Exception {
        //example ('s, father) -> hasFather

        String path = GateNamedEntity.serverpath + dicname;
        BufferedReader in = new BufferedReader(new FileReader(path));

        String dictentry;
        while ((dictentry = in.readLine()) != null) {
            String relwords = dictentry.substring(dictentry.indexOf('(') + 1, dictentry.indexOf(','));
            String object = dictentry.substring(dictentry.indexOf(',') + 1, dictentry.indexOf(")"));
            object = object.trim();
            relwords = relwords.trim();
            if (querytriple.rel.value.compareToIgnoreCase(relwords) == 0 && querytriple.object.value.compareToIgnoreCase(object) == 0) {
                String relation = "";
                relation = dictentry.substring(dictentry.indexOf(")") + 1);
                relation = relation.trim();

                if (relation.compareTo("") != 0) {
                    String rel;
                    if (relation.indexOf("<-") != -1) {
                        rel = relation.substring(relation.indexOf("<-") + 2);
                        rel = rel.trim();
                        querytriple.rel.InsertRelation(rel, 2, querytriple);
                    } else {
                        rel = relation.substring(relation.indexOf("->") + 2);
                        rel = rel.trim();
                        querytriple.rel.InsertRelation(rel, 1, querytriple);
                    }

                }

            }
        }

        in.close();
    }

    public static boolean IsValidCG(QueryBuffer buffer) throws Exception {
        boolean valid = true;
        for (int i = 0; i < buffer.length; i++) {
            ItemType tmp = buffer.getItem(i);
            if (tmp.className.compareTo("RW") == 0) {
                if (tmp.relcount == 0 && tmp.delete == false) {
                    valid = false;
                    break;
                }
            }
        }

        IsValid = valid;

        return valid;
    }

  	/**
	 *
	 * @param consequentlist
	 * @param ret
	 * @throws Exception
	 */
	public static void processConsequentlistForADJ(Node consequentlist,
			TripleType ret) throws Exception {
		NodeList consequentcol = consequentlist.getChildNodes();
		for (int i = 0; i < consequentcol.getLength(); i++) {
			Node consequent = consequentcol.item(i);
			String nodename = consequent.getNodeName();
			if (nodename
					.compareToIgnoreCase(Constants.TRANSFORM_CONSEQUENT_TAG) == 0) {
				processConsequentForADJ(consequent, ret);
			}
		}
	}

   /**
	 *
	 * @param premiselist
	 * @param adj
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static boolean checkPremiselistForADJ(Node premiselist,
			ItemType adj, ItemType obj) throws Exception {
		NodeList premisecol = premiselist.getChildNodes();
		for (int i = 0; i < premisecol.getLength(); i++) {
			Node premise = premisecol.item(i);
			if (premise.getNodeName().compareToIgnoreCase(
					Constants.TRANSFORM_PREMISE_TAG) == 0) {
				if (checkPremiseForAdj(premise, adj, obj) == false)
					return false;
			}
		}

		return true;
	}

    	/**
	 *
	 * @param premise
	 * @param adj
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static boolean checkPremiseForAdj(Node premise, ItemType adj,
			ItemType obj) throws Exception {
		Element adjFromRules = findElement(premise.getChildNodes(),
				Constants.TRANSFORM_ADJECTIVE_ATTR);
		Element objFromRules = findElement(premise.getChildNodes(),
				Constants.TRANSFORM_SUBJECT_TAG);

		if (adjFromRules != null && checkItem(adjFromRules, adj)
				&& checkItem(objFromRules, obj)) {
			return true;
		}

		return false;
	}

    /**
	 *
	 * @param consequent
	 * @param ret
	 * @throws Exception
	 */
	public static void processConsequentForADJ(Node consequent, TripleType ret)
			throws Exception {
		Element subject = findElement(consequent.getChildNodes(),
				Constants.TRANSFORM_SUBJECT_TAG);
		Element relation = findElement(consequent.getChildNodes(),
				Constants.TRANSFORM_RELATION_TAG);
		Element object = findElement(consequent.getChildNodes(),
				Constants.TRANSFORM_OBJECT_TAG);
		int direction = 1;

		//XU LY CLASSNAME CUA SUBJECT, OBJECT CUA BO 3
		String subjclassName = subject
				.getAttribute(Constants.TRANSFORM_CLASSNAME_ATTR);
		if (subjclassName.compareTo("") != 0) {
			ret.subject.className = subjclassName; // override new
			// class
		}

		String objclassName = object
				.getAttribute(Constants.TRANSFORM_CLASSNAME_ATTR);
		if (objclassName.compareTo("") != 0) {
			ret.object.className = objclassName; // override new
			// class
		}

		//XU LY QUANTIFIER CUA SUBJ, OBJ
		String subjquantifier = subject
				.getAttribute(Constants.TRANSFORM_QUANTIFIER_ATTR);
		if (subjquantifier.compareTo("") != 0) {
			if (subjquantifier
					.compareToIgnoreCase(Constants.TRANSFORM_OBJVALUE_ATTR) == 0) {
				ret.subject.quantifier = ret.object.value;
				ret.subject.classType = Constants.IE;
			} else
				ret.subject.quantifier = subjquantifier; // override
			// new class

		}
		String objquantifier = object
				.getAttribute(Constants.TRANSFORM_QUANTIFIER_ATTR);
		if (objquantifier.compareTo("") != 0) {
			if (objquantifier
					.compareToIgnoreCase(Constants.TRANSFORM_SUBJVALUE_ATTR) == 0) {
				ret.object.quantifier = ret.subject.value;
				ret.object.classType = Constants.IE;
			} else
				ret.object.quantifier = objquantifier; // override new
			// class
		}

		//XAC DINH TEN QUAN HE
		String relvalue = relation.getAttribute(Constants.TRANSFORM_VALUE_ATTR);
		ret.relationName = relvalue;
		ret.rel.InsertRelation(relvalue, direction, ret);
	}

/**
	 * 
	 * @param tripleset
	 * @param buffer
	 * @throws Exception
	 */
	public static void processQuantitativeAdjective(QueryTriple tripleset,
			QueryBuffer buffer) throws Exception {

        /* Process for QTA, SA */

		if (buffer.length < 2)
			return;

		for (int i = 0; i < buffer.length; i++) {

			if ((buffer.getItem(i).classType
							.compareTo(Constants.QUANTITATIVE_ADJ) == 0)
                 || (buffer.getItem(i).classType
							.compareTo(Constants.SUPERLATIVE_QUANTITATIVE_ADJ) == 0)) {

                boolean check = false;
                int afterUE = -1;
                int beforeUE = -1;

				// Heuristic 1: adjs always stand before entity. So, find the
				// UE that is closest with adj in the next.
				for (int j = i + 1; j < buffer.length; j++) {

					if ((buffer.getItem(j).classType.compareTo(Constants.UE) == 0)
						&& (!buffer.getItem(j).delete)) {

                        afterUE = j;
                        if ( addRelationFromQuantitatidveAdj(tripleset, buffer,
                                                                       i, j) ) {
                            check = true;
                            break;
                        }

					}//end of if UE found

				}//end of heuristic 1

				if (!check) {
					// Heuristic 2: adjs stand after entity. So, find the
					// entity that is closest with adj in the previous.
					for (int j = i - 1; j > 0; j--) {

                        if ((buffer.getItem(j).classType.compareTo(Constants.UE) == 0)
                            && (!buffer.getItem(j).delete)) {

                            beforeUE = j;
                            if ( addRelationFromQuantitatidveAdj(tripleset,
                                                                buffer, i, j) ) {
                                check = true;
                                break;
                            }

                        }//end of if UE found

                    }//end of heuristic 2

                }//end of if(!check)

                if (!check) {
                    //if !check again, try to figure out a fake relation

                    if (afterUE != -1) {
                        //First check if have UE after the adjective
                        addFakeRelForADJ(tripleset, buffer.getItem(i),
                                        buffer.getItem(afterUE));

                    } else if (beforeUE != -1) {
                        //If not, check if there is any UE before the adj
                        addFakeRelForADJ(tripleset, buffer.getItem(i),
                                        buffer.getItem(beforeUE));
                    }

                }//end of create fake relation

            //end of if QTA or SQTA

			} else if (buffer.getItem(i).classType
                    .compareTo(Constants.COMPARATIVE_QUANTITATIVE_ADJ) == 0) {

                //3 relation to find
                TripleType relationA = null;
                TripleType relationB = null;
                TripleType comparativeRel = null;

                //Keep 2 objects to create the relation:
                //obj1 -> is greater/smaller than -> obj2
                ItemType objectA = null;
                ItemType objectB = null;

                //Find the comparated UE, and generate relation between it and CQTA
                boolean found = false;

                for (int j = i - 1; j > 0; j--) {

                    if ((buffer.getItem(j).classType.compareTo(Constants.UE) == 0)
                         && (!buffer.getItem(j).delete)) {

                         // Get triple from relation between ADJ and UE
                        relationA = getTripleFromRelationshipOfAdjAndEntity(
                                        buffer.getItem(i), buffer.getItem(j));

                        // if can find triple
                        // Check validation for this triple.
                        if ((relationA.relationName.length() != 0)
                            & (CheckValidRE(relationA, buffer))) {

                            found = true;
                            objectA = relationA.object;
                            break;
                        }

                        if (relationA.relationName.length() == 0) {
                            // if cannot find relation from rule, create fake relation
                            relationA = createFakeTripleFromAdjAndEntity(
                                        buffer.getItem(i), buffer.getItem(j));
                            System.out.println(relationA.relationName);
                            found = true;
                            objectA = relationA.object;
                            break;
                        }
                    }
                }

                //ignore CQTA if cannot find UE before it
                if (!found) {
                    continue;
                }

                //Find the comparating real number/ IE
                found = false;

                for (int j = i + 1; j < buffer.length; j++) {

					if ((buffer.getItem(j).classType.compareTo(Constants.IE) == 0)
						&& (!buffer.getItem(j).delete)) {

                        // Get triple from relation between ADJ and UE
                        relationB = getTripleFromRelationshipOfAdjAndEntity(
                                        buffer.getItem(i), buffer.getItem(j));

                        // if can find triple
                        // Check validation for this triple.
                        if ((relationB.relationName.length() != 0)
                            & (CheckValidRE(relationB, buffer))) {

                            found = true;
                            objectB = relationB.object;
                            break;
                        }

                        if (relationB.relationName.length() == 0) {
                            // if cannot find relation from rule, create fake relation
                            relationB = createFakeTripleFromAdjAndEntity(
                                        buffer.getItem(i), buffer.getItem(j));
                            System.out.println("Here: " + relationB.relationName);
                            found = true;
                            objectB = relationB.object;
                            break;
                        }

					} else if ((buffer.getItem(j).classType
                                         .compareTo(Constants.REAL_NUMBER) == 0)
                                && (!buffer.getItem(j).delete)) {

                        found = true;
                        objectB = new ItemType();
                        objectB.className = "(String)";
                        objectB.classType = Constants.REAL_NUMBER;
                        objectB.quantifier = buffer.getItem(j).value;

                        break;
                    }

				}

                //ignore CQTA if cannot find UE before it
                if (!found) {
                    continue;
                }

                //Generate comparative relation
                comparativeRel = new TripleType();

                comparativeRel.subject = objectA;
                comparativeRel.object = objectB;

                comparativeRel.rel = new ItemType();
                comparativeRel.rel.className = Constants.RW;
                comparativeRel.rel.classType = Constants.RW;

                //Identify name of the comparative relation ( Greater / Smaller )
                String comADJ = buffer.getItem(i).value;

                String rel = specifyComparativeRel(comADJ);

                if (rel == null) {
                    continue;
                }

                comparativeRel.relationName = "(" + rel + ")";

                comparativeRel.rel.InsertRelation(comparativeRel.relationName
                                                          , 1, comparativeRel);

                //Add to tripleset
                tripleset.tripleset[tripleset.length] = relationA;
                tripleset.length++;
                if (relationB != null) {
                    tripleset.tripleset[tripleset.length] = relationB;
                    tripleset.length++;
                }
                tripleset.tripleset[tripleset.length] = comparativeRel;
                tripleset.length++;

            } //end of CQTA

		}// end of "for" to find adj

	}

     private static void addFakeRelForADJ (QueryTriple tripleSet,
                            ItemType adj, ItemType subject) throws Exception {

        //Create Relation
		TripleType result = createFakeTripleFromAdjAndEntity(adj, subject);

        //Add to triple
        if(adj.classType.equals(Constants.SUPERLATIVE_QUANTITATIVE_ADJ)) {
            //if SQTA
            result.object.setMarkMaxMinRel(true);
            result.rel.setIdentifiedFromSQTA(true); //de sinh "Where" cho SeRQL
            tripleSet.tripleset[tripleSet.length] = result;

            // Correct for President case
            if (result.subject.className.equalsIgnoreCase
                                    (Constants.UE_PRESIDENT)) {

                for (int k = 0; k < tripleSet.length; k++) {
                    TripleType tpt = tripleSet.tripleset[k];

                    if (tpt.relationName.equals(HAS_POSITION)) {

                        if (tpt.subject.classType.equals(Constants.UE)
                                && CheckRelationConstraint.checkChildClass(
                                            tpt.subject.className,PERSON)) {

                            result.subject = tpt.subject;
                            break;
                        }

                        if (tpt.object.classType.equals(Constants.UE)
                                && CheckRelationConstraint.checkChildClass(
                                                tpt.object.className,PERSON)) {

                            result.subject = tpt.object;
                            break;
                        }
                    }
                }
            }
        } else {
            //if normal QTA
            result.rel.setIdentifiedFromQTA(true);
            result.object.value = adj.value; //Dung trong luc sinh Order Clause va ve CG
            result.object.setValueOfNormalQTA(true); //dung trong generateJsCG()
                                                        //de doi label trong CG
            tripleSet.tripleset[tripleSet.length] = result;

        } //end of normal QTA

        tripleSet.length++;
    }

    public static TripleType createFakeTripleFromAdjAndEntity(
                            ItemType adj, ItemType subject) throws Exception {
        //Create Relation
		TripleType result = new TripleType();
		result.subject = subject;
		result.rel = new ItemType();
		result.rel.className = Constants.RW;
		result.rel.classType = Constants.RW;


		result.object = new ItemType();

        result.object.className = "(String)";
        result.object.quantifier = "*";

        if (adj.classType.equalsIgnoreCase(Constants.QUANTITATIVE_ADJ)) {

            result.relationName = "(" + "is"
                                    + adj.value.substring(0, 1).toUpperCase()
                                    + adj.value.substring(1) + ")";
        } else if (adj.classType.equalsIgnoreCase
                                    (Constants.SUPERLATIVE_QUANTITATIVE_ADJ)) {
            String tmp;
            if (adj.wordbefore.equalsIgnoreCase(Constants.MOST_STRING)
                   || adj.wordbefore.equalsIgnoreCase(Constants.LEAST_STRING)) {

                tmp = adj.value.replace(Constants.MOST_STRING + " ", "");
                tmp = tmp.replace(Constants.LEAST_STRING + " ", "");

            } else {
                tmp = adj.value.replace("est", "");
                if (tmp.charAt(tmp.length() - 1)
                        == tmp.charAt(tmp.length() - 2)) {
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
            }

            result.relationName = "(" + "is"
                                            + tmp.substring(0, 1).toUpperCase()
                                            +tmp.substring(1) + ")";
        } else if (adj.classType.equalsIgnoreCase
                                    (Constants.COMPARATIVE_QUANTITATIVE_ADJ)) {
            String tmp;
            if (adj.wordbefore.equalsIgnoreCase(Constants.MORE_STRING)
                   || adj.wordbefore.equalsIgnoreCase(Constants.LESS_STRING)) {

                tmp = adj.value.replace(Constants.MORE_STRING + " ", "");
                tmp = tmp.replace(Constants.LESS_STRING + " ", "");

            } else {
                tmp = adj.value.replace("er", "");
                if (tmp.charAt(tmp.length() - 1)
                        == tmp.charAt(tmp.length() - 2)) {
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
            }

            result.relationName = "(" + "is"
                                            + tmp.substring(0, 1).toUpperCase()
                                            +tmp.substring(1) + ")";
        }

        result.rel.InsertRelation(result.relationName, 1, result);

        return result;
    }

    /**
     * @param tripleset
     * @param buffer
     * @param adjIndex
	 * @param subjectIndex
	 * @return boolean
	 * @throws Exception
     */
    public static boolean addRelationFromQuantitatidveAdj(QueryTriple tripleset,
        QueryBuffer buffer, int adjIndex, int subjectIndex) throws Exception {

        // Get triple from relation between ADJ and UE
        TripleType tmp = getTripleFromRelationshipOfAdjAndEntity(
                buffer.getItem(adjIndex), buffer.getItem(subjectIndex));

        // if can find triple
        // Check validation for this triple.
        if ((tmp.relationName.length() != 0) & (CheckValidRE(tmp, buffer))) {

            if(buffer.getItem(adjIndex).classType
                    .equals(Constants.SUPERLATIVE_QUANTITATIVE_ADJ)) {
                //if SQTA
                tmp.object.setMarkMaxMinRel(true);
                tmp.rel.setIdentifiedFromSQTA(true); //de sinh "Where" cho SeRQL
                tripleset.tripleset[tripleset.length] = tmp;

                // Correct for President case
                if (tmp.subject.className
                        .equalsIgnoreCase(Constants.UE_PRESIDENT)) {

                    for (int k = 0; k < tripleset.length; k++) {
                        TripleType tpt = tripleset.tripleset[k];

                        if (tpt.relationName.equals(HAS_POSITION)) {

                            if (tpt.subject.classType.equals(Constants.UE)
                                && CheckRelationConstraint.checkChildClass(
                                            tpt.subject.className,PERSON)) {

                                tmp.subject = tpt.subject;
                                break;
                            }

                            if (tpt.object.classType.equals(Constants.UE)
                                && CheckRelationConstraint.checkChildClass(
                                                tpt.object.className,PERSON)) {

                                tmp.subject = tpt.object;
                                break;
                            }
                        }
                    }
                }
            //End of SQTA
            } else {//if normal QTA

                tmp.rel.setIdentifiedFromQTA(true);
                tmp.object.value = buffer.getItem(adjIndex).value; //Dung trong luc sinh Order Clause va ve CG
                tmp.object.setValueOfNormalQTA(true); //dung trong generateJsCG()
                                                        //de doi label trong CG
                tripleset.tripleset[tripleset.length] = tmp;

            } //end of normal QTA

            tripleset.length++;
            return true;

        }//end of CheckValidRE

        //if cannot find triple
        return false;
    }
    /**
	 *
	 * @param adj
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public static TripleType getTripleFromRelationshipOfAdjAndEntity(
			ItemType adj, ItemType subject) throws Exception {

		TripleType tmp = new TripleType();
		if (subject.delete)
			return tmp;
		//Tim bang luat
		tmp = findRelationbyRulesForADJ(adj, subject, 0);

		return tmp;
	}

    /**
	 *
	 * @param adj
	 * @param subject
	 * @param option
	 * @return
	 * @throws Exception
	 */
	public static TripleType findRelationbyRulesForADJ(ItemType adj,
                                ItemType subject, int option) throws Exception {
		TripleType result = new TripleType();
		result.subject = subject;
		result.rel = new ItemType();
		result.rel.className = Constants.RW;
		result.rel.classType = Constants.RW;
//        result.rel.setIdentifiedFromQTA(true);

		result.object = new ItemType();

//        boolean hasRule = false;

		String path = GateNamedEntity.serverpath + "transformrulesforADJ.xml";

		DOMParser parser = new DOMParser();
		parser.parse(path);
		Document doc = parser.getDocument();
		// orderRules(doc);
		NodeList ruleset = doc
				.getElementsByTagName(Constants.TRANSFORM_RULE_TAG);
		for (int i = 0; i < ruleset.getLength(); i++) {
			Element rule = (Element) ruleset.item(i);
			String pri = rule.getAttribute(Constants.TRANSFORM_PRIORITY_TAG);
			if (option == 1) {
				if (Integer.valueOf(pri) > -1)
					continue;
			} else {
				if (Integer.valueOf(pri) == -1)
					continue;
			}

			Node premiselist = findElement(rule.getChildNodes(),
					Constants.TRANSFORM_PREMISELIST_TAG);
			if (premiselist != null) {
				if (checkPremiselistForADJ(premiselist, adj, subject)) {
					Node consequentlist = findElement(rule.getChildNodes(),
							Constants.TRANSFORM_CONSEQUENTLIST_TAG);
					if (consequentlist != null)
//                        hasRule = true;
						processConsequentlistForADJ(consequentlist, result);
					break;
				}

			}
		}

//        //If there is no rule for this pair of adj and UE,
//        //create a fake relation between them
//        if (!hasRule) {
//            result.object.className = "(String)";
//            result.object.quantifier = "*";
//
//            if (adj.classType.equalsIgnoreCase(Constants.QUANTITATIVE_ADJ)) {
//
//                result.relationName = "(" + "is"
//                                + adj.value.substring(0, 1).toUpperCase()
//                                + adj.value.substring(1) + ")";
//
//            } else if (adj.classType.equalsIgnoreCase
//                                    (Constants.SUPERLATIVE_QUANTITATIVE_ADJ)) {
//
//                String tmp;
//
//                if (adj.wordbefore.equalsIgnoreCase(Constants.MOST_STRING)
//                   || adj.wordbefore.equalsIgnoreCase(Constants.LEAST_STRING)) {
//
//                    tmp = adj.value.replace(Constants.MOST_STRING + " ", "");
//                    tmp = tmp.replace(Constants.LEAST_STRING + " ", "");
//
//                } else {
//                    tmp = adj.value.replace("est", "");
//                }
//
//                result.relationName = "(" + "is"
//                                            + tmp.substring(0, 1).toUpperCase()
//                                            +tmp.substring(1) + ")";
//            } //Can bo sung them truong hop so sanh hon
//
//            result.rel.InsertRelation(result.relationName, 1, result);
//        }

		return result;

	}


    //parse file xml ?? bi?t ?ó là property hay subclass
    public static String getAdjectiveTypeInOntology(ItemType adj, ItemType entity) {
        if ((adj.value.compareToIgnoreCase("famous") == 0) && (adj.wordbefore.compareToIgnoreCase("most") == 0)) {
            return "Class#MostFamousWoman";
        }
        if (adj.value.compareToIgnoreCase("beautiful") == 0) {
            return "Relation#isBeautiful";
        }
        if (adj.value.compareToIgnoreCase("famous") == 0) {
            return "Class#FamousWoman";
        }

        return "";
    }

    //thêm vào tripleset ho?c thay tên class c?a entity thành subclass, tùy k?t qu? tr? v? t? hàm getAdjectiveType
    //n?u là so sánh nh?t thì ?ánh d?u quantifier là most ho?c lease ?? t?o câu SeRQL
    public static boolean addRelationFromQuanlitatidveAdj(QueryTriple tripleset, ItemType adj, ItemType entity) {
        try {
        if (((entity.classType.compareTo("IE") == 0) || (entity.classType.compareTo("UE") == 0)) && (!entity.delete)) {
            String prop = ProcessingXML.getAdjectiveTypeInOntology(adj, entity);
            String type = prop.split("#")[0];

            if (type.equalsIgnoreCase("Relation")) {
                ItemType sub = new ItemType();
                sub.className = "String";
                sub.classType = "Property";
                if ((adj.wordbefore.equalsIgnoreCase("most"))||(adj.classType
                        .equalsIgnoreCase(Constants.SUPERLATIVE_QUANLITATIVE_ADJ))) {

                    adj.classType = Constants.SUPERLATIVE_QUANLITATIVE_ADJ;
                    sub.quantifier = "most";
                } else if (adj.wordbefore.equalsIgnoreCase("least")) {
                    adj.classType = Constants.SUPERLATIVE_QUANLITATIVE_ADJ;
                    sub.quantifier = "least";
                } else {
                    sub.quantifier = "*";
                }
                sub.var = "a";
                ItemType obj = entity;

                ItemType rel = new ItemType();
                rel.value = "hasProperty";
                rel.className = Constants.RW;
                rel.classType = Constants.RW;
                rel.relation = prop.split("#")[1];
                rel.direction = 2;
                rel.relcount = 2;
                rel.var = "b";

                TripleType tt = new TripleType();
                tt.subject = sub;
                tt.object = obj;
                tt.rel = rel;
                tt.relationName = prop.split("#")[1];
                tt.direction = 2;
                tripleset.tripleset[tripleset.length] = tt;
                tripleset.length++;
            } else if (type.equalsIgnoreCase("Class")) {
                entity.className = prop.split("#")[1];
            } else {
                return false;
            }

            return true;
        }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //duy?t qua buffer, khi g?p QLA hay SQLA thì x? lý
    public static void processQuanlitativeAdjective(QueryTriple tripleset, QueryBuffer buffer) {
        if (buffer.length < 2) {
            return;
        }
        for (int i = 0; i < buffer.length; i++) {
            ItemType adj = buffer.getItem(i);
            if ((adj.classType.compareTo(Constants.QUANLITATIVE_ADJ) == 0)||(adj.classType
                            .compareTo(Constants.SUPERLATIVE_QUANLITATIVE_ADJ) == 0)) {
                boolean check = false;

                for (int j = i + 1; j < buffer.length; j++) {
                    ItemType entity = buffer.getItem(j);
                    if (addRelationFromQuanlitatidveAdj(tripleset, adj, entity)) {
                        check = true;
                        break;
                    }
                }

                if (!check) {
                    for (int j = i - 1; j > -1; j--) {
                        {
                            ItemType entity = buffer.getItem(j);
                            if (addRelationFromQuanlitatidveAdj(tripleset, adj, entity)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private static String specifyComparativeRel(String cqta) {
		String result = null;
		try {
			String rel = ProcessingXML.specifyComparativeRelFromDic(cqta);

			if (rel != null && rel.trim().equalsIgnoreCase(Constants.GREATER)) {

				result = Constants.GREATER;

			} else if (rel != null
					&& rel.trim().equalsIgnoreCase(Constants.SMALLER)) {

				result = Constants.SMALLER;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}//end of class

