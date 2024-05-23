import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TicTacToe extends JFrame {
    private JButton[] buttons = new JButton[9];
    private JLabel playerInfo;
    private JButton resumeButton, exitButton;
    private boolean playerTurn = true; // starts the game
    private String playerName;
    private int currentScore;
    private int difficultyLevel; // simulate game speed (response time)
    private GamePanel gamePanel;
    private String gameSpeed;

    public TicTacToe(GamePanel gamePanel, String playerName, String gameSpeed, int currentScore) {
        this.gamePanel = gamePanel;
        this.playerName = playerName;
        this.gameSpeed = gameSpeed;
        this.difficultyLevel = convertSpeedToLevel(gameSpeed);
        this.currentScore = currentScore;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel ticTacToePanel = new JPanel(new GridLayout(3, 3)); // Renamed to ticTacToePanel to avoid conflict!!!
        JPanel controlPanel = new JPanel(new FlowLayout());

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            ticTacToePanel.add(buttons[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this::buttonClicked);
        }

        playerInfo = new JLabel("Player: " + playerName + ", Score: " + currentScore + ", Level: " + difficultyLevel, JLabel.CENTER);
        playerInfo.setFont(new Font("Arial", Font.BOLD, 16));

        resumeButton = new JButton("Resume");
        exitButton = new JButton("Exit");

        resumeButton.addActionListener(e -> {
            gamePanel.resumeSnakeGame(currentScore);
            this.dispose();
        });
        exitButton.addActionListener(e -> exitGame());

        controlPanel.add(resumeButton);
        controlPanel.add(exitButton);

        add(ticTacToePanel, BorderLayout.CENTER);
        add(playerInfo, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);

        setTitle("Tic Tac Toe - Player: " + playerName);
        setVisible(true);
    }

    private void buttonClicked(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        if (clickedButton.getText().equals("") && playerTurn) {
            clickedButton.setText("X");
            playerTurn = false;
            if (!checkForWin()) {
                if (isBoardFull()) {
                    JOptionPane.showMessageDialog(this, "Draw! No more moves left.");
                    resumeGame();
                } else {
                    new Timer(difficultyLevel * 1000, this::computerMove).start();
                }
            }
        }
    }

    private void computerMove(ActionEvent e) {
        ((Timer) e.getSource()).stop(); // stop timer to prevent multiple calls
    
        int bestMove = findBestMove(); // get best move using minimax
        buttons[bestMove].setText("O");
        playerTurn = true;
    
        if (!checkForWin() && isBoardFull()) {
            JOptionPane.showMessageDialog(this, "Draw! No more moves left.");
            resumeGame();
        }
    }
    
    private int findBestMove() {
        int bestVal = Integer.MIN_VALUE; // initialize best value
        int bestMove = -1; // initialize best move
    
        // evaluate minimax for all empty cells
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O"); // make move
                int moveVal = minimax(0, false); // evaluate move
                buttons[i].setText(""); // undo move
    
                if (moveVal > bestVal) { // if current move is better
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }
    
        return bestMove;
    }
    
    private int minimax(int depth, boolean isMax) {
        int score = evaluate(); // evaluate board
    
        if (score == 10) return score; // maximizer won
        if (score == -10) return score; // minimizer won
        if (isBoardFull()) return 0; // draw
    
        if (isMax) {
            int best = Integer.MIN_VALUE;
    
            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("O"); // make move
                    best = Math.max(best, minimax(depth + 1, !isMax)); // call minimax
                    buttons[i].setText(""); // undo move
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
    
            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("X"); // make move
                    best = Math.min(best, minimax(depth + 1, !isMax)); // call minimax
                    buttons[i].setText(""); // undo move
                }
            }
            return best;
        }
    }
    
    private int evaluate() {
        int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };
    
        for (int[] condition : winConditions) {
            if (buttons[condition[0]].getText().equals(buttons[condition[1]].getText()) &&
                buttons[condition[1]].getText().equals(buttons[condition[2]].getText())) {
                if (buttons[condition[0]].getText().equals("O")) {
                    return 10; // computer wins
                } else if (buttons[condition[0]].getText().equals("X")) {
                    return -10; // player wins
                }
            }
        }
        return 0; // no winner
    }
    

    private boolean checkForWin() {
        int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] condition : winConditions) {
            if (checkCondition(condition[0], condition[1], condition[2])) {
                String winner = buttons[condition[0]].getText();
                gameOver(winner);
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCondition(int a, int b, int c) {
        return !buttons[a].getText().equals("") &&
               buttons[a].getText().equals(buttons[b].getText()) &&
               buttons[b].getText().equals(buttons[c].getText());
    }

    private void gameOver(String winner) {
        if (winner.equals("X")) {
            JOptionPane.showMessageDialog(this, "Game Over. You lose!");
        } else {
            currentScore++;
            JOptionPane.showMessageDialog(this, "Game Over. Computer wins!");
        }
        resumeGame();
    }

    private void resumeGame() {
        gamePanel.resumeSnakeGame(currentScore);
        this.dispose();
    }

    private void exitGame() {
        System.exit(0);  // kill the application
    }

    public int getScore() {
        return currentScore;
    }

    private int convertSpeedToLevel(String gameSpeed) {
        switch (gameSpeed) {
            case "Easy": return 1;
            case "Medium": return 2;
            case "Hard": return 3;
            default: return 1;
        }
    }

    public static void main(String[] args) {
        new TicTacToe(null, "Player", "Medium", 0);  // Example instantiation
    }
}
