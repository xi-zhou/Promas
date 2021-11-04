package jzombies;

import java.lang.reflect.*;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import jep.JepException;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;


// initialize the simulation
public class JZombiesBuilder implements ContextBuilder<Object> {
  // Database dbs;

  // build a context for every agents and agentstype.
  @Override
  public Context build(Context<Object> context) {
    context.setId("jzombies"); // ID should be project name.
    Database.create();
    // build a infection network, used in infect() from zombie class.
    NetworkBuilder<Object> netBuilder =
        new NetworkBuilder<Object>("infection network", context, true);
    netBuilder.buildNetwork();


    // use factory method,avoid using constrcutor.
    ContinuousSpaceFactory spaceFactory =
        ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
    // use factory to create instance, create continuousSpace named space,every object added in
    // here.
    // name;context;adder with random location; border of grid;dimension.
    ContinuousSpace<Object> space =
        spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(),
            new repast.simphony.space.continuous.WrapAroundBorders(), 20, 20);


    GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
    // true means two object in the same grid.
    // SimpleGridAdder: not give location,but via build().
    Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
        new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 20, 20)); // IF FALSE null
                                                                                // pointer inital

    // create agents.
    Parameters params = RunEnvironment.getInstance().getParameters();
    int zombieCount = (Integer) params.getValue("zombie_count");
    for (int i = 0; i < zombieCount; i++) {
      String zName = RandomStringUtils.random(8, true, true);
      Database.addPerson(zName);
      Database.addIsIll(zName);

      context.add(new Zombie(space, grid, zName));
    }

    int humanCount = (Integer) params.getValue("human_count");
    for (int i = 0; i < humanCount; i++) {
      int energy = RandomHelper.nextIntFromTo(4, 10);
      String hName = RandomStringUtils.random(8, true, true);

      Database.addPerson(hName);
      context.add(new Human(space, grid, hName, energy));
    }

    // move agent to grid location that corresponds to continouspace location
    for (Object obj : context.getObjects(Object.class)) {
      NdPoint pt = space.getLocation(obj);
      grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
      try {
        String name = (String) FieldUtils.readField(obj, "name", true);
        Database.addPoint(name, (int) pt.getX(), (int) pt.getY());
      } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    if (RunEnvironment.getInstance().isBatch()) {
      RunEnvironment.getInstance().endAt(20);
    }

    return context;

  }
}
