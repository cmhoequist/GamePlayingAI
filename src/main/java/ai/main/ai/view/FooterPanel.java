package ai.main.ai.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by Moritz on 11/29/2016.
 * <p></p>
 */
public class FooterPanel extends JPanel {

    FlowLayout windowLayout = new FlowLayout();

    //Buttons
    JRadioButton pvpButton = new JRadioButton("PvP");
    JRadioButton aiButton = new JRadioButton("vs AI");
    JButton quitButton = new JButton("Quit");
    JButton newGameButton = new JButton("New Game");

    public FooterPanel(){
        setLayout(windowLayout);
        setBorder(new TitledBorder(""));

        //Decoration rules
        pvpButton.setFocusPainted(false);
        aiButton.setFocusPainted(false);
        newGameButton.setFocusPainted(false);
        quitButton.setFocusPainted(false);

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(pvpButton);
        radioGroup.add(aiButton);

        add(pvpButton);
        add(aiButton);
        add(newGameButton);
        add(quitButton);
    }

    public JRadioButton getPvpButton() {
        return pvpButton;
    }

    public JRadioButton getAiButton() {
        return aiButton;
    }

    public JButton getQuitButton() {
        return quitButton;
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }
}
