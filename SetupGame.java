import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetupGame extends JFrame {
    private JTextField playerNameField;
    private JComboBox<String> colorPaletteDropdown;
    private JComboBox<String> gameSpeedDropdown;
    private JCheckBox bordersCheckBox;
    private JButton startButton;
    private JButton leaderboardButton;

    public SetupGame() {
        setTitle("Game Setup");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));  // 6 rows, 2 columns

        playerNameField = new JTextField();
        colorPaletteDropdown = new JComboBox<>(new String[]{"Black and White", "Neon", "Pinky", "Dark", "High Contrast", "Party Mode"});
        gameSpeedDropdown = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        bordersCheckBox = new JCheckBox("With Borders");
        startButton = new JButton("Start Game");
        leaderboardButton = new JButton("Leaderboards");

        add(new JLabel("Player Name:"));
        add(playerNameField);
        add(new JLabel("Color Palette:"));
        add(colorPaletteDropdown);
        add(new JLabel("Game Speed:"));
        add(gameSpeedDropdown);
        add(new JLabel("Game Borders:"));
        add(bordersCheckBox);
        add(startButton);
        add(leaderboardButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLeaderboard();
            }
        });

        setVisible(true);
    }

    private void startGame() {
        // get settings from the setup form
        String playerName = playerNameField.getText();
        String colorPalette = (String) colorPaletteDropdown.getSelectedItem();
        String gameSpeed = (String) gameSpeedDropdown.getSelectedItem();
        boolean withBorders = bordersCheckBox.isSelected();

        //launching a game instance with selected options
        GameFrame gameFrame = new GameFrame(playerName, colorPalette, gameSpeed, withBorders);
        dispose();  // close setup window
    }

    private void showLeaderboard() {
        // the leaderboard functionality
        JOptionPane.showMessageDialog(this, "Leaderboard feature coming soon, or not)");
    }

    public static void main(String[] args) {
        new SetupGame();
    }
}
