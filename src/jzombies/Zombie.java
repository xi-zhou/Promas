/**
 * 
 */
package jzombies;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import jep.JepException;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

/**
 * @author nick
 * 
 */
public class Zombie {

	private ContinuousSpace<Object> space; //n-dim coordonate
	private Grid<Object> grid; // query neigbhour
	private boolean moved;
	final private String name;
	final private Database dbs;
	private TransmissionModel trans ;

	public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, String zName, Database dbs) {
		this.space = space;
		this.grid = grid;
		this.name=zName;
		this.dbs=dbs;
		
		try {
			trans = TransmissionModel.create();
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// call this method on every iteration of simulation
	@ScheduledMethod(start = 1, interval = 1) 
	public void step() {
		// get the grid location of this Zombie
		GridPoint pt = grid.getLocation(this);

		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt,
				Human.class, 1, 1);
		List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		GridPoint pointWithMostHumans = gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size()-1)).getPoint();
		moveTowards(pointWithMostHumans);
		infect();
	}

	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
			dbs.updatePoint(name, myPoint.getX(), myPoint.getY());
			moved = true;
		}
	}

	public void infect() {
		ArrayList<String> infectedPerson = new ArrayList<String>();
		GridPoint pt = grid.getLocation(this);
		List<Object> humans = new ArrayList<Object>();
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Human) {
				humans.add(obj);
			}
		}
		
		try {
		//trans= TransmissionModel.create();
		trans.loadModel();
		trans.getResFromJep();
		infectedPerson=trans.getInfectedPerson();
		trans.closeJep();
		} catch (JepException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}


		if (humans.size() > 0) {
			for(int i = 0; i<humans.size();i++) {

			Object obj = humans.get(i);
			String hName = null;
			try {
				hName = (String) FieldUtils.readField(obj, "name", true);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(infectedPerson.contains(hName)) {
				
			
			NdPoint spacePt = space.getLocation(obj);
			Context<Object> context = ContextUtils.getContext(obj);
			context.remove(obj);
			
			Zombie zombie = new Zombie(space, grid, name, dbs);

			boolean isExist = dbs.checkExistIll(hName);
			if(!isExist) {
				dbs.addIsIll(infectedPerson.get(i));
				System.out.println("new infection"+hName);
			}
			context.add(zombie);
			space.moveTo(zombie, spacePt.getX(), spacePt.getY());
			grid.moveTo(zombie, pt.getX(), pt.getY());
			
			Network<Object> net = (Network<Object>)context.getProjection("infection network");
			net.addEdge(this, zombie);
		}
			}
		}
	}
}
