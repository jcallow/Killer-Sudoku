package killerSoduku.preprocessing.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PreConstraint {
	HashMap<Integer, Variable> variablesMap;  // map between variable puzzle index and the data it stores for this constraint
	String type;
	int preArcAssignments;
	int postArcAssignments;
	String namepostfix;
	
	ArrayList<Integer> variableOrder;
	HashMap<Integer, HashSet<Integer>> filterMap;
	
	public PreConstraint() {
		variablesMap = new HashMap<Integer, Variable>();
	}
	
	public Set<Integer> getVariables() {
		return variablesMap.keySet();
	}
	
	public Variable getVariable(Integer x) {
		return variablesMap.get(x);
	}
	
	public void addVariable(Integer index) {
		variablesMap.put(index, new Variable());
	}
	
	public void setPreAssignments() {
		preArcAssignments = getNumberAssignments();
	}
	
	public void setPostAssignments() {
		postArcAssignments = getNumberAssignments();
	}
	
	private int getNumberAssignments() {
		ArrayList<Integer> testSet = new ArrayList<Integer>();
		for (Variable index : variablesMap.values()) {
			testSet.add(index.getNumAssignments());
		}
		if (!tester(testSet)) {
			return -1;
		}
		return testSet.get(0);
	}
	
	public String getOutput() {
		String output = getName() + "-" + type + "-Variables{";
		ArrayList<Integer> indices = new ArrayList<Integer>(variablesMap.keySet());
		Collections.sort(indices);
		for (Integer variable : indices) {
			output += variable + ",";
		}
		output = output.substring(0, output.length()-1);
		output += "}-Before:" + preArcAssignments + "-After:" + postArcAssignments;
		return output;
	}
	
	public String getName() {
		String name = "c[";
		ArrayList<Integer> indices = new ArrayList<Integer>(variablesMap.keySet());
		Collections.sort(indices);
		for (Integer variable : indices) {
			name += variable + ",";
		}
		name += namepostfix;
		name += "]";
		return name;
	}
	
	public Boolean tester(ArrayList<Integer> testSet) {
		HashSet<Integer> test = new HashSet<Integer>(testSet);
		if (test.size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setNamePostFix(String post) {
		namepostfix = post;
	}
	
	
	/* Order the variables for the constraint in the order they will be assigned, makes making the filterMap easier */
	public void orderVariables(ArrayList<Integer> varOrder) {
		variableOrder = new ArrayList<Integer>();
		
		for (int i = 0; i < varOrder.size(); i++) {
			if (variablesMap.containsKey(varOrder.get(i))) {
				variableOrder.add(varOrder.get(i));
			}
		}
	}
	
	
	/* Filter map works by inputing current variable assigned into the hashmap, then inputting the current assignment of relevant
	 * variables into the hashset to see if it is feasible.  Assignements are integers such as 124, which would mean first var = 1, second = 2
	 * , third = 4.  
	 */
	public void createFilterMap() {
		filterMap = new HashMap<Integer, HashSet<Integer>>();
		Integer currentIndex = variableOrder.get(0);
		Variable currentVar = variablesMap.get(currentIndex);
		HashSet<Assignment> assignments = currentVar.getAssignments();
		
		for (int i = 0; i < variableOrder.size(); i++) {
			HashSet<Integer> set = new HashSet<Integer>();
			for(Assignment assignment : assignments) {
				Integer partialAssignment = 0;
				for (int j = 0; j <= i; j++) {
					if (j == 0) {
						partialAssignment += assignment.getAssignedValue(variableOrder.get(j));
					} else {
						partialAssignment = partialAssignment*10;
						partialAssignment += assignment.getAssignedValue(variableOrder.get(j));
					}
				}
				set.add(partialAssignment);
			}
			filterMap.put(variableOrder.get(i), set);
		}
	}
	
	public HashMap<Integer, HashSet<Integer>> getFilterMap() {
		return filterMap;
	}
	
	public ArrayList<Integer> getVariableOrder() {
		return variableOrder;
	}
	
	public HashMap<Integer, HashSet<Integer>> filterMap() {
		return filterMap;
	}
}
