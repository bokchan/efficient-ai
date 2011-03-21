import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/***
 * Save the gamestate in bytearray to save space.
 * In the standard 7x6 board each gamestate would occupy 7*6*32 = 1344byte  
 * With a bytearray it is 4 times less : 7*6*8 = 336byte
 * If we do explore six moves: 
 * The memore load for the byte[][] is = 336 * 7^6 = 37.6988068 MB 
 * The memore load for the int[][] is = 336 * 7^6 = 150.795227 MB
 *  
 *    
 * Solution: One byte is used to represent 3 columns fields on the board:
 * 2 bits are overhead xx  [xx000000]. Thus ie. partial gamestate below    
 * 1110011
 * 2120012
 * 1211221
 * byte1 [00011001][00100101][00011001][00010000][00100101][00011001]
 * For a full representation of a gamestate we would need 
 * 2 * 8([6 bit used 2 bits overhead])  per columns * 7 = 102 bits
 * Thus the memoryload of 7^6 gamestates would be :´´ 
 * 102 * 7^6 = 1.43053508 MB Thats freakin nice:)
 * 
 * 
 * A similar calculation for a String representation is  
 * 8 (for the String class) + 16 (for the char[]) + 42 * 2 (for the characters) + 4 (value) + 4 (offset) + 4 (count) + 4 (hash) = 124 bytes
 * 124byte * 7^6 = 13.9126549MB, about a factor 10  
 * 
 * TODO: Check the chapter on datacompression in Algorithms
 * For one most gamestates in the beginning of the game, could be an idea only to store the part of the array which is filled out
 * Works if the state is stored as an one dimensional array
 * 
 * TODO: Optimize Regex Patterns
 * a: Precompile
 * b: Use possessive quantifiers
 * c: (?: not grab text in groups
 * d: Avoid backtracking    
 * http://blog.stevenlevithan.com/archives/regex-optimization
 * http://www.javaworld.com/javaworld/jw-09-2007/jw-09-optimizingregex.html#resources
 * http://www.javaworld.com/javaworld/jw-09-2007/jw-09-optimizingregex.html
 * 
 *  
 *  Tested Arrays.deepHashCode on 7^6 byte[][] arrays : 50 ms, not a bottleneck
 *  Tested Arrays.deepHashCode on 7^7 byte[][] arrays : 176 ms, not a bottleneck
 *  
 * @author Andreas
 *
 */
public class GameHeuristics {
	private int cols = 0;
	private int rows = 0;
	private static int playerID;
	private static final int UTILITY_MAX = 20;
	private static final int UTILITY_MIN = -20;


	private Map<Integer, GameState> explored;
	private Queue<GameState> frontier;
	private Queue<GameState> exploredCleanupList;
	private SubGoals subgoals; 
	private int cutOff = 3;
	private enum GAMETYPE {MIN, MAX}; 

	private RegexEvaluation threeInARow = new RegexEvaluation("RegexThreeInARow.xml", RegexEvaluation.MATCH_TYPE.THREE_IN_A_ROW);
	private RegexEvaluation killermoves = new RegexEvaluation("RegexKillerMovesMinimal.xml", RegexEvaluation.MATCH_TYPE.KILLER_MOVE);
	private RegexEvaluation fourInARow = new RegexEvaluation("RegexFourInARow.xml",RegexEvaluation.MATCH_TYPE.FOUR_IN_A_ROW);
	private RegexEvaluation basicMoves = new RegexEvaluation("RegexBasicMoves.xml",RegexEvaluation.MATCH_TYPE.BASIC);

	private GameState current;
	private GameState next;
	private GAMETYPE type;   

	public GameHeuristics(int cols, int rows, int playerid) {
		this.cols = cols;
		this.rows = rows;
		explored = new HashMap<Integer, GameState>();
		exploredCleanupList = new LinkedList<GameState>();
		frontier = new LinkedList<GameState>();



		setPlayerID(playerid);
		type = getPlayerID() == 1 ? GAMETYPE.MIN : GAMETYPE.MAX;

		getOpponentPlayerID();
		subgoals = (getPlayerID() == 1)  ? new SubGoals(SubGoals.TYPE.MINGOALS) : new SubGoals(SubGoals.TYPE.MAXGOALS);

		updateCurrentState(new GameState(new byte[rows][cols]));
	}

	/***
	 * TODO: Implement,  
	 * Function that receives an explored board state isOpponent
	 * Search till all leaves within the cutoff have been explored or a goal state has been reached
	 *   
	 * @param gamestate
	 * @return
	 */
	private boolean exploreBoardGameState()  
	{
		if (explored.containsKey(current.hashCode)) {
			// Get the current subgoal and search using backtracking in explored    
			GameState gamestate = frontier.poll();
			if ((next = isOnSubGoalPath(gamestate)) !=null){
				return true;
			} else {
				return false;
			}
		}else {
			return false;
		}
	}
	/***
	 * Function that receives an explored board state isOpponent
	 * Search till all leaves within the cutoff have been explored or a goal state has been reached
	 *   TODO: Avoid illegal gamestate moves
	 * @param gamestate
	 * @return
	 */
	private void exploreBoardGameState(int player) 
	{
		// Get the head of the queue
		// Check if the depth is ok, keep dequeueing or what
		GameState gamestate = frontier.poll();
		// There is a limit on the size of the frontier
		if (gamestate != null && frontier.size() < Math.pow(7, 5))
		{   	
			// We alternate the turn 
			player = (player == playerID) ? getOpponentPlayerID() : playerID;
			for (int col = 0; col < cols; col++) 
			{
				for (int row = rows-1; row >0; row--) {
					// Iterate through columns top to bottom
					// If we encounter an empty field, explore it
					if (gamestate.state[row][col] == 0) {
						GameState newGS  = gamestate.createGamestate(col, player);

						// Don't add already added states
						if (!frontier.contains(newGS)) {
							// Assign utility

							assignUtility(newGS);
							/* put gamestate on frontier
							 TODO: Prune
							 Prune criterias
							 1. Only add unique states to the frontier
							 2. Only add states based on the specific MinMax/MaxMin value of the state
							 	2.a All patterns have the same utility
							 	2.b Depth is used as a factor, (1+(1/delta_depth)   

							 If utility is acceptable add as child and add to gamestate
							 */
							gamestate.addToChildren(newGS);

							if ((newGS.depth - current.depth) <= cutOff) {
								addGameStateToSubgoals(newGS);
								addGameStateToFrontier(newGS);
							}
						}
						break;
					}
				}

			}
			// Add state to explored
			explored.put(gamestate.hashCode, gamestate);
			GameHelper.Trace(
					"Explored so far" + explored.size()+ 
					"\nFrontier size:" + frontier.size() + 
					"\nDepth:" + gamestate.depth);
			// call exploregamestate again, going depth first;
			exploreBoardGameState(player);
		} else {
			GameHelper.Trace("Frontier limit reached: ");
			GameHelper.Trace("Subgoals: " + Arrays.toString(subgoals.states.toArray()));
			// Of the explored states get the one with the best minmax, then search backwards to child of current
			next = getNextByParent(subgoals.getSubGoal());
			return;
		}
	}

	private GameState getNextByParent(GameState subgoal) {
		GameHelper.Trace("getNextByParent: " + subgoal.parent);
		GameHelper.Trace("current: " + current.hashCode);
		GameHelper.Trace("Size of explored: "  + explored.size());

		GameState gBack = explored.get(subgoal.parent);

		// Get the optimal child of the current node 
		while(gBack != null) {
			// We hit the child of the current node 
			if (gBack.parent.equals(current.hashCode)) {
				GameHelper.Trace("Found the child of the current node for our subgoal"); 
				break;
			} else { 
				gBack = explored.get(gBack.parent);
			}
		}
		return gBack;
	}

	/***
	 * For now only for the first player
	 * 
	 * @param state
	 */
	private void assignUtility(GameState state) 
	{
		int deltaDepth = state.depth  - current.depth;
		// Get state as string
		String strState = state.stateAsString(); 

		//		RegexResult result = fourInARow.match(strState, state.turn);
		//		if (result != null) {
		//			if (result.resultstate.equals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON)) {
		//				state.utilityMin = -8;
		//				state.utilityMin = 0;
		//			} else {
		//				state.utilityMin = 0;
		//				state.utilityMin = 8;
		//			}
		//			return;
		//		}

		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent
		 * case players turn : playerthreeinarow / opponentthreeinarow
		 * case opponents turn : playerthreeinrow / opponentthreeinarow 
		 *    
		 */
		RegexResult resultMax  = threeInARow.match(strState, getOpponentPlayerID());
		RegexResult resultMin = threeInARow.match(strState, getPlayerID());

		if (resultMax != null) {
			// Opponent has 3 in a row  
			if (state.turn == getPlayerID()) {
				if (type.equals(GAMETYPE.MIN)) {
					state.utilityMax = UTILITY_MAX- deltaDepth;
				} else {
					state.utilityMin = UTILITY_MIN + deltaDepth;
				}
			}else {
				if (resultMin!= null)
					state.utilityMin = UTILITY_MIN - deltaDepth; 
			}
		}

		/***
		 * check for killer move
		 */
		resultMax = killermoves.match(strState, getOpponentPlayerID());
		resultMin = killermoves.match(strState, getPlayerID());
		if (resultMax != null){
			if (resultMax.resultstate.equals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1KILLERMOVE)) {
				state.utilityMin = -8;
				state.utilityMax = 0;
			} else {
				state.utilityMin = 0;
				state.utilityMax = 8;
			}
		}

		resultMax = basicMoves.match(strState, state.turn);
		if (resultMax!=null) {
			if (resultMax.resultstate.equals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1PREKILLER)) {
				state.utilityMin = -8;
				state.utilityMax = 0;
			} else {
				state.utilityMin = -8;
				state.utilityMax = 0;
			}
		}
		return;
	}

	/***
	 * Evaluates utility based on a state 
	 * @param g GameState 
	 * @return
	 */
	private boolean MinMax(GameState g) {
		if (g.turn == 1) {
			return g.utilityMin == -8;   
		} else {
			return g.utilityMax == 8;
		}	
	}
	/***
	 * 
	 * Evaluates the gamestate. Only public access. Is called from GameLogic. 
	 * @param state
	 */

	public int evaluateGameState()
	{
		Stopwatch w = null;
		GameHelper.Trace("Evaluating gamestate");
		// Clear all objects from the frontier; 
		frontier.clear();
		// clean up subgoals
		subgoals.cleanup();
		// trim away irrelevant gamestates  
		trimExplored();

		// Get state as string
		String strState = current.stateAsString();

		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */
		RegexResult result = threeInARow.match(strState, getOpponentPlayerID());
		if (result != null) {
			// Return blocking move
			return getMove(result);
		}
		/***
		 * Check if we have three in a row
		 */

		result = threeInARow.match(strState, playerID);
		if(result != null ) {
			// Clean up subgoals before returning move
			return getMove(result);
		} 
		else 
		{
			/*** 
			 * Check if the current gamestate is still on the 
			 * path to our current subgoal using backtracking in the explored list 
			 * If true 
			 * 		Find the current gamestate in explored and return its child
			 * Else 
			 * 		Search the possible gamestates using current as base   
			 */

			addGameStateToFrontier(current);
			w = new Stopwatch();
			GameHelper.Trace("Added state to frontier");
			if (exploreBoardGameState()) {
				GameHelper.Trace("Searching for state amongst already explored states");
				GameHelper.Trace( "Time to decide: " +  w.elapsedTime());
				return next.action;
			} else {
				exploreBoardGameState(playerID);
				GameHelper.Trace( "Time to decide: " +  w.elapsedTime());
				return next.action;
			}

		}		
	}

	/****
	 * Adds gamestate to frontier
	 * @param state
	 */
	private void addGameStateToFrontier(GameState state) {
		frontier.add(state);
	}

	private void addGameStateToSubgoals(GameState state) {
		subgoals.add(state);
	}

	public Queue<GameState> getFrontier() {
		return frontier;
	}

	public Map<Integer,GameState> getExplored() {
		return explored;
	}

	/***
	 * Updates the current state of the board. Is called from IGameLogic insertcoin
	 * @param move column id 0-based 
	 * @param player id of the player (1|2) 
	 */
	public void updateCurrentState(int move, int player) {
		GameHelper.Trace("Player: " + player + "Move: " + move);
		GameState g = current.createGamestate(move, player);
		current = g;
		GameHelper.Trace("Board \n" + current.stateAsStringMatrix());	
	}

	/***
	 * Called from the explore methods
	 * @param state
	 */
	private void updateCurrentState(GameState state){
		// Implicitly updates gamestate by asignning and evaluting at the same time
		GameHelper.Trace("Current state updated");
		current = state; 
	}

	private int getMove(RegexResult result) {
		int col = (result.matchStartIdx % cols) + result.offsetX; 
		return col;
	}

	private int getOpponentPlayerID(){
		if (type.equals(GAMETYPE.MIN)){
			return 2;

		} else {
			return 1;
		}
	}
	public IGameLogic.Winner getWinner() {
		// Get state as string
		String strState = current.stateAsString();

		// 	Has opponent won 
		RegexResult result= fourInARow.match(strState, current.turn);
		if (result !=null) {
			if (current.turn == 1)
				return IGameLogic.Winner.PLAYER1;
			else 
				return IGameLogic.Winner.PLAYER2; 
		}

		return IGameLogic.Winner.NOT_FINISHED;
	}

	public static void setPlayerID(int id) {
		playerID = id;
	}

	public static int getPlayerID() {
		return playerID;
	}

	private GameState isOnSubGoalPath(GameState gs) {
		GameState state = subgoals.getSubGoal();
		if (state != null) {

			while(state.parent != null &&  state.depth > gs.depth) {
				if (state.parent.equals(gs.hashCode)) {
					return state;
				}
				state = explored.get(state.parent);
			}
		} 
		return null;
	}

	/***
	 * Backtracks from current state, removes al siblings of the current state
	 * Recursively removes all irrelevant gamestates   
	 */
	private void trimExplored() {
		GameState gs = exploredCleanupList.poll();
		if (gs != null) {
			// Get the parent of the current
			for (Integer hash :  gs.children) {
				if (!hash.equals(current.hashCode) ) {
					exploredCleanupList.add(explored.get(hash));
				}
			}
			explored.remove(gs);
			trimExplored();
		}
	} 

	//Section public interface for Testing 

	public RegexResult matchFourInARow(String input, int player) {
		return fourInARow.match(input, player);
	}

	public RegexResult matchThreeInARow(String input, int player) {
		return threeInARow.match(input, player);
	}
	public RegexResult matchKillerMoves(String input, int player) {
		return killermoves.match(input, player);
	}
}