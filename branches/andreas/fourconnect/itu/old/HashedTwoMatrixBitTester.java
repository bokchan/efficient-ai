package andreas.fourconnect.itu.old;


public class HashedTwoMatrixBitTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashedTwoBitArray m2 = new HashedTwoBitArray(7, 6, 1);
		String s = "0,1,1,l,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0";
		String s2 = "2,1,2,1,0,1,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
		String s3 = "2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1;";
		String[] sa= s2.split(",");
		int[] testState = new int[sa.length];
		for(int i = 0; i<testState.length;i++) testState[i] = Integer.valueOf(sa[i].trim());
		int[][] intM = new int[][] {
				{0,2,0,2,1,0,1},
				{0,2,0,2,1,0,1},
				{0,2,0,2,1,0,1},
				{0,2,0,2,1,0,1},
				{0,2,0,2,1,0,1},
				{0,2,0,2,1,0,1}};
		
		byte[] ba = m2.toByteArray(intM);
		System.out.println("m2 as string" + m2.asString(ba));
		Byte b1 = new Byte( Byte.parseByte("10", 2));
		System.out.println("Bytesdf" +  Integer.toBinaryString(b1));
	}
}