import java.sql.*;
import java.util.Iterator;
/**
 * Handles putting given tuples into the database.
 * @author Eliana
 *
 */
public class MySQLTableInput {
	
	// Database connection information
	private String path, user, pass;
	Connection con;
	
	/**
	 * Sets up a handler to process MySQL input.
	 * @param path
	 * @param user
	 * @param pass
	 */
	public MySQLTableInput(String path, String user, String pass) {
		this.path = path;
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 * Uses the given DDL to create a new table.
	 * @param ddl "CREATE TABLE" syntax.
	 * @return
	 */
	public boolean createTable(String ddl) {
		boolean ret = false;
		if (!(ddl.contains("CREATE TABLE"))) return false;
		
		// Connect to database
		boolean connected = openConnection();
		if (connected == false) {
			System.err.println("Could not connect.");
		}
		
		// Execute the given DDL update
		try {
			if (con != null) {
				Statement stat = con.createStatement();
				try {
					stat.executeUpdate(ddl);
				} catch (SQLException e) {
					System.err.println("Could not create table: ");
					e.printStackTrace();
				}
			} else {
				System.err.println("Not connected to database; cannot create a table.");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		finish();
		return ret;
	}
	
	/**
	 * Populate the specified table with the given set of tuples.
	 * @param tuples Set of tuples to be added.
	 * @return The number of tuples input.
	 */
	public int populateTable(String tableName, Iterator<Tuple> tuples) {
		// End immediately if no tuples to insert
		if (!(tuples.hasNext())) {
			return 0;
		}
		
		// Set up connection
		boolean connected = openConnection();
		if (connected == false) {
			System.err.println("Could not connect.");
		}
		
		Tuple t = tuples.next();
		String[] columns = t.getColumns();
		int numColumns = columns.length;
		int count = 0;
		PreparedStatement pstat;
		
		// Generate the SQL syntax for a prepared statement
		String fields = "", vals = "";
		for (int i=0; i<numColumns-1; i++) {
			fields += columns[i]+", ";
			vals += "?,";
		}
		fields += columns[numColumns-1];
		vals += "?";
		String sql = "INSERT INTO "+tableName+" ("+fields+") VALUES ("+vals+"); ";
		
		// Execute this prepared statement with each tuple in the iterator
		try {
			pstat = con.prepareStatement(sql);
			
			do  {
				count++;
				String[] values = t.getValues();
				for (int i=1; i<=numColumns; i++) {
					// (note, prepared statements 1-indexed, arrays 0-indexed)
					pstat.setString(i, values[i-1]);
				}
				pstat.addBatch();
			// (Get the next tuple ready if available)
			} while (tuples.hasNext() && (t=tuples.next()) != null);
			
			pstat.executeBatch();
			// If successful (no exceptions produced), return number added
			
		} catch (SQLException e) {
			e.printStackTrace();
			count = -1;
		}
		finish();
		return count;
	}
	
	/**
	 * Sets up the connection to the specified MySQL database.
	 * @return True if successfully opened connection.
	 */
	private boolean openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(path, user, pass);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Tasks to be done when finished with a connection.
	 * (Called when done updating the table.)
	 */
	private void finish() {
		// For now, just close the connection.
		closeConnection();
	}
	
	/**
	 * Close the connection to the SQL server.
	 */
	private void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
