import javax.swing.JFrame;

public class ShowSudoku {
	public static void main(String[] arg){
		
		ISudokuSolver s = new SudokuSolver();
		s.setup(3);
		
		if (arg.length == 1)
			if (arg[0].equals("example"))
				s.readInPuzzle(loadExample());
		
		SudokuGUI g = new SudokuGUI(s);

		// Setup of the frame containing the puzzle
		JFrame f = new JFrame();
		f.setSize(1000,1000);
		f.setTitle("Sudoku Solver");
		f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(g);    
		f.setVisible(true);		
	}
	
	private static int[][] loadExample() {
		int [][] puz= new int[9][9];
		puz[0][0]= 9;
		puz[2][0]= 1;
		puz[0][1]= 2;
		puz[1][1]= 7;
		puz[2][1]= 5;
		puz[5][1]= 4;
		puz[8][1]= 8;
		puz[1][2]= 3;
		puz[2][2]= 6;
		puz[4][2]= 9;
		puz[7][2]= 5;
		puz[3][3]=9;
		puz[5][3]=8;
		puz[8][3]=2;
		puz[0][5]=7;
		puz[3][5]=6;
		puz[5][5]=5;
		puz[1][6]=9;
		puz[4][6]=4;
		puz[6][6]=5;
		puz[7][6]=2;
		puz[0][7]=6;
		puz[3][7]=3;
		puz[6][7]=8;
		puz[7][7]=9;
		puz[8][7]=1;
		puz[6][8]=4;
		puz[8][8]=3;
		
		return puz;
	}
	
	private static void printHelp() {
		
	} 
}

