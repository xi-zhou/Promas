
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


public class Zombie extends Human{

  public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space,grid,hName);
  }
  

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    // get the grid location of this Human
    GridPoint pt = grid.getLocation(this);
    // use the GridCellNgh class to create GridCells for
    // the surrounding neighborhood.

    infect();
    GridPoint location =super.findLocation(grid,pt);
    super.moveTowards(location);
  }
  // call this method on every iteration of simulation
//  @ScheduledMethod(start = 1, interval = 1)
//  public void step() {
//    // get the grid location of this Zombie
//    GridPoint pt = grid.getLocation(this);
//
//    // use the GridCellNgh class to create GridCells for
//    // the surrounding neighborhood.
//    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, 4, 4);
//    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
//    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
//
//    GridPoint pointWithMostHumans =
//        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
//    
//    moveTowards(pointWithMostHumans);
//
//  }
  

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
    newInfection = Database.getNewInfection();

    System.out.println(this.name + " new infection list" + newInfection);


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
      System.out
          .println(name + " infection detected but not in this ngh,this ngh contains no human");
    }

  }
}
