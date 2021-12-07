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
    super.moveTowards(partyPos, 4);
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);

    if (seed > 0.95) {
      super.vaccination();
    }
  }

}
