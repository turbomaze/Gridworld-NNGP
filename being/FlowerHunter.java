package being;
import java.util.ArrayList;
import info.gridworld.actor.Actor;
import info.gridworld.actor.Flower;
import info.gridworld.actor.Critter;
import info.gridworld.grid.Location;
import neuralnetwork.NNetwork;

public class FlowerHunter extends Critter {
	//////////
	//config//
	private final static double mutateRate = 0.02; //2% of all weights/thresholds
	private final static int numInputs = 3*8+1; //(2coords+1piece of info)/neighbor + randomness
	private final static int numLayers = 2;
	private final static int numPerLayer = 3;
	private final static int numOutputs = 3; //one 3bit number

	private NNetwork brain;

	public FlowerHunter() {
		//////////////////////////////////
		//get a random, reasonable brain//
		this.brain = new NNetwork(numInputs, numLayers, numPerLayer, numOutputs);
		double[] newWts = brain.getWeights();
		for (int ai = 0; ai < newWts.length; ai++) {
			newWts[ai] = 2*Math.random() - 1; // in [-1, 1)
		}
		double[] newThrs = brain.getThresholds();
		for (int ai = 0; ai < newThrs.length; ai++) {
			newWts[ai] = 20*Math.random() - 10; // in [-10, 10)
		}
		this.brain.setWeights(newWts);
		this.brain.setThresholds(newThrs);
	}
	
	//the weights and thresholds MUST conform to the NN size in the config
	private FlowerHunter(double[] weights, double[] thresholds) {
		this.brain = new NNetwork(numInputs, numLayers, numOutputs, weights, thresholds);
	}
	
	public FlowerHunter mutate() { //returns a new FlowerHunter with a slightly diff brain
		double[] newWts = brain.getWeights();
		for (int ai = 0; ai < newWts.length; ai++) {
			if (Math.random() < mutateRate) newWts[ai] = mutateDouble(newWts[ai]);
		}
		double[] newThrs = brain.getThresholds();
		for (int ai = 0; ai < newThrs.length; ai++) {
			if (Math.random() < mutateRate) newThrs[ai] = mutateDouble(newThrs[ai]);
		}
		return new FlowerHunter(newWts, newThrs);
	}
	
	public double mutateDouble(double d) {
		double switcher = Math.random();
		if (switcher < 0.2) {
			return d*(1+0.1*Math.random()); //scale up to 10% up
		} else if (switcher < 0.4) {
			return d*(1-0.1*Math.random()); //scale up to 10% down
		} else if (switcher < 0.6) {
			return d+0.1; //add 0.1
		} else if (switcher < 0.8) {
			return d-0.1; //subtract 0.1
		} else {
			return -d; //negate
		}
	}
	
	public void processActors(ArrayList<Actor> actors) {
		//DO NOTHING!
		//eating other creatures is taken care of by selectMoveLocation and the brain
		//you aren't allowed to modify Critter.act() so this is the only way
	}
	
	public ArrayList<Location> getMoveLocations() { //get the sensical locations
		ArrayList<Location> locs = new ArrayList<Location>();
		
		//the empties are all cool
		ArrayList<Location> empties = getGrid().getEmptyAdjacentLocations(getLocation());
		for (Location l : empties) if (l != null) locs.add(l);

		//and the locations of edible neighbors ONLY
		ArrayList<Actor> neighbors = getGrid().getNeighbors(getLocation());
		for (Actor a : neighbors) {
			if (a != null && a instanceof Flower) {
				Location actorLoc = a.getLocation();
				if (actorLoc != null) locs.add(actorLoc);			
			}
		}

        return locs;
    }
	
    public Location selectMoveLocation(ArrayList<Location> locs) {
    	///////////////////////
    	//load the input info//
    	//idx of North=0; each dir gets 3; clockwise order
    	double[] inputs = new double[numInputs];
    	Location currentLoc = getLocation();
    	for (Location l : locs) {
    		int dir = currentLoc.getDirectionToward(l); //guaranteed multiple of 45
    		int idx = 3*(dir/45); //dir/45 in [0,7]
    		inputs[idx+0] = l.getRow();
    		inputs[idx+1] = l.getCol();
    		inputs[idx+2] = -1; //type of creature here; -1=empty, 1=edible
    		if (getGrid().get(l) != null) { //there's an actor here
    			inputs[idx+2] = 1; //guaranteed edible by getMoveLocations()
    		}
    	}
    	inputs[numInputs-1] = Math.random(); //last input is random
        
    	///////////////////////
    	//choose the location//
    	boolean[] result = brain.think(inputs); //one 3 bit number
    	int out = boolArrToInt(result); //in [0,7]
    	if (locs.size() == 0) return getLocation();
    	else return locs.get(out%locs.size()); //8 elements at most
    }
    
    private static int boolArrToInt(boolean[] b) {
    	int ret = 0;
    	for (int ai = b.length-1; ai >= 0; ai--) {
    		ret += ((int) Math.pow(2, ai)) * (b[ai]?1:0);
    	}
    	return ret;
    }
    
    public NNetwork getBrain() { return brain; }
}
