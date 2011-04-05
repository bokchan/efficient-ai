package src;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;


public class BDDRestrictor {

	/**
	 * Gets the current assignment of queens on the board and
	 * restrict a given BDD with these assignments.
	 * @param nQueensBDD
	 * @param fact
	 * @param board
	 * @return
	 */
	public static BDD restrictBDD(BDD nQueensBDD, BDDFactory fact, int[][] board) {
		
		// Copy the BDD so we don't overwrite the original
		BDD nQueensBDDRestricted = nQueensBDD;

		// Loop through positions on the board
		for (int x = 0; x < board.length; x++)
			for (int y = 0; y < board.length; y++)
			{	
				 System.out.print("fact : " + fact.ithVar(Utility.getVarNumber(board.length, x, y)));
				// Check if there is a queen on this position
				if (board[x][y] == 1) {
					int varNum = Utility.getVarNumber(board.length, x, y);

					// Make a restriction for this variable and apply it to the restricted BDD
					BDD Restriction = fact.ithVar(varNum);
					nQueensBDDRestricted = nQueensBDDRestricted.restrict(Restriction);
				}
			}
		System.out.println("");	
		// Return the restricted BDD
		return nQueensBDDRestricted;
	}
}
