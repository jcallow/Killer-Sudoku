package killerSoduku;

import java.util.ArrayList;
import java.util.HashSet;

import killerSoduku.preprocessing.PreProcessing;
import killerSoduku.preprocessing.data.PreConstraint;
import killerSoduku.solver.Solver;
import killerSoduku.solver.data.Constraint;

public class Main {
	PreProcessing arcConsistency;
	Solver solver;
	
	public static void main(String args[]) {
		Main main = new Main();
		
		if (args.length == 0) {
			main.run(-1);
		} else {
			try {
				main.run(Integer.parseInt(args[0]));
			} catch(Exception e) {
				System.out.println("invalid input, running without narrator mode");
				main.run(-1);
			}
		}		
	}
	
	public void run(int narratorModeStop) {
		arcConsistency = new PreProcessing();
		arcConsistency.run();
		
		Solver solver = new Solver();
		
		solver.initializeVariables(arcConsistency.getDomainMap());
		solver.setVariableOrder(arcConsistency.getVariableOrder());
		
		passFilters(solver, arcConsistency);

		solver.run(narratorModeStop);
	}
	
	public void passFilters(Solver solver, PreProcessing arcConsistency) {
		for (int i = 0; i < arcConsistency.getVariableOrder().size(); i++) {
			HashSet<PreConstraint> depthiFilters = arcConsistency.currentFilters(i);
			ArrayList<Constraint> filters = new ArrayList<Constraint>();
			for (PreConstraint preConstraint : depthiFilters) {
				Constraint constraint = new Constraint(preConstraint.getVariableOrder(), preConstraint.getFilterMap());
				filters.add(constraint);
			}
			solver.addGenerator(filters, i);
		}
	}
}
