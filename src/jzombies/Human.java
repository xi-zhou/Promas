package jzombies;

import java.util.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;


public class Human {

  private ContinuousSpace<Object> space;
  private Grid<Object> grid;
  private int energy, startingEnergy;
  final private String name;


  public Human(ContinuousSpace<Object> space, Grid<Object> grid, String hName, int energy) {
    this.space = space;
    this.grid = grid;
    this.name = hName;
    this.energy = startingEnergy = energy;

  }


  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    // get the grid location of this Human
    GridPoint pt = grid.getLocation(this);
    // use the GridCellNgh class to create GridCells for
    // the surrounding neighborhood.
    GridCellNgh<Zombie> nghCreator = new GridCellNgh<Zombie>(grid, pt, Zombie.class, 1, 1);
    List<GridCell<Zombie>> gridCells = nghCreator.getNeighborhood(true);
    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());


    GridPoint pointWithLeastZombies =
        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();

    if (energy > 0) {
      moveTowards(pointWithLeastZombies);
    } else {
      energy = startingEnergy;
    }
  }

  /**
   * Move to a random point in Moore Neighborhood and update position in dbs
   * 
   * @param pt random point
   */
  public void moveTowards(GridPoint pt) {
    // only move if we are not already in this grid location
    if (!pt.equals(grid.getLocation(this))) {
      NdPoint myPoint = space.getLocation(this);
      NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
      double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
      space.moveByVector(this, 2, angle, 0);
      myPoint = space.getLocation(this);
      grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
      Database.updatePoint(name, (int) myPoint.getX(), (int) myPoint.getY());
      energy--;
    }
  }

}
