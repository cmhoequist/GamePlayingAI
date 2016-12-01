package ai.main.ai.view;

import moritz.current.Utility;

import javax.swing.*;
import java.util.stream.IntStream;

/**
 * Created by Moritz on 11/29/2016.
 * <p></p>
 */
public class ToeUI extends GamePanel {
    private int size = 3;
    private JButton[] cells = new JButton[3*3];

    public ToeUI(){
        for(int i =0 ;i < cells.length; i++){
            cells[i] = new JButton();
        }
        reset();
    }

    @Override
    public void setMove(int cellIndex, int polarity) {
        //MSB-LSB conversion
        int bitIndex = size-1-cellIndex;
        long newMove = Utility.getBinaryPattern(bitIndex);
        String cellText = "X";
        if(polarity==1){
            posMoves |= newMove;
        }
        else{
            negMoves |= newMove;
            cellText = "O";
        }
        cells[cellIndex].setText(cellText);
        cells[cellIndex].setEnabled(false);
    }

    @Override
    public void reset() {
        gameOver = false;
        posMoves = 0;
        negMoves = 0;
        for (int i = 0; i < size*size; i++) {
            cells[i].setText("");
            cells[i].setEnabled(true);
            cells[i].setOpaque(true);
        }
    }

    @Override
    public boolean isOver(long posMoves, long negMoves){
        int[] winShifts = new int[]{1, //horizontal
                                    3, //vertical
                                    4, //diagonal \
                                    2 //diagonal /
        };

        long winString;
        long bits = turn == 1 ? posMoves : negMoves;
        for(int shift : winShifts){
            winString = bits & (bits >> shift);
            if ((winString & (winString >> shift)) != 0) {
                gameOver = true;
            }
        }

        return gameOver;
    }

    @Override
    public void endGame() {
        IntStream.range(0, size).forEach(i -> cells[i].setEnabled(false));
    }

    @Override
    public long getAvailableMoves() {
        return posMoves | negMoves;
    }
}
