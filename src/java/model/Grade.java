/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import SQL.Query;
import SQL.Helper;

import metaData.MetaData;

import metaData.grade.GradeGroup;
import metaData.grade.Question;
import metaData.grade.GroupBy;

/**
 *
 * @author aryner
 */
public class Grade extends Model {
	private int id;
	private String grader;
	private Map<String,String> group_meta_data;
	private Map<String,String> question_answers;

	public static final String FILENAME = "_photo_file_name";
	

	public Grade() {}

	public Grade(int id, String grader, Map<String,String> group_meta_data, Map<String,String> question_answers) {
		this.id = id;
		this.grader = grader;
		this.group_meta_data = group_meta_data;
		this.question_answers = question_answers;
	}

	@Override
	public Grade getModel(ResultSet resultSet) {
		try {
			Map<String,String> groupMeta = new HashMap<String,String>();
			Map<String,String> questions = new HashMap<String,String>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colCount = metaData.getColumnCount();
			for(int i=3; i<=colCount; i++) {
				String colName = metaData.getColumnLabel(i);
				if(colName.indexOf("_") == 0) 
					groupMeta.put(colName,resultSet.getString(colName));
				else
					questions.put(colName,resultSet.getString(colName));
			}

			return new Grade(resultSet.getInt("id"),resultSet.getString("grader"),groupMeta,questions);
		}
		catch(SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static ArrayList<Grade> getGrades(GradeGroup group) {
		String query = "SELECT * FROM "+group.getGrade_name();

		return (ArrayList)Query.getModel(query, new Grade());
	}

	public static void createTable(int group_id) {
		String tableName = "grade_"+Query.getField("photo_grade_group","grade_name","id='"+group_id+"'",null).get(0);
		String query = "CREATE TABLE IF NOT EXISTS "+tableName+" ( "+
				"id int unsigned AUTO_INCREMENT,grader varchar(50),";
		String postfix = "PRIMARY KEY(id)) ENGINE=INnoDB";
		ArrayList<GroupBy> grouping = GroupBy.getGroup(group_id);
		ArrayList<Question> questions = Question.getQuestions(group_id);

		for(GroupBy group : grouping) 
			query += group.getPhoto_attribute()+" varchar(40),";
		for(Question question : questions) 
			query += question.getLabel()+" varchar(40),";

		Query.update(query+postfix);
	}

	public static void grade(HttpServletRequest request, Study study, GradeGroup group, User user) {
		String query = "INSERT INTO "+group.getGrade_name()+" ";

		Photo photo = new Photo(study, request.getParameter("photo"));
		String parameters = "(grader";
		String values = "VALUES ('"+user.getName();

		for(int i=0; i<group.groupBySize(); i++) {
			String key = group.getGroupBy(i).getPhoto_attribute();
			String value = photo.getField(key);
			if(key.equals(FILENAME)) value = photo.getName();
			parameters += ", "+key;
			values += "', '"+value;
		}
		for(int i=0; i<group.questionSize(); i++) {
			parameters += ", "+group.getQuestion(i).getLabel();
			values += "', '"+Helper.escape(getAnswer(request, group.getQuestion(i)));
		}
		parameters += ") ";
		values += "')";

		Query.update(query+parameters+values);
	}

	public static String getAnswer(HttpServletRequest request, Question question) {
		String answers = "";
		if(question.getQ_type() == MetaData.CHECKBOX) {
			//get option count, cycle through adding to the result seperating by |
			for(int i=0; i<question.optionSize(); i++) {
				String currBox = request.getParameter(question.getLabel()+"_"+i);
				if(currBox != null) {
					if(answers.length() != 0) 
						answers += "|"+(i+1);
					else 
						answers += (i+1);
				}
			}
			return answers;
		}
		//if its not a checkbox question then just get the value from the request
		answers = request.getParameter(question.getLabel());

		return answers == null ? "" : answers;
	}

	public static ArrayList<String> getCSVLines(GradeGroup category, Study study) {
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<Grade> grades = getGrades(category);
		ArrayList<String> fields = new ArrayList<String>();
		fields.addAll(grades.get(0).getMetaKeys());
		fields.addAll(grades.get(0).getQuestionKeys());

		String currLine = "Grader";
		for(String key : fields) {
			currLine += ", "+key;
		}
		lines.add(currLine);

		for(Grade grade : grades) {
			currLine = grade.getGrader();
			for(String field : fields) {
				if(field.indexOf("_") == 0) currLine += ","+grade.getGroupMetaData(field);
				else currLine += ","+grade.getAnswer(field);
			}
			lines.add(currLine);
		}

		return lines;
	}

	public static ArrayList<Grade> getGrades(String grader, String grade_table) {
		String query = "SELECT * FROM "+grade_table+" WHERE grader='"+grader+"'";
		return (ArrayList)Query.getModel(query, new Grade());
	}

	public ArrayList<String> getMetaKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(group_meta_data.keySet());
		return keys;
	}

	public ArrayList<String> getQuestionKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(question_answers.keySet());
		return keys;
	}

	public String getGroupMetaData(String key) {
		return group_meta_data.get(key);
	}

	public String getAnswer(String key) {
		return question_answers.get(key);
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
	 * @return the grader
	 */
	public String getGrader() {
		return grader;
	}

	/**
	 * @param grader the grader to set
	 */
	public void setGrader(String grader) {
		this.grader = grader;
	}
}
