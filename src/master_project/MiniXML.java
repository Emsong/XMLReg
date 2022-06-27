package master_project;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import static java.util.Spliterators.iterator;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MiniXML {
    
   public static Connection conn; 
   static int id =1;
   static String sql;
   static int parent = 0;
   static int self = 1;
   static String path;
   static int level =1;
   static String pos;
   static String value;
   static int elementid;
   static int svself;
   
   static List<String> parentlist = new ArrayList<String>();
   static List<Integer> parentidlist = new ArrayList<Integer>();
   
   void createTable() throws SQLException {      
        Statement s = Connect.conn.createStatement();
        s.addBatch("DROP TABLE IF EXISTS PathTable_Mini");
        s.addBatch("DROP TABLE IF EXISTS LeafTable_Mini");
        s.addBatch("create table PathTable_Mini (PathId int not null ," + "Path VARCHAR(MAX) ," + "Pos VARCHAR(MAX))");
        s.addBatch("create table LeafTable_Mini (LeafId INT NOT NULL, " + "Name VARCHAR(MAX) ," + "Value VARCHAR(MAX) ," +  "Pos VARCHAR(MAX)) " );
        s.executeBatch();
        
        System.out.print("Table path & value created!\n\n");
    }
          
   public void dbConnect()   {
      try {
        
       Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String server = "DESKTOP-UO341V4\\SQLEXPRESS";
        int port = 57103;
        String user = "sa"; 
        String password = "Emy191295.";
        String database = "Mini-XML";
        String jdbcUrl = "jdbc:sqlserver://"+server+":"+port+";user="+user+";password="+password+";databaseName="+database+"";
        conn = DriverManager.getConnection(jdbcUrl);
      }catch (Exception e) {
         e.printStackTrace();
     }
   }
   
    public static void dbConnect_Close() throws SQLException{
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
            
     //get XPath --> put in path table, not unie path, unique pos ( actually dont care) 
    private static String getParentPath(Node n) {
        
    Node parent = n.getParentNode();
    if (parent.getNodeName() == "#document") {
        return "/" + n.getNodeName();
    }
    else 
    return getParentPath(parent)+ "/" + n.getNodeName();
}
    
    public void  treeBrowser(Node root,int level) throws SQLException, InterruptedException {
       
       level++;
       Statement stmt = Connect.conn.createStatement();
       int self = 0;
       
       NodeList nodeList = root.getChildNodes();
       for (int i = 0; i < nodeList.getLength(); i++) {
    
        Node n = nodeList.item(i);
        
        if (n.hasChildNodes()){
            self++;
            
            svself = self;
        }
        
        //element
        if(n.getNodeType() == 1 && n.getChildNodes().getLength() !=1){
        
            id++; 
            
            parentlist.add(n.getNodeName());
            parentidlist.add(id);
            
            path = getParentPath(n);
            
            String parentNode = n.getParentNode().getNodeName();
            
            
            //for the pos
            if (parentlist.contains(parentNode)){
                int a = parentlist.indexOf(parentNode);
                    int b = parentidlist.get(a);
                    parent = b;
            }else 
                    parent = parentidlist.get(parentidlist.size() - 1);
            
            pos = "("+ level +  "," + "[" + parent + "," + self + "]" + ")";
            
            System.out.println("ID: " + id + " Name: " + n.getNodeName());
            System.out.println("Element Path :" + path);   
            System.out.println("POS: " + pos + "\n");
            
            sql = " INSERT INTO PathTable_Mini (pathid, path, pos) values ('" + id + "','" + path + "','" + pos+ "')";
            stmt.execute(sql);
        }
        
        //attribute
        if(n.hasAttributes()){
            
             NamedNodeMap attrs = n.getAttributes();
             for (int y = 0; y < attrs.getLength(); y++ ){
                Node attr = attrs.item(y);

                level++;
                id++;     
                int attself = 1;
                
                String parentNode = n.getParentNode().getNodeName();
            
            if (parentlist.contains(parentNode)){
                int a = parentlist.indexOf(parentNode);
                    int b = parentidlist.get(a);
                    parent = b;
            }else 
                    parent = parentidlist.get(parentidlist.size() - 1);
            
            
                pos = "("+ level +  "," + "[" + parent + "," + attself + "]" + ")";
                int attparent = parentidlist.get(parentidlist.size() - 1);
        
                String attrValue = fChar4T(attr.getNodeValue());
                String attrname = "@" + attr.getNodeName();
           
                
                System.out.println("ID: " + id  + "  Name: " + attrname + "  Value: " + attrValue + " Pos: " + pos);
           
                sql = " INSERT INTO LeafTable_Mini (LeafId, name, value, pos) values ('" + id + "','" + attrname + "','" + attrValue + "','" + pos+ "')";
                stmt.execute(sql); 
           
                level--;
            }
        }
        
        
        //text
       if(n.getNodeType() == 3  && ! n.getNodeValue().trim().isEmpty()){
        
           
           id++;
           value = fChar4T(n.getNodeValue());
           String name = n.getParentNode().getNodeName();
           
           String parentNode = n.getParentNode().getParentNode().getNodeName();
           if (parentlist.contains(parentNode)){
                int a = parentlist.indexOf(parentNode);
                    int b = parentidlist.get(a);
                    parent = b;
            }else 
                    parent = parentidlist.get(parentidlist.size() - 1);
           
           pos = "("+ level +  "," + "[" + parent + "," + svself + "]" + ")";
           
           System.out.println("--ID: " + id + " Name: " + n.getParentNode().getNodeName());
           System.out.println("Info: " + value);     
           System.out.println("POS: " + pos + "\n" );  
           
           sql = " INSERT INTO LeafTable_Mini (LeafId, name, value, pos) values ('" + id + "','" + name + "','" + value + "','" + pos+ "')";
           stmt.execute(sql);  
           
        }
       
                  
       treeBrowser(n,level);
       }
   }
}
