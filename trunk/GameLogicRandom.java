import java.util.Random;

public class GameLogicRandom implements IGameLogic {

    private int x = 0;
    private int y = 0;
    private int playerID;

    public GameLogicRandom() {
        //TODO Write your implementation for this method
    }

    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        //TODO Write your implementation for this method
    }

    public Winner gameFinished() {
        //TODO Write your implementation for this method
        return Winner.NOT_FINISHED;
    }

    public void insertCoin(int column, int playerID) {
        //TODO Write your implementation for this method	
    }

    public int decideNextMove() {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(7);
        System.out.println(randomInt);
        return randomInt;

    }
}
