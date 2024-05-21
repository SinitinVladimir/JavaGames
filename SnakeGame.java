import javax.swing.*;
import java.awt.*;

public class SnakeGame extends JFrame implements GamePanel.GameEventListener {
    private GamePanel gamePanel;
    private String colorPalette;
    private String gameSpeed;
    private boolean withBorders;

    public SnakeGame(String playerName, String colorPalette, String gameSpeed, boolean withBorders) {
        this.colorPalette = colorPalette;
        this.gameSpeed = gameSpeed;
        this.withBorders = withBorders;
        setupGame(playerName, colorPalette, gameSpeed, withBorders);
    }

    private void setupGame(String playerName, String colorPalette, String gameSpeed, boolean withBorders) {
        if (gamePanel != null) {
            this.remove(gamePanel);
        }
        gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders, this);
        this.add(gamePanel);
        setTitle("Unusual Snake Game - " + playerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void onGameRestartRequested() {
        restartGame();
    }

    public void restartGame() {
        this.remove(gamePanel);
        gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders, this);
        this.add(gamePanel);
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
        this.revalidate();
        this.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SetupGame());
    }

    public static class SetupGame extends JFrame {
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
            setLayout(new GridLayout(6, 2, 10, 10));

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

            startButton.addActionListener(e -> {
                String playerName = playerNameField.getText();
                String colorPalette = (String) colorPaletteDropdown.getSelectedItem();
                String gameSpeed = (String) gameSpeedDropdown.getSelectedItem();
                boolean withBorders = bordersCheckBox.isSelected();

                new SnakeGame(playerName, colorPalette, gameSpeed, withBorders);
                this.dispose();
            });

            leaderboardButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Leaderboard feature coming soon, maybe:)"));

            setVisible(true);
        }
    }
}
