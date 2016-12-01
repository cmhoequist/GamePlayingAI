package ai.main.ai.view;

import ai.main.AIPlayer;
import ai.main.GeneticPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Moritz on 11/30/2016.
 * <p></p>
 */
public class Controller extends JFrame {

    private AIPlayer ai = new GeneticPlayer();

    private int mode;
    private int moveCount = 0;

    //Top-level containers
    private HeaderPanel headerPanel = new HeaderPanel();
    private GamePanel gamePanel;
    private FooterPanel footerPanel = new FooterPanel();

    public Controller(String title, GamePanel game){
        this.gamePanel = game;

        //Populate
        //Add sections / JPanels to main pane
        add(headerPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        //Add listeners to buttons
        footerPanel.getPvpButton().addActionListener(e -> mode = 0);
        footerPanel.getAiButton().addActionListener(e -> mode = 1);
        footerPanel.getQuitButton().addActionListener(e -> System.exit(0)); // End addActionListener
        footerPanel.getNewGameButton().addActionListener(e -> newGame()); // End addActionListener

        //Finish frame
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void newGame(){
        moveCount = 0;
        headerPanel.setHeader("Player 1 - X");
        gamePanel.reset();
    }

    public GamePanel getGame(){
        return gamePanel;
    }

    public void setMove(int m){
        gamePanel.setMove(m, gamePanel.getTurn());
    }
}
