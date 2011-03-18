

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class MainLogicV1 {
	// All regex expressions are precompiled. Should improve performance
	// Cols | Rows | DiagonalBT | DiagonalTB

	private final Pattern REGEX_HASWON_PLAYER1 = Pattern.compile("(1{1}[0-2]{6}){4}|1{4,}|(1{1}[0-2]{7}){4,}|(1{1}[0-2]{5}){4,}");
	private final Pattern REGEX_HASWON_PLAYER2 = Pattern.compile("(2{1}[0-2]{6}){4}|2{4,}|(2{1}[0-2]{7}){4,}|(2{1}[0-2]{5}){4,}");

	// Test for three in a row
	// 1[0-2]{6}){3} = Column, 1{3,} Row,(1{1}[0-2]{7}){3,} = DiagonalBT, (1{1}[0-2]{5}){3,} = DiagonalTB     
	private final String REGEX_3_IN_A_ROW_PLAYER1 = "(1[0-2]{6}){3}:3:0|21{3}0{1,}:0:4|01{3}2:0:0|1101:0:2|1011:1:1|(1[0-2]{7}){3,}:3:3|(1[0-2]{7}){2}0[0-2]{7}1:2:2|(1[0-2]{7})0[0-2]{7}1[0-2]{7}1:1:1|(1[0-2]{5}){3}0:3:-3|0[0-2]{5}(1[0-2]{5}){3}:0:01[0-2]{5}0[0-2]{5}1[0-2]{5}1:1:-1|(1[0-2]{5}){2}0[0-2]{5}(1[0-2]{5}){1,}:2:-2";
	private final String REGEX_3_IN_A_ROW_PLAYER2 = "(2[0-2]{6}){3}:3:0|12{3}0{1,}:0:4|02{3}2:0:0|2202:0:2|2022:1:1|(2[0-2]{7}){3,}:3:3|(2[0-2]{7}){2}0[0-2]{7}2:2:2|(2[0-2]{7})0[0-2]{7}2[0-2]{7}2:1:1|(2[0-2]{5}){3}0:3:-3|0[0-2]{5}(2[0-2]{5}){3}:0:02[0-2]{5}0[0-2]{5}2[0-2]{5}2:1:-1|(2[0-2]{5}){2}0[0-2]{5}(2[0-2]{5}){1,}:2:-2";

	//private final String killerMoves = "(1010.1110)|(1[0-2]{7}1[0-2]{5}1100[0-2]{5}00)|(1100..11100)|(10101..110101)|(1.0....1101.....0......01)|(0110..10110)|(0.....1011.111)|(1.......1.0...1110......0)|(1.011...1010.111000)|(1011..1101)|(11...0..1011.000)|(1......1....110)|(01.....011....011....0.1)|(0...1110....1.0...1..0)|(0110..10110)" ;
	private final String REGEX_KILLERMOVES_PLAYER_1 = "1010.1110.0:0:1:2:3|0101...0.0111:0:0:0:1";
	private final String REGEX_KILLERMOVES_PLAYER_2 = "2020.2220.0:0:1:2:3|0202...0.0111:0:0:0:1";
	/***
	 *
	 * 
	 */
	// Map with deephashvalue of gamestate as key

	public Map<Integer, GameState> explored;
	public Queue<GameState> frontier;

	private int rows = 6;
	private int cols = 7;

	private int playerid;
	private int opponent;
	GameState current;
	GameState next;
	enum CURRENT_GAMESTATE {PLAYER1_WIN, PLAYER1_3_IN_A_ROW, PLAYER1_KILLERMOVE, PLAYER2_WIN, PLAYER2_3_IN_A_ROW, PLAYER2_KILLERMOVE, NA};

	private RegexEvaluation Eval_P1_3InARow = new RegexEvaluation(REGEX_3_IN_A_ROW_PLAYER1); 
	private RegexEvaluation Eval_P2_3InARow = new RegexEvaluation(REGEX_3_IN_A_ROW_PLAYER1);
	
	private RegexEvaluation Eval_P1_KILLERMOVE = new RegexEvaluation(REGEX_KILLERMOVES_PLAYER_1); 
	private RegexEvaluation Eval_P2_KILLERMOVE = new RegexEvaluation(REGEX_KILLERMOVES_PLAYER_2);
	
	CURRENT_GAMESTATE currentstate = CURRENT_GAMESTATE.NA;

	// The depth of cutoff 
	private int cutOff = 6;
	
	public MainLogicV1(int cols, int rows, int playerid) {
		this.cols = cols;
		this.rows = rows;
		explored = new HashMap<Integer, GameState>();
		frontier = new LinkedList<GameState>();

		this.playerid = playerid;
		opponent = playerid == 1 ? 2 : 1;

	}

	public void putGameState(int[][] state, int playerid) {
		GameState g = new GameState(toByteMatrix(state));
		g.turn = playerid;
	}

	public void addGameStateToFrontier(GameState state) {
		frontier.add(state);
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
		return explored.get(Arrays.deepHashCode((toByteMatrix(state))));
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
			
			player = player == playerid ? opponent : playerid;
			for (int col = 0; col < cols; col++) 
			{
				for (int row = rows-1; row >= 0; row--) {
					// Iterate through columns top to bottom
					// If we encounter an empty field, explore it
					if (gamestate.state[row][col] == 0) {

						// Clone the state 
						byte[][] newState = copyArray(gamestate.state);
						// Set the empty field to a
						newState[row][col] = (byte)player;

						GameState g = new GameState(newState);
						g.parent = gamestate.hashCode;

						// Set the parent
						// Set the column in which we put the coin
						g.action = col;
						// Set the turn of the gamestate
						g.turn = player;

						// This is the next level exploration
						g.depth = gamestate.depth + 1;

						// Assign utility
						assignUtility(g);

						// put gamestate on frontier
						// TODO: Alpha beta prune 
						addGameStateToFrontier(g);
						//Break the loop
						row=-1;
					}
				}
			}
			 
		} else {
			return;
		}
		// cutoff check
		addGameStateToExplored(gamestate);
		// call exploregamestate again, going depth first;
		exploreBoardGameState(player);
	}

	public byte[][] copyArray(byte[][] ba) {
		byte[][] ba2 = new byte[ba.length][ba[0].length];
		for (int i = 0; i < ba.length; i++) {
			for (int j = 0; j < ba[0].length; j++) {
				ba2[i][j] =ba[i][j]; 
			}
		}
		return ba2;
	}

	/***
	 * For now only for the first player
	 * 
	 * @param state
	 */
	public void assignUtility(GameState state) {
		// Get state as string   
		String strState = asString(state);
		
		Matcher m = REGEX_HASWON_PLAYER1.matcher(strState);
		if (m.find()) {
			currentstate = CURRENT_GAMESTATE.PLAYER1_WIN;
			state.utilityMIN = -8;
			return;
		}

		m = REGEX_HASWON_PLAYER2.matcher(strState);
		if (m.find()) {
			currentstate = CURRENT_GAMESTATE.PLAYER2_WIN;
			state.utilityMAX = 8;
			return;
		}
		
		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */
		int[] result = null;
		if (playerid == 1) {
			result = Eval_P2_3InARow.match(strState, 2);
			state.utilityMIN = result != null ? 0 : -4;
			state.utilityMAX = result != null ? 8 : 4;
		} else {
			result = Eval_P1_3InARow.match(strState, 1);
			state.utilityMAX = result != null ? 0 : 4;
			state.utilityMIN = result != null ? -8 : -4;
		}
		
		/***
		 * check for killer move
		 */
		
	}

	/***
	 * Evaluates the gamestate. Is called from Player. 
	 * @param state
	 */

	public int evaluateGameState(int[][] state) 
	{
		// Clear all objects from the frontier; 
		frontier.clear();
		// Set the current gamestate
		setCurrentState(toByteMatrix(state));
		frontier.add(current);
		// Get state as string

		String strState = asString(current.state);

		// Test if game is finished
		Matcher m = REGEX_HASWON_PLAYER1.matcher(strState);
		if (m.find()) {
			currentstate = CURRENT_GAMESTATE.PLAYER1_WIN;
			return -1;
		}

		// Evaluate cols
		m = REGEX_HASWON_PLAYER2.matcher(strState);
		if (m.find()) {
			currentstate = CURRENT_GAMESTATE.PLAYER2_WIN;
			return -2;
		}

		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */
		int[] result = null;
		if (playerid == 1) {
			result = Eval_P2_3InARow.match(strState, 2);
		} else {
			result = Eval_P1_3InARow.match(strState,1);
		}

		if(result != null ) {
			// Return blocking move
			return getMove(result);
		} else {
			// Else we start exploring the state space
			exploreBoardGameState(opponent);
		}
		return (next !=null) ? next.action: Integer.MIN_VALUE;

	} 

	public void setCurrentState(byte[][] state) {
		if (explored.containsKey(Arrays.deepHashCode(state))) {
			current = explored.get(Arrays.deepHashCode(state));
		} else {
			// This is the start of the game or the opponent has made a move not anticipated by us
			current = new GameState(state); 
		}
	}

	public String asString(GameState state) {
		return asString(state.state);	
	}

	/***
	 * Returns a byte[][] as a string, starting from bottom left corner of the byte[][]
	 * @param state
	 * @return
	 */
	public String asString(byte[][] state) {
		StringBuilder sb = new StringBuilder();
		for (int row = rows-1; row >= 0; row--) {
			int emptySpaces = 0; 
			for (int col = 0; col < cols; col++)
			{
				int val = state[row][col];
				//if (val == 0) emptySpaces++;
				sb.append(val);   
				//if (emptySpaces == 7) break;
			}
		}
		return sb.toString();
	}
	/***
	 * Converts an int[][] to a byte[][]
	 * @param state
	 * @return
	 */
	public byte[][] toByteMatrix(int[][] state) {

		byte[][] bm = new byte[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (byte)state[i][j];
			}
		}
		return bm;
	}
	/**
	 * Converts a byte[][] to an int[][]
	 * @param state
	 * @return
	 */
	public int[][] toIntMatrix(byte[][] state) {
		int[][] bm = new int[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (int)state[i][j];
			}
		}
		return bm;
	}

	public int[][] convertToMatrix(int[] array) { 
		int aIdx = 0;
		int[][] matrix = new int[rows][cols];

		for (int row = rows-1; row >= 0; row--) {
			for (int col = 0; col <cols ;  col++) {
				matrix[row][col] = array[aIdx];
				aIdx++;
			}
		}
		return matrix;
	}

	public Queue<GameState> getFrontier() {
		return frontier;
	}

	public Map<Integer,GameState> getExplored() {
		return explored;
	}

	private int getMove(int[] result) {
		int start = result[0];
		int offsetY = result[1];
		int offsetX = result[2];

		int row =  start/cols + offsetY;
		int col = start % cols + offsetX;
		byte[][] newState = copyArray(current.state);
		newState[col][row] = (byte)playerid;
		// Update current state 
		setCurrentState(newState);
		// return 1 based column index 
		return col + 1;
	}
}