import java.util.SortedMap;
import java.util.TreeMap;

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
		  s5
		 0000000
		 0000000
		 0000000
		 0000000
		 0012000
		 0122100
		 */
		
		
		 String s1 = "111122000000200000000000000000000000000000"; 
		 String s2 = "100022010000201000000100000000000000000000";
		 String s3 = "100022001000200010000000100000000000000000";
		 String s4 = "000022100000100000100000100000000000000000"; 
		 String s5 = "012210000120000000000000000000000000000000";
		 
		 RegexResult result = heuristics.matchFourInARow(s1, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s2, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s3, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s4, 1);
		 Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		 
		 result = heuristics.matchFourInARow(s5, 1);
		 //Assert.assertEquals(RegexEvaluation.MATCH_RESULT_STATE.PLAYER1WON, result.resultstate);
		
	}
	
	@Test
	public void TestGameStateI() 
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
		GameHelper.Trace(g.stateAsString());
		GameState g2 = g.createGamestate(2, 3);
		GameHelper.Trace(g2.stateAsString());
		
		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		map.put(3,3);
		map.put(2,1);
		map.put(3,2);
		System.out.println(map.toString());
	}
	
	
}