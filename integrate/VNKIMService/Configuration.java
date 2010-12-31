package org.me.VNKIMService;

import java.io.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Configuration {
  private static String ServerConfigurationFile = "./VNKIMConnection.config";

  public static clServerConnection ServerConnection = new clServerConnection();
//  public static clOptions Options = new clOptions();
//  public static clPreferences Preferences = new clPreferences();

  private Configuration() { }

  public static void Load(){
    LoadServerConnectionConfiguration();
    // FW: Load something here!!!
  }
  private static boolean LoadServerConnectionConfiguration(){
    try {
      ServerConnection.Load(ServerConfigurationFile);
      return true;
    }
    catch (Exception ex) {
      ServerConnection.LoadDefault();
      return false;
    }
  }
  private static boolean LoadServerConnectionConfiguration(String file){
    try {
      ServerConnection.Load(file);
      return true;
    }
    catch (Exception ex) {
      ServerConnection.LoadDefault();
      return false;
    }
  }

  private boolean LoadFromXML()  throws Exception {
    boolean success = false;
    // Create a DocumentBuilderFactory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    // Create a DocumentBuilder
    DocumentBuilder db = dbf.newDocumentBuilder();
    // Parse the input file to get a Document object
    Document doc = db.parse(new File(this.ServerConfigurationFile));
    // Get the first child (the jgx-element)
    Node kbConnectionNode = null;
    Node optionsNode = null;

    Element rootElement = doc.getDocumentElement();
    for (int i = 0; i < rootElement.getChildNodes().getLength(); i++) {
      Node node = rootElement.getChildNodes().item(i);
      if (node.getNodeName().toLowerCase().equals("Serverconnection")) {
        kbConnectionNode = node;
      } else if (node.getNodeName().toLowerCase().equals("options")) {
        optionsNode = node;
      }
    }

    return success;
  }

  private void Save2XML() throws Exception {
    OutputStream out = null;
    out = new FileOutputStream(this.ServerConfigurationFile);
    out = new BufferedOutputStream(out);
    String xml = "<configuration>\n";
    xml += "<ServerConnection>\n";

    xml += "<lserver>\n";
    xml += "\t" + this.ServerConnection.getLServer();
    xml += "</lserver>\n";

    xml += "<kbserver>\n";
    xml += "\t" + this.ServerConnection.getKBServer();
    xml += "</kbserver>\n";

    xml += "<repositoryID>\n";
    xml += "\t" + this.ServerConnection.getRepositoryID();
    xml += "</repositoryID>\n";

    xml += "<username>\n";
    xml += "\t" + this.ServerConnection.getUsername();
    xml += "</username>\n";

    xml += "<password>\n";
    xml += "\t" + this.ServerConnection.getPassword();
    xml += "</password>\n";

    xml += "</ServerConnection>\n";

    out.write(xml.getBytes());
    out.flush();
    out.close();
   }
}
