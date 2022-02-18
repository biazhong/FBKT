package driver;

import java.util.ArrayList;
import java.util.Random;

import problems.SOAnswer;
import problems.SOProb;
import problems.TpMax;
import rngstream.RngStream;


public class EAnoCRNsJ {
	String param;
	double N;
	long simulationTime = (long)0.0;
	int bestID;
	int numOfCores;
	Random R;
	double XBar=-100000;

	double finalSeed;
	
	int coreID;
	int nSys;
	String cov = "-0.1";
	
	public EAnoCRNsJ(){
		this.coreID = 0;
		this.numOfCores = 48;
		this.nSys = 10;
		this.N = 90*nSys;
		this.param = "20";
		this.finalSeed = 57624;
		
	}
	
	public EAnoCRNsJ(int coreID, int numOfCores, int nSys, double N, String param, double finalSeed){
		this.coreID = coreID;
		this.numOfCores = numOfCores;
		this.nSys = nSys;
		this.N = N;
		this.param = param;
		this.finalSeed=finalSeed;
	}
	public int getBestID(){
		return bestID;
	}
	

	public double getBestIDSampleAverage() {
		return XBar;
	}
	
	public long getSimTime() {
		return simulationTime;
	}
	
	public void runSystem() {
		ArrayList<Integer> I = new ArrayList<Integer>();
		for(int i = 0; i < nSys; i++) {
			if(i % numOfCores == coreID) {
				I.add(i);
			}
		}
		R=new Random((long)finalSeed);
		for(int i = 0; i < nSys; i++) {
			
			if(i % numOfCores == coreID) {
				long [] seed = new long[6];
				
				
				double getSeed = R.nextDouble()*1000000;
				getSeed = Math.ceil(getSeed);
				
				for(int m=0; m<6; m++) seed[m]=(long)getSeed;

				SOProb prob = new TpMax(param,cov);
				RngStream rStream = new RngStream();
				rStream.setSeed(seed);
				long _tt = System.currentTimeMillis();
				prob.runSystem(i,(int)Math.floor(N*1d/nSys),rStream);
				simulationTime =  simulationTime +System.currentTimeMillis()-_tt;
				
				
				SOAnswer ans = prob.getAns();
				if(ans.getFn()>XBar ) {
					XBar  = ans.getFn();
					bestID = i;
				}
			}
		}
	}
}
