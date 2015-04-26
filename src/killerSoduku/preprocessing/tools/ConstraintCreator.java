package killerSoduku.preprocessing.tools;

import java.util.ArrayList;

public class ConstraintCreator {
	Permutator permutator;
	
	public static void main(String[] args) {
		ConstraintCreator cc = new ConstraintCreator();
		ArrayList<int[]> allCombos = cc.getAllCombos(27, 5);
		
		for (int i = 0; i < allCombos.size(); i++) {
			for (int j = 0; j < allCombos.get(i).length; j++) {
				System.out.print(allCombos.get(i)[j] + " ");
			}
			System.out.println("");
		}
	}
	
	public ConstraintCreator() {
		permutator = new Permutator();
	}
	
	
	public ArrayList<int[]> getAllCombos(int n, int k) {
		ArrayList<int[]> sumCombinations = getAllSumCombos(n,k);
		ArrayList<int[]> allCombos = new ArrayList<int[]>();
		
		for (int[] combo : sumCombinations) {
			allCombos.addAll(permutator.getPermutations(combo));
		}
		
		return allCombos;
		
	}
	
	private ArrayList<int[]> getAllSumCombos(int n, int k) {
		ArrayList<int[]> sumCombinations = new ArrayList<int[]>();
		for (int i = 1; i < 10; i++) {
			sumCombinations.addAll(sum_combinations(n,k,i));
		}
		
		return sumCombinations;
	}
	
	private ArrayList<int[]> sum_combinations(int n, int k, int min) {
		
		ArrayList<int[]> combinations = new ArrayList<int[]>();
		
		if (k == 1) {
			//System.out.println(min + " " + n);
			if (n == min) {
				int[] combination = new int[1];
				combination[0] = n;
				combinations.add(combination);
				return combinations;
			}
		}

		// If sum is impossible/will go over return empty list
		int lowSum = (min + k - 1)*(min + k)/2 - (min-1)*(min)/2;
		if (lowSum > n) {
			return combinations;
		}
		
		//Test all possible next entries
		if (k >= 2) {
			for (int i = 1; min + i <= 9; i++) {
				ArrayList<int[]> subarrays = sum_combinations(n-min, k-1, min+i);
				for (int[] subarray : subarrays) {
					int[] combination = new int[k];
					combination[0] = min;
					System.arraycopy(subarray,0,combination,1,subarray.length);
					combinations.add(combination);
				}
			}

			return combinations;
		} 
		return combinations;
		
	}

}
