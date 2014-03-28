package gp;

import java.util.ArrayList;

import being.FlowerHunter;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Flower;
import info.gridworld.grid.Location;

public class GPHandler extends ActorWorld {
	//////////
	//config//
	private static int numFlowers = 20;
	private static int stepsPerBeingPerTrial = 25;
	private static int trialsPerBeingPerGen = 100; //to remove luck
	
	private int popSize;
	private FlowerHunter[] population; //purposely this specific
	private int genNum;
	
    public static void main(String[] args) {
        GPHandler world = new GPHandler(5);
        world.show();
    }
    
    public GPHandler(int popSize) {
    	this.popSize = popSize;
    	this.population = new FlowerHunter[popSize];
 		for (int ai = 0; ai < popSize; ai++) {
 			population[ai] = new FlowerHunter();
 		}
 		this.genNum = 0;
    }
    
    public void step() {
    	/* The algorithm
    	 * 1. Repeat steps 2-6 for each member of the population trials # of times.
    	 * 2. Clear the grid and add the Being to the center of the grid.
    	 * 3. Add n random flowers.
    	 * 4. Call on the Being to act t times.
    	 * 5. Count the number of flowers left f.
    	 * 6. Set the score s equal to n - f (average across all trials).
    	 * 7. Pick the Being with the best score
    	 * 8. Create mutated children from the best one.
    	 * 9. Kill off the old population.
    	 */
    	
    	genNum += 1;
    	double[] scores = new double[popSize];
    	Location center = new Location(getGrid().getNumRows()/2, getGrid().getNumCols()/2);
    	for (int fhi = 0; fhi < popSize; fhi++) {
	    	for (int ti = 0; ti < trialsPerBeingPerGen; ti++) {
    			//step 2
	    		clearGrid();
	    		this.add(center, population[fhi]); 
	    		
	    		loadRandomFlowers(numFlowers); //step 3
	    		for (int si = 0; si < stepsPerBeingPerTrial; si++) { //step 4
	    			population[fhi].act();
	    		}
	    		
	    		//easiest way to do step 5
	    		String squishedGrid = toString().replaceAll("\\s", ""); //remove whitespace
	    		int numFlowersLeft = squishedGrid.length() - 1; //-1 for the Being
	    		
	    		scores[fhi] += numFlowers - numFlowersLeft; //step 6
    		}
	    	scores[fhi] /= trialsPerBeingPerGen;
    	}
    	
    	//step 7
    	int bestScoreIdx = -1;
    	double bestScore = 0;
    	for (int pi = 0; pi < popSize; pi++) {
    		if (scores[pi] >= bestScore) {
    			bestScoreIdx = pi;
    			bestScore = scores[pi];
    		}
    	}
    	
    	//step 8
    	FlowerHunter[] newPop = new FlowerHunter[popSize];
    	newPop[0] = population[bestScoreIdx];
    	for (int npi = 1; npi < popSize; npi++) {
    		newPop[npi] = newPop[0].mutate();
    	}
    	
    	//step 9
    	population = newPop;
    	
    	//report out
    	String msg = "Gen #"+genNum+" scores: {";
    	for (int ai = 0; ai < scores.length; ai++) msg += scores[ai] + " ";
    	msg = msg.substring(0, msg.length()-1) + "}; Avg score: " + avg(scores);
    	setMessage(msg);
    	System.out.println(msg);
    	
    	//output the best Being's dna every 50 generations
    	if (genNum%50 == 0) System.out.println(population[0].getBrain().squish());
    }
    
    public void clearGrid() {
    	ArrayList<Location> locs = getGrid().getOccupiedLocations();
    	for (Location l : locs) this.remove(l);
    }
    
    public boolean loadRandomFlowers(int n) { //guart'd to add min(n,# grid cells) flowers
    	for (int fi = 0; fi < n; fi++) {
    		Location randLoc = getRandomEmptyLocation();
    		if (randLoc == null) return false;
    		this.add(getRandomEmptyLocation(), new Flower());
    	}
    	return true;
    }
    
    public int getRandInt(int low, int high) { //in [low, high)
    	return (int) Math.floor(low+(high-low)*Math.random());
    }
    
    public double avg(double[] a) {
    	double sum = 0;
    	for (double d : a) sum += d;
    	return sum/a.length;
    }
}
