
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
        
    }

    public void insertCoin(int column, int playerID) {
    	heuristics.setCurrentState(column, playerID);
    }

    public int decideNextMove() {
    	
    	return heuristics.evaluateGameState();
        
        
    }

    public Winner gameFinished() {
    	return heuristics.getWinner();
    }

    public int getOpponentPlayerID(){
        if (playerID == 1){
            return 2;
        }
        return 1;
    }
   
	
	
	
}