package jzombies;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.RandomStringUtils;

public class Database {
//	static String tt = RandomStringUtils.random(8, true, false);
//    String url = "jdbc:sqlite:/Users/z.x/testDB/"+tt+".db";
	String url = "jdbc:sqlite:/Users/z.x/test.db";
	public Database() {
	    String person = "CREATE TABLE \"person\" (\n" + 
	    		"	\"name\"	TEXT UNIQUE,\n" + 
	    		"	PRIMARY KEY(\"name\")\n" + 
	    		");";
	    
	    String point = "CREATE TABLE \"point\" (\n" + 
	    		"	\"name\"	TEXT UNIQUE,\n" + 
	    		"	\"x\"	REAL,\n" + 
	    		"	\"y\"	REAL,\n" + 
	    		"	FOREIGN KEY(\"name\") REFERENCES \"person\"(\"name\") ON UPDATE CASCADE,\n" + 
	    		"	PRIMARY KEY(\"name\")\n" + 
	    		");";
	    
	    String isIll = "CREATE TABLE \"is_ill\" (\n" + 
	    		"	\"name\"	TEXT UNIQUE,\n" + 
	    		"	PRIMARY KEY(\"name\")\n" + 
	    		");";
	    try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try (Connection connection = this.connect()) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                Statement statement = connection.createStatement();
                statement.execute(person);
                statement.execute(point);
                statement.execute(isIll);
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static Database create() {
		return new Database();
		
	}
	
    private Connection connect() {
        // SQLite connection string
        Connection connection = null;
        try {
        	connection = DriverManager.getConnection(this.url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }



	public void addIsIll(String zName) {
        String illPerson = "INSERT INTO is_ill(name) VALUES(?)";
        try (Connection connection = this.connect();
                PreparedStatement addPerson = connection.prepareStatement(illPerson);) {
        	addPerson.setString(1, zName);
            addPerson.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		
	}

	public void addPerson(String hName) {
        String healthyPerson = "INSERT INTO person(name) VALUES(?);";
        try (Connection connection = this.connect();
                PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
        	addPerson.setString(1, hName);
            addPerson.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		
	}

	public void addPoint(String name, int d, int f) {
        String point = "INSERT INTO point(name,x,y) VALUES(?,?,?)";
        try (Connection connection = this.connect();
                PreparedStatement addPoint = connection.prepareStatement(point);) {
        	addPoint.setString(1, name);
        	addPoint.setInt(2, d);
        	addPoint.setInt(3, f);
        	addPoint.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
		
	}
	
    public void updatePoint(String name, double x, double y) {
        String point = "UPDATE point SET x=?,y=? WHERE name=?";
        try (Connection connection = this.connect();
                PreparedStatement updatePoint = connection.prepareStatement(point);) {
        	updatePoint.setDouble(1, x);
        	updatePoint.setDouble(2, y);
        	updatePoint.setString(3, name);
        	updatePoint.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

	public boolean checkExistIll(String name) {
		String checkExist = "SELECT name\n" + 
				"FROM is_ill\n;";
		
        try (Connection connection = this.connect();
                PreparedStatement checkIllExist = connection.prepareStatement(checkExist);) {
        	 ResultSet rs=checkIllExist.executeQuery();
        	 
             while (rs.next()) {
            	 if(name.equals(rs.getString("name"))) {
            		 return true;
            	 }

             }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return false;
	}

	
}
