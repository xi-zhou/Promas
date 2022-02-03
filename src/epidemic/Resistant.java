package epidemic;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * social agent will retain their destination, other resistant agents move 5 units per tick to
 * random location.
 */
public class Resistant extends Human {
  public Resistant(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
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
