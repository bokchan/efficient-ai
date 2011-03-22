import org.junit.Assert;
import org.junit.Test;

public class RegexTester 
{
	GameHeuristics heuristics = new GameHeuristics(7, 6, 1);
	@Test
	public void Test4InARow() {
		/***
		 * 
		 * s1
		 0000000
		 0000000
		 0000000
		 0000000
		 0000020
		 1111220
		 * 
		 * s2
		 0000000
		 0000000
		 1000000
		 1000000
		 1000020
		 1000220
		 * 
		 * 
		 * s3
		 0000000
		 0000000
		 0001000
		 0010000
		 0100020
		 1000220
		 
		 * s4
		 0000000
		 0000000
		 0001000
		 0000100
		 0000010
		 0000221
		 */
		
		
		 String s1 = "111122000000200000000000000000000000000000"; 
		 String s2 = "100022010000201000000100000000000000000000";
		 String s3 = "100022001000200010000000100000000000000000";
		 String s4 = "000022100000100000100000100000000000000000"; 
		 
		 RegexResult result = heuristics.matchFourInARow(s1, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s2, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s3, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s4, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
	}
	
	@Test
	public void Test3InARow() {
		/***
		 * 
		 * s1
		 0000000
		 0000000
		 0000000
		 0000000
		 0001110
		 1112221
		 
		 * 
		 * s2
		 0000000
		 0000000
		 0000000
		 0000000
		 0000020
		 0111220
		 
		 * 
		 * 
		 * s3
		 0000000
		 0000000
		 0000000
		 1000000
		 1000020
		 1000220
		 
		 * s4
		 0000000
		 0000000
		 0000000
		 0002100
		 0000010
		 0000221
		 
		 
		  s5
		 0000000
		 0200000
		 0010000
		 0001000
		 0010100
		 0000000
		 
		 s6
		 2000000
		 0100000
		 0010000
		 0000000
		 0002100
		 0000220
		 
		 0000000
		 0000000
		 0000000
		 0002200
		 0001110
		 0021122
		 */
		
		
		 String s1 = "111222100011100000000000000000000000000000"; 
		 String s2 = "011122000000200000000000000000000000000000";
		 String s3 = "100022010000201000000000000000000000000000";
		 String s4 = "000022100000100002100000000000000000000000"; 
		 String s5 = "000000000101000001000001000002000000000000";
		 String s6 = "000022000021000000000001000001000002000000";
		 String s7 = "002112200011100002200000000000000000000000"; 

		 RegexResult result = heuristics.matchThreeInARow(s1, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1THREEINAROW, result.resultstate);
		 
		 result = heuristics.matchThreeInARow(s2, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1THREEINAROW, result.resultstate);
		 
		 result = heuristics.matchThreeInARow(s3, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1THREEINAROW, result.resultstate);
		 
		 result = heuristics.matchThreeInARow(s4, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1THREEINAROW, result.resultstate);
		 
		 result = heuristics.matchThreeInARow(s5, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1THREEINAROW, result.resultstate);
		 
		 result = heuristics.matchThreeInARow(s6, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1THREEINAROW, result.resultstate);
		 
		 result = heuristics.matchThreeInARow(s7, 1);
		 Assert.assertEquals(10, result.matchStartIdx);
	}
	
	@Test
	public void TestKillerMoves() {
		/***
		 * 
		 * s1
		 0000000
		 0000000
		 0000000
		 0000000
		 0000000
		 0101000
		 
		  */
		
		
		 String s1 = "010100000000000000000000000000000000000000"; 
		 String s2 = "011122000000200000000000000000000000000000";
		 String s3 = "100022010000201000000000000000000000000000";
		 String s4 = "000022100000100000100000000000000000000000"; 
		 String s5 = "000000000101000001000001000002000000000000";
		 String s6 = "000022000001000000000001000001000002000000";
		 
		 
//		 RegexResult result = heuristics.matchKillerMoves(s1, 1);
//		 Assert.assertEquals(1, result.matchStartIdx);		 
	}

	
	@Test
	public void TestGameState() 
	{
		byte[] board = new byte[] {2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1};
		byte[][] boardState = new byte[6][7];
		int i = 0;
			for (int j = 5; j>= 0; j-- ) {
				for (int k = 0; k < 7; k++){
				boardState[j][k] = board[i];
				i++;
				}
			}
		
			
		GameState g = new GameState(boardState);
		GameHelper.Trace(false, g.stateAsString());
		GameState g2 = g.createGamestate(2, 3);
		GameHelper.Trace(false, g2.stateAsString());
		
				
	}
	@Test
	public void test() {
		System.out.println(!(1==1));
	}
}