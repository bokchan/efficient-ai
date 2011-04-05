package src;
public class Utility {

	/**
	 * Find the number of the variable that corresponds to
	 * a given cell on the board.
	 * @param size
	 * @param column
	 * @param row
	 * @return
	 */
	public static int getVarNumber(int size, int column, int row) {
		return (column * size) + row;
	}
	
	public static void printBoard(int[][] board) {
		for (int i = 0; i < board.length; i++) {
			String row = "";
			for (int j = 0; j < board[0].length; j++  ) {
				row += board[i][j];
			}
			System.out.println(row);
		}
	}
	
}
