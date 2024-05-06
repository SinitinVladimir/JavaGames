import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
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
    boolean isQuizFood = false;
    char direction = 'R';  //direction variable
    boolean running = false;
    Timer timer;
    Random random;
    boolean isSpecialFood = false;

    GamePanel(String colorPalette, String gameSpeed, boolean withBorders) {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        // this.addMouseMotionListener(new MouseMotionAdapter() {
        //     @Override
        //     public void mouseMoved(MouseEvent e) {
        //         setDirectionBasedOnMouse(e.getX(), e.getY());
        //     }
        // });
        startGame();
        // game speed
        switch (gameSpeed) {
            case "Easy":
                DELAY = 100;
                break;
            case "Medium":
                DELAY = 75;
                break;
            case "Hard":
                DELAY = 50;
                break;
        }

        //  color settings
        if (colorPalette.equals("Neon")) {
            // color theme
        }

        // borders
        this.setBorder(withBorders ? BorderFactory.createLineBorder(Color.gray) : null);
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // normal food
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // brick Breaker food
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
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        // randomly normal food
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

        // randomly decide which special food to spawn
        int foodType = random.nextInt(3); // number between 0 and 2
        if (foodType == 0) {
            isSpecialFood = true;
            specialAppleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            specialAppleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else if (foodType == 1) {
            isQuizFood = true;
            quizAppleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            quizAppleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else {
            isSpecialFood = false;
            isQuizFood = false;
        }
    }

    // private void setDirectionBasedOnMouse(int mouseX, int mouseY) {
    //     int headX = x[0];
    //     int headY = y[0];
    //     int deltaX = mouseX - headX;
    //     int deltaY = mouseY - headY;
    //     // Decide direction based on mouse position relative to snake head
    //     if (Math.abs(deltaX) > Math.abs(deltaY)) {
    //         if (deltaX > 0 && direction != 'L') {
    //             direction = 'R'; // Mouse is to the right of the head
    //         } else if (deltaX < 0 && direction != 'R') {
    //             direction = 'L'; // Mouse is to the left of the head
    //         }
    //     } else {
    //         if (deltaY > 0 && direction != 'U') {
    //             direction = 'D'; // Mouse is below the head
    //         } else if (deltaY < 0 && direction != 'D') {
    //             direction = 'U'; // Mouse is above the head
    //         }
    //     }
    // }

    public void checkApple() {
        // normal food eaten
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }

        // special bb food tart bb
        if (isSpecialFood && (x[0] == specialAppleX) && (y[0] == specialAppleY)) {
            pauseSnakeAndStartBrickBreaker();
        }

        // q food eaten, start Q
        if (isQuizFood && (x[0] == quizAppleX) && (y[0] == quizAppleY)) {
            pauseSnakeAndStartQuiz();
        }
    }

    private void pauseSnakeAndStartQuiz() {
        running = false;
        timer.stop();
        QuizGame quizGame = new QuizGame(this);
        quizGame.startQuiz();
    }

    // bb start method to include snakeGamePanel
    private void pauseSnakeAndStartBrickBreaker() {
        running = false;
        timer.stop();
        JOptionPane.showMessageDialog(this, "Press OK to start Brick Breaker");

        // window for bb
        JFrame gameFrame = new JFrame("Brick Breaker Game");
        BrickBreakerGame brickBreakerGame = new BrickBreakerGame(this);

        gameFrame.add(brickBreakerGame);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setSize(700, 600);
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
        gameFrame.setLocationRelativeTo(null);
    }


    public void resumeSnakeGame() {
        JOptionPane.showMessageDialog(this, "Tetris game completed. Resuming Snake game.");
        running = true;
        timer.start();
    }

    public void checkCollisions() {
        // check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // check if head touches left border
        if (x[0] < 0) {
            running = false;
        }

        // check if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }

        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }

        // check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
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
        // body  movement
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[(i - 1)];
            y[i] = y[(i - 1)];
        }

        // head movement on direction
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
