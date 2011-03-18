/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author visti
 */
public class MainLogicNew implements IGameLogic {

    private int columns = 0;
    private int rows = 0;
    private int playerID;
    private GameState currentGameState;
    private RegexEvaluation threeInARow = new RegexEvaluation("3inarow.xml");
    private RegexEvaluation killermoves = new RegexEvaluation("killermoves.xml");
    private RegexEvaluation fourInARow = new RegexEvaluation("winnermoves.xml");


    public void initializeGame(int columns, int rows, int playerID) {
        this.columns = columns;
        this.rows = rows;
        this.playerID = playerID;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertCoin(int column, int playerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int decideNextMove() {
        threeInARow.match(currentGameState.toString(), playerID);

        threeInARow.match(currentGameState.toString(), getOpponentPlayerID());

        killermoves.match(currentGameState.toString(), playerID);

        killermoves.match(currentGameState.toString(), getOpponentPlayerID());

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Winner gameFinished() {
        // change toString to parser
        fourInARow.match(currentGameState.toString(), playerID);
        fourInARow.match(currentGameState.toString(), getOpponentPlayerID());
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getOpponentPlayerID(){
        if (playerID == 1){
            return 2;
        }
        return 1;
    }
}
