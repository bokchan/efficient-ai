import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;


public class SudokuUnitTest {
	String path = System.getProperty("user.dir") + System.getProperty("file.separator");
	String linebreak = System.getProperty("line.separator");

	@Test
	public void TestLevels() throws IOException{
		String[] files = {"sudoku_easy.txt", "sudoku_standard.txt", "sudoku_hard.txt", "sudoku_expert.txt", "sudoku_extreme.txt",  
				"sudoku_sudoku_download_net_easy.txt", "sudoku_sudoku_download_net_medium.txt", "sudoku_sudoku_download_net_medium.txt", "sudoku_sudoku_download_net_verydiff.txt", 
				"sudoku_difficult_no_source.txt"};
		//"10_5sudoku_plain.txt"
		int sudokuCount = 4;

		StringBuilder results = new StringBuilder();
		results.append("Time" + linebreak);
		Stopwatch total = new Stopwatch();
		for (String file : files) {
			results.append(file + linebreak);
			for (String sudoku : readFile(file, sudokuCount)) {
				
				Stopwatch elapsed = new Stopwatch();
				for (int i = 0; i< 5; i++) {
					SudokuSolver ss = new SudokuSolver();
					ss.setup(3);
					int idx = 0;

					for (char x: sudoku.toCharArray()) {

						ss.setValue(ss.GetColumn(idx), ss.GetRow(idx),  Integer.valueOf(String.valueOf(x)));
						idx++;
					}
					Assert.assertTrue(ss.solve());
				}				
				results.append(elapsed.elapsedTime() / 5+linebreak);
			}
		} 
		double time = total.elapsedTime();
		results.append(time + linebreak);
		results.append("avg: " + time/ (files.length * 4)+ linebreak);
		System.out.println(results.toString());
	}

	private ArrayList<String> readFile(String filename, int max)throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		ArrayList<String> sudokus = new ArrayList<String>();
		int count = 0;
		while(count < max) {
			String sudoku = r.readLine();
			if (sudoku == null) break;
			sudokus.add(sudoku);
			count++;
		}
		return sudokus;
	}

}