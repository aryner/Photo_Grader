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
	private int constraints;
	private ArrayList<String> options;
	private int defaultIndex;

	public static final int MANDATORY = 0;
	public static final int OPTIONAL = -1;
	public static final int IS_NOT_DEFAULT = 0;
	public static final int IS_DEFAULT = 1;

	public Question() {}

	public Question(int id, int grade_group_id, String label, String question, int q_type, int constraints) {
		this.id = id;
		this.grade_group_id = grade_group_id;
		this.label = label;
		this.question = question;
		this.q_type = q_type;
		this.constraints = constraints;

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
					    resultSet.getInt("q_type"),resultSet.getInt("constraints"));
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public String getHtml(int index) {
		return Tools.getQuestionHtml(this.question, this.q_type == MetaData.TEXT ? getTextHtml() :
					     this.q_type == MetaData.RADIO ? getRadioHtml(index) : getCheckHtml());
	}

	private String getTextHtml() {
		return "<input type='text' title='"+this.q_type+"' name='"+this.label+"'>";
	}

	private String getRadioHtml(int questionIndex) {
		String html = "";
		int index = 0;
		for(String option : this.options) {
			html += "<span style='font-family:Courier' name='"+questionIndex+"_"+index+
				"'></span><input type='radio' name='"+this.label+"' title='"+questionIndex+
				"_"+index+"' value='"+option+"'"+(index == defaultIndex ? " checked='true'" : "")+"> "+option+"<br>";
			index++;
		}
		return html;
	}

	private String getCheckHtml() {
		String html = "";
		int index = 0;
		for(String option : this.options) {
			html += "<input type='checkbox' name='"+this.label+"_"+index+"' value='"+option+
				"'"+(index == defaultIndex ? " checked='true'" : "")+"> "+option+"<br>";
			index++;
		}
		return html;
	}

	public String getOption(int index) {
		return options.get(index);
	}

	public int optionSize() {
		return options != null ? options.size() : 0;
	}

	private ArrayList<String> getOptions() {
		setDefault();
		return (ArrayList)Query.getField("check_radio_option","value",
						 "photo_data_id="+this.id+" AND meta_grade="+MetaData.GRADE,
						 "id");
	}

	private void setDefault() {
		ArrayList<Integer> defaults = (ArrayList)Query.getField("check_radio_option","defaultCheck",
								        "photo_data_id="+this.id+" AND meta_grade="+MetaData.GRADE,
								         "id");
		defaultIndex = defaults.indexOf(IS_DEFAULT);
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

	/**
	 * @return the constraints
	 */
	public int getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints the constraints to set
	 */
	public void setConstraints(int constraints) {
		this.constraints = constraints;
	}

	/**
	 * @return the defaultIndex
	 */
	public int getDefaultIndex() {
		return defaultIndex;
	}

	/**
	 * @param defaultIndex the defaultIndex to set
	 */
	public void setDefaultIndex(int defaultIndex) {
		this.defaultIndex = defaultIndex;
	}
}
