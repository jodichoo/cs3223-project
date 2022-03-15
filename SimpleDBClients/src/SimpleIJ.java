import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;

import simpledb.jdbc.embedded.EmbeddedDriver;
import simpledb.jdbc.network.NetworkDriver;

public class SimpleIJ {
   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      System.out.println("Connect> ");
      String s = sc.nextLine();
      Driver d = (s.contains("//")) ? new NetworkDriver() : new EmbeddedDriver();

      try (Connection conn = d.connect(s, null);
           Statement stmt = conn.createStatement()) {
         System.out.print("\nSQL> ");
         while (sc.hasNextLine()) {
            // process one line of input
            String cmd = sc.nextLine().trim();
            if (cmd.startsWith("exit")) {
               break;
            } else if (cmd.startsWith("select")) {
//            	long startTime = System.currentTimeMillis();
            	doQuery(stmt, cmd);
//	            long endTime = System.currentTimeMillis();
//	            System.out.println("\nThis query took " + (endTime - startTime) + " milliseconds");
            } else {
               doUpdate(stmt, cmd);
               System.out.print("\nSQL> ");
            }
         }
      }
      catch (SQLException e) {
         e.printStackTrace();
      }
      sc.close();
   }

   private static void doQuery(Statement stmt, String cmd) {
	  long startTime = System.currentTimeMillis();
      try (ResultSet rs = stmt.executeQuery(cmd)) {
    	 long endTime = System.currentTimeMillis();
         ResultSetMetaData md = rs.getMetaData();
         int numcols = md.getColumnCount();
         int totalwidth = 0;
         
         // print header
         for(int i=1; i<=numcols; i++) {
            String fldname = md.getColumnName(i);
            int width = md.getColumnDisplaySize(i);
            totalwidth += width;
            String fmt = "%" + width + "s";
            System.out.format(fmt, fldname);
         }
         System.out.println();
         for(int i=0; i<totalwidth; i++)
            System.out.print("-");
         System.out.println();

         // print records
         while(rs.next()) {
            for (int i=1; i<=numcols; i++) {
               String fldname = md.getColumnName(i);
               int fldtype = md.getColumnType(i);
               String fmt = "%" + md.getColumnDisplaySize(i);
               if (fldtype == Types.INTEGER) {
                  int ival = rs.getInt(fldname);
                  System.out.format(fmt + "d", ival);
               }
               else {
                  String sval = rs.getString(fldname);
                  System.out.format(fmt + "s", sval);
               }
            }
            System.out.println();
         }
         System.out.println("\nThis query took " + (endTime - startTime) + " milliseconds");

      }
      catch (SQLException e) {
         e.printStackTrace();
         System.out.println("SQL Exception: " + e.getMessage());
      }
   }

   private static void doUpdate(Statement stmt, String cmd) {
      try {
         int howmany = stmt.executeUpdate(cmd);
         System.out.println(howmany + " records processed");
      }
      catch (SQLException e) {
         System.out.println("SQL Exception: " + e.getMessage());
      }
   }
}