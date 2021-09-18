package driver;
import rngstream.*;

import java.util.*;

import problems.*;
public class FBKTnoCRNsJ {
	String param;
	double N0;
	double N;
	long simulationTime = (long)0.0;
	int bestID;
	int numOfCores;
	int Allocation = 3;
	Random R = new Random();
	double XBar;
	
	int coreID;
	int nSys;
	String cov = "-0.1";
	
	public FBKTnoCRNsJ(){
		this.coreID = 0;
		this.numOfCores = 48;
		this.nSys = 10;
		this.N0= 10*nSys;
		this.N = 90*nSys;
		this.param = "20";
	}
	
	public FBKTnoCRNsJ(int coreID, int numOfCores, int nSys, double N0, double N, String param){
		this.coreID = coreID;
		this.numOfCores = numOfCores;
		this.nSys = nSys;
		this.N0 = N0;
		this.N = N;
		this.param = param;
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
		ArrayList<Double> T = new ArrayList<Double>();
		ArrayList<Double> sumFrakX = new ArrayList<Double>();
		for(int i = 0; i < nSys; i++) {
			if(i % numOfCores == coreID) {
				I.add(i);
			}
		}
		for(int i = 0; i < nSys; i++) {
			if(i % numOfCores == coreID) {
				T.add(Math.floor(N0*1d/nSys));
				long [] seed = new long[6];
				
				
				double getSeed = R.nextDouble()*1000000;
				getSeed = Math.ceil(getSeed);
				
				for(int m=0; m<6; m++) seed[m]=(long)getSeed;

				SOProb prob = new TpMax(param,cov);
				RngStream rStream = new RngStream();
				rStream.setSeed(seed);
				long _tt = System.currentTimeMillis();
				prob.runSystem(i,(int)Math.floor(N0*1d/nSys),rStream);
				simulationTime =  simulationTime +System.currentTimeMillis()-_tt;
				
				
				SOAnswer ans = prob.getAns();
				sumFrakX.add(ans.getFn()*Math.floor(N0*1d/nSys));
			}else {
				T.add(0d);
				sumFrakX.add(0d);
			}
		}
		
		
		int maxR = (int)(Math.ceil(Math.log(nSys*1d/numOfCores)/Math.log(2)));
		

		
		for(int i = 0; i<maxR;i++) {
			
			
			int r = i+1;
			ArrayList<Integer> IPrime = new ArrayList<Integer>();
			
			
			
			double avgTemp[]=new double[I.size()];
			for(int h = 0; h < I.size(); h++) {
				avgTemp[h]=sumFrakX.get(I.get(h))/T.get(I.get(h));
			}
			int left = 0;
			int right = avgTemp.length-1;
			mergeSort(avgTemp, I,left,right);
			
			
		
			if(I.size() % 2 == 1) {
				IPrime.addAll(I.subList(0,I.size()-1));
				int surID = I.get(I.size()-1);
				I.clear();
				I.add(surID);
			}else {
				IPrime.addAll(I);
				I.clear();
			}
			double Nr =Math.floor(r*1d/(Allocation*(Allocation-1))*Math.pow((Allocation-1d)/Allocation, r)*N/numOfCores);
			
			
			
			int numOfMatch = (int) IPrime.size() / 2;
			double numOfObvPerAlt = Math.floor(Nr/IPrime.size());
			
			for(int j = 0 ; j <numOfMatch; j++) {
				
				
				int indexA = IPrime.get(j);
				int indexB = IPrime.get(IPrime.size()-1-j);
				
				Random R = new Random();
				long [] seed1 = new long[6];
				double getSeed1 = R.nextDouble()*1000000;
				getSeed1 = Math.ceil(getSeed1);
				
				for(int m=0; m<6; m++) seed1[m]=(long)getSeed1;

				SOProb prob1 = new TpMax(param,cov);
				RngStream rStream1 = new RngStream();
				rStream1.setSeed(seed1);
				long _tt1 = System.currentTimeMillis();
				prob1.runSystem(indexA,(int)numOfObvPerAlt,rStream1);
				simulationTime =  simulationTime +System.currentTimeMillis()-_tt1;			
				SOAnswer ans1 = prob1.getAns();
				
				double tempA = ans1.getFn();
				
				long [] seed2 = new long[6];
				double getSeed2 = R.nextDouble()*1000000;
				getSeed2 = Math.ceil(getSeed2);
				
				for(int m=0; m<6; m++) seed2[m]=(long)getSeed2;

				SOProb prob2 = new TpMax(param,cov);
				RngStream rStream2 = new RngStream();
				rStream2.setSeed(seed2);
				long _tt2 = System.currentTimeMillis();
				prob2.runSystem(indexB,(int)numOfObvPerAlt,rStream2);
				simulationTime =  simulationTime +System.currentTimeMillis()-_tt2;			
				SOAnswer ans2 = prob2.getAns();
				double tempB = ans2.getFn();
				
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
		int r = maxR + 1;
		double NR =Math.floor(r*1d/(Allocation-1)*Math.pow((Allocation-1d)/Allocation, r)*N/numOfCores);
		long [] seed3 = new long[6];
		double getSeed3 = R.nextDouble()*1000000;
		getSeed3 = Math.ceil(getSeed3);
		
		for(int m=0; m<6; m++) seed3[m]=(long)getSeed3;

		SOProb prob3 = new TpMax(param,cov);
		RngStream rStream3 = new RngStream();
		rStream3.setSeed(seed3);
		long _tt3 = System.currentTimeMillis();
		prob3.runSystem(bestID,(int)NR,rStream3);
		simulationTime =  simulationTime +System.currentTimeMillis()-_tt3;			
		SOAnswer ans3 = prob3.getAns();
		XBar = ans3.getFn();
	}
	
	
	public static void mergeSort(double[] array, ArrayList<Integer> I, int left, int right) {
	    if (right <= left) return;
	    int mid = (left+right)/2;
	    mergeSort(array, I ,left, mid);
	    mergeSort(array, I ,mid+1, right);
	    merge(array, I ,left, mid, right);
	}

	public static void merge(double[] array, ArrayList<Integer> I,int left, int mid, int right) {
	    // calculating lengths
	    int lengthLeft = mid - left + 1;
	    int lengthRight = right - mid;
	    
	    

	    // creating temporary subarrays
	    double leftArray[] = new double [lengthLeft];
	    double rightArray[] = new double [lengthRight];

	    ArrayList<Integer> leftI = new ArrayList<Integer>();
	    ArrayList<Integer> rightI = new ArrayList<Integer>();
	    
	    // copying our sorted subarrays into temporaries
	    for (int i = 0; i < lengthLeft; i++) {
	    	leftArray[i] = array[left+i];
	    	leftI.add(I.get(left+i));
	    }
	        
	    for (int i = 0; i < lengthRight; i++) {
	        rightArray[i] = array[mid+i+1];
	        rightI.add(I.get(mid+i+1));
	    }

	    // iterators containing current index of temp subarrays
	    int leftIndex = 0;
	    int rightIndex = 0;

	    // copying from leftArray and rightArray back into array
	    for (int i = left; i < right + 1; i++) {
	        // if there are still uncopied elements in R and L, copy minimum of the two
	        if (leftIndex < lengthLeft && rightIndex < lengthRight) {
	            if (leftArray[leftIndex] < rightArray[rightIndex]) {
	                array[i] = leftArray[leftIndex];
	                I.set(i, leftI.get(leftIndex));
	                leftIndex++;
	            }
	            else {
	                array[i] = rightArray[rightIndex];
	                I.set(i, rightI.get(rightIndex));
	                rightIndex++;
	            }
	        }
	        // if all the elements have been copied from rightArray, copy the rest of leftArray
	        else if (leftIndex < lengthLeft) {
	            array[i] = leftArray[leftIndex];
	            I.set(i, leftI.get(leftIndex));
	            leftIndex++;
	        }
	        // if all the elements have been copied from leftArray, copy the rest of rightArray
	        else if (rightIndex < lengthRight) {
	            array[i] = rightArray[rightIndex];
	            I.set(i, rightI.get(rightIndex));
	            rightIndex++;
	        }
	    }
	}
}
