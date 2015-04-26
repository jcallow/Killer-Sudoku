package killerSoduku.solver.data;

public class DomainValue {
	private int value;
	private ConflictSet conflictSet = null;
	
	public DomainValue(Integer value) {
		this.value = value;
	}
	
	public void setConflictSet(ConflictSet conflictSet) {
		this.conflictSet = conflictSet;
	}

	public ConflictSet getConflictSet() {
		return conflictSet;
	}
	
	public Integer getDomainValue() {
		return value;
	}
}
