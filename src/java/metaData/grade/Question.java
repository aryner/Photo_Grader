/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData.grade;

import java.sql.*;
import SQL.*;
import model.*;
import java.util.*;
import metaData.*;
import utilities.*;

/**
 *
 * @author aryner
 */
public class Question extends Model{
	private int id;
	private int grade_group_id;
	private String label;
	private String question;
	private int q_type;
	private ArrayList<String> options;

	public Question() {}

	public Question(int id, int grade_group_id, String label, String question, int q_type) {
		this.id = id;
		this.grade_group_id = grade_group_id;
		this.label = label;
		this.question = question;
		this.q_type = q_type;

		if(q_type == MetaData.TEXT) options = null;
		else options = getOptions();
	}

	public static ArrayList<Question> getQuestions(int group_id) {
		String query = "SELECT * FROM question WHERE grade_group_id = "+group_id;

		return (ArrayList)Query.getModel(query, new Question());
	}

	@Override
	public Question getModel(ResultSet resultSet) {
		try {
			return new Question(resultSet.getInt("id"),resultSet.getInt("grade_group_id"),
					    resultSet.getString("label"),resultSet.getString("question"),
					    resultSet.getInt("q_type"));
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public String getHtml() {
		switch (this.q_type) {
			case MetaData.TEXT:
				return Tools.getQuestionHtml(this.question, "<input type='text' name='"+this.label+"'>");
			case MetaData.RADIO:
				return Tools.getQuestionHtml(this.question, getRadioHtml());
			default:
				return Tools.getQuestionHtml(this.question, getCheckHtml());
		}
	}

	private String getRadioHtml() {
		String html = "";
		for(String option : this.options) {
			html += "<input type='radio' name='"+this.label+"' value='"+option+"'> "+option+"<br>";
		}
		return html;
	}

	private String getCheckHtml() {
		String html = "";
		for(String option : this.options) {
			html += "<input type='checkbox' name='"+this.label+"_"+option+"' value='"+option+"'> "+option+"<br>";
		}
		return html;
	}

	public String getOption(int index) {
		return options.get(index);
	}

	public int optionSize() {
		return options.size();
	}

	private ArrayList<String> getOptions() {
		return (ArrayList)Query.getField("check_radio_option","value",
						 "photo_data_id="+this.id+" AND meta_grade="+MetaData.GRADE,
						 null);
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

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
