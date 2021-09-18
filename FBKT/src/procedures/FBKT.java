package procedures;

import java.util.ArrayList;
import java.util.Random;

public class FBKT extends RSP{
	Random R = new Random();
	boolean PCSAllocation;
	
	public FBKT() {
		N = 100;
		double delta = 0.1;
		mu.add(delta);
		mu.add(0d);
		sigma2.add(1d);
		sigma2.add(1d);
		PCSAllocation =true;
	}
	public FBKT(int N, ArrayList<Double> mu, ArrayList<Double> sigma2,boolean PCSAllocation) {
		this.N = N;
		this.mu.addAll(mu);
		this.sigma2.addAll(sigma2);
		this.k = mu.size();
		this.PCSAllocation=PCSAllocation;
	}
	
	public void run() {
		int maxRound = (int)Math.ceil(Math.log(k)/Math.log(2));

		ArrayList<Integer> I = new ArrayList<Integer>();
		for(int i = 0 ; i < k; i++) {
			I.add(i);
		}
		
		for(int i = 0; i<maxRound;i++) {
			int r = i+1;
			
			ArrayList<Integer> IPrime = new ArrayList<Integer>();
			if(I.size() % 2 == 1) {
				int sample = (int)Math.floor(R.nextDouble()*I.size());
				int surID = I.get(sample);
				
				IPrime.addAll(I);
				IPrime.set(sample, IPrime.get(IPrime.size()-1));
				IPrime.remove(IPrime.size()-1);
				I.clear();
				I.add(surID);
			}else {
				IPrime.addAll(I);
				I.clear();
			}
			
			double Nr = 0d;
			if(PCSAllocation) {
				Nr=r*1d/Math.pow(2, r+1)*N;
			}else {
				Nr=r/72d*Math.pow(8d/9, r)*N;
			}
			int numOfMatch = (int) IPrime.size() / 2;
			double numOfObvPerAlt = Nr/IPrime.size();
			
			for(int j = 0 ; j <numOfMatch; j++) {
				int indexA = IPrime.get(j);
				int indexB = IPrime.get(numOfMatch+j);
				double tempA = R.nextGaussian()*Math.sqrt(sigma2.get(indexA)/numOfObvPerAlt)+mu.get(indexA);
				double tempB = R.nextGaussian()*Math.sqrt(sigma2.get(indexB)/numOfObvPerAlt)+mu.get(indexB);
				if(tempA >= tempB) {
					I.add(indexA);
				}else {
					I.add(indexB);
				}
			}
		}
		bestID = I.get(0);
	}
}
