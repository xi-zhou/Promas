
package jzombies;

import java.util.ArrayList;
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


public class Zombie {

  private ContinuousSpace<Object> space; // n-dim coordonate
  private Grid<Object> grid; // query neigbhour
  private boolean moved;
  final private String name;
  final private Database dbs;
  // private TransmissionModel trans;

  public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, String zName, Database dbs) {
    this.space = space;
    this.grid = grid;
    this.name = zName;
    this.dbs = dbs;

    // try {
    // trans = TransmissionModel.create();
    // } catch (JepException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }

  // call this method on every iteration of simulation
  @ScheduledMethod(start = 1, interval = 1)
  public void step() {
    // get the grid location of this Zombie
    GridPoint pt = grid.getLocation(this);

    // use the GridCellNgh class to create GridCells for
    // the surrounding neighborhood.
    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, 4, 4);
    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

    GridPoint pointWithMostHumans =
        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
    moveTowards(pointWithMostHumans);
    infect();
  }

  /**
   * Move to a random point in Moore Neighborhood and update position in dbs
   * 
   * @param pt a random point
   */
  public void moveTowards(GridPoint pt) {
    // only move if we are not already in this grid location
    if (!pt.equals(grid.getLocation(this))) {
      NdPoint myPoint = space.getLocation(this);
      NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
      double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
      space.moveByVector(this, 1, angle, 0);
      myPoint = space.getLocation(this);
      grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
      dbs.updatePoint(name, (int) myPoint.getX(), (int) myPoint.getY());
      moved = true;
    }
  }

  public void infect() {
    ArrayList<String> modelAllInfection = new ArrayList<String>();
    ArrayList<String> newInfection = new ArrayList<String>();

    GridPoint pt = grid.getLocation(this);
    List<Object> humans = new ArrayList<Object>();
    // get all humans at zombies'grid
    for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
      if (obj instanceof Human) {
        humans.add(obj);
      }
    }


    modelAllInfection = TransmissionModel.getInfectedPerson();

    newInfection = dbs.findNewInfected(modelAllInfection);

    System.out.println(name + " new infection list" + newInfection);


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
        // System.out.println("human name" + hName);
        if (newInfection.contains(hName)) {
          dbs.addIsIll(hName);
          System.out.println("Infecting" + hName);
          NdPoint spacePt = space.getLocation(obj);
          Context<Object> context = ContextUtils.getContext(obj);
          context.remove(obj);

          Zombie zombie = new Zombie(space, grid, hName, dbs);
          context.add(zombie);
          space.moveTo(zombie, spacePt.getX(), spacePt.getY());
          grid.moveTo(zombie, pt.getX(), pt.getY());

          Network<Object> net = (Network<Object>) context.getProjection("infection network");
          net.addEdge(this, zombie);

        }
      }
    } else {
      System.out
          .println(name + " infection detected but not in this ngh,this ngh contains no human");
    }

  }
}
