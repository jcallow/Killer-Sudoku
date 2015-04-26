package killerSoduku.solver.data;

public class Variable {
	private DomainValue value; //currently assigned value
	private int lastAssigned; //step variable was last assigned
	private DomainSet domain;
	private Integer index;
	
	public Variable(DomainSet domain, Integer index) {
		this.domain = domain;
		this.index = index;
	}
	
	public DomainValue getValue() {
		return value;
	}

	public ConflictSet getConflictSets() {
		ConflictSet conflictSet = new ConflictSet();
		for (DomainValue dv : domain.getDomainList()) {
			conflictSet.union(dv.getConflictSet());
		}
		
		return conflictSet;
	}


	public DomainSet getDomainSet() {
		return domain;
	}


	public void setDomainValue(DomainValue domainValue, int stepAssigned) {
		value = domainValue;
		lastAssigned = stepAssigned;
	}
	
	public int getLastAssigned() {
		return lastAssigned;
	}
	
	public Integer getIndex() {
		return index;
	}
}
