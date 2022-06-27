
package master_project;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


class Connect {
    public static Connection conn;
        }

public class ConnectMSSQLServer
{
   public void dbConnect()
   {
      try {
        
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String server = "DESKTOP-UO341V4";
        int port = 57103;
        String user = "DESKTOP-UO341V4\\Emy"; 
        String password = "";
        String database = "simulation";
        String jdbcUrl = "jdbc:sqlserver://"+server+":"+port+";user="+user+";password="+password+";databaseName="+database+"";
        Connect.conn = DriverManager.getConnection(jdbcUrl);
      }catch (Exception e) {
         e.printStackTrace();
     }
   }
      
   public void dbConnect_Close() throws SQLException{
      Connect.conn.close();
   }
}