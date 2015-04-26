package killerSoduku.solver.data;

import java.util.ArrayList;

public class Generator {
	ArrayList<Constraint> filters;
	Variable variable; 
	int assignCount;
	
	public Generator(Variable variable, ArrayList<Constraint> filters) {
		this.variable = variable;
		this.filters = filters;
	}
	
	public void assignCount(int i) {
		assignCount = i;	
	}

	public Variable getVariable() {
		return variable;
	}

	public void setWorkingHypothesis(DomainValue value) {
		variable.getDomainSet().moveHypothesisToFront(value);
	}

	public ArrayList<Constraint> getFilters() {
		return filters;
	}

	public int getAssignementCount() {
		return assignCount;
	}

	public void incrementCount() {
		assignCount++;	
	}

	public DomainValue getDomainValue() {
		DomainSet set = variable.getDomainSet();
		return set.getDomainList().get(assignCount);
	}	
}
