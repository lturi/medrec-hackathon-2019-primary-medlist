package medrechackathon.medlist.coordinator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Patient;

public class DbConnector {

	private final String url = "jdbc:postgresql://localhost/";
	private final String user = "postgres";
	private final String password = "";
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
	
	 public Connection connect() {
	        Connection conn = null;
	        try {
	            conn = DriverManager.getConnection(url, user, password);
	         //   System.out.println("Connected to the PostgreSQL server successfully.");
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	 
	        return conn;
	    }
	   public int getPtFromCentralDb(Patient pt) {
	        String SQL = "SELECT count(*) from mrh2019_pt where first_name='" + 
	        		(pt.getName().get(0)).getGivenAsSingleString() + 
	        			"' AND last_name='" +
	        			(pt.getName().get(0)).getFamily().toString() +
	        			"' AND dob='" + dateFormat.format(pt.getBirthDate()) +"'";
	        int count = 0;
	 
	        try (Connection conn = connect();
	                Statement stmt = conn.createStatement();
	                ResultSet rs = stmt.executeQuery(SQL)) {
	            rs.next();
	            count = rs.getInt(1);
	        } catch (SQLException ex) {
	            System.out.println(ex.getMessage());
	        }
	 
	        return count;
	    }
	 
	   public void insertPt(Patient pt) {
	        String SQL = "INSERT INTO mrh2019pt(PtId, first_name,last_name,dob,create_date) "
	                + "VALUES(?,?,?,?,?)";
	        String nextVal = "SELECT nextval(pt_seq)";
	        String nextId = "";
	        long id = 0;
	 
	        try (Connection conn = connect();
		        	
	                Statement stmt = conn.createStatement();
	                ResultSet rs = stmt.executeQuery(nextVal)) {
	            rs.next();
	            nextId = rs.getString(1);
	        } catch (SQLException ex) {
	            System.out.println(ex.getMessage());
	        }	

        try (Connection conn = connect();

	            PreparedStatement pstmt = conn.prepareStatement(SQL,
	                Statement.RETURN_GENERATED_KEYS)) {
	              Date date = new Date();
	            pstmt.setString(1, (nextId));
	            pstmt.setString(2, (pt.getName().get(0)).getGivenAsSingleString());
	            pstmt.setString(3, (pt.getName().get(0)).getFamily().toString());
	            pstmt.setString(4, dateFormat.format(pt.getBirthDate()));
	        	pstmt.setString(5, dateFormat.format(date));
	 
	            int affectedRows = pstmt.executeUpdate();
	            System.out.println(affectedRows);
	            // check the affected rows 
	        /*    if (affectedRows > 0) {
	                // get the ID back
	                try (ResultSet rs = pstmt.getGeneratedKeys()) {
	                    if (rs.next()) {
	                        id = rs.getLong(1);
	                    }
	                } catch (SQLException ex) {
	                    System.out.println(ex.getMessage());
	                }
	            } */
	        } catch (SQLException ex) {
	            System.out.println(ex.getMessage());
	        }
	       // return id;
	    }
	 public static void main (String[] args) {
	//	 DbConnector dbc= new DbConnector();
		// dbc.connect();
		//int pt_count= dbc.getPtFromCentralDb("Millie Janine", "Bryant");
		//System.out.println("I found " + pt_count + " instance(s) of Millie Janine Bryant");
		 
	 
	 }
}
