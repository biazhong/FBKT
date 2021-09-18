package driver;

import java.util.Random;

import problems.SOAnswer;
import problems.SOProb;
import problems.TpMax;
import rngstream.RngStream;

public class FBKTCRNsFixedTimeFinalStepJ {
	String param;
	long totalSampleSize = (long)0.0;
	long totalSimTime = (long)0.0;
	int coreOutPut[];
	double TRmax;
	
	double sumX[];
	
	Random R = new Random();
	
	
	String cov = "-0.1";
	
	public FBKTCRNsFixedTimeFinalStepJ(){
		this.coreOutPut = new int[10];
		this.param = "20";
		this.TRmax = 0d;
	}
	
	public FBKTCRNsFixedTimeFinalStepJ(int[] coreOutPut, double TRmax, String param){
		
		this.coreOutPut = coreOutPut;
		this.TRmax = TRmax;
		this.param = param;
	}
	

	
	public long getTotalSampleSize() {
		return totalSampleSize;
	}
	public long getTotalSimTime() {
		return totalSimTime;
	}
	public double[] getResults() {
		return sumX;
	}
	
	public void runSystem() {
		sumX = new double[coreOutPut.length];
		
		long _startTime = System.currentTimeMillis();
		while(System.currentTimeMillis()<= _startTime+TRmax) {
			double getSeed = R.nextDouble()*1000000;
			long [] seed = new long[6];
			getSeed = Math.ceil(getSeed);
			for(int m=0; m<6; m++) seed[m]=(long)getSeed;
			
			for(int i = 0; i < coreOutPut.length;i++) {
				int index = coreOutPut[i];
				SOProb prob = new TpMax(param,cov);
				RngStream rStream = new RngStream();
				rStream.setSeed(seed);
				long _tt=System.currentTimeMillis();
				prob.runSystem(index,1,rStream);
				totalSimTime+=System.currentTimeMillis()-_tt;
				
				SOAnswer ans = prob.getAns();
				sumX[i] = sumX[i]+ans.getFn();
			}
			totalSampleSize = totalSampleSize + coreOutPut.length;
		}
	}
	
	
	
}
