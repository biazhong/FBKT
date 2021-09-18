package procedures;

import java.util.ArrayList;
import java.util.Random;

public class EA extends RSP {
	Random R = new Random();
	public EA() {
		N = 100;
		double delta = 0.1;
		mu.add(delta);
		mu.add(0d);
		sigma2.add(1d);
		sigma2.add(1d);
	}
	public EA(int N, ArrayList<Double> mu, ArrayList<Double> sigma2) {
		this.N = N;
		this.mu.addAll(mu);
		this.sigma2.addAll(sigma2);
		this.k = mu.size();
	}
	
	public void run() {
		ArrayList<Double> X = new ArrayList<Double>();
		double sampleSizePerAlt = N/k;
		double recordMax = -10000000;
		for(int i=0; i < k;i++) {
			double tempX = R.nextGaussian()*Math.sqrt(sigma2.get(i)/sampleSizePerAlt)+mu.get(i);
			X.add(tempX);
			if(tempX>recordMax) {
				recordMax = tempX;
				bestID = i;
			}
		}
	}
	
	
}
