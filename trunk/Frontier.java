import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Frontier extends HashMap<Integer, GameState> {
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	List<Integer> order = new ArrayList<Integer>();

	@Override
	public GameState put(Integer key, GameState value) {
		order.add(key);
		return super.put(key, value);
	} 

	@Override
	public GameState remove(Object key) {
		order.remove(key);
		return super.remove(key);
	}

	public GameState poll(){
		if (order.size() > 0) { 
		
		int hash = order.get(0);	
		order.remove(0);

		return super.remove(hash);
		} else {
			return null;
		}
	}

	public GameState getAt(int index) {
		return super.get(order.get(0));
	}
}