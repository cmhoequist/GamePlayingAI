package moritz.closed;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Moritz on 11/25/2016.
 * <p></p>
 */
public class Negamax {
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
    private final boolean DEBUG = true;

    //Scoring
    private int maxLineBits;
    private int idealScore;

    public Negamax(){
        //Initialize maximum line score and maximum overall score
        maxLineBits = 3;
        idealScore = maxLineBits*winningPatterns.size();
    }

    public int getMove(){
        Scanner scanner = new Scanner(System.in);
        //While isOver is 1, the game continues.
        while(!isOver()){
            if(currentPolarity == 1){
                System.out.println("Enter a cell index: ");
                ultimateChoice = scanner.nextInt();
            }
            else{
                negamax(-1, 2);
            }
            System.out.println(currentPolarity +" moves to "+ ultimateChoice);
            setMove(getBinaryIndex(8- ultimateChoice), currentPolarity);
            System.out.println("x(1): " + Integer.toBinaryString(xMoves));
            System.out.println("o(-1): " + Integer.toBinaryString(oMoves));
            currentPolarity *= -1;
        }
        return ultimateChoice;
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
        //Heuristic implementation
        int player = currentPolarity == 1 ? xMoves : oMoves;
        int opponent = player == xMoves ? oMoves : xMoves;
        int totalScore = 0;

        for(int pattern : winningPatterns){
            int playerBits = pattern & player;
            int opponentBits = pattern & opponent;
            int bitDifference = Integer.bitCount(playerBits) - Integer.bitCount(opponentBits);
            int score = bitDifference*new Double(Math.pow(10, Math.abs(bitDifference))).intValue();
            totalScore += score;
        }
        return totalScore;
    }

    private boolean isOver(){
        //If there is a winner, the game is over
        for(int pattern : winningPatterns){
            if((pattern & xMoves)==pattern || (pattern & oMoves)==pattern){
                return true;
            }
        }
        //If all cells are occupied, the game is over
        return (xMoves | oMoves) == 0b111111111;
    }
}
