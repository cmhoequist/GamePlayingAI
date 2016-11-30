package ai.main;

import ai.main.ai.view.GamePanel;
import moritz.current.Parser;
import moritz.current.Utility;

/**
 * Created by Moritz on 11/28/2016.
 * <p></p>
 */
public class GeneticPlayer extends AIPlayer {
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
//    private int getBinaryPattern(int index){
//        return new Double(Math.pow(2, index)).intValue();
//    }
//
//    public int score(GamePanel game){
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

    long posMoves = 0;
    long negMoves = 0;
    long movesToEvaluate = 0;
    GamePanel game;

    /**
     * Toggles bit rather than simple set.
     * @param thisBit
     * @param polarity
     */
    private void setMove(long thisBit, int polarity){
        if(polarity == 1){
            posMoves ^= thisBit;
        }
        else{
            negMoves ^= thisBit;
        }
    }

    private int getMove(GamePanel game){
        this.game = game;
        posMoves = game.getPosMoves();
        negMoves = game.getNegMoves();
        movesToEvaluate = game.getAvailableMoves();
        return negamax(game.getTurn(), 2);
    }
    private int negamax(int polarity, int maxDepth){
        this.depth += 1;

        if(game.isOver(posMoves, negMoves) || maxDepth==0){
            this.depth -= 1;
//            return score();
        }

        int max = Integer.MIN_VALUE;
        int depthChoice = -1;

        long move;
        for(int i = 0; i < Long.SIZE; i++){
            //Get the bit pattern representing this move
            move = Utility.getBinaryPattern(i);
            //Check whether it's a valid move
            if((movesToEvaluate & move)==1){
                //Make a move to create a transient board state (the next recursive level)
                setMove(move, polarity);
            }

            //Evaluate the move
            int outcome = -negamax(-1*polarity, maxDepth-1);
            if(outcome > max){
                max = outcome;
                //We iterate from LSB to MSB, but the grid indices are mapped from MSB to LSB: must reverse
                depthChoice = game.boardSize()-1-i;
            }
        }

        //Set final choice and return
        ultimateChoice = depthChoice;
        this.depth -= 1;
        return max;
    }
}
