package epidemic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * Build the context e.g. Grid, continuous space, projection, charts and initialize the dbs with
 * random agent name.
 */
public class SimBuilder implements ContextBuilder<Object> {

  @Override
  public Context build(Context<Object> context) {
    context.setId("epidemic");

    NetworkBuilder<Object> netBuilder =
        new NetworkBuilder<Object>("infection network", context, true);
    netBuilder.buildNetwork();

    Parameters params = RunEnvironment.getInstance().getParameters();
    int gridSizeX = (Integer) params.getValue("grid_size_x");
    int gridSizeY = (Integer) params.getValue("grid_size_y");

    ContinuousSpaceFactory spaceFactory =
        ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);

    ContinuousSpace<Object> space =
        spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(),
            new repast.simphony.space.continuous.WrapAroundBorders(), gridSizeX, gridSizeY);

    GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
    Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
        new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, gridSizeX, gridSizeY));

    Database.create();
    SocietyModel.create();
    int zombieCount = (Integer) params.getValue("zombie_count");
    for (int i = 0; i < zombieCount; i++) {
      String zName = RandomStringUtils.random(8, true, true);
      Database.addIsIll(zName);
      context.add(new Infected(space, grid, zName));
    }

    int socialHumanCount = (Integer) params.getValue("social_human_count");
    int cautiousHumanCount = (Integer) params.getValue("cautious_human_count");
    int resistanceHumanCount = (Integer) params.getValue("resistance_human_count");

    for (int i = 0; i < resistanceHumanCount; i++) {
      String hName = RandomStringUtils.random(8, true, true);
      Database.addIsResistant(hName);
      Resistant resistanceHuman = new Resistant(space, grid, hName);
      context.add(resistanceHuman);
    }

    for (int i = 0; i < socialHumanCount; i++) {
      String hName = RandomStringUtils.random(8, true, true);
      Database.addIsSocial(hName);
      SocialHuman socialHuman = new SocialHuman(space, grid, hName);
      SocietyModel.addSocialHuman(socialHuman);
      context.add(socialHuman);
    }
    Map<Integer, List<SocialHuman>> group = new HashMap<Integer, List<SocialHuman>>();
    int numGrp = (Integer) params.getValue("numGrp");
    boolean useCenterLoc = (Boolean) params.getValue("use_center_loc");
    group = SocietyModel.group(numGrp);
    SocietyModel.organizeParty(space, group, useCenterLoc);

    for (int i = 0; i < cautiousHumanCount; i++) {
      String hName = RandomStringUtils.random(8, true, true);

      Database.addIsCautious(hName);
      CautiousHuman cautiousHuman = new CautiousHuman(space, grid, hName);
      SocietyModel.addCautiousHuman(cautiousHuman);
      context.add(cautiousHuman);
    }

    // move agent to grid location that corresponds to continouspace location
    for (Object obj : context.getObjects(Object.class)) {
      NdPoint pt = space.getLocation(obj);
      grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
      try {
        String name = (String) FieldUtils.readField(obj, "name", true);
        Database.addPoint(name, pt.getX(), pt.getY());
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    if (RunEnvironment.getInstance().isBatch()) {
      RunEnvironment.getInstance().endAt(20);
    }

    return context;

  }
}
