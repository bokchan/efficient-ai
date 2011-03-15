package andreas.fourconnect.itu;


public class Nobody implements IGameLogic {
	HashedByteMatrix states;
	
	enum UTILITY {
		PLAYER1, 
		PLAYER2};		

		private int x = 0;
		private int y = 0;
		private int playerID;
		private int[][] board;

		public void initializeGame(int rows, int columns, int player) {
			// x * y matrix
			this.x = columns;
			this.y = rows;
			this.playerID = player;
			board = new int[x][y];
			states = new HashedByteMatrix(x, y, playerID); 
			
		}

		public void insertCoin(int column, int playerID) {  
			int row = y-1;
			boolean validMove = false; 
			while(row >= 0) {
				if (board[column-1][row] == 0) {
					board[column-1][row] = playerID;
					validMove = true;
					return;
				}
				row--;
			}

			if (!validMove) throw new StackOverflowError(); 
		}

		public int decideNextMove() {
			return states.evaluateGameState(board);
		}

		public Winner gameFinished() {
			return Winner.NOT_FINISHED;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < y; i++) {
				sb.append("|");
				for (int j=0; j< x; j++) {
					sb.append(board[j][i]+ "|" );
				}
				sb.append("\n");
			}
			return sb.toString();
		} 
}
