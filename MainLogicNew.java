
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author visti
 */
public class MainLogicNew implements IGameLogic {
	
    private int cols = 0;
    private int rows = 0;
    private int playerID;
    private int opponent;
    
    private GameHeuristics heuristics;
    
    public void initializeGame(int columns, int rows, int playerID) {
    	heuristics = new GameHeuristics(columns, rows, playerID);
        this.cols = columns;
        this.rows = rows;
        this.playerID = playerID;
        this.opponent = getOpponentPlayerID();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertCoin(int column, int playerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int decideNextMove() {
        // TODO: Call heuristics 
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Winner gameFinished() {
        // change toString to parser
        // TODO: CALL heurictics 
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getOpponentPlayerID(){
        if (playerID == 1){
            return 2;
        }
        return 1;
    }
   
	
	
	
}