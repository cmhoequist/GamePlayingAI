package moritz.current;

import ai.main.C4UI;
import ai.main.T3UI;
import ai.main.ai.view.CoUI;

import java.util.List;
import java.util.Stack;

/**
 * Created by Moritz on 11/30/2016.
 * <p></p>
 */
public class TC4 {
    //Teaching
    private boolean display;
    private C4UI c;

    //Debugging
    int depth = -1;
    private boolean DEBUG = false;

    //Scoring
    private int maxLineBits;
    private int idealScore;
    long posMoves = 0;
    long negMoves = 0;
    int winner = 0;
    int ultimateChoice = -1;
    int currentPolarity = 1;
    Stack<Integer> alg1, alg2;

    public TC4(boolean doIt) {
        this();
        display = doIt;
        if(display){
            c = new C4UI("Connect Four", 6, 7);
        }
    }

    public TC4(){

    }

    private void clearState(){
        winner = 0;
        currentPolarity = 1;
        posMoves = 0;
        negMoves = 0;
        ultimateChoice = -1;
        depth = -1;
    }

    private boolean isOver(){
        return c.isOver();
    }

    private int getBinaryIndex(int i ){
        return -1;
    }



    Parser scorer = new Parser();
    private int score(){
        int totalScore = 0;
        int winModes = 3;
        for(int i = 0; i < winModes; i++){
            if(currentPolarity == 1){
                scorer.setPlayerState(posMoves);
                scorer.setOpponentState(negMoves);
            }
            else{
                scorer.setPlayerState(posMoves);
                scorer.setOpponentState(negMoves);
            }
            totalScore += scorer.score(currentPolarity == 1 ? alg1 : alg2);
        }
        return totalScore;
    }



    public int teach(Stack<Integer> alg1, Stack<Integer> alg2){
        clearState();
        this.alg1 = alg1;
        this.alg2 = alg2;

        if(DEBUG){
            System.out.println("Init alg1: "+alg1);
            System.out.println("Init alg2: "+alg2);
        }

        while(!c.isOver()){
            negamax(currentPolarity, 2);
            System.out.println("Currpat: "+Long.toBinaryString(posMoves | negMoves));
            System.out.println("nextmove: "+Long.toBinaryString(new Double(Math.pow(2.0,ultimateChoice)).longValue()));
            setMove(ultimateChoice, currentPolarity);
            if(DEBUG){
//                System.out.println(currentPolarity +" moves to "+ ultimateChoice);
//                System.out.println(currentPolarity+": "+ Integer.toBinaryString(currentPolarity==1 ? xMoves : oMoves));
            }
            if(display){
                System.out.println("Next move: "+ultimateChoice);
                System.out.println(Long.toBinaryString(posMoves));
                System.out.println(Long.toBinaryString(negMoves));
                try {
                    Thread.sleep(180);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c.setMove(48 - ultimateChoice, currentPolarity);
            }

            currentPolarity *= -1;
        }

        //Reset for winner determination
        winner = 0;
        isOver();
        if(display){
            try {
                Thread.sleep(180);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            c.getResetButton().doClick();
        }


        currentPolarity *= -1;
        if(winner == 0){
            return 1;
        }
        return currentPolarity == 1 ? 2 : 0;
    }

    private void setMove(long index, int polarity){
        long thisBit = new Double(Math.pow(2.0,index)).longValue();
        if(polarity == 1){
            posMoves ^= thisBit;
        }
        else{
            negMoves ^= thisBit;
        }
    }

    private int negamax(int polarity, int currentDepth){
        this.depth += 1;

        if(isOver() || currentDepth==0){
            this.depth -= 1;
            return score();
        }

        int max = Integer.MIN_VALUE;
        int depthChoice = -1;

        //Get board configuration
        long occupied = posMoves | negMoves;
        //For all potential moves
        List<Integer> valid = c.getValidMoves();
        for(int i : valid){
            //If the cell is empty (the move is valid)
            if((occupied >> i & 1) != 1) {
                //We iterate from LSB to MSB, but the grid indices are mapped from MSB to LSB: must reverse
                int index = 48 - i;
                int thisBit = getBinaryIndex(i);

                //Make a move to create a transient board state (the next recursive level)
                setMove(index, polarity);

                //Evaluate the move
                int outcome = -negamax(-1 * polarity, currentDepth - 1);
                if (outcome > max) {
                    max = outcome;
                    depthChoice = index;
                }

                //Unset the move - use of XOR rather than OR guarantees toggling in either direction
                setMove(index, polarity);
            }
        }

        ultimateChoice = depthChoice;
        this.depth -= 1;
        return max;
    }
}
