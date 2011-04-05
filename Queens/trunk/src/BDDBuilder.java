package src;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;


public class BDDBuilder {

	/**
	 * Build a BDD for the n-queens problem
	 * @param size The size of the board
	 * @param fact
	 * @return
	 */
	public static BDD buildBDD(int size, BDDFactory fact) {

		// set no. of variables in bdd
		fact.setVarNum(size*size);

		// the five overall rules
		BDD maxOneQueenPerRow = fact.one();
		BDD maxOneQueenPerColumn = fact.one();
		BDD maxOneQueenPerDiaUpRight = fact.one();
		BDD maxOneQueenPerDiaDownRight = fact.one();
		BDD aQueenInAllRows = fact.one();

		for (int r1 = 0; r1 < size; r1++) {

			BDD aQueenInThisRow = fact.zero();

			for (int c1 = 0; c1 < size; c1++) {

				// the current variable
				BDD x_r1c1 = fact.ithVar(Utility.getVarNumber(size, c1, r1));

				// only one queen in this row
				BDD noQueensInOtherColumns = fact.one();
				for (int c2 = 0; c2 < size; c2++) {
					if (c1 != c2) {
						BDD x_r1c2 = fact.nithVar(Utility.getVarNumber(size, c2, r1));
						noQueensInOtherColumns = noQueensInOtherColumns.and(x_r1c2);
					}
				}
				BDD maxOneQueenInThisRow = x_r1c1.imp(noQueensInOtherColumns);
				maxOneQueenPerRow = maxOneQueenPerRow.and(maxOneQueenInThisRow);

				// only one queen in this column
				BDD noQueensInOtherRows = fact.one();
				for (int r2 = 0; r2 < size; r2++) {
					if (r1 != r2) {
						BDD x_r2c1 = fact.nithVar(Utility.getVarNumber(size, c1, r2));
						noQueensInOtherRows = noQueensInOtherRows.and(x_r2c1);
					}
				}
				BDD maxOneQueenInThisColumn = x_r1c1.imp(noQueensInOtherRows);
				maxOneQueenPerColumn = maxOneQueenPerColumn.and(maxOneQueenInThisColumn);

				// only one queen in the up-right diagonal
				BDD noQueensInUpRightDia = fact.one();
				for (int r2 = 0; r2 < size; r2++) {
					int c2 = c1+r2-r1;
					if (r2 != r1 && c2 >= 0 && c2 < size) {
						BDD x_r2c2 = fact.nithVar(Utility.getVarNumber(size, c2, r2));
						noQueensInUpRightDia = noQueensInUpRightDia.and(x_r2c2);
					}
				}
				BDD maxOneQueenInUpRightDia = x_r1c1.imp(noQueensInUpRightDia);
				maxOneQueenPerDiaUpRight = maxOneQueenPerDiaUpRight.and(maxOneQueenInUpRightDia);


				// only one queen in the down-right diagonal
				BDD noQueensInDownRightDia = fact.one();
				for (int r2 = 0; r2 < size; r2++) {
					int c2 = c1+r1-r2;
					if (r2 != r1 && c2 >= 0 && c2 < size) {
						BDD x_r2c2 = fact.nithVar(Utility.getVarNumber(size, c2, r2));
						noQueensInDownRightDia = noQueensInDownRightDia.and(x_r2c2);
					}
				}
				BDD maxOneQueenInDownRightDia = x_r1c1.imp(noQueensInDownRightDia);
				maxOneQueenPerDiaDownRight = maxOneQueenPerDiaDownRight.and(maxOneQueenInDownRightDia);

				// a queen in at least one column in this row
				aQueenInThisRow = aQueenInThisRow.or(x_r1c1);
			}

			// at least one queen in all rows is a conjunction of
			// the test of a queen in every row
			aQueenInAllRows = aQueenInAllRows.and(aQueenInThisRow);
		}
		

		return maxOneQueenPerRow.
		and(maxOneQueenPerColumn).
		and(maxOneQueenPerDiaUpRight).
		and(maxOneQueenPerDiaDownRight).
		and(aQueenInAllRows);
	}
}
