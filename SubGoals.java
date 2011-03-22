import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SubGoals {
	private static final long serialVersionUID = 1L;
	private int maxSize = 10;
	public static enum TYPE {MINGOALS, MAXGOALS}
	private TYPE type;
	private GameState current;

	SortedSet<GameState> set;
	MinComparator cMin = new MinComparator();
	MaxComparator cMax = new MaxComparator();

	public SubGoals(TYPE type) {
		this.type = type;

		if (type.equals(TYPE.MINGOALS))
			set = new TreeSet<GameState>(cMin);
		else 
			set = new TreeSet<GameState>(cMax);
	}

	/***
	 * Adds gamestate iff it is of greater utility than the worst gamestate of the current collection   
	 */
	public void add(GameState e) {
		if (!set.isEmpty()) {
			if (compare(e)) {
				set.add(e);
			}
		} else {
			if ((e.utilityMin != e.utilityMax)) set.add(e );
		}
		if (set.size() > maxSize) remove();
	}

	/***
	 * 
	 * @param e
	 * @return
	 */
	public boolean compare(GameState e) {
		if (type.equals(TYPE.MINGOALS)) {
			return cMin.compare(e, set.last()) > 0;
		}else {
			return cMax.compare(e,set.first()) < 0;
		}
	}

	/***
	 * Polls first/last
	 * Clears subgoals 
	 * Adds subgoal to list   
	 */
	public void cleanup(boolean clear) {
		if (!set.isEmpty()) {
			if (clear) set.clear();
			List<GameState> tmp = new ArrayList<GameState>();
			for (GameState goal : set) {
				if (goal.depth <= current.depth) 
					tmp.add(goal);
			}

			set.removeAll(tmp);
			// Since depth has increased with 2, remaining subgoal have their utilities updated  	
			for (GameState goal : set) {
				if (type.equals(SubGoals.TYPE.MINGOALS)) goal.utilityMin -= 2;
				else goal.utilityMax+=2;

			}

			tmp = null;
		}
	}

	public GameState getSubGoal()  {
		if (!set.isEmpty()) 
			return set.last();
		else 
			return null;
	}


	public void remove() {
		this.set.remove(set.first());
	}

	public void setCurrent(GameState g) {
		this.current = g;
	}	
}