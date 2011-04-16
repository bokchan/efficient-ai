


public class BDDTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		StringBuilder sb = new StringBuilder();
		for (int i=11; i < 13; i++) {
			long time = System.currentTimeMillis();
			for (int j = 4; j < 5; j++) {
				QueensLogic l = new QueensLogic();
				l.initializeGame(i);
				l = null;
			}
			time = System.currentTimeMillis() - time;
			sb.append("Average time: " + time);
		}
		
		System.out.println(sb.toString());
		
	}
}
