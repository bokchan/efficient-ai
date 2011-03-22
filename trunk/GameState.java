

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GameState{
	
	int turn =-1;
	Integer hashCode;
	byte[][] state;
	// The column in which a coin was inserted to get into this state. 
	int action;
	Integer parent= null;
	List<Integer> children = null; 
	Integer utilityMin = 0;
	Integer utilityMax = 0;
	Integer depth = 0;

	public GameState(byte[][] state) {
		this.hashCode =	Arrays.deepHashCode(state);
		this.state = state;
		children = new ArrayList<Integer>();
	}

	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameState [turn=").append(turn)
		.append(", hashCode=").append(hashCode).append(", state=")
		.append(Arrays.toString(state)).append(", action=")
		.append(action).append(", parent=").append(parent)
		.append(", utilityMIN=").append(utilityMin)
		.append(", utilityMAX=").append(utilityMax)
		.append(", depth=").append(depth).append("]");
		return builder.toString();
	}


	public byte[][] peekGamestate(int newCol, int playerid) {
		byte[][] newState = GameHelper.copyArray(this.state);
		for (int i = newState.length -1; i>= 0; i--) {
			if (newState[i][newCol] ==  (byte )0) { 
				newState[i][newCol] = (byte) playerid;
				i = -1;
			}
		}
		return newState;
	}  

	/***
	 * 
	 * 
	 */
	public GameState createGamestate(int newCol, int playerid) {
		//TODO: Avoid illegal gamestate moves, return null if is not legal
		byte[][] newState = GameHelper.copyArray(this.state);

		for (int i = newState.length -1; i>= 0; i--) {
			if (newState[i][newCol] == (byte )0) { 
				newState[i][newCol] = (byte) playerid;
				i = -1;
			}
		}

		GameState newGS = new GameState(newState);
		newGS.parent = this.hashCode;
		newGS.action = newCol;
		newGS.turn = playerid;
		newGS.depth = this.depth +1;
		return newGS;
	}
	/***
	 * @param state   
	 * @param newCol colindex of the new move
	 * @param newRow row index of the new move
	 * @param playerid id of the player moving 
	 * 
	 * @return
	 */
	public void addToChildren(GameState state) {
		this.children.add(state.hashCode);
	}

	/***
	 * Returns a byte[][] as a string, starting from bottom left corner of the byte[][]
	 * @param state
	 * @return
	 */
	public  String stateAsString() {
		StringBuilder sb = new StringBuilder();
		for (int row = this.state.length-1; row >= 0; row--) {
			for (int col = 0; col < this.state[0].length; col++)
			{
				int val = state[row][col];
				sb.append(val);   
			}
		}
		return sb.toString();
	}

	public String stateAsStringMatrix() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < state.length; row++) {
			for (int col = 0 ;col < state[0].length; col++) {
				sb.append(state[row][col]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}

class MinComparator implements Comparator<GameState> {
	public int compare(GameState o1, GameState o2) {
		int c= o1.utilityMin.compareTo(o2.utilityMin);
		return c==0 ? 0 : c > 0 ? -1 : 1; 
	}
}

class MaxComparator implements Comparator<GameState> {
	public int compare(GameState o1, GameState o2) {
		return o1.utilityMax.compareTo(o2.utilityMax);
	}
}