import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    int delay;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    int specialAppleX;
    int specialAppleY;
    int quizAppleX;
    int quizAppleY;
    int ticTacToeAppleX;
    int ticTacToeAppleY;
    boolean isQuizFood = false;
    boolean isTicTacToeFood = false;
    boolean isSpecialFood = false;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    private JButton reloadButton; // reloadButton as a class variable
    private Color backgroundColor;
    private Color foodColor;
    private Color snakeHeadColor;
    private Color snakeBodyColor;
    private Timer colorTimer;
    private String gameSpeed;
    private MyKeyAdapter keyAdapter;
    private SnakeGame gameEventListener;
    private boolean withBorders;
    private String playerName;
    private JLabel scoreLabel;

    public GamePanel(String colorPalette, String gameSpeed, boolean withBorders, SnakeGame listener) {
        this.playerName = listener.getTitle().replace("Unusual Snake Game - ", ""); // Extract player name from title
        this.gameSpeed = gameSpeed;
        this.withBorders = withBorders;
        this.gameEventListener = listener;
        random = new Random();
        keyAdapter = new MyKeyAdapter();
        addKeyListener(keyAdapter);
        applyColorPalette(colorPalette);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        setupGameSettings(gameSpeed, withBorders);
        startGame();

        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateScoreLabel();
        this.add(scoreLabel);
    }

    private void setupGameSettings(String gameSpeed, boolean withBorders) {
        switch (gameSpeed) {
            case "Easy": delay = 100; break;
            case "Medium": delay = 75; break;
            case "Hard": delay = 50; break;
            default: delay = 75; break;
        }
        if (withBorders) {
            setBorder(BorderFactory.createLineBorder(Color.gray));
        } else {
            setBorder(null);
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.format("Player: %s | Score: %d | Difficulty: %s", playerName, applesEaten, gameSpeed));
    }

    public void restartGame() {
        removeKeyListener(keyAdapter);
        keyAdapter = new MyKeyAdapter();
        addKeyListener(keyAdapter);
        requestFocusInWindow();
        startGame();
        updateScoreLabel();
    }

    public String getGameSpeed() {
        return this.gameSpeed;
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(delay, this); // delay dynamically adjusted instead of fixed DELAY
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // normal food
            g.setColor(foodColor);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // special food for Brick Breaker
            if (isSpecialFood) {
                g.setColor(Color.blue);
                g.fillOval(specialAppleX, specialAppleY, UNIT_SIZE, UNIT_SIZE);
            }

            // quiz food
            if (isQuizFood) {
                g.setColor(Color.magenta);  // distinct color - quiz food
                g.setFont(new Font("Arial", Font.BOLD, UNIT_SIZE));
                g.drawString("?", quizAppleX, quizAppleY + UNIT_SIZE);  // question mark
            }

            // Tic Tac Toe food
            if (isTicTacToeFood) {
                g.setColor(Color.orange);  // distinct color - Tic Tac Toe food
                g.setFont(new Font("Arial", Font.BOLD, UNIT_SIZE));
                g.drawString("T", ticTacToeAppleX, ticTacToeAppleY + UNIT_SIZE);  // T for Tic Tac Toe
            }

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(snakeHeadColor);
                } else {
                    g.setColor(snakeBodyColor);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    private void startPartyMode() {
        colorTimer = new Timer(2000, e -> {
            // change colors every 2 sec
            foodColor = new Color((int) (Math.random() * 0x1000000));
            snakeHeadColor = new Color((int) (Math.random() * 0x1000000));
            snakeBodyColor = new Color((int) (Math.random() * 0x1000000));
            repaint();
        });
        colorTimer.start();
    }

    private void applyColorPalette(String colorPalette) {
        if (colorTimer != null) {  // ensure that any existing timer is stopped before switching modes
            stopPartyMode();
        }

        switch (colorPalette) {
            case "Black and White":
                backgroundColor = Color.white;
                foodColor = Color.black;
                snakeHeadColor = Color.gray;
                snakeBodyColor = Color.darkGray;
                break;
            case "Neon":
                backgroundColor = Color.black;
                foodColor = Color.magenta;
                snakeHeadColor = Color.green;
                snakeBodyColor = Color.cyan;
                break;
            case "Pinky":
                backgroundColor = new Color(255, 192, 203); // Pink
                foodColor = new Color(255, 105, 180); // Hot Pink
                snakeHeadColor = new Color(255, 20, 147); // Deep Pink
                snakeBodyColor = new Color(199, 21, 133); // Medium Violet Red
                break;
            case "Dark":
                backgroundColor = new Color(25, 25, 25);
                foodColor = new Color(75, 75, 75);
                snakeHeadColor = new Color(0, 100, 0);
                snakeBodyColor = new Color(47, 79, 79);
                break;
            case "High Contrast":
                backgroundColor = Color.yellow;
                foodColor = Color.green;
                snakeHeadColor = Color.red;
                snakeBodyColor = Color.blue;
                break;
            case "Party Mode":
                startPartyMode();
                return; // to skip setting static colors
            default:
                backgroundColor = Color.black;
                foodColor = Color.red;
                snakeHeadColor = Color.green;
                snakeBodyColor = new Color(45, 180, 0);
                break;
        }
        setBackground(backgroundColor);
        repaint();  // to apply a new color immediately
    }

    private void stopPartyMode() {
        if (colorTimer != null) {
            colorTimer.stop();
        }
    }

    public void newApple() {
        // Reset all food flags
        isSpecialFood = false;
        isQuizFood = false;
        isTicTacToeFood = false;

        // Generate positions for normal food
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

        // Randomly decide which special food to spawn
        int foodType = random.nextInt(4); // number between 0 and 3
        if (foodType == 0) {
            isSpecialFood = true;
            specialAppleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            specialAppleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else if (foodType == 1) {
            isQuizFood = true;
            quizAppleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            quizAppleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else if (foodType == 2) {
            isTicTacToeFood = true;
            ticTacToeAppleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            ticTacToeAppleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        }
    }

    public void checkApple() {
        // normal food eaten
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            updateScoreLabel();
        }

        // special brick breaker food eaten
        if (isSpecialFood && (x[0] == specialAppleX) && (y[0] == specialAppleY)) {
            pauseSnakeAndStartBrickBreaker();
        }

        // quiz food eaten, start quiz
        if (isQuizFood && (x[0] == quizAppleX) && (y[0] == quizAppleY)) {
            pauseSnakeAndStartQuiz();
        }

        // Tic Tac Toe food eaten, start Tic Tac Toe
        if (isTicTacToeFood && (x[0] == ticTacToeAppleX) && (y[0] == ticTacToeAppleY)) {
            pauseSnakeAndStartTicTacToe();
        }
    }

    private void pauseSnakeAndStartQuiz() {
        running = false;
        timer.stop();
        QuizGame quizGame = new QuizGame(this, playerName, gameSpeed, applesEaten);
        quizGame.startQuiz();
    }

    private void pauseSnakeAndStartBrickBreaker() {
        running = false;
        timer.stop();
        JFrame gameFrame = new JFrame("Brick Breaker Game");
        BrickBreaker brickBreaker = new BrickBreaker(this, playerName, gameSpeed, applesEaten*100);
        gameFrame.add(brickBreaker);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the frame full screen
        gameFrame.setUndecorated(true); // Remove title bar
        gameFrame.setVisible(true);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                resumeSnakeGame(brickBreaker.getScore());
            }
        });
    }
    

    private void pauseSnakeAndStartTicTacToe() {
        running = false;
        timer.stop();
        JFrame gameFrame = new JFrame("Tic Tac Toe Game");
        TicTacToe ticTacToe = new TicTacToe(this, playerName, gameSpeed, applesEaten);
    
        gameFrame.add(ticTacToe);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the frame full screen
        gameFrame.setUndecorated(true); // Remove title bar
        gameFrame.setVisible(true);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                resumeSnakeGame(ticTacToe.getScore());
            }
        });
    }

    public void resumeSnakeGame(int updatedScore) {
        applesEaten = updatedScore;
        bodyParts = 6 + (applesEaten * 1); // Update body parts based on new score
        JOptionPane.showMessageDialog(this, "Returning to Snake Game.");
        running = true;
        updateScoreLabel();
        timer.start();
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                gameEventListener.onGameRestartRequested();
                return;
            }
        }

        if (withBorders) {
            if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
                gameEventListener.onGameRestartRequested();
                return;
            }
        } else {
            // Portal-like behavior
            if (x[0] < 0) {
                x[0] = SCREEN_WIDTH - UNIT_SIZE;
            } else if (x[0] >= SCREEN_WIDTH) {
                x[0] = 0;
            }
            if (y[0] < 0) {
                y[0] = SCREEN_HEIGHT - UNIT_SIZE;
            } else if (y[0] >= SCREEN_HEIGHT) {
                y[0] = 0;
            }
        }
    }

    // Updated gameOver method
    public void gameOver(Graphics g) {
        stopPartyMode();  // stop changing colors
        // the game over message
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        if (reloadButton == null) {
            reloadButton = new JButton("Reload");
            reloadButton.addActionListener(e -> {
                restartGame();
                this.remove(reloadButton);  // Remove the button after it is pressed
                reloadButton = null;  // Reset the button reference
            });
            this.add(reloadButton);
            reloadButton.setBounds(SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2 + 100, 100, 50);
        }
        this.repaint();
    }

    public void pauseGame() {
        timer.stop();
        running = false;
    }

    public void resumeGame() {
        timer.start();
        running = true;
        requestFocusInWindow();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public void move() {
        // body movement
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[(i - 1)];
            y[i] = y[(i - 1)];
        }

        // head movement
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public interface GameEventListener {
        void onGameRestartRequested();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}

