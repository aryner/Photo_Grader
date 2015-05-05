/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData.grade;

import java.sql.*;
import model.*;

/**
 *
 * @author aryner
 */
public class Question extends Model{
	private int id;
	private int grade_group_id;
	private String question;
	private int q_type;

	public Question(int id, int grade_group_id, String question, int q_type) {
		this.id = id;
		this.grade_group_id = grade_group_id;
		this.question = question;
		this.q_type = q_type;
	}

	@Override
	public Question getModel(ResultSet resultSet) {
		try {
			return new Question(resultSet.getInt("id"),resultSet.getInt("grade_group_id"),
					    resultSet.getString("question"),resultSet.getInt("q_type"));
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the grade_group_id
	 */
	public int getGrade_group_id() {
		return grade_group_id;
	}

	/**
	 * @param grade_group_id the grade_group_id to set
	 */
	public void setGrade_group_id(int grade_group_id) {
		this.grade_group_id = grade_group_id;
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the q_type
	 */
	public int getQ_type() {
		return q_type;
	}

	/**
	 * @param q_type the q_type to set
	 */
	public void setQ_type(int q_type) {
		this.q_type = q_type;
	}
}
