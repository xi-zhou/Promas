package epidemic;

import java.util.HashMap;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

/**
 * Quarantine agents can recover, die or remain isolated.
 */
public class Quarantine extends Human {

  public Quarantine(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {

    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);
    HashMap<String, Float> newDead = Database.getNewDead();
    HashMap<String, Float> newResistant = Database.getNewResistant();
    if (newResistant.containsKey(name) && (newResistant.get(name) >= seed)) {
      recover();
    } else if (newDead != null && newDead.containsKey(name) && (newDead.get(name) >= seed)) {
      dead();
    } else {
    }
  }

  private void recover() {
    Database.rmInQuarantine(name);
    Database.rmNewResistantFromList(name);
    Database.rmReinfected(name);
    Database.addRecovers(name);
    Database.addIsResistant(name);
    System.out.println(name + " after quarantine recovred");
    GridPoint pt = grid.getLocation(this);
    NdPoint spacePt = space.getLocation(this);
    Context<Object> context = ContextUtils.getContext(this);
    context.remove(this);

    Resistant human = new Resistant(space, grid, name);
    context.add(human);
    space.moveTo(human, spacePt.getX(), spacePt.getY());
    grid.moveTo(human, pt.getX(), pt.getY());

  }

  private void dead() {
    Database.rmInQuarantine(name);
    Database.rmNewDeadFromList(name);
    Database.addDies(name);

    System.out.println(name + " during quarantine dead");
    GridPoint pt = grid.getLocation(this);
    NdPoint spacePt = space.getLocation(this);
    Context<Object> context = ContextUtils.getContext(this);
    context.remove(this);

    Dead human = new Dead(space, grid, name);
    context.add(human);
    space.moveTo(human, spacePt.getX(), spacePt.getY());
    grid.moveTo(human, pt.getX(), pt.getY());

  }
}
