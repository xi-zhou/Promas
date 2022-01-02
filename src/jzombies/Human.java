package jzombies;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


public class Human {

  protected ContinuousSpace<Object> space;
  protected Grid<Object> grid;
  protected String name;
  protected static int infectionRadius;

  public Human(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    this.space = space;
    this.grid = grid;
    this.name = hName;
  }

  public static void setInfectionRadius(int infectionRadius) {
    Human.infectionRadius = infectionRadius;
  }

  protected GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, 1, 1);
    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
    SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
    GridPoint randomPos =
        gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
    return randomPos;
  }

  public ContinuousSpace<Object> getSpace() {
    return space;
  }

  public Grid<Object> getGrid() {
    return grid;
  }


  /**
   * Move to a random point in Moore Neighborhood and update position in dbs
   * 
   * @param pt random point
   */
  protected void moveTowards(GridPoint pt, double distance) {
    // only move if we are not already in this grid location
    if (!pt.equals(grid.getLocation(this))) {
      NdPoint myPoint = space.getLocation(this);
      NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
      double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
      space.moveByVector(this, distance, angle, 0);
      myPoint = space.getLocation(this);
      grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
      Database.updatePoint(name, myPoint.getX(), myPoint.getY());
    }
  }


  protected void vaccination() {
    Database.addIsResistant(name);
    Database.addVaccinated(name);
    Database.rmNewResistantFromList(name);
    System.out.println(name + " is vaccinated");
    GridPoint pt = grid.getLocation(this);
    NdPoint spacePt = space.getLocation(this);
    Context<Object> context = ContextUtils.getContext(this);
    context.remove(this);

    ResistanceHuman human = new ResistanceHuman(space, grid, name);
    context.add(human);
    space.moveTo(human, spacePt.getX(), spacePt.getY());
    grid.moveTo(human, pt.getX(), pt.getY());

  }

  protected List<GridCell<Human>> getNgh(Grid<Object> grid, GridPoint pt, int size) {
    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, size, size);
    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
    return gridCells;
  }

  protected <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
    Set<T> keys = new HashSet<T>();
    for (Entry<T, E> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        keys.add(entry.getKey());
      }
    }
    return keys;
  }

}
