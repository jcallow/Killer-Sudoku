package killerSoduku.solver.data;

import java.util.ArrayList;

public class ConflictSet {
	private ArrayList<Variable> variables = new ArrayList<Variable>();
	private int stepAssigned;
	
	public ConflictSet(ArrayList<Variable> variables) {
		if (variables != null) {
			this.variables = variables;
		}
	}
	
	public ConflictSet() {
		variables = new ArrayList<Variable>();
	}

	public boolean contains(Variable variable) {
		if (variables.contains(variable)) return true;
		return false;
	}

	public void stepAssigned(int stepCount) {
		stepAssigned = stepCount;
	}

	public void remove(Variable variable) {
		variables.remove(variable);
	}

	public void union(ConflictSet conflictSet) {
		if (conflictSet != null) {
			for (Variable var : conflictSet.getVariables()) {
				if (!variables.contains(var)) {
					variables.add(var);
				}
			}
		}
	}

	public ArrayList<Variable> getVariables() {
		return variables;
	}

	public int getStepAssigned() {
		return stepAssigned;
	}
	
	public String printConflictSet() {
		String output = "";
		for( Variable var : variables) {
			output += var.getIndex() + " ";
		}
		return output;
	}
	
	public boolean isEmpty() {
		return variables.isEmpty();
	}
}
