package jzombies;

import java.util.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class CautiousHuman extends Human {

  public CautiousHuman(ContinuousSpace<Object> space, Grid<Object> grid, String hName) {
    super(space, grid, hName);
  }

  @ScheduledMethod(start = 1, interval = 1)
  public void run() {
    GridPoint pt = grid.getLocation(this);
    GridPoint location = findLocation(grid, pt);
    super.moveTowards(location);
  }

  @Override
  public GridPoint findLocation(Grid<Object> grid, GridPoint pt) {
    GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt, Human.class, 1, 1);
    List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
    GridPoint leastHumanPos = null;
    int minCount = Integer.MAX_VALUE;
    for (GridCell<Human> cell : gridCells) {
      if (cell.size() < minCount) {
        leastHumanPos = cell.getPoint();
        minCount = cell.size();
      }
    } ;
    return leastHumanPos;
  }
}
