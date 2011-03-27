package fourconnect;


import java.util.regex.Pattern;

public class RegexExpression implements Comparable<RegexExpression>{
	private String description;
	private String pattern;
	private Pattern patternPlayer1;
	private Pattern patternPlayer2;
	private int offsetX = 0;
	private int offsetY = 0;
	private int modMin = -1;
	private int modMax = -1;

	/***
	 * 1010
	 * 1xx1011
	 * xxxxxx1
	 * 
	 * 0111010
	 * 2211221
	 */


	public RegexExpression(String d, String p, int offsetY,int offsetX, int modMin, int modMax) {
		this.description = d;
		this.pattern = p;
		this.patternPlayer1 = Pattern.compile(p.replace("P", "1"));
		this.patternPlayer2 = Pattern.compile(p.replace("P", "2"));
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.modMin = modMin;
		this.modMax = modMax;
	}

	public Pattern getPattern(int playerid ) {
		if (playerid == 1) return patternPlayer1; 
		else 
			return patternPlayer2;   
	}

	public int getOffsetX() {
		return this.offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}
	public int getModMin() {
		return modMin;
	}

	public int getModMax() {
		return modMax;
	}


	public String getDescription() {
		return description;
	}

	public String getPattern() {
		return pattern;
	}

	public int compareTo(RegexExpression o) {
		
		return ((Integer) this.patternPlayer1.pattern().length())
		.compareTo(o.patternPlayer1.pattern().length());
//		int c = ((Integer) this.patternPlayer1.pattern().length())
//		.compareTo(o.patternPlayer1.pattern().length()); 
//		return c == 0 ? 0 : c < 0 ? 1 : -1; 
	}


}