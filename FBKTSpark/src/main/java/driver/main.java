package driver;

import java.util.Random;

import problems.SOAnswer;
import problems.SOProb;
import problems.TpMax;
import rngstream.RngStream;

public class main {
	public static void main(String[] args) {
		int RB = 128;
		int[] x_disc = new int [5];
		int sysID=557348;
		
		int rr = RB * 2 - 3;
		int n  = sysID / (RB-1) + 1;
		x_disc[3] = sysID % (RB-1) + 1;
		x_disc[4] = RB - x_disc[3];
		x_disc[0] = (int) Math.ceil( ( (double)rr - Math.sqrt( (double) rr*rr-8.*n) )  / 2);
		x_disc[1] = RB - 1 + (n - (rr-x_disc[0]) * x_disc[0] /2) - x_disc[0];
		x_disc[2] = RB - x_disc[0] - x_disc[1];
		
		for(int i = 0; i < 5;i++) {
			System.out.println(x_disc[i]);
		}
		/**
		
		FBKTnoCRNsJ FBKT = new FBKTnoCRNsJ(0,1,3249,6498,162450,"20");
		FBKT.runSystem();
		System.out.println(FBKT.getBestID());
		System.out.println(FBKT.getBestIDSampleAverage());
		long [] seed = new long[6];
		Random R = new Random();
		
		double getSeed = R.nextDouble()*1000000;
		for(int m=0; m<6; m++) seed[m]=(long)getSeed;
		getSeed = Math.ceil(getSeed);
		SOProb prob = new TpMax("20","-0.1");
		RngStream rStream = new RngStream();
		
		rStream.setSeed(seed);
		long _tt = System.currentTimeMillis();
		prob.runSystem(505,1000,rStream);
		SOAnswer ans = prob.getAns();
		System.out.println(ans.getFn());
		**/
	}
}