
package master_project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


class StaticVar{
    static int j=0;
    
    public  int increment(){
       return j++;       }
    public void setVariable() {
    j = 0; }
    public  int decrement(){
       return j--;     }
    public  int currentValue(){
       return j;     }
}
  
class StaticPosVar{
    static int k=0;
      
    public  int increment(){
       return ++k;       }
    public void setVariable() {
    k = 0; }
    public  int decrement(){
       return --k;     }
    public  int currentValue(){
        
        
       return k;     }
}
    
class StaticPathID{
    static int counter=1;
    public  int increment(){
       return counter++;     }
    public int current(){
      return counter;
    }
    public int Set(){
      return counter=1;
    }
}
    class Root {
    public static String StRootname;
    public static Node doc_root;
}

 //XAncestor node info
class NodeInfo {
    XAncestor Storing_Info = new XAncestor();
             
public void getNodeAttr(Node n, String Position) throws SQLException {
    
    NamedNodeMap attrs = n.getAttributes();
    for (int y = 0; y < attrs.getLength(); y++ ){
        Node attr = attrs.item(y);
     
        Storing_Info.GetNodeName(attr);
        Storing_Info.GetNodeValue(attr);
        Storing_Info.GetAncesPos(Position);
        Storing_Info.GetAncesAttr(n);
        Storing_Info.Storing();
        Storing_Info.SetPathArray();
    }      
    }

public boolean isWhitespaceNode(Node n) {
    if (n.getNodeType() == Node.TEXT_NODE) {
        String val = n.getNodeValue();
        return val.trim().length() == 0;
    } else {
        return false;
    }
}

public void  ReadXML(Node m, String Position ) throws SQLException, InterruptedException {
    int j=0;          
 
   // m.normalize();
    System.out.println("Element name :" + m.getNodeName());
    String ParentPos = Position;
    
    NodeList nodeList = m.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
    
        Node currentNode = nodeList.item(i);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getChildNodes().getLength() != 0 && ! isWhitespaceNode(currentNode)  ) {
    
            j=j+1;
            Position = ParentPos + "." + String.valueOf(j) ;       
            this.getNodeAttr(currentNode,Position);
          
            ReadXML(currentNode,Position);        
        }
        else if (currentNode.getNodeType() == Node.TEXT_NODE && ! currentNode.getNodeValue().trim().isEmpty())  {
                
            Storing_Info.GetAncesPos(ParentPos.substring(0, ParentPos.lastIndexOf("."))+".");
            Storing_Info.GetNodeName(currentNode.getParentNode());
            Storing_Info.GetNodeValue(currentNode);
            Storing_Info.GetAncesPath(currentNode.getParentNode());
            Storing_Info.Storing();
            Storing_Info.SetPathArray();
        }   
    }        
} 
}

public class XAncestor {  
     
    //Done - drop & create table 
    void CreateTable() throws SQLException {      
        Statement s = Connect.conn.createStatement();
        s.addBatch("DROP TABLE IF EXISTS Leaf_Node_Ances");
        s.addBatch("DROP TABLE IF EXISTS Ancestor_Path_Ances");
        s.addBatch("create table Ancestor_Path_Ances (Ances_PathID VARCHAR(MAX), " + "Ances_PathExp VARCHAR(MAX))");
        s.addBatch("create table Leaf_Node_Ances (Node_Name VARCHAR(MAX), " + "Ances_PathID INT NOT NULL, " + "Ances_Pos VARCHAR(MAX) ," + " Node_Value VARCHAR(MAX))");
        
        int[] results = s.executeBatch();
        
        System.out.print("\nTable path & value created!\n");
    }
    
    public StaticVar  PathID = new StaticVar();
    public String PathExp[] = new String[100];
    public String Node_Name;
    public String Ances_PathID;
    public String Ances_Pos;
    public String Node_Value;
    
   public void setfields() {
   
       this.Node_Name = null;
       this.Ances_PathID = null;
       this.Ances_Pos=null;
       this.Node_Value= null;
       
       }    
   
   public void SetPathArray(){
        for (int x = 0; x < PathExp.length; x++ ) {
          if (PathExp[x] != null){
            PathExp[x] = null;  }  }
            StaticVar.j = 0;
     }
   
   public void AddNodePath(Node nn ){
       String str;
        str = "/" + nn.getNodeName() ;
        PathExp[PathID.increment()] = str.trim();
       
        }
            
   public void Storing () throws SQLException {
        String str = "";
        StaticPathID PathCounter = new StaticPathID();
       
                
        for (int y = PathExp.length-1; y >= 0 ; y-- ) {
            if (PathExp[y] != null){
            str = str + PathExp[y];  } 
       
    }
 //    System.out.println(str);
     
      
        if (AncesPathExist(str) == 1){ 
        String StInsert;  
        Statement stmt = Connect.conn.createStatement();
        StInsert = "insert into Leaf_Node_Ances " + "values  ('"+this.Node_Name+"','"+this.Ances_PathID+"','"+this.Ances_Pos+"','"+this.Node_Value.replaceAll("'", "")+"')";
        stmt.executeUpdate(StInsert);
         }
       
           
        else if (AncesPathExist(str) == 0 ){
            
        Statement stmt = Connect.conn.createStatement();
        stmt.addBatch(
        "insert into Leaf_Node_Ances " + "values  ('"+this.Node_Name+"','"+PathCounter.current()+"','"+this.Ances_Pos+"','"+this.Node_Value.replaceAll("'", "")+"')");
        stmt.addBatch(
        "insert into Ancestor_Path_Ances " + "values  ('"+PathCounter.increment()+"','"+str+"')");

   int[] results = stmt.executeBatch();
      
         }
             
     }
    
   public void GetAncesPath (Node LeafNode){
                             
    if (Root.doc_root ==LeafNode){
       return;
        }
    else if (LeafNode.getParentNode() != null){
        this.AddNodePath(LeafNode.getParentNode());
        this.GetAncesPath(LeafNode.getParentNode());
        } 
    }
    
   public void GetAncesAttr (Node PNode){
                           
    if (Root.doc_root == PNode){
        this.AddNodePath(PNode);
        }
    else if (PNode!= null){
        this.AddNodePath(PNode);
        this.GetAncesAttr(PNode.getParentNode());
        } 
    }
     
   public void GetNodeName (Node n){
       if (n.getNodeType() == Node.ATTRIBUTE_NODE){
           this.Node_Name= "@" + n.getNodeName().trim();
       }
       else {
           this.Node_Name=  n.getNodeName().trim(); 
       }
   }    
   
   public void GetAncesPathID (String Pno){
       this.Ances_PathID= Pno.trim();
          }  
   
   public void GetNodeValue (Node n){
       this.Node_Value= n.getNodeValue().trim();
   }
   
   public void GetAncesPos (String StrPos){
     this.Ances_Pos = StrPos.trim() ;
   } 
    
   public int AncesPathExist (String s) throws SQLException{
    
    Statement statement1 = Connect.conn.createStatement();
    String queryString = "select Ances_PathID,Ances_PathExp from Ancestor_path_Ances where Ances_pathExp = '"+ s + "'";
    ResultSet rs = statement1.executeQuery(queryString);
        
        while (rs.next()) {
            {
                this.GetAncesPathID(rs.getString(1));
                rs.close();
                return 1;   // the path exist
                       
               }
            }
           rs.close();
           return 0;   // the path not exist
         }   
    
}
  
