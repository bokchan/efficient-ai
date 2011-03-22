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
 * 
 * For one most gamestates in the beginning of the game, could be an idea only to store the part of the array which is filled out
 * Works if the state is stored as an one dimensional array
 * 
 * 
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
	private Frontier frontier;
	private Queue<GameState> exploredCleanupList;
	private SubGoals subgoals; 
	private int cutOff = 6;
	private enum GAMETYPE {MIN, MAX}; 

	private RegexEvaluation threeInARow = new RegexEvaluation("RegexThreeInARow.xml", RegexEvaluation.MATCH_TYPE.THREE_IN_A_ROW);
	private RegexEvaluation killermoves = new RegexEvaluation("RegexKillerMovesDESC.xml", RegexEvaluation.MATCH_TYPE.KILLER_MOVE);
	private RegexEvaluation fourInARow = new RegexEvaluation("RegexFourInARow.xml",RegexEvaluation.MATCH_TYPE.FOUR_IN_A_ROW);
	private RegexEvaluation basicMoves = new RegexEvaluation("RegexBasicMoves.xml",RegexEvaluation.MATCH_TYPE.BASIC);

	private GameState current;
	private GameState next;
	private GAMETYPE type;
	
	private int subgoalCutoff = 5;    

	private int count = 0; 

	public GameHeuristics(int cols, int rows, int playerid) {
		this.cols = cols;
		this.rows = rows;
		explored = new HashMap<Integer, GameState>();
		exploredCleanupList = new LinkedList<GameState>();
		frontier = new Frontier();

		setPlayerID(playerid);
		type = getPlayerID() == 1 ? GAMETYPE.MIN : GAMETYPE.MAX;

		getOpponentPlayerID();
		subgoals = (getPlayerID() == 1) ? new SubGoals(SubGoals.TYPE.MINGOALS) : new SubGoals(SubGoals.TYPE.MAXGOALS);
		updateCurrentState(new GameState(new byte[rows][cols]));
	}
	/***
	 * 
	 * Function that receives an explored board state isOpponent
	 * Search till all leaves within the cutoff have been explored or a goal state has been reached
	 *   
	 * @param gamestate
	 * @return
	 */
	private boolean exploreBoardGameState()  
	{
		GameHelper.Trace(false, "explored.containsKey(current.hashCode): " + explored.containsKey(current.hashCode));
		if (explored.containsKey(current.hashCode)) {
			// Get the current subgoal and search using backtracking in explored
			GameHelper.Trace(false, "isOnSubGoalPath())" + isOnSubGoalPath());
			if ((next = isOnSubGoalPath()) != null){
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
	 * 
	 * @param gamestate
	 * @return
	 */
	private void exploreBoardGameState(int player) throws Exception 
	{
		// Get the head of the queue
		// Check if the depth is ok, keep dequeueing or what
		
		GameState gamestate = frontier.poll();
		 
		GameHelper.Trace(false, 
				"\nExplored so far recursive: " + explored.size()+ 
				"\nFrontier size recursive:" + frontier.size()+
				"\nSubgoals: " + subgoals.set.size());
		
		// There is a limit on the size of the frontier
		if (gamestate != null && (frontier.size() + explored.size()) < Math.pow(7, 6))
		{   
			// Add state to explored
			explored.put(gamestate.hashCode, gamestate);

			GameHelper.Trace(false, "Put gamestate in explored");
			// We alternate the turn 
			player = (player == playerID) ? getOpponentPlayerID() : playerID;
			for (int col = 0; col < cols; col++)
			{
				for (int row = rows-1; row >0; row--) {
					// Iterate through columns top to bottom
					// If we encounter an empty field, explore it
					if (gamestate.state[row][col] == 0) 
					{
						GameState newGS  = gamestate.createGamestate(col, player);
						// Add as child and add to the gamestate
						gamestate.addToChildren(newGS);
						boolean notContained = !(frontier.containsKey(newGS.hashCode) || explored.containsKey(newGS.hashCode));
						
						// Don't add already added states and cutoff if the gamestate exceeds our cutoff
						// 6 - 1 = 5
						GameHelper.Trace(false, "Depth: cur " + current.depth + "poten; "  + newGS.depth);
						
						if (notContained && (newGS.depth - current.depth) < cutOff) {
							
							addGameStateToFrontier(newGS);
							// Assign utility
							GameHelper.Trace(true, "Before assignutility" + newGS.toString());
							if (assignUtility(newGS)) {
								
								// Add to subgoals if the state.turn == opponent.  
								if (player == getOpponentPlayerID()) addGameStateToSubgoals(newGS);
							}
							GameHelper.Trace(true, "After assignutility" +newGS.toString());
							/* put gamestate on frontier

							 Prune criterias
							 1. Only add unique states to the frontier
							 2. Only add states based on the specific MinMax/MaxMin value of the state
							 	2.a All patterns have the same utility UTILITYMAX
							 	2.b Depth is used as a factor, (+(1/delta_depth)   
							 */  
						}
						break;
					}					
				}
			}
			if (subgoals.set.size() >subgoalCutoff ) decideMove();
			// call exploregamestate again, going depth first;
			exploreBoardGameState(player);
		} else {
			GameHelper.Trace(false, "Frontier limit reached: " + subgoals.getSubGoal());
			
			GameHelper.Trace(false, "Top Subgoal: " + subgoals.getSubGoal());
			GameHelper.Trace(false, "Subgoals: " + subgoals.set.toString());

			// Of the explored states get the one with the best minmax, then search backwards to child of current
			
			decideMove();
			
		}
	}
	
	private void decideMove() {
		next = getNextByParent(subgoals.getSubGoal());
	}

	private boolean MINMAX(GameState gs) {
		// The gamestate is a draw
		//if (gs.utilityMin.equals(gs.utilityMax)) return true;
		if (type.equals(GAMETYPE.MIN)) {
			// Player = min
			if (playerID == gs.turn)
				// Next move = opponents. If move has utilitymax > 0 return false 
				return !(gs.utilityMax > 0);
			else 
				// Next move = player. If move has utilitymin < 0 return true
				return gs.utilityMin < 0;
		} else {
			// Player = Max 
			if (playerID == gs.turn)
				// Next move = opponents. If move has utilitymin < 0 return false
				return !(gs.utilityMin < 0);
			// current move = min, next move = max. If move has utilityMax > 0 return true
			else 
				return gs.utilityMax > 0;
		}
	}

	private GameState getNextByParent(GameState subgoal) {
		GameState gBack = subgoal;
		// Get the optimal child of the current node 
		
		while(gBack.parent != null && gBack.depth > current.depth) {
			// We hit the child of the current node 
			if (gBack.parent.equals(current.hashCode)) {
				GameHelper.Trace(true, "Found the child of the current node for our subgoal");
				break;		
			} else { 
				gBack = explored.get(gBack.parent);
			}
		}
		GameHelper.Trace(false, "Next By parent: ");
		return gBack;
	}
	/***
	 * 
	 * 
	 * @param state
	 */
	private boolean assignUtility(GameState state) 
	{
		//TODO: ASSIGN 0-0 = 0, MATCH(^ownturn) = MID 
		
		// Get state as string
		String strState = state.stateAsString();
		/***
		 * First check opponent 
		 */

		//		RegexResult resultOpponent  = threeInARow.match(strState, getOpponentPlayerID());
		//		RegexResult resultPlayer = threeInARow.match(strState, getPlayerID());
		//		if (!assignUtility(resultOpponent, resultPlayer, state)) {
		//			//check for killer move
		//			resultOpponent = killermoves.match(strState, getOpponentPlayerID());
		//			resultPlayer = killermoves.match(strState, getPlayerID());
		//		} if (!assignUtility(resultOpponent, resultPlayer, state))  {
		//			// Checks for basicmoves
		//			resultOpponent = basicMoves.match(strState, getOpponentPlayerID());
		//			resultPlayer = basicMoves.match(strState, getPlayerID());
		//			assignUtility(resultOpponent, resultPlayer, state);
		//		}

		RegexResult result  = threeInARow.match(strState, getOpponentPlayerID());
		if (!assignUtility(result, state)) {
			//check for killer move
			result = killermoves.match(strState, getOpponentPlayerID());
		} 
		if (!assignUtility(result, state))  {
			// Checks for basicmoves
			result = basicMoves.match(strState, getOpponentPlayerID());	
		} 
		if (assignUtility(result, state)) {
			if (state.turn == getPlayerID()){
				// Next turn = oppoenent, opponent has a killermove or three in a row
				// The min/max is false for the player  
				return false;
			} else {
				// Next turn = player
				result = threeInARow.match(strState, getPlayerID());
				if (!assignUtility(result, state)) {
					result = killermoves.match(strState, getPlayerID());
				} if (!assignUtility(result, state)) {
					result = basicMoves.match(strState, getPlayerID());
				} if (!assignUtility(result, state)) {
					// Next turn = player, can win  
					return true;
				}
				else {
					// Next turn = player, player has nothing, opponent has something   
					return true;
				}
			}
		} else {
			// Opponent has nothing
// TODO:
			if (state.turn == getOpponentPlayerID()) {
				// Next turn = player
				result = threeInARow.match(strState, getPlayerID());
				if (!assignUtility(result, state)) {
					result = killermoves.match(strState, getPlayerID());
				} if (!assignUtility(result, state)) {
					result = basicMoves.match(strState, getPlayerID());
				} if (!assignUtility(result, state)) {
					// Next turn = player, can win  
					return true;
				}
			} 
			else {
				// Nethier player nor opponent have something   
				return true;

			}
			return false;
		}
	}

	private boolean assignUtility(RegexResult result, GameState state) {
		int deltaDepth = state.depth  - current.depth;
		boolean status = false;
		if (result!= null) {
			if (state.turn ==getPlayerID() ) {
				if (type.equals(GAMETYPE.MIN)) {
					state.utilityMax = UTILITY_MAX - deltaDepth;
				} else {
					state.utilityMin = UTILITY_MIN + deltaDepth;
				}

			} else {
				if (type.equals(GAMETYPE.MIN)) {
					state.utilityMax = UTILITY_MIN + deltaDepth;
				} else {
					state.utilityMin = UTILITY_MAX - deltaDepth;
				}
			}
			status =  true;
		}

		return status;
	}

	private boolean assignUtility(RegexResult opponent, RegexResult player, GameState state) {
		int deltaDepth = state.depth  - current.depth;
		if (opponent != null) {
			// Opponent has something!!  
			if (state.turn == getPlayerID()) {
				// It's the players move
				if (type.equals(GAMETYPE.MIN)) {
					// Player is min. The opponent utility = UTILITYMAX
					state.utilityMax = UTILITY_MAX - deltaDepth;
				} else {
					// Player is max. The opponent utility = UTILITYMIN
					state.utilityMin = UTILITY_MIN + deltaDepth;
				}
				return true;
			}else {
				// It's opponents move
				if (player!= null) {
					// Player has something 
					if (type.equals(GAMETYPE.MIN)) {
						// Player is MIN. The players utility = UTILITYMIN
						state.utilityMin = UTILITY_MIN + deltaDepth;
					} else {
						// Player is max. The players utility = UTILITYMAX
						state.utilityMax = UTILITY_MAX - deltaDepth;
					} 
					return true;
				} 
			}

		} else {
			// Opponent has nothing. Is this an optimal state for us
			if (player!= null) {
				// The player has somthing 
				if (state.turn == getOpponentPlayerID()) {
					// It's the opponents turn 
					if (type.equals(GAMETYPE.MIN)) {
						// Player is min. The opponent utility = UTILITYMAX
						state.utilityMin = UTILITY_MIN + deltaDepth;
					} else {
						// Player is max. The opponent utility = UTILITYMIN
						state.utilityMax = UTILITY_MAX - deltaDepth;
					}
					return true;
				} 
			} 
		}
		return false;
	}

	/***
	 * 
	 * Evaluates the gamestate. Only public access. Is called from GameLogic. 
	 * @param state
	 */

	public int evaluateGameState()
	{
		Stopwatch w = null;
		GameHelper.Trace(false, "Evaluating gamestate");
		// Clear all objects from the frontier; 
		frontier.clear();
		// clean up subgoals
		subgoals.cleanup(false);
		GameHelper.Trace(false, "Subgoals size after cleanup: " + subgoals.set.size());

		// trim away irrelevant gamestates  
		if (current.parent != null && explored.containsKey(current.parent)) {
			GameHelper.Trace(false, "Trimming explored, before: " + explored.size());
			exploredCleanupList.add(explored.get(current.parent));
			trimExplored();
			GameHelper.Trace(false, "Trimming explored, after: " + explored.size());
		}

		// Get state as string
		String strState = current.stateAsString();

		/***
		 * Check if we have three in a row
		 */

		RegexResult result = null;
		int nextMove = -1; 
		if((result = threeInARow.match(strState, playerID)) != null) {
			// Clean up subgoals before returning move
			nextMove = getMove(result);
		}  else if ((result = threeInARow.match(strState, getOpponentPlayerID()))!=null){
			nextMove = getMove(result);
		} else if ((result = basicMoves.match(strState, playerID)) != null){
			nextMove = getMove(result);
		} 
		else if((result = basicMoves.match(strState, getOpponentPlayerID())) != null)
		{
			nextMove = getMove(result); 
		}

		if (result != null) return nextMove;

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
		GameHelper.Trace(false,  "Searching for state among already explored states");
		boolean alreadyExplored = exploreBoardGameState();
		GameHelper.Trace(false, "Time to decide [exploreBoardGameState()]: " +  w.elapsedTime());
		if (alreadyExplored) {
			return next.action;
		} else {
			try {
				exploreBoardGameState(playerID);
			} catch (Exception e){ 
				e.printStackTrace();
				System.exit(-1);
			}
			GameHelper.Trace(false,  "Time to decide [exploreBoardGameState(playerID)]: " +  w.elapsedTime());
			return next.action;
		}
	}

	/****
	 * Adds gamestate to frontier
	 * @param state
	 */
	private void addGameStateToFrontier(GameState state) {
		frontier.put(state.hashCode, state);
	}

	private void addGameStateToSubgoals(GameState state) {
		subgoals.add(state);
	}

	public Frontier getFrontier() {
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

		GameState g = current.createGamestate(move, player);
		current = g;
		subgoals.setCurrent(current);
		GameHelper.Trace(false, "Player: " + player + " Move: " + move + " Depth: " + g.depth);
		GameHelper.Trace(false, "Board \n" + current.stateAsStringMatrix());	
	}

	/***
	 * Called from the explore methods
	 * @param state
	 */
	private void updateCurrentState(GameState state){
		// Implicitly updates gamestate by asignning and evaluting at the same time
		GameHelper.Trace(false,"Current state updated");
		current = state; 
		subgoals.setCurrent(current);
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

	/***
	 * Iterates through each subgoal to check if current state is on the backtracked path 
	 * @param gs
	 * @return
	 */
	private GameState isOnSubGoalPath() {
		GameState state = subgoals.getSubGoal();
		if (state != null) {
			while(state.parent != null &&  state.depth > current.depth) {
				if (state.parent.equals(current.hashCode)) {
					return state;
				}
				state = explored.get(state.parent);
			}
			subgoals.remove();
			isOnSubGoalPath();
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