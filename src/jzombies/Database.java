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
import repast.simphony.engine.schedule.ScheduledMethod;

public final class Database {
  // static // static String tt = RandomStringUtils.random(8, true, false);
  // String url = "jdbc:sqlite:/Users/z.x/testDB/"+tt+".db";
  static String url = "jdbc:sqlite:/Users/z.x/test.db";
  static ArrayList<String> newInfection;
  static ArrayList<String> oldInfection;
  static ArrayList<String> allInfection;

  /**
   * Create empty database with three empty table:person,point,isIll.Corresponding problog term.
   */
  private Database() {
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

  private static Connection connect() {
    // SQLite connection string
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(url);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return connection;
  }



  public static void addIsIll(String zName) {
    String illPerson = "INSERT INTO is_ill(name) VALUES(?)";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(illPerson);) {
      addPerson.setString(1, zName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }

  public static void addPerson(String hName) {
    String healthyPerson = "INSERT INTO person(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }

  public static void addPoint(String name, double d, double f) {
    String point = "INSERT INTO point(name,x,y) VALUES(?,?,?)";
    try (Connection connection = connect();
        PreparedStatement addPoint = connection.prepareStatement(point);) {
      addPoint.setString(1, name);
      addPoint.setDouble(2, d);
      addPoint.setDouble(3, f);
      addPoint.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }


  }

  /**
   * Update dbs when person moved.
   * 
   * @param name person's name
   * @param f position
   * @param g position
   */
  public static void updatePoint(String name, double f, double g) {
    String point = "UPDATE point SET x=?,y=? WHERE name=?";
    try (Connection connection = connect();
        PreparedStatement updatePoint = connection.prepareStatement(point);) {
      updatePoint.setDouble(1, f);
      updatePoint.setDouble(2, g);
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
  @ScheduledMethod(start = 0.8, interval = 1)
  public static void findNewInfected() {

    oldInfection = new ArrayList<String>();
    allInfection = new ArrayList<String>();
    allInfection = TransmissionModel.getInfectedPerson();
    String findIll = "SELECT * from is_ill";

    try (Connection connection = connect();
        PreparedStatement checkIllExist = connection.prepareStatement(findIll);) {
      ResultSet rs = checkIllExist.executeQuery();

      while (rs.next()) {
        oldInfection.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    newInfection = (ArrayList<String>) allInfection.clone();
    newInfection.removeAll(oldInfection);


  }

  public static ArrayList<String> getNewInfection() {
    return newInfection;
  }


  public static void removeFromList(String hName) {
    newInfection.remove(hName);
  }


}
