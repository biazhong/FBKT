package procedures;

import java.util.ArrayList;
import java.util.Random;

public class FBKTSMergeSortGeneral extends RSP {
	Random R1 = new Random();
	Random R2 = new Random();
	int Allocation;
	
	int N0 = 10;
	
	public FBKTSMergeSortGeneral() {
		N = 100;
		N0 = 10;
		double delta = 0.1;
		mu.add(delta);
		mu.add(0d);
		sigma2.add(1d);
		sigma2.add(1d);
		Allocation = 2;
	}
	public FBKTSMergeSortGeneral(int N, int N0, ArrayList<Double> mu, ArrayList<Double> sigma2,int Allocation) {
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
		
		
		for(int i = 0; i<maxRound;i++) {
			int r = i+1;
			
			/**for(int c = 0; c < I.size();c++) {
				System.out.print(I.get(c)+" ");
				
			}
			
			System.out.println(" ");
			
			for(int c = 0; c < I.size();c++) {
				System.out.print(mu.get(I.get(c))+" ");
			}
			System.out.println(" ");
			
			for(int c = 0; c < I.size();c++) {
				System.out.print(sumFrakX.get(I.get(c))/T.get(I.get(c))+" ");
			}
			System.out.println(" ");
			
			for(int c = 0; c < I.size();c++) {
				System.out.print(T.get(I.get(c))+" ");
			}
			System.out.println(" ");**/
			
			
			
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
			
			double Nr = 0d;
			Nr=r*1d/(Allocation*(Allocation-1))*Math.pow((Allocation-1d)/Allocation, r)*N;
			
			
			int numOfMatch = (int) IPrime.size() / 2;
			double numOfObvPerAlt = Nr/IPrime.size();
			
			
			
			
			
			for(int j = 0 ; j <numOfMatch; j++) {
				
				
				int indexA = IPrime.get(j);
				int indexB = IPrime.get(IPrime.size()-1-j);
				
				double tp = R1.nextGaussian();
				
				double tempA = tp*Math.sqrt(sigma2.get(indexA)/numOfObvPerAlt)+mu.get(indexA);
				tp = R2.nextGaussian();
				
				double tempB = tp*Math.sqrt(sigma2.get(indexB)/numOfObvPerAlt)+mu.get(indexB);
				if(indexA == 0 | indexB==0) {
					//System.out.println(indexA+" competes with "+indexB+" num of obv "+numOfObvPerAlt);
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


