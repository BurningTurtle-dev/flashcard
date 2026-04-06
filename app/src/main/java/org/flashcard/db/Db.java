package org.flashcard.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.flashcard.datatypes.Vocab;

public class Db {

	private Connection conn;

	public Db() throws SQLException {
		conn = connect();

		getQuestionAndAnswers(); //TODO remove

	}


	private Connection connect() throws SQLException {
		String url = "jdbc:sqlite:words.db";

		try {
			Connection conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");
			return conn;
		}
		
		catch (SQLException e) {
			System.err.println("Connection failed: " + e.getMessage());
			throw e;
		}
	}


	public ArrayList<Vocab> getQuestionAndAnswers() throws SQLException {
		String getQuestionQuery = "SELECT question,answer FROM german ORDER BY RANDOM()";

		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(getQuestionQuery);

		ArrayList<Vocab> vocabList = new ArrayList<>();

		while (resultSet.next()) {
			String question = resultSet.getString("question");
			String answer = resultSet.getString("answer");

			vocabList.add(new Vocab(question, answer));
		}

		return vocabList;
	}

	public ArrayList<Vocab> getData() throws SQLException {
		String getQuestionQuery = "SELECT question,answer FROM german";

		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(getQuestionQuery);

		ArrayList<Vocab> vocabList = new ArrayList<>();

		while (resultSet.next()) {
			String question = resultSet.getString("question");
			String answer = resultSet.getString("answer");

			vocabList.add(new Vocab(question, answer));
		}

		return vocabList;
	}

	public void updateData(String oldQuestion, String oldAnswer, String newQuestion, String newAnswer) throws SQLException {
		String updateQuery = "UPDATE german SET question = ?, answer = ? WHERE question = ? AND answer = ?";

		try (var pstmt = conn.prepareStatement(updateQuery)) {
			pstmt.setString(1, newQuestion);
			pstmt.setString(2, newAnswer);
			pstmt.setString(3, oldQuestion);
			pstmt.setString(4, oldAnswer);

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				System.out.println("Data updated successfully.");
			} else {
				System.out.println("No matching data found to update.");
			}
		} catch (SQLException e) {
			System.err.println("Error updating data: " + e.getMessage());
			throw e;
		}
	}


	public void addEntry(String question, String answer) throws SQLException {
		String insertQuery = "INSERT INTO german (question, answer) VALUES (?, ?)";

		try (var pstmt = conn.prepareStatement(insertQuery)) {
			pstmt.setString(1, question);
			pstmt.setString(2, answer);

			pstmt.executeUpdate();
			System.out.println("Entry added successfully.");
		} catch (SQLException e) {
			System.err.println("Error adding entry: " + e.getMessage());
			throw e;
		}
	}


	public void deleteEntry(String question, String answer) throws SQLException {
		String deleteQuery = "DELETE FROM german WHERE question = ? AND answer = ?";

		try (var pstmt = conn.prepareStatement(deleteQuery)) {
			pstmt.setString(1, question);
			pstmt.setString(2, answer);

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				System.out.println("Entry deleted successfully.");
			} else {
				System.out.println("No matching entry found to delete.");
			}
		} catch (SQLException e) {
			System.err.println("Error deleting entry: " + e.getMessage());
			throw e;
		}
	}
}
