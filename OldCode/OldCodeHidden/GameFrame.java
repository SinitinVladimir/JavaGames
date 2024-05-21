import javax.swing.JFrame;

public class GameFrame extends JFrame implements OldGamePanel.GameEventListener {
    private OldGamePanel gamePanel;
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
        gamePanel = new OldGamePanel(colorPalette, gameSpeed, withBorders, this);
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
        gamePanel = new OldGamePanel(colorPalette, gameSpeed, withBorders, this);
        this.add(gamePanel);
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
        this.revalidate();
        this.repaint();
    }
}
