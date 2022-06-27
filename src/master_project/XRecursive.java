package master_project;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XRecursive {
             
    public static Connection conn;   
    String type = null;
    static String name = null;   
    String value = null;
    static String parent = null;
    static int id;
    static String sql;
    
    
    static void InsertStruct() throws SQLException {
       Statement stmt = Connect.conn.createStatement();
       sql = "INSERT INTO tag_structure_XRec(tagName,id, pid) VALUES ('" + name+ "','" + id +"','"+ parent +"')";
       stmt.execute(sql);
    }
    
    //Create Table tag_structure & tag_value
    void CreateTable() throws SQLException {
      
        Statement stmt = Connect.conn.createStatement();
        stmt.addBatch("DROP TABLE IF EXISTS tag_structure_XRec");
        stmt.addBatch("DROP TABLE IF EXISTS tag_value_XRec");
        stmt.addBatch("CREATE TABLE tag_structure_XRec (tagName VARCHAR(255),id varchar(max) not NULL, pid varchar(max))"); 
        stmt.addBatch("CREATE TABLE tag_value_XRec (id varchar(max) NOT NULL,  value VARCHAR(max), type varchar(255) )"); 
        stmt.executeBatch();
        
        System.out.print("\nTable path & value created!\n");
    }
    
    public void dbConnect()
   {
      try {
        
       Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String server = "DESKTOP-UO341V4\\SQLEXPRESS";
        int port = 57103;
        String user = "sa"; 
        String password = "Emy191295.";
        String database = "XRecursive";
        String jdbcUrl = "jdbc:sqlserver://"+server+":"+port+";user="+user+";password="+password+";databaseName="+database+"";
        conn = DriverManager.getConnection(jdbcUrl);
      }catch (Exception e) {
         e.printStackTrace();
     }
   }
    
    public void dbConnect_Close() throws SQLException{
      conn.close();
   }
    
    public boolean isWhitespaceNode(Node n) {
            if (n.getNodeType() == Node.TEXT_NODE) {
                String val = n.getNodeValue().trim();
                return val.trim().length() == 0;
            } else {
                return false;
            }
        }

    private String fChar4T(String textContent) {
         textContent = textContent.replaceAll("'", " ").replaceAll("&", " ").replaceAll("\\s+", " ").replaceAll("\\n", " ");
        return textContent;
    }
        
     public void  ReadXML(Node m) throws SQLException, InterruptedException {
                   
         Statement stmt = Connect.conn.createStatement();
         NodeList nodeList = m.getChildNodes();
           
           //* While xml document is not null
           for (int i = 0; i < nodeList.getLength(); i++) {
               Node currentNode = nodeList.item(i);
               
               //- text node
               if (currentNode.getNodeType() == Node.TEXT_NODE && ! currentNode.getNodeValue().trim().isEmpty())  {
                    value = fChar4T(currentNode.getTextContent().trim());     
                    System.out.println( "----Elmt:" + id + " Name: " + name + " value: " + value);
                   // stmt.addBatch("INSERT INTO tag_value (id, value, type ) VALUES ('" + id + "','" + value + "','" + "E" +"')");
                  //  stmt.addBatch("INSERT INTO tag_structure(tagName,id, pid) VALUES ('" + name+ "','" + id +"','"+ parent +"')");
                    sql = "INSERT INTO tag_value_XRec (id, value, type ) VALUES ('" + id + "','" + value + "','" + "E" +"')";
                    stmt.execute(sql);
               }                  
               
           //*if element type - element or attribute
               if (currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getChildNodes().getLength() != 0  ) {
                // read parent as pName
                // id = id + 1;     
                // Add name, pName and id to N
                   parent = currentNode.getParentNode().getNodeName();
                   name = currentNode.getNodeName();
                   id++;
                   
                   System.out.println( ":" + id + " Name: " + name  + " Parent: " + parent);
                  sql = "INSERT INTO tag_structure_XRec(tagName,id, pid) VALUES ('" + name+ "','" + id +"','"+ parent +"')";
                  stmt.execute(sql);
                   
                   NamedNodeMap attrs = currentNode.getAttributes();
                   for (int y = 0; y < attrs.getLength(); y++ ){
                       Node attr = attrs.item(y);
                       String attrValue = fChar4T(attr.getNodeValue().trim());
                       name = attr.getNodeName();
                       id++;
                       System.out.println("----Attr: " + id + "    Name: " + name + "    value: " + attrValue);
                       
                       // attribute masuk N - name, id, parent  //attrubute masuk V - id, value and type
                        stmt.addBatch("INSERT INTO tag_value_XRec (id, value, type ) VALUES ('" + id + "','" + attrValue + "','" + "A" +"')");
                        stmt.addBatch("INSERT INTO tag_structure_XRec(tagName,id, pid) VALUES ('" + name+ "','" + id +"','"+ parent +"')");
                        stmt.executeBatch();
                   }
                       ReadXML(currentNode);                    
               }
           }  
     }   
}
