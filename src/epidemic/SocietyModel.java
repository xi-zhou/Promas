package epidemic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.GridPoint;

/**
 * Grouping agents base on their characteristic and form party within social agents as cluster.
 */
public final class SocietyModel {
  private static List<SocialHuman> socialHuman = new ArrayList<SocialHuman>();
  private static List<CautiousHuman> cautiousHuman = new ArrayList<CautiousHuman>();
  private static Map<Integer, List<SocialHuman>> group = new HashMap<Integer, List<SocialHuman>>();
  private static Map<String, GridPoint> partyLocationMap = new HashMap<String, GridPoint>();

  /**
   * @return a many to one relation map, contains all social agent name and their party location
   */
  public static Map<String, GridPoint> getPartyLocationMap() {
    return partyLocationMap;
  }

  public static GridPoint getPartyLocation(String name) {
    GridPoint pt = partyLocationMap.get(name);
    return pt;
  }

  public static Map<Integer, List<SocialHuman>> getGroup() {
    return group;
  }

  private SocietyModel() {}

  static SocietyModel create() {
    return new SocietyModel();
  }

  static void addSocialHuman(SocialHuman human) {
    socialHuman.add(human);
  }

  static void addCautiousHuman(CautiousHuman human) {
    cautiousHuman.add(human);
  }

  public static boolean isSocial(String name) {
    for (SocialHuman human : socialHuman) {
      if (human.name.equals(name)) {
        return true;
      }
    }
    return false;

  }

  public static boolean isCautious(String name) {
    for (CautiousHuman human : cautiousHuman) {
      if (human.name.equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * assign social human into different group.
   * 
   * @param numGrp
   * 
   * @return a map, contains unique groupID and the agents in it.
   */
  public static Map<Integer, List<SocialHuman>> group(int numGrp) {

    for (int i = 0; i < numGrp; ++i) {
      group.put(i, new ArrayList<>());
    }
    final int N = socialHuman.size();
    for (int i = 0; i < N; ++i) {
      group.get(i % numGrp).add(socialHuman.get(i));
    }
    return group;

  }

  /**
   * for each group find a party location
   * 
   * @param space
   * @param group map with one to many relationship
   * @param useRanLoc if to use a random location
   */
  public static void organizeParty(ContinuousSpace<Object> space,
      Map<Integer, List<SocialHuman>> group, boolean useRanLoc) {
    List<SocialHuman> friends = new ArrayList<>();
    int grpId;

    for (Entry<Integer, List<SocialHuman>> entry : group.entrySet()) {
      friends = entry.getValue();
      grpId = entry.getKey();
      findPartyLocation(space, grpId, friends, useRanLoc);
    }
  }

  /**
   * find partylocation for A group of social person, if NOT use a random location then compute the
   * center of agent group.
   * 
   * @param space
   * @param grpId unique group id
   * @param friends list of human in a group
   * @param useRanLoc if to use a random location
   */
  private static void findPartyLocation(ContinuousSpace<Object> space, int grpId,
      List<SocialHuman> friends, boolean useRanLoc) {
    GridPoint partyLocation;

    if (useRanLoc) {
      double x = RandomHelper.nextDoubleFromTo(0.0, space.getDimensions().getWidth());
      double y = RandomHelper.nextDoubleFromTo(0.0, space.getDimensions().getHeight());
      partyLocation = new GridPoint((int) x, (int) y);
    } else {
      List<Double> xPos = new ArrayList<>();

      List<Double> yPos = new ArrayList<>();

      for (SocialHuman human : friends) {
        Double x, y;
        x = space.getLocation(human).getX();
        y = space.getLocation(human).getY();
        xPos.add(x);
        yPos.add(y);
      }
      double xMax = xPos.stream().mapToDouble(Double::doubleValue).max().getAsDouble();// 16.0
      double xMin = xPos.stream().mapToDouble(Double::doubleValue).min().getAsDouble();// -6.0
      double yMax = yPos.stream().mapToDouble(Double::doubleValue).max().getAsDouble();// 16.0
      double yMin = yPos.stream().mapToDouble(Double::doubleValue).min().getAsDouble();// -6.0


      double centerY = (yMax + yMin) / 2;
      double centerX = (xMax + xMin) / 2;

      partyLocation = new GridPoint((int) centerY, (int) centerX);
    }

    for (SocialHuman human : friends) {
      partyLocationMap.put(human.name, partyLocation);
    }

    System.out.println(
        grpId + " meeting at " + partyLocation.getX() + " " + partyLocation.getY());
  }


}
