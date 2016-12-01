package ai.main;

import ai.main.ai.view.FooterPanel;
import ai.main.ai.view.HeaderPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

//T3UI inherits JFrame
public class T3UI extends JFrame {

    //serialVersionUID
    private static final long serialVersionUID = 1L;
    //windowLayout
    private FlowLayout windowLayout = new FlowLayout();

    // Tiles as buttons
    private JButton[][] tiles;

    // Matrix
    final private int[][] tileMatrix;

    // turn = 0 - Player 1
    // turn = 1 - PLayer 2 or AI
    private int turn = 0;

    // When moves are 9, game over
    private int moves = 0;

    // mode = 0 - Player vs. Player
    // mode = 1 - PLayer vs. Computer (AI)
    private int mode = 0;

    // Player win counts
    private long xWin = 0;
    private long oWin = 0;

    // AI Player
    final private AIPlayer aiPlayer;

    // Timer - for AI "thinking"
    private Timer timer;
    // Timer duration
    private int duration;

    //Top level components
    HeaderPanel headerPanel = new HeaderPanel();
    FooterPanel footerPanel = new FooterPanel();


    // Constructor for T3UI
    public T3UI(String name) {
        //Inherits name from JFrame
        setTitle(name);
        aiPlayer = new GeneticPlayer();

        tileMatrix = new int[3][3];
        timer = new Timer(500, timerAction);

        //Exit on close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Setup list of images for frame's icons
        final ArrayList<Image> icons = new ArrayList<Image>();
        icons.add(new ImageIcon("Images/icon1616.png").getImage());
        icons.add(new ImageIcon("Images/icon3232.png").getImage());
        icons.add(new ImageIcon("Images/icon128128.png").getImage());

        //Add Icons to frame
        setIconImages(icons);
        //Set location by platform
        setLocationByPlatform(true);
        //Set up the content pane.
        addComponentsToPane(getContentPane());
        //Display the window.
        pack();

        //Show the T3UI
        setSize(350, 350);
        setVisible(true);
        setResizable(false);
    }

    //Add components to Container pane
    private void addComponentsToPane(final Container pane) {


//----------------------JPanels----------------------------------
        final JPanel game = new JPanel(new GridLayout(3, 3));

//----------------------JButtons--------------------------------------------------
        tiles = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                tiles[i][j] = new JButton();
                tiles[i][j].setText("");
                tiles[i][j].setVisible(true);
                tiles[i][j].setFocusPainted(false);
                tiles[i][j].setBackground(Color.WHITE);
                tiles[i][j].setFont(new Font("Arial", Font.PLAIN, 40));

                game.add(tiles[i][j]);
                final int finalI = i;
                final int finalJ = j;
                tiles[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(mode == 1 && turn == 1)
                        {
                            return;
                        }
                        tiles[finalI][finalJ].setText(turn == 0 ? "X" : "O");
                        tiles[finalI][finalJ].setEnabled(false);
                        tileMatrix[finalI][finalJ] = (turn == 0 ? 1 : -1);
                        if (moves != 10) {
                            moves += 1;
                        }
                        checkGame();
                    } // End actionPerformed
                }); // End addActionListener
            }
        }

//--------------Add components to JPanels-----------------------

        //Add sections / JPanels to main pane
        pane.add(headerPanel, BorderLayout.NORTH);
        pane.add(game, BorderLayout.CENTER);
        pane.add(footerPanel, BorderLayout.SOUTH);

        //Add listeners to buttons
        footerPanel.getPvpButton().addActionListener(e -> mode = 0);
        footerPanel.getAiButton().addActionListener(e -> mode = 1);
        footerPanel.getQuitButton().addActionListener(e -> System.exit(0)); // End addActionListener
        footerPanel.getNewGameButton().addActionListener(e -> newGame()); // End addActionListener
    }

    public JButton getResetButton(){
        return footerPanel.getNewGameButton();
    }

    private void newGame() {
        moves = 0;
        if (turn == 0) {
            headerPanel.setHeader("Player 1 - X");
        } else {
            headerPanel.setHeader("Player 2 - O");
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                tileMatrix[i][j] = 0;
                tiles[i][j].setText("");
                tiles[i][j].setEnabled(true);
                tiles[i][j].setBackground(Color.WHITE);
                tiles[i][j].setForeground(Color.BLACK);
                tiles[i][j].setOpaque(true);
            }
        }

        if(mode == 1 && turn == 1)
        {
            aiMove();
        }
    }

    private void checkGame() {
        boolean gameOver = false;

        // 1st row
        if ((tileMatrix[0][0] + tileMatrix[0][1] + tileMatrix[0][2]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 0, 0}, new int[]{0, 1, 2});
        } else if ((tileMatrix[0][0] + tileMatrix[0][1] + tileMatrix[0][2]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 0, 0}, new int[]{0, 1, 2});
        }

        // 2nd row
        else if ((tileMatrix[1][0] + tileMatrix[1][1] + tileMatrix[1][2]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{1, 1, 1}, new int[]{0, 1, 2});
        } else if ((tileMatrix[1][0] + tileMatrix[1][1] + tileMatrix[1][2]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{1, 1, 1}, new int[]{0, 1, 2});
        }

        // 3rd row
        else if ((tileMatrix[2][0] + tileMatrix[2][1] + tileMatrix[2][2]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{2, 2, 2}, new int[]{0, 1, 2});
        } else if ((tileMatrix[2][0] + tileMatrix[2][1] + tileMatrix[2][2]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{2, 2, 2}, new int[]{0, 1, 2});
        }

        // 1st column
        else if ((tileMatrix[0][0] + tileMatrix[1][0] + tileMatrix[2][0]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 0, 0});
        } else if ((tileMatrix[0][0] + tileMatrix[1][0] + tileMatrix[2][0]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 0, 0});
        }

        // 2nd column
        else if ((tileMatrix[0][1] + tileMatrix[1][1] + tileMatrix[2][1]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{1, 1, 1});
        } else if ((tileMatrix[0][1] + tileMatrix[1][1] + tileMatrix[2][1]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{1, 1, 1});
        }

        // 3rd column
        else if ((tileMatrix[0][2] + tileMatrix[1][2] + tileMatrix[2][2]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 2, 2});
        } else if ((tileMatrix[0][2] + tileMatrix[1][2] + tileMatrix[2][2]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 2, 2});
        }

        // 1st cross
        else if ((tileMatrix[0][0] + tileMatrix[1][1] + tileMatrix[2][2]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 1, 2});
        } else if ((tileMatrix[0][0] + tileMatrix[1][1] + tileMatrix[2][2]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 1, 2});
        }

        // 2nd cross
        else if ((tileMatrix[0][2] + tileMatrix[1][1] + tileMatrix[2][0]) == -3) {
            gameOver = true;
            headerPanel.setHeader("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 1, 0});
        } else if ((tileMatrix[0][2] + tileMatrix[1][1] + tileMatrix[2][0]) == 3) {
            gameOver = true;
            headerPanel.setHeader("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 1, 0});

        }

        // All moves and no wins
        if (moves >= 9 && !gameOver) {
            gameOver = true;
            headerPanel.setHeader("Draw!");
        }

        // Game Over so disable all button tiles
        if (gameOver) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {

                    tiles[i][j].setEnabled(false);
                }
            }
        } else {
            turn = (turn == 0 ? 1 : 0);

            if (turn == 0) {
                headerPanel.setHeader("Player 1 - X");
            } else {
                headerPanel.setHeader("Player 2 - O");
            }

            if(mode == 1 && turn == 1)
            {
                aiMove();
            }
        }
    }

    //For hacky use with TTT
    public void setMove(int num, int polarity){
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int row = num/3;
        int col = num%3;

        tiles[row][col].setText(polarity == 1 ? "X" : "O");
        tiles[row][col].setEnabled(false);
        tileMatrix[row][col] = (polarity == 1 ? 1 : -1);
        if (moves != 10) {
            moves += 1;
        }
        checkGame();
    }

    private void aiMove()
    {
        headerPanel.setHeader("AI thinking...");
        duration = 1;
        timer.start();
        timer.setInitialDelay(0);
    }

    private ActionListener timerAction = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(duration == 0)
            {
                timer.stop();
                int[] aiMove = aiPlayer.move(tileMatrix, turn);
                tiles[aiMove[0]][aiMove[1]].setText(turn == 0 ? "X" : "O");
                tiles[aiMove[0]][aiMove[1]].setEnabled(false);
                tileMatrix[aiMove[0]][aiMove[1]] = (turn == 0 ? 1 : -1);
                if (moves != 10) {
                    moves += 1;
                }
                checkGame();
            }
            else
            {
                duration--;
            }
        }
    };


    private void colorWinningTiles(int[] tileX, int[] tileY) {
        for (int i = 0; i < 3; i++) {
            tiles[tileX[i]][tileY[i]].setBackground(Color.CYAN);
            tiles[tileX[i]][tileY[i]].setOpaque(true);

        }
    }

    private void addXWinCount() {
        xWin += 1;
        headerPanel.setXScore("X : " + xWin);
    }

    private void addOWinCount() {
        oWin += 1;
        headerPanel.setOScore("     O : " + oWin);
    }
} // End T3UI
