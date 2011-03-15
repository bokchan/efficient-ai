package andreas.fourconnect.itu;

public class TriangleStrategy extends Strategy {
	
	public TriangleStrategy(String pattern) {
		super(pattern);
	}

	/***
	 * Searches for possible killermoves related to either 
	 * 0111
	 * 0010
	 * 0001

	 * Triangle
	 * Evaluates how many moves a boardstate is from this subgoal   
	 */
	@Override
	public double eval(String s) {		
		return 0.0;
	}
}
