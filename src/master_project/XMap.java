package master_project;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMap {
    
   public static Connection conn;  
   static Stack stackVertex = new Stack();
   static Stack stackPath = new Stack(); 
   static String PathParentPos;
   String PathPosition = "1";
   String AttrPathPosition;
   static String sql;
   static String value;
   static int vertexId;
   static String path;
   static int id = 1 ;
   static int pathId;
   static int numberAttribute;
   static String OrdParentPos;
   static String sparent = "1";
   static String sid;
   static int vId;
   static String length;
   static int pathValue;
   static int numberElement;
   static String PathPos;
    
      
   void CreateTable() throws SQLException {
      
        Statement s = Connect.conn.createStatement();
        s.addBatch("DROP TABLE IF EXISTS Data_XMap");
        s.addBatch("DROP TABLE IF EXISTS Vertex_XMap");
        s.addBatch("DROP TABLE IF EXISTS Path_XMap");
        s.addBatch("create table Data_XMap (ORDPath VARCHAR(MAX)  ," + "Node_Value VARCHAR(MAX) ," + "Order_id INT, " +  " numberElement INT, " + "numberAttribute INT, " + "PathID INT)");
        s.addBatch("create table Vertex_XMap (ID INT NOT NULL, " + "Name VARCHAR(MAX)) " );
        s.addBatch("create table Path_XMap (ID INT , " + "Path VARCHAR(MAX)) " );
        s.executeBatch();
        
        System.out.print("\nTable path & value created!\n");
    }
     
       //function TreeBrowser
   void treeBrowser(Node elm) throws SQLException{
       
       elm.normalize();
       
       int j=1;    
       String ParentPos = PathPosition;
       String sid = sparent;
       Statement stmt = Connect.conn.createStatement();  
       int nodeId;
                       
       NodeList ChildNodes = elm.getChildNodes();
       for (int i = 0; i < ChildNodes.getLength(); i++) {
           
           Node currentNode = ChildNodes.item(i);   
                      
       //text node
        if (currentNode.getNodeType() == Node.TEXT_NODE && ! currentNode.getNodeValue().trim().isEmpty())  {
            pathValue = stackPath.indexOf(PathPosition) + 1;
           
            System.out.println(PathPosition);
            
            value = fChar4T(currentNode.getTextContent());
            sql = " INSERT INTO data_XMap (ordpath, node_value,order_id,numberAttribute,numberELement,pathid) values ('" + sid + "','" + value + "','" + id + "','" + 0 + "','" + 0 + "','" + pathValue + "')";
            stmt.execute(sql);      
        }
        
        if(currentNode.getNodeType() == Node.ELEMENT_NODE){
                      
            System.out.println(id + "\tElement name :" + currentNode.getNodeName());
                     
            id++;       
            
            //PathPosition =  ParentPos + "." + vertexId  ; 
            
             if (!stackVertex.contains(currentNode.getNodeName()) && !currentNode.getNodeName().contains("#")){               
               vertexId++;
               stackVertex.add(currentNode.getNodeName());
               
               PathPosition =  ParentPos + "." + vertexId; 
               
               pathId++;
               stackPath.add(PathPosition);
                            
           } else {
                 nodeId = stackVertex.indexOf(currentNode.getNodeName()) +1 ;
                 PathPosition =  ParentPos + "." + nodeId; 
             }
             
             if (!stackPath.contains(PathPosition)){
                 
                 pathId++;
                 stackPath.add(PathPosition);
               } 
            
            sparent = sid + "." + j;
                       
            //numberAttribute-----------------
               if (currentNode.hasAttributes()){
                   numberAttribute = currentNode.getAttributes().getLength();                   
               } 
               else{
                   numberAttribute = 0;  
               }        
               
               numberElement = currentNode.getChildNodes().getLength();
               
               if(currentNode.getFirstChild() != null){
                   length = fChar4T(currentNode.getFirstChild().getTextContent());  
               }                    
               
               if(currentNode.getFirstChild() == null || length.length() == 1){
                   pathValue = stackVertex.indexOf(currentNode.getNodeName()) + 1;

                  sql = " INSERT INTO data_XMap (ordpath, node_value,order_id,numberAttribute,numberELement,pathid) values ('" + sparent + "','" + "" + "','" + id + "','" + numberAttribute + "','" + numberElement + "','" + pathValue + "')";
                  stmt.execute(sql);      
            }
            
            //attribute
            NamedNodeMap attrs = currentNode.getAttributes();

            int p = 1;
            
            for (int y = 0; y < attrs.getLength(); y++ ){
                
            Node attr = attrs.item(y);
            id++;

            String AttrPathPosition = "";
            String attrValue = fChar4T(attr.getNodeValue());
            String attrsid = sparent + "." + p;
            String attrName = '@' + attr.getNodeName();
                        
               if (!stackVertex.contains(attrName)){
                   
                   vertexId++;
                   stackVertex.add(attrName);
               
                   AttrPathPosition =  PathPosition + "." + vertexId ;
                   
                   pathId++;
                   stackPath.add(AttrPathPosition);
                   
                   sql = "INSERT INTO Data_XMap (ORDPath, Node_Value,Order_id, numberElement,numberAttribute,pathId) values ('" + attrsid + "','" + attrValue + "','" + id + "','" + 0 + "','" + numberAttribute +  "','" + pathId +"')";
                   stmt.execute(sql);
                                      
               } else {
                 nodeId = stackVertex.indexOf(attrName) +1 ;
                 AttrPathPosition =  PathPosition + "." + nodeId; 
                 
                 
                 pathValue = stackPath.indexOf(AttrPathPosition) +1 ;
                 
                 sql = "INSERT INTO Data_XMap (ORDPath, Node_Value,Order_id, numberElement,numberAttribute,pathId) values ('" + attrsid + "','" + attrValue + "','" + id + "','" + 0 + "','" + 0 +  "','" + pathValue +"')";
                 stmt.execute(sql);
             }
             
             if (!stackPath.contains(AttrPathPosition)){
                 
                 pathId++;
                 stackPath.add(AttrPathPosition);
                 
                 System.out.println(attrName);
                 System.out.println(AttrPathPosition);
               }        
               
               p = p + 2;              
            }            
            j=j+2;
        }                               
        treeBrowser(currentNode);                
   }       
   }   
        
    //insert all distinct path (SQL)
   static void insertPath() throws SQLException {
        if(!stackPath.empty()){
        Statement stmt = Connect.conn.createStatement();
        sql = "INSERT INTO path_XMap (id,path) values ('" + pathId + "','" + stackPath.pop() + "')";
        stmt.execute(sql);
        pathId--;
        insertPath();
    }   
}
    
    //insert all distinct vertex (SQL)
    static void insertVertex() throws SQLException {
        if(!stackVertex.empty()){
        Statement stmt = Connect.conn.createStatement();
        sql = "INSERT INTO vertex_XMap (id,name) values ('" + vertexId + "','" + stackVertex.pop() + "')";
        stmt.execute(sql);
        vertexId--;
        insertVertex();
    }        
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
               
}

