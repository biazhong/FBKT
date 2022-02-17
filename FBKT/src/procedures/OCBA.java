package procedures;

import java.util.ArrayList;
import java.util.Random;

public class OCBA extends RSP{
	Random R;
	
	public OCBA() {
		N = 100;
		double delta = 0.1;
		mu.add(delta);
		mu.add(0d);
		sigma2.add(1d);
		sigma2.add(1d);
		R=new Random();
	}
	public OCBA(int N, ArrayList<Double> mu, ArrayList<Double> sigma2, long seed) {
		this.N = N;
		this.mu.addAll(mu);
		this.sigma2.addAll(sigma2);
		this.k = mu.size();
		this.R=new Random(seed);
	}
	
	public void run() {
		ArrayList<Double> Ni= new ArrayList<Double>();
		
		Ni.add(0d);
		Ni.add(1d);
		double sumB = 1/sigma2.get(1);
		for(int i = 2;i < k; i++) {
			double temp = sigma2.get(i)/Math.pow(mu.get(0)-mu.get(i), 2)*Math.pow(mu.get(0)-mu.get(1),2)/sigma2.get(1);
			
			Ni.add(temp);
			sumB = sumB + temp * temp / sigma2.get(i); 
		}
		Ni.set(0, Math.sqrt(sigma2.get(0)*sumB));
		double recordSum = 0d;
		for(int i = 0; i < k;i ++) {
			recordSum = recordSum + Ni.get(i);
		}
		double recordMax = -10000000;
		ArrayList<Double> X = new ArrayList<Double>();
		for(int i=0; i < k;i++) {
			
			double tempX = R.nextGaussian()*Math.sqrt(sigma2.get(i)/(Ni.get(i)/recordSum*N))+mu.get(i);
		//	System.out.println(Ni.get(i));
			//System.out.println(tempX+" "+mu.get(i)+" "+Ni.get(i)/recordSum*N);
			X.add(tempX);
			if(tempX>recordMax) {
				recordMax = tempX;
				bestID = i;
			}
		}
		//System.out.println(X.get(0)+" "+mu.get(0)+" "+Ni.get(0)/recordSum*N);
	}
}
