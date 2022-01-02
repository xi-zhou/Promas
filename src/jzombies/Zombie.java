
package jzombies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


public class Zombie extends Human {

  public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);

    GridPoint pt = grid.getLocation(this);
    List<GridCell<Human>> gridCells = getNgh(grid, pt, super.infectionRadius);

    HashMap<String, Float> newResistant = Database.getNewResistant();
    HashMap<String, Float> newQuarantine = Database.getNewQuarantine();
    if (newQuarantine.containsKey(name) && (newQuarantine.get(name) >= seed)) {
      quarantine();
    } else if (newResistant.containsKey(name) && (newResistant.get(name) >= seed)) {
      recover();
    } else {
      infect(gridCells);
      GridPoint location = findLocation(grid, pt);
      moveTowards(location, 3);
    }
  }

  private void quarantine() {
    Database.rmIsIll(name);
    Database.rmQuarantineFromList(name);
    Database.addInQuarantine(name);

    System.out.println(name + " is in quarantine");
    GridPoint pt = grid.getLocation(this);
    NdPoint spacePt = space.getLocation(this);
    Context<Object> context = ContextUtils.getContext(this);
    context.remove(this);

    QuarantineZombie human = new QuarantineZombie(space, grid, name);
    context.add(human);
    space.moveTo(human, spacePt.getX(), spacePt.getY());
    grid.moveTo(human, pt.getX(), pt.getY());

  }

  /** find next location depend on different agent type */
  @Override
  public GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
    List<GridCell<Human>> ngh = getNgh(grid, pt, 1);
    if (SocietyModel.isSocial(this.name)) {
      GridPoint partyPos = SocietyModel.getPartyLocation(this.name);
      return partyPos;
    } else if (SocietyModel.isCautious(this.name)) {
      GridPoint leastHumanPos = null;
      int minCount = Integer.MAX_VALUE;
      for (GridCell<Human> cell : ngh) {
        if (cell.size() < minCount) {
          leastHumanPos = cell.getPoint();
          minCount = cell.size();
        }
      } ;
      return leastHumanPos;
    } else {
      SimUtilities.shuffle(ngh, RandomHelper.getUniform());
      GridPoint randomPos = ngh.get(RandomHelper.nextIntFromTo(0, ngh.size() - 1)).getPoint();
      return randomPos;
    }
  }

  private static <E> Collection<E> makeCollection(Iterable<E> iter) {
    Collection<E> list = new ArrayList<E>();
    for (E item : iter) {
      list.add(item);
    }
    return list;
  }

  private void infect(List<GridCell<Human>> gridCells) {
    ArrayList<String> newInfection = new ArrayList<String>();
    GridPoint pt = grid.getLocation(this);
    ArrayList<Object> humans = new ArrayList<Object>();

    humans = humanInNgh(gridCells);
    newInfection = Database.getNewInfection();

    // System.out.println("new and reinfection: "+newInfection);
    // for each zombie check if there are humans in its moore neighborhood, if yes than for each
    // human check if there name in the new infection list,if yes add name to isIll
    if (humans.size() > 0) {

      for (int i = 0; i < humans.size(); i++) {

        Object obj = humans.get(i);
        String hName = null;
        try {
          hName = (String) FieldUtils.readField(obj, "name", true);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        if (newInfection.contains(hName)) {
          Database.addIsIll(hName);
          if (obj instanceof ResistanceHuman) {
            System.out.println(hName + " reinfection");
            Database.addReinfected(hName);
            Database.rmIsResistant(hName);
            Database.rmRecovers(hName);
          } else if (obj instanceof CautiousHuman) {
            Database.rmIsCautious(hName);
            System.out.println("Infecting " + hName);
          } else {
            System.out.println("Infecting " + hName);
            Database.rmIsSocial(hName);
          }

          Database.rmNewInfectFromList(hName);

          NdPoint spacePt = space.getLocation(obj);
          Context<Object> context = ContextUtils.getContext(obj);
          context.remove(obj);

          Zombie zombie = new Zombie(space, grid, hName);
          context.add(zombie);
          space.moveTo(zombie, spacePt.getX(), spacePt.getY());
          grid.moveTo(zombie, pt.getX(), pt.getY());

          Network<Object> net = (Network<Object>) context.getProjection("infection network");
          net.addEdge(this, zombie);

        }
      }
    } else {
      // System.out
      // .println(name + " infection detected but not in this ngh,this ngh contains no human, or"+
      // "resistance eist");
    }

  }


  private void recover() {
    Database.addRecovers(name);
    Database.addIsResistant(name);
    Database.rmIsIll(name);
    Database.rmNewResistantFromList(name);
    Database.rmReinfected(name);
    System.out.println(name + " is recovred");
    GridPoint pt = grid.getLocation(this);
    NdPoint spacePt = space.getLocation(this);
    Context<Object> context = ContextUtils.getContext(this);
    context.remove(this);

    ResistanceHuman human = new ResistanceHuman(space, grid, name);
    context.add(human);
    space.moveTo(human, spacePt.getX(), spacePt.getY());
    grid.moveTo(human, pt.getX(), pt.getY());
  }

  private ArrayList<Object> humanInNgh(List<GridCell<Human>> gridCells) {
    ArrayList<Object> humans = new ArrayList<Object>();
    Collection<Object> objInList = new ArrayList<Object>();

    for (GridCell cell : gridCells) {
      objInList = makeCollection(cell.items());
      for (Object obj : objInList) {
        if (obj instanceof SocialHuman || obj instanceof CautiousHuman
            || obj instanceof ResistanceHuman) {
          humans.add(obj);
        }
      }
    }
    return humans;
  }
}
