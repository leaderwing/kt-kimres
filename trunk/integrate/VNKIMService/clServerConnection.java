package org.me.VNKIMService;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * <p>Title: Fuzzy matching of Ontology-based Knowledge Graphs</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: University of Polytechnique</p>
 *
 * @author Huynh Tan Dat
 * @version 1.0
 */
public class clServerConnection {
  private Properties properties = new Properties();

  public boolean Changed;

  public clServerConnection() {}

  public String getKBServer() {
    return properties.getProperty("kbserver", "http://172.28.10.28:8080/sesame/");
  }

  public void setKBServer(String value) {
    if (!properties.getProperty("kbserver").equalsIgnoreCase(value)) {
      properties.setProperty("kbserver", value);
      this.Changed = true;
    }
  }
  public String getLServer() {
    return properties.getProperty("lserver", "http://www.vn-kim.hcmut.edu.vn/vols/");
  }

  public void setLServer(String value) {
    if (!properties.getProperty("lserver").equalsIgnoreCase(value)) {
      properties.setProperty("lserver", value);
      this.Changed = true;
    }
  }

  public String getRepositoryID() {
    return properties.getProperty("repository", "vnkim-mem");
  }

  public void setRepositoryID(String value) {
    if (!properties.getProperty("repository").equalsIgnoreCase(value)) {
      properties.setProperty("repository", value);
      this.Changed = true;
    }
  }

  public String getUsername() {
    return properties.getProperty("username", "testuser");
  }

  public void setUsername(String value) {
    if (!properties.getProperty("username").equalsIgnoreCase(value)) {
      properties.setProperty("username", value);
      this.Changed = true;
    }
  }

  public String getPassword() {
    return properties.getProperty("password", "opensesame");
  }

  public void setPassword(String value) {
    if (!properties.getProperty("password").equalsIgnoreCase(value)) {
      properties.setProperty("password", value);
      this.Changed = true;
    }
  }
  public void LoadDefault() {
    // SeameConnection
    this.setKBServer("http://172.28.10.28:8080/sesame/");
//    this.setLServer("http://www.vn-kim.hcmut.edu.vn/vols/");
    this.setRepositoryID("vnkim-mem");
    this.setUsername("testuser");
    this.setPassword("opensesame");
    // Suppose that setting default value is not user's change.
    this.Changed = false;
  }
  public void Load(String FileName) throws Exception {
    properties.load(new FileInputStream(FileName));
  }

  public void write(String FileName) throws Exception {
    properties.store(new FileOutputStream(FileName),
                "VN-KIM Server Connection Configuration");
  }

}
