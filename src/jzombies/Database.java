package jzombies;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.RandomStringUtils;

public class Database {
  // static String tt = RandomStringUtils.random(8, true, false);
  // String url = "jdbc:sqlite:/Users/z.x/testDB/"+tt+".db";
  String url = "jdbc:sqlite:/Users/z.x/test.db";

  /**
   * Create empty database with three empty table:person,point,isIll.Corresponding problog term.
   */
  public Database() {
    String person = "CREATE TABLE \"person\" (\n" + "	\"name\"	TEXT UNIQUE,\n"
        + "	PRIMARY KEY(\"name\")\n" + ");";

    String point = "CREATE TABLE \"point\" (\n" + "	\"name\"	TEXT UNIQUE,\n" + "	\"x\"	REAL,\n"
        + "	\"y\"	REAL,\n"
        + "	FOREIGN KEY(\"name\") REFERENCES \"person\"(\"name\") ON UPDATE CASCADE,\n"
        + "	PRIMARY KEY(\"name\")\n" + ");";

    String isIll = "CREATE TABLE \"is_ill\" (\n" + "	\"name\"	TEXT UNIQUE,\n"
        + "	PRIMARY KEY(\"name\")\n" + ");";
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e1) {
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

  /**
   * Update dbs when person moved.
   * 
   * @param name person's name
   * @param x position
   * @param y position
   */
  public void updatePoint(String name, int x, int y) {
    String point = "UPDATE point SET x=?,y=? WHERE name=?";
    try (Connection connection = this.connect();
        PreparedStatement updatePoint = connection.prepareStatement(point);) {
      updatePoint.setInt(1, x);
      updatePoint.setInt(2, y);
      updatePoint.setString(3, name);
      updatePoint.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }



  /**
   * Comparing problog result with dbs record, filter out not logged person,which is fresh
   * infected,that are not log into dbs yet.
   * 
   * @param allInfection list contains current all infected person
   * @return list newly infected person in this iteration
   */
  public ArrayList<String> findNewInfected(ArrayList<String> allInfection) {
    // ArrayList<String> newInfection = new ArrayList<String>();
    ArrayList<String> oldInfection = new ArrayList<String>();
    String findIll = "SELECT * from is_ill";

    try (Connection connection = this.connect();
        PreparedStatement checkIllExist = connection.prepareStatement(findIll);) {
      ResultSet rs = checkIllExist.executeQuery();

      while (rs.next()) {
        oldInfection.add(rs.getString("name"));
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    //System.out.println("dbs  all infection list" + allInfection);
    allInfection.removeAll(oldInfection);
    //System.out.println(" dbs new infection list" + allInfection);
    return allInfection;

    // newInfection = allInfection;
    // return newInfection;

  }


}
