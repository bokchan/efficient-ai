import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

import org.junit.Test;


public class SubGoalsTest {
	/**
	 * 
	 */
	@Test
	public void TestSubGoal() {
		
		Comparator<GameState> com = new MinComparator();
		
	
		SubGoals s = new SubGoals(SubGoals.TYPE.MAXGOALS);

		GameState g1 = new GameState(new byte[7][6]);
		g1.utilityMax = 12;
		s.add(g1);

		GameState g2 = new GameState(new byte[7][6]);
		g2.utilityMax = 11;
		s.add(g2);

		GameState g3 = new GameState(new byte[7][6]);
		g3.utilityMax = 17;
		s.add(g3);

		GameState g4 = new GameState(new byte[7][6]);
		g4.utilityMax = 15;
		s.add(g4);

		GameState g5 = new GameState(new byte[7][6]);
		g5.utilityMax = 11;
		s.add(g5);

		junit.framework.Assert.assertEquals(g3, s.set.last());
		
		SubGoals s2 = new SubGoals(SubGoals.TYPE.MINGOALS);
		
		GameState g6 = new GameState(new byte[7][6]);
		g6.utilityMin = -12;
		s2.add(g6);
		GameState g7 = new GameState(new byte[7][6]);
		g7.utilityMin = -11;
		s2.add(g7);
		
		GameState g8 = new GameState(new byte[7][6]);
		g8.utilityMin = -17;
		g8.utilityMax = 12;
		s2.add(g8);
		
		GameState g9 = new GameState(new byte[7][6]);
		g9.utilityMin = -15;
		s2.add(g9);
		
		GameState g10 = new GameState(new byte[7][6]);
		g10.utilityMin = -11;
		s2.add(g10);
		
		System.out.println(s2.set.last());
		ArrayList<Integer> i = new ArrayList();
		i.add(1);
		i.add(2);
		System.out.println(i.get(0));
		
		Frontier map = new Frontier();
		
		map.put(1, new GameState(new byte[7][6]));
		map.put(2, new GameState(new byte[7][6]));
	} 
}
