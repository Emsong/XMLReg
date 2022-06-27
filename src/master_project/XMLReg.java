package master_project;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLReg {
    
    static int id;
    static int level;
    static String qName;
    static String path = "";
    static int pathid;
    static String elementValue;
    static String sql;
    public static Connection conn;
    static int existvalue;
    static int pathvalue;
    static String attrPath;
    static String str = "";
    static Stack stackPath = new Stack();
    
    public static int exist(String str) throws SQLException{
        
        Statement statement1 = Connect.conn.createStatement();
        String queryString = "select * from pathtable_Reg where pathExp = '"+ str + "'";
        ResultSet rs = statement1.executeQuery(queryString);
    
        while (rs.next()) {
            pathvalue = rs.getInt("pathid");
            rs.close();
            return 1;   // the path exist
        }
        rs.close();
        return 0;   // the path not exist 
    }
    
    static String fChar4T(String textContent) {
         textContent = textContent.replaceAll("'", " ").replaceAll("&", " ").replaceAll("\\s+", " ").replaceAll("\\n", " ");
        return textContent;
    }
    
    static void createTable() throws SQLException {      
      
        Statement s = Connect.conn.createStatement();
        
        s.addBatch("DROP TABLE IF EXISTS PathTable_Reg");
        s.addBatch("DROP TABLE IF EXISTS ValueTable_Reg");
        s.addBatch("create table PathTable_Reg (PathId int not null ," + "pathexp VARCHAR(MAX))");
        s.addBatch("create table ValueTable_Reg (Level int not null, " + "ID INT NOT NULL, " + "Value VARCHAR(MAX) ," +  "RPathId int) " );
        s.executeBatch();
        
        System.out.print("Table path & value created!\n\n");
    }
}