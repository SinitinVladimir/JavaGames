import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class BrickBreaker extends JPanel implements ActionListener, KeyListener {
    private String playerName;
    private int currentScore;
    private String gameSpeed;
    private int difficultyLevel;
    private Timer timer;
    private int ballPosX, ballPosY, ballDirX, ballDirY;
    private int paddleX, paddleY;
    private boolean gameRunning;
    private JButton resumeButton;
    private JButton exitButton;
    private JButton reloadButton;
    private int[][] bricks;
    private int brickWidth = 50;
    private int brickHeight = 20;
    private int brickRows = 10;
    private int brickColumns = 16;
    private JFrame frame;
    private GamePanel gamePanel;
    private int screenWidth;
    private int screenHeight;

    public BrickBreaker(GamePanel gamePanel, String playerName, String gameSpeed, int currentScore) {
        this.gamePanel = gamePanel;
        this.playerName = playerName;
        this.gameSpeed = gameSpeed;
        this.currentScore = currentScore;
        this.difficultyLevel = convertSpeedToLevel(gameSpeed);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight();
        initGame();
    }

    public static void main(String[] args) {
        String playerName = "Player1";
        int currentScore = 0;
        String gameSpeed = "Medium"; // Easy, Medium, Hard

        BrickBreaker game = new BrickBreaker(null, playerName, gameSpeed, currentScore);
        game.startGame();
    }

    private void initGame() {
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setLayout(null);
        addKeyListener(this);

        ballPosX = 120;
        ballPosY = 350;
        ballDirX = new Random().nextBoolean() ? 2 : -2;
        ballDirY = -2;

        paddleX = screenWidth / 2 - 75;
        paddleY = screenHeight - 60;
        gameRunning = true;

        int delay = (4 - 1 / 2) - difficultyLevel; // Higher difficulty, faster speed
        timer = new Timer(delay, this);
        timer.start();

        bricks = new int[brickRows][brickColumns];
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                bricks[i][j] = 1; // 1 indicates that the brick is not broken
            }
        }

        resumeButton = new JButton("Resume");
        resumeButton.setBounds(screenWidth - 150, screenHeight - 150, 100, 30);
        resumeButton.addActionListener(e -> {
            gamePanel.resumeSnakeGame(currentScore);
            frame.dispose();
        });
        add(resumeButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(screenWidth - 150, screenHeight - 100, 100, 30);
        exitButton.addActionListener(e -> exitGame());
        add(exitButton);

        reloadButton = new JButton("Reload");
        reloadButton.setBounds(screenWidth - 150, screenHeight - 50, 100, 30);
        reloadButton.addActionListener(e -> reloadGame());
        add(reloadButton);

        resumeButton.setVisible(false);
        exitButton.setVisible(false);
        reloadButton.setVisible(false);
    }

    public void startGame() {
        frame = new JFrame("Brick Breaker");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the frame full screen
        frame.setUndecorated(true); // Remove title bar
        frame.add(this);
        frame.setVisible(true);
    }

    private void exitGame() {
        gameRunning = false;
        timer.stop();
        System.exit(0); // Terminate the application completely
    }

    private void reloadGame() {
        gameRunning = false;
        timer.stop();
        frame.dispose();
        BrickBreaker newGame = new BrickBreaker(gamePanel, playerName, gameSpeed, 0);
        newGame.startGame();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (gameRunning) {
            // Background
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, screenWidth, screenHeight);

            // Paddle
            g.setColor(Color.GREEN);
            g.fillRect(paddleX, paddleY, 150, 10);

            // Ball
            g.setColor(Color.YELLOW);
            g.fillOval(ballPosX, ballPosY, 20, 20);

            // Bricks
            int bricksStartX = (screenWidth - (brickColumns * brickWidth)) / 2;
            for (int i = 0; i < brickRows; i++) {
                for (int j = 0; j < brickColumns; j++) {
                    if (bricks[i][j] == 1) {
                        g.setColor(Color.RED);
                        g.fillRect(bricksStartX + j * brickWidth, i * brickHeight + 50, brickWidth, brickHeight);
                        g.setColor(Color.BLACK);
                        g.drawRect(bricksStartX + j * brickWidth, i * brickHeight + 50, brickWidth, brickHeight);
                    }
                }
            }

            // Score, Name, Difficulty
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Player: " + playerName, 10, 20);
            g.drawString("Score: " + currentScore, 10, 40);
            g.drawString("Difficulty: " + gameSpeed, 10, 60);
        } else {
            // Game Over
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over, " + playerName + "!", screenWidth / 2 - 150, screenHeight / 2);
            g.drawString("Final Score: " + currentScore, screenWidth / 2 - 150, screenHeight / 2 + 40);

            resumeButton.setVisible(true);
            exitButton.setVisible(true);
            reloadButton.setVisible(true);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            moveBall();
            checkCollision();
            repaint();
        }
    }

    private void moveBall() {
        ballPosX += ballDirX * difficultyLevel;
        ballPosY += ballDirY * difficultyLevel;

        if (ballPosX < 0 || ballPosX > screenWidth - 20) {
            ballDirX = -ballDirX;
        }
        if (ballPosY < 0) {
            ballDirY = -ballDirY;
        }
        if (ballPosY > screenHeight - 20) {
            gameRunning = false;
            timer.stop();
        }
    }

    private void checkCollision() {
        // Paddle collision
        if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(paddleX, paddleY, 150, 10))) {
            ballDirY = -ballDirY;
        }

        // Brick collision
        int bricksStartX = (screenWidth - (brickColumns * brickWidth)) / 2;
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                if (bricks[i][j] == 1) {
                    int brickX = bricksStartX + j * brickWidth;
                    int brickY = i * brickHeight + 50;

                    Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                    Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                    if (ballRect.intersects(brickRect)) {
                        bricks[i][j] = 0;
                        currentScore += 1/10;

                        // Ball collision logic with bricks
                        if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                            ballDirX = -ballDirX;
                        } else {
                            ballDirY = -ballDirY;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int paddleSpeed = 30; // Increase paddle speed

        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            if (paddleX > 0) {
                paddleX -= paddleSpeed;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            if (paddleX < screenWidth - 150) { // Screen width - paddle width
                paddleX += paddleSpeed;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            if (paddleY > 0) {
                paddleY -= paddleSpeed;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            if (paddleY < screenHeight - 10) { // Screen height - paddle height
                paddleY += paddleSpeed;            
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public int getScore() {
        return currentScore;
    }

    private int convertSpeedToLevel(String gameSpeed) {
        switch (gameSpeed) {
            case "Easy":
                return 1;
            case "Medium":
                return 2;
            case "Hard":
                return 3;
            default:
                return 2; // Default to Medium if gameSpeed is not recognized
        }
    }
}
