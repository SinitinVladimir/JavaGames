import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends JFrame {
    private JTable quizTable;
    private DefaultTableModel tableModel;
    private JTextField questionField, option1Field, option2Field, option3Field, option4Field, correctAnswerField;
    private JButton addButton, updateButton, deleteButton, exitButton;

    public AdminPanel() {
        setTitle("Admin Panel - Manage Quiz Questions");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // model and JTable setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct Answer"}, 0);
        quizTable = new JTable(tableModel);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(quizTable);

        // form panel for CRUD operations
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.add(new JLabel("Question:"));
        questionField = new JTextField();
        formPanel.add(questionField);
        formPanel.add(new JLabel("Option 1:"));
        option1Field = new JTextField();
        formPanel.add(option1Field);
        formPanel.add(new JLabel("Option 2:"));
        option2Field = new JTextField();
        formPanel.add(option2Field);
        formPanel.add(new JLabel("Option 3:"));
        option3Field = new JTextField();
        formPanel.add(option3Field);
        formPanel.add(new JLabel("Option 4:"));
        option4Field = new JTextField();
        formPanel.add(option4Field);
        formPanel.add(new JLabel("Correct Answer:"));
        correctAnswerField = new JTextField();
        formPanel.add(correctAnswerField);

        // CRUD operations
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        exitButton = new JButton("Exit");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);

        add(tableScrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        loadQuizData();

        addButton.addActionListener(e -> addQuizQuestion());
        updateButton.addActionListener(e -> updateQuizQuestion());
        deleteButton.addActionListener(e -> deleteQuizQuestion());
        exitButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new SnakeGame.SetupGame().setVisible(true));
        });

        quizTable.getSelectionModel().addListSelectionListener(e -> loadSelectedQuizQuestion());
    }

    private void loadQuizData() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM quiz_q")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("question"),
                    rs.getString("option1"),
                    rs.getString("option2"),
                    rs.getString("option3"),
                    rs.getString("option4"),
                    rs.getString("correct_answer")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSelectedQuizQuestion() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow != -1) {
            questionField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            option1Field.setText(tableModel.getValueAt(selectedRow, 2).toString());
            option2Field.setText(tableModel.getValueAt(selectedRow, 3).toString());
            option3Field.setText(tableModel.getValueAt(selectedRow, 4).toString());
            option4Field.setText(tableModel.getValueAt(selectedRow, 5).toString());
            correctAnswerField.setText(tableModel.getValueAt(selectedRow, 6).toString());
        }
    }

    private void addQuizQuestion() {
        String question = questionField.getText();
        String option1 = option1Field.getText();
        String option2 = option2Field.getText();
        String option3 = option3Field.getText();
        String option4 = option4Field.getText();
        String correctAnswer = correctAnswerField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake", "root", "");
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO quiz_q (question, option1, option2, option3, option4, correct_answer) VALUES (?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, question);
            pstmt.setString(2, option1);
            pstmt.setString(3, option2);
            pstmt.setString(4, option3);
            pstmt.setString(5, option4);
            pstmt.setString(6, correctAnswer);
            pstmt.executeUpdate();
            loadQuizData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateQuizQuestion() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow == -1) return;

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String question = questionField.getText();
        String option1 = option1Field.getText();
        String option2 = option2Field.getText();
        String option3 = option3Field.getText();
        String option4 = option4Field.getText();
        String correctAnswer = correctAnswerField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake", "root", "");
             PreparedStatement pstmt = conn.prepareStatement("UPDATE quiz_q SET question = ?, option1 = ?, option2 = ?, option3 = ?, option4 = ?, correct_answer = ? WHERE id = ?")) {
            pstmt.setString(1, question);
            pstmt.setString(2, option1);
            pstmt.setString(3, option2);
            pstmt.setString(4, option3);
            pstmt.setString(5, option4);
            pstmt.setString(6, correctAnswer);
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
            loadQuizData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteQuizQuestion() {
        int selectedRow = quizTable.getSelectedRow();
        if (selectedRow == -1) return;

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/snake", "root", "");
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM quiz_q WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadQuizData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPanel().setVisible(true));
    }
}
