package killerSoduku.preprocessing.data;

import java.util.HashMap;

public class Assignment {
//Map index of variable to assignment in map
		HashMap<Integer, Integer> assignmentMap;
		
		public Assignment() {
			assignmentMap = new HashMap<Integer, Integer>();
		}
		
		public Integer getAssignedValue(Integer x) {
			return assignmentMap.get(x);
		}
		
		public void addVariableAssignment(Integer x, Integer xa) {
			assignmentMap.put(x, xa);
		}

		public HashMap<Integer, Integer> getAssignmentMap() {
			return assignmentMap;
		}
		
		
}
