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
    String isCautious= "CREATE TABLE \"is_cautious\" (\n" + "	\"name\"	TEXT UNIQUE,\n"
        + "	PRIMARY KEY(\"name\")\n" + ");";
    String isSocial= "CREATE TABLE \"is_social\" (\n" + "   \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";

    String isIll = "CREATE TABLE \"is_ill\" (\n" + "	\"name\"	TEXT UNIQUE,\n"
        + "	PRIMARY KEY(\"name\")\n" + ");";
    String isResistant = "CREATE TABLE \"is_resistant\" (\n" + "   \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";
    String isQuarantine = "CREATE TABLE \"in_quarantine\" (\n" + "   \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";
    String pointTable = "CREATE TABLE \"point\" (\n" + "    \"name\"  TEXT UNIQUE,\n"
        + "    \"x\" REAL,\n" + "    \"y\" REAL,\n" + "    PRIMARY KEY(\"name\")\n" + ");";
    String vaccinated = "CREATE TABLE \"vaccinated\" (\n" + "    \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";
    String reinfected= "CREATE TABLE \"reinfected\" (\n" + "    \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";
    String dies= "CREATE TABLE \"dies\" (\n" + "    \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";
    String quarantine= "CREATE TABLE \"quarantine\" (\n" + "    \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";
    String recovers= "CREATE TABLE \"recovers\" (\n" + "    \"name\"    TEXT UNIQUE,\n"
        + " PRIMARY KEY(\"name\")\n" + ");";

    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    }
    try (Connection connection = this.connect()) {
      if (connection != null) {
        DatabaseMetaData meta = connection.getMetaData();
        Statement statement = connection.createStatement();
        statement.execute(isCautious);
        statement.execute(isSocial);
        statement.execute(isIll);
        statement.execute(isResistant);
        statement.execute(pointTable);
        statement.execute(vaccinated);
        statement.execute(reinfected);
        statement.execute(dies);
        statement.execute(quarantine);
        statement.execute(recovers);
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

  public static void addIsResistant(String zName) {
    String resisPerson = "INSERT INTO is_resistant(name) VALUES(?)";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(resisPerson);) {
      addPerson.executeUpdate();
      addPerson.setString(1, zName);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  
  public static void addIsCautious(String hName) {
    String healthyPerson = "INSERT INTO is_cautious(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }
  public static void addIsSocial(String hName) {
    String healthyPerson = "INSERT INTO is_social(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }
  
  public static void addInQuarantine(String hName) {
    String healthyPerson = "INSERT INTO in_quarantine(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }

  public static void rmIsIll(String zName) {
    String rmIsIll = "DELETE FROM is_ill WHERE name=?";
    try (Connection connection = connect();
        PreparedStatement rmPerson = connection.prepareStatement(rmIsIll)) {
      rmPerson.setString(1, zName);
      rmPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  
  public static void rmIsCautious(String zName) {
    String rmIsIll = "DELETE FROM is_cautious WHERE name=?";
    try (Connection connection = connect();
        PreparedStatement rmPerson = connection.prepareStatement(rmIsIll)) {
      rmPerson.setString(1, zName);
      rmPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  public static void rmIsSocial(String zName) {
    String rmIsIll = "DELETE FROM is_social WHERE name=?";
    try (Connection connection = connect();
        PreparedStatement rmPerson = connection.prepareStatement(rmIsIll)) {
      rmPerson.setString(1, zName);
      rmPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  
  public static void rmIsResistant(String zName) {
    String rmIsIll = "DELETE FROM is_resistant WHERE name=?";
    try (Connection connection = connect();
        PreparedStatement rmPerson = connection.prepareStatement(rmIsIll)) {
      rmPerson.setString(1, zName);
      rmPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  
  public static void rmInQuarantine(String zName) {
    String rmIsIll = "DELETE FROM in_quarantine WHERE name=?";
    try (Connection connection = connect();
        PreparedStatement rmPerson = connection.prepareStatement(rmIsIll)) {
      rmPerson.setString(1, zName);
      rmPerson.executeUpdate();
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

  public static void addVaccinated(String hName) {
    String healthyPerson = "INSERT INTO vaccinated(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }
  public static void addReinfected(String hName) {
    String healthyPerson = "INSERT INTO reinfected(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }
  public static void addDies(String hName) {
    String healthyPerson = "INSERT INTO dies(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }
  public static void addQuarantine(String hName) {
    String healthyPerson = "INSERT INTO quarantine(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

  }
  public static void addRecovers(String hName) {
    String healthyPerson = "INSERT INTO recovers(name) VALUES(?);";
    try (Connection connection = connect();
        PreparedStatement addPerson = connection.prepareStatement(healthyPerson);) {
      addPerson.setString(1, hName);
      addPerson.executeUpdate();
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
