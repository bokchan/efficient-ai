/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author visti
 */
public class TestLogicTest implements IGameLogic {

    @Test
    public void testPlay1() throws Exception {
        System.out.println("play");

        SimulateGame instance = new SimulateGame();
        IGameLogic.Winner expResult = Winner.TIE;
        IGameLogic.Winner result = instance.play("MainLogicNew", "GameLogicDummy1");
        assertEquals(expResult, result);

    }

    @Test
    public void testPlay2() throws Exception {
        System.out.println("play");

        SimulateGame instance = new SimulateGame();
        IGameLogic.Winner expResult = Winner.TIE;
        IGameLogic.Winner result = instance.play("MainLogicNew", "GameLogicDummy1");
        assertEquals(expResult, result);

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
