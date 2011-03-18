

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameState{
		int turn =-1;
		Integer hashCode;
		byte[][] state;
		// The column in which a coin was inserted to get into this state. 
		int action;
		Integer parent= null;
		List<Integer> children = null; 
		double utilityPlayer1 = 0.0;
		double utilityPlayer2 = 0.0;
		int depth = 0;

		public GameState(byte[][] state) {
			this.hashCode =	Arrays.deepHashCode(state);
			this.state = state;
			children = new ArrayList<Integer>();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("GameState [turn=").append(turn)
			.append(", hashCode=").append(hashCode).append(", state=")
			.append(Arrays.toString(state)).append(", action=")
			.append(action).append(", parent=").append(parent)
			.append(", utilityMIN=").append(utilityPlayer1)
			.append(", utilityMAX=").append(utilityPlayer2)
			.append(", depth=").append(depth).append("]");
			return builder.toString();
		}
		
		/***
		 * 
		 * 
		 */
		public GameState createGamestate(int newCol, int playerid) {
			byte[][] newState = GameHelper.copyArray(this.state);
			for (int i = this.state.length -1; i>= 0; i--) {
				if (this.state[i][newCol] == 0) newState[i][newCol] = (byte) playerid; 
			}
			GameState newGS = new GameState(state);
			newGS.parent = this.hashCode;
			newGS.action = newCol;
			this.turn = playerid;
			
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
}