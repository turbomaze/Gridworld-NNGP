package neuralnetwork;

import java.util.Arrays;

public class NNetwork {
	private int numInps;
	private int numLayers;
	private int numPerLayer;
	private int numOuts;
	private double[] weights;
	private double[] thresholds;
	
	public NNetwork(int numInps, int numLayers, int numPerLayer, int numOuts) {
		this.numInps = numInps;
		this.numLayers = numLayers;
		this.numPerLayer = numPerLayer;
		this.numOuts = numOuts;
		
		int numWeights = 0;
			numWeights += numInps*numPerLayer; //the inputs to the hidden layer
			numWeights += (numLayers-1)*numPerLayer*numPerLayer; //the hidden layers
			numWeights += numPerLayer*numOuts; //the last hidden layer to the outputs
		this.weights = new double[numWeights];
		this.thresholds = new double[numLayers*numPerLayer+numOuts];
	}
	
	public NNetwork(int numInps, int numLayers, int numOuts, double[] wts, double[] thrs) {
		this.numInps = numInps;
		this.numLayers = numLayers;
		this.numPerLayer = (thrs.length-numOuts)/numLayers;
		this.numOuts = numOuts;

		this.weights = Arrays.copyOf(wts, wts.length);
		this.thresholds = Arrays.copyOf(thrs, thrs.length);
	}
	
	public boolean[] think(double[] inputs) {
		boolean[] nodeVals = new boolean[thresholds.length]; //boolean NN
		for (int ni = 0; ni < nodeVals.length; ni++) { //for every node w/ a value
			/////////////////////////////////
			//get the following two indices//
			int idxInWeights = 0; //idx of first child's weight
			int idxOfChildren = 0; //node id of the first child
			if (ni < numPerLayer) idxInWeights = ni*numInps;
			else if (ni < numPerLayer*numLayers) {
				idxInWeights = (ni-numPerLayer)*numPerLayer;
				idxOfChildren = 5*(ni/numPerLayer-1);
			}
			else {
				idxInWeights = ni-numPerLayer*numLayers; //output idx
				idxInWeights = idxInWeights*numPerLayer; //idx in weights[]
				int offset = weights.length-numOuts*numPerLayer; //output weights start
				idxInWeights += offset;
				idxOfChildren = (numLayers-1)*numPerLayer;
			}
			
			/////////////////////////////////////////
			//compute the sum of all values*weights//
			double sum = 0;
			if (ni < numPerLayer) { //first hidden layer
				for (int ii = 0; ii < numInps; ii++) { //no activation function
					sum += inputs[ii]*weights[idxInWeights+ii];
				}
			} else { //other hidden layers and output nodes
				for (int hi = 0; hi < numPerLayer; hi++) { //no activation function
					sum += (nodeVals[idxOfChildren+hi]?1:0)*weights[idxInWeights+hi];
				}
			}
			nodeVals[ni] = sum > thresholds[ni]; //compare to threshold, it's a boolean NN
		}
		//return the values of the output nodes
		return Arrays.copyOfRange(nodeVals, nodeVals.length-numOuts, nodeVals.length);
	}
	
	public String squish() {
		String ret = "";
		
		ret += "W:{";
		for (double d : weights) ret += round(d, 1) + " ";
		ret = ret.substring(0, ret.length()-1) + "}; T:{";
		
		for (double d : thresholds) ret += round(d, 1) + " ";
		ret = ret.substring(0, ret.length()-1) + "}";
		
		return ret;
	}
	
	public double round(double num, int places) {
		double pow = Math.pow(10, places);
		return Math.floor(num*pow)/pow;
	}
	
	public double[] getWeights() { return Arrays.copyOf(weights, weights.length); }
	public double[] getThresholds() { return Arrays.copyOf(thresholds, thresholds.length); }
	
	public void setWeights(double[] weights) { this.weights = weights; }
	public void setThresholds(double[] thresholds) { this.thresholds = thresholds; }
}
