package epidemic;

import java.util.HashMap;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

/**
 * susceptible social agents move 4 units per tick and tend to gather at a party location
 * {@link SocietyModel#organizeParty(ContinuousSpace, java.util.Map, boolean)}.
 */
public class SocialHuman extends Human {

  public SocialHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);

    GridPoint partyPos = SocietyModel.getPartyLocation(this.name);
    moveTowards(partyPos, 4);
    HashMap<String, Float> newResistant = Database.getNewResistant();
    if (newResistant.containsKey(name) && (newResistant.get(name) >= seed)) {
      Database.rmIsSocial(name);
      vaccination();
    }

  }
}
