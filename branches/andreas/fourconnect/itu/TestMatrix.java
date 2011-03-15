package andreas.fourconnect.itu;

public class TestMatrix 
{
	public static void main(String[] args) {
		Long t1 = System.nanoTime();
		for (int i = 0; i < 6000000; i++) {
			int[][] m = new int[6][7];
			for (int j = 0; j< 6; j++) {
				for (int k = 0; k < 7; k++) {
					m[j][k] = 1;
				}
			}
		}
		Long t2 = System.nanoTime();
		System.out.println((t2-t1)/1000000);		
	}
}