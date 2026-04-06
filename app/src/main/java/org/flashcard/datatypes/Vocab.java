package org.flashcard.datatypes;

public class Vocab {
    private String question;
    private String answer;

    public Vocab(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
