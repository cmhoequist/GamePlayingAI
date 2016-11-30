package ai.main.ai.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by Moritz on 11/29/2016.
 * <p></p>
 */
public class HeaderPanel extends JPanel {
    //Top-level containers
    final JPanel titlePanel = new JPanel();
    final JPanel winsPanel = new JPanel();
    FlowLayout windowLayout = new FlowLayout();

    //Header labels
    private JLabel headerLabel = new JLabel("Player 1 - X");
    private JLabel xScore = new JLabel("X : 0");
    private JLabel oScore = new JLabel("     O : 0");

    public HeaderPanel(){
        titlePanel.setLayout(windowLayout);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titlePanel.add(headerLabel);

        winsPanel.setLayout(windowLayout);
        winsPanel.setBorder(new TitledBorder(""));
        winsPanel.add(xScore);
        winsPanel.add(oScore);

        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.CENTER);
        add(winsPanel, BorderLayout.SOUTH);
    }

    public void setHeader(String header) {
        headerLabel.setText(header);
    }

    public void setXScore(String xScore) {
        this.xScore.setText(xScore);
    }

    public void setOScore(String oScore) {
        this.oScore.setText(oScore);
    }
}
