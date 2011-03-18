package andreas.fourconnect.itu;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import andreas.fourconnect.itu.HashedByteMatrix.GameState;



public class HashedByteMatrixTester {
	public static void main(String[] args) throws Exception {
		int rows = 6;
		int columns = 7;
		int size =(int) Math.pow(7, 6);

		Long t1 = System.nanoTime();
		HashedByteMatrix a = new HashedByteMatrix(rows, columns, 1);	

		/***
		 * Simulate 6 ply = 7^7
		 * 
		 */

		Sizeof sizeof = new Sizeof();
		sizeof.init();

		for (int j = 0; j<size; j++) {
			int[][] state = new int[6][7];
			double priority = 0.0;
			for (int i = 0; i < 42; i++) {
				state[i/7][i%7] = (int) (Math.random() * 3);

			}
			a.putGameState(state, 2);
		}
		t1 =((System.nanoTime() - t1)/1000000 );
		System.out.println("HashedByteMatrixBuildTime: " + t1);

		sizeof.getMemUsage();

		t1 =  System.nanoTime();

		String s = "0,1,1,l,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0";
		String s2 = "2,1,2,1,0,1,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
		String s3 = "2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1";
		String[] sa= s3.split(",");
		int[] testState = new int[sa.length];
		
		HashedByteMatrix m = new HashedByteMatrix(7,6,1 );
		int[][] intM = new int[][] {
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0}};
		
		m.putGameState(intM, 2);
		
	
		Map<Integer, GameState> list = m.getExplored();
		list.put(123,null);
		list.put(122,null);
		list.put(1212,null);
		System.out.println(list.size());

		
		for (GameState g : list.values()) {
			System.out.println("To byte matrix: " + m.asString(g.state));
			System.out.println("Byte matrix Hash" + Arrays.deepHashCode(g.state));
		}
		
		m.evaluateGameState(intM);
	

	}  
	//@Test
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
t		 * 0,2,0,0,0,0,0,
		 * 2,0,2,1,0,1,0,
		 * 2,0,1,0,0,0,1,
		 * 2,1,2,1,2,1,0,
		 * 
		 * Se: 212121020100012021010020000000010110010001;
		 * 
		 *
		 */

		HashedTwoBitArray m2 = new HashedTwoBitArray(7, 6, 1);
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

		System.out.println( "Byte array: "+ Arrays.toString(ba));
		System.out.println(m2.asString(ba));
		//Assert.assertEquals(s.replaceAll(",", ""), matrix.asString(m1));

		//System.out.println(matrix.evaluateGameState(m1));
	}
	
	@Test
	public void testExploreGameState() {
		HashedByteMatrix m = new HashedByteMatrix(7,6,1);
		int[][] intM = new int[][] {
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{2,0,0,0,0,0,0}};		
		m.evaluateGameState(intM);
		Map<Integer, GameState> list = m.getExplored();
		for (GameState g : list.values()) {
			System.out.println(g.toString());
		} 
		System.out.println(list.size());
	}	
}
