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
		
		factory = JFactory.init(NODES, CACHE);
		factory.setVarNum((int) x * y );
		
		if (TRACE) BDDHelper.TRACE(String.format("initialized factory with #nodes: %s, cache: %s, vars: %s", NODES, CACHE, factory.varNum()));
		queen = BDDBuilder.buildBDD(x, factory);
		BDDHelper.TRACE("initGame()");
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
		
		BDDHelper.TRACE("Factory");
		BDDHelper.printBDDFactory(factory);
		BDDHelper.TRACE("Validdomains: " + validDomains.length);
		BDDHelper.printValidDomains(validDomains, x);
		
		if (TRACE) {			
			solution = queen.satOne();
	        double result = queen.satCount();
	        BDDHelper.TRACE("There are " + (long) result + " solutions.");
            double result2 = solution.satCount();
            BDDHelper.TRACE("Here is "+(long) result2 + " solution:");
            //solution.printSet();
            System.out.println();
		}
		
		return true;
	}
	
}
