package jzombies;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class ResistanceHuman extends Human{
  public ResistanceHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space,grid,hName);
    // TODO Auto-generated constructor stub
  }
  
  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    // get the grid location of this Human
    GridPoint pt = grid.getLocation(this);
    // use the GridCellNgh class to create GridCells for
    // the surrounding neighborhood.

    
    GridPoint location =super.findLocation(grid,pt);
    super.moveTowards(location);
  }



}
