package killerSoduku.solver.data;

import java.util.ArrayList;
import java.util.HashSet;

public class DomainSet {
	ArrayList<DomainValue> domainValues = new ArrayList<DomainValue>();
	
	public DomainSet(HashSet<Integer> domain) {
		for (Integer value : domain) {
			DomainValue domainValue = new DomainValue(value);
			domainValues.add(domainValue);
		}
	}

	public ArrayList<DomainValue> getDomainList() {
		return domainValues;
	}

	public int size() {
		return domainValues.size();
	}
	
	public void moveHypothesisToFront(DomainValue hypothesis) {
		DomainValue currentFirst = domainValues.get(0);
		int index = domainValues.indexOf(hypothesis);
		domainValues.set(0, hypothesis);
		domainValues.set(index, currentFirst);
	}
}
