import javax.swing.JFrame;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private String colorPalette;
    private String gameSpeed;
    private boolean withBorders;

    public GameFrame(String playerName, String colorPalette, String gameSpeed, boolean withBorders) {
        this.colorPalette = colorPalette;
        this.gameSpeed = gameSpeed;
        this.withBorders = withBorders;
        setupGame(playerName, colorPalette, gameSpeed, withBorders);
    }

    private void setupGame(String playerName, String colorPalette, String gameSpeed, boolean withBorders) {
        if (gamePanel != null) {
            this.remove(gamePanel);
        }
        gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders);
        this.add(gamePanel);
        setTitle("Unusual Snake Game - " + playerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void restartGame() {
        this.remove(gamePanel);
        gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders);
        this.add(gamePanel);
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
        this.revalidate();
        this.repaint();
    }
}
