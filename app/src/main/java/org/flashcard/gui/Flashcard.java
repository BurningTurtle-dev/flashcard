package org.flashcard.gui;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

import org.flashcard.datatypes.Vocab;
import org.flashcard.db.Db;

public class Flashcard {
    private String question;
    private String answer;
    private boolean isAnswerShown = false;

    private JLabel questionLabel;
    private JLabel answerLabel;
    private JTextField answerInput;
    private JButton showAnswerButton;

    private Db db;
    private ArrayList<Vocab> vocabList;

    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;

        vocabList = getVocabList();
    }

    public Flashcard(Db db) {
        this.question = "";
        this.answer = "";
        this.db = db;

        vocabList = getVocabList();
        nextQuestion();
    }



    private ArrayList<Vocab> getVocabList() {
        try {
            return db.getQuestionAndAnswers();
        } catch (SQLException e) {
            System.err.println("Error fetching vocab list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void nextQuestion() {
        if (vocabList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No more questions available.");
        } else {
            Vocab vocab = vocabList.remove(0);
            question = vocab.getQuestion();
            answer = vocab.getAnswer();

            setQuestion(question);
            setAnswer(answer);
        }

        isAnswerShown = false;
    }





    public JPanel show() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        createLabels();
        createTextInput();
        createShowAnswerButton();

        panel.add(questionLabel);
        panel.add(answerLabel);
        panel.add(answerInput);
        panel.add(showAnswerButton);
        

        return panel;
    }

    private void createLabels() {
        this.questionLabel = new JLabel();
        this.answerLabel = new JLabel();

        questionLabel.setText(question);
        questionLabel.setVisible(true);

        answerLabel.setText(answer);
        answerLabel.setVisible(false);
    }

    private void createTextInput() {
        this.answerInput = new JTextField(1);
        answerInput.addActionListener(e -> {
            String userInput = answerInput.getText();
            if (userInput.equals(answer)) {
                nextQuestion();
                answerInput.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect. Try again.");
            }
        });
    }

    private void createShowAnswerButton() {
        this.showAnswerButton = new JButton("Show Answer");
        showAnswerButton.addActionListener(e -> {
            if (isAnswerShown) {
                answerLabel.setVisible(true);
                answerInput.setVisible(false);
                showAnswerButton.setText("Hide Answer");
            } else {
                showAnswerButton.setText("Show Answer");
                answerLabel.setVisible(false);
                answerInput.setVisible(true);
            }

            isAnswerShown = !isAnswerShown;
        });
    }

    public void setQuestion(String question) {
        this.question = question;
        if (questionLabel != null) {
            questionLabel.setText(question);
        }
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        if (answerLabel != null) {
            answerLabel.setText(answer);
        }
    }

}
