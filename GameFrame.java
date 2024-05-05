import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame(String playerName, String colorPalette, String gameSpeed, boolean withBorders) {
        GamePanel gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders);
        this.add(gamePanel);
        setTitle("Unusual Snake Game - " + playerName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new GameFrame();
    }
}
