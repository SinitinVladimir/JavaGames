import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SnakeGame extends JFrame implements GamePanel.GameEventListener {
    private GamePanel gamePanel;
    private String colorPalette;
    private String gameSpeed;
    private boolean withBorders;
    private boolean threeDBehavior;
    private String playerName;

    public SnakeGame(String playerName, String colorPalette, String gameSpeed, boolean withBorders, boolean threeDBehavior) {
        this.playerName = playerName;
        this.colorPalette = colorPalette;
        this.gameSpeed = gameSpeed;
        this.withBorders = withBorders;
        this.threeDBehavior = threeDBehavior;
        setupGame(playerName, colorPalette, gameSpeed, withBorders, threeDBehavior);
    }

    private void setupGame(String playerName, String colorPalette, String gameSpeed, boolean withBorders, boolean threeDBehavior) {
        if (gamePanel != null) {
            this.remove(gamePanel);
        }
        gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders, this, threeDBehavior);
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
        gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders, this, threeDBehavior);
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
        private JCheckBox threeDCheckBox;
        private JButton startButton;
        private JButton leaderboardButton;
        private JButton adminButton;

        public SetupGame() {
            setTitle("Game Setup");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new GridLayout(7, 2, 10, 10));

            playerNameField = new JTextField();
            colorPaletteDropdown = new JComboBox<>(new String[]{"Black and White", "Neon", "Pinky", "Dark", "High Contrast", "Party Mode"});
            gameSpeedDropdown = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
            bordersCheckBox = new JCheckBox("With Borders");
            threeDCheckBox = new JCheckBox("3D Behavior");
            startButton = new JButton("Start Game");
            leaderboardButton = new JButton("Leaderboards");
            adminButton = new JButton("Admin Login");

            add(new JLabel("Player Name:"));
            add(playerNameField);
            add(new JLabel("Color Palette:"));
            add(colorPaletteDropdown);
            add(new JLabel("Game Speed:"));
            add(gameSpeedDropdown);
            add(new JLabel("Game Borders:"));
            add(bordersCheckBox);
            add(new JLabel("3D Behavior:"));
            add(threeDCheckBox);
            add(startButton);
            add(leaderboardButton);
            add(adminButton);

            startButton.addActionListener(e -> {
                String playerName = playerNameField.getText();
                String colorPalette = (String) colorPaletteDropdown.getSelectedItem();
                String gameSpeed = (String) gameSpeedDropdown.getSelectedItem();
                boolean withBorders = bordersCheckBox.isSelected();
                boolean threeDBehavior = threeDCheckBox.isSelected();

                new SnakeGame(playerName, colorPalette, gameSpeed, withBorders, threeDBehavior);
                this.dispose();
            });

            leaderboardButton.addActionListener(e -> new LeaderboardFrame().setVisible(true));

            adminButton.addActionListener(e -> showAdminLogin());

            setVisible(true);
        }

        private void showAdminLogin() {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (validateAdminLogin(username, password)) {
                    new AdminPanel().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid login. Please try again.");
                }
            }
        }

        private boolean validateAdminLogin(String username, String password) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake", "root", "");
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM login WHERE player_name = ? AND password = ?")) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public static class LeaderboardFrame extends JFrame {
        public LeaderboardFrame() {
            setTitle("Leaderboards");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());

            JTextArea leaderboardArea = new JTextArea();
            leaderboardArea.setEditable(false);
            add(new JScrollPane(leaderboardArea), BorderLayout.CENTER);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> new SetupGame().setVisible(true));
            });
            add(exitButton, BorderLayout.SOUTH);

            loadLeaderboard(leaderboardArea);
        }

        private void loadLeaderboard(JTextArea leaderboardArea) {
            StringBuilder sb = new StringBuilder();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake", "root", "");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM leaderboards ORDER BY score DESC")) {

                while (rs.next()) {
                    String playerName = rs.getString("player_name");
                    int score = rs.getInt("score");
                    String difficulty = rs.getString("difficulty");
                    sb.append(String.format("Player: %s, Score: %d, Difficulty: %s%n", playerName, score, difficulty));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                sb.append("Error loading leaderboard.");
            }
            leaderboardArea.setText(sb.toString());
        }
    }
}
