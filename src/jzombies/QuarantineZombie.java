package jzombies;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class QuarantineZombie extends Human {

  public QuarantineZombie(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }
  
  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    if (Database.getNewResistant().contains(name)) {
      recover();
    }else if (Database.getNewDead().contains(name)) {
      dead();
    }
  }

  public void recover() {
    Database.rmInQuarantine(name);
    Database.rmNewResistantFromList(name);
    Database.addRecovers(name);
      Database.addIsResistant(name);
      System.out.println(name + " after quarantine recovred");
      GridPoint pt = grid.getLocation(this);
      NdPoint spacePt = space.getLocation(this);
      Context<Object> context = ContextUtils.getContext(this);
      context.remove(this);

      ResistanceHuman human = new ResistanceHuman(space, grid, name);
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

    DeadZombie human = new DeadZombie(space, grid, name);
    context.add(human);
    space.moveTo(human, spacePt.getX(), spacePt.getY());
    grid.moveTo(human, pt.getX(), pt.getY());

  }
}
