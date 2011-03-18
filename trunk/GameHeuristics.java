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
 *  TODO: 
 *  Tested Arrays.deepHashCode on 7^6 byte[][] arrays : 50 ms, not a bottleneck
 *  Tested Arrays.deepHashCode on 7^7 byte[][] arrays : 176 ms, not a bottleneck
 *  
 * @author Andreas
 *
 */
public class GameHeuristics {
	
	private int cols = 0;
    private int rows = 0;
    private int playerID;
    private int opponent;
	
	public Map<Integer, GameState> explored;
	public Queue<GameState> frontier;
	private int cutOff = 6;
    
    private RegexEvaluation threeInARow = new RegexEvaluation("3inarow.xml", RegexEvaluation.MATCH_TYPE.KILLER_MOVE);
    private RegexEvaluation killermoves = new RegexEvaluation("killermoves.xml", RegexEvaluation.MATCH_TYPE.THREE_IN_A_ROW);
    private RegexEvaluation fourInARow = new RegexEvaluation("winnermoves.xml",RegexEvaluation.MATCH_TYPE.FOUR_IN_A_ROW);

    private GameState current;

	public GameHeuristics(int cols, int rows, int playerid) {
		this.cols = cols;
		this.rows = rows;
		explored = new HashMap<Integer, GameState>();
		frontier = new LinkedList<GameState>();

		this.playerID = playerid;
		opponent = getOpponentPlayerID();
	}

	/***
	 * Function that receives an explored board state isOpponent
	 * Search till all leaves within the cutoff have been explored or a goal state has been reached
	 *   
	 * @param gamestate
	 * @return
	 */
	public void exploreBoardGameState(int player) 
	{
		// Get the head of the queue
		// Check if the depth is ok, keep dequeueing or what 
		GameState gamestate = frontier.poll();
		if (gamestate != null && (gamestate.depth - current.depth) < cutOff) 
		{
			// We alternate the turn 
			player = player == playerID ? opponent : playerID;
			for (int col = 0; col < cols; col++) 
			{
				for (int row = rows-1; row >= 0; row--) {
					// Iterate through columns top to bottom
					// If we encounter an empty field, explore it
					if (gamestate.state[row][col] == 0) {

						GameState newGS  = gamestate.createGamestate(col, row, player);
						// Assign utility
						assignUtility(newGS);

						// put gamestate on frontier
						// TODO: Alpha beta prune
						// If utility is acceptable add as child and add to gamestate
						gamestate.addToChildren(newGS);
						addGameStateToFrontier(newGS);

						row=-1; 						//Break the loop 
					}
				}
			}

		} else {
			return;
		}
		// end cutoff check
		addGameStateToExplored(gamestate);
		// call exploregamestate again, going depth first;
		exploreBoardGameState(player);
	}

	/***
	 * For now only for the first player
	 * 
	 * @param state
	 */
	public void assignUtility(GameState state) {
		// Get state as string
		String strState = state.stateAsString();
		
		RegexResult result = fourInARow.match(strState, state.turn);
		if (result != null) {
			state.utilityMIN = -8;
			return;
		}
		
		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */
		result = threeInARow.match(strState, state.turn);
		if (result != null) {
			state.utilityMIN = result != null ? 0 : -4;
			state.utilityMAX = result != null ? 8 : 4;
		} 

		/***
		 * check for killer move
		 */
		result = killermoves.match(strState, state.turn);
		if (result != null){
			state.utilityMAX = 02;
			state.utilityMIN = 123;
		}
	}

	/***
	 * Evaluates the gamestate. Is called from Player. 
	 * @param state
	 */

	public int evaluateGameState(int[][] state, int player) 
	{
		// Clear all objects from the frontier; 
		frontier.clear();
		// Set the current gamestate
		setCurrentState(GameHelper.toByteMatrix(state));
		frontier.add(current);
		// Get state as string

		String strState = current.stateAsString();

		// Test if game is finished
		RegexResult  result= fourInARow.match(strState, player);
		if (result !=null) {
			return -player;
		}

		// Evaluate cols
		result  = fourInARow.match(strState, opponent);
		if (result != null) {
			return -opponent;
		}

		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */
		result = threeInARow.match(strState, playerID);
		if (result != null) {
			return getMove(result); 
			// TODO: doSomeThing 
		}
		result = threeInARow.match(strState, getOpponentPlayerID());
		if(result != null ) {
			// Return blocking move
			return getMove(result);
		} else {
			// Else we start exploring the state space
			exploreBoardGameState(opponent);
		}
		
		//return (next !=null) ? next.action: Integer.MIN_VALUE;
		// TODO: How do we get the best move from a search
		return getMove(result);

	}
	
	/***
	 * TODO: should do it through state.createGame
	 * Add a gamestate to frontier, should do it through state.createGame 
	 * @param state
	 * @param playerid
	 */
	public void addGameStateToFrontier(int[][] state, int playerid) {
		GameState g = new GameState( GameHelper.toByteMatrix(state));
		g.turn = playerid;
	}

	/****
	 * Adds gamestate to frontier
	 * @param state
	 */
	public void addGameStateToFrontier(GameState state) {
		frontier.add(state);
	}
	
	public Queue<GameState> getFrontier() {
		return frontier;
	}

	public Map<Integer,GameState> getExplored() {
		return explored;
	}

	public void setCurrentState(byte[][] state) {
		if (explored.containsKey(Arrays.deepHashCode(state))) {
			current = explored.get(Arrays.deepHashCode(state));
		} else {
			// This is the start of the game or the opponent has made a move not anticipated by us
			current = new GameState(state); 
		}
	}
	
	public void updateCurrentState(GameState state) {
		// Implicitly updates gamestate by asignning and evaluting at the same time  
		if ((current = explored.get(state.hashCode)) ==  null) {
			current = state; 
		} 
	}
	
	public String asString(GameState state) {
		return state.stateAsString();
	}

	private int getMove(RegexResult result) {
	
		int row =  result.matchStartIdx/cols + result.offsetY;
		int col = result.matchStartIdx % cols + result.offsetX;
		
		// Creates a new gamestate based on the current state
		// Updates the currentstate 
		updateCurrentState(current.createGamestate(col, row, result.playerid));
		// return 1 based column index 
		return col + 1;
	}
	public void addGameStateToExplored(GameState state) {
		// Use the hashcode of the bytearray not the int array
		explored.put(state.hashCode, state);
	}

	/***
	 * Checks if the current gamestate has been explored  
	 * @param state
	 * @return
	 */
	public GameState exploredContains(int[][] state) {
		return explored.get(Arrays.deepHashCode((GameHelper.toByteMatrix(state))));
	}
	
	public int getOpponentPlayerID(){
        if (playerID == 1){
            return 2;
        }
        return 1;
    }
	

}