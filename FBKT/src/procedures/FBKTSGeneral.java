package procedures;

import java.util.ArrayList;
import java.util.Random;

public class FBKTSGeneral extends RSP {
	Random R1 = new Random();
	Random R2 = new Random();
	int Allocation;
	
	int N0 = 10;
	
	public FBKTSGeneral() {
		N = 100;
		N0 = 10;
		double delta = 0.1;
		mu.add(delta);
		mu.add(0d);
		sigma2.add(1d);
		sigma2.add(1d);
		Allocation =3;
	}
	public FBKTSGeneral(int N, int N0, ArrayList<Double> mu, ArrayList<Double> sigma2,int Allocation) {
		this.N = N;
		this.N0 = N0;
		this.mu.addAll(mu);
		this.sigma2.addAll(sigma2);
		this.k = mu.size();
		this.Allocation=Allocation;
	}
	
	public void run() {
		int maxRound = (int)Math.ceil(Math.log(k)/Math.log(2));
		
		ArrayList<Double> T = new ArrayList<Double>();
		ArrayList<Double> sumFrakX = new ArrayList<Double>();

		ArrayList<Integer> I = new ArrayList<Integer>();
		for(int i = 0 ; i < k; i++) {
			I.add(i);
			
			T.add(N0*1d/k);
			sumFrakX.add(R1.nextGaussian()*Math.sqrt(sigma2.get(I.get(i))*(N0*1d/k))+N0*1d/k*mu.get(I.get(i)));
		}
		
		//System.out.println(sumFrakX.get(0));
		for(int i = 0; i<maxRound;i++) {
			int r = i+1;
			
			
			ArrayList<Integer> IPrime = new ArrayList<Integer>();
			if(I.size() % 2 == 1) {
				IPrime.addAll(I.subList(1,I.size()));
				int surID = I.get(0);
				I.clear();
				I.add(surID);
			}else {
				IPrime.addAll(I);
				I.clear();
			}
			
			double Nr = 0d;
			Nr=r*1d/(Allocation*(Allocation-1))*Math.pow((Allocation-1d)/Allocation, r)*N;
			int numOfMatch = (int) IPrime.size() / 2;
			double numOfObvPerAlt = Nr/IPrime.size();
			
			
			
			
			
			for(int j = 0 ; j <numOfMatch; j++) {
				
				int searchSize = (int) (Math.max(2, IPrime.size()/Math.pow(2, j)));
			
				
				int maxIdx = -1; //max index
				int minIdx = -1; //min index
				double averageMax = -100000000;
				double averageMin = 100000000;
				ArrayList<Integer> Irv =  new ArrayList<Integer>();

				for(int v = 0; v < searchSize; v++) {
					int sample = (int)Math.floor(R1.nextDouble()*IPrime.size());
					
					Irv.add(IPrime.get(sample));
					
					if(sumFrakX.get(Irv.get(v))/T.get(Irv.get(v))>averageMax) {
						maxIdx = v;
						averageMax = sumFrakX.get(Irv.get(v))/T.get(Irv.get(v));
					}
					
					if(sumFrakX.get(Irv.get(v))/T.get(Irv.get(v))<averageMin) {
						minIdx = v;
						averageMin = sumFrakX.get(Irv.get(v))/T.get(Irv.get(v));
					}
					
					IPrime.set(sample, IPrime.get(IPrime.size()-1));
					IPrime.remove(IPrime.size()-1);
				}
				int indexA = Irv.get(maxIdx);
				int indexB = Irv.get(minIdx);
				if(minIdx!=Irv.size()-1) {
					Irv.set(maxIdx, Irv.get(Irv.size()-1));
					Irv.remove(Irv.size()-1);
					Irv.set(minIdx, Irv.get(Irv.size()-1));
					Irv.remove(Irv.size()-1);
				}else {
					Irv.remove(Irv.size()-1);
					Irv.set(maxIdx, Irv.get(Irv.size()-1));
					Irv.remove(Irv.size()-1);
				}
				
				IPrime.addAll(Irv);
				Irv.clear();
				double tp = R1.nextGaussian();
				
				double tempA = tp*Math.sqrt(sigma2.get(indexA)/numOfObvPerAlt)+mu.get(indexA);
				tp = R2.nextGaussian();
				
				double tempB = tp*Math.sqrt(sigma2.get(indexB)/numOfObvPerAlt)+mu.get(indexB);
				if(indexA == 0 | indexB==0) {
				//	System.out.println(indexA+" competes with "+indexB+" num of obv "+numOfObvPerAlt+" "+mu.get(indexA)+" "+mu.get(indexB));
				}
				
				if(tempA >= tempB) {
					
					I.add(indexA);
					T.set(indexA, T.get(indexA)+numOfObvPerAlt);
					sumFrakX.set(indexA, sumFrakX.get(indexA)+tempA*numOfObvPerAlt);
				}else {
					
					I.add(indexB);
					T.set(indexB, T.get(indexB)+numOfObvPerAlt);
					sumFrakX.set(indexB, sumFrakX.get(indexB)+tempB*numOfObvPerAlt);
				}
			}
			
		}
		
		bestID = I.get(0);
		
	}

}


