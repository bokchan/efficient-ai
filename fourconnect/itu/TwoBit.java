package andreas.fourconnect.itu;

import java.util.BitSet;

public class TwoBit extends BitSet {
	public TwoBit(boolean i0, boolean i1) {
		super(2);
		
		this.set(0, i0);
		this.set(1, i1);
	}
}
