package andreas.fourconnect.itu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
 * @author Andreas
 *
 */
public class HashedByteMatrix {

	// All regex expressions are precompiled. Should improve performance
	// Cols | Rows | DiagonalBT | DiagonalTB

	private final Pattern REGEX_HASWON_PLAYER1 = Pattern.compile("(1{1}[0-2]{6}){coins}|1{coins,}|(1{1}[0-2]{7}){coins,}|(1{1}[0-2]{5}){coins,}");
	private final Pattern REGEX_HASWON_PLAYER2 = Pattern.compile("(2{1}[0-2]{6}){coins}|2{coins,}|(2{1}[0-2]{7}){coins,}|(2{1}[0-2]{5}){coins,}");

	// Test for three in a row
	// 1[0-2]{6}){3} = Column, 1{3,} Row,(1{1}[0-2]{7}){3,} = DiagonalBT, (1{1}[0-2]{5}){3,} = DiagonalTB     
	private final Pattern REGEX_3_IN_A_ROW_PLAYER1 = Pattern.compile("(?:1[0-2]{6}){3}|1{3,}|1{1,}01{2}|1{2}01{1,}|(?:1[0-2]{7}){3,}|(?:1[0-2]{7}){2}0[0-2]{7}1|(?:1[0-2]{7}){1,}0[0-2]{7}1[0-2]{7}1|(?:1[0-2]{5}){3,}|(?:1[0-2]{5}){1,}0[0-2]{5}1[0-2]{5}1|(?:1[0-2]{5}){2}0[0-2]{5}(?:1[0-2]{5}){1,}");
	private final Pattern REGEX_3_IN_A_ROW_PLAYER2 = Pattern.compile("(?:2[0-2]{6}){3}|2{3,}|2{1,}02{2}|2{2}02{1,}|(?:2[0-2]{7}){3,}|(?:2[0-2]{7}){2}0[0-2]{7}2|(?:2[0-2]{7}){1,}0[0-2]{7}2[0-2]{7}2|(?:2[0-2]{5}){3,}|(?:2[0-2]{5}){1,}0[0-2]{5}2[0-2]{5}2|(?:2[0-2]{5}){2}0[0-2]{5}(?:2[0-2]{5}){1,}");

	//private final String killerMoves = "(1010.1110)|(1[0-2]{7}1[0-2]{5}1100[0-2]{5}00)|(1100..11100)|(10101..110101)|(1.0....1101.....0......01)|(0110..10110)|(0.....1011.111)|(1.......1.0...1110......0)|(1.011...1010.111000)|(1011..1101)|(11...0..1011.000)|(1......1....110)|(01.....011....011....0.1)|(0...1110....1.0...1..0)|(0110..10110)" ;
	private final String REGEX_KILLERMOVES_PLAYER_1 = "(player0player0.playerplayerplayer0)|(player[0-2]{7}player[0-2]{5}playerplayer00[0-2]{5}00)|(playerplayer00..playerplayerplayer00)|(player0player0player..playerplayer0player0player)|(player.0....playerplayer0player.....0......0player)|(0playerplayer0..player0playerplayer0)|(0.....player0playerplayer.playerplayerplayer)|(player.......player.0...playerplayerplayer0......0)|(player.0playerplayer...player0player0.playerplayerplayer000)|(player0playerplayer..playerplayer0player)|(playerplayer...0..player0playerplayer.000)|(player......player....playerplayer0)|(0player.....0playerplayer....0playerplayer....0.player)|(0...playerplayerplayer0....player.0...player..0)|(0playerplayer0..player0playerplayer0)";
	/***
	 * 
	 *  
	 */
	// Map with deephashvalue of gamestate as key
	public Map<Integer, GameState> frontier;
	public Map<Integer, GameState> explored;
	private int rows = 6;
	private int cols = 7;

	private int playerid;
	private int opponent;
	GameState current;
	GameState next;

	private int cutOff = 6;

	class GameState {
		Integer hashCode;
		byte[][] state;
		int action;
		Integer parent= null;
		double utilityMIN = 0.0;
		double utilityMAX = 0.0;
		int depth = 0;

		public GameState(byte[][] state) {
			this.state = state;
		}
	}

	public HashedByteMatrix(int cols, int rows, int playerid) {
		this.cols = cols;
		this.rows = rows;
		frontier = new HashMap<Integer, GameState>();
		explored = new HashMap<Integer, GameState>();
		this.playerid = playerid;
		opponent = playerid == 1 ? 2 : 1;
	}

	public void putGameState(int[][] state) {
		GameState g = new GameState(toByteMatrix(state));
		putGameStateOnFrontier(g);
	}

	public void putGameStateOnFrontier(GameState state) {
		int hash = Arrays.hashCode(state.state);
		state.hashCode = hash;
		frontier.put(hash, state);
	}

	public GameState frontierContains(int[][] state) {
		return frontier.get(Arrays.hashCode(state));
	}

	/***
	 * Function that receives an explored board state isOpponent 
	 * @param state
	 * @return
	 */

	public void exploreBoardGameState(GameState state, int player, int depth) {
		if ((depth - current.depth) < cutOff) {
			for (int col = 0; col < cols; col++) {
				for (int row = 0; row<rows; row++) {
					// Iterate through columns top to bottom
					// If we encounter an empty field, explore it
					if (state.state[col][row] == 0) {
						// Set the value to the value of the player
						byte[][] newState = state.state.clone(); 
						newState[col][row] = (byte)player;
						GameState g = new GameState(newState);
						g.parent = state.hashCode;
						g.action = col;
						assignUtility(g);
						// put gamestate on frontier
						// TODO: Implement the evaluation
						putGameStateOnFrontier(g);
						// Reset the board
						player = player == playerid ? opponent : playerid; 
						exploreBoardGameState(g, player, depth++);
					}
				}
			}
		} else {
			return;
		}
	}

	/***
	 * For now only for the first player
	 * 
	 * @param state
	 */
	public void assignUtility(GameState state) {
		// Get state as string   
		String strState = asString(state);

		// Test if game can be won in one move
		String r = regex; 		
		Pattern goalPattern = Pattern.compile(r.replaceAll("player", String.valueOf(playerid)).replaceAll("coins", "4"));

		Matcher m = goalPattern.matcher(strState);
		// Assumes player1 is max player  
		if (m.find())  {
			state.utilityMAX = 8.0;
			state.utilityMIN = 0;
			next = state;
			return;
		}
		// Evaluate cols
		/***
		 * Check for killer moves that is where a coin creates two or more winning options  
		 * 
		 */
		r = killerMoves;
		Pattern p = Pattern.compile(r.replaceAll("player", String.valueOf(playerid)));   
		m = p.matcher(strState);
		//
		if(m.find()) {
			state.utilityMAX = 6.0;
			next = state;
			return;
		}
	}

	/***
	 * Evaluates 
	 * @param state
	 */
	public int evaluateGameState(int[][] state) {
		setCurrentState(toByteMatrix(state));
		// Get state as string   
		String strState = asString(current.state);

		// Test if game is finished
		Matcher m = REGEX_HASWON_PLAYER1.matcher(strState);
		if (m.find()) return -1;

		// Evaluate cols
		m = REGEX_HASWON_PLAYER2.matcher(strState);
		if (m.find()) return -2;

		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */

		if (playerid == 1) { 
			m = REGEX_3_IN_A_ROW_PLAYER2.matcher(strState);
			if(m.find()) {
				int start = m.start();
				int row =  (int) start/cols;
				int col = (start+1) % cols;
				byte[][] newState = current.state.clone();
				newState[col][row] = (byte)playerid;
				setCurrentState(newState);
				return (m.start() % cols)+1;  
			} else {
				// Else we start exploring the state space
				exploreBoardGameState(current, playerid, current.depth);
			}
		} 
		else {
			m = REGEX_3_IN_A_ROW_PLAYER1.matcher(strState);
		} 
		return (next !=null) ? next.action: Integer.MIN_VALUE;
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

	public void setCurrentState(byte[][] state) {
		this.current = new GameState(state);
	}

	public GameState createGameState(int[][] state) {
		return new GameState(toByteMatrix(state));

	}

	public String asString(GameState state) {
		return asString(state.state);	
	}

	public String asString(byte[][] state) {
		StringBuilder sb = new StringBuilder();
		for (int row = rows-1; row >= 0; row--) {
			int emptySpaces = 0; 
			for (int col = 0; col < cols; col++)
			{
				int val = state[row][col];
				if (val == 0) emptySpaces++;
				sb.append(val);
				// We only need to evaluate 
				if (emptySpaces == 7) break;
			}
		}
		return sb.toString();
	}

	private byte[][] toByteMatrix(int[][] state) {
		byte[][] bm = new byte[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (byte)state[i][j];
			}
		}
		return bm;
	}

	public int[][] toIntMatrix(byte[][] state) {
		int[][] bm = new int[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (int)state[i][j];
			}
		}
		return bm;
	}
}