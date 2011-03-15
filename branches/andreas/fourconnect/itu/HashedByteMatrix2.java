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
 * 1111100    
 * 1100011
 * 2120012
 * 1211221
 * byte1 [00011001][00100101][00011001][00010000][00100101][00011001]
 * For a full representation of a gamestate we would need 
 * 2 * 8([6 bit used 2 bits overhead])  per columns * 7 = 112 bits
 * ByteArraySize = 2*8 * 7 = 112; 
 * Thus the memoryload of 7^6 gamestates would be : 
 * 112 * 7^6 = 1.57078362 MB Thats freakin nice:)
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
 * @author Andreas
 *
 */
public class HashedByteMatrix2 {
	// Cols | Rows | DiagonalForward | DiagonalBackwards
	// Make a three in a row
	private final Pattern splitByte = Pattern.compile("[0-2]{2}");  
	//"(player{1}[0-2]{6}){coins}|player{coins,}|(player{1}[0-2]{7}){coins,}|(player{1}[0-2]{5}){coins,}";
	private final String regex = "(player{1}[0-2]{6}){coins}|player{coins,}|(player{1}[0-2]{7}){coins,}|(player{1}[0-2]{5}){coins,}";
	private final String regexThreeInArrow = "(player{1}[0-2]{6}){coins}|player{coins,}|(player{1}[0-2]{7}){coins,}|(player{1}[0-2]{5}){coins,}";

	//private final String killerMoves = "(1010.1110)|(1[0-2]{7}1[0-2]{5}1100[0-2]{5}00)|(1100..11100)|(10101..110101)|(1.0....1101.....0......01)|(0110..10110)|(0.....1011.111)|(1.......1.0...1110......0)|(1.011...1010.111000)|(1011..1101)|(11...0..1011.000)|(1......1....110)|(01.....011....011....0.1)|(0...1110....1.0...1..0)|(0110..10110)" ;
	private final String killerMoves = "(player0player0.playerplayerplayer0)|(player[0-2]{7}player[0-2]{5}playerplayer00[0-2]{5}00)|(playerplayer00..playerplayerplayer00)|(player0player0player..playerplayer0player0player)|(player.0....playerplayer0player.....0......0player)|(0playerplayer0..player0playerplayer0)|(0.....player0playerplayer.playerplayerplayer)|(player.......player.0...playerplayerplayer0......0)|(player.0playerplayer...player0player0.playerplayerplayer000)|(player0playerplayer..playerplayer0player)|(playerplayer...0..player0playerplayer.000)|(player......player....playerplayer0)|(0player.....0playerplayer....0playerplayer....0.player)|(0...playerplayerplayer0....player.0...player..0)|(0playerplayer0..player0playerplayer0)";
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
		byte[] state;
		int action;
		Integer parent= null;
		double utilityMIN = 0.0;
		double utilityMAX = 0.0;
		int depth = 0;

		public GameState(byte[] state) {
			this.state = state;
		}
	}

	public HashedByteMatrix2(int cols, int rows, int playerid) {
		this.cols = cols;
		this.rows = rows;
		frontier = new HashMap<Integer, GameState>();
		explored = new HashMap<Integer, GameState>();
		this.playerid = playerid;
		opponent = playerid == 1 ? 2 : 1;
	}

	public void putGameState(int[][] state) {
		GameState g = new GameState(toByteArray(state));
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
//		int[][] matrix =toByteArray(state. 
//		if ((depth - current.depth) < cutOff) {
//			for (int col = 0; col < cols; col++) {
//				for (int row = 0; row<rows; row++) {
//					// Iterate through columns top to bottom
//					// If we encounter an empty field, explore it
//					if (state.state[col][row] == 0) {
//						// Set the value to the value of the player
//						byte[][] newState = state.state.clone(); 
//						newState[col][row] = (byte)player;
//						GameState g = new GameState(newState);
//						g.parent = state.hashCode;
//						g.action = col;
//						assignUtility(g);
//						// put gamestate on frontier
//						// TODO: Implement the evaluation
//						putGameStateOnFrontier(g);
//						// Reset the board
//						player = player == playerid ? opponent : playerid; 
//						exploreBoardGameState(g, player, depth++);
//					}
//				}
//			}
//		} else {
//			return;
//		}
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
		setCurrentState(toByteArray(state));
		// Get state as string   
		String strState = asString(current.state);

		// Test if game is finished
		String r = regex; 		
		Pattern goalPattern = Pattern.compile(r.replaceAll("player", String.valueOf(playerid)).replaceAll("coins", "4"));
		Matcher m = goalPattern.matcher(strState);
		if (m.find()) return -1;

		// Evaluate cols
		r = regex;
		goalPattern = Pattern.compile(r.replaceAll("player", String.valueOf(opponent)).replaceAll("coins", "4"));

		m = goalPattern.matcher(strState);
		if (m.find()) return -2;

		/***
		 * Check if opponent has any three in row
		 * If this is the case, return the row that that blocks the opponent   
		 */
		r = regex;
		Pattern p = Pattern.compile(r.replaceAll("player", String.valueOf(opponent)).replaceAll("coins", "3"));   
		m = p.matcher(strState);
		if(m.find()) {
			int start = m.start();
			int row =  (int) start/cols;
			int col = (start+1) % cols;
			byte[] newState = current.state.clone();
			//newState[col][row] = (byte)playerid;
			setCurrentState(newState);
			return (m.start() % cols)+1;  
		} else {
			// Else we start exploring the state space  
			exploreBoardGameState(current, playerid, current.depth);
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

	public void setCurrentState(byte[] state) {
		this.current = new GameState(state);
	}

	public GameState createGameState(int[][] state) {
		return new GameState(toByteArray(state));

	}

	public String asString(GameState state) {
		return asString(state.state);	
	}

	/***
	 * Now getting from a bytearray to a string representation is a little bit tricky
	 * 
	 * 1110011
	 * 2120012
	 * 1211221
	 * 1110011
	 * 2120012
	 * 1211221
	 * byte1 [00011001][00100101][00011001][00010000][00100000][00100101][00011001]
	 * 
	 * after string concat
	 * 0 1 2  3 4 5  0 1 2  3 4 5  0 1 2  3 4 5  0 1 2  3 4 5  0 1 2  3 4 5  0 1 2  3 4 5  0 1 2  3 4 5
	 * 0	  1      2      3      4      5      6      7      8      9      10     11     12     13
	 * 011001 100101 011001 010000 100000 100101 011001 011001 100101 011001 010000 100000 100101 011001
	 * 0 7 14 212835 1 8 15 222936 2 9 16 233037 3 1017 243138 4 1118 253239 5 1219 263340 6 1320 273441
	 * 071727 374757 07
	 * 
	 * 
	 * @param state
	 * @return
	 */
	public String asString(byte[] state) {
		Matcher m;
		String sByte = "";
		String out = "";
		// State.length = 14
		int row = 0;
		int column = 0;
		System.out.println(state.length);
		for (int i = 0 ; i < (state.length); i++) {
			String s = Integer.toBinaryString(state[i]);
			while(s.length()< 7) {
				s=0 + s;
			}	
			out+=s;
	
		}
		
		String[] car =  splitByte.split(out);
		System.out.println(Arrays.toString(car));
			
			/***
			 * 
			 * 						row	col
			 * 						0	0
			 * (row)*7+column		0		= 0
			 * row++				1
			 * (row)*7)+column		1		= 7
			 * row++				2
			 * i+(row)*7)+column	2		= 14´
			 * row++				3
			 * 	
			 *						1	
			 * row*7				3		= 21
			 * row++				4
			 * row*7				4		= 28
			 * row++				5
			 * (row)7				5		= 35
			 * 
			 * 							
			 * column++, row=0		0	1
			 * row*7+column			0	1	= 1
			 * row++				1	1	
			 * row*7+column			1	1	= 8
			 * row++				2	1
			 * row*					
			 */						
				 
		
		
		return out.toString();
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


	public byte[] toByteArray(int[][] state) {
		// Rows by columns
		byte[] bm = new byte[(state.length * state[0].length / 3)];

		// For each row
		/***
		 * A 6x7 board will give a bytearray of size 14;  

		 * Write two bytes at a time. 
		 * Traversing the all columns but only half the rows;  
		 */
		// Column
		for (int col = 0; col<  state[0].length; col++) {
			// Add 
			String s1 = "00";
			String s2 = "00";
			for(int row = state.length-1; row>= state.length-3; row--) {
				//Row
				s1 += Integer.toBinaryString(state[row][col]);
				s2 += Integer.toBinaryString(state[row-3][col]);
			}
			Byte b1 = new Byte(s1);
			Byte b2 = new Byte(s2);
			bm[col*2] = b1;
			bm[col*2 + 1] = b2;

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