


public class RegexResult {
	RegexEvaluation.MATCH_RESULT_STATE resultstate = null;
	
	int matchStartIdx = -1;
	int offsetY = -1;
	int offsetX = -1;
	int playerid = -1;
	
	public RegexResult(int findStart, int offsetY, int offsetX, RegexEvaluation.MATCH_RESULT_STATE state, int player) {
		super();
		this.matchStartIdx = findStart;
		this.offsetY = offsetY;
		this.offsetX = offsetX;
		this.resultstate = state;
		this.playerid = player;
	}
}
