package jzombies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.reflect.FieldUtils;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.GridPoint;

public final class SocietyModel {
  private static List<SocialHuman> socialHuman= new ArrayList<SocialHuman>();
  private static List<Human> cautiousHuman= new ArrayList<Human>();
  private static Map<Integer, List<SocialHuman>> group =new HashMap<Integer, List<SocialHuman>>();
  final static int numOfGroups = 4;
  static GridPoint partyLocation;
  
  public static GridPoint getPartyLocation() {
    return partyLocation;
  }

  private SocietyModel() {}
  
  static SocietyModel create() {
    return new SocietyModel();
  }
  
  static void addSocialHuman(SocialHuman human) {
    socialHuman.add(human);
  }
  

  public static void findPartyLocation(ContinuousSpace<Object> space,SocialHuman human) {
    int grpId = 0;
    List<SocialHuman> friends = new ArrayList<>();
    double[] friendsPoint;
    List<Double> xPos = new ArrayList<>();
    List<Double> yPos = new ArrayList<>();
    
    for (Entry<Integer,List<SocialHuman>> entry : group.entrySet()) {
      if (entry.getValue().contains(human)) {
         friends=entry.getValue();
         grpId= entry.getKey();
         human.setGroupID(grpId);
        //System.out.println("this human is in group: "+grpId);
    }
  }
    

    for(Human human1:friends) {
      Double x,y;
      x=space.getLocation(human).getX();
      y=space.getLocation(human).getY();
      xPos.add(x);
      yPos.add(y);
      }
    
    //Collections.max(Arrays.asList(d));
    double xMax = xPos.stream().mapToDouble(Double::doubleValue).max().getAsDouble();//16.0
    double xMin = xPos.stream().mapToDouble(Double::doubleValue).min().getAsDouble();//-6.0
    double yMax = yPos.stream().mapToDouble(Double::doubleValue).max().getAsDouble();//16.0
    double yMin = yPos.stream().mapToDouble(Double::doubleValue).min().getAsDouble();//-6.0  


    double centerY = (yMax+yMin ) / 2;
    double centerX = (xMax+xMin )/2;
    
    partyLocation = new GridPoint((int) centerY,(int) centerX);
    System.out.println(grpId+" meeting at "+(int) centerY+" "+(int) centerX);
    //return point;
  }

  public static void addCautiousHuman(Human human) {
    cautiousHuman.add(human);    
  }
  

public static Map<Integer, List<SocialHuman>> group() {
  
  for (int i = 0; i < numOfGroups; ++i) {
      //parts.add(new ArrayList<>());
      group.put(i, new ArrayList<>());
  }
  final int N = socialHuman.size();
  for (int i = 0; i < N; ++i) {
      group.get(i % numOfGroups).add(socialHuman.get(i));
  }
  return group;
//  for (Entry<Integer,List<Human>> entry : group.entrySet()) {
//      System.out.println(entry.getKey()+" : "+entry.getValue());
//  }

}

public static void organizeParty(Map<Integer, List<SocialHuman>> group2) {
  // TODO Auto-generated method stub
  
}

}
