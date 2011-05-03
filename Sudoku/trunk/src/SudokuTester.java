import javax.swing.JFrame;
 


public class SudokuTester {
	static int size;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		size = 3;
		ISudokuSolver s = new SudokuSolver();
		s.setup(3);
		
		int[] vals = 
				{0,0,0,2,0,0,0,6,3,
				3,0,0,0,0,5,4,0,1,
				0,0,1,0,0,3,9,8,0,
				0,0,0,0,0,0,0,9,0,
				0,0,0,5,3,8,0,0,0,
				0,0,3,0,0,0,0,0,0,
				0,2,6,3,0,0,5,0,0,
				5,0,3,7,0,0,0,0,8,
				4,7,0,0,0,1,0,0,0};
		
		int[][] puzzle = new int[size*size][size*size];
		
		puzzle[3][0] = 2;
		puzzle[7][0] = 6;
		puzzle[8][0] = 3;
		puzzle[0][1] = 3;
		puzzle[5][1] = 5;
		puzzle[6][1] = 4;
		puzzle[8][1] = 1;
		puzzle[2][2] = 1;
		puzzle[5][2] = 3;
		puzzle[6][2] = 9;
		puzzle[7][2] = 8;
		puzzle[8][3] = 9;
		puzzle[3][4] = 5;
		puzzle[4][4] = 3;
		puzzle[5][4] = 8;
		puzzle[2][5] = 3;
		puzzle[1][6] = 2;
		puzzle[2][6] = 6;
		puzzle[3][6] = 3;
		puzzle[6][6] = 5;
		puzzle[0][7] = 5;
		puzzle[2][7] = 3;
		puzzle[3][7] = 7;
		puzzle[8][7] = 8;
		puzzle[0][8] = 4;
		puzzle[1][8] = 7;
		puzzle[5][8] = 1;
		
//		for (int i = 0; i< vals.length; i++) {
//			// cols, rows
//			puzzle[i%9][i/9] = vals[i];
//		}
		System.out.println(SudokuUtil.printBoard(puzzle));
		s.readInPuzzle(puzzle);
		System.out.println(SudokuUtil.printBoard(s.getPuzzle()));
		
		s.solve();
		SudokuGUI g = new SudokuGUI(s);

		// Setup of the frame containing the puzzle
		JFrame f = new JFrame();
		f.setSize(1000,1000);
		f.setTitle("Sudoku Solver");
		f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(g);    
		f.setVisible(true);

	}
	
	public static int GetRow(int X){
		return (X / (size*size)); 	
	}	
	
	public static int GetColumn(int X){
		return X - ((X / (size*size))*size*size);	
	}
	
	
}
