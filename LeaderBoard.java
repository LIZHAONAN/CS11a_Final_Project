/**
*This class connects to a SQLite database for storing relevant data
*and contains algorithms for calculating each player's scores.It is 
*responsible for storing relevant information and displaying the 
*leader board whenever needed.
*@author Zhaonan Li
*/
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeaderBoard{
  
  //The main method is only for testing
  public static void main(String[] args){
	LeaderBoard lb = new LeaderBoard();
	lb.updateGame("Zhaonan", 12.5, 3);
  }
  
  public Connection c = null;
  public Statement stmt = null;
  public String sql = new String();//sql is command in SQL
  
  //This method helps to connect to the leader board database and avoids
  //duplicating code
  public void connect() {
	  try {
	  Class.forName("org.sqlite.JDBC");
	  c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");
	  //System.out.println("Connected to database successfully...");
	  } catch (Exception e) {
		  System.out.println("Connection fails because of an exception");
		  System.out.println(e.getClass().getName() +
				  ": " + e.getMessage());
	  }
  }
  
  //This method inserts user names to the player table.
  public void updateName(String name) {
	  if (nameId(name) != -1) {
		  System.out.println("name exists");
	  } else {
		  try {
			  connect();
			  c.setAutoCommit(false);
			  stmt = c.createStatement();
			  sql = "INSERT INTO players (userName) " 
			  		+"VALUES(\""+ name +"\");";
			  stmt.executeUpdate(sql);
			  stmt.close();
			  c.commit();
			  c.close();
			  System.out.println("name inserted successfully");
		  } catch(Exception e) {
			  System.out.println("There's an error:"+e.getClass().getName()
					  +": "+e.getMessage());
			  System.exit(0);
		  }
	  }
  }
  
  //This method updates information inside playData 
  //un is user name, tm is time taken in second and level is level
  public void updateGame(String un, double tm, int level) {
	   try {
		   updateName(un);
		   int id = nameId(un);
		   connect();
		   c.setAutoCommit(false);
		   stmt = c.createStatement();
		   sql = "INSERT INTO GameData (userName_id,date,time,level) " 
				  	+"VALUES("+ id +","
				  	+ "\"" + getTime() +"\""
				  	+ ","+ tm + "," + level
				  	+ ");";
		   stmt.executeUpdate(sql);
		   stmt.close();
		   c.commit();
		   c.close();
		   System.out.println("game data inserted successfully");
	   } catch (Exception e) {
			  System.out.println("There's an error:"+e.getClass().getName()
					  +": "+e.getMessage());
			  System.exit(0);
		  }
	   
  }
  
  //This method returns current time in desired format
  public String getTime() {
	  LocalDateTime now = LocalDateTime.now();
	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
			  "yyyy-MM-dd HH:mm:ss");
	  String time = now.format(formatter);
	  return time;
  }
  //This method checks if the user name is taken. If name doesn't
  //exist, it returns -1
  public int nameId(String name) {
	  try {
	  connect();
	  stmt = c.createStatement();
	  ResultSet rs = stmt.executeQuery("SELECT * FROM players;");
	  while (rs.next()) {
		  int id = rs.getInt("id");
		  String userName = rs.getString("userName");
		  if (userName.toLowerCase().equals(name.toLowerCase())) {
			  rs.close();stmt.close();c.close();
			  return id;
		  }
	  	} rs.close();stmt.close();c.close();
	  	
	  } catch(Exception e){
		  System.out.println("There's an error:"+e.getClass().getName()
				  +": "+e.getMessage());
		  System.exit(0);
	  }
	  return -1;
  }
  
  //This method creates a table in the database for storing players' user names
  public void createPlayerTable(){
	  try {
		  connect();
		  stmt = c.createStatement();
		  String sql = "DROP TABLE IF EXISTS players;"
				+  "CREATE TABLE players("
		  		+ "id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"
		  		+ "userName TEXT UNIQUE)";
		  stmt.executeUpdate(sql);
		  stmt.close();
		  c.close();
	  } catch (Exception e) {
		  System.out.println("There's an error:"+e.getClass().getName()
				  +": "+e.getMessage());
		  System.exit(0);
	  }
	  System.out.println("Table created successfully!");
  }
  
  //This method creates a table in the database for storing all relevant information
  //for every game
  public void createDataTable(){
	  try {
		  connect();
		  stmt = c.createStatement();
		  String sql = "DROP TABLE IF EXISTS GameData;"
				+  "CREATE TABLE GameData("
		  		+ "id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"
		  		+ "userName_id INTEGER,"
		  		+ "date DATETIME,"
		  		+ "time DOUBLE,"
		  		+ "level INTEGER)";
		  stmt.executeUpdate(sql);
		  stmt.close();
		  c.close();
	  } catch (Exception e) {
		  System.out.println("There's an error:"+e.getClass().getName()
				  +": "+e.getMessage());
		  System.exit(0);
	  }
	  System.out.println("Table created successfully!");
  }
}