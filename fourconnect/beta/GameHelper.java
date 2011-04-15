package fourconnect.beta;

public class GameHelper {	
	private static boolean trace = true;
	private static boolean debugmode = false;

	//	/***
	//	 * Converts an int[] to a int[][]  
	//	 * @param array
	//	 * @return
	//	 */
	//	public int[][] convertToMatrix(int[] array) { 
	//		int aIdx = 0;
	//		int[][] matrix = new int[rows][cols];
	//
	//		for (int row = rows-1; row >= 0; row--) {
	//			for (int col = 0; col <cols ;  col++) {
	//				matrix[row][col] = array[aIdx];
	//				aIdx++;
	//			}
	//		}
	//		return matrix;
	//	}

	/**
	 * Converts a byte[][] to an int[][]
	 * @param state
	 * @return
	 */
	public static int[][] toIntMatrix(byte[][] state) {
		int[][] bm = new int[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (int)state[i][j];
			}
		}
		return bm;
	}

	/***
	 * Converts an int[][] to a byte[][]
	 * @param state
	 * @return
	 */
	public static byte[][] toByteMatrix(int[][] state) {

		byte[][] bm = new byte[state.length][state[0].length];
		for(int i = state.length-1; i>= 0; i--) {
			for (int j = 0; j<  state[0].length; j++) {				
				bm[i][j] = (byte)state[i][j];
			}
		}
		return bm;
	}

	public static byte[][] copyArray(byte[][] ba) {
		byte[][] ba2 = new byte[ba.length][ba[0].length];
		for (int i = 0; i < ba.length; i++) {
			for (int j = 0; j < ba[0].length; j++) {
				ba2[i][j] =ba[i][j]; 
			}
		}
		return ba2;
	}

	public static void Trace(boolean isDebug, String s) {
		if (trace)
		{
			if (debugmode) 
			{
				System.out.println("Trace: " + s + System.getProperty("line.separator"));
			} else if (!isDebug)
			{
				System.out.println("Trace: " + s + System.getProperty("line.separator"));
			}
		}		
	}
}
