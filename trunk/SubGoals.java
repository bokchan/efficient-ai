import java.util.LinkedList;


public class SubGoals {
	private static final long serialVersionUID = 1L;
	private int maxSize = 10;
	public static enum TYPE {MINGOALS, MAXGOALS}
	private TYPE type;
	LinkedList<GameState> states; 

	public SubGoals(TYPE type) {
		this.type = type;
		states = new LinkedList<GameState>();
	}

	/***
	 * Adds gamestate iff it is of greater utility than the worst gamestate of the current collection   
	 */
	public void add(GameState e) {
		if (compare(e)) 
		states.addFirst(e);
		if (states.size() > maxSize) states.removeLast();
	}
	
	public boolean compare(GameState e) {
		if (type.equals(TYPE.MINGOALS)) {
			return e.utilityMin.compareTo(states.getFirst().utilityMin) > 0;
		}else {
			return e.utilityMax.compareTo(states.getFirst().utilityMax) < 0;
		}
	}
	
	/***
	 * Polls first/last
	 * Clears subgoals 
	 * Adds subgoal to list   
	 */
	public void cleanup() {
		if (!states.isEmpty()) {
			GameState goal = states.pollFirst();
			states.clear();
			states.add(goal);
		}
	}
	
	public GameState getSubGoal()  {
		return states.getFirst();
	}
}