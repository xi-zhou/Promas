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

  protected ContinuousSpace<Object> space;
  protected Grid<Object> grid;
 protected String name;


  public Human(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    this.space = space;
    this.grid = grid;
    this.name = hName;
  }



//    GridPoint pointWithLeastZombies =
//        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
    
//    if(isSocial) {
//
//     // GridPoint partyLocation= SocietyModel.findPartyLocation(space, this);

//      //moveTowards(partyLocation);
//      moveTowards(SocietyModel.getPartyLocation());
//    }else {
//      
//      GridPoint pointWithLeastZombies = null;
//      int minCount = Integer.MAX_VALUE;
//      for (GridCell<Zombie> cell : gridCells) {
//          if (cell.size() < minCount) {
//              pointWithLeastZombies = cell.getPoint();
//              minCount = cell.size();
//          }
//      }
//
//      moveTowards(pointWithLeastZombies);
//    }
    
  



  public GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, 1, 1);
    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
    GridPoint randomPos =
        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
  return randomPos;
  }


  public ContinuousSpace<Object> getSpace() {
    return space;
  }


  public Grid<Object> getGrid() {
    return grid;
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
      Database.updatePoint(name,myPoint.getX(), myPoint.getY());
    }
  }

}
