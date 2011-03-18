package andreas.fourconnect.itu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PerformanceTesting {

	/**
	 * @param args
	 */
	@Test
	public void TestGameStateExploration() {
		StringBuilder sb = new StringBuilder();
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("C:\\RegexInputMinimal.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line

			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
					sb.append(strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		String[] regs = sb.toString().split(";");
		
		Map<Integer, Integer> count = new HashMap<Integer, Integer>();

		for (String re : regs) {
			if (count.containsKey(re.length())) {
				Integer i = count.get(re.length());
				i++;
				count.put(re.length(), i);
			}
			else {
				count.put(re.length(), 1);
			}
		}
		
		Arrays.sort(regs);
		System.out.println(Arrays.toString(regs));
		

		Pattern p = Pattern.compile(sb.toString().replace(";", "|"));
		System.out.println("Testing gamestate exploration");
		System.out.println("Regex pattern: " + p.pattern());
		HashedByteMatrix bitA = new HashedByteMatrix(7, 6, 1);

		Stopwatch w = new Stopwatch();
		int matches=0;
		byte[] board = new byte[] {2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1};
		byte[][] boardState;  
		for (int i = 0; i < Math.pow(7, 6); i++) {
			// 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
			// 0,00,10,20,30,40,4   1,0
			boardState = new byte[6][7];
			StdRandom.shuffle(board);
			for (int j = 0; j< 42; j++ ) {
				boardState[j/7][j%7] = board[j];
			}
			
			Arrays.deepHashCode(boardState);
			String s ="";
			for (int j = 0; j< boardState.length; j++ ) {
				for (int k= 0; k < boardState[0].length; k++) {
					s += String.valueOf(boardState[j][k]);
				}
			}
			

			Matcher m = p.matcher(bitA.asString(boardState)); 
			if (m.find()) matches++;
		}

		System.out.println("Regex pattern stats: "+  count.toString());
		System.out.println("Time to match in ms: " + w.elapsedTime());
		System.out.println("Matches: "+ matches);
	}
	@Test
	public void TestHashing() {
		HashedByteMatrix m = new HashedByteMatrix(7,6,1);
		int[][] boardState;
		Object[] list = new Object[(int) Math.pow(7, 7)]; 
		int[] board = new int[] {2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1};
		for (int i = 0; i < Math.pow(7, 7); i++) {
			// 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
			// 0,00,10,20,30,40,4   1,0
			boardState = new int [6][7];
			StdRandom.shuffle(board);
			for (int j = 0; j< 42; j++ ) {
				boardState[j/7][j%7] = board[j];
			}
			
			list[i] = m.toByteMatrix(boardState);
		}
		long t1 = System.nanoTime();
			 for (Object o : list) {
				 Arrays.deepHashCode((byte[][]) o);
		 }
		System.out.println("Testing hashing performance. Hashing 7^7 byte[][] using Arrays.deepHashCode") ;	
		System.out.println((System.nanoTime()-t1)/1000000);
		t1= System.nanoTime();
		int c =(int) Math.pow(7, 6);
		 for (Object o : list) {
			 if (c == 0) break;
			 Arrays.deepHashCode((byte[][]) o);
			 c--;
			 
	 }
		System.out.println("Testing hashing performance. Hashing 7^6 byte[][] using Arrays.deepHashCode") ;
		System.out.println((System.nanoTime()-t1)/1000000);
	}
	
	@Test
	public void TestByteMatrixConversion() {
		byte[] board = new byte[] {2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1};
		byte[][] boardState;
		Stopwatch w = new Stopwatch(); 
		for (int i = 0; i < Math.pow(7, 7); i++) {
			// 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
			// 0,00,10,20,30,40,4   1,0
			boardState = new byte[6][7];
			//StdRandom.shuffle((Object[]) board);
			for (int j = 0; j< 42; j++ ) {
				boardState[j/7][j%7] = board[j];
			}
			
		}
		System.out.println("Time to create 7^7 byte[][]: " + w.elapsedTime());
		
		for (int i = 0; i < Math.pow(7, 6); i++) {
			// 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
			// 0,00,10,20,30,40,4   1,0
			boardState = new byte[6][7];
			//StdRandom.shuffle((Object[]) board);
			for (int j = 0; j< 42; j++ ) {
				boardState[j/7][j%7] = board[j];
			}
		}
		System.out.println("Time to create 7^6 byte[][]: " + w.elapsedTime());
	}
}