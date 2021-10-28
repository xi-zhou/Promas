/**
 * 
 */
package jzombies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	public Zombie(ContinuousSpace<Object> space, Grid<Object> grid, String zName, Database dbs) {
		this.space = space;
		this.grid = grid;
		this.name=zName;
		this.dbs=dbs;
	}
	
	// call this method on every iteration of simulation
	@ScheduledMethod(start = 1, interval = 1) 
	public void step() {
		// get the grid location of this Zombie
		GridPoint pt = grid.getLocation(this);

		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		GridCellNgh<Human> nghCreator = new GridCellNgh<Human>(grid, pt,
				Human.class, 1, 1); // D<10 infection happens
		List<GridCell<Human>> gridCells = nghCreator.getNeighborhood(false);
		// when neigh contain equal number of human, random select one direction
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		// go to a random position in this ngh		
		GridPoint pointWithMostHumans = gridCells.get(RandomHelper.nextIntFromTo(0, gridCells.size() - 1)).getPoint();
		System.out.println("go to human"+pointWithMostHumans.getX()+pointWithMostHumans.getY());
		//		int maxCount = -1;
//		for (GridCell<Human> cell : gridCells) {
//			if (cell.size() > maxCount) {
//				pointWithMostHumans = cell.getPoint();
//				maxCount = cell.size();
//			}
//		}
		moveTowards(pointWithMostHumans);
		infect();
	}



	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this); //zoombie current pos
			// human pos in double
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY()); //convert Gridpoint to Nppoint
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 1, angle, 0); // zoombie moved in a angle
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY()); //convert stetig loc to int in the grid
			// after move update position
			System.out.println("at human"+myPoint.getX()+myPoint.getY());
			//dbs.updatePoint(this.name, myPoint.getX(), myPoint.getY());
			
			moved = true;
		}
	}

	public void infect() {
		ArrayList<String> infectedPerson = new ArrayList<String>();

			
		GridPoint pt = grid.getLocation(this);
		List<Object> humans = new ArrayList<Object>();
				
		//iterated through all,egal zombie or human.
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Human) {
				humans.add(obj);
			}
		}
		
		try {
			TransmissionModel trans = new TransmissionModel();
			infectedPerson=trans.getInfectedPerson();
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (humans.size() > 0) {
			for(int i = 0; i<humans.size();i++) {
				//random choose a human
				//int index = RandomHelper.nextIntFromTo(0, humans.size() - 1);
				Object obj = humans.get(i);
				String hName = null;
				try {
					hName = (String) FieldUtils.readField(obj, "name", true);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(infectedPerson.contains(hName)) {
					//convert continous local to double coordinate
					NdPoint spacePt = space.getLocation(obj);
					//get context that contain this random human
					Context<Object> context = ContextUtils.getContext(obj);
					//remove this human from context
					context.remove(obj);
					
					Zombie zombie = new Zombie(space, grid,hName, dbs);
					boolean isExist = dbs.checkExistIll(hName);
					if(!isExist) {
						dbs.addIsIll(infectedPerson.get(i));
					}
					
					
					context.add(zombie);
					//move zombie to former postion of human
					space.moveTo(zombie, spacePt.getX(), spacePt.getY());
					grid.moveTo(zombie, pt.getX(), pt.getY());
					
					Network<Object> net = (Network<Object>)context.getProjection("infection network");
					net.addEdge(this, zombie);
					
				}

			}

		}
	}
}
