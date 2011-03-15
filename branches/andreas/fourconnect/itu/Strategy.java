package andreas.fourconnect.itu;

public abstract class Strategy {
	private String pattern;
	

	public Strategy(String pattern) {
		this.pattern = pattern;
	}
	/***
	 * Eval the string (board state ) against the regex
	 * @param s boardstate
	 * @return eval for pursuing this strategy
	 */
	public abstract double eval(String s);
}