import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame(String playerName, String colorPalette, String gameSpeed, boolean withBorders) {
        GamePanel gamePanel = new GamePanel(colorPalette, gameSpeed, withBorders);
        this.add(gamePanel);
        setTitle("Unusual Snake Game - " + playerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
    
    // for testing only
    // public static void main(String[] args) {
    //     new GameFrame("Test Player", "Black and White", "Medium", true);
    // }

    // public static void main(String[] args) {
    //     new GameFrame(); 
    // }
}