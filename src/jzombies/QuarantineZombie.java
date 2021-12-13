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

  @ScheduledMethod(start = 2.5, interval = 2)
  public void recover() {
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);
    if (seed > 0.99) {
      dead();
    } else if (seed > 0.7) {
      Database.rmIsIll(name);
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
  }

  private void dead() {
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
