package andreas.fourconnect.itu;

public class TestNobody {

	/**
	 * @param args
	 * Four ply cutoff  
	 */
	
	public static void main(String[] args) {
		Nobody player1 = new Nobody();  
		
		player1.initializeGame(7, 6, 1);
		
		player1.insertCoin(1, 1);
		player1.insertCoin(2, 2);
		player1.insertCoin(2, 1);
		player1.insertCoin(3, 2);
		player1.insertCoin(4, 1);
		player1.insertCoin(5, 2);
		player1.insertCoin(6, 1);
		player1.insertCoin(6, 2);
		player1.insertCoin(3, 1);
		player1.insertCoin(6, 2);
		player1.insertCoin(4, 1);
		player1.insertCoin(6, 2);
		//System.out.println(r1.toString());
		System.out.println("BOARD MATRIX");
		
		System.out.println(player1.decideNextMove());
		
		int[][] matrix = new int[4][5];
		System.out.println(matrix.length);
		System.out.println(matrix[0].length);
		
	}
}
