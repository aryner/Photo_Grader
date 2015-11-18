/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData.grade;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import model.Model;

import SQL.Query;
import SQL.Helper;

import metaData.MetaData;

import utilities.Tools;

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

	public static final int TEXT = 1;
	public static final int INTEGER = 2;
	public static final int DECIMAL = 3;

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

	public static void createQuestions(int group_id, HttpServletRequest request) {
		int questionCount = Integer.parseInt(request.getParameter("questionCount"));
		ArrayList<String> questions = new ArrayList<String>();
		ArrayList<Integer> types = new ArrayList<Integer>();
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<String> constraints = new ArrayList<String>();

		for(int i=0; i<questionCount; i++) {
			questions.add(request.getParameter("question_"+i));
			types.add(Integer.parseInt(request.getParameter("type_"+i)));
			labels.add(request.getParameter("label_"+i));
			int sign = (request.getParameter("constraints_"+i).equals("0")?1:-1);
			if(types.get(types.size()-1) == MetaData.TEXT) {
				String type = request.getParameter("text_option_"+i);
				if(type.equals("text")) {
					constraints.add((sign*TEXT)+"");
				} else if (type.equals("int")) {
					constraints.add((sign*INTEGER)+"");
				} else {
					constraints.add((sign*DECIMAL)+"");
				}
			}
			else {
				constraints.add(""+sign);
			}
		}

		String query = "INSERT INTO question (grade_group_id, label, question, q_type, constraints) VALUES ";
		for(int i=0; i<types.size(); i++) {
			if(i>0) query += ", ";
			query += "('"+group_id+"', '"+labels.get(i)+"', '"+Helper.escape(questions.get(i))+"', '"+types.get(i)+"', '"+constraints.get(i)+"')";
		}

		Query.update(query);
		createOptions(group_id, questions, types, request);
//		createConstraints(group_id, questions, request);
	}

	private static void createConstraints(int group_id, ArrayList<String> questions, HttpServletRequest request) {
		/*
		String query = "SELECT * FROM question WHERE grade_group_id="+group_id+" AND "+
			       "constraints="+MANDATORY;
		ArrayList<Question> questionData = (ArrayList)Query.getModel(query, new Question());
		*/
		//TODO
		createSelections();
	}

	private static void createOptions(int group_id, ArrayList<String> questions, ArrayList<Integer> types, HttpServletRequest request) {
		String query = "INSERT INTO check_radio_option (photo_data_id, value, meta_grade, defaultCheck) VALUES ";
		String postfix = "";

		for(int i=0; i<types.size(); i++) {
			if(types.get(i)!=MetaData.TEXT) {
				String questionId = ""+Query.getField("question","id",
						"question='"+Helper.escape(questions.get(i))+"' AND grade_group_id='"+group_id+"'",null).get(0);
				if(postfix.length() > 0) postfix += ", ";

				int optionCount = Integer.parseInt(request.getParameter("option_count_"+i));
				int defaultIndex = request.getParameter("default_"+i) != null ? Integer.parseInt(request.getParameter("default_"+i)) : -1;
				//do this for each option of this question
				for(int j=0; j<optionCount-1; j++) {
					if(j>0) postfix += ", ";
					postfix += "('"+questionId+"', '"+Helper.escape(request.getParameter("option_"+i+"_"+(j+1)))+"', '"+MetaData.GRADE+
						   "', '"+((defaultIndex == j) ? Question.IS_DEFAULT : Question.IS_NOT_DEFAULT)+"')";
				}
			}
		}

		if(postfix.length() > 0) Query.update(query+postfix);
	}

	private static void createSelections() {
		//TODO
	}

	public String getHtml(int index) {
		return Tools.getQuestionHtml(this.question, this.q_type == MetaData.TEXT ? getTextHtml() :
					     this.q_type == MetaData.RADIO ? getRadioHtml(index) : getCheckHtml(index));
	}

	private String getTextHtml() {
		return "<input type='text' title='"+this.q_type+"' name='"+this.label+"' class='constraint_"+Math.abs(this.constraints)+"'>";
	}

	private String getRadioHtml(int questionIndex) {
		String html = "";
		int index = 0;
		for(String option : this.options) {
			html += "<span style='font-family:Courier' name='"+questionIndex+"_"+index+
				"' title='radio'></span><input type='radio' name='"+this.label+"' title='"+questionIndex+
				"_"+index+"' value='"+option+"'"+(index == defaultIndex ? " checked='true'" : "")+"> "+option+"<br>";
			index++;
		}
		return html;
	}

	private String getCheckHtml(int questionIndex) {
		String html = "";
		int index = 0;
		for(String option : this.options) {
			html += "<span style='font-family:Courier' name='"+questionIndex+"_"+index+
				"' title='check'></span><input type='checkbox' name='"+this.label+"_"+index+"' "+
				"title='"+questionIndex+"_"+index+"' value='"+option+
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
