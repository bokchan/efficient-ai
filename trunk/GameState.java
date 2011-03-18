

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
		double utilityMIN = 0.0;
		double utilityMAX = 0.0;
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
			.append(", utilityMIN=").append(utilityMIN)
			.append(", utilityMAX=").append(utilityMAX)
			.append(", depth=").append(depth).append("]");
			return builder.toString();
		}
	

}
