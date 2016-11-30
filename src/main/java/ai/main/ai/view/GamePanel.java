package ai.main.ai.view;

import ai.main.AIPlayer;
import ai.main.ai.game.Heuristic;
import moritz.current.Utility;

import javax.swing.*;
import java.util.List;

/**
 * Created by Moritz on 11/29/2016.
 * <p></p>
 */
public abstract class GamePanel extends JPanel {
    protected long posMoves = 0;
    protected long negMoves = 0;
    protected int turn = 0;
    protected boolean gameOver = false;
    protected int size = 0;
    private int currentDepth = -1;

    /**
     * Sets the given cell to the given polarity. Assumes indexing begins at zero in origin (top-left corner).
     * @param cellIndex
     * @param polarity
     */
    public abstract void setMove(int cellIndex, int polarity);

    public long getPosMoves(){
        return posMoves;
    }

    public long getNegMoves(){
        return negMoves;
    }

    public int getTurn(){
        return turn;
    }

    /**
     * Reset to new game state.
     */
    public abstract void reset();

    public abstract boolean isOver(long posMoves, long negMoves);

    public boolean isOver(){
        return isOver(posMoves, negMoves);
    }

    public abstract void endGame();

    public abstract long getAvailableMoves();

    public int boardSize(){
        return size;
    }

    public int accept(Heuristic fn){
//        for()
        return -1;
    }
}
