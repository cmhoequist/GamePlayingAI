package ai.main.ai.view;

import ai.main.AIPlayer;
import ai.main.GeneticPlayer;

import javax.swing.*;

/**
 * Created by Moritz on 11/30/2016.
 * <p></p>
 */
public class CoUI extends GamePanel {

    //Top level components
    HeaderPanel headerPanel = new HeaderPanel();
    FooterPanel footerPanel = new FooterPanel();

    //Buttons
    private JButton[][] cells;

    //Game state
    private int turn = 0;
    private int moves = 0;
    private int mode = 0;

    //Win Counts
    private long xWin = 0;
    private long oWin = 0;

    // AI Player
    final private AIPlayer aiPlayer;

    public CoUI(String title, int rows, int cols){
        aiPlayer = new GeneticPlayer();



    }

    @Override
    public void setMove(int cellIndex, int polarity) {

    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isOver(long posMoves, long negMoves) {
        return false;
    }

    @Override
    public void endGame() {

    }

    @Override
    public long getAvailableMoves() {
        return 0;
    }

    public JButton getResetButton(){
        return footerPanel.getNewGameButton();
    }
}
