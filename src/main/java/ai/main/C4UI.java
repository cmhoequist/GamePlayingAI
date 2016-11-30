package ai.main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Moritz on 11/28/2016.
 * <p></p>
 */
public class C4UI extends JFrame{

    //serialVersionUID
    private static final long serialVersionUID = 1L;
    //windowLayout
    private FlowLayout windowLayout = new FlowLayout();

    // Tiles as buttons
    private JButton[][] tiles;
    private int[] heightIndex;

    // Matrix
    final private int[][] tileMatrix;

    // turn = 1 - Player 1
    // turn = -1 - PLayer 2 or AI
    private int turn = 1;

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

    //Size
    private int rows;
    private int cols;

    // Constructor for T3UI
    private C4UI(String name, int rows, int cols) {
        //Inherits name from JFrame
        super(name);

        this.rows = rows;
        this.cols = cols;

        aiPlayer = new GeneticPlayer();

        tileMatrix = new int[cols][rows+1];
        heightIndex = new int[cols];
        timer = new Timer(500, timerAction);
    }

    private void addButtonIcons(){
        Image downArrow = new ImageIcon("down1.png").getImage();
        int w, h;
        for(int i = 0; i < cols; i++){
            w = tiles[i][0].getPreferredSize().width;
            h = tiles[i][0].getPreferredSize().height*3;
            Icon icon = new ImageIcon(downArrow.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH));
            tiles[i][0].setIcon(icon);
        }
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

        //actions JPanel
        final JPanel actions = new JPanel();
        actions.setLayout(windowLayout);
        actions.setBorder(new TitledBorder(""));

        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(titlePanel, BorderLayout.NORTH);
        innerPanel.add(winsPanel, BorderLayout.CENTER);

        final JPanel game = new JPanel(new GridLayout(rows+1, cols));

//----------------------JButtons--------------------------------------------------
        JButton quitButton = new JButton("Quit");
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFocusPainted(false);
        quitButton.setFocusPainted(false);

        tiles = new JButton[cols][rows+1];

        for(int x = 0; x < cols; x++){
            tiles[x][0] = new JButton();
            tiles[x][0].setFocusPainted(false);
            final int i = x;
            tiles[x][0].addActionListener(e ->{
                tiles[i][heightIndex[i]].setText(turn == 1 ? "X" : "O");
                tileMatrix[i][heightIndex[i]] = (turn);
                heightIndex[i] -= 1;
                if(heightIndex[i] == 0){
                    tiles[i][0].setEnabled(false);
                }
                checkWin();
            });
            game.add(tiles[x][0]);
            heightIndex[x] = rows;
        }
        for(int y = 1; y < rows+1; y++){
            for(int x = 0; x < cols; x++){
                tiles[x][y] = new JButton();
                tiles[x][y].setVisible(true);
                tiles[x][y].setFocusPainted(false);
                tiles[x][y].setBackground(Color.WHITE);
                tiles[x][y].setFont(new Font("Arial", Font.PLAIN, 40));
                tiles[x][y].setEnabled(false);
                game.add(tiles[x][y]);
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

    private void checkWin() {
        moves += 1;
        long bits = 0;

        for(int i = rows; i >= 1; i--){
            for(int j = cols; j > 0; j--){
                //Only need to check winner for current turn
                if(tileMatrix[cols-j][rows-i+1]==turn){
                    int num = rows*(j-1)+(i-1);
                    bits += new Double(Math.pow(2.0, num)).longValue();
                }
            }


        }
        long winString;
        int[] winShifts = { 1, //vertical
                            5, //diagonal /
                            6, //horizontal
                            7 //diagonal \
                          };

        boolean gameOver =false;
        for(int shift : winShifts){
            winString = bits & (bits >> shift);
            //Re !=0; excessive matching (5-in-a-row) can occur, or perfect match (1 bit remaining) may occur but not in last bit
            if ((winString & (winString >> 2 * shift)) != 0) {
                gameOver = true;
                titleLBL.setText(turn+" WINS on shift " + shift);
            }
        }

        // All moves and no wins
        if (moves >= cols*rows && !gameOver) {
            gameOver = true;
            titleLBL.setText("Draw!");
        }

        turn = (turn == 1 ? -1 : 1);
    }

    private void newGame() {
        moves = 0;
        if (turn == 1) {
            titleLBL.setText("Player 1 - X");
        } else {
            titleLBL.setText("Player 2 - O");
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tileMatrix[i][j] = 0;
                tiles[i][j].setText("");
                tiles[i][j].setBackground(Color.WHITE);
                tiles[i][j].setForeground(Color.BLACK);
                tiles[i][j].setOpaque(true);
            }
        }

        if(mode == 1 && turn == -1)
        {
            aiMove();
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
                int[] aiMove = aiPlayer.move(tileMatrix, turn);
                tiles[aiMove[0]][aiMove[1]].setText(turn == 1 ? "X" : "O");
                tileMatrix[aiMove[0]][aiMove[1]] = (turn);
                if (moves != 10) {
                    moves += 1;
                }
//                checkGame();
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
        C4UI frame = new C4UI("Connect Four", 6, 7);

        //Exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Setup list of images for frame's icons
        final ArrayList<Image> icons = new ArrayList<>();
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
        frame.addButtonIcons();

        //Show the T3UI
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setResizable(false);
    } // createAndShowGUI
}
