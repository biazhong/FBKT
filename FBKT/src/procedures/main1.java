package procedures;

import java.util.ArrayList;
import java.util.Random;


public class main1 {
	public static void main(String[] args) {
		
		
		
		int k = 1000000;
		int repeat = 1000;
		double delta = 0.1;
		int n =1500*k;
		int n0 = (int)(0.1*n);
		int correctness = 0;
		Random R = new Random(565L);
		
		
		
		for(int count = 0; count < repeat; count++) {
			ArrayList<Double> mu = new ArrayList<Double>();
			ArrayList<Double> sigma2 = new ArrayList<Double>();
			
			
			ArrayList<Double> sigma2Temp = new ArrayList<Double>();
			
			
			for(int i = 0 ; i <k; i++) {
				if(i==0) {
					mu.add(delta);
					sigma2.add(1d);
				}else {
					mu.add(0d);
					sigma2.add(1d);
				}
			}
			
			
			

			//RSP y = new FBKTGeneral(n,mu,sigma2,3,R.nextLong());
			RSP y = new OCBA(n,mu,sigma2,R.nextLong());
			//RSP y = new EA (n,mu,sigma2,R.nextLong());
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