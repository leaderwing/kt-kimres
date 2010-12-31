package org.me.VNKIMService;

import java.util.*;

public class SeRQLMapping {

   public static HashMap nameVarHashMap;
  public static int selectVarIndex=0;
  public static int noCareVarIndex=0;
  public static int instanceVarIndex=0;
  public static int propertyVarIndex=0;

  public static String fromClause;
  public static String uri = "http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#";
  public static String uriIE = "http://www.dit.hcmut.edu.vn/vnkim/vnkimkb.rdf#";
  public static String selectClause;
  public static String whereClause;

  public SeRQLMapping() {
    selectVarIndex=0;
    noCareVarIndex=0;
    instanceVarIndex=0;
    propertyVarIndex=0;
  }
  public HashMap GetNameVarHashMap() {
       return this.nameVarHashMap;
   }

  public static String getSeRQLQuery(QueryBuffer buffer) {
    selectVarIndex=0;
    noCareVarIndex=0;
    instanceVarIndex=0;
    propertyVarIndex=0;
    createSerqlStatement(buffer);
    if (selectClause != null) {
      if (selectClause.equalsIgnoreCase("select ")) {
        return null;
      }
      else {
        if (whereClause.equalsIgnoreCase("\nwhere ")) {
          return selectClause + fromClause;
        }
        else {
          return selectClause + fromClause + whereClause;
        }
      }
    }
    return null;
  }

  public String delpre(String entity){
      String result = entity.replaceAll("UE_", "");
      result = result.replaceAll("IE_", "");
      return result;
  }
  public static void createSerqlStatement(QueryBuffer buffer) {
    selectClause = "select ";
    fromClause = "\nfrom ";
    whereClause = "\nwhere ";

    boolean[] check = new boolean[buffer.length];
    for(int i=0; i<buffer.length; i++) check[i]=false;
    for(int i=0; i<buffer.length; i++) {

        ItemType tmp = buffer.getItem(i);
        if (tmp.delete) continue;
        String className = tmp.className;
        if (className.compareTo("RW") == 0) {

            if (tmp.relation.contains(Constants.GREATER)
                || tmp.relation.contains(Constants.SMALLER)) {

                continue; //Ignore because GREATER / SMALLER is not supposed to be
                          //in the "from" clause but in the "where" clause
            }

            //Duy?t qua các concept c?a RW ?ang xét
            int[] domain = {tmp.subindex, tmp.objindex};
            for (int j = 0; j < domain.length; j++) {

                ItemType concept = buffer.getItem(domain[j]);

                if (!check[domain[j]]) {

                    if (concept.quantifier.equals("?")) {
                        int tmpInt = ++selectVarIndex;
                        buffer.getItem(domain[j]).variable = "x" + tmpInt;
                        if (concept.className
                                   .compareToIgnoreCase("String") != 0
                                && concept.classType
                                   .compareToIgnoreCase("Property") != 0) {


                            addVarToSelectClause("x" + tmpInt + ",z" + tmpInt);
                            addStmToFromClause("{x" + tmpInt + "} rdfs:label {z" + tmpInt + "}");
                            addStmToFromClause("{x" + tmpInt + "} rdf:type {<" +
                                    ENSearch.getNS(concept.className.replaceAll("UE_", ""))
                                    + concept.className.replaceAll("UE_", "") + ">}");

                        } else {
                            addVarToSelectClause("x" + tmpInt);
                        }
                    } else {
                        if (concept.quantifier.equals("*")){
                            int tmpInt = ++noCareVarIndex;
                            buffer.getItem(domain[j]).variable= "y" + tmpInt;

                            //kiem tra, neu la thuoc tinh thi khong can them phan rdf:type
                            if ( concept.className
                                    .compareToIgnoreCase("String") != 0
                                 && concept.classType
                                    .compareToIgnoreCase("Property") != 0 ) {

                                addStmToFromClause("{y"+tmpInt+"} rdf:type {<"
                                        + ENSearch.getNS(concept.className
                                            .replaceAll("UE_", ""))
                                        + concept.className.replaceAll("UE_", "")
                                        + ">}" );
                            }

                        }
                        else {

                            //kiem tra, neu la so sanh nhat thi them menh de where
                            if (concept.classType.compareToIgnoreCase("Property") == 0) {

                                int tmpInt = ++noCareVarIndex;
                                buffer.getItem(domain[j]).variable = "y" + tmpInt;
                                addStmToWhereClause("y" + tmpInt + " like \""
                                                    + buffer.getItem(domain[j])
                                                            .quantifier + "\"");

                            } else {

                                buffer.getItem(domain[j]).variable= "<"
                                            + buffer.getItem(domain[j]).ID + ">";

                            }
                        }
                    }
                    check[domain[j]] = true;
               }
           }
           if (tmp.direction==1) {
                addStmToFromClause
                    ("{" + buffer.getItem(tmp.subindex).variable + "}"
                     + " <"+ENSearch.getNS(tmp.relation)+tmp.relation+"> " + "{"
                     + buffer.getItem(tmp.objindex).variable + "}");
           } else {
                addStmToFromClause
                    ("{" + buffer.getItem(tmp.objindex).variable + "}"
                     +" <"+ENSearch.getNS(tmp.relation)+tmp.relation+"> "+ "{"
                     + buffer.getItem(tmp.subindex).variable + "}");
           }
        }
     }
  }

  public void processPropertyNames(Hashtable propertyNames, String newVar) {
    Enumeration pNames = propertyNames.keys();
    while (pNames.hasMoreElements()) {
      String propertyURI = (String) pNames.nextElement();
      String propertyVar = "p" +
          (++propertyVarIndex);
      propertyNames.put(propertyURI, propertyVar);
      if ( (newVar.charAt(0) != 'x') &&
          (newVar.charAt(0) != 'y') && (newVar.charAt(0) != 't')) {
        String tmpVar = "<" + newVar + ">";

        addStmToFromClause("{" + tmpVar + "} <" + propertyURI +
                          "> {" + propertyVar + "}");
      }
      else {
        addStmToFromClause("{" + newVar + "} <" + propertyURI +
                          "> {" + propertyVar + "}");
      }
    }
  }


  public static void addVarToSelectClause(String var) {
    if (selectClause.equals("select ")) {
      selectClause = selectClause + var;
    }
    else { // if select already has at least one variable
      selectClause = selectClause + ", " + var;
    }
  }

public static String addVarToExternalSelectClause(String var, String clause) {
    if (clause.equals("select ")) {
      clause = clause + var;
    }
    else { // if select already has at least one variable
      clause = clause + ", " + var;
    }
    return clause;
  }

  public static void addStmToFromClause(String stm) {
    if (fromClause.equals("\nfrom ")) {
      fromClause = fromClause + stm;
    }
    else { // if select already has at least one variable
      fromClause = fromClause + ",\n\t" + stm;
    }
  }

  public static String addStmToExternalFromClause(String stm, String clause) {
    if (clause.equals("\nfrom ")) {
      clause = clause + stm;
    }
    else { // if select already has at least one variable
      clause = clause + ",\n\t" + stm;
    }
    return clause;
  }

  public static void addStmToWhereClause(String stm) {
    if (whereClause.equals("\nwhere ")) {

      whereClause = whereClause + stm;
    }
    else {
      whereClause = whereClause + "\n\tAND (" + stm + ")";
    }
  }

  public static String addStmToExternalWhereClause(String stm, String clause) {
    if (clause.equals("\nwhere ")) {
      clause = clause + stm;
    }
    else {
      clause = clause + "\n\tAND (" + stm + ")";
    }
    return clause;
  }
}
