
import java.lang.reflect.InvocationTargetException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author visti
 */
public class SimulateGame implements IGameLogic{
    private Winner winner;
    
    public Winner play(String player1, String player2) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        IGameLogic dummyLogic = null;
        IGameLogic ourLogic = null;
        int cols = 7;
        int rows = 6;

        int moves = 6;

        dummyLogic = parseGameLogicParam(player1);
        ourLogic = parseGameLogicParam(player2);
        int move = 0;

        dummyLogic.initializeGame(cols, rows, 1);
        ourLogic.initializeGame(cols, rows, 2);

        System.out.println("******* NEW GAME *******");
        System.out.println("************************");

        for (int i = 0; i < moves; i++) {
            int col = -1;
            System.out.println("TURN:" + (i + 1));

            // Player one:
            col = dummyLogic.decideNextMove();
            System.out.println("Returned: " + col);
            System.out.println();

            ourLogic.insertCoin(col, 1);
            dummyLogic.insertCoin(col, 1);

            winner = dummyLogic.gameFinished();
            System.out.println("Returned:" + winner);
            System.out.println();
            if (winner != winner.NOT_FINISHED) {
                return winner;
            }
            // Player 2
            col = ourLogic.decideNextMove();
            System.out.println("Returned: " + col);
            System.out.println();

            dummyLogic.insertCoin(col, 2);
            ourLogic.insertCoin(col, 2);

            winner = ourLogic.gameFinished();
            System.out.println("Returned: " + winner);
            System.out.println();
            if (winner != winner.NOT_FINISHED) {
                return winner;
            }
        }

        System.out.println("******* END GAME *******");
        System.out.println("************************");
        return winner.NOT_FINISHED;
    }

    public static IGameLogic parseGameLogicParam(String cmdParam)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        IGameLogic retGL = null;
        retGL = (IGameLogic) Class.forName(cmdParam).getConstructor().newInstance();

        return retGL;
    }

    public void initializeGame(int columns, int rows, int player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertCoin(int column, int playerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int decideNextMove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Winner gameFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
