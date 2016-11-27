package moritz.core;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class TeachTacToe {
    private int winner = 0;
    ParseRPN scorer = new ParseRPN();
    Stack<Integer> alg1, alg2;
    public Stack<Integer> teach(Stack<Integer> alg1, Stack<Integer> alg2){
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
                System.out.println(currentPolarity +" moves to "+ ultimateChoice);
                System.out.println(currentPolarity+": "+ Integer.toBinaryString(currentPolarity==1 ? xMoves : oMoves));
            }

            currentPolarity *= -1;
        }

        //Reset for winner determination
        winner = 0;
        isOver();

        currentPolarity *= -1;
        if(winner == 0){
            return null;
        }
        return currentPolarity == 1 ? alg1 : alg2;
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
    private final boolean DEBUG = false;

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

                if(this.depth==0 && DEBUG){
                    System.out.println("i="+index+", outcome="+outcome);
                }

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
        ParseRPN parser = new ParseRPN();
        for(int pattern : winningPatterns){
            scorer.setWinPattern(pattern);
            if(currentPolarity == 1){
                scorer.setPlayerState(xMoves);
                scorer.setOpponentState(oMoves);
            }
            else{
                scorer.setPlayerState(oMoves);
                scorer.setOpponentState(xMoves);
            }
//            System.out.println("Example1: "+alg1+", "+parser.score(alg1));
//            System.out.println("Example2: "+alg2+", "+parser.score(alg2));
            totalScore += scorer.score(currentPolarity == 1 ? alg1 : alg2);
        }
        return totalScore;
    }

    private boolean isOver(){
        //If there is a winner, the game is over
        for(int pattern : winningPatterns){
            if((pattern & xMoves)==pattern){
                if(depth<= 0 && DEBUG){
                    System.out.println("WINNER1");
                }
                winner = 1;
                return true;
            }
            else if((pattern & oMoves)==pattern){
                if(depth<= 0 && DEBUG){
                    System.out.println("WINNER-1");
                }
                winner = -1;
                return true;
            }
        }
        //If all cells are occupied, the game is over
        return (xMoves | oMoves) == 0b111111111;
    }
}
