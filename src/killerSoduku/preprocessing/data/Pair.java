package killerSoduku.preprocessing.data;

public class Pair {
	Integer index;
	Integer domainValue;
	
	public Pair (Integer index, Integer domainValue) {
		this.index = index;
		this.domainValue = domainValue;
	}
	
	public Integer getIndex() {
		return index;
	}
	
	public Integer getDomainValue() {
		return domainValue;
	}
}
