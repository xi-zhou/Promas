package jzombies;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class SocialHuman extends Human {

  public SocialHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    GridPoint partyPos = SocietyModel.getPartyLocation(this.name);
    moveTowards(partyPos, 4);

    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);

    if (seed > 0.95) {
      vaccination();
    }
  }


}
