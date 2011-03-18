package andreas.fourconnect.itu.old;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeMap;


/***
 * Use this as a frontier
 * @author Andreas
 *
 */
public class HashedByteArray {
	static int columns = 0;
	static int rows = 0;
	private TreeMap<Double, Integer> priority;
	private Hashtable<Integer, Node> frontier;
	
	class Node{
		int[] state;
		double priority = 0.0;
		
		public Node(int[] state, double priotity) {
			this.state = state;
			this.priority = priotity;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Node [state=").append(Arrays.toString(state))
					.append(", priority=").append(priority).append("]");
			return builder.toString();
		} 	
	}

	public HashedByteArray(int rows, int columns) {
		frontier = new Hashtable<Integer, Node>();
		priority = new TreeMap<Double, Integer>();
		this.rows = rows;
		this.columns = columns;
		
	}
	
	public void putState(int[] state, double priority) {
		int hash = Arrays.hashCode(state);
		frontier.put(hash, new Node(state, priority));
		this.priority.put(priority, hash);
	}
	
	public Node contains(int[] state) {
		return frontier.get(Arrays.hashCode(state));
	}
	
	public Node getTopPriority() {
		SortedSet<Double> sorted = (SortedSet<Double>) priority.keySet(); 
		return frontier.get(priority.get(sorted.last()));
	}
	public static int[] gameStateMatrixToArray(int[][] gamestate){
		int[] arrayState = new int[rows*columns];
		int idx = 0;
		for(int row = rows-1; row >= 0; row-- ) {
			for(int col= 0; col < columns; col++) {
				arrayState[idx] = gamestate[row][col];
				idx++;
			}
		}
		return arrayState;
	}
}