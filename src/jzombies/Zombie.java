
package jzombies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import jep.JepException;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


public class Zombie extends Human {
  private List<GridCell<Human>> gridCells;

  public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {

    GridPoint pt = grid.getLocation(this);
    gridCells = getNgh(grid, pt);
    
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);
    if (seed > 0.8) {
      quarantine();
    }else {
      infect(gridCells);
      GridPoint location = findLocation(grid, pt);
      super.moveTowards(location);
    }   

  }

  private void quarantine() {
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

  private List<GridCell<Human>> getNgh(Grid<Object> grid, GridPoint pt) {
    GridCellNgh<Human> nghCreator =
        new GridCellNgh<Human>(grid, pt, Human.class, super.infectionRadius, super.infectionRadius);
    gridCells = nghCreator.getNeighborhood(true);
    return gridCells;
  }

  @Override
  public GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
    GridPoint randomPos =
        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
    return randomPos;
  }

  public static <E> Collection<E> makeCollection(Iterable<E> iter) {
    Collection<E> list = new ArrayList<E>();
    for (E item : iter) {
      list.add(item);
    }
    return list;
  }

  public void infect(List<GridCell<Human>> gridCells) {
    ArrayList<String> newInfection = new ArrayList<String>();

    GridPoint pt = grid.getLocation(this);
    List<Object> humans = new ArrayList<Object>();

    // get all humans at zombies'grid
    Collection<Object> objInList = new ArrayList<Object>();

    for (GridCell cell : gridCells) {
      objInList = makeCollection(cell.items());
      for (Object obj : objInList) {
        if (obj instanceof SocialHuman || obj instanceof CautiousHuman) {
          humans.add(obj);
        }
      }
    }

    newInfection = Database.getNewInfection();

    // System.out.println(this.name + " new infection list" + newInfection);

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

          System.out.println("Infecting" + hName);
          Database.removeFromList(hName);
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
      // .println(name + " infection detected but not in this ngh,this ngh contains no human, or
      // resistance eist");
    }

  }

  @ScheduledMethod(start = 2.5, interval = 1)
  public void recover() {
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);
    if (seed > 0.9) {
      Database.removeIllPerson(name);
      Database.addResistance(name);
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
  }

}
