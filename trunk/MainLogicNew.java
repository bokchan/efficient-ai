
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
    
    public MainLogicNew() {
    	
    }
    
    public void initializeGame(int columns, int rows, int playerID) {
    	heuristics = new GameHeuristics(columns, rows, playerID);
        this.cols = columns;
        this.rows = rows;
        this.playerID = playerID;
        this.opponent = getOpponentPlayerID();
        
    }

    public void insertCoin(int column, int playerID) {
    	heuristics.updateCurrentState(column, playerID);
    }

    public int decideNextMove() {
    	GameHelper.Trace("Deciding move");
    	long t1 = System.nanoTime();
    	int move = heuristics.evaluateGameState(); 
    	GameHelper.Trace("Move: "  + move);
    	GameHelper.Trace("Time: "  + (System.nanoTime()-t1)/100000);
    	return move; 
    }

    public Winner gameFinished() {
    	Winner w = heuristics.getWinner();
    	GameHelper.Trace("Game status: " + w);
    	return w;
    }

    public int getOpponentPlayerID(){
        if (playerID == 1){
            return 2;
        }
        return 1;
    }
   
	
	
	
}