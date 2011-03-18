public class GameLogicDummy1 implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private String name = "Dummy1";
    int[] dummymoves = new int[]{1, 2, 3, 4, 5, 6};
    int turn = 0;
    
    public GameLogicDummy1() {
        //TODO Write your implementation for this method
    }
	
    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        System.out.println(this.name + ".<initializeGame>("+x+"," + y + "," + playerID + ")");
        //TODO Write your implementation for this method
    }
	
    public Winner gameFinished() {
        if (turn >= dummymoves.length) return Winner.TIE;
        System.out.println(this.name + ".<gameFinised()>");
        //TODO Write your implementation for this method
        return Winner.NOT_FINISHED;
    }


    public void insertCoin(int column, int playerID) {
        //TODO Write your implementation for this method
        System.out.println(this.name + ".<insertCoin>(" +  column + "," + playerID+ ")");
    }

    public int decideNextMove() {
        int move = dummymoves[turn];
        turn++;
        System.out.println(this.name + " inserted a coin in " + move);
        return move;
    }

}
