package ai.main;

import moritz.current.Parser;

/**
 * Created by Moritz on 11/28/2016.
 * <p></p>
 */
public class GeneticPlayer implements AIPlayer {
    private int depth = 0;
    private int ultimateChoice = -1;

    @Override
    public int[] move(int[][] tileMatrix, int turnPolarity) {
//        negamax(turnPolarity, 2);
        Parser scorer = new Parser();
        scorer.setOpponentState(1);
        scorer.setPlayerState(1);
        scorer.setWinPattern(1);
        return new int[0];
    }
//
//    public void setMove(Game game){
//        Parser scorer = new Parser();
//        int polarity = game.getTurn();
//        int playerMap = game.getPlayerMap();
//        int oppMap = game.getOppMap();
//
//    }
//
//    private int getBinaryIndex(int index){
//        return new Double(Math.pow(2, index)).intValue();
//    }
//
//    private int score(){
//        int totalScore = 0;
//        for(int pattern : winningPatterns){
//            scorer.setWinPattern(pattern);
//            if(currentPolarity == 1){
//                scorer.setPlayerState(xMoves);
//                scorer.setOpponentState(oMoves);
//            }
//            else{
//                scorer.setPlayerState(oMoves);
//                scorer.setOpponentState(xMoves);
//            }
//            totalScore += scorer.score(currentPolarity == 1 ? alg1 : alg2);
//        }
//        return totalScore;
//    }
//
//    private int negamax(int polarity, int depth){
//        this.depth += 1;
//
//        if(isOver() || depth==0){
//            this.depth -= 1;
//            return score();
//        }
//
//        int max = Integer.MIN_VALUE;
//        int depthChoice = -1;
//
//        //Get board configuration
//        int occupied = xMoves | oMoves;
//        //For all potential moves
//        for(int i = 0; i < 9; i++){
//            //If the cell is empty (the move is valid)
//            if((occupied >> i & 1) != 1){
//                //We iterate from LSB to MSB, but the grid indices are mapped from MSB to LSB: must reverse
//                int index = 8 - i;
//                int thisBit = getBinaryIndex(i);
//
//                //Make a move to create a transient board state (the next recursive level)
//                setMove(thisBit, polarity);
//
//                //Evaluate the move
//                int outcome = -negamax(-1*polarity, depth-1);
//                if(outcome > max){
//                    max = outcome;
//                    depthChoice = index;
//                }
//
////                if(this.depth==0 && DEBUG){
////                    System.out.println("i="+index+", outcome="+outcome);
////                }
//
//                //Unset the move - use of XOR rather than OR guarantees toggling in either direction
//                setMove(thisBit, polarity);
//            }
//        }
//        ultimateChoice = depthChoice;
//        this.depth -= 1;
//        return max;
//    }
}
