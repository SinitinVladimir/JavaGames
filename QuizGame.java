import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Statement;

public class QuizGame {
    private JFrame frame;
    private GamePanel snakeGamePanel;
    private JPanel panel;
    private JButton submitButton;
    private JLabel questionLabel;
    private ButtonGroup options;
    private JRadioButton[] buttons;
    private String[][] questions;
    private int currentQuestion = 0;
    private int score = 0;
    private String playerName;
    private String gameSpeed;
    private int snakeScore;

    public QuizGame(GamePanel panel, String playerName, String gameSpeed, int snakeScore) {
        this.snakeGamePanel = panel;
        this.playerName = playerName;
        this.gameSpeed = gameSpeed;
        this.snakeScore = snakeScore;
        createQuestions();
        setupUI();
        snakeGamePanel.pauseGame();
    }

    private void createQuestions() {
        ArrayList<String[]> questionsList = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");  // JDBC driver
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake?useSSL=false&serverTimezone=UTC", "root", "");
            stmt = conn.createStatement();
            String sql = "SELECT question, option1, option2, option3, option4, correct_answer FROM quiz_q";
            rs = stmt.executeQuery(sql);
    
            // Process result set
            while (rs.next()) {
                String[] qData = new String[6];
                qData[0] = rs.getString("question");
                qData[1] = rs.getString("option1");
                qData[2] = rs.getString("option2");
                qData[3] = rs.getString("option3");
                qData[4] = rs.getString("option4");
                qData[5] = rs.getString("correct_answer");
                questionsList.add(qData);
            }
    
            if (questionsList.isEmpty()) {
                throw new IllegalStateException("No questions found in the database.");
            }
    
            questions = questionsList.toArray(new String[0][]);  // Convert to array
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading questions: " + e.getMessage());
            System.exit(1);  // Exit the application
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupUI() {
        if (questions == null || questions.length == 0) {
            JOptionPane.showMessageDialog(null, "No questions available to display.");
            return;  // Handle empty questions scenario
        }
    
        frame = new JFrame("Sorting Algorithm Quiz");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
    
        panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        questionLabel = new JLabel("Question: " + questions[currentQuestion][0]);
        panel.add(questionLabel);
    
        options = new ButtonGroup();
        buttons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            buttons[i] = new JRadioButton(questions[currentQuestion][i + 1]);
            buttons[i].setActionCommand(questions[currentQuestion][i + 1]);
            options.add(buttons[i]);
            panel.add(buttons[i]);
        }
    
        submitButton = new JButton("Submit Answer");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });
        panel.add(submitButton);
    
        frame.setVisible(true);
    }

    private void checkAnswer() {
        String selectedAnswer = options.getSelection().getActionCommand();
        if (selectedAnswer.equals(questions[currentQuestion][5])) {
            score++;
        }
        currentQuestion++;
        if (currentQuestion < questions.length) {
            updateQuestion();
        } else {
            finishQuiz();
        }
    }

    private void updateQuestion() {
        questionLabel.setText("Question: " + questions[currentQuestion][0]);
        for (int i = 0; i < 4; i++) {
            buttons[i].setText(questions[currentQuestion][i + 1]);
            buttons[i].setActionCommand(questions[currentQuestion][i + 1]);
        }
    }

    private void finishQuiz() {
        JOptionPane.showMessageDialog(frame, "Quiz completed! Your score: " + score + "/" + questions.length);
        frame.dispose();
        snakeGamePanel.resumeSnakeGame(snakeScore + score);  // Update the snake game score
    }

    public void startQuiz() {
        // Placeholder for any future initialization logic
    }
}
