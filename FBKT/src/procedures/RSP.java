package procedures;

import java.util.ArrayList;

public abstract class RSP {
	protected int k,N,bestID=-1;
	
	
	protected ArrayList<Double> mu = new ArrayList<Double>();
	protected ArrayList<Double> sigma2 = new ArrayList<Double>();
	
	public abstract void run();
	
	
	public int getBestID() {
		return bestID;
	}
	
	public int getSamplingBudget() {
		return N;
	}
	
	public int getSysSize() {
		return k;
	}
	
	
}
