package jzombies;

import java.util.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class SocialHuman extends Human{
  private int groupID;
  private GridPoint partyPos;
  
  public void setPartyPos(GridPoint partyPos) {
    this.partyPos = partyPos;
  }

  public void setGroupID(int groupID) {
    this.groupID = groupID;
  }

  public SocialHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space,grid,hName);
    // TODO Auto-generated constructor stub
  }
  
  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    // get the grid location of this Human
    GridPoint pt = grid.getLocation(this);
    // use the GridCellNgh class to create GridCells for
    // the surrounding neighborhood.

    
    GridPoint location =findLocation(grid,pt);
    super.moveTowards(location);
  }
  
  @Override
  public GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
        //SocietyModel.findPartyLocation(space, this);
        GridPoint partyLocation= SocietyModel.getPartyLocation();
  return partyLocation;
  }
}
