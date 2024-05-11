import javax.swing.*;
//import java.awt.*;
import java.awt.event.*;

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

    public QuizGame(GamePanel panel) {
        this.snakeGamePanel = panel;
        createQuestions();
        setupUI();
        snakeGamePanel.pauseGame();
    }

    private void createQuestions() {
        // arr Q&A
        questions = new String[][]{
                {"What is the average case time complexity of QuickSort?", "O(n log n)", "O(n^2)", "O(n)", "O(log n)", "O(n log n)"},
                {"Which sorting algorithm is stable?", "QuickSort", "HeapSort", "MergeSort", "BubbleSort", "MergeSort"},
                {"What is the worst case time complexity of MergeSort?", "O(n log n)", "O(n^2)", "O(n)", "O(log n)", "O(n log n)"}
        };
    }

    private void setupUI() {
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
        snakeGamePanel.resumeSnakeGame();
    }

    public void startQuiz() {
        // to initiate the quiz externally, may be later
    }
}
