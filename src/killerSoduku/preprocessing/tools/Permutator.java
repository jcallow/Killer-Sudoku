package killerSoduku.preprocessing.tools;

import java.util.ArrayList;

public class Permutator {
	int[] array;
	int[] swap_count;
	int swap_index;
	int length;
	
	public static void main(String[] args) {
		Permutator permutator = new Permutator();
		int[] array = new int[4];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = i + 1;
		}
		
    	ArrayList<int[]> permutations = permutator.permute(array, 0);
    			//permutator.getPermutations(array);		
		for (int i = 0; i < permutations.size(); i++) {
			for (int j = 0; j < permutations.get(i).length; j++) {
				System.out.print(permutations.get(i)[j] + " ");
			}
			System.out.println("");
		}
		
	}
	
	public ArrayList<int[]> getPermutations(int[] arr) {
		return permute(arr, 0);
	}
	
	// This method will double count if entries are not distinct.  Solver would still work, but would just have duplicate assignments.
	public ArrayList<int[]> permute(int[] arr, int k){
		ArrayList<int[]> permutations = new ArrayList<int[]>();
        for(int i = k; i < arr.length; i++){
            swap(arr, i, k);
            permutations.addAll(permute(arr, k+1));
            swap(arr, k, i);
        }
        if (k == arr.length -1){
        	int[] newArray = new int[arr.length];
			System.arraycopy(arr, 0, newArray, 0, arr.length);
            permutations.add(newArray);
        }
        return permutations;
    }
	
	public void swap(int[] arr, int i, int k) {
		int temp = arr[i];
		arr[i] = arr[k];
		arr[k] = temp;
	}
	
	
	
	/* This method didn't seem to work beyond size 3 */
	
//	public void initialize(int[] array) {
//		this.array = array;
//		this.length = array.length;
//		this.swap_count = new int[length];
//		this.swap_index = 0;
//		
//		for (int i=0; i<length;i++) {
//			swap_count[i] = 0;
//		}
//	}
//	
//	public Boolean advance_Permutator() {
//		
//		while(true) {
//			if(swap_count[swap_index] == swap_index) {
//				swap_count[swap_index++] = 0;
//				
//				if(swap_index == length) {
//					return false;
//				} 
//			} else {
//				swap_count[swap_index]++;
//				int temp = array[swap_index];
//				array[swap_index] = array[swap_index-1];
//				array[swap_index-1] = temp;
//				swap_index = 0;
//				return true;
//			}
//		}
//	}
//	
//	public ArrayList<int[]> getPermutations(int[] array) {
//		initialize(array);
//		ArrayList<int[]> permutations = new ArrayList<int[]>();
//
//		do {
//			int[] a = new int[length];
//			System.arraycopy(array, 0, a, 0, length);
//			permutations.add(a);
//		} while(advance_Permutator());
//		
//		
//		return permutations;
//	}
}
