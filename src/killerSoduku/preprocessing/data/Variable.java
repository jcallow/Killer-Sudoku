package killerSoduku.preprocessing.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Variable {
	HashMap<Integer, HashSet<Assignment>> AssignmentsMap;  // Domain to assignments map;
	
	public Variable() {
		AssignmentsMap = new HashMap<Integer, HashSet<Assignment>>();
		initialize();
	}
	
	public void initialize() {
		for(int i = 1; i <= 9; i++) {
			AssignmentsMap.put(i, new HashSet<Assignment>());
		}
		
	}
	
	public Set<Integer> getDomain() {
		return AssignmentsMap.keySet();
	}
	
	// gets assignements that satisfy a specific value
	public HashSet<Assignment> getAssignments(Integer domainValue) {
		return AssignmentsMap.get(domainValue);
	}
	
//	public void removeFromDomain(Integer domainValue) {
//		AssignmentsMap.remove(domainValue);
//	}
	
	public void removeAssignment(Integer domainValue, Assignment assignment) {
		try {
			AssignmentsMap.get(domainValue).remove(assignment);
		
		} catch(Exception e) {
			for (Integer i : assignment.getAssignmentMap().keySet()) {
				System.out.println(i);
			}
		}
	}
	
	public void addAssignment(Integer domainValue, Assignment assignment) {
		
		if (AssignmentsMap.containsKey(domainValue)) {
			AssignmentsMap.get(domainValue).add(assignment);
		} else {
			System.out.println(domainValue + " out of bounds");
		}
	}
	
	public int getNumAssignments() {
		int number = 0;
		
		for (HashSet<Assignment> assignment : AssignmentsMap.values()) {
			number += assignment.size();
		}
		
		return number;
	}
	
	//grabs set of all assignments for the constraint/variable
	public HashSet<Assignment> getAssignments() {
		HashSet<Assignment> assignments = new HashSet<Assignment>();
		for (HashSet<Assignment> assignmentList : AssignmentsMap.values()) {
			assignments.addAll(assignmentList);
		}
		return assignments;
	}
}
