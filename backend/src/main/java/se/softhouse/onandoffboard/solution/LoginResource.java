package se.softhouse.onandoffboard.solution;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/login")
public class LoginResource
{
    private static Map<Integer, Login> DATA = new ConcurrentHashMap<>();
    private static AtomicInteger ID_COUNTER = new AtomicInteger();
    
    private final String userName = "sa";

	private final String password = "";

	private final String dbName = "h2bd";
	
	
	
	/** The name of the table we are testing with */
	private final String tableName = "Employees";
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);
		String workingDir = System.getProperty("user.dir");
		String dbDir =  workingDir + "/database/" + dbName;
		System.out.println("Current working directory : " +dbDir );
		conn = DriverManager.getConnection("jdbc:h2:"+ dbDir ,"sa", "");		
		
		  
		return conn;
	}
    
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
    @PUT
    public Login create(Login login)
    {
    	Connection conn = null;
        login.setId(ID_COUNTER.incrementAndGet());
        DATA.put(login.getId(), login);
        //System.out.println("hi im here" +login.getaddress());
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			
		}
		try {
		    String createString =
			        "CREATE TABLE IF NOT EXISTS " + this.tableName + " ( " +
			        "ID INTEGER NOT NULL AUTO_INCREMENT, " +
			        "LastName varchar(50) NOT NULL, " +
			        "FirstName varchar(50) NOT NULL, " +
			        "Email varchar(50) NOT NULL, " +
			        "Phone INTEGER(15) NOT NULL,"+
			        "Pnumber INTEGER(12) NOT NULL,"+
			        "Employee_type VARCHAR(30) NOT NULL,"+
			        "Line_manager VARCHAR(50) NOT NULL,"+
			        "Address VARCHAR(200) NOT NULL,"+
			        "PRIMARY KEY (ID))";
			this.executeUpdate(conn, createString);
			System.out.println("Created a table");
			System.out.println("Inserting records into the table...");
		    Statement stmt = conn.createStatement();  
		    String updatestatement =  "'"+login.getlast()+"','"+login.getfirst()+"','"+login.getmail()+"','"+login.getphone()+"','"+login.getssnnumber()+"','"+login.gettype()+"','"+login.getlinemanager()+"','"+login.getaddress()+"'";
			System.out.println(updatestatement);
		     String sql = "INSERT INTO "+ this.tableName +
		    "(LastName,FirstName,Email,Phone,Pnumber,Employee_type,Line_manager,Address) VALUES ("+updatestatement+")";
		      stmt.executeUpdate(sql);
		
		      System.out.println("Inserted records into the table...");

		      sql = "SELECT ID,LastName,FirstName,Email,Phone,Pnumber,Employee_type,Line_manager,Address FROM " + this.tableName;
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         int id  = rs.getInt("ID");
		         String lastname = rs.getString("LastName");
		         String firstname = rs.getString("FirstName");
		         String email = rs.getString("Email");
		         String type = rs.getString("Employee_type");
		         String linemanager = rs.getString("Line_manager");
		         String address = rs.getString("Address");
		         Integer ssn = rs.getInt("Pnumber");
		         Integer phone = rs.getInt("Phone");
		         
		         //Display values
		         System.out.print("ID: " + id);
		         System.out.print(", LastName: " + lastname);
		         System.out.print(", FirstName: " + firstname);
		         System.out.print(", Email: " + email);
		         System.out.print(", Employee_Type: " + type);
		         System.out.print(", Line_Manager: " + linemanager);
		         System.out.print(", Address: " + address);
		         System.out.print(", SSN: " + ssn);
		         System.out.print(", Phone: " + phone + "\n");
		      }

		} catch (SQLException e) {
			System.out.println("ERROR: Could not create the table");
			e.printStackTrace();
			
		}
		
		
				
		return login;
    }
    
    @GET
    public Collection<Login> getNotes()
    {
        return DATA.values();
    }
    
    @GET
    @Path("{loginId}")
    public Login get(@PathParam("loginId") int loginId)
    {
        Login found = DATA.get(loginId);
        if(found == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return found;
    }
    
    @DELETE
    @Path("{loginId}")
    public void delete(@PathParam("loginId") int loginId)
    {
        Login found = DATA.remove(loginId);
        if(found == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return;
    }
    
    
}
