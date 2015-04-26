package killerSoduku.preprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import killerSoduku.preprocessing.data.Assignment;
import killerSoduku.preprocessing.data.PreConstraint;
import killerSoduku.preprocessing.data.Pair;
import killerSoduku.preprocessing.data.Variable;
import killerSoduku.preprocessing.tools.ConstraintCreator;

public class PreProcessing {
	LinkedList<Pair> Que;
	HashSet<PreConstraint> constraints;
	String constraintsFile = "constraints.txt";
	ConstraintCreator constraintCreator;
	HashMap<Integer,HashSet<Integer>> domainMap;
	HashMap<Integer, HashSet<PreConstraint>> variableToConstraints;
	ArrayList<Integer> variableOrdering;
	
	public static void main(String[] args) {
		PreProcessing solver = new PreProcessing();
		solver.run();
		
	}
	
	public PreProcessing() {
		domainMap = new HashMap<Integer,HashSet<Integer>>();
		constraintCreator = new ConstraintCreator();
		constraints = new HashSet<PreConstraint>();
		Que = new LinkedList<Pair>();
	}
	
	public void run() {
		initializeDomains();
		initializeHashtables();
		setPreConstAssignments();
		findNonEssential(); 
		propegateNonEssential();
		setPostConstAssignments();
		findOrdering();
		generateFilters();
	
	}
	
	private void initializeDomains() {
		HashSet<Integer> initialDomain = new HashSet<Integer>();
		for(int i = 1; i <= 9; i++) {
			initialDomain.add(i);
		}
		for(int i = 1; i <= 9; i++) {
			for(int j = 1; j <= 9; j++) {
				domainMap.put(10*i + j, new HashSet<Integer>(initialDomain));
			}
		}
	}
	
	private void initializeHashtables() {
		kuroConstraints();
		sudokuConstraints();
	}
	
	/* Generate kuro like constraints from text file */
	private void kuroConstraints() {
		try (BufferedReader br = new BufferedReader(new FileReader(constraintsFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	ArrayList<Integer> variableIndex = new ArrayList<Integer>();
		    	ArrayList<int[]> explicitConstraints = new ArrayList<int[]>();
		       String[] data = line.split(",");
		       int sum = Integer.parseInt(data[data.length-1]);
		       for(int i = 0; i < data.length-1; i = i + 2) {
		    	   variableIndex.add(Integer.parseInt(data[i] + data[i+1]));
		       }
		       explicitConstraints = constraintCreator.getAllCombos(sum, variableIndex.size());
		       addConstraints(variableIndex, explicitConstraints, "sum", "Sum=" + sum);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sudokuConstraints() {
		
		// They all look the same, so just use the same set of explicits for building them.
		ArrayList<int[]> explicitConstraits = generateRowColBinaryExplicitConstraints();
		ArrayList<Integer> variableIndex;
		
		 //row constraints
		for (int i = 1; i < 10; i++) {
			variableIndex = new ArrayList<Integer>();
			for (int j = 1; j < 10; j++) {
				variableIndex.add(i*10 + j);
			}
			Collections.sort(variableIndex);
			indexContraints(variableIndex, explicitConstraits, "row");
		}
		
		//column
		for (int i = 1; i < 10; i++) {
			variableIndex = new ArrayList<Integer>();
			for (int j = 1; j < 10; j++) {
				variableIndex.add(j*10 + i);
			}
			Collections.sort(variableIndex);
			indexContraints(variableIndex, explicitConstraits, "column");
		}
		
		// 3x3
		
		for (int i = 1; i < 10; i = i + 3) {
			for (int j = 1; j < 10; j = j + 3) {
				variableIndex = generate3by3(i,j);
				Collections.sort(variableIndex);
				indexContraints(variableIndex, explicitConstraits, "grid");
			}
		}
	}
	
	private ArrayList<Integer> generate3by3(int k, int l) {
		ArrayList<Integer> threexthree = new ArrayList<Integer>();
		for (int i = k; i < k+3; i++) {
			for (int j = l; j < l+3; j++) {
				threexthree.add(i*10 + j);
			}
		}		
		return threexthree;
	}
	
	private void indexContraints(ArrayList<Integer> variableIndex, ArrayList<int[]> explicit, String type) {
		for (int i = 0; i < variableIndex.size(); i++) {
			for (int j = i + 1; j < variableIndex.size(); j++) {
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add(variableIndex.get(i));
				temp.add(variableIndex.get(j));
				addConstraints(temp, explicit, type, "Binary");
			}
		}
	}
	
	// Generate Constraints to be used by the sudoku constraints.  In form of (firstvar, secondvar)
	private ArrayList<int[]> generateRowColBinaryExplicitConstraints() {
		ArrayList<int[]> binaryConstraints = new ArrayList<int[]>();
		
		for (int i = 1; i<=9; i++) {
			for (int j = 1; j<=9; j++) {
				if (i != j) {
					int[] binaryC = new int[2];
					binaryC[0] = i;
					binaryC[1] = j;
					binaryConstraints.add(binaryC);
				}
			}
		}		
		return binaryConstraints;
	}
	
	private void addConstraints(ArrayList<Integer> variableIndex, ArrayList<int[]> explicitConstraints, String type, String name) {
		PreConstraint constraint = new PreConstraint();
		constraint.setType(type);
		constraint.setNamePostFix(name);
		
		for (Integer x : variableIndex) {
			constraint.addVariable(x);
		}
		for (int[] assignment : explicitConstraints) {
			Assignment a = new Assignment();
			for(int i = 0; i < assignment.length; i++) {
				a.addVariableAssignment(variableIndex.get(i), assignment[i]);
				constraint.getVariable(variableIndex.get(i)).addAssignment(assignment[i], a);
			}
			
		}
		constraints.add(constraint);
	}
	
	private void findNonEssential() {
		for (PreConstraint c : constraints) {
			for (Integer x : c.getVariables()) {
				Variable varx = c.getVariable(x);
				HashSet<Integer> currentDomain = new HashSet<Integer>(domainMap.get(x));
				for (Integer d : currentDomain) {
					if (varx.getAssignments(d).isEmpty()) {
						if (removeFromDomain(x, d)) {
							Pair pairX = new Pair(x, d);
							Que.addLast(pairX);
						};
					}
				}	
			}
		}
	}
	
	private void propegateNonEssential() {
		while(!Que.isEmpty()) {
			Pair pairX = Que.pop();
			Integer x = pairX.getIndex();
			Integer d = pairX.getDomainValue();
			
			for (PreConstraint c : constraints) {
				if (c.getVariables().contains(x)) {
					Variable varx = c.getVariable(x);
					HashSet<Assignment> xAssignments = new HashSet<Assignment>(varx.getAssignments(d));
					for (Assignment a : xAssignments) {
						varx.removeAssignment(d, a);
						for (Integer y : c.getVariables()) {
							if (y != x) {
								Integer valueY = a.getAssignedValue(y);
								Variable vary = c.getVariable(y);
								
								vary.removeAssignment(valueY,a);
								
								if (vary.getAssignments(valueY).isEmpty()) {
									
									if (removeFromDomain(y, valueY)) {
										Pair pairY = new Pair(y, valueY);
										Que.addLast(pairY);
									};
								}		
							}
						}
					}
				}
			}
		}
	}

	public LinkedList<Pair> getQue() {
		return Que;
	}

	public HashSet<PreConstraint> getConstraints() {
		return constraints;
	}
	
	private Boolean removeFromDomain(Integer index, Integer domainValue) {
		if (domainMap.get(index).contains(domainValue)) {
			domainMap.get(index).remove(domainValue);
			return true;
		}
		return false;
	}
	
	private void setPreConstAssignments() {
		for (PreConstraint c : constraints) {
			c.setPreAssignments();
		}
	}
	
	private void setPostConstAssignments() {
		for (PreConstraint c : constraints) {
			c.setPostAssignments();
		}
	}
	
	private void findOrdering() {
		variableOrdering = new ArrayList<Integer>();
		for(int i = 1; i < 10; i++) {
			for(int j = 1; j < 10; j++) {
				variableOrdering.add(i*10 + j);
			}
		}
		
		Collections.sort(variableOrdering, new variableComparator());
	}
	
	private class variableComparator implements Comparator<Integer> {

		@Override
		public int compare(Integer var1, Integer var2) {
			if (domainMap.get(var1).size() < domainMap.get(var2).size()) return -1;
			if (domainMap.get(var1).size() > domainMap.get(var2).size()) return 1;
			return 0;
		}
		
	}
	
	/* Grabs the set of filters for depth d starting from 0 */
	public HashSet<PreConstraint> currentFilters(int d) {
		HashSet<PreConstraint> filters = new HashSet<PreConstraint>();
		Integer assigned = variableOrdering.get(d);
		for (PreConstraint constraint : constraints) {
			if (constraint.getVariables().contains(assigned)) {
				filters.add(constraint);
			}
		}
		return filters;
	}
	
	public ArrayList<Integer> getVariableOrder() {
		return variableOrdering;
	}
	
	public HashMap<Integer,HashSet<Integer>> getDomainMap() {
		return domainMap;
	}
	
	public void generateFilters() {
		for (PreConstraint constraint : constraints) {
			constraint.orderVariables(variableOrdering);
			constraint.createFilterMap();
		}
	}
}
