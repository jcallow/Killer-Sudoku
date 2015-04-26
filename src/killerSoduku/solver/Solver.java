package killerSoduku.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import killerSoduku.solver.data.ConflictSet;
import killerSoduku.solver.data.Constraint;
import killerSoduku.solver.data.DomainSet;
import killerSoduku.solver.data.DomainValue;
import killerSoduku.solver.data.Generator;
import killerSoduku.solver.data.Variable;

public class Solver {
	int stepCount = 0;
	int maxSteps = 0;
	int furthestDown = 0;
	int narratorModeStep = 0;
	int narratorModeStop = 0;
	int MAX_DEPTH = 80; //81 -1 as indexing starts at 0
	ConflictSet EMPTY_SET = new ConflictSet();
	DomainValue NO_MORE_VALUES = null;
	
	HashMap<Integer, Variable> variableMap = new HashMap<Integer, Variable>();
	ArrayList<Variable> assignmentOrder = new ArrayList<Variable>(81);
	ArrayList<Generator> generators = new ArrayList<Generator>(81);
	ArrayList<Integer> solution = new ArrayList<Integer>(81);
	boolean foundSolution = false;
	
	public void addGenerator(ArrayList<Constraint> filters, int i) {
		Generator generator = new Generator(assignmentOrder.get(i), filters);
		generators.add(i, generator);	
	}
	
	public void initializeVariables(HashMap<Integer, HashSet<Integer>> domainMap) {
		for (int i = 1; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				Integer index = 10*i + j;
				HashSet<Integer> domain = domainMap.get(index);
				DomainSet domainSet = new DomainSet(domain);
				variableMap.put(index, new Variable(domainSet, index)); 
			}
		}
	}
	
	public void setVariableOrder(ArrayList<Integer> order) {
		for (int i = 0; i < order.size(); i ++) {
			Integer index = order.get(i);
			assignmentOrder.add(variableMap.get(index));
		}
	}
	
	public void run(int narratorModeStop) {
		this.narratorModeStop = narratorModeStop;

		while(!foundSolution) {
			furthestDown = 0;
			stepCount = 0;
			maxSteps = 1000;
			randomStart();
			extendAssignment(0);
		}
		
		printSolution();
	}

	public ConflictSet extendAssignment(int currentDepth) {
		if (currentDepth > furthestDown) {
			furthestDown = currentDepth;
			maxSteps = maxSteps + 2500*currentDepth;
		}
		
		narratorPrintEnterDepth(currentDepth);
		
		Generator generator = generators.get(currentDepth);
		generator.assignCount(0);
		ConflictSet conflictSet = null;  
		
		while (assignVariable(generator, currentDepth)) {
			if (currentDepth == MAX_DEPTH) {
				recordSolution();
				return EMPTY_SET;
			} else if (stepCount > maxSteps) {
				return EMPTY_SET;
			} else {
				conflictSet = extendAssignment(currentDepth+1);
			} 
			if(conflictSet.isEmpty()) {
				return EMPTY_SET;
			}
			//BackJump
			if(!conflictSet.contains(generator.getVariable()))  {
				generator.setWorkingHypothesis(generator.getVariable().getValue());
				
				narratorPrintBackjump(currentDepth);
				return conflictSet;
			}
			conflictSet.remove(generator.getVariable());
			narratorPrintVariableRemovedFromConflict(currentDepth, generator.getVariable());
			conflictSet.stepAssigned(stepCount);
			generator.getVariable().getValue().setConflictSet(conflictSet);
		}
		narratorPrintFailure(currentDepth);
		return generator.getVariable().getConflictSets();
	}
	
	public Boolean assignVariable(Generator generator, int currentDepth) {
		DomainValue  domainValue;
		ConflictSet conflictSet = null;
		
		while ((domainValue = selectNextAssignment(generator)) != NO_MORE_VALUES) {
			stepCount++;
			conflictSet = filterCurrentAssignment(generator.getFilters(), currentDepth);
			
			if(conflictSet == null) {
				domainValue.setConflictSet(null);
				narratorPrintVariableAssigned(generator.getVariable());
				return true;
			} else {
				conflictSet.stepAssigned(stepCount);
				domainValue.setConflictSet(conflictSet);
				narratorPrintConflictCreated(domainValue, conflictSet, currentDepth);
			}
		}
		return false;
	}
	
	

	public DomainValue selectNextAssignment(Generator generator) {
		if (generator.getAssignementCount() == generator.getVariable().getDomainSet().size()) {
			return NO_MORE_VALUES;
		}
		/* Changed logic so that the working hypothesis is moved to front of list to check instead of having
		 * logic for both working hypothesis and then the other values.
		 */
		
		//DomainValue domainValue = generator.getWorkingHypothesis();
		DomainValue domainValue;
		while(generator.getAssignementCount() < generator.getVariable().getDomainSet().size()) {
			domainValue = generator.getDomainValue();
			generator.incrementCount();
			if (hasRecentlyReassigned(domainValue.getConflictSet())) {
				generator.getVariable().setDomainValue(domainValue, stepCount);
				return domainValue;
			}
		}
		return NO_MORE_VALUES;
	}
	
	public Boolean hasRecentlyReassigned(ConflictSet conflictSet) {
		if (conflictSet == null) return true;
		for (Variable var : conflictSet.getVariables()) {
			if (var.getLastAssigned() > conflictSet.getStepAssigned()) {
				return true;
			}
		}
		return false;
	}
	
	public ConflictSet filterCurrentAssignment(ArrayList<Constraint> filters, int currentDepth) {
		Integer var = assignmentOrder.get(currentDepth).getIndex();
		for (Constraint constraint : filters) {
			if(!constraint.checkAssignment(var, variableMap)) {
				return new ConflictSet(constraint.getVariables(var, variableMap));
			}
		}
		return null;
	}
	
	public void recordSolution() {
		foundSolution = true;
		for (int i = 1; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				Integer index = i*10 + j;
				Variable var = variableMap.get(index);
				Integer solutionValue = var.getValue().getDomainValue();
				solution.add(solutionValue);
			}
		}
	}
	
	public void printSolution() {
		StringBuilder solutionString = new StringBuilder();
		solutionString.append("\nSolution: \n\n");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j<9; j++) {
				Integer index = i*9 + j;
				solutionString.append(solution.get(index) + " ");
			}
			solutionString.append("\n");
		}
		System.out.println(solutionString.toString());
	}

	private void narratorPrintEnterDepth(int currentDepth) {
		
		if (narratorModeStep >= narratorModeStop) {
			return;
		}
		narratorModeStep++;
		System.out.println("\nStep " + narratorModeStep);
		StringBuilder narrator = new StringBuilder();
		narrator.append("Entering depth: " + currentDepth + "\n");
		narrator.append("Variable index: ");
		for (int i = 0; i < currentDepth; i++) {
			narrator.append(assignmentOrder.get(i).getIndex() + " ");
		}
		narrator.append("\n");
		narrator.append("Variable value: ");
		for (int i = 0; i < currentDepth; i++) {
			narrator.append(assignmentOrder.get(i).getValue().getDomainValue() + "  ");
		}
		narrator.append("\n");
		System.out.print(narrator.toString());
		
	}
	
	private void narratorPrintBackjump(int currentDepth) {
		if (narratorModeStep >= narratorModeStop) {
			return;
		}
		narratorModeStep++;
		System.out.println("\nStep " + narratorModeStep);
		System.out.print("Backjumped depth: " + currentDepth + ", now at depth: " + (currentDepth-1) + "\n");
	}
	
	private void narratorPrintFailure(int currentDepth) {
		if (narratorModeStep >= narratorModeStop) {
			return;
		}
		narratorModeStep++;
		System.out.println("\nStep " + narratorModeStep);
		Generator generator = generators.get(currentDepth);
		StringBuilder failureReport = new StringBuilder();
		failureReport.append("Failed to assign a value at depth: " + currentDepth + ".\n");
		failureReport.append("Conflicting Variables: " + generator.getVariable().getConflictSets().printConflictSet() + "\n");
		System.out.print(failureReport.toString());
	}
	
	private void narratorPrintConflictCreated(DomainValue domainValue,
			ConflictSet conflictSet, int currentDepth) {
		if (narratorModeStep >= narratorModeStop) {
			return;
		}
		StringBuilder conflictCreated = new StringBuilder();
		conflictCreated.append("Conflict set created for variable " + generators.get(currentDepth).getVariable().getIndex()
				+ ".  Unable to assign it value: " + domainValue.getDomainValue() + ".\n");
		conflictCreated.append("Conflicting variables: " + conflictSet.printConflictSet());
		System.out.print(conflictCreated.toString() + "\n");
		
	}
	
	private void narratorPrintVariableRemovedFromConflict(int currentDepth,
			Variable variable) {
		if (narratorModeStep >= narratorModeStop) {
			return;
		}
		System.out.println("Variable " + variable.getIndex() + " removed from current conflict set");
		
	}
	
	private void narratorPrintVariableAssigned(Variable variable) {
		if (narratorModeStep >= narratorModeStop) {
			return;
		}
		System.out.println("Variable " + variable.getIndex() + " assigned value: " + variable.getValue().getDomainValue());
	}
	
	private void randomStart() {
		for (Variable variable : assignmentOrder) {
			Collections.shuffle(variable.getDomainSet().getDomainList());
			for (DomainValue value: variable.getDomainSet().getDomainList()) {
				value.setConflictSet(null);
			}
		}
	}
}
