/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.VNKIMService;
import java.io.*;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
import java.util.List;
import org.jdom.*;
import org.jdom.input.*;
//import org.jdom.output.*;
//import org.w3c.dom.*;
//import com.sun.org.apache.xerces.internal.impl.xs.dom.DOMParser;

/**
 *
 * @author Minh Dung
 */
public class InitCode {
    public void extractParentRelation(){
/*        String output="";
        FileOutputStream fout;
        BufferedWriter out;
        try {
            fout = new FileOutputStream ("C:\\Relationparent.xml" );
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
            out.write("<constraints> \n");
            out.write("<ClassRelationship> \n");
            DOMParser parser = new DOMParser();
            parser.parse("C:\\relationa.xml");
            org.w3c.dom.Document doc  = parser.getDocument();
            NodeList lista = doc.getElementsByTagName("relations");
            Element ele = (Element) lista.item(0);
            NodeList list = ele.getElementsByTagName("relation");
            for(int i = 0, length = list.getLength(); i < length; i++){
                output="";
                Element en  =(Element) list.item(i);
                Attr name = en.getAttributeNode("name");
                String val = name.getValue();
                NodeList parents = en.getElementsByTagName("parents");
                if (parents != null && parents.getLength() > 0){
                    Element parente =(Element) parents.item(0);
                    String par=null;
                    output += i+" ";
//                    par = getTextValue(parente,"parent");
                    NodeList parent = parente.getElementsByTagName("parent");
                    if (parent!=null && parent.getLength() > 0){
                        Element pa = (Element) parent.item(0);
                        Attr namepa = pa.getAttributeNode("val");
                        if (namepa!=null) par = namepa.getValue();
                    }
                    if (par!=null)
                    out.write("<relationship value=\"" + val + "\" parentclass=\"" + par + "\"/> \n");
                }
            }
            out.write("</ClassRelationship> \n");
            out.write("</constraints> \n");
            out.close();
            fout.close();
        } catch (Exception e) {
        }*/
        
    }
    public void ExtractParentRe(){
        String output="";
        FileOutputStream fout;
        BufferedWriter out;
        try {
            fout = new FileOutputStream ("C:/Relationparent.xml" );
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
            out.write("<constraints> \n");
            out.write("<ClassRelationship> \n");
            Document doc = new SAXBuilder().build(new File("C:/relationa.xml"));
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren("relation");
            for (int i=0; i< children.size();i++){
                Element rela = (Element) children.get(i);
                String val = rela.getAttributeValue("name");
                Element parents = rela.getChild("parents");
                Element parent = parents.getChild("parent");
                String par= null;
                if (parent!=null) par = parent.getText();
                if (par!=null)
                  out.write("<relationship value=\"" + val + "\" parentclass=\"" + par + "\"/> \n");
            }
            out.write("</ClassRelationship> \n");
            out.write("</constraints> \n");
            out.close();
            fout.close();
        } catch (Exception e) {
        }
        
    }
    public static String ExtractRelationDic(){
        FileOutputStream fout;
        BufferedWriter out;
        try {
//            String path = GateNamedEntity.serverpath + "UEVNDic.xml";
            fout = new FileOutputStream ("C:/ReVNDic.xml" );
            out = new BufferedWriter(new OutputStreamWriter(fout,"UTF8"));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
            out.write("<dictionary> \n");
            Document doc = new SAXBuilder().build(new File("C:/LexicalRelation.rdf"));
            Element root = doc.getRootElement();
            Namespace namespace = root.getNamespace();
            List<Element> children = root.getChildren();
            for (int i=0; i< children.size();i++){
                Element rela = (Element) children.get(i);                
                Element child = rela.getChild("type", namespace);
                String val = child.getAttributeValue("resource", namespace);
                val = val.replace("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#Từ_khóa_", "");
                Element label = rela.getChild("label", root.getNamespace("rdfs"));
                String par = label.getText();
                out.write("<entry Revalue=\"" + par + "\" Reclass=\""+ val +"\"/> \n");
            }
            out.write("</dictionary> \n");
            out.close();
            fout.close();
        } catch (Exception e) {
            return e.toString();
        }
        return "";
    }

    public static String getRelationSetEntities(String domain, String range, String firstset) {
        String result = "";
        String path = GateNamedEntity.serverpath + "ENRelation.xml";
        try {
            Document doc = new SAXBuilder().build(new File(path));
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element rela = (Element) children.get(i);
                //String relation = rela.getAttributeValue("name").replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "");
                String relation = rela.getAttributeValue("name");
                String selectedDom = "";
                String selectedRang = "";

                if (firstset.contains(relation)) {
                    Element dom = rela.getChild("domains");
                    List<Element> domchild = dom.getChildren();
                    Boolean domok = false;
                    for (int j = 0; j < domchild.size(); j++) {
                        Element doma = (Element) domchild.get(j);
                        //String val = doma.getText().replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "");
                        String val = doma.getText();
                        if (domain.equalsIgnoreCase(val)) {
                            selectedDom = val;
                            domok = true;
                            break;
                        }
                    }
                    if (domok == false) {
                        continue;
                    }
                    Element ran = rela.getChild("ranges");
                    List<Element> ranchild = ran.getChildren();
                    for (int k = 0; k < ranchild.size(); k++) {
                        Element rana = (Element) ranchild.get(k);
                        //String val = rana.getText().replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "");
                        String val = rana.getText();
                        if (range.equalsIgnoreCase(val)) {
                            selectedRang = val;
                            //result+=rela.getAttributeValue("name").replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "")+",";
                            //Check for the relation that same Domain and Range can't be accepted
                            if (ProcessingXML.checkExceptionRelations(relation,
                                    selectedDom, selectedRang) == true) {
                                continue;//ignore this result
                            }

                            result += rela.getAttributeValue("name") + ",";
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        if (!result.equalsIgnoreCase("")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String getRelationSetEntities2(String domain, String range){
        String result="";
        String path = GateNamedEntity.serverpath + "relation.xml";
        try{
            Document doc = new SAXBuilder().build(new File(path));
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren();
            for (int i=0; i<children.size();i++){
                Element rela = (Element) children.get(i);
                Element dom = rela.getChild("domains");
                List<Element> domchild = dom.getChildren();
                Boolean domok=false;
                for (int j=0; j < domchild.size(); j++){
                    Element doma = (Element) domchild.get(j);
                    String val = doma.getText().replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "");
                    if (domain.equalsIgnoreCase(val)){
                        domok=true;
                        break;
                    }
                }
                if (domok=false) break;
                Element ran = rela.getChild("ranges");
                List<Element> ranchild = ran.getChildren();
                for (int k=0; k < ranchild.size(); k++){
                    Element rana = (Element) ranchild.get(k);
                    String val = rana.getText().replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "");
                    if (range.equalsIgnoreCase(val)){
                        result+=rela.getAttributeValue("name").replaceAll("http://www.dit.hcmut.edu.vn/vnkim/vnkimo.rdfs#", "")+",";
                        break;
                    }
                }
            }
        }catch (Exception e){
            
        }
        if (!result.equalsIgnoreCase("")) result = result.substring(0, result.length()-1);
        return result;
    }
}