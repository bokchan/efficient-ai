package src;

import java.util.Arrays;

public class BoardUpdater {
	
	/**
	 * Update a board using a list of valid domains, sorted by variable
	 * number. It is assumed that the domain for a variable where a
	 * queen can be placed is 1 and 0 otherwise.
	 * @param domains
	 * @param board
	 */
	public static void update(int[] domains, int[][] board) {
		System.out.println("Valid domains: " + Arrays.toString(domains));
		// the number of spots on the board where a queen can be placed
		int validSpots = 0;
		
		// find all valid spots
		for (int x = 0; x < board.length; x++)
			for (int y = 0; y < board.length; y++) {
				int varNum = Utility.getVarNumber(board.length, x, y);
				
				if (domains[varNum] == 0)
					board[x][y] = -1;
				else
					validSpots++;
			}
		
		// the number of valid spots correspond to the one possible
		// solution, which means that we can place a queen in all the
		// valid spots and hereby end configuration
		if (validSpots == board.length) {
			for (int x = 0; x < board.length; x++)
				for (int y = 0; y < board.length; y++) {
					if (board[x][y] != -1)
						board[x][y] = 1;
				}
		}
	}
}
