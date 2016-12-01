package moritz.current;

import ai.main.T3UI;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class TeachTacToe {
    private boolean display = false;
    private int winner = 0;
    Parser scorer = new Parser();
    Stack<Integer> alg1, alg2;
    private T3UI t;

    public TeachTacToe(boolean doIt){
        this();
        display = doIt;
        if(display){
            t = new T3UI("Tic Tac Toe");
        }
    }

    /**
     * Returns 2 if alg1 won, 1 if tie, and 0 if alg1 lost.
     * @param alg1
     * @param alg2
     * @return
     */
    public int teach(Stack<Integer> alg1, Stack<Integer> alg2){

        clearState();
        this.alg1 = alg1;
        this.alg2 = alg2;

        if(DEBUG){
            System.out.println("Init alg1: "+alg1);
            System.out.println("Init alg2: "+alg2);
        }

        while(!isOver()){
            negamax(currentPolarity, 2);
            setMove(getBinaryIndex(8- ultimateChoice), currentPolarity);
            if(DEBUG){
//                System.out.println(currentPolarity +" moves to "+ ultimateChoice);
//                System.out.println(currentPolarity+": "+ Integer.toBinaryString(currentPolarity==1 ? xMoves : oMoves));
            }
            if(display){
                t.setMove(ultimateChoice, currentPolarity);
            }


            currentPolarity *= -1;
        }

        //Reset for winner determination
        winner = 0;
        isOver();
        if(display){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t.getResetButton().doClick();
        }


        currentPolarity *= -1;
        if(winner == 0){
            return 1;
        }
        return currentPolarity == 1 ? 2 : 0;
    }

    public void setDebug(boolean newval){
        DEBUG = newval;
    }

    private int[] patterns = {
            0b111000000, 0b000111000, 0b000000111, // rows
            0b100100100, 0b010010010, 0b001001001, // cols
            0b100010001, 0b001010100               // diagonals
    };
    private Set<Integer> winningPatterns = Arrays.stream(patterns).boxed().collect(Collectors.toSet());
    private int currentPolarity = 1; //1 == x, -1 == o
    int xMoves = 0b000000000;
    int oMoves = 0b000000000;
    int ultimateChoice = -1;

    //Debugging
    int depth = -1;
    private boolean DEBUG = false;

    //Scoring
    private int maxLineBits;
    private int idealScore;

    public TeachTacToe(){
        //Initialize maximum line score and maximum overall score
        maxLineBits = 3;
        idealScore = maxLineBits*winningPatterns.size();
    }

    private void clearState(){
        winner = 0;
        currentPolarity = 1;
        xMoves = 0b000000000;
        oMoves = 0b000000000;
        ultimateChoice = -1;
        depth = -1;
    }

    private int negamax(int polarity, int depth){
        this.depth += 1;

        if(isOver() || depth==0){
            this.depth -= 1;
            return score();
        }

        int max = Integer.MIN_VALUE;
        int depthChoice = -1;

        //Get board configuration
        int occupied = xMoves | oMoves;
        //For all potential moves
        for(int i = 0; i < 9; i++){
            //If the cell is empty (the move is valid)
            if((occupied >> i & 1) != 1){
                //We iterate from LSB to MSB, but the grid indices are mapped from MSB to LSB: must reverse
                int index = 8 - i;
                int thisBit = getBinaryIndex(i);

                //Make a move to create a transient board state (the next recursive level)
                setMove(thisBit, polarity);

                //Evaluate the move
                int outcome = -negamax(-1*polarity, depth-1);
                if(outcome > max){
                    max = outcome;
                    depthChoice = index;
                }

//                if(this.depth==0 && DEBUG){
//                    System.out.println("i="+index+", outcome="+outcome);
//                }

                //Unset the move - use of XOR rather than OR guarantees toggling in either direction
                setMove(thisBit, polarity);
            }
        }
        ultimateChoice = depthChoice;
        this.depth -= 1;
        return max;
    }

    private void setMove(int thisBit, int polarity){
        if(polarity == 1){
            xMoves ^= thisBit;
        }
        else{
            oMoves ^= thisBit;
        }
    }

    private int getBinaryIndex(int index){
        return new Double(Math.pow(2, index)).intValue();
    }

    private int score(){
        int totalScore = 0;
        for(int pattern : winningPatterns){
//            scorer.setWinPattern(pattern);
            if(currentPolarity == 1){
                scorer.setPlayerState(xMoves);
                scorer.setOpponentState(oMoves);
            }
            else{
                scorer.setPlayerState(oMoves);
                scorer.setOpponentState(xMoves);
            }
            totalScore += scorer.score(currentPolarity == 1 ? alg1 : alg2);
        }
        return totalScore;
    }

    private boolean isOver(){
        //If there is a winner, the game is over
        for(int pattern : winningPatterns){
            if((pattern & xMoves)==pattern){
//                if(depth<= 0 && DEBUG){
//                    System.out.println("WINNER1");
//                }
                winner = 1;
                return true;
            }
            else if((pattern & oMoves)==pattern){
//                if(depth<= 0 && DEBUG){
//                    System.out.println("WINNER-1");
//                }
                winner = -1;
                return true;
            }
        }
        //If all cells are occupied, the game is over
        return (xMoves | oMoves) == 0b111111111;
    }
}
