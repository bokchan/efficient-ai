package unused;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;


public class BDDTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BDDFactory B = BDDFactory.init(10,10);
		B.setVarNum(4);
		
		B.printAll();
		
		BDD True = B.one();
		BDD False = B.zero();
		
		BDD b =  True.or(False).and(True).biimp(True);
		BDD d = B.one(); 
		b.andWith(True).andWith(d);
		System.out.print(b.isOne());
		
		BDD[][] x = new BDD[2][2];
		for (int i = 0; i < 2; i++) 
			for (int j = 0; j< 2; j++  )
				x[i][j] = B.ithVar(i * 2 + j);
		
		BDD u = x[1][1].apply(B.one(), B.and);
		System.out.println(x[1][1].isOne());
		System.out.println(u.isOne());	
	}
}
