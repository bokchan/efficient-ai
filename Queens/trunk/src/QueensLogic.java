package src;
/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

public class QueensLogic {
	private int x = 0;
	private int y = 0;
	private int[][] board;

	private static int NODES = 2000000;
	private static int CACHE = 200000;
	private BDD nQueensBDD; 
	private BDDFactory fact;


	public QueensLogic() {
		//constructor
	}

	public void initializeGame(int size) {
		this.x = size;
		this.y = size;
		this.board = new int[x][y];

		fact = JFactory.init(NODES,CACHE);

		// build bdd with rules
		nQueensBDD = BDDBuilder.buildBDD(x, fact);
		System.out.println(fact);
	}


	public int[][] getGameBoard() {
		return board;
	}

	public boolean insertQueen(int column, int row) {
		System.out.println("Inserting @(" + row + "," + column +")");
		if (board[column][row] == -1 || board[column][row] == 1) {
			return true;
		}

		board[column][row] = 1;

		// restrict the BDD with current assignments
		BDD restricted = BDDRestrictor.restrictBDD(nQueensBDD, fact, board);

		// find valid domains for all variables
		int validDomains[] = getValidDomains(restricted, fact);

		// update the board using these domains
		BoardUpdater.update(validDomains, board);
		System.out.println("Set with domains");
		nQueensBDD.printSetWithDomains();
		return true;
	}

	/**
	 * Get all valid domains of the variables in a given BDD,
	 * returned in an array, ordered by variable no.
	 * @param bdd
	 * @return
	 */
	private int[] getValidDomains(BDD bdd, BDDFactory fact) {

		int vars = x*x;
		int validDomains[] = new int[vars]; 
		
		// step through all variables
		for (int i = 0; i < vars; i++) {
			BDD var = fact.ithVar(i);
			BDD restricted = bdd.restrict(var);

			// if the restricted BDD is not unsatifiable we can place
			// a queen in the spot
			if (!(restricted.isZero())) {
				validDomains[i] = 1;
			}
		}
		
		return validDomains;
	}
}
