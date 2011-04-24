import javax.swing.*;

public class ShowSudoku {
	public static void main(String[] arg){
		
		ISudokuSolver s = new SudokuSolver();
		s.setup(3);
	
	/**	int [][] puz= new int[9][9];
		puz[1][0]= 7;
		puz[1][1]= 4;
		puz[4][1]= 9;
		puz[4][2]= 2;
		puz[4][3]= 1;
		puz[4][5]= 5;
		puz[4][8]= 7;
		puz[0][7]= 9;
		puz[6][6]= 6;
		puz[7][7]= 4;
		puz[7][8]= 2;
	//**/
		
		//s.readInPuzzle(puz);
		//System.out.println(s.getPuzzle());
		
		//s.setValue(4,1,9);
		s.setValue(1,1,4);
		s.setValue(7,8,2);
		//s.setValue(7,7,4);
		s.solve();
		
	/**	SudokuGUI g = new SudokuGUI(s);

		// Setup of the frame containing the puzzle
		JFrame f = new JFrame();
		f.setSize(1000,1000);
		f.setTitle("Sudoku Solver");
		f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(g);    
		f.setVisible(true);
		**/
		
	}
}

