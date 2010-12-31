/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.VNKIMService;

import org.w3c.dom.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
//import org.apache.xerces.parsers.*;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Minh Dung
 */
public class ConnectServers {
    public synchronized String performLucene(String triple, String start, String hitNumber){
	String tripleText = triple;
//        tripleText = Mapper.convert(tripleText);
        String resulta="";
	try {
//		URL serverURL = new URL("http://www.vn-kim.hcmut.edu.vn/LServerFrontEnd/SearchService?");
//                URL serverURL = new URL("http://172.28.10.27:8080/ClusterSearch/SearchServlet?");
        URL serverURL = new URL("http://localhost:8080/ClusterSearch/SearchServlet?");
        resulta = performQuery(serverURL, tripleText, start, hitNumber);
	}
	catch(Exception ex){
		System.out.println("Exception: " + ex.getMessage());
	}
        return resulta;
    }
/*    public static void VNKIManno(String query){
	String annotationText = "<?xml version=\"1.0\"?><galleon_input><galleon_text>" + query + "</galleon_text></galleon_input>";
	try {
		URL serverURL = new URL("http://172.28.10.28:8080/aserver/servlets/annotationservice");
		//resultano = performAnnotationQuery(serverURL, annotationText, "ALL_NE");
		annotext = performAnnotationQuery(serverURL, annotationText, "STANDARD");
		//String result = performAnnotationQuery(serverURL, annotationText, "KB_ENRICHMENT");
	}
	catch(Exception ex){
		System.out.println("Exception: " + ex.getMessage());
	}
    }*/
    public String performVNKIManno(String query, QueryBuffer output){

        String annotationText = "<?xml version=\"1.0\"?><galleon_input><galleon_text>" + query + "</galleon_text></galleon_input>";
        String annotext="";
        try {
//            URL serverURL = new URL("http://172.28.10.28:8080/aserver/servlets/annotationservice");
            URL serverURL = new URL("http://localhost:8080/aserver/servlets/annotationservice");
//            String annotext = performAnnotationQuery(serverURL, annotationText, "ALL_NE");
            annotext = performAnnotationQuery(serverURL, annotationText, "STANDARD");
//          creates a DOM parser object
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(annotext)));
            NodeList listano = doc.getElementsByTagName("Annotations");
            NodeList list = listano.item(0).getChildNodes();
            for(int i = 0, length = list.getLength(); i < length; i++){
                Element entity  = (Element)list.item(i);
                String classname = entity.getNodeName();
                Attr name = entity.getAttributeNode("name");
                String namene = "";
                if (name!=null)
                    namene = name.getValue();
                Attr id = entity.getAttributeNode("inst");
                Attr start = entity.getAttributeNode("startOffset");
                Attr end = entity.getAttributeNode("endOffset");
                int startoff = Integer.valueOf(start.getValue());
                int endoff = Integer.valueOf(end.getValue());
                String wordfollow = GateNamedEntity.getWordFollow(query, endoff);
                String wordbefore = GateNamedEntity.getWordBefore(query, startoff);
                String value = query.substring(startoff, endoff);
                String sidname="";
                if (id!=null) sidname=id.getValue();
//                sidname = sidname.substring(sidname.indexOf("#")+1);
//                if (!classname.equalsIgnoreCase("NamedEntity"))
                output.InsertIE(namene, value, classname, "IE", sidname, startoff, endoff, "K", wordfollow, wordbefore);
//              else
//                    output.InsertIE(value, value, classname, "IE", sidname, startoff, endoff, "K", wordfollow, wordbefore);
//                OutputSeRql("(" + name.getValue() + "/" + classname + "/"+ sidname +") ","C:\\triple.txt");
            }
        } catch (Exception e) {

        }
        return annotext;
    }

    public String VNKIMambiguous(String query, QueryBuffer output, String ambiguous){
	String annotationText = "<?xml version=\"1.0\"?><galleon_input><galleon_text>" + query + "</galleon_text></galleon_input>";
        String resultano="";
        try {
//            URL serverURL = new URL("http://172.28.10.28:8080/aserver/servlets/annotationservice");
            URL serverURL = new URL("http://localhost:8080/aserver/servlets/annotationservice");
//            String annotext = performAnnotationQuery(serverURL, annotationText, "ALL_NE");
            String annotext = performAnnotationQuery(serverURL, annotationText, "STANDARD");
            resultano = annotext;
//          creates a DOM parser object
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(annotext)));
            NodeList listano = doc.getElementsByTagName("Annotations");
            NodeList list = listano.item(0).getChildNodes();
            for(int i = 0, length = list.getLength(); i < length; i++){
                Element entity  = (Element)list.item(i);
                String classname = entity.getNodeName();
                Attr name = entity.getAttributeNode("name");
                String namene = "";
                if (name!=null)
                    namene = name.getValue();
                Attr id = entity.getAttributeNode("inst");
                Attr start = entity.getAttributeNode("startOffset");
                Attr end = entity.getAttributeNode("endOffset");
                int startoff = Integer.valueOf(start.getValue());
                int endoff = Integer.valueOf(end.getValue());
                String value = query.substring(startoff, endoff);
                if (ambiguous.contains(value)&&(!ambiguous.contains(classname))) continue;
                String wordfollow = GateNamedEntity.getWordFollow(query, endoff);
                String wordbefore = GateNamedEntity.getWordBefore(query, startoff);
                String sidname="";
                if (id!=null) sidname=id.getValue();
//                sidname = sidname.substring(sidname.indexOf("#")+1);
                output.InsertIE(namene, value, classname, "IE", sidname, startoff, endoff, "K", wordfollow, wordbefore);
//                OutputSeRql("(" + name.getValue() + "/" + classname + "/"+ sidname +") ","C:\\triple.txt");
            }
        } catch (Exception e) {

        }
        return resultano;
    }
    
    public String isAmbiguous(String query, String annotext){
        String result="";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(annotext)));
            NodeList listano = doc.getElementsByTagName("Annotations");
            NodeList list = listano.item(0).getChildNodes();
            for(int i = 0, length = list.getLength(); i < length; i++){
                Element entity  = (Element)list.item(i);
                Attr start = entity.getAttributeNode("startOffset");
                Attr end = entity.getAttributeNode("endOffset");
                int startoff = Integer.valueOf(start.getValue());
                int endoff = Integer.valueOf(end.getValue());
                String listAmbiguous = getListAmbiguous(query, list, startoff, endoff);
                if ((!result.contains(listAmbiguous))&&(!listAmbiguous.equalsIgnoreCase("")))
                    result+= listAmbiguous+";";                
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        if (!result.equalsIgnoreCase("")){
            result = result.substring(0, result.length()-1);
            return "Ambiguous@"+result;
        }
        else return "";
    }
    
    public String getsimple(String query, String annotext){
        String result="";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(annotext)));
            NodeList listano = doc.getElementsByTagName("Annotations");
            NodeList list = listano.item(0).getChildNodes();

            Element entity  = (Element)list.item(0);
            Attr start = entity.getAttributeNode("startOffset");
            Attr end = entity.getAttributeNode("endOffset");
            int startoff = Integer.valueOf(start.getValue());
            int newoff = startoff;
            int i=0;
            while (i<list.getLength()){
                entity  = (Element)list.item(i);
                start = entity.getAttributeNode("startOffset");
                end = entity.getAttributeNode("endOffset");
                newoff = Integer.valueOf(start.getValue());
                if (newoff!=startoff) break;
                int endoff = Integer.valueOf(end.getValue());
                String value = query.substring(newoff, endoff);
                Attr id = entity.getAttributeNode("inst");
                if (id!=null){
                    String sidname = id.getValue();
                    sidname = sidname.substring(sidname.indexOf("#")+1);                    
                    result+=sidname+":"+value+";";
                    i++;
                    continue;
                }
                String classname = entity.getNodeName();
                Attr name = entity.getAttributeNode("name");
                if (name!=null){
                    result+=classname+":~"+name.getValue()+";";
                    i++;
                    continue;
                }
                i++;
            }
        } catch (Exception e) {
            return "";
        }
        return result;
    }

    public String getListAmbiguous(String query, NodeList list, int startoff, int endoff){
        String result=query.substring(startoff, endoff)+":";
        int count=0;
            for(int i = 0, length = list.getLength(); i < length; i++){
                    Element entity  = (Element)list.item(i);
                    String classname = entity.getNodeName();
                    Attr start = entity.getAttributeNode("startOffset");                    
                    int starto = Integer.valueOf(start.getValue());
                    if (starto > startoff) break;
                    Attr end = entity.getAttributeNode("endOffset");
                    int endo = Integer.valueOf(end.getValue());
                    if ((starto==startoff)&&(endo == endoff)&&(!result.contains(classname))){
                        count++;
                        result+=classname+",";
                    }
            }
        result = result.substring(0, result.length()-1);
        if (count>1) return result;   
        else
            return "";
    }
    public String performAnnotationQuery(URL serverURL, String annotationText, String annotationType) throws IOException {
            InputStream resultStream = sendPostRequest(serverURL, annotationText, annotationType);
            StringBuffer result = new StringBuffer();
            try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream, "UTF-8"));

                    int c;
                    while((c = reader.read()) != -1){
                            result.append((char) c);
                    }
            }
            finally {
                    resultStream.close();
            }
            return result.toString();
    }

    public synchronized InputStream sendPostRequest(URL url, String annotationText, String annotationType) throws IOException  {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String postData = "AnnotationText" + "=" + annotationText;
            postData += "&AnnotationType" + "=" + annotationType;
            // Set up request
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
                    // Write the form data to the connection
            OutputStream postStream = connection.getOutputStream();
            Writer postWriter = new OutputStreamWriter(postStream, "UTF-8");
            postWriter.write(postData);
            postWriter.flush();
            postWriter.close();
                    // Send the request
                connection.connect();
                    // Check whether the server reported any errors
                checkResponse(connection);
            
                return new BufferedInputStream(connection.getInputStream(), 2048);            
        }

    public void checkResponse(HttpURLConnection conn) throws IOException {
            int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                    return;
                    }

            String responseMsg = conn.getResponseMessage();
    //		System.out.println("Got error code: " + responseCode + " " + responseMsg);

            //FW: Classify HTTP errors here
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    throw new IOException(responseMsg);
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST && responseMsg != null) {
                            throw new IOException(responseMsg);
            } else {
                    throw new IOException(responseMsg);
            }
        }
/*    public static void Annotation(int numPartern){
        FileOutputStream fout;
	FileInputStream fin;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        try
        {
            fin = new FileInputStream ("C:\\J\\VNKIMService\\test\\Inputchugiai.txt");
            in = new BufferedReader(new InputStreamReader(fin, "UTF8"));
            bis = new BufferedInputStream(fin);
            dis = new DataInputStream(bis);
            for (int i=1; i<= numPartern; i++){
                fout = new FileOutputStream ("C:\\J\\VNKIMService\\test\\Chugiai\\" + i + ".xml" );
                out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
		URL serverURL = new URL("http://172.28.10.28:8080/aserver/servlets/annotationservice");
                String query = in.readLine();
        	String annotationText = "<?xml version=\"1.0\"?><galleon_input><galleon_text>" + query + "</galleon_text></galleon_input>";
		String resultano = performAnnotationQuery(serverURL, annotationText, "STANDARD");
                out.write(resultano);
                out.close();
                fout.close();
            }
            bis.close();
            fin.close();
        }
        catch (IOException e)
        {
            System.err.println ("Unable to read or write from file");
            System.exit(-1);
        }
    }

    public static String extractUE(){
        FileOutputStream fout;
	FileInputStream fin;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        String problem="";
        try
        {
            fin = new FileInputStream ("C:\\ngulieu6.txt");
            in = new BufferedReader(new InputStreamReader(fin, "UTF8"));
            bis = new BufferedInputStream(fin);
            dis = new DataInputStream(bis);
            fout = new FileOutputStream ("C:\\J\\VNKIMService\\test\\UE3.xml" );
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
            out.write("<dictionary> \n");
            String next="";
            String query = in.readLine();
            String[] list = query.split(":");
            String current = list[0];
            String UE = list[1];
            String tam="<entry IEvalue=\"";
            while (query!=null){
                tam +=UE;
                query = in.readLine();
                if (query!=null){
                    list = query.split(":");
                    next = list[0];
                }
                else next="";
                if (!next.equalsIgnoreCase(current)){
                    out.write(tam + "\" IEclass=\""+current+"\"/> \n");
                    tam = "<entry IEvalue=\"" ;
                }else
                    tam +=", ";
                current = next;
                UE = list[1];
            }
            out.write("</dictionary> \n");
            out.close();
            fout.close();
            bis.close();
            fin.close();
        }
        catch (IOException e)
        {
            return e.toString();
        }
        return problem;
    }

    public static String extractparent(){
        FileOutputStream fout;
	FileInputStream fin;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        String problem="";
        try
        {
            fin = new FileInputStream ("C:\\parent.txt");
            in = new BufferedReader(new InputStreamReader(fin, "UTF8"));
            bis = new BufferedInputStream(fin);
            dis = new DataInputStream(bis);
            fout = new FileOutputStream ("C:\\parent.xml" );
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
            out.write("<constraints> \n");
            out.write("<ClassRelationship> \n");
            String next="";
            String query = in.readLine();
            String[] list = query.split(":");
            String current = list[0];
            String UE = list[1];
            String tam="<relationship value=\"";
            while (query!=null){
                tam +=UE;
                query = in.readLine();
                if (query!=null){
                    list = query.split(":");
                    next = list[0];
                }
                else next="";
                if (!next.equalsIgnoreCase(current)){
                    out.write(tam + "\" parentclass=\""+current+"\"/> \n");
                    tam = "<relationship value=\"" ;
                }else
                    tam +=", ";
                current = next;
                UE = list[1];
            }
            out.write("</ClassRelationship> \n");
            out.write("</constraints> \n");
            out.close();
            fout.close();
            bis.close();
            fin.close();
        }
        catch (IOException e)
        {
            return e.toString();
        }
        return problem;
    }*/
    public String performQuery(URL serverURL, String queryText, String start, String hitNumber) throws IOException {
	InputStream resultStream = sendTripleRequest(serverURL, queryText, start, hitNumber);
	StringBuffer result = new StringBuffer();
	try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream, "UTF-8"));

                int c;
		while((c = reader.read()) != -1){
			result.append((char) c);
		}
	}
	finally {
		resultStream.close();
	}
	return result.toString();
/*        FileOutputStream fout;
        BufferedWriter out;
        try {
            fout = new FileOutputStream ("C:\\UEVN.txt" );
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            DOMParser parser = new DOMParser();
            parser.parse("C:\\UEVNDic.xml");
            org.w3c.dom.Document doc  = parser.getDocument();
            NodeList lista = doc.getElementsByTagName("dictionary");
            Element ele = (Element) lista.item(0);
            NodeList list = ele.getElementsByTagName("entry");
            for(int i = 0, length = list.getLength(); i < length; i++){
                Element en  =(Element) list.item(i);
                Attr name = en.getAttributeNode("UEvalue");
                String val = name.getValue();
                String[] parent = val.split(", ") ;
                for (int j=0; j< parent.length; j++){
                    out.write(parent[j] + "\n");
                }
            }
            out.close();
            fout.close();
        } catch (Exception e) {
                return output+e.toString();
        }*/        
    }

public InputStream sendTripleRequest(URL url, String queryText, String start,String hitNumber) throws IOException  {
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		String postData = "query" + "=" + queryText;
	postData += "&start" + "=" + start + "&hitNumber" + "=" + hitNumber;
	// Set up request
	connection.setRequestMethod("POST");
	connection.setDoInput(true);
	connection.setDoOutput(true);
	connection.setUseCaches(false);
		// Write the form data to the connection
	OutputStream postStream = connection.getOutputStream();
	Writer postWriter = new OutputStreamWriter(postStream, "UTF-8");
	postWriter.write(postData);
	postWriter.flush();
	postWriter.close();
		// Send the request
         connection.connect();
		// Check whether the server reported any errors
	checkResponse(connection);
	return new BufferedInputStream(connection.getInputStream(), 2048);
    }
    public static void OutputSeRql(String SeRql, String file){
        FileOutputStream fout;
        BufferedWriter out;
        try {
            fout = new FileOutputStream (file);
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            out.write(SeRql);
            out.close();
            fout.close();
        } catch (Exception e) {
        }                
    }
}
