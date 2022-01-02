package jzombies;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class ResistanceHuman extends Human {
  public ResistanceHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    RandomHelper.nextDoubleFromTo(0.0, 1.0);
    GridPoint pt = grid.getLocation(this);
    GridPoint location;

    if (SocietyModel.isSocial(this.name)) {
      location = SocietyModel.getPartyLocation(this.name);
    } else {
      location = findLocation(grid, pt);
    }
    moveTowards(location, 5);
  }
}
