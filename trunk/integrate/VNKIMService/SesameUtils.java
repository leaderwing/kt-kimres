//package org.me.VNKIMService;
//
//import java.io.*;
//import java.util.*;
//
//import org.openrdf.model.*;
//import org.openrdf.sesame.*;
//import org.openrdf.sesame.config.*;
//import org.openrdf.sesame.constants.*;
//import org.openrdf.sesame.query.*;
//import org.openrdf.sesame.repository.*;
//import org.openrdf.sesame.repository.remote.HTTPService;
//import javax.swing.JOptionPane;
//import vnkim.fuzzyMatching.remoteRepository.*;
//
//public class SesameUtils {
//  public static String NAMESPACE_URI =
//      "http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#";
//  public static String Alias_RelationName = "có_tên";
////  private static String NAMESPACE_URI = "http://www.ontotext.com/kim/kimo.rdfs#";
////  private static String Alias_RelationName = "hasAlias";
//
//  public static SesameRepository repository = null;
//
//  public SesameUtils() {}
//
//  /**
//   * Ham dung de login va Sesame
//   *
//   * @return HTTPService
//   */
//  public static HTTPService LoginSesame() throws Exception {
//    String Server = Configuration.ServerConnection.getKBServer();
//    if (Server == null || Server.equals("")) {
//      throw new Exception("URL of server is not provided!");
//    }
//    // Get service
//    java.net.URL sesameServerURL = new java.net.URL(Server);
//    HTTPService service = Sesame.getService(
//        sesameServerURL);
//
//    service.login(Configuration.ServerConnection.getUsername(),
//                  Configuration.ServerConnection.getPassword());
//    return service;
//  }
//  public static SesameRepository GetRepository(boolean force2CreateNewFlag) {
//    if (force2CreateNewFlag){
//      repository = null;
//    }
//    return GetRepository();
//  }
//  public static SesameRepository GetRepository() {
//    try {
//      if (repository != null)
//        return repository;
//      else {
//        HTTPService service = LoginSesame();
//        String RepositoryID = Configuration.ServerConnection.getRepositoryID();
//        if (RepositoryID == null || RepositoryID.equals("")) {
//          throw new Exception("RepositoryID is not provided!");
//        }
//        repository = service.getRepository(RepositoryID);
//        return repository;
//      }
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (UnknownRepositoryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getInstancesBeginWith(String resource,
//      String begin, int limit, int offset) {
//    QueryResultsTable queryResult = null;
//
//    try {
//      String querySentence =
//          "select distinct y, z, x from {x} rdf:type {<" +
//          resource +
//          ">} ; rdfs:label {y}, {x} serql:directType {} rdfs:label {z}  where (y like \"" +
//          begin.toLowerCase() +
//          "*\") or (y like \"" + begin.toUpperCase() + "*\") limit " +
//          limit + " offset " +
//          offset + " using namespace vnkimo_rdfs = <" + NAMESPACE_URI +
//          ">";
//      SesameRepository instanceRep = GetRepository();
//      queryResult = instanceRep.performTableQuery(QueryLanguage.SERQL,
//                                                  querySentence);
//      return queryResult;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//
//  }
//
//  /**
//   * Ham dung de lay tat cac cac lop trong repository, cac lop nay co
//   * namespace khac voi dinh nghia co san trong sesame, hay cac namespace
//   * co www.w3.org
//   *
//   * @return QueryResultsTable
//   */
//
//  public static QueryResultsTable getAllEntityClasses() {
//     // Bo di phan Ngu_lieu
//      return getSubClasses("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#Th\u1EF1c_th\u1EC3");
///*
//    //Lay luon phan Ngu_lieu
//    String serql = "select distinct x from {x} rdf:type {rdfs:Class} where not x like \"*www.w3.org*\"";
//    return runSeRQLStm(serql);
////*/
// }
//
//  /**
//   * Ham nay dung de lay lop cha truc tiep cua mot lop dua vao
//   * gia tri uri co san
//   *
//   * @param value Value
//   * @return QueryResultsTable
//   */
//  public static QueryResultsTable getDirectSuperClasses(String value) {
//    QueryResultsTable queryResult = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence = "select distinct y from {<" + value +
//          ">} serql:directSubClassOf {y} ";
//      queryResult = myRep.performTableQuery(QueryLanguage.SERQL,
//                                            querySentence);
//
//      return queryResult;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getSubClasses(String value) {
//    QueryResultsTable queryResult = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence =
//          "select distinct y from {y} rdfs:subClassOf {<" + value + ">} ";
//      queryResult = myRep.performTableQuery(QueryLanguage.SERQL, querySentence);
//
//      return queryResult;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getDirectSubClasses(String value) {
//    QueryResultsTable queryResult = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      //System.out.println("Rep Id la: "+resourceBundle.getString("repID"));
//      String querySentence =
//          "select distinct x from {x} serql:directSubClassOf {<" +
//          value + ">} ";
//      queryResult = myRep.performTableQuery(QueryLanguage.SERQL,
//                                            querySentence);
//
//      return queryResult;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  /**
//   * Ham dung de lay tat cac cac thuoc tinh trong kho luu tru cua Sesame
//   *
//   * @return QueryResultsTable
//   */
//
//  public static QueryResultsTable getAllProperties() {
//    QueryResultsTable queryResult = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence = "select distinct x from {x} rdf:type {rdf:Property}, {x} rdfs:range {y} where ( not y like \"*string\" and not y like \"*int\" and not y like \"*float\" and not y like \"*Literal\") and not x like \"*www.w3.org*\"";
//      queryResult = myRep.performTableQuery(QueryLanguage.SERQL,
//                                            querySentence);
//      return queryResult;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  /**
//   * Ham dung de lay tat ca cac thuoc tinh cha truc tiep cua mot thuoc tinh
//   *
//   * @param value String
//   * @return QueryResultsTable
//   */
//  public static QueryResultsTable getDirectSuperProperties(String value) {
//    QueryResultsTable queryResults = null;
//    try {
//      String querySentence = "select distinct x from {<" + value +
//          ">} serql:directSubPropertyOf {x} ";
//      SesameRepository myRep = GetRepository();
//
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL,
//                                             querySentence);
//
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  // Dat has modified this method!
//  public static QueryResultsTable getDomainOf(String p) {
//    String domainQuery = "select distinct y from {<" + p +
//        ">} rdfs:domain {y} ";
//    try {
//      SesameRepository myRep = GetRepository();
//      QueryResultsTable resultTable = myRep.performTableQuery(
//          QueryLanguage.SERQL, domainQuery);
//      return resultTable;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getRangeOf(String p) {
//
//    String domainQuery = "select distinct y from {<" + p +
//        ">} rdfs:range {y} ";
//
//    try {
//      SesameRepository myRep = GetRepository();
//      QueryResultsTable resultTable = myRep.performTableQuery(
//          QueryLanguage.SERQL, domainQuery);
//      return resultTable;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException e) {
//      e.printStackTrace();
//      return null;
//    }
//    catch (AccessDeniedException e) {
//      e.printStackTrace();
//      return null;
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//
//  public static String getCommentOf(String uri) {
//    Collection result = new ArrayList();
//    String domainQuery = "select distinct y from {<" + uri +
//        ">} rdfs:comment {y} ";
//
//    try {
//      SesameRepository myRep = GetRepository();
//      QueryResultsTable resultTable = myRep.performTableQuery(
//          QueryLanguage.SERQL, domainQuery);
//      for (int r = 0; r < resultTable.getRowCount(); r++)
//        for (int col = 0; col < resultTable.getColumnCount(); col++) {
//          Value v;
//          v = resultTable.getValue(r, col);
//          result.add(v.toString());
//        }
//    }
//    catch (MalformedQueryException e) {
//      e.printStackTrace();
//    }
//    catch (AccessDeniedException e) {
//      e.printStackTrace();
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//    }
//    catch (QueryEvaluationException ex) {
//    }
//
//    return result.toString();
//  }
//
//  public static QueryResultsTable getAllParentTypeOf(String childURI) {
//    String allParentQuery = "select distinct y from {<" + childURI +
//        ">} rdfs:subClassOf {y} where not y like \"*www.w3.org*\"";
//
//    try {
//      SesameRepository myRep = GetRepository();
//      QueryResultsTable resultTable = myRep.performTableQuery(
//          QueryLanguage.SERQL, allParentQuery);
//      return resultTable;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException e) {
//      e.printStackTrace();
//      return null;
//    }
//    catch (AccessDeniedException e) {
//      e.printStackTrace();
//      return null;
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getPropertiesOfConcept(String uri) {
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence =
//          "select distinct x, y from {x} rdf:type {rdf:Property}, {x} rdfs:domain {<" +
//          uri + ">},{x} rdfs:range {y} where (y like \"*string\" or y like \"*int\" or y like \"*float\" or y like \"*Literal\")and (not x like \"*www.w3.org*\" and not x like \"*node*\")";
//      //"select x from {x} <rdf:type> {<rdfs:Class>} where not x like \"*www.w3.org*\"";
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL,
//                                             querySentence);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getDomainRelationOfConcept(String uri) {
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence =
//          "select distinct x from {x} rdf:type {rdf:Property}, {x} rdfs:domain {<" +
//          uri +
//          ">}where (not x like \"*www.w3.org*\" and not x like \"*node*\")";
//      //"select x from {x} <rdf:type> {<rdfs:Class>} where not x like \"*www.w3.org*\"";
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL,
//                                             querySentence);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  public static QueryResultsTable getRangeRelationOfConcept(String uri) {
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence =
//          "select distinct x from {x} rdf:type {rdf:Property}, {x} rdfs:range {<" +
//          uri +
//          ">} where not x like \"*www.w3.org*\" and not x like \"*node*\"";
//      //"select x from {x} <rdf:type> {<rdfs:Class>} where not x like \"*www.w3.org*\"";
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL,
//                                             querySentence);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//    public static QueryResultsTable runSeRQLStm(String query) {
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL, query);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      return null;
//    }
//    catch (IOException ex) {
//      return null;
//    }
//  }
//
//    public static QueryResultsTable getClassformLexical(String lexical) {
//    String query = "select distinct y \n from {x} serql:directType {y},{y} rdfs:subClassOf {vnkimo_rdfs:Ngữ_liệu_Đối_tượng},{x} rdfs:label {z} \nwhere z like \""+lexical+"\" \nusing namespace  vnkimo_rdfs = <http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#>";
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL, query);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      return null;
//    }
//    catch (IOException ex) {
//      return null;
//    }
//  }
//
//    public static QueryResultsTable getallLexical() {
//    String query = "select distinct z \n from {x} rdf:type {y},{y} rdfs:subClassOf {vnkimo_rdfs:Ngữ_liệu_Đối_tượng},{x} rdfs:label {z} \nwhere not y like \"*Tên_người*\" \nusing namespace  vnkimo_rdfs = <http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#>";
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL, query);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      return null;
//    }
//    catch (IOException ex) {
//      return null;
//    }
//  }
//
//    public static String getClasslex(String lexical){
//        String result="";
//        QueryResultsTable resulttable=null;
//        resulttable = SesameUtils.getClassformLexical(lexical);
//        if (resulttable!=null)
////            for (int i=0; i<resulttable.getRowCount();i++)
//                result+= resulttable.getValue(0, 0).toString();
//        result=result.replaceAll("Ngữ_liệu_", "");
//        return result;
//    }
//
//    public static String getAllLex(){
//        String result="";
//        QueryResultsTable resulttable=null;
//        resulttable = SesameUtils.getallLexical();
//        if (resulttable!=null)
//            for (int i=0; i<resulttable.getRowCount();i++)
//                result+= resulttable.getValue(i, 0).toString()+"\n";
//        return result;
//    }
//
//    public static QueryResultsTable getAllAlias(String conceptURI) {
//    QueryResultsTable queryResults = null;
//    try {
//      SesameRepository myRep = GetRepository();
//      String querySentence = "select y from {x} rdf:type {<" +
//          conceptURI +
//          ">}, {x} <vnkimo_rdfs:c�_t�n> {} rdfs:label {y} using namespace vnkimo_rdfs = <http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#>";
//      queryResults = myRep.performTableQuery(QueryLanguage.SERQL,
//                                             querySentence);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return null;
//    }
//  }
//
//  /**
//   * Dat has added this method
//   */
//  public static int GetNoConceptInstances(String ontologyTypeStr) {
//    String query = "select distinct x" +
//        " from {x} rdf:type {<" + ontologyTypeStr + ">}";
//    try {
//      SesameRepository myRep = GetRepository();
//      QueryResultsTable resultTable = myRep.performTableQuery(
//          QueryLanguage.SERQL, query);
//      return resultTable.getRowCount();
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//  } // end of get no concept instances
//
//  public static int GetNoRelationInstances(String URIRelationStr) {
//    String query = "select *" +
//        " from {x} <" + URIRelationStr + "> {y}";
//    try {
//      SesameRepository myRep = GetRepository();
//      QueryResultsTable resultTable = myRep.performTableQuery(
//          QueryLanguage.SERQL, query);
//      return resultTable.getRowCount();
//    }
//    catch (AccessDeniedException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//    catch (QueryEvaluationException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//    catch (MalformedQueryException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//      return -1;
//    }
//  } // end of get no concept instances
//
//  /**
//   * runFuzzyMatching
//   *
//   * @param queryCGStr String
//   * @return QueryResultsTable
//   */
//  public static QueryResultsTable runFuzzyMatching(String queryCGStr) {
//    QueryResultsTable queryResults = null;
//    try {
//      FuzzyMatchingHTTPRepository myRep =
//          (FuzzyMatchingHTTPRepository) GetRepository();
//      queryResults = myRep.performFuzzyMatchingQuery(queryCGStr);
//      return queryResults;
//    }
//    catch (AccessDeniedException ex) {
//      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
//      return null;
//    }
//    catch (QueryEvaluationException ex) {
//      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
//      return null;
//    }
//    catch (MalformedQueryException ex) {
//      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
//      return null;
//    }
//    catch (IOException ex) {
//      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
//      return null;
//    }
//  }
//}
