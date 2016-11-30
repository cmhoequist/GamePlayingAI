package ai.main;

import ai.main.ai.view.GamePanel;

/**
 * Created by Moritz on 11/28/2016.
 * <p></p>
 */
public abstract class AIPlayer {
    public abstract int[] move(int[][] tileMatrix, int turnPolarity);

    public int score(GamePanel game){
//        return game.accept(this);
        return -1;
    }
}
