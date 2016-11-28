package ai.main;

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

    // Labels
    private JLabel titleLBL = new JLabel("Player 1 - X");
    private JLabel xWinsLBL = new JLabel("X : 0");
    private JLabel oWinsLBL = new JLabel("     O : 0");

    // Constructor for T3UI
    private T3UI(String name) {
        //Inherits name from JFrame
        super(name);


        aiPlayer = new GeneticPlayer();


        tileMatrix = new int[3][3];
        timer = new Timer(500, timerAction);
    }

    //Add components to Container pane
    private void addComponentsToPane(final Container pane) {


//----------------------JPanels----------------------------------


        Font contactfont = new Font("SansSerif", Font.BOLD, 18);
        titleLBL.setFont(contactfont);

        final JPanel titlePanel = new JPanel();
        titlePanel.setLayout(windowLayout);
        titlePanel.add(titleLBL);

        final JPanel winsPanel = new JPanel();
        winsPanel.setLayout(windowLayout);
        winsPanel.setBorder(new TitledBorder(""));
        winsPanel.add(xWinsLBL);
        winsPanel.add(oWinsLBL);

        final JPanel game = new JPanel(new GridLayout(3, 3));

        //actions JPanel
        final JPanel actions = new JPanel();
        actions.setLayout(windowLayout);
        actions.setBorder(new TitledBorder(""));

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(titlePanel, BorderLayout.CENTER);
        innerPanel.add(winsPanel, BorderLayout.SOUTH);


//----------------------JButtons--------------------------------------------------

        JButton quitButton = new JButton("Quit");
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFocusPainted(false);
        quitButton.setFocusPainted(false);

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


//--------------------------Radio Buttons-----------------------

        JRadioButton pvpButton = new JRadioButton("P1 vs P2");
        pvpButton.setSelected(true);
        pvpButton.setFocusPainted(false);
        pvpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = 0;
            }
        });

        JRadioButton aiButton = new JRadioButton("P1 vs AI");
        pvpButton.setFocusPainted(false);
        aiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = 1;
            }
        });

        //Group the radio buttons.
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(pvpButton);
        radioGroup.add(aiButton);


//--------------Add components to JPanels-----------------------

        //Add action buttons
        actions.add(pvpButton);
        actions.add(aiButton);
        actions.add(newGameButton);
        actions.add(quitButton);


        //Add sections / JPanels to main pane
        pane.add(innerPanel, BorderLayout.NORTH);
        pane.add(game, BorderLayout.CENTER);
        pane.add(actions, BorderLayout.SOUTH);

//--------------Add ActionListeners to JButtons-----------------------

        //Quit button
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            } // End actionPerformed
        }); // End addActionListener

        //New game button
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGame();
            } // End actionPerformed
        }); // End addActionListener

    }

    private void newGame() {
        moves = 0;
        if (turn == 0) {
            titleLBL.setText("Player 1 - X");
        } else {
            titleLBL.setText("Player 2 - O");
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
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 0, 0}, new int[]{0, 1, 2});
        } else if ((tileMatrix[0][0] + tileMatrix[0][1] + tileMatrix[0][2]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 0, 0}, new int[]{0, 1, 2});
        }

        // 2nd row
        else if ((tileMatrix[1][0] + tileMatrix[1][1] + tileMatrix[1][2]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{1, 1, 1}, new int[]{0, 1, 2});
        } else if ((tileMatrix[1][0] + tileMatrix[1][1] + tileMatrix[1][2]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{1, 1, 1}, new int[]{0, 1, 2});
        }

        // 3rd row
        else if ((tileMatrix[2][0] + tileMatrix[2][1] + tileMatrix[2][2]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{2, 2, 2}, new int[]{0, 1, 2});
        } else if ((tileMatrix[2][0] + tileMatrix[2][1] + tileMatrix[2][2]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{2, 2, 2}, new int[]{0, 1, 2});
        }

        // 1st column
        else if ((tileMatrix[0][0] + tileMatrix[1][0] + tileMatrix[2][0]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 0, 0});
        } else if ((tileMatrix[0][0] + tileMatrix[1][0] + tileMatrix[2][0]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 0, 0});
        }

        // 2nd column
        else if ((tileMatrix[0][1] + tileMatrix[1][1] + tileMatrix[2][1]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{1, 1, 1});
        } else if ((tileMatrix[0][1] + tileMatrix[1][1] + tileMatrix[2][1]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{1, 1, 1});
        }

        // 3rd column
        else if ((tileMatrix[0][2] + tileMatrix[1][2] + tileMatrix[2][2]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 2, 2});
        } else if ((tileMatrix[0][2] + tileMatrix[1][2] + tileMatrix[2][2]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 2, 2});
        }

        // 1st cross
        else if ((tileMatrix[0][0] + tileMatrix[1][1] + tileMatrix[2][2]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 1, 2});
        } else if ((tileMatrix[0][0] + tileMatrix[1][1] + tileMatrix[2][2]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{0, 1, 2});
        }

        // 2nd cross
        else if ((tileMatrix[0][2] + tileMatrix[1][1] + tileMatrix[2][0]) == -3) {
            gameOver = true;
            titleLBL.setText("Player 2 - O Wins!");
            addOWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 1, 0});
        } else if ((tileMatrix[0][2] + tileMatrix[1][1] + tileMatrix[2][0]) == 3) {
            gameOver = true;
            titleLBL.setText("Player 1 - X Wins!");
            addXWinCount();
            colorWinningTiles(new int[]{0, 1, 2}, new int[]{2, 1, 0});

        }

        // All moves and no wins
        if (moves >= 9 && !gameOver) {
            gameOver = true;
            titleLBL.setText("Draw!");
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
                titleLBL.setText("Player 1 - X");
            } else {
                titleLBL.setText("Player 2 - O");
            }

            if(mode == 1 && turn == 1)
            {
                aiMove();
            }
        }
    }

    private void aiMove()
    {
        titleLBL.setText("AI thinking...");
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
                int[] aiMove = aiPlayer.move(tileMatrix);
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
        xWinsLBL.setText("X : " + xWin);
    }

    private void addOWinCount() {
        oWin += 1;
        oWinsLBL.setText("     O : " + oWin);
    }

    static void createAndShowGUI() {
        //Create and set up a new T3UI
        T3UI frame = new T3UI("Tic-Tac-Toe");

        //Exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Setup list of images for frame's icons
        final ArrayList<Image> icons = new ArrayList<Image>();
        icons.add(new ImageIcon("Images/icon1616.png").getImage());
        icons.add(new ImageIcon("Images/icon3232.png").getImage());
        icons.add(new ImageIcon("Images/icon128128.png").getImage());

        //Add Icons to frame
        frame.setIconImages(icons);
        //Set location by platform
        frame.setLocationByPlatform(true);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();

        //Show the T3UI
        frame.setSize(350, 350);
        frame.setVisible(true);
        frame.setResizable(false);
    } // createAndShowGUI
} // End T3UI
