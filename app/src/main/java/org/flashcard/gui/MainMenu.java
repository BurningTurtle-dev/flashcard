package org.flashcard.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainMenu {
    private final Runnable onStartFlashcard;
    private final Runnable onEditDatabase;
    
    public MainMenu(Runnable onStartFlashcard, Runnable onEditDatabase) {
        this.onStartFlashcard = onStartFlashcard;
        this.onEditDatabase = onEditDatabase;
    }

    public JPanel show() {
        JPanel panel = new JPanel();
        JButton startFlashcardButton = new JButton("Start Flashcard");
        startFlashcardButton.addActionListener(e -> onStartFlashcard.run());

        JButton editDbButton = new JButton("Edit Database");
        editDbButton.addActionListener(e -> onEditDatabase.run());

        panel.add(startFlashcardButton);
        panel.add(editDbButton);

        return panel;
    }
}
