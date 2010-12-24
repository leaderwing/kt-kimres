/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.List;

import java.util.ArrayList;

/**
 *
 * @author Mrt. Long
 */
public class LTLL {

    enum ElementType {

        Entity, Relation
    }

    public static String convert2VN(String query) {

        String[] addr = {"headquarters", "Headquarters", "head office",
            "Head office", "address", "Address"};
        for (int i = 0; i < addr.length; i++) {
            if (query.contains(addr[i])) {
                query = query.replaceAll("Where", "");
                query = query.replaceAll("where", "");
            }
        }
        query = query.replaceAll("'s ", " 's ");
        query = query.replaceAll("s' ", "s 's ");
        query = query.replaceAll(",", " , ");

        return query;
    }

    public static String test(QueryBuffer bufferInput) { //MODIFY

        QueryBean result = new QueryBean();
        String outtest = "";
        String SeRQL = "";

        try {
            String query = bufferInput.query;
            // output query
            result.setQuery(query);

            // pre recognize entity
            //query = query.replace("'s", " 's");

            QueryBuffer buffer = new QueryBuffer();
            /*
            int wherePos = query.toLowerCase().indexOf(" where ");
            if (wherePos > 0) {
            System.out.println("0. Move \"Where\" to head of sentence.");
            query = "Where " + query.substring(0, wherePos) + " "
            + query.substring(wherePos + " where ".length());
            }
            int whatPos = query.toLowerCase().indexOf(" what ");
            if (whatPos > 0) {
            System.out.println("0. Move \"What\" to head of sentence.");
            query = query.substring(whatPos) + " "
            + query.substring(0, whatPos);
            }
            if ((" " + query).toLowerCase().indexOf(" where ") >= 0) {
            query += " in ";
            }
             */
            buffer = bufferInput;
            buffer.query = query;

            // Identify IE, UE, ADJ, CONJ, RW
            //ENSearch.GetNamedEntity(buffer.query, buffer, "");
            //ENSearch.GetUERW(buffer.query, buffer);

            System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");

            // Annotation
            result.setAnnotation(QueryOutput.getHTMLAnnotatedQuery(buffer));
            System.out.println("2. Process annotation");

            /*
            // Re-process query for "it"
            if (buffer.getQuery().toLowerCase().indexOf(" is it ") < 0
            && buffer.getQuery().toLowerCase().indexOf(" would it ") < 0
            && buffer.getQuery().toLowerCase().indexOf(" was it ") < 0
            && buffer.getQuery().indexOf(PRONOUN_IT) > 0) {
            String modifiedQuery = "";
            int itPos = buffer.getQuery().indexOf(PRONOUN_IT);
            for (int i = 0; i < buffer.length; i++) {
            ItemType t = buffer.buffer[i];
            if (t.classType.equals("IE") && t.end < itPos) {
            modifiedQuery = buffer.getQuery().replace(PRONOUN_IT,
            " " + t.value + " ");
            break;
            }
            }
            buffer = new QueryBuffer();
            buffer.query = modifiedQuery;
            //	GateNamedEntity.GetEntityandRelationWord(ctx, buffer.query,
            //			buffer);

            ENSearch.GetNamedEntity(buffer.query, buffer, ambiguous);
            ENSearch.GetUERW(buffer.query, buffer);

            }
             */

            System.out.println("3. Process for pronoun (\"it\")");

            // Split the query to atomic query
            List<ItemType> itemList = new ArrayList<ItemType>();
            if (buffer != null && buffer.length > 0) {
                for (int i = 0; i < buffer.length; i++) {
                    ItemType t = buffer.buffer[i];
                    itemList.add(t);
                }
            }
            List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
            System.out.println("4. Split query to atomic query.");
            splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

            ListCG listCG = new ListCG();
            listCG.setQuery(query);

            boolean isvalid = false;
            boolean isCheckedTopRel = false;
            System.out.println("5. Process...");
            for (int i = 0; i < atomicQueryList.size(); i++) {
                String atomQuery = atomicQueryList.get(i).getQuery().query; //Doi "query" tu Str->Buf
                System.out.println("Process atom query: " + atomQuery);
                RelationType nextRel = atomicQueryList.get(i).getNextReal();
                QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
                atomBuffer.setRelToNextAtomQuery(nextRel);

                //outtest += atomBuffer.test() + "<br/><br/>";

                System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

                // identify nested relation (COUNT, MAX, MIN, AVERAGE, MOST)

                if (isCheckedTopRel == false) {
                    if (identifyTopRel(atomBuffer) != null) {
                        listCG.setTopRel(identifyTopRel(atomBuffer));
                        isCheckedTopRel = true;
                    }

                }
                /*
                if (atomBuffer.getQuery().contains("how many people")) {
                atomBuffer.buffer[0].delete = true;
                atomBuffer.buffer[1].classType = "QTA";
                atomBuffer.buffer[1].classType = "QTA";
                }
                 */
                // Processing for
                //System.out.println("5.4 Processing for NOT words.");
                //ProcessingXML.AddRealtionForNOTConj(atomBuffer);

                System.out.println("5.5 Identify class of agent.");
                // Identify the class of Agent (What, which)
                ProcessingXML.FindClassofAgent(atomBuffer);

                // Combine all pair of entity in the query (First: combine 2
                // entity
                // close to each other, then: combine all pair of entity in the
                // query by
                // using transform rules has priority = -1.
                System.out.println("5.6 Combine entities.");
                ProcessingXML.CombineEntitys(atomBuffer);

                // Insert new relation for the pair of items that close to each
                // other

                System.out.println("5.7 Add addition relation.");
                ProcessingXML.AddRealtion(atomBuffer);

                // Identify quantifier for entity in buffer
                System.out.println("5.8 Specify quantifier for entities.");
                ProcessingQuery.SpecifyQuantifier(atomBuffer);
                
                // Mapping <entity, relation word, entity) to <entity, relation
                // type, entity>
                System.out.println("5.9 Mapping <entity, relation word, entity) to <entity, relation type, entity>.");
                QueryTriple tripleAtomSet = ProcessingQuery.ProcessQuery2Triple(atomBuffer);

                System.out.println("tripleAtomSet is: " + tripleAtomSet.length);

                outtest += atomBuffer.test() + "<br/><br/>";
                /*
                System.out.println("5.10 Mapping <Not, entity> to <entity, relation type, entity>.");
                QueryTriple tripleAtomSetForNOTConj = ProcessingQuery
                .ProcessQuery2TripleForNOTConj(atomBuffer);

                if (tripleAtomSetForNOTConj != null
                && tripleAtomSetForNOTConj.length > 0) {
                for (int h = 0; h < tripleAtomSetForNOTConj.length; h++) {
                tripleAtomSet.tripleset[tripleAtomSet.length] = tripleAtomSetForNOTConj.tripleset[h];
                tripleAtomSet.length++;
                }
                }
                 */
                // Remove redundancy triple
                System.out.println("5.11 Remove redundancy triple.");
                RemoveTriple(tripleAtomSet, atomBuffer);

                System.out.println("tripleAtomSet is: " + tripleAtomSet.length);
                /*
                tripleAtomSet.length++;
                ItemType sub = new ItemType();
                sub.value = "father";
                sub.className = "Man";
                sub.classType = "UE";
                sub.quantifier = "?";
                sub.var = "a";
                ItemType obj = atomBuffer.buffer[3];
                //obj.value = "Bush";
                //obj.className = "Man";
                //obj.classType = "IE";
                //obj.ID = "http://www.ontotext.com/kim/2006/05/wkb#Person_T.80";
                //obj.name = "Bush";
                //obj.quantifier = "Bush";
                //obj.var = "c";
                ItemType rel = new ItemType();
                rel.value = "of";
                rel.className = "RW";
                rel.classType = "RW";
                rel.relation = "hasFather";
                rel.direction = 2;
                rel.relcount = 2;
                rel.var = "b";
                TripleType tt = new TripleType();
                tt.subject = sub;
                tt.object = obj;
                tt.rel = rel;
                tt.relationName = "hasFather";
                tt.direction = 2;
                tripleAtomSet.tripleset[1] = tt;

                tripleAtomSet.length++;
                ItemType sub1 = new ItemType();
                sub1.value = "father";
                sub1.className = "Man";
                sub1.classType = "UE";
                sub1.quantifier = "?";
                sub1.var = "a";
                ItemType obj1 = atomBuffer.buffer[1];
                //obj.value = "Bush";
                //obj.className = "Man";
                //obj.classType = "IE";
                //obj.ID = "http://www.ontotext.com/kim/2006/05/wkb#Person_T.80";
                //obj.name = "Bush";
                //obj.quantifier = "Bush";
                //obj.var = "c";
                ItemType rel1 = new ItemType();
                rel1.value = "of";
                rel1.className = "RW";
                rel1.classType = "RW";
                rel1.relation = "hasFather";
                rel1.direction = 2;
                rel1.relcount = 2;
                rel1.var = "b";
                TripleType tt1 = new TripleType();
                tt1.subject = sub1;
                tt1.object = obj1;
                tt1.rel = rel1;
                tt1.relationName = "hasFather";
                tt1.direction = 2;
                tripleAtomSet.tripleset[2] = tt1;
                 */

                // Identify the relationship between Adjective and Entity
                System.out.println("5.12 Identify the relationship between Adjective and Entity.");

                System.out.println("5.12a Identify the relationship between Quantitative Adjective and Entity.");
                //ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                 //       atomBuffer);

                System.out.println("5.12b Identify the relationship between Quanlitative Adjective and Entity.");
                ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                        atomBuffer);
                /*
                if (tripleAtomSet.length <= 0) {
                boolean hasRW = false;
                for (int j = 0; j < atomBuffer.length; j++) {
                if (atomBuffer.getItem(j).classType.equalsIgnoreCase(Constants.UE) && !atomBuffer.getItem(j).value.equalsIgnoreCase(Constants.HOW_MANY_STRING) && !atomBuffer.getItem(j).delete) {
                hasRW = true;
                break;
                }
                }
                if (hasRW) {
                System.out.println("5.12.1 Get suggested triple <entity, relation type, entity> from entity and RW.");

                tripleAtomSet = ProcessingQuery.getSuggestedTripleIncaseOnlyOneEntity(atomBuffer);
                }

                }*/
                // Get new QueryBuffer for purpose of generating CG
/*
                if (atomBuffer.getQuery().contains("how many people")) {
                for (int ii = 0; ii < tripleAtomSet.length; ii++) {
                TripleType temp = tripleAtomSet.getItem(ii);
                ItemType obj = temp.object;
                ItemType sub = temp.subject;
                ItemType rel = temp.rel;
                if (sub.className.equalsIgnoreCase("Person")) {
                sub.className = "String";
                sub.classType = "Property";
                sub.quantifier = "?";
                sub.var = "a";

                rel.value = "hasProperty";
                rel.className = Constants.RW;
                rel.classType = Constants.RW;
                rel.relation = "populationCount";
                rel.direction = 2;
                rel.relcount = 2;
                rel.var = "b";

                temp.relationName = "populationCount";
                temp.direction = 2;

                listCG.setTopRel(null);
                }
                }
                }
                 */
                if (listCG.getTopRel() != null && listCG.getTopRel().name().equals(
                        TopRelationType.COUNT.name())) {
                    for (int ii = 0; ii < tripleAtomSet.length; ii++) {
                        if (tripleAtomSet.getItem(ii).relationName.equalsIgnoreCase("populationCount")) {
                            listCG.setTopRel(null);
                            break;
                        }
                    }
                }

                System.out.println("5.13 Get new QueryBuffer for purpose of generating CG.");
                QueryBuffer buferAtomTmp = MakeQueryBuffer(tripleAtomSet);

                System.out.println("bufferAtomTmp is: " + buferAtomTmp.test());

                for (int j = 0; j < buferAtomTmp.length; j++) {
                    if (buferAtomTmp.getItem(j).delete == false) {
                        isvalid = true;
                        break;
                    }
                }
                buferAtomTmp.query = atomBuffer.query;
                buferAtomTmp.setRelToNextAtomQuery(atomBuffer.getRelToNextAtomQuery());
                System.out.println("5.14 Specify quntifiers again.");
                ProcessingQuery.ReSpecifyQuantifier(buferAtomTmp);

                if (isvalid == false) {
                    System.out.println("5.14.1 Processing in case how many has only one Entity");
                    if (listCG.getTopRel() != null && listCG.getTopRel().name().equals(
                            TopRelationType.COUNT.name())) {
                        // In case the query "how many" have only one entity.
                        buferAtomTmp.length = 1;
                        for (int v = 0; v < atomBuffer.length; v++) {
                            String type = atomBuffer.buffer[v].classType;
                            if (!atomBuffer.buffer[v].className.equals("UE_How many") && (type.equals(Constants.UE) || type.equals(Constants.IE))) {
                                buferAtomTmp.buffer[0] = atomBuffer.buffer[v];
                                buferAtomTmp.buffer[0].delete = false;
                                buferAtomTmp.buffer[0].col = 1;
                                buferAtomTmp.buffer[0].row = 1;
                                buferAtomTmp.totalcol = 1;
                                buferAtomTmp.totalrow = 1;
                                break;
                            }
                        }
                        isvalid = true;
                    }
                }

                listCG.getListQB().add(buferAtomTmp);

                //tao serql
                ProcessingQuery.IsValid = isvalid;
                if (isvalid) {
                    String SeRQLAtom = SeRQLMapping.getSeRQLQuery(buferAtomTmp);
                    //Add Where clause for MAX QTA
                    if (buferAtomTmp.isContainSuperlativeQTA()) {

                        SeRQLAtom = appendWhereClauseForSQTA(SeRQLAtom, buferAtomTmp, listCG.getTopRel());

                    } else if (buferAtomTmp.isContainQTA()) {

                        SeRQLAtom = appendOrderByClause(SeRQLAtom, buferAtomTmp);

                    } else if (buferAtomTmp.isContainCQTA()) {

                        SeRQLAtom = appendWhereClauseForCQTA(SeRQLAtom,
                                buferAtomTmp);
                    }


                    SeRQL = SeRQL + " " + SeRQLAtom;

                    if (nextRel == RelationType.UNION) {
                        SeRQL = SeRQL + " UNION";
                    }
                    if (nextRel == RelationType.INTERSECT) {
                        SeRQL = SeRQL + " INTERSECT";
                    }

                //resulttable = SesameUtils.runSeRQLStm(SeRql);
                /*if (resulttable != null) {
                for (int i = 0; i < resulttable.getRowCount(); i++) {
                String sidname = resulttable.getValue(i, 0).toString();
                sidname = sidname.substring(sidname.indexOf("#") + 1);
                result += sidname + ":" + resulttable.getValue(i, 1);
                result += ";";
                }
                }*/
                }
            }


            // Generate the CG Graphic
            System.out.println("6. Generate the CG Graphic.");
            String out = QueryOutput.generatelistJsCG(listCG);
            ProcessingQuery.IsValid = isvalid;
            result.setValid(isvalid);
            outtest = out + outtest;

            System.out.println("isvalid is: " + isvalid);
            if (isvalid) {
                result.setCg(out);

                System.out.println("SeRQL is: " + SeRQL);
                String entities = "";// ENSearch.runSeRQL(SeRQL);
                if (!entities.equalsIgnoreCase("")) {
                    entities = entities.substring(0, entities.length() - 1);
                }
                if (listCG.getTopRel() != null && listCG.getTopRel().name().equalsIgnoreCase(TopRelationType.COUNT.name())) {
                    entities = " :" + entities.split(";").length;
                }
                if (listCG.getTopRel() != null && listCG.getTopRel().name().equalsIgnoreCase(TopRelationType.AVERAGE.name())) {
                    double temp = 0;
                    int count = 0;
                    String[] e = entities.split("[\",:,;]");
                    for (int i = 0; i < e.length; i++) {
                        if (!e[i].trim().isEmpty()) {
                            temp = temp + Double.parseDouble(e[i]);
                            count++;
                        }
                    }
                    temp = temp / count;
                    entities = " :" + temp;
                }

                System.out.println(entities);
                outtest += "\n" + SeRQL + "\n" + "Result Entities:" + entities;
            }

            System.out.println("---------------End processQueryForWeb");
        } catch (Exception e) {
            System.out.println("Error occurs when processing query.");
            e.printStackTrace();

        }

        // return output;
        return outtest;
    }

    public static void splitQueryToAtomicQuery(String fullQuery,
            List<ItemType> query, List<AtomicQuery> result,
            RelationType defaultRel) {
        if (query != null && query.size() > 0) {
            // Get position of conjunction in the query
            int conjPos = 0;
            String conjStr = "";
            for (; conjPos < query.size(); conjPos++) {
                ItemType t = query.get(conjPos);
                if (t.classType.equals(Constants.CONJ) && (t.className.equals(RelationType.UNION.name()) || t.className.equals(RelationType.INTERSECT.name()) || t.className.equals(RelationType.MINUS.name()))) {
                    conjStr = t.className;
                    break;
                }
            }
            AtomicQuery aqr = new AtomicQuery();
            if (conjStr.equals(RelationType.UNION.name())) {
                aqr.setNextReal(RelationType.UNION);
            } else if (conjStr.equals(RelationType.INTERSECT.name())) {
                aqr.setNextReal(RelationType.INTERSECT);
            } else if (conjStr.equals(RelationType.MINUS.name())) {
                aqr.setNextReal(RelationType.MINUS);
            } else if (conjStr.equals("")) {
                aqr.setNextReal(defaultRel);
            }

            if (conjPos >= query.size()) {
                // Atomic query
                String atomicQuery = buildSentenceFromQueryBuffer(fullQuery,
                        query);
                QueryBuffer atomicQueryBuffer = new QueryBuffer();
                atomicQueryBuffer.query = atomicQuery;
                atomicQueryBuffer.length = query.size();
                atomicQueryBuffer.buffer = query.toArray(new ItemType[atomicQueryBuffer.length+5]);
                aqr.setQuery(atomicQueryBuffer);
                System.out.println("\t\t - Atomic Query:" + atomicQuery);
                if (!atomicQuery.trim().equals("")) {
                    result.add(aqr);
                }
                return;
            } else {
                List<ItemType> atomQBuff = new ArrayList<ItemType>();

                // Entity next to conjunction word
                int posEntityNextConj = getEntityOrRelationNext(conjPos,
                        ElementType.Entity, query);

                // Relation next to entity that net to conjunction word
                int posRelationNextEntity = getEntityOrRelationNext(
                        posEntityNextConj, ElementType.Relation, query);
                if (posRelationNextEntity > 0) {
                    ItemType rel = query.get(posRelationNextEntity);
                    if (getEntityOrRelationNext(posRelationNextEntity,
                            ElementType.Entity, query) < 0 && rel.value.equalsIgnoreCase("is")) {
                        posRelationNextEntity = -1;
                    }// Case: exclude automatic appended relation ("is)
                // (GateNamedEntity - Line 233)
                }

                // Entity previous to conjunction word
                int posEntityPrevConj = getEntityOrRelationPrevious(conjPos,
                        ElementType.Entity, query);

                // Relation previous to conjunction word
                int posRelationPrevConj = getEntityOrRelationPrevious(conjPos,
                        ElementType.Relation, query);
                // Entity prev Relation
                int posEntityPrevRelation = getEntityOrRelationPrevious(
                        posRelationPrevConj, ElementType.Entity, query);

                // Relation previous to entity that previous to conjunction word
                int posRelationPrevEntity = getEntityOrRelationPrevious(
                        posEntityPrevConj, ElementType.Relation, query);

                // Relation next to conjunction word
                int posRelationNextConj = getEntityOrRelationNext(conjPos,
                        ElementType.Relation, query);
                if (posRelationNextConj > 0) {
                    ItemType rel = query.get(posRelationNextConj);
                    if (getEntityOrRelationNext(posRelationNextConj,
                            ElementType.Entity, query) < 0 && rel.value.equalsIgnoreCase("is")) {
                        posRelationNextConj = -1;
                    }// Case: exclude automatic appended relation ("is)
                // (GateNamedEntity - Line 233)
                }
                // Entity next to relation that next to conjunction word
                int posEntityNextRelation = getEntityOrRelationNext(
                        posRelationNextConj, ElementType.Entity, query);

                if (posEntityNextConj > 0 
                        && posRelationNextEntity < 0
                        && posEntityPrevConj > 0
                        && posRelationPrevEntity >= 0
                        && posRelationNextConj < 0) {
                    // Case1 E1 RW E2 or E3 --> (E1 RW E2) or (E1 RW E3)
                    for (int i = 0; i < conjPos; i++) {
                        atomQBuff.add(query.get(i));
                    }
                    for (int j = conjPos; j >= posEntityPrevConj; j--) {
                        query.remove(j); // Remove from prevEntity to CONJ
                    }
                    String atomicQSentence = buildSentenceFromQueryBuffer(
                            fullQuery, atomQBuff);
                    System.out.println("Atomic Query:" + atomicQSentence);
                    if (!atomicQSentence.trim().equals("")) {
                        QueryBuffer atomicQueryBuffer = new QueryBuffer();
                        atomicQueryBuffer.query = atomicQSentence;
                        atomicQueryBuffer.length = atomQBuff.size();
                        atomicQueryBuffer.buffer = atomQBuff.toArray(new ItemType[atomicQueryBuffer.length]);
                        aqr.setQuery(atomicQueryBuffer);
                        result.add(aqr);
                    }
                    splitQueryToAtomicQuery(fullQuery, query, result,
                            defaultRel);
                } else if (posRelationNextConj > posEntityNextConj
                        && posEntityNextConj > 0
                        && posRelationNextEntity > 0
                        && posEntityPrevConj >= 0
                        && posRelationPrevEntity < 0
                        && posEntityNextRelation > 0) {
                    // Case2 E1 or E2 RW E3 --> (E1 RW E3) or (E2 RW E3)

                    // Initial 2 list for subquery
                    List<ItemType> subQ1 = new ArrayList<ItemType>();
                    List<ItemType> subQ2 = new ArrayList<ItemType>();
                    for (int i = 0; i < query.size(); i++) {
                        subQ1.add(query.get(i));
                        subQ2.add(query.get(i));
                    }

                    // Make subquery for (E1 RW E3)
                    for (int k = conjPos; k < posRelationNextEntity; k++) {
                        subQ1.remove(posEntityPrevConj + 1);
                    }

                    // Make subquery for (E2 RW E3)
                    if (query.get(0).className.equals(Constants.UE_AGENT)) {
                        for (int j = 1; j <= conjPos; j++) {
                            subQ2.remove(1);
                        }

                    } else {
                        for (int j = 0; j <= conjPos; j++) {
                            subQ2.remove(0);
                        }

                    }

                    RelationType nR = null;
                    if (conjStr.equals(RelationType.UNION.name())) {
                        nR = RelationType.UNION;
                    } else if (conjStr.equals(RelationType.INTERSECT.name())) {
                        nR = RelationType.INTERSECT;
                    } else if (conjStr.equals(RelationType.MINUS.name())) {
                        nR = RelationType.MINUS;
                    }

                    splitQueryToAtomicQuery(fullQuery, subQ1, result, nR);
                    splitQueryToAtomicQuery(fullQuery, subQ2, result, null);

                } else if (posEntityNextConj > 0 
                        && posRelationNextEntity > 0
                        && posEntityPrevConj > 0
                        && posRelationPrevEntity > 0
                        && posEntityNextRelation > 0) {
                    // Case 3: E1 RW E2 or E1 RW E3 --> (E1 RW E2) or (E1 RW E3)
                    for (int i = 0; i < conjPos; i++) {
                        atomQBuff.add(query.get(i));
                    }
                    String atomicQSentence = buildSentenceFromQueryBuffer(
                            fullQuery, atomQBuff);
                    System.out.println("Atomic Query:" + atomicQSentence);
                    if (!atomicQSentence.trim().equals("")) {
                        QueryBuffer atomicQueryBuffer = new QueryBuffer();
                        atomicQueryBuffer.query = atomicQSentence;
                        atomicQueryBuffer.length = atomQBuff.size();
                        atomicQueryBuffer.buffer = atomQBuff.toArray(new ItemType[atomicQueryBuffer.length]);
                        aqr.setQuery(atomicQueryBuffer);
                        result.add(aqr);
                    }

                    if (query.get(0).className.equals(Constants.UE_AGENT)) {
                        for (int j = 1; j <= conjPos; j++) {
                            query.remove(1);
                        }

                    } else {
                        for (int j = 0; j <= conjPos; j++) {
                            query.remove(0);
                        }

                    }
                    splitQueryToAtomicQuery(fullQuery, query, result, null);
                } else if (posRelationNextConj > 0
                        && posEntityNextRelation > 0
                        && posEntityPrevConj > 0
                        && posRelationPrevEntity > 0) {
                    // Case 4: E1 RW E2 or RW E3 --> (E1 RW E2) or (E1 RW E3)
                    for (int i = 0; i < conjPos; i++) {
                        atomQBuff.add(query.get(i));
                    }
                    String atomicQSentence = buildSentenceFromQueryBuffer(
                            fullQuery, atomQBuff);
                    System.out.println("Atomic Query:" + atomicQSentence);
                    if (!atomicQSentence.trim().equals("")) {
                        QueryBuffer atomicQueryBuffer = new QueryBuffer();
                        atomicQueryBuffer.query = atomicQSentence;
                        atomicQueryBuffer.length = atomQBuff.size();
                        atomicQueryBuffer.buffer = atomQBuff.toArray(new ItemType[atomicQueryBuffer.length]);
                        aqr.setQuery(atomicQueryBuffer);
                        result.add(aqr);
                    }
                    int posSub = getEntityOrRelationPrevious(
                            posRelationPrevEntity, ElementType.Entity, query);

                    if (posSub >= 0) {
                        for (int j = posSub + 1; j <= conjPos; j++) {
                            query.remove(posSub + 1);
                        }

                        splitQueryToAtomicQuery(fullQuery, query, result,
                                defaultRel);
                    }

                } else if (posRelationNextConj > 0
                        && posEntityNextRelation > 0
                        && posRelationPrevConj > 0
                        && posEntityPrevRelation > 0) {
                    // Case 5: E1 RW1 or RW2 E2 --> (E1 RW1 E2) or (E1 RW2 E2)

                    // Initial 2 list for subquery
                    List<ItemType> subQ1 = new ArrayList<ItemType>();
                    List<ItemType> subQ2 = new ArrayList<ItemType>();
                    for (int i = 0; i < query.size(); i++) {
                        subQ1.add(query.get(i));
                        subQ2.add(query.get(i));
                    }

                    // Make subquery for (E1 RW1 E2)
                    for (int k = conjPos; k <= posRelationNextConj; k++) {
                        subQ1.remove(posRelationPrevConj + 1);
                    }

                    // Make subquery for (E1 RW2 E2)
                    for (int k = posRelationPrevConj; k <= conjPos; k++) {
                        subQ2.remove(posRelationPrevConj);
                    }

                    RelationType nR = null;
                    if (conjStr.equals(RelationType.UNION.name())) {
                        nR = RelationType.UNION;
                    } else if (conjStr.equals(RelationType.MINUS.name())) {
                        nR = RelationType.MINUS;
                    } else if (conjStr.equals(RelationType.INTERSECT.name())) {
                        nR = RelationType.INTERSECT;
                    }

                    splitQueryToAtomicQuery(fullQuery, subQ1, result, nR);
                    splitQueryToAtomicQuery(fullQuery, subQ2, result, null);

                } else {
                    System.out.println("New format. Not researched yet.");
                    // Process as an atomic query
                    String atomicQuery = buildSentenceFromQueryBuffer(fullQuery,
                            query);
                    QueryBuffer atomicQueryBuffer = new QueryBuffer();
                    atomicQueryBuffer.query = atomicQuery;
                    atomicQueryBuffer.length = query.size();
                    atomicQueryBuffer.buffer = query.toArray(new ItemType[atomicQueryBuffer.length + 5]);
                    aqr.setQuery(atomicQueryBuffer);
                    System.out.println("\t\t - Atomic Query:" + atomicQuery);
                    if (!atomicQuery.trim().equals("")) {
                        result.add(aqr);
                    }
                    return;
                }

            }
        }

    }

    /**
     * Build sentence query from QueryBuffer data.
     *
     * @return
     */
    private static String buildSentenceFromQueryBuffer(String fullQuery,
            List<ItemType> buffer) {
        String result = "";
        if (buffer != null && buffer.size() > 0) {
            for (int i = 0; i < buffer.size(); i++) {
                System.out.print(buffer.get(i).classType + " ");
                ItemType t = buffer.get(i);
                if (t != null) {
                    t.start = result.length();
                    t.end = result.length() + t.value.length();
                    ItemType tnew = makeClone(t);
                    buffer.set(i, tnew);

                    if (t.classType.equalsIgnoreCase(Constants.SUPERLATIVE_QUANTITATIVE_ADJ)) {
                        result += t.value + " ";
                    } else if ((t.wordbefore.equalsIgnoreCase(Constants.MOST_STRING) || t.wordbefore.equalsIgnoreCase(Constants.LEAST_STRING)) && (t.value.indexOf(Constants.MOST_STRING) < 0 && t.value.indexOf(Constants.LEAST_STRING) < 0)) {
                        result += t.wordbefore + " " + t.value + " ";
                    } else {
                        result += t.value + " ";
                    }
                }
            }
        }
        System.out.println();
        return result;
    }

    /**
     * Identify the top relation
     *
     * @param t
     */
    public static TopRelationType identifyTopRel(QueryBuffer buf) { //MODIFY
        TopRelationType ret = null;
        if (buf != null) {
            if (buf.getQuery().toLowerCase().contains(Constants.HOW_MANY_STRING) //HOW MANY -> COUNT
                    && !buf.getQuery().toLowerCase().contains("how many miles")) {

                ret = TopRelationType.COUNT;

            } else {
                for (int j = 0; j < buf.length; j++) {
                    ItemType it = buf.getItem(j);
                    if (it != null && (it.classType.equals(Constants.SUPERLATIVE_QUANTITATIVE_ADJ))) { //DINH LUONG
                        if (it.value.toLowerCase().indexOf(
                                Constants.MOST_STRING) >= 0) {
                            ret = TopRelationType.MAX;
                        } else if (it.value.toLowerCase().indexOf(
                                Constants.LEAST_STRING) >= 0) {
                            ret = TopRelationType.MIN;
                        } else {
                            ret = specifyMAX_MIN_relation(it.value); // SHORT ADJ
                        }
                        break;
                    } else if (it != null && (it.classType.equals(Constants.UE) || (it.classType.length() > 2 && it.classType.substring(0,
                            3).equals(Constants.UE)))) { //UE
                        if (it.wordbefore.equalsIgnoreCase(Constants.MOST_STRING)) {
                            ret = TopRelationType.MOST;
                            break;

                        } else if (it.wordbefore.equalsIgnoreCase(Constants.AVERAGE_STRING)) {
                            ret = TopRelationType.AVERAGE;
                            break;
                        }

                    }

                }
            }
        }
        return ret;
    }

    /**
     * Identify exactyly top relation is MAX of MIN
     *
     * @param adj
     * @return
     */
    private static TopRelationType specifyMAX_MIN_relation(String adj) { //MODIFY
        TopRelationType ret = null;
        try {
            String supAdjType = ProcessingXML.specifyMAX_MINfromDic(adj);
            if (supAdjType != null && supAdjType.trim().equalsIgnoreCase("max")) {
                ret = TopRelationType.MAX;
            } else if (supAdjType != null && supAdjType.trim().equalsIgnoreCase("min")) {
                ret = TopRelationType.MIN;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static String specifyOrderType(String adj) {
        String result = null;
        try {
            String OrderType = ProcessingXML.specifyOrderTypeFromDic(adj);

            if (OrderType != null && OrderType.trim().equalsIgnoreCase(Constants.ASC)) {

                result = Constants.ASC;

            } else if (OrderType != null && OrderType.trim().equalsIgnoreCase(Constants.DESC)) {

                result = Constants.DESC;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static ItemType makeClone(ItemType t) {
        ItemType tnew = new ItemType();
        tnew.ID = t.ID;
        tnew.className = t.className;
        tnew.classType = t.classType;
        tnew.start = t.start;
        tnew.end = t.end;
        tnew.value = t.value;
        tnew.wordbefore = t.wordbefore;
        tnew.wordfollow = t.wordfollow;
        tnew.name = t.name;
        tnew.progreg = t.progreg;
        tnew.col = t.col;
        tnew.delete = t.delete;
        tnew.direction = t.direction;
        tnew.variable = t.variable;
        tnew.quantifier = t.quantifier;
        tnew.subindex = t.subindex;
        tnew.violation = t.violation;
        return tnew;
    }

    private static int getEntityOrRelationNext(int currentPos,
            ElementType type, List<ItemType> buffer) {
        int ret = -1;
        if (currentPos >= 0 && buffer != null) {
            for (int i = currentPos; i < buffer.size(); i++) {
                ItemType t = buffer.get(i);
                if (isEntityOrRelation(t, type)) {// E or RW
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * This method is used to get the position of an entity or a relation that
     * be previous to current position with given number steps
     *
     * @param currentPos
     * @param steps
     * @param buffer
     * @return
     */
    private static int getEntityOrRelationPrevious(int currentPos,
            ElementType type, List<ItemType> buffer) {
        int ret = -1;
        if (currentPos >= 0 && buffer != null) {
            for (int i = currentPos; i >= 0; i--) {
                ItemType t = buffer.get(i);
                if (isEntityOrRelation(t, type)) {// E or RW
                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * this method return the boolean show if item is entity, relation or not.
     *
     * @param item
     * @return
     */
    private static boolean isEntityOrRelation(ItemType t, ElementType type) {
        boolean ret = false;
        if (type == ElementType.Entity) {
            if (t.classType != null && (t.classType.equals(Constants.IE) || t.classType.equals(Constants.UE) || t.classType.substring(0, 2).equals(Constants.UE))) {
                ret = true;
            }// Entity
        } else if (type == ElementType.Relation) {
            if (t.classType != null && (t.classType.equals(Constants.RW))) {
                ret = true;
            }// RW
        }
        return ret;
    }

    public static QueryBuffer MakeQueryBuffer(QueryTriple tripleset) //MODIFY
            throws Exception {
        QueryBuffer buffer = new QueryBuffer();
        ItemType sub;
        ItemType rel;
        ItemType obj;
        int subindex = 0; //vi tri subject cua 1 relation
        int objindex = 0; //vi tri object cua 1 relation
        boolean check = false;

        for (int i = 0; i < tripleset.length; i++) {
            TripleType tmp = tripleset.getItem(i);
            /*
             * if(tmp.isDelete) { tmp.subject.totalrel--; tmp.object.totalrel--;
             * tmp.rel.totalrel--; }
             */
            if (!tmp.isDelete) {
                check = false;

                if (tmp.object.totalrel > tmp.subject.totalrel) {
                    ItemType temp = tmp.object;
                    tmp.object = tmp.subject;
                    tmp.subject = temp;
                    if (tmp.direction == 1) {
                        tmp.direction = 2;
                    } else {
                        tmp.direction = 1;
                    }
                }

                sub = tmp.subject;
                sub.totalrel++;
                for (int j = 0; j < buffer.length; j++) {
                    if (buffer.buffer[j] == tmp.subject) {
                        subindex = j;
                        check = true;
                        break;
                    }
                }// Check if subject is exist in buffer
                if (!check) {
                    // Insert subject to buffer
                    buffer.buffer[buffer.length] = sub;
                    buffer.length++;
                    subindex = buffer.length - 1;
                    tripleset.SetItemRow(buffer, sub, 1);
                    tripleset.SetItemCol(buffer, sub, i * 3 + 1);
                }

                // Insert relation to buffer
                rel = buffer.InsertItemOrg(tmp.rel.value, tmp.rel.className,
                        tmp.rel.classType, tmp.rel.start, tmp.rel.end,
                        tmp.rel.progreg, tmp.rel.wordfollow,
                        tmp.rel.wordbefore);
                rel.quantifier = tmp.rel.quantifier;
                rel.text = tmp.rel.quantifier;
                rel.relation = tmp.relationName;
                rel.direction = tmp.direction;

                if (tmp.rel.isIdentifiedFromQTA()) {
                    rel.setIdentifiedFromQTA(tmp.rel.isIdentifiedFromQTA());
                    buffer.setContainQTA(true); //for generating SeRQL
                }


                if (tmp.rel.isIdentifiedFromSQTA()) {
                    rel.setIdentifiedFromSQTA(tmp.rel.isIdentifiedFromSQTA());
                    buffer.setContainSuperlativeQTA(true); //for generating SeRQL
                }

                rel.subindex = subindex;

                tripleset.SetItemRow(buffer, rel, sub.row - 1 + sub.totalrel);
                tripleset.SetItemCol(buffer, rel, sub.col + 1);

                check = false;
                obj = tmp.object;
                for (int j = 0; j < buffer.length; j++) {
                    if (buffer.buffer[j] == obj) {
                        objindex = j;
                        check = true;
                        break;
                    }
                }// Check if object is exist in buffer
                if (!check) {
                    // Insert object to buffer
                    buffer.buffer[buffer.length] = obj;
                    buffer.length++;
                    objindex = buffer.length - 1;
                    tripleset.SetItemRow(buffer, obj, sub.row - 1 + sub.totalrel);
                    tripleset.SetItemCol(buffer, obj, sub.col + 2);
                }
                obj.totalrel++;
                if (obj.totalrel > 1) {
                    tripleset.SetItemRow(buffer, rel, sub.row + sub.totalrel + 1);
                }
                rel.objindex = objindex;

            }
        }
        return buffer;
    }

    public static String appendWhereClauseForSQTA(String query, QueryBuffer buf, TopRelationType topRe) {

        /*To create the template:
         *select...
         *from...
         *where  xsd:double(QTA) >= (<=)
         *                          ALL ( Select xsd:double(QTA)
         *                                From   ....
         *                                Where  ....
         *                              )
         */
        String operator, leftOperand, rightOperand;
        String sClause = "";
        String fClause = "";
        String wClause = "";

        String subSelClause = "select ";
        String subFromClause = "\nfrom ";

        //seperate clauses
        int sStart = query.indexOf("select");
        int fStart = query.indexOf("from");
        int wStart = query.indexOf("where") != -1 ? query.indexOf("where") : -1;

        sClause = query.substring(sStart, fStart);
        fClause = wStart != -1 ? query.substring(fStart, wStart) : query.substring(fStart);
        wClause = wStart != -1 ? query.substring(wStart) : "where";


        operator = topRe.name().equals(TopRelationType.MAX.name()) ? ">= ALL" : "<= ALL";

        leftOperand = "xsd:double";

        for (int i = 0; i < buf.length; i++) {
            ItemType tmp = buf.getItem(i);

            if (tmp.delete) {
                continue;
            }

            if (tmp.className.compareTo("RW") == 0 && tmp.isIdentifiedFromSQTA()) {

                int[] domain = {tmp.subindex, tmp.objindex};
                ItemType concept;

                for (int j = 0; j < domain.length; j++) {
                    concept = buf.getItem(domain[j]);

                    if (concept.quantifier.equals("?")) {
                        subFromClause =
                                SeRQLMapping.addStmToExternalFromClause(
                                "{" + concept.variable + "temp" + "}" + "rdf:type {<" + ENSearch.getNS(concept.className.replaceAll("UE_", "")) + concept.className.replaceAll("UE_", "") + ">}",
                                subFromClause);
                    } else {
                        if (concept.quantifier.equals("*")) {
                            subSelClause = subSelClause + "xsd:double(" + concept.variable + "temp" + ")";

                            leftOperand += "(" +
                                    concept.variable + ")";
                        }
                    }
                }
                if (tmp.direction == 1) {
                    subFromClause = SeRQLMapping.addStmToExternalFromClause(
                            "{" + buf.getItem(tmp.subindex).variable + "temp" + "}" + " <" + ENSearch.getNS(tmp.relation) + tmp.relation + "> " + "{" + buf.getItem(tmp.objindex).variable + "temp" + "}", subFromClause);
                } else {
                    subFromClause = SeRQLMapping.addStmToExternalFromClause(
                            "{" + buf.getItem(tmp.objindex).variable + "temp" + "}" + " <" + ENSearch.getNS(tmp.relation) + tmp.relation + "> " + "{" + buf.getItem(tmp.subindex).variable + "temp" + "}", subFromClause);
                }

                break;
            }//end of if(RW && Superlative)
        }//end of for
        rightOperand = "(" + subSelClause + "\n" + subFromClause + ")";

        if (wStart != -1) {
            wClause += " AND " + leftOperand + " " + operator + " " + rightOperand;
        } else {
            wClause += " " + leftOperand + " " + operator + " " + rightOperand;
        }

        return (sClause + "\n" + fClause + "\n" + wClause);
    }

    public static String appendOrderByClause(String query, QueryBuffer buf) {

        String sClause = "";
        String fClause = "";
        String oClause = "\norder by ";
        String OrderType = "";

        //Seperate Select and From clauses
        int startIndex = query.indexOf("select");
        int endIndex = query.indexOf("from", startIndex);

        sClause = query.substring(startIndex, endIndex);
        fClause = "\n" + query.substring(endIndex);

        //Modify SeRQL query to order result
        for (int i = 0; i < buf.length; i++) {
            ItemType tmp = buf.getItem(i);

            if (tmp.delete) {
                continue;
            }

            if (tmp.className.compareTo("RW") == 0 && tmp.isIdentifiedFromQTA()) {

                //Seperate "Select" and "From" clause
                sClause += "," + buf.getItem(tmp.objindex).variable;

                OrderType = specifyOrderType(buf.getItem(tmp.objindex).value);

                if (OrderType == null) {

                    OrderType = Constants.ASC;

                }

//                oClause = "\norder by xsd:double("
                oClause += "xsd:double(" + buf.getItem(tmp.objindex).variable + ")" + " " + OrderType + ",";

            }
        }
        System.out.println(sClause + fClause + oClause);
        return (sClause + fClause + oClause.substring(0, oClause.length() - 1));
    }


    public static String appendWhereClauseForCQTA(String query, QueryBuffer buf) {
        String sClause = "";
        String fClause = "";
        String wClause = "";

        String operator;

        //seperate clauses
        int sStart = query.indexOf("select");
        int fStart = query.indexOf("from");
        int wStart = query.indexOf("where") != -1 ?
                        query.indexOf("where") : -1;

        sClause = query.substring(sStart, fStart);
        fClause = wStart != -1 ? query.substring(fStart, wStart) :
                                query.substring(fStart);
        wClause = wStart != -1 ? query.substring(wStart) : "where";



//        leftOperand = "xsd:double" ;

        for(int i=0; i<buf.length; i++) {
            ItemType tmp = buf.getItem(i);

            if (tmp.delete) { continue; }

            if (tmp.className.compareTo("RW") == 0
                && (tmp.relation.contains(Constants.GREATER)
                    || tmp.relation.contains(Constants.SMALLER))) {

                operator = tmp.relation.contains(Constants.GREATER) ? ">" : "<";

                 if (wStart != -1) {
                     wClause += " AND "
                                + "xsd:double("
                                + buf.getItem(tmp.subindex).variable + ")"
                                + " " + operator + " "
                                + "xsd:double("
                                + (
                                buf.getItem(tmp.objindex).className
                                                .equals(Constants.REAL_NUMBER) ?
                                 buf.getItem(tmp.objindex).quantifier :
                                 buf.getItem(tmp.objindex).variable
                                 )
                                + ")";
                 } else {
                     wClause += " "
                                + "xsd:double("
                                + buf.getItem(tmp.subindex).variable + ")"
                                + " " + operator + " "
                                + "xsd:double("
                                + (
                                buf.getItem(tmp.objindex).className
                                                .equals(Constants.REAL_NUMBER) ?
                                 buf.getItem(tmp.objindex).quantifier :
                                 buf.getItem(tmp.objindex).variable
                                 )
                                + ")";
                     wStart = 0; //signal so that next loop (if any)
                                //will use the "wStart != -1" case
                 }
            }
        }//end of for

        return (sClause + "\n" + fClause + "\n"  + wClause);
    }


    public static void RemoveTriple(QueryTriple tripleset, QueryBuffer buffer)
            throws Exception {
        for (int i = tripleset.length - 1; i >= 0; i--) {
            TripleType tmp1 = tripleset.getItem(i);
            if (!tmp1.isDelete) {

                // Get string between subject and object
                String st = "ABC";
                if (tmp1.subject.end < tmp1.object.start) {
                    st = ProcessingXML.GetStringBetween2Item(tmp1.subject,
                            tmp1.object, buffer);
                }
                if (tmp1.object.end < tmp1.subject.start) {
                    st = ProcessingXML.GetStringBetween2Item(tmp1.object,
                            tmp1.subject, buffer);
                }
                if ((st.trim().compareToIgnoreCase("and") == 0) || (st.trim().compareToIgnoreCase("or") == 0)) {
                    tmp1.isDelete = true;
                }// Case "and", "or"

                // Get relation between subject and object
                ItemType item = ProcessingXML.GetItemBetween2Item(tmp1.subject,
                        tmp1.object, buffer);
                if (item != null) {
                    if (item.value.trim().compareToIgnoreCase("'s") == 0) {
                        // Process for case relation 's between subject and
                        // object
                        for (int j = 0; j < i; j++) {
                            if (i != j) {
                                if ((tripleset.getItem(j).subject == tmp1.subject) || (tripleset.getItem(j).object == tmp1.subject)) {
                                    tripleset.getItem(j).isDelete = true;
                                }
                            }
                        }
                    }
                }
                if ((st.replace('"', ' ').trim().compareTo(",") == 0) || (st.trim().length() == 0)) {
                    for (int j = 0; j < i; j++) {
                        if (i != j) {
                            if ((tripleset.getItem(j).object == tmp1.object) || (tripleset.getItem(j).subject == tmp1.object)) {
                                tripleset.getItem(j).isDelete = true;
                            }
                        }
                    }
                }

            }
        }
        for (int i = tripleset.length - 1; i >= 0; i--) {
            TripleType tmp1 = tripleset.getItem(i);
            if (!tmp1.isDelete) {
                if (tmp1.subject.classType.compareTo(Constants.IE) == 0) {
                    for (int j = i - 1; j >= 0; j--) {
                        if ((tripleset.getItem(j).object == tripleset.getItem(i).object) & (!tripleset.getItem(j).isDelete)) {
                            // object of triple j == object of triple i (1
                            // object has 2 relation)
                            if (tripleset.getItem(j).subject.classType.compareTo(Constants.UE) == 0) {
                                for (int k = tripleset.length - 1; k >= 0; k--) {
                                    if (k != j & k != i & !tripleset.getItem(k).isDelete & ((tmp1.subject == tripleset.getItem(k).object) || (tmp1.subject == tripleset.getItem(k).subject))) {
                                        // Case subject is the immediate node,
                                        // so it could be remove
                                        tmp1.isDelete = true;
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                if (tmp1.subject.classType.compareTo(Constants.UE) == 0) {
                    for (int j = i - 1; j >= 0; j--) {
                        // object of triple j == object of triple i (1 object
                        // has 2 relation)
                        if ((tripleset.getItem(j).object == tripleset.getItem(i).object) & (!tripleset.getItem(j).isDelete)) {
                            for (int k = tripleset.length - 1; k >= 0; k--) {
                                if (k != j & k != i & !tripleset.getItem(k).isDelete & ((tripleset.getItem(j).subject == tripleset.getItem(k).object) || (tripleset.getItem(j).subject == tripleset.getItem(k).subject))) {
                                    // Case subject is the immediate node, so it
                                    // could be remove
                                    tripleset.getItem(j).isDelete = true;
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
