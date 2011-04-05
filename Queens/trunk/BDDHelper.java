import java.util.Arrays;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;


public class BDDHelper {

	/***
	 * Returns the var of a cell variable 
	 * @param row The 0 based row index 
	 * @param col The 0 based column index 
	 * @param N The sidelength of the board  
	 * @return
	 */
	public static int getVarNumber(int N, int col, int row) {
		return (row * N) + col;
	}

	/**
	 * Get all valid domains of the variables in a given BDD,
	 * returned in an array, ordered by variable no.
	 * @param bdd
	 * @return
	 */
	public static int[] getValidDomains(BDD bdd, BDDFactory fact, int size) {
		
		if (QueensLogic.TRACE) {
			BDDHelper.TRACE("getValidDomains");
			
		}
		int vars = size*size;
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
		if (QueensLogic.TRACE) BDDHelper.TRACE("Valid domains: " + Arrays.toString(validDomains));
		return validDomains;
	}

	public static void TRACE(Object msg) {
		TRACE(String.valueOf(msg));
	}

	public static void TRACE(String msg) {
		System.out.println("TRACE: " + msg);
	}

	public static void printBoard(int[][] board) {
		for (int i = 0; i < board.length; i++) {
			String row = "";
			for (int j = 0; j < board[0].length; j++  ) {
				row += board[j][i];
			}
			System.out.println(row);
		}
	}
	
	public static void printValidDomains(int[] domains, int size) {
		for (int i = 0; i< domains.length; i++) {
			if (i > 0 && i % size == 0)  
				System.out.println();
			System.out.print(domains[i]);
		}
		System.out.println();
	}

	public static void printBDDFactory(BDDFactory factory) {
		String  row ="";
		for (int i = 0; i < factory.varNum(); i++) { 
			System.out.print(factory.ithVar(i).var() + ",");
		} 
		System.out.println();
	}
	
	

}