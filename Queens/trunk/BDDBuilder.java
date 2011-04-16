

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class BDDBuilder {
	/**
	 * Build a BDD for the n-queens problem
	 * @param size The size of the board
	 * @param fact
	 * @return
	 */

	private static int size;
	private static BDD[][] X;
	private static BDD queen;

	public static BDD buildBDD(int size, BDDFactory fact) {
		if (QueensLogic.TRACE) BDDHelper.TRACE("Building BDD");
		BDDBuilder.size = size;
		BDDBuilder.queen = fact.one();
		X = new BDD[size][size];
		int i, j;
		for (i = 0; i < size; i++)
			for (j = 0; j < size; j++)
			{
				X[i][j] = fact.ithVar(i * size + j);
			}

		/* Build requirements for each variable(field) */
		for (i = 0; i < size; i++)
			for (j = 0; j < size; j++) {
				build(i, j, fact);
			}
		return queen;
	}

	private static void build(int i, int j, BDDFactory fact) {
		// a = row, b = col, c = dia, d = dia, e = 1qperrow 
		BDD a = fact.one(), b = fact.one(), c = fact.one(), d = fact.one(), e = fact.one();

		int k, l;

		/* No one in the same column */
		for (l = 0; l < size; l++) {
			if (l != j) {
				BDD u = X[i][l].apply(X[i][j], BDDFactory.nand);
				a.andWith(u);
			}
		}

		/* No one in the same row */
		for (k = 0; k < size; k++) {
			if (k != i) {
				BDD u = X[i][j].apply(X[k][j], BDDFactory.nand);
				b.andWith(u);
			}
		}

		/* No one in the same up-right diagonal */
		for (k = 0; k < size; k++) {
			int ll = k - i + j;
			if (ll >= 0 && ll < size) {
				if (k != i) {
					BDD u = X[i][j].apply(X[k][ll], BDDFactory.nand);
					c.andWith(u);
				}
			}
		}

		/* No one in the same down-right diagonal */
		for (k = 0; k < size; k++) {
			int ll = i + j - k;
			if (ll >= 0 && ll < size) {
				if (k != i) {
					BDD u = X[i][j].apply(X[k][ll], BDDFactory.nand);
					d.andWith(u);
				}
			}
		}

		/*  One queen in each row */
		BDD u = fact.zero();
		for (l = 0; l< size; l++) {
			u = u.apply(X[i][l], BDDFactory.xor);
		}  
		// Only one queen per row
		e.andWith(u);

		c.andWith(d);
		b.andWith(c);
		a.andWith(b);
		BDDBuilder.queen.andWith(a);
		BDDBuilder.queen.andWith(e);
	}
}
