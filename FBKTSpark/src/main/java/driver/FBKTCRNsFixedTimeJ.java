package driver;
import rngstream.*;

import java.util.*;

import problems.*;
public class FBKTCRNsFixedTimeJ {
	String param;
	double T0; // in sec
	double T; // in sec
	long totalSampleSize = (long)0.0;
	long totalSimTime = (long)0.0;
	
	
	int bestID;
	int numOfCores;
	int Allocation = 3;
	Random R = new Random();
	
	int coreID;
	int nSys;
	double finalSeed;
	
	String cov = "-0.1";
	
	public FBKTCRNsFixedTimeJ(){
		this.coreID = 0;
		this.numOfCores = 48;
		this.nSys = 10;
		this.T0= 10;
		this.T = 90;
		this.param = "20";
	}
	
	public FBKTCRNsFixedTimeJ(int coreID, int numOfCores, int nSys, double T0, double T, String param){
		this.coreID = coreID;
		this.numOfCores = numOfCores;
		this.nSys = nSys;
		this.T0 = T0;
		this.T = T;
		this.param = param;
	}
	public int getBestID(){
		return bestID;
	}

	
	public long getTotalSampleSize() {
		return totalSampleSize;
	}
	public long getTotalSimTime() {
		return totalSimTime;
	}
	public void runSystem() {
		ArrayList<Integer> indexI = new ArrayList<Integer>();
		ArrayList<Integer> I = new ArrayList<Integer>();
		ArrayList<Double> count = new ArrayList<Double>();
		ArrayList<Double> sumFrakX = new ArrayList<Double>();
		int _i = 0;
		for(int i = 0; i < nSys; i++) {
			if(i % numOfCores == coreID) {
				
				indexI.add(i);
				I.add(_i);
				_i++;
				count.add(0d);
				sumFrakX.add(0d);
			}
		}
		long [] seed = new long[6];
		
		
		double runTime = T0*1d/numOfCores;
		long _start = System.currentTimeMillis();
		while(System.currentTimeMillis()<=_start+runTime) {
			double getSeed = R.nextDouble()*1000000;
			getSeed = Math.ceil(getSeed);
			for(int m=0; m<6; m++) seed[m]=(long)getSeed;
			
			for(int i = 0; i < I.size(); i++) {
				SOProb prob = new TpMax(param,cov);
				RngStream rStream = new RngStream();
				rStream.setSeed(seed);
				long _tt = System.currentTimeMillis();			
				prob.runSystem(indexI.get(I.get(i)),1,rStream);
				totalSimTime = totalSimTime+System.currentTimeMillis()-_tt;
				SOAnswer ans = prob.getAns();
				
				count.set(I.get(i), count.get(I.get(i))+1);
				sumFrakX.set(I.get(i), sumFrakX.get(i)+ans.getFn());
			}
			
			totalSampleSize = totalSampleSize+I.size();
		
		}
		
		
		
		
		int maxR = (int)(Math.ceil(Math.log(nSys*1d/numOfCores)/Math.log(2)));
		

		
		for(int i = 0; i<maxR;i++) {
			
			
			int r = i+1;
			ArrayList<Integer> IPrime = new ArrayList<Integer>();
			
			
			
			double avgTemp[]=new double[I.size()];
			for(int h = 0; h < I.size(); h++) {
				avgTemp[h]=sumFrakX.get(I.get(h))/count.get(I.get(h));
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
			double Tr =r*1d/(Allocation*(Allocation-1))*Math.pow((Allocation-1d)/Allocation, r)*T/numOfCores;
			

			
			int numOfMatch = (int) IPrime.size() / 2;
			double runTimePerMatch = Tr/numOfMatch;
			
			for(int j = 0 ; j <numOfMatch; j++) {
				
				
				int indexA = IPrime.get(j);
				int indexB = IPrime.get(IPrime.size()-1-j);
				double sumXA =0d;
				double sumXB = 0d;
				double sampleSize = 0d;
				_start = System.currentTimeMillis();
				
				while(System.currentTimeMillis()<= _start+runTimePerMatch) {
					
					Random R = new Random();
					long [] seed1 = new long[6];
					double getSeed1 = R.nextDouble()*1000000;
					getSeed1 = Math.ceil(getSeed1);
					for(int m=0; m<6; m++) seed1[m]=(long)getSeed1;

					SOProb prob1 = new TpMax(param,cov);
					RngStream rStream1 = new RngStream();
					rStream1.setSeed(seed1);
					long _tt = System.currentTimeMillis();
					prob1.runSystem(indexI.get(indexA),1,rStream1);
					totalSimTime = totalSimTime+System.currentTimeMillis()-_tt;
					SOAnswer ans1 = prob1.getAns();
					sumXA = ans1.getFn();
					
					SOProb prob2 = new TpMax(param,cov);
					RngStream rStream2 = new RngStream();
					rStream2.setSeed(seed1);
					_tt = System.currentTimeMillis();
					prob2.runSystem(indexI.get(indexB),1,rStream2);
					totalSimTime = totalSimTime+System.currentTimeMillis()-_tt;
					SOAnswer ans2 = prob2.getAns();
					sumXB = ans2.getFn();
					
					sampleSize++;
					totalSampleSize=totalSampleSize+2; 
					
				}
				
				
				
				if(sumXA >= sumXB) {
					
					I.add(indexA);
					count.set(indexA, count.get(indexA)+sampleSize);
					sumFrakX.set(indexA, sumFrakX.get(indexA)+sumXA);
				}else {
					
					I.add(indexB);
					count.set(indexB, count.get(indexB)+sampleSize);
					sumFrakX.set(indexB, sumFrakX.get(indexB)+sumXB);
				}
			}
		}
		bestID = indexI.get(I.get(0));
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
