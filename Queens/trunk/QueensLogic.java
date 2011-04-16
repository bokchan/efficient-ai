import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

/***
 * 
 * 
 *
 */
public class QueensLogic {

	static boolean TRACE = true;
	private int x = 0;
	private int y = 0;
	private int[][] board;

	private int NODES = 200000;
	private int CACHE = 20000;

	static BDDFactory factory;
	static BDD queen;
	static BDD solution;
	
	/***
	 * Define the BDD's needed 
	 */

	public QueensLogic() {

	}

	public void initializeGame(int size) {
		if (TRACE) BDDHelper.TRACE("Initializing game with size" + size);
		this.x = size;
		this.y = size;
		this.board = new int[x][y];
		
		// Using Whaleys calculation of the number of nodes
		NODES = (int) (Math.pow(4.42, x-6))*1000;
		CACHE = NODES/10; 
		
		factory = JFactory.init(NODES, CACHE);
		factory.setVarNum((int) x * y );
		
		queen = BDDBuilder.buildBDD(x, factory);
		
		BDDHelper.TRACE("initGame()");
		 
		if (TRACE) BDDHelper.TRACE(String.format("Factory with #nodes: %s, cache: %s, vars: %s", factory.getNodeTableSize(), factory.getCacheSize(), factory.varNum()));
	}

	public int[][] getGameBoard() {
		
		return board;
	}

	public boolean insertQueen(int column, int row) {
		if (TRACE) BDDHelper.TRACE(String.format("Inserting queen at position: (%s,%s)", column, row));
		if (board[column][row] == -1 || board[column][row] == 1) {
			return true;
		}

		board[column][row] = 1;

		// restrict the BDD with current assignments
		BDD restricted = BDDRestrictor.restrictBDD(queen, factory, board);
		
		// find valid domains for all variables
		int validDomains[] = BDDHelper.getValidDomains(restricted, factory, x);
		
		// update the board using these domains
		BoardUpdater.update(validDomains, board);
		
		return true;
	}
}
