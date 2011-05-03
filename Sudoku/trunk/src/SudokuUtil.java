
public class SudokuUtil {
	public static String printBoard(int[][] board) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("-------------\n");
		for (int i = 0; i < board.length; i++) {
			sb.append("|");
			for (int j = 0; j < board[0].length; j++) {
				sb.append(board[j][i]);
				//if ((j+1) % 3 == 0) sb.append("|");
			}
			sb.append("\n");
			if ((i+1) % 3 == 0) sb.append("-------------\n");
		}
		return sb.toString();
	}


}
