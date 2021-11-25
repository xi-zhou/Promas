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

public class SocialHuman extends Human {
  private int groupID;
  private GridPoint partyPos;

  public void setPartyPos(GridPoint partyPos) {
    this.partyPos = partyPos;
  }

  public void setGroupID(int groupID) {
    this.groupID = groupID;
  }

  public SocialHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    GridPoint pt = grid.getLocation(this);
    // GridPoint location = findLocation(grid, pt);
    // super.moveTowards(location);
    super.moveTowards(partyPos);
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);

    if(seed>0.9) {
      super.vaccination();
      }
  }

  @Override
  public GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, 1, 1);
    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
    GridPoint mostHumanPos = null;
    int maxCount = Integer.MIN_VALUE;
    for (GridCell<Human> cell : gridCells) {
      if (cell.size() > maxCount) {
        mostHumanPos = cell.getPoint();
        maxCount = cell.size();
      }
    } ;
    return mostHumanPos;
  }


}
