package org.flashcard.gui;

import javax.swing.*;

import org.flashcard.db.Db;

import java.awt.*;
import java.sql.SQLException;

public class Gui {
    private JFrame frame;
    private JPanel flashcardPanel;
    private JPanel dbEditPanel;
    private JPanel mainMenuPanel;

    private Db db;

    enum Mode {
        FLASHCARD,
        DBEDIT,
        MAINMENU
    }

    private Mode currentMode = Mode.MAINMENU;

    public Gui() {
        //init
        this.db = connectToDb();

        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        frame = new JFrame("Flashcard Learning App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setLayout(new BorderLayout());

        renderCurrentMode();

        frame.pack();
        frame.setVisible(true);
    }

    private void setMode(Mode mode) {
        currentMode = mode;
        renderCurrentMode();
    }

    private void renderCurrentMode() {
        frame.getContentPane().removeAll();

        switch (currentMode) {
            case DBEDIT:
                DbEditor dbEditor = new DbEditor(db);
                dbEditPanel = dbEditor.show();
                frame.add(dbEditPanel, BorderLayout.CENTER);
                break;
            case FLASHCARD:
                Flashcard flashcard = new Flashcard(db);
                flashcardPanel = flashcard.show();
                frame.add(flashcardPanel, BorderLayout.CENTER);
                break;
            case MAINMENU:
                MainMenu mainMenu = new MainMenu(
                    () -> setMode(Mode.FLASHCARD),
                    () -> setMode(Mode.DBEDIT)
                );
                mainMenuPanel = mainMenu.show();
                frame.add(mainMenuPanel, BorderLayout.CENTER);
                break;
        }

        frame.revalidate();
        frame.repaint();
        frame.pack();
    }


    private Db connectToDb() {
        try {
            db = new Db();  
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
        return db;
    }
}
