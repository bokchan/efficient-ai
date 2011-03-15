package andreas.fourconnect.itu;

import java.util.Arrays;

import org.junit.Test;



public class HashedIntArrayTester {
	public static void main(String[] args) throws Exception {
		int rows = 6;
		int columns = 7;
		int size =(int) Math.pow(7, 7);
		
		Long t1 = System.nanoTime();
		HashedByteArray a = new HashedByteArray(rows, columns);	
		 
		/***
		 * Simulate 6 ply = 7^7
		 * 
		 */
		for (int j = 0; j<size; j++) {
			int[] state = new int[42];
			double priority = 0.0;
			for (int i = 0; i < 42; i++) {
				state[i] =  (int) (Math.random() * 3);
				priority = Math.random() * 50;
			}
			a.putState(state, priority);
		}
		t1 =((System.nanoTime() - t1)/1000000 );
		System.out.println("HashedIntArrayBuildTime: " + t1);
		
		HashedByteArray.Node n = a.getTopPriority();
		
		System.out.println(n.toString());
		
		Sizeof sizeof = new Sizeof();
		sizeof.init();
		t1 = System.nanoTime();
		HashedByteMatrix matrix = new HashedByteMatrix(columns, rows, 1);
		for (int i = 0; i < size; i++) {
			int[][] m = new int[7][6];
			for (int j = 0; j< 7; j++) {
				for (int k = 0; k < 6; k++) {
					m[j][k] = (int) (Math.random() * 3);
				}
			}
			matrix.putGameState(m);
		}
		sizeof.getMemUsage();
		t1 =((System.nanoTime() - t1)/1000000 );
		System.out.println("HashedIntMatrixBuildTime: " + t1);
		
		t1 =  System.nanoTime();
		
//		for (Integer i : matrix.frontier.keySet()) {
//			GameState s = matrix.frontier.get(i);
//			int[][] intarray = matrix.toIntMatrix(s.state);
//			matrix.evaluateGameState(intarray);
//		}
		
		String s = "0,1,1,l,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0";
		String s2 = "2,1,2,1,0,1,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
		String s3 = "2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1";
		String[] sa= s3.split(",");
		int[] testState = new int[sa.length];
		for(int i = 0; i<testState.length;i++) testState[i] = Integer.valueOf(sa[i].trim());
		int[][] m1 = matrix.convertToMatrix(testState);
		matrix.evaluateGameState(m1);
		
		t1 =((System.nanoTime() - t1)/1000000 );
		System.out.println("HashedIntMatrix Evaluation: " + t1);
		
	}  
	@Test
	public void TestMatrixToStringConversion() {
		HashedByteMatrix matrix = new HashedByteMatrix(7,6,1);
		/***
		 * 
		 * S
		 * 0,0,0,0,0,0,0
		 * 0,1,0,1,0,0,0
		 * 0,0,1,0,1,0,0
		 * 0,1,0,1,1,0,0
		 * 1,0,0,0,1,0,0
		 * 0,1,1,1,1,0,0
		 * 
		 * 
		 * 	
		 * 
		 * S2
		 * 0,0,0,0,0,0,0;
		 * 0,0,0,0,0,0,0,
		 * 0,0,0,0,0,0,0,
		 * 2,0,0,0,0,0,0,
		 * 2,0,0,0,0,0,0,
		 * 2,1,2,1,0,1,0,
		 * 
		 * S3
		 * 
		 * 0,0,1,0,0,0,1;
		 * 0,0,0,1,0,1,1,
		 * 0,2,0,0,0,0,0,
		 * 2,0,2,1,0,1,0,
		 * 2,0,1,0,0,0,1,
		 * 2,1,2,1,2,1,0,
		 * 
		 * Se: 212121020100012021010020000000010110010001;
		 * 
		 *
		 */
		
		HashedByteMatrix2 m2 = new HashedByteMatrix2(7, 6, 1);
		String s = "0,1,1,l,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0";
		String s2 = "2,1,2,1,0,1,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
		String s3 = "2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1;";
		String[] sa= s2.split(",");
		int[] testState = new int[sa.length];
		for(int i = 0; i<testState.length;i++) testState[i] = Integer.valueOf(sa[i].trim());
		int[][] intM = new int[][] {
				{0,1,0,1,1,0,1},
				{0,1,0,1,1,0,1},
				{0,1,0,1,1,0,1},
				{0,1,0,1,1,0,1},
				{0,1,0,1,1,0,1},
				{0,1,0,1,1,0,1}}; 
		
		
		byte[] ba = m2.toByteArray(intM);
		System.out.println(Arrays.toString(ba));
		System.out.println(m2.asString(ba));
		//Assert.assertEquals(s.replaceAll(",", ""), matrix.asString(m1));
		
		//System.out.println(matrix.evaluateGameState(m1));
		
	}
	
	
	
		 
}
