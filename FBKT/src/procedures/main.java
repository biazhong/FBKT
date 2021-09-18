package procedures;

import java.util.ArrayList;
import java.util.Random;


public class main {
	public static void main(String[] args) {
		
		
		
		int k = 1000000;
		int repeat = 1000;
		double delta = 0.1;
		int n =100*k;
		int n0 = (int)(0.1*n);
		int correctness = 0;
		Random R = new Random();
		
		
		
		for(int count = 0; count < repeat; count++) {
			ArrayList<Double> mu = new ArrayList<Double>();
			ArrayList<Double> sigma2 = new ArrayList<Double>();
			
			
			ArrayList<Double> sigma2Temp = new ArrayList<Double>();
			
			/**
			for(int i = 0 ; i <k; i++) {
				if(i==0) {
					mu.add(delta);
					sigma2.add(1d);
				}else {
					mu.add(0d);
					sigma2.add(1d);
				}
			}
			**/
			
			
			for(int i = 0;i < k; i++) {
				if(i==0) {
					mu.add(delta);
					sigma2.add(delta+15*delta*R.nextDouble());
				//	sigma2Temp.add(delta+15*delta*R.nextDouble());
				}else {
					mu.add(-15d*delta*(i)/(k-1));
					
					sigma2.add(delta+15*delta*R.nextDouble());
					
				//	sigma2Temp.add(delta+15*delta*i/(k));
				}
			}
			/**

			for(int i = 0; i < k; i++) {
				//variance decreases as mean decreases;
				//sigma2.add(sigma2Temp.get(i));
				
				//variance decreases as mean increases;
				
				//sigma2.add(sigma2Temp.get(k-1-i));
			
				
				// random variance
			
				int sample = (int)Math.floor(sigma2Temp.size()*R.nextDouble());
				sigma2.add(sigma2Temp.get(sample));
				sigma2Temp.remove(sample);
			
			}
		
			**/
			//RSP y = new FBKTSMergeSortGeneral(n-n0,n0,mu,sigma2,3);

			RSP y = new FBKTGeneral(n,mu,sigma2,3);
			//RSP y = new OCBA(n,mu,sigma2);
			//RSP y = new EA (n,mu,sigma2);
			y.run();
			//System.out.println(y.getBestID());
			if(y.getBestID() == 0) {
				correctness++;
			}
			System.out.println(count);
		}
		System.out.println(correctness*1.0/repeat);
	}
}