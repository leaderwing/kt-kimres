package org.me.VNKIMService;

//import gate.util.GateException;
//import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileReader;
//import org.openrdf.sesame.query.*;
import javax.servlet.*;

import java.util.List;

import java.util.ArrayList;


public class QuerytoCG {

    public static int displaytype = 0;
    
    enum ElementType {

        Entity, Relation
    }

//    public static String ProcessQueryList(ServletContext ctx) throws Exception {
//        String serverpath = "C:/apache-tomcat-5.5.20/webapps/QuerytoCG/";
//        if (ctx != null) {
//            serverpath = ctx.getRealPath("/") + "/";
//        }
//
//        String path = serverpath + "testquestion.txt";
//        BufferedReader in = new BufferedReader(new FileReader(path));
//        String output = "<table border=\"0\" cellpadding=\"10\">";
//        String query;
//
//        QueryBuffer.querycount = 1;
//        while ((query = in.readLine()) != null) {
//            if (query.compareTo("") == 0) {
//                continue;
//            }
//            String cg = ProcessQuery(null, query);
//            if (displaytype == 1) // display valid only
//            {
//                if (ProcessingQuery.IsValid) {
//                    //output += "<tr><td><font color=\"blue\"><b>Query " + QueryBuffer.querycount + "</b></font></td><td>&nbsp;</td></tr>";
//                    output += "<tr><td colspan=\"2\">" + cg + "</td></tr>";
//                    //output += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
//                    QueryBuffer.querycount++;
//                }
//            } else if (displaytype == 2) // display invalid only
//            {
//                if (ProcessingQuery.IsValid == false) {
//                    //output += "<tr><td><font color=\"blue\"><b>Query " + QueryBuffer.querycount + "</b></font></td><td>&nbsp;</td></tr>";
//                    output += "<tr><td colspan=\"2\">" + cg + "</td></tr>";
//                    //output += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
//                    QueryBuffer.querycount++;
//                }
//            } else {
//                //output += "<tr><td><font color=\"blue\"><b>Query " + QueryBuffer.querycount + "</b></font></td><td>&nbsp;</td></tr>";
//                output += "<tr><td colspan=\"2\">" + cg + "</td></tr>";
//                //output += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
//                QueryBuffer.querycount++;
//            }
//
//        }
//        output += "</table>";
//        return output;
//    }

    public static void RemoveTriple(QueryTriple tripleset, QueryBuffer buffer)
            throws Exception {

        TripleType trip;
        ItemType Ei = null;
        ItemType Eiplus1 = null;
        ItemType relationBtw;

        for (int i = tripleset.length - 1; i >= 0; i--) {

            trip = tripleset.getItem(i);

            if (!trip.isDelete) {

                // Get string between subject and object
                String st = "ABC";
                if (trip.subject.end < trip.object.start) {
                    st = ProcessingXML.GetStringBetween2Item(trip.subject,
                            trip.object, buffer);
                    Ei = trip.subject;
                    Eiplus1 = trip.object;
                }
                if (trip.object.end < trip.subject.start) {
                    st = ProcessingXML.GetStringBetween2Item(trip.object,
                            trip.subject, buffer);
                    Ei = trip.object;
                    Eiplus1 = trip.subject;
                }
                if ((st.trim().compareToIgnoreCase("and") == 0) || (st.trim().compareToIgnoreCase("or") == 0)) {
                    trip.isDelete = true;
                }// Case "and", "or"

                if ((st.trim().compareToIgnoreCase("") == 0)) {
                    //Implement: Neu Ei va Ei+1 lien ke nhau, hoac cach nhau bang “ ’s ”
                    //quan he cua Ei voi cac thuc the truoc no se bi xoa bo
                    //*Chu y: tai day da xet truong hop "fakeRW", vi "fakeRW"
                    //chi duoc them vao khi st == ""
                    for (int j = 0; j < i; j++) {
                        if (i != j) {
                            if ((tripleset.getItem(j).subject == Ei) || (tripleset.getItem(j).object == Ei)) {
                                tripleset.getItem(j).isDelete = true;
                            }
                        }
                    }
                }

                // Get relation between subject and object
                relationBtw = trip.rel;

                if (relationBtw != null) {
                    if (relationBtw.value.trim().compareToIgnoreCase("'s") == 0) {
                       // || relationBtw.value.trim().compareToIgnoreCase("fakeRW") == 0) {
                        //Implement: Neu Ei va Ei+1 lien ke nhau, hoac cach nhau bang “ ’s”
                        //quan he cua Ei voi cac thuc the truoc no se bi xoa bo
                        for (int j = 0; j < i; j++) {
                            if (i != j) {
                                if ((tripleset.getItem(j).subject == Ei) || (tripleset.getItem(j).object == Ei)) {
                                    tripleset.getItem(j).isDelete = true;
                                }
                            }
                        }
                    }
                }
                if ((st.replace('"', ' ').trim().compareTo(",") == 0)) {
                    //Neu Ei và Ei+1 cach nhau boi "," thi Ei+1 chi quan he voi Ei
                    //cac moi quan he cua Ei+1 voi cac thuc the khac se bi xoa bo
                    for (int j = 0; j < i; j++) {
                        if (i != j) {
                            if ((tripleset.getItem(j).object == Eiplus1)
                                || (tripleset.getItem(j).subject == Eiplus1)) {
                                tripleset.getItem(j).isDelete = true;
                            }
                        }
                    }
                }

            }
        }

        //Neu 1 thuc the co quan he voi nhieu thuc the ?ung truoc no,
        //thi chi giu lai moi quan he voi thuc the khong xac ?inh ?ung truoc, gan no nhat.
        TripleType walker;
        for (int i = tripleset.length - 1; i>= 0; i--) {

            trip = tripleset.getItem(i);

            if (!trip.isDelete) {

                if (trip.subject.end < trip.object.start) {
                    Ei = trip.subject;
                    Eiplus1 = trip.object;
                }
                if (trip.object.end < trip.subject.start) {
                    Ei = trip.object;
                    Eiplus1 = trip.subject;
                }

                //Neu co UE dung truoc, tien hanh xoa quan he voi nhung thuc the dung truoc
                if (Eiplus1.preNearestUE != null) {

                    if (!(Eiplus1.start - Ei.end == 1) //ko lien ke
                        && !(Eiplus1.preNearestUE == Ei) //ko phai quan he can giu lai
                       ){

                        relationBtw = trip.rel;
                        if ( !( (relationBtw != null)
                                && (relationBtw.value.trim().compareToIgnoreCase("'s") == 0)
                              )
                           ) { //ko quan he so huu)

                            trip.isDelete = true;
                        }

                    }

                    for (int j = i - 1; j >= 0; j--) {

                        walker =  tripleset.getItem(j);
                        if (!walker.isDelete) {

                            if ((Eiplus1 == walker.subject
                                    && walker.object != Eiplus1.preNearestUE)
                                || (Eiplus1 == walker.object
                                    && walker.subject != Eiplus1.preNearestUE)) {

                                relationBtw = walker.rel;
                                if ( !( (walker.subject.start - walker.object.end == 1 || walker.subject.start - walker.object.end == -1)
                                        ||
                                        ((relationBtw != null) && (relationBtw.value.trim().compareToIgnoreCase("'s") == 0) )
                                      )
                                   ) {

                                    walker.isDelete = true;
                                }

                            }

                        }

                    }

                }
            }

        }

//        for (int i = tripleset.length - 1; i >= 0; i--) {
//
//            trip = tripleset.getItem(i);
//
//            if (!trip.isDelete) {
//
//                if (trip.subject.classType.compareTo(Constants.IE) == 0) {
//                    for (int j = i - 1; j >= 0; j--) {
//                        if ((tripleset.getItem(j).object == tripleset.getItem(i).object) & (!tripleset.getItem(j).isDelete)) {
//                            // object of triple j == object of triple i (1
//                            // object has 2 relation)
//                            if (tripleset.getItem(j).subject.classType.compareTo(Constants.UE) == 0) {
//                                for (int k = tripleset.length - 1; k >= 0; k--) {
//                                    if (k != j
//                                        & k != i
//                                        & !tripleset.getItem(k).isDelete
//                                        & ((trip.subject == tripleset.getItem(k).object)
//                                            || (trip.subject == tripleset.getItem(k).subject))) {
//                                        // Case subject is the immediate node,
//                                        // so it could be remove
//                                        trip.isDelete = true;
//                                        break;
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//
//                if (trip.subject.classType.compareTo(Constants.UE) == 0) {
//                    for (int j = i - 1; j >= 0; j--) {
//                        if ((tripleset.getItem(j).object == tripleset.getItem(i).object) & (!tripleset.getItem(j).isDelete)) {
//                            // object of triple j == object of triple i (1 object
//                            // has 2 relation)
//                            for (int k = tripleset.length - 1; k >= 0; k--) {
//                                if (k != j
//                                    & k != i
//                                    & !tripleset.getItem(k).isDelete
//                                    & ((tripleset.getItem(j).subject == tripleset.getItem(k).object)
//                                        || (tripleset.getItem(j).subject == tripleset.getItem(k).subject))) {
//                                    // Case subject is the immediate node, so it
//                                    // could be remove
//                                    tripleset.getItem(j).isDelete = true;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
//        }

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
                } else if (tmp.rel.isIdentifiedFromSQTA()) {
                    rel.setIdentifiedFromSQTA(tmp.rel.isIdentifiedFromSQTA());
                    buffer.setContainSuperlativeQTA(true); //for generating SeRQL
                } else if (tmp.relationName.contains(Constants.GREATER)
                            || tmp.relationName.contains(Constants.SMALLER)) {
                    buffer.setContainCQTA(true);
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
   
    /*D	public static String ProcessQueryList2(ServletContext ctx, String filename, int type)  throws  Exception
    {
    String serverpath = "C:\\apache-tomcat-5.5.20\\webapps\\QuerytoCG\\";
    if ( ctx != null)
    serverpath = ctx.getRealPath("/") + "/";

    String path = serverpath + filename;

    BufferedReader in = new BufferedReader(new FileReader(path));
    String output = "<table border=\"0\" cellpadding=\"10\">";
    String query;

    displaytype=type;
    QueryBuffer.querycount = 1;
    while ( (query = in.readLine()) != null)
    {
    if (query.compareTo("")==0)
    continue;
    String cg = ProcessQuery2(ctx, query, QueryBuffer.querycount);
    if (displaytype == 1) // display valid only
    {
    if (ProcessingQuery.IsValid)
    {
    //output += "<tr><td><font color=\"blue\">" + QueryBuffer.querycount + ". </font></td></tr>";
    output += "<tr><td colspan=\"2\">" + cg + "</td></tr>";
    //output += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
    QueryBuffer.querycount++;
    }
    }
    else if (displaytype == 2) // display invalid only
    {
    if (ProcessingQuery.IsValid==false)
    {
    //output += "<tr><td><font color=\"blue\">" + QueryBuffer.querycount + ". </font></td></tr>";
    output += "<tr><td colspan=\"2\">" + cg + "</td></tr>";
    //output += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
    QueryBuffer.querycount++;
    }
    }
    else
    {
    //output += "<tr><td><font color=\"blue\">" + QueryBuffer.querycount + ". </font></td></tr>";
    output += "<tr><td colspan=\"2\">" + cg + "</td></tr>";
    //output += "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>";
    QueryBuffer.querycount++;
    }

    }
    output += "</table>";
    return output;
    }
    public static String ProcessQueryStringorFile(ServletContext ctx, String st) throws  ServletException, Exception
    {
    //if(true)return ProcessingXML.ReadFile("D:/LuanVan/Workspace/QuerytoCG/WebContent/transformrules.xml");
    String[] lstst=st.trim().split(":");
    if(lstst[0].compareToIgnoreCase("file")!=0)
    {
    return ProcessQuery2(ctx, st,0);
    }
    else
    {
    int type=0;
    if(lstst.length>2)
    {
    if(lstst[2].compareToIgnoreCase("valid")==0) type=1;
    if(lstst[2].compareToIgnoreCase("invalid")==0) type=2;
    }
    return ProcessQueryList2(ctx,lstst[1],type);
    }
    D*/

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

    public static String ProcessQuery2(ServletContext ctx, String query, int count) throws ServletException, Exception {
        String output = "<table border=\"0\" cellpadding=\"5\">";

        QueryBuffer buffer = new QueryBuffer();
        buffer.query = query;
        ConnectServers connect = new ConnectServers();
        //pre recognize entity

        //output query
//		output += "<tr><td><font color=\"red\"><b>Query "+":</b></font></td><td> <i>" + query + "</i></td></tr>";
        buffer.query = convert2VN(query);
//		output += "<tr><td><font color=\"red\"><b>Query "+":</b></font></td><td> <i>" + buffer.query + "</i></td></tr>";
        String IEanno = connect.performVNKIManno(buffer.query, buffer);
//                ConnectServers.OutputSeRql(IEanno, "C:\\anno.txt");
//        GateNamedEntity.GetEntityandRelationWord(ctx, buffer.query, buffer);

        String nhapnhang = connect.isAmbiguous(buffer.query, IEanno);
//                if (!nhapnhang.equalsIgnoreCase("")) return nhapnhang;

        output += "<tr><td><font color=\"red\"><b>" + "</b></font></td><td> <i>" + nhapnhang + "</i></td></tr>";

        ProcessingXML.FindClassofAgent(buffer);

        ProcessingXML.CombineEntitys(buffer);

//		ProcessingXML.AddRealtion(buffer);

//		output += "<tr><td><font color=\"red\"><b>List "+":</b></font></td><td> <i>" + "</i></td></tr>";
        //GATE annotated
//		AnnieER.getEntities(ctx, buffer.query, buffer);
//		output += "<tr><td><font color=\"red\"><b>GATE:</b></font></td><td> " + QueryOutput.getHTMLAnnotatedQuery(buffer)+ "</td></tr>";

        //ProcessingQuery.PreRecognizeEntity(buffer);
        //ProcessingQuery.PreProcessQuery(buffer);

        ProcessingQuery.SpecifyQuantifier(buffer);

        output += buffer.length + "______";
        for (int i = 0; i < buffer.length; i++) {
            ItemType tmp = buffer.getItem(i);
            output += "</br>" + (i + 1) + ") " + tmp.value + "-" + tmp.className + "-" + tmp.classType + "-" + tmp.quantifier + "-*-delete: " + tmp.delete;
        }

        QueryTriple tripleset = ProcessingQuery.ProcessQuery2Triple(buffer);
//		output += "<tr><td><font color=\"red\"><b>Debug "+":</b></font></td><td> <i>" + ProcessingQuery.debug + "</i></td></tr>";

        RemoveTriple(tripleset, buffer);

        /*		output +="</br>"+tripleset.length+"______";
        for(int i=0;i<tripleset.length;i++)
        {
        TripleType tmp=tripleset.getItem(i);
        output +="</br>"+(i+1)+") "+tmp.subject.value+"*"+tmp.rel.value+"*"+tmp.object.value+"->"+tmp.relationName+"-*-delete: "+tmp.isDelete;
        }*/
//		ProcessingQuery.AddQuery2TripleForAdjective(tripleset, buffer);

        QueryBuffer bufertmp = MakeQueryBuffer(tripleset);

        boolean isvalid = false;
        for (int j = 0; j < bufertmp.length; j++) {
            if (bufertmp.getItem(j).delete == false) {
                isvalid = true;
                break;
            }
        }

        /*
        output += "</br>"+bufertmp.length+"_____";

        for(int j=0; j < bufertmp.length; j++)
        {
        ItemType tmp = bufertmp.getItem(j);
        output +="</br>";
        output += j+")" +tmp.value+"_R:"+tmp.row+"_C:"+tmp.col+"_SI:"+tmp.subindex+"_OI:"+tmp.objindex;
        }*/

        //output += "<tr><td><font color=\"red\"><b>GATE_ENTITY:</b></font></td><td> " + QueryOutput.getHTMLAnnotatedQuery(buffer) + "</td></tr>";
        bufertmp.query = buffer.query;
        ProcessingQuery.ReSpecifyQuantifier(bufertmp);
        String out = QueryOutput.generateJsCG(bufertmp);
        ProcessingQuery.IsValid = isvalid;
        if (isvalid) {
            String SeRql = SeRQLMapping.getSeRQLQuery(bufertmp);
            ConnectServers.OutputSeRql(SeRql, "C:/SeRql.txt");
//            QueryResultsTable resulttable = null;
//                        resulttable = SesameUtils.runSeRQLStm(SeRql);
            output += "<tr><td valign=\"center\"><font color=\"red\"><b></b></font></td><td><b>" + out + "</b></td></tr>";
//            if (resulttable != null) {
//                for (int i = 0; i < resulttable.getRowCount(); i++) {
//                    for (int j = 0; j < resulttable.getColumnCount(); j++) {
//                        output += "<tr><td valign=\"center\"><font color=\"red\"><b>Result Entities:</b></font></td><td><b>" + resulttable.getValue(i, j).toString() + "</b></td></tr>";
//                    }
//                }
//            }
//                        lucene = ConnectServers.performLucene(lucene);
        } else {
            output += "<tr><td><font color=\"black\"><b>CG:</b></font></td><td><b><font color=black>NO CG is generated !</font></b></td></tr>";
        //out=QueryOutput.generateJsCG2(buffer);
        //output += "<tr><td valign=\"center\"><font color=\"red\"><b>CG:</b></font></td><td><b>" + out + "</b></td></tr>";
        }
        /*ProcessingQuery.ProcessQuery2(buffer);
        if (ProcessingQuery.IsValidCG(buffer))
        {
        output += "<tr><td><font color=\"red\"><b>CG:</b></font></td><td><b>" + QueryOutput.getHTMLCG(buffer) + "</b></td></tr>";
        //output += "<tr><td valign=\"center\"><font color=\"red\"><b>CG:</b></font></td><td><b>" + QueryOutput.generateJsCG(buffer) + "</b></td></tr>";
        }
        else
        output += "<tr><td><font color=\"black\"><b>CG:</b></font></td><td><b><font color=black>NO CG is generated !</font></b></td></tr>";
         */

        //Process query
        //Checking grammar before processing
		/*if (GrammarChecking.CheckGrammar(buffer.getParsedQuery()))
        {
        ProcessingQuery.SpecifyQuantifier(buffer);
        ProcessingEntity.ProcessEntity(buffer);
        if (ProcessingQuery.IsValidTriples(buffer))
        {
        ProcessingQuery.ProcessQuery(buffer);
        if (ProcessingQuery.IsValidCG(buffer))
        {
        output += "<tr><td><font color=\"red\"><b>CG:</b></font></td><td><b>" + QueryOutput.getHTMLCG(buffer) + "</b></td></tr>";
        output += "<tr><td valign=\"center\"><font color=\"red\"><b>CG:</b></font></td><td><b>" + QueryOutput.generateJsCG(buffer) + "</b></td></tr>";
        }
        else
        output += "<tr><td><font color=\"black\"><b>CG:</b></font></td><td><b><font color=black>NO CG is generated !</font></b></td></tr>";
        }
        else
        output += "<tr><td><font color=\"black\"><b>CG:</b></font></td><td><b><font color=black>NO CG is generated !</font></b></td></tr>";

        }
        else
        {
        ProcessingQuery.IsValid = false;
        QueryBuffer.invalidquery++;
        output += "<tr><td><font color=\"red\"><b>CG:</b></font></td><td><b><font color=blue>INVALID QUERY (GRAMMAR)</font></b></td></tr>";
        }
        output += "<tr><td>"+buffer.length+"**";
        for(int j=0; j < buffer.length; j++)
        {
        ItemType tmp = buffer.getItem(j);
        output +="</br>";
        output += j+")" +tmp.value+"_R:"+tmp.row+"_C:"+tmp.col+"_SI:"+tmp.subindex+"_OI:"+tmp.objindex;
        }
        output += "</td><tr>";*/
        output += "</table>";

        return output;
    }

    public static synchronized String resultEntities(ServletContext ctx, 
                   String query, int count) throws ServletException, Exception {
        String results = "";
        QueryBuffer buffer = new QueryBuffer();
        String SeRQL = "";

        //DA2
//        buffer.query = query;

        //ALT
        query = convert2VN(query);
        buffer.query = query;

        GateNamedEntity.serverpath = ctx.getRealPath("/") + "/";
        ConnectServers connect = new ConnectServers();

        String IEanno = "";

        //DA2
//        IEanno += ENSearch.GetNamedEntity(buffer.query, buffer, "");
//        ENSearch.GetUERW(buffer.query, buffer);
        
        //ALT
        System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");
        IEanno += ENSearch.GetAnnotation(query, buffer, "");

        if (IEanno.startsWith("Ambiguous@")) return IEanno;
        //DA2
//        boolean simple = true;
//        for (int i = 0; i < buffer.length; i++) {
//            ItemType tmp = buffer.getItem(i);
//            if (tmp.classType.equalsIgnoreCase("RW")) {
//                simple = false;
//                break;
//            }
//        }

//                String nhapnhang = "Ambiguous@June:Woman,Man" ;//connect.isAmbiguous(buffer.query, IEanno);
//        if (true) return nhapnhang;

        //DA2
//        if (!nhapnhang.equalsIgnoreCase("") && (!simple)) {
//            return nhapnhang;
//        }
        
        //ALT
//        if (!nhapnhang.equalsIgnoreCase("")) {
//            return nhapnhang;
//        }

        //DA2
//        if (simple) {
//            String simplelist = "";
//            if (buffer.length > 0) {
//                ItemType tmp = buffer.getItem(0);
//                if (tmp.progreg.equalsIgnoreCase("G")) {
//                    if (tmp.classType.equalsIgnoreCase("UE")) {
//                        return simplelist += "class:" + tmp.className + ";";
//                    }
//                } else {
//                    simplelist = connect.getsimple(buffer.query, IEanno);
//                }
//            }
//            if (!simplelist.equalsIgnoreCase("")) {
//                simplelist = simplelist.substring(0, simplelist.length() - 1);
//            }
//            return simplelist;
//        }

        System.out.println("4. Split query to atomic query.");
        List<ItemType> itemList = new ArrayList<ItemType>();
        
        if (buffer != null && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                ItemType t = buffer.buffer[i];
                itemList.add(t);
            }
        }
        
        List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
        splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

        System.out.println("5. Process atom queries...");

        boolean isvalid = false;
        boolean isCheckedTopRel = false;
        TopRelationType topRelation = null;

        for (int i = 0; i < atomicQueryList.size(); i++) {

            String atomQuery = atomicQueryList.get(i).getQuery().query;

            System.out.println("Process atom query: " + atomQuery);
            RelationType nextRel = atomicQueryList.get(i).getNextReal();
            QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
            atomBuffer.setRelToNextAtomQuery(nextRel);

            System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

            if (isCheckedTopRel == false) {

                if (identifyTopRel(atomBuffer) != null) {

                    topRelation = identifyTopRel(atomBuffer);
                    isCheckedTopRel = true;

                }

            }

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
            System.out.println("5.9 Mapping <entity, relation word, entity) to"
                                 + " <entity, relation type, entity>.");
            QueryTriple tripleAtomSet = ProcessingQuery.
                                                ProcessQuery2Triple(atomBuffer);

            // Remove redundancy triple
            System.out.println("5.11 Remove redundancy triple.");
            RemoveTriple(tripleAtomSet, atomBuffer);

            // Identify the relationship between Adjective and Entity
            System.out.println("5.12 Identify the relationship between "
                                                     + "Adjective and Entity.");

            System.out.println("5.12a Identify the relationship between "
                                        + "Quantitative Adjective and Entity.");
            ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.12b Identify the relationship between "
                                    + "Quanlitative Adjective and Entity.");
            ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

             if (topRelation != null && topRelation.name().equals(
                        TopRelationType.COUNT.name())) {
                    for (int j = 0; j < tripleAtomSet.length; j++) {
                        if (tripleAtomSet.getItem(j).relationName.equalsIgnoreCase("populationCount")) {
                            topRelation = null;
                            break;
                        }
                    }
                }

             // Get new QueryBuffer for purpose of generating CG
            System.out.println("5.13 Get new QueryBuffer for purpose of " +
                                                            "generating CG.");
            QueryBuffer buferAtomTmp = MakeQueryBuffer(tripleAtomSet);

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
                if (topRelation != null && topRelation.name()
                                        .equals(TopRelationType.COUNT.name())) {
                    // In case the query "how many" have only one entity.
                    buferAtomTmp.length = 1;
                    for (int v = 0; v < atomBuffer.length; v++) {
                        String type = atomBuffer.buffer[v].classType;
                        if (!atomBuffer.buffer[v].className.equals("UE_Agent")
                            && (type.equals(Constants.UE)
                                || type.equals(Constants.IE))) {
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

            System.out.println("5.15 Generate SeRQL for atom query");
            ProcessingQuery.IsValid = isvalid;
            if (isvalid) {

                String SeRQLAtom = SeRQLMapping.getSeRQLQuery(buferAtomTmp);

                //Add Where clause for Superlative QTA
                if (buferAtomTmp.isContainSuperlativeQTA()) {

                    SeRQLAtom = appendWhereClauseForSQTA(SeRQLAtom, buferAtomTmp, topRelation);

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

            }

        }

        if (isvalid) {

            System.out.println("SeRQL for the whole query is: " + SeRQL);

            results = ENSearch.runSeRQL(SeRQL);

            if (!results.equalsIgnoreCase("")) {
                results = results.substring(0, results.length() - 1);
            }
            if (topRelation != null && topRelation.name().equalsIgnoreCase(TopRelationType.COUNT.name())) {
                results = " :" + results.split(";").length;
            }
            if (topRelation != null && topRelation.name().equalsIgnoreCase(TopRelationType.AVERAGE.name())) {
                double temp = 0;
                int c = 0;
                String[] e = results.split("[\",:,;]");
                for (int i = 0; i < e.length; i++) {
                    if (!e[i].trim().isEmpty()) {
                        temp = temp + Double.parseDouble(e[i]);
                        c++;
                    }
                }
                temp = temp / c;
                results = " :" + temp;
            }
//            System.out.println("Execute SeRQL: " + SeRQL);
//            System.out.println("Result entities: " + results);
        }
        return results;
    }

    public static synchronized String resultAmbiguous(ServletContext ctx, String query, int count, String ambiguous) throws ServletException, Exception {
        String results = "";
        QueryBuffer buffer = new QueryBuffer();
        String SeRQL = "";

        //DA2
//        buffer.query = query;

        //ALT
        query = convert2VN(query);
        buffer.query = query;

        GateNamedEntity.serverpath = ctx.getRealPath("/") + "/";
        ConnectServers connect = new ConnectServers();

        String IEanno = "";

        //DA2
//        IEanno += ENSearch.GetNamedEntity(buffer.query, buffer, "");
//        ENSearch.GetUERW(buffer.query, buffer);

        //ALT
        System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");
        IEanno += ENSearch.GetAnnotation(query, buffer, ambiguous);


         System.out.println("4. Split query to atomic query.");
        List<ItemType> itemList = new ArrayList<ItemType>();

        if (buffer != null && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                ItemType t = buffer.buffer[i];
                itemList.add(t);
            }
        }

        List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
        splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

        System.out.println("5. Process atom queries...");

        boolean isvalid = false;
        boolean isCheckedTopRel = false;
        TopRelationType topRelation = null;

        for (int i = 0; i < atomicQueryList.size(); i++) {

            String atomQuery = atomicQueryList.get(i).getQuery().query;

            System.out.println("Process atom query: " + atomQuery);
            RelationType nextRel = atomicQueryList.get(i).getNextReal();
            QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
            atomBuffer.setRelToNextAtomQuery(nextRel);

            System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

            if (isCheckedTopRel == false) {

                if (identifyTopRel(atomBuffer) != null) {

                    topRelation = identifyTopRel(atomBuffer);
                    isCheckedTopRel = true;

                }

            }

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
            System.out.println("5.9 Mapping <entity, relation word, entity) to"
                                 + " <entity, relation type, entity>.");
            QueryTriple tripleAtomSet = ProcessingQuery.
                                                ProcessQuery2Triple(atomBuffer);

            // Remove redundancy triple
            System.out.println("5.11 Remove redundancy triple.");
            RemoveTriple(tripleAtomSet, atomBuffer);

            // Identify the relationship between Adjective and Entity
            System.out.println("5.12 Identify the relationship between "
                                                     + "Adjective and Entity.");

            System.out.println("5.12a Identify the relationship between "
                                        + "Quantitative Adjective and Entity.");
            ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.12b Identify the relationship between "
                                    + "Quanlitative Adjective and Entity.");
            ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            if (topRelation != null && topRelation.name().equals(
                    TopRelationType.COUNT.name())) {
                for (int j = 0; j < tripleAtomSet.length; j++) {
                    if (tripleAtomSet.getItem(j).relationName.equalsIgnoreCase("populationCount")) {
                        topRelation = null;
                        break;
                    }
                }
            }

             // Get new QueryBuffer for purpose of generating CG
            System.out.println("5.13 Get new QueryBuffer for purpose of " +
                                                            "generating CG.");
            QueryBuffer buferAtomTmp = MakeQueryBuffer(tripleAtomSet);

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
                if (topRelation != null && topRelation.name()
                                        .equals(TopRelationType.COUNT.name())) {
                    // In case the query "how many" have only one entity.
                    buferAtomTmp.length = 1;
                    for (int v = 0; v < atomBuffer.length; v++) {
                        String type = atomBuffer.buffer[v].classType;
                        if (!atomBuffer.buffer[v].className.equals("UE_Agent")
                            && (type.equals(Constants.UE)
                                || type.equals(Constants.IE))) {
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

            System.out.println("5.15 Generate SeRQL for atom query");
            ProcessingQuery.IsValid = isvalid;
            if (isvalid) {

                String SeRQLAtom = SeRQLMapping.getSeRQLQuery(buferAtomTmp);

                //Add Where clause for Superlative QTA
                if (buferAtomTmp.isContainSuperlativeQTA()) {

                    SeRQLAtom = appendWhereClauseForSQTA(SeRQLAtom, buferAtomTmp, topRelation);

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

            }

        }

        if (isvalid) {

            System.out.println("SeRQL for the whole query is: " + SeRQL);

            results = ENSearch.runSeRQL(SeRQL);

            if (!results.equalsIgnoreCase("")) {
                results = results.substring(0, results.length() - 1);
            }
            if (topRelation != null && topRelation.name().equalsIgnoreCase(TopRelationType.COUNT.name())) {
                results = " :" + results.split(";").length;
            }
            if (topRelation != null && topRelation.name().equalsIgnoreCase(TopRelationType.AVERAGE.name())) {
                double temp = 0;
                int c = 0;
                String[] e = results.split("[\",:,;]");
                for (int i = 0; i < e.length; i++) {
                    if (!e[i].trim().isEmpty()) {
                        temp = temp + Double.parseDouble(e[i]);
                        c++;
                    }
                }
                temp = temp / c;
                results = " :" + temp;
            }
//            System.out.println("Execute SeRQL: " + SeRQL);
//            System.out.println("Result entities: " + results);
        }
        return results;
    }

    public static synchronized String viewCG(ServletContext ctx, String query, int count, String ambiguous) throws ServletException, Exception {

        String output = "<table border=\"0\" cellpadding=\"5\">";

        QueryBuffer buffer = new QueryBuffer();
        String SeRQL = "";

        //DA2
//        buffer.query = query;
        //ALT
        query = convert2VN(query);
        buffer.query = query;

        GateNamedEntity.serverpath = ctx.getRealPath("/") + "/";
        
        String IEanno = "";

        //DA2
//        IEanno += ENSearch.GetNamedEntity(buffer.query, buffer, ambiguous);
//        IEanno += ENSearch.GetUERW(buffer.query, buffer);

        //ALT
        System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");
        IEanno += ENSearch.GetAnnotation(query, buffer, ambiguous);

        IEanno += buffer.test();

        System.out.println("4. Split query to atomic query.");
        List<ItemType> itemList = new ArrayList<ItemType>();

        if (buffer != null && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                ItemType t = buffer.buffer[i];
                itemList.add(t);
            }
        }

        List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
        splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

        System.out.println("5. Process atom queries...");

        boolean isvalid = false;
        boolean isCheckedTopRel = false;
        ListCG listCG = new ListCG();
        listCG.setQuery(query);

        for (int i = 0; i < atomicQueryList.size(); i++) {

            String atomQuery = atomicQueryList.get(i).getQuery().query;

            System.out.println("Process atom query: " + atomQuery);
            RelationType nextRel = atomicQueryList.get(i).getNextReal();
            QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
            atomBuffer.setRelToNextAtomQuery(nextRel);

            System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

            if (isCheckedTopRel == false) {

                if (identifyTopRel(atomBuffer) != null) {

                    listCG.setTopRel(identifyTopRel(atomBuffer));
                    isCheckedTopRel = true;

                }

            }

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
            System.out.println("5.9 Mapping <entity, relation word, entity) to"
                                 + " <entity, relation type, entity>.");
            QueryTriple tripleAtomSet = ProcessingQuery.
                                                ProcessQuery2Triple(atomBuffer);

            IEanno += atomBuffer.test() + "<br/><br/>";

            // Remove redundancy triple
            System.out.println("5.11 Remove redundancy triple.");
            RemoveTriple(tripleAtomSet, atomBuffer);

            // Identify the relationship between Adjective and Entity
            System.out.println("5.12 Identify the relationship between Adjective and Entity.");

            System.out.println("5.12a Identify the relationship between Quantitative Adjective and Entity.");
            ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.12b Identify the relationship between Quanlitative Adjective and Entity.");
            ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            if (listCG.getTopRel() != null && listCG.getTopRel().name().equals(
                    TopRelationType.COUNT.name())) {
                for (int ii = 0; ii < tripleAtomSet.length; ii++) {
                    if (tripleAtomSet.getItem(ii).relationName.equalsIgnoreCase("populationCount")) {
                        listCG.setTopRel(null);
                        break;
                    }
                }
            }

            System.out.println("5.13 Get new QueryBuffer for purpose of " +
                                                              "generating CG.");
            QueryBuffer buferAtomTmp = MakeQueryBuffer(tripleAtomSet);
            
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

                System.out.println("5.14a Processing in case how many has " +
                                                            "only one Entity");
                if (listCG.getTopRel() != null && listCG.getTopRel().name()
                                        .equals(TopRelationType.COUNT.name())) {
                    // In case the query "how many" have only one entity.
                    buferAtomTmp.length = 1;
                    for (int v = 0; v < atomBuffer.length; v++) {
                        String type = atomBuffer.buffer[v].classType;
                        if (!atomBuffer.buffer[v].className.equals("UE_Agent")
                            && (type.equals(Constants.UE)
                                || type.equals(Constants.IE))) {
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
        }

        // Generate the CG Graphic
        System.out.println("6. Generate the CG Graphic.");
        String out = QueryOutput.generatelistJsCG(listCG);

        ProcessingQuery.IsValid = isvalid;
        if (isvalid) {
            output += "<tr><td valign=\"center\"><font color=\"red\"><b></b>" +
                    "</font></td><td><b>" + out + "</b></td></tr>";
        } else {
            output += "<tr><td><font color=\"black\"><b></b></font></td>" +
                    "<td><b><font color=black>NO CG is generated !</font></b></td>" +
                    "</tr>";
        }

        output += "</table>";
        
        return output;
    }

    public static synchronized String test(ServletContext ctx, String query, int count, String ambiguous) throws ServletException, Exception {

//TEST resultEntities()
       /* String results = "";
        QueryBuffer buffer = new QueryBuffer();
        String SeRQL = "";

        //DA2
//        buffer.query = query;

        //ALT
        buffer.query = convert2VN(query);

        GateNamedEntity.serverpath = ctx.getRealPath("/") + "/";
        ConnectServers connect = new ConnectServers();

        String IEanno = "";

        //DA2
//        IEanno += ENSearch.GetNamedEntity(buffer.query, buffer, "");
//        ENSearch.GetUERW(buffer.query, buffer);
        
        //ALT
        System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");
        IEanno += ENSearch.GetAnnotation(query, buffer);

        //DA2
//        boolean simple = true;
//        for (int i = 0; i < buffer.length; i++) {
//            ItemType tmp = buffer.getItem(i);
//            if (tmp.classType.equalsIgnoreCase("RW")) {
//                simple = false;
//                break;
//            }
//        }

        String nhapnhang = connect.isAmbiguous(buffer.query, IEanno);
        nhapnhang = "";

        //DA2
//        if (!nhapnhang.equalsIgnoreCase("") && (!simple)) {
//            return nhapnhang;
//        }
        
        //ALT
        if (!nhapnhang.equalsIgnoreCase("")) {
            return nhapnhang;
        }

        //DA2
//        if (simple) {
//            String simplelist = "";
//            if (buffer.length > 0) {
//                ItemType tmp = buffer.getItem(0);
//                if (tmp.progreg.equalsIgnoreCase("G")) {
//                    if (tmp.classType.equalsIgnoreCase("UE")) {
//                        return simplelist += "class:" + tmp.className + ";";
//                    }
//                } else {
//                    simplelist = connect.getsimple(buffer.query, IEanno);
//                }
//            }
//            if (!simplelist.equalsIgnoreCase("")) {
//                simplelist = simplelist.substring(0, simplelist.length() - 1);
//            }
//            return simplelist;
//        }

        System.out.println("4. Split query to atomic query.");
        List<ItemType> itemList = new ArrayList<ItemType>();
        
        if (buffer != null && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                ItemType t = buffer.buffer[i];
                itemList.add(t);
            }
        }
        
        List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
        splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

        System.out.println("5. Process atom queries...");

        boolean isvalid = false;
        boolean isCheckedTopRel = false;
        TopRelationType topRelation = null;

        for (int i = 0; i < atomicQueryList.size(); i++) {

            String atomQuery = atomicQueryList.get(i).getQuery().query;

            System.out.println("Process atom query: " + atomQuery);
            RelationType nextRel = atomicQueryList.get(i).getNextReal();
            QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
            atomBuffer.setRelToNextAtomQuery(nextRel);

            System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

            if (isCheckedTopRel == false) {

                if (identifyTopRel(atomBuffer) != null) {

                    topRelation = identifyTopRel(atomBuffer);
                    isCheckedTopRel = true;

                }

            }

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
            System.out.println("5.9 Mapping <entity, relation word, entity) to"
                                 + " <entity, relation type, entity>.");
            QueryTriple tripleAtomSet = ProcessingQuery.
                                                ProcessQuery2Triple(atomBuffer);

            // Remove redundancy triple
            System.out.println("5.11 Remove redundancy triple.");
            RemoveTriple(tripleAtomSet, atomBuffer);

            // Identify the relationship between Adjective and Entity
            System.out.println("5.12 Identify the relationship between "
                                                     + "Adjective and Entity.");

            System.out.println("5.12a Identify the relationship between "
                                        + "Quantitative Adjective and Entity.");
            ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.12b Identify the relationship between "
                                    + "Quanlitative Adjective and Entity.");
            ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

             // Get new QueryBuffer for purpose of generating CG
            System.out.println("5.13 Get new QueryBuffer for purpose of " +
                                                            "generating CG.");
            QueryBuffer buferAtomTmp = MakeQueryBuffer(tripleAtomSet);

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
                if (topRelation != null && topRelation.name()
                                        .equals(TopRelationType.COUNT.name())) {
                    // In case the query "how many" have only one entity.
                    buferAtomTmp.length = 1;
                    for (int v = 0; v < atomBuffer.length; v++) {
                        String type = atomBuffer.buffer[v].classType;
                        if (!atomBuffer.buffer[v].className.equals("UE_Agent")
                            && (type.equals(Constants.UE)
                                || type.equals(Constants.IE))) {
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

            System.out.println("5.15 Generate SeRQL for atom query");
            ProcessingQuery.IsValid = isvalid;
            if (isvalid) {

                String SeRQLAtom = SeRQLMapping.getSeRQLQuery(buferAtomTmp);

                //Add Where clause for Superlative QTA
                if (buferAtomTmp.isContainSuperlativeQTA()) {
                    SeRQLAtom = appendWhereClauseForSQTA(SeRQLAtom, buferAtomTmp, topRelation);
                } else if(buferAtomTmp.isContainQTA()) {
                    SeRQLAtom = appendOrderByClause(SeRQLAtom, buferAtomTmp);
                }

                SeRQL = SeRQL + " " + SeRQLAtom;

                if (nextRel == RelationType.UNION) {
                    SeRQL = SeRQL + " UNION";
                }

                if (nextRel == RelationType.INTERSECT) {
                    SeRQL = SeRQL + " INTERSECT";
                }

            }

        }

        if (isvalid) {

            System.out.println("SeRQL for the whole query is: " + SeRQL);

            results = ENSearch.runSeRQL(SeRQL);

            if (!results.equalsIgnoreCase("")) {
                results = results.substring(0, results.length() - 1);
            }
            System.out.println("Execute SeRQL: " + SeRQL);
            System.out.println("Result entities: " + results);
        }
        return results;
        */
//END TEST resultEntities()


//TEST viewCG()
          /*String output = "<table border=\"0\" cellpadding=\"5\">";

        QueryBuffer buffer = new QueryBuffer();
        String SeRQL = "";

        //DA2
//        buffer.query = query;
        //ALT
        buffer.query = convert2VN(query);

        GateNamedEntity.serverpath = ctx.getRealPath("/") + "/";

        String IEanno = "";

        //DA2
//        IEanno += ENSearch.GetNamedEntity(buffer.query, buffer, ambiguous);
//        IEanno += ENSearch.GetUERW(buffer.query, buffer);

        //ALT
        System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");
        IEanno += ENSearch.GetAnnotation(query, buffer);

        IEanno += buffer.test();

        System.out.println("4. Split query to atomic query.");
        List<ItemType> itemList = new ArrayList<ItemType>();

        if (buffer != null && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                ItemType t = buffer.buffer[i];
                itemList.add(t);
            }
        }

        List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
        splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

        System.out.println("5. Process atom queries...");

        boolean isvalid = false;
        boolean isCheckedTopRel = false;
        ListCG listCG = new ListCG();
        listCG.setQuery(query);

        for (int i = 0; i < atomicQueryList.size(); i++) {

            String atomQuery = atomicQueryList.get(i).getQuery().query;

            System.out.println("Process atom query: " + atomQuery);
            RelationType nextRel = atomicQueryList.get(i).getNextReal();
            QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
            atomBuffer.setRelToNextAtomQuery(nextRel);

            System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

            if (isCheckedTopRel == false) {

                if (identifyTopRel(atomBuffer) != null) {

                    listCG.setTopRel(identifyTopRel(atomBuffer));
                    isCheckedTopRel = true;

                }

            }

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
            System.out.println("5.9 Mapping <entity, relation word, entity) to"
                                 + " <entity, relation type, entity>.");
            QueryTriple tripleAtomSet = ProcessingQuery.
                                                ProcessQuery2Triple(atomBuffer);

            IEanno += atomBuffer.test() + "<br/><br/>";

            // Remove redundancy triple
            System.out.println("5.11 Remove redundancy triple.");
            RemoveTriple(tripleAtomSet, atomBuffer);

            // Identify the relationship between Adjective and Entity
            System.out.println("5.12 Identify the relationship between Adjective and Entity.");

            System.out.println("5.12a Identify the relationship between Quantitative Adjective and Entity.");
            ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.12b Identify the relationship between Quanlitative Adjective and Entity.");
            ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.13 Get new QueryBuffer for purpose of " +
                                                              "generating CG.");
            QueryBuffer buferAtomTmp = MakeQueryBuffer(tripleAtomSet);

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

                System.out.println("5.14a Processing in case how many has " +
                                                            "only one Entity");
                if (listCG.getTopRel() != null && listCG.getTopRel().name()
                                        .equals(TopRelationType.COUNT.name())) {
                    // In case the query "how many" have only one entity.
                    buferAtomTmp.length = 1;
                    for (int v = 0; v < atomBuffer.length; v++) {
                        String type = atomBuffer.buffer[v].classType;
                        if (!atomBuffer.buffer[v].className.equals("UE_Agent")
                            && (type.equals(Constants.UE)
                                || type.equals(Constants.IE))) {
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
        }

        // Generate the CG Graphic
        System.out.println("6. Generate the CG Graphic.");
        String out = QueryOutput.generatelistJsCG(listCG);

        ProcessingQuery.IsValid = isvalid;
        if (isvalid) {
            output += "<tr><td valign=\"center\"><font color=\"red\"><b></b>" +
                    "</font></td><td><b>" + out + "</b></td></tr>";
        } else {
            output += "<tr><td><font color=\"black\"><b></b></font></td>" +
                    "<td><b><font color=black>NO CG is generated !</font></b></td>" +
                    "</tr>";
        }

        output += "</table>";

        return output;
        */
//END TEST viewCG()

        String output = "<table border=\"0\" cellpadding=\"5\">";

        QueryBuffer buffer = new QueryBuffer();
        String SeRQL = "";

        //DA2
//        buffer.query = query;
        //ALT
        query = convert2VN(query);
        buffer.query = query;

        GateNamedEntity.serverpath = ctx.getRealPath("/") + "/";

        String IEanno = "";
        
//      DA2
//        IEanno += ENSearch.GetNamedEntity(buffer.query, buffer, ambiguous);
//        IEanno += ENSearch.GetUERW(buffer.query, buffer);

        //ALT
        System.out.println("1. Identify IE, UE, ADJ, CONJ, RW");
        IEanno += "ENSearch.GetAnnotation: " + "<br/>"
                    + ENSearch.GetAnnotation(query, buffer, "");

        //DA2
//        ConnectServers connect = new ConnectServers();

//        boolean simple = true;
//        for (int i = 0; i < buffer.length; i++) {
//            ItemType tmp = buffer.getItem(i);
//            if (tmp.classType.equalsIgnoreCase("RW")) {
//                simple = false;
//                break;
//            }
//        }
//
//        String nhapnhang = connect.isAmbiguous(buffer.query, IEanno);
//        //nhapnhang = "";        //simple = false;
//        if (!nhapnhang.equalsIgnoreCase("") && (!simple)) {
//            IEanno += "nhapnhang" + nhapnhang;
//        }
//
//        if (simple) {
//            String simplelist = "";
//            if (buffer.length > 0) {
//                ItemType tmp = buffer.getItem(0);
//                if (tmp.progreg.equalsIgnoreCase("G")) {
//                    if (tmp.classType.equalsIgnoreCase("UE")) {
//                        return simplelist += "class:" + tmp.className + ";";
//                    }
//                } else {
//                    simplelist = connect.getsimple(buffer.query, IEanno);
//                }
//            }
//            if (!simplelist.equalsIgnoreCase("")) {
//                simplelist = simplelist.substring(0, simplelist.length() - 1);
//            }
//           IEanno += "simplelist" + simplelist;
//        }
////kjhkjhkjh

        IEanno += "<br/>" + "buffer.test(): " + "<br/>" + buffer.test();
        System.out.println("4. Split query to atomic query.");
        List<ItemType> itemList = new ArrayList<ItemType>();

        if (buffer != null && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                ItemType t = buffer.buffer[i];
                itemList.add(t);
            }
        }

        List<AtomicQuery> atomicQueryList = new ArrayList<AtomicQuery>();
        splitQueryToAtomicQuery(buffer.getQuery(), itemList,
                    atomicQueryList, null);

        System.out.println("5. Process atom queries...");

        boolean isvalid = false;
        boolean isCheckedTopRel = false;
        ListCG listCG = new ListCG();
        listCG.setQuery(query);

        for (int i = 0; i < atomicQueryList.size(); i++) {

            String atomQuery = atomicQueryList.get(i).getQuery().query;

            System.out.println("Process atom query: " + atomQuery);
            RelationType nextRel = atomicQueryList.get(i).getNextReal();
            QueryBuffer atomBuffer = atomicQueryList.get(i).getQuery();
            atomBuffer.setRelToNextAtomQuery(nextRel);

            System.out.println("5.3 Identify Top Relation: COUNT, MAX, MIN, AVERAGE, MOST.");

            if (isCheckedTopRel == false) {

                if (identifyTopRel(atomBuffer) != null) {

                    listCG.setTopRel(identifyTopRel(atomBuffer));
                    isCheckedTopRel = true;

                }

            }

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
            System.out.println("5.9 Mapping <entity, relation word, entity) to"
                                 + " <entity, relation type, entity>.");
            QueryTriple tripleAtomSet = ProcessingQuery.
                                                ProcessQuery2Triple(atomBuffer);

            output += " atomBuffer.test(): " + "<br/>" + atomBuffer.test() + "<br/><br/>";

            // Remove redundancy triple
            System.out.println("5.11 Remove redundancy triple.");
            RemoveTriple(tripleAtomSet, atomBuffer);

            // Identify the relationship between Adjective and Entity
    		System.out.println("5.12 Identify the relationship between Adjective and Entity.");

            System.out.println("5.12a Identify the relationship between Quantitative Adjective and Entity.");
            ProcessingQuery.processQuantitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            System.out.println("5.12b Identify the relationship between Quanlitative Adjective and Entity.");
            ProcessingQuery.processQuanlitativeAdjective(tripleAtomSet,
                                                                    atomBuffer);

            if (listCG.getTopRel() != null && listCG.getTopRel().name().equals(
                    TopRelationType.COUNT.name())) {
                for (int j = 0; j < tripleAtomSet.length; j++) {
                    if (tripleAtomSet.getItem(j).relationName.equalsIgnoreCase("populationCount")) {
                        listCG.setTopRel(null);
                        break;
                    }
                }
            }

            System.out.println("5.13 Get new QueryBuffer for purpose of " +
                                                              "generating CG.");
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

                System.out.println("5.14a Processing in case how many has " +
                                                            "only one Entity");
                if (listCG.getTopRel() != null && listCG.getTopRel().name()
                                        .equals(TopRelationType.COUNT.name())) {
                    // In case the query "how many" have only one entity.
                    buferAtomTmp.length = 1;
                    for (int v = 0; v < atomBuffer.length; v++) {
                        String type = atomBuffer.buffer[v].classType;
                        if (!atomBuffer.buffer[v].className.equals("UE_Agent")
                            && (type.equals(Constants.UE)
                                || type.equals(Constants.IE))) {
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
            }
        }

        // Generate the CG Graphic
        System.out.println("6. Generate the CG Graphic.");
        String out = QueryOutput.generatelistJsCG(listCG);

        ProcessingQuery.IsValid = isvalid;
        if (isvalid) {
            output += "<tr><td valign=\"center\"><font color=\"red\"><b></b>" +
                    "</font></td><td><b>" + out + "</b></td></tr>";
        } else {
            output += "<tr><td><font color=\"black\"><b></b></font></td>" +
                    "<td><b><font color=black>NO CG is generated !</font></b></td>" +
                    "</tr>";
        }

        output += "</table>";

        if (isvalid) {
            System.out.println("SeRQL is: " + SeRQL);
            String entities = ENSearch.runSeRQL(SeRQL);
            if (!entities.equalsIgnoreCase("")) {
                entities = entities.substring(0, entities.length() - 1);
            }
            if (listCG.getTopRel() != null && listCG.getTopRel().name().equalsIgnoreCase(TopRelationType.COUNT.name())) {
                entities = " :" + entities.split(";").length;
            }
            if (listCG.getTopRel() != null && listCG.getTopRel().name().equalsIgnoreCase(TopRelationType.AVERAGE.name())) {
                double temp = 0;
                int c = 0;
                String[] e = entities.split("[\",:,;]");
                for (int i = 0; i < e.length; i++) {
                    if (!e[i].trim().isEmpty()) {
                        temp = temp + Double.parseDouble(e[i]);
                        c++;
                    }
                }
                temp = temp / c;
                entities = " :" + temp;
            }
            System.out.println(entities);
            output += "<br/>" + SeRQL + "<br/>" + "Result Entities:" + entities;
        }

        System.out.println("---------------End test");

        return IEanno + "<br/>" + output;
    }







//    public static String Annotation(ServletContext ctx, String query, int count) throws ServletException, Exception {
//        QueryBuffer buffer = new QueryBuffer();
//        buffer.query = query;
//        buffer.query = convert2VN(query);
//
//        GateNamedEntity.GetEntityandRelationWord(ctx, buffer.query, buffer);
//
//        ProcessingXML.FindClassofAgent(buffer);
//
//        ProcessingXML.CombineEntitys(buffer);
//
//        ProcessingXML.AddRealtion(buffer);
//
//        String anno = "";
//        for (int i = 0; i < buffer.length; i++) {
//            ItemType tmp = buffer.getItem(i);
//            anno += " " + tmp.value + " : " + tmp.className + " start:" + tmp.start + " end:" + tmp.end;
//        }
//        return anno;
//    }

//    public static String ProcessQuery(ServletContext ctx, String query) throws ServletException, Exception {
//        String output = "<table border=\"0\" cellpadding=\"5\">";
//
//        QueryBuffer buffer = new QueryBuffer();
//        buffer.query = query;
//
//        //pre recognize entity
//        ProcessingQuery.PreRecognizeEntity(buffer);
//
//        //output query
//        output += "<tr><td><font color=\"red\"><b>Query:</b></font></td><td> <i>" + query + "</i></td></tr>";
//
//        //KIM annotated
//        //KIMNER.GetNamedEntity(buffer.query, buffer);
//        output += "<tr><td><font color=\"red\"><b>KIM:</b></font></td><td> " + QueryOutput.getHTMLAnnotatedQuery(buffer) + "</td></tr>";
//
//        //GATE annotated
//        AnnieER.getEntities(ctx, buffer.query, buffer);
//        output += "<tr><td><font color=\"red\"><b>GATE:</b></font></td><td> " + QueryOutput.getHTMLAnnotatedQuery(buffer) + "</td></tr>";
//
//
//        //check query and insert more relation
//        ProcessingQuery.PreProcessQuery(buffer);
//
//        //Process query
//        //Checking grammar before processing
//        if (GrammarChecking.CheckGrammar(buffer.getParsedQuery())) {
//            ProcessingQuery.SpecifyQuantifier(buffer);
//            //ProcessingEntity.ProcessEntity(buffer);
//            if (ProcessingQuery.IsValidTriples(buffer)) {
//                ProcessingQuery.ProcessQuery(buffer);
//                if (ProcessingQuery.IsValidCG(buffer)) {
//                    //output += "<tr><td><font color=\"red\"><b>CG:</b></font></td><td><b>" + QueryOutput.getHTMLCG(buffer) + "</b></td></tr>";
//                    output += "<tr><td valign=\"center\"><font color=\"red\"><b>CG:</b></font></td><td><b>" + QueryOutput.generateJsCG(buffer) + "</b></td></tr>";
//                } else {
//                    output += "<tr><td><font color=\"black\"><b>CG:</b></font></td><td><b><font color=black>NO CG is generated !</font></b></td></tr>";
//                }
//            } else {
//                output += "<tr><td><font color=\"black\"><b>CG:</b></font></td><td><b><font color=black>NO CG is generated !</font></b></td></tr>";
//            }
//
//        } else {
//            ProcessingQuery.IsValid = false;
//            QueryBuffer.invalidquery++;
//            output += "<tr><td><font color=\"red\"><b>CG:</b></font></td><td><b><font color=blue>INVALID QUERY (GRAMMAR)</font></b></td></tr>";
//        }
//
//        output += "</table>";
//
//        return output;
//    }

//    public static String loadfile(String path) throws Exception {
//        String content = "";
//
//        FileInputStream fin = new FileInputStream(path);
//        int totalbytes = fin.available();
//        for (int i = 0; i < totalbytes; i++) {
//            content += (char) fin.read();
//        }
//        fin.close();
//
//        return content;
//    }
//
//    public static void print(Object o) {
//        System.out.println(o);
//    }

//    public static void createHTMLoutput(String content) throws Exception {
//        String path = "output.htm";
//        FileOutputStream output = new FileOutputStream(path, false);
//        String desc = "<script type=\"text/javascript\" src=\"wz_jsgraphics.js\"></script>\r\n";
//        for (int i = 0; i < desc.length(); i++) {
//            output.write(desc.charAt(i));
//        }
//        for (int i = 0; i < content.length(); i++) {
//            output.write(content.charAt(i));
//        }
//        output.close();
//
//    }
//
//    public static void createTestResult() throws Exception {
//        String path = "testresult.txt";
//        String content = "";
//        content = "Unrecognized: " + QueryBuffer.unrecognized + "\r\n";
//        content += "Recognized: " + QueryBuffer.recognized + "\r\n";
//        content += "Invalid: " + QueryBuffer.invalidquery + "\r\n";
//
//        FileOutputStream output = new FileOutputStream(path, false);
//        for (int i = 0; i < content.length(); i++) {
//            output.write(content.charAt(i));
//        }
//        output.close();
//
//    }

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
                atomicQueryBuffer.buffer = query.toArray(new ItemType[atomicQueryBuffer.length+10]);
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
                    } else if (t.wordbefore.trim().equalsIgnoreCase(",")) {
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
        int wStart = query.indexOf("where") != -1 ?
                        query.indexOf("where") : -1;

        sClause = query.substring(sStart, fStart);
        fClause = wStart != -1 ? query.substring(fStart, wStart) :
                                query.substring(fStart);
        wClause = wStart != -1 ? query.substring(wStart) : "where";


        operator = topRe.name().equals(TopRelationType.MAX.name()) ?
                   ">= ALL" : "<= ALL";

        leftOperand = "xsd:double" ;


        for(int i = 0; i < buf.length; i++) {
            ItemType tmp = buf.getItem(i);

            if (tmp.delete) { continue; }

            if (tmp.className.compareTo("RW") == 0
                && tmp.isIdentifiedFromSQTA()) {

                int[] domain = {tmp.subindex, tmp.objindex};
                ItemType concept;

                for (int j = 0; j < domain.length; j++) {
                    concept = buf.getItem(domain[j]);

                    if (concept.quantifier.equals("?")){
                        subFromClause =
                            SeRQLMapping.addStmToExternalFromClause(
                            "{" + concept.variable + "temp"
                            + "}" + "rdf:type {<"
                            + ENSearch.getNS(concept.className.replaceAll("UE_", ""))
                            + concept.className.replaceAll("UE_", "")+">}"
                            ,
                            subFromClause);
                    } else {
                        if (concept.quantifier.equals("*")) {
                            subSelClause = subSelClause + "xsd:double("
                                      + concept.variable + "temp"
                                      + ")";

                            leftOperand += "(" +
                                           concept.variable
                                            + ")";
                            }
                   }
               }
               if (tmp.direction == 1) {
                    subFromClause = SeRQLMapping.addStmToExternalFromClause(
                        "{" + buf.getItem(tmp.subindex).variable + "temp" + "}"
                        + " <" + ENSearch.getNS(tmp.relation) + tmp.relation
                        + "> " + "{" + buf.getItem(tmp.objindex).variable
                        + "temp" + "}", subFromClause);
               } else {
                    subFromClause = SeRQLMapping.addStmToExternalFromClause(
                        "{" + buf.getItem(tmp.objindex).variable + "temp" + "}"
                        + " <" + ENSearch.getNS(tmp.relation) + tmp.relation
                        + "> " + "{" + buf.getItem(tmp.subindex).variable
                        + "temp" + "}", subFromClause);
               }

               //Add all other constraints on the subject to the nested FROM clause
               ItemType rel;
               for (int relIndex = 0; relIndex < buf.length; relIndex++) {

                   if (buf.getItem(relIndex).className.compareTo("RW") == 0) {
                       rel = buf.getItem(relIndex);

                       if ((relIndex != i) && (rel.subindex == tmp.subindex)) {

                           if (rel.direction == 1) {
                               subFromClause = SeRQLMapping.addStmToExternalFromClause(
                                "{" + buf.getItem(rel.subindex).variable + "temp" + "}"
                                + " <" + ENSearch.getNS(rel.relation) + rel.relation
                                + "> " + "{" + buf.getItem(rel.objindex).variable
                                + "}", subFromClause);
                           } else {
                               subFromClause = SeRQLMapping.addStmToExternalFromClause(
                                "{" + buf.getItem(rel.objindex).variable + "}"
                                + " <" + ENSearch.getNS(rel.relation) + rel.relation
                                + "> " + "{" + buf.getItem(rel.subindex).variable
                                + "temp" + "}", subFromClause);
                           }

                       } else if ((relIndex != i) && (rel.objindex == tmp.subindex)) {

                           if (rel.direction == 1) {
                               subFromClause = SeRQLMapping.addStmToExternalFromClause(
                                "{" + buf.getItem(rel.subindex).variable + "}"
                                + " <" + ENSearch.getNS(rel.relation) + rel.relation
                                + "> " + "{" + buf.getItem(rel.objindex).variable
                                + "temp" + "}", subFromClause);
                           } else {
                               subFromClause = SeRQLMapping.addStmToExternalFromClause(
                                "{" + buf.getItem(rel.objindex).variable + "temp" + "}"
                                + " <" + ENSearch.getNS(rel.relation) + rel.relation
                                + "> " + "{" + buf.getItem(rel.subindex).variable
                                + "}", subFromClause);
                           }

                       }
                   }
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

        return (sClause + "\n" + fClause + "\n"  + wClause);
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
        for(int i=0; i<buf.length; i++) {
            ItemType tmp = buf.getItem(i);

            if (tmp.delete) { continue; }

            if (tmp.className.compareTo("RW") == 0
                && tmp.isIdentifiedFromQTA()) {

                //Seperate "Select" and "From" clause
                sClause += "," + buf.getItem(tmp.objindex).variable;

                OrderType = specifyOrderType(buf.getItem(tmp.objindex).value);

                if (OrderType == null) {

                    OrderType = Constants.ASC;

                }

//                oClause = "\norder by xsd:double("
                oClause += "xsd:double("
                        + buf.getItem(tmp.objindex).variable + ")" + " "
                        + OrderType + ",";

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
                                buf.getItem(tmp.objindex).classType
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
    
}