

import java.util.regex.Pattern;

public class RegexExpression{
	private String description;
	private Pattern patternPlayer1;
	private Pattern patternPlayer2;
	private int offsetX = 0;
	private int offsetY = 0;
	private int modMin = -1;
	
	/***
	 * 1010
	 * 1xx1011
	 * xxxxxx1
	 * 
	 * 0111010
	 * 2211221
	 */
	private int modMax = -1;
	
	public RegexExpression(String p, int offsetY,int offsetX, int modMin, int modMax) {
		this.patternPlayer1 = Pattern.compile(p.replace("VAR", "1"));
		this.patternPlayer2 = Pattern.compile(p.replace("VAR", "2"));
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
	
	public int getXOffset() {
		return this.offsetX;
	}

	public int getyOffset() {
		return offsetY;
	}
	public int getModMin() {
		return modMin;
	}

	public int getModMax() {
		return modMax;
	}
}