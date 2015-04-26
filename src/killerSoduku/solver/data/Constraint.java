package killerSoduku.solver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Constraint {
	ArrayList<Integer> variables;
	HashMap<Integer, HashSet<Integer>> filterTables;
	
	public Constraint(ArrayList<Integer> variables, HashMap<Integer, HashSet<Integer>> filterTables) {
		this.variables = variables;
		this.filterTables = filterTables;
	}
	
	public boolean checkAssignment (Integer variable, HashMap<Integer, Variable> variableMap) {
		Integer mapValue = 0;
		
		for (int i = 0; i <= variables.indexOf(variable); i++) {
			mapValue = mapValue * 10;
			mapValue += variableMap.get(variables.get(i)).getValue().getDomainValue();
		}
		HashSet<Integer> checkSet = filterTables.get(variable);
		if (checkSet.contains(mapValue)) {
			return true;
		}
		return false;
	}

	public ArrayList<Variable> getVariables(Integer variable, HashMap<Integer, Variable> variableMap) {
		ArrayList<Variable> vars = new ArrayList<Variable>();
		for (int i = 0; i <= variables.indexOf(variable); i++) {			
			vars.add(variableMap.get(variables.get(i)));
		}
		return vars;
	}
	
	public ArrayList<Integer> getVar() {
		return variables;
	}
}
