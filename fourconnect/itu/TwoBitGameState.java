package andreas.fourconnect.itu;

import java.util.BitSet;

public class TwoBitGameState  {
	int rows;
	int cols;
	BitSet[] state;

	public TwoBitGameState(int cols, int rows) {

		this.cols = cols;
		this.rows = rows;
		 state = new BitSet[cols*rows];
	}

	public static void main(String[] args ) throws Exception {
		
		
		 		 
			
		  // byte1 [00011001][00100101][00011001][00010000][00100000][00100101]
		
		// 2 * 8([6 bit used 2 bits overhead])  per columns * 7
		// 16 * 7 102 bits *
		
		
		/***
		 * 
		 * 101
		 * 101
		 * 212
		 * 110
		 * 220
		 * 112
		 */

		 
		Byte[] bar = new Byte[] {
		Byte.valueOf("00011001", 2),
		Byte.valueOf("00100101", 2),
		Byte.valueOf("00011001", 2),
		Byte.valueOf("00010000", 2),
		Byte.valueOf("00100000", 2),
		Byte.valueOf("00100101", 2)};
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<bar.length; i++) {	
			Byte b1 = bar[i];
			String s = Integer.toBinaryString(b1.intValue());
			while(s.length() < 7) s = 0 + s;
			
		}
		
		System.out.println("Byte: " + Integer.parseInt("01", 2 ));
		
		String out = sb.toString(); 
		System.out.println(out);
		
		String chars = "";  
		 
		for (int i = 0; i < sb.length();i++ ) {
			
			
		}
		
		TwoBitGameState tbs = new TwoBitGameState(7, 7);
		
		
		tbs.state[0] = new TwoBit(false, false);
		tbs.state[1] = new TwoBit(true, false);
		tbs.state[2] = new TwoBit(true, false);
		
		System.out.println(tbs.state[2]);
		int size = (int)Math.pow(7, 6);
		long t1 = System.nanoTime();
		Sizeof s = new Sizeof();
		s.init();
		
		Object[] a = new Object[size];
		
		
		for (int j = 0; j<size; j++) {
			int[][] state = new int[6][7];
			for (int i = 0; i < 42; i++) {
				// 0-6 = 0,
				// 7-13 = 1, 
				// 35-41 = 5, 0,1,2,3,4,5,6
				state[(int)(i/7)][i%7] =  (int) (Math.random() * 3);	
			}
			
			a[j] = state;
		}
		s.getMemUsage();
		
		//System.out.println("Time to build: " + (System.nanoTime()-t1)/1000000);
		
		
		/***
		 * 
		 */
		
		s = new Sizeof();
		s.init();
		Object[] str = new Object[size];
		
		t1 = System.nanoTime();
		for (int j = 0; j<size; j++) {
			String state = new String("011l010010001000101100001010001010000000000");			
			str[j] = state;
		}
		s.getMemUsage();


//		t1 = System.nanoTime();
//		int counter = 0;
//		for (Object o: a ) {
//			toByteMatrix((int[][])o);
//			counter++;
//		}
//		System.out.println("Time to convert: " + (System.nanoTime()-t1)/1000000);
//		System.out.println(counter);
		
		/***
		 * 1,2
		 * 3,4
		 * 5,6
		 */
		//TwoBitGameState tbs = new TwoBitGameState(2, 2);
		//tbs.toBitArray(m);
		
	}

	private static byte[][] toByteMatrix(int[][] state) {
		byte[][] bm = new byte[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (byte)state[i][j];
			}
		}
		return bm;
	}
}
