package org.flashcard.gui;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.flashcard.datatypes.Vocab;
import org.flashcard.db.Db;

public class DbEditor {

    @FunctionalInterface
    public interface CellEditListener {
        void onCellEdited(int row, int column, Object oldValue, Object newValue);
    }

    Db db;
    ArrayList<Vocab> vocabList;
    private CellEditListener cellEditListener;
    private DefaultTableModel tableModel;


    JTable vocabTable;

    public DbEditor(Db db) {
        this.db = db;
        this.vocabList = getDataList();

        createTable();
    }

    public JPanel show() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        createTable();

        JPanel buttonRow = new JPanel();
        buttonRow.add(createAddEntryButton());
        buttonRow.add(createDeleteEntryButton());

        panel.add(buttonRow);
        panel.add(new JScrollPane(vocabTable));

        return panel;
    }

    public void setCellEditListener(CellEditListener cellEditListener) {
        this.cellEditListener = cellEditListener;
    }


    private void createTable() {
        String[] columnNames = { "Question", "Answer"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                String oldQuestion = String.valueOf(getValueAt(row, 0));
                String oldAnswer = String.valueOf(getValueAt(row, 1));
                Object oldValue = getValueAt(row, column);
                super.setValueAt(aValue, row, column);

                String newQuestion = String.valueOf(getValueAt(row, 0));
                String newAnswer = String.valueOf(getValueAt(row, 1));

                try {
                    db.updateData(oldQuestion, oldAnswer, newQuestion, newAnswer);
                    vocabList.set(row, new Vocab(newQuestion, newAnswer));
                } catch (SQLException e) {
                    System.err.println("Error saving edited cell: " + e.getMessage());
                    super.setValueAt(oldValue, row, column);
                }

                if (cellEditListener != null && (oldValue == null || !oldValue.equals(aValue))) {
                    cellEditListener.onCellEdited(row, column, oldValue, aValue);
                }
            }
        };

        vocabTable = new JTable(tableModel);

        //fill table with data
        for (int i = 0; i < vocabList.size(); i++) {
            Vocab vocab = vocabList.get(i);
            tableModel.addRow(new Object[] { vocab.getQuestion(), vocab.getAnswer() });
        }
    }

    private JButton createAddEntryButton() {
        JButton addButton = new JButton("Add Entry");
        addButton.addActionListener(e -> {
            JTextField questionField = new JTextField(20);
            JTextField answerField = new JTextField(20);

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            inputPanel.add(new JLabel("Question"));
            inputPanel.add(questionField);
            inputPanel.add(new JLabel("Answer"));
            inputPanel.add(answerField);

            int result = JOptionPane.showConfirmDialog(
                null,
                inputPanel,
                "Add New Entry",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String question = questionField.getText().trim();
            String answer = answerField.getText().trim();

            if (question.isEmpty() || answer.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Question and answer are required.");
                return;
            }

            try {
                db.addEntry(question, answer);
                vocabList.add(new Vocab(question, answer));
                tableModel.addRow(new Object[] { question, answer });
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to add entry: " + ex.getMessage());
            }
        });

        return addButton;
    }

    private JButton createDeleteEntryButton() {
        JButton deleteButton = new JButton("Delete Entry");
        deleteButton.addActionListener(e -> {
            int selectedViewRow = vocabTable.getSelectedRow();
            if (selectedViewRow < 0) {
                JOptionPane.showMessageDialog(null, "Select a row to delete.");
                return;
            }

            int selectedModelRow = vocabTable.convertRowIndexToModel(selectedViewRow);
            String question = String.valueOf(tableModel.getValueAt(selectedModelRow, 0));
            String answer = String.valueOf(tableModel.getValueAt(selectedModelRow, 1));

            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Delete selected entry?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                db.deleteEntry(question, answer);
                vocabList.remove(selectedModelRow);
                tableModel.removeRow(selectedModelRow);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to delete entry: " + ex.getMessage());
            }
        });

        return deleteButton;
    }


    private ArrayList<Vocab> getDataList() {
        try {
            return db.getData();
        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    
}
