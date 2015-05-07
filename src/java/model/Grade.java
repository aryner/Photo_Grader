/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import SQL.*;
import metaData.grade.*;
import metaData.*;
import java.sql.*;

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

	public static void createGroup(int group_id, String attr_table_name, HttpServletRequest request) {
		int groupOptionCount = Integer.parseInt(request.getParameter("groupOptionCount"));
		ArrayList<String> columns = Photo.getMetaDataKeys(attr_table_name);
		ArrayList<String> attributes = new ArrayList<String>();

		for(int i=-1; i<groupOptionCount; i++) {
			if(request.getParameter("groupBy_"+i)!=null) {
				if(i>=0) 
					attributes.add(columns.get(i));
				else 
					attributes.add(FILENAME);
			}
		}

		String query = "INSERT INTO group_by (grade_group_id, photo_attribute) VALUES ";
		for(int i=0; i<attributes.size(); i++) {
			if(i>0) query += ", ";
			query += "('"+group_id+"', '"+attributes.get(i)+"')";
		}

		Query.update(query);
	}

	public static void createQuestions(int group_id, HttpServletRequest request) {
		int questionCount = Integer.parseInt(request.getParameter("questionCount"));
		ArrayList<String> questions = new ArrayList<String>();
		ArrayList<Integer> types = new ArrayList<Integer>();
		ArrayList<String> labels = new ArrayList<String>();

		for(int i=0; i<questionCount; i++) {
			questions.add(request.getParameter("question_"+i));
			types.add(Integer.parseInt(request.getParameter("type_"+i)));
			labels.add(request.getParameter("label_"+i));
		}

		String query = "INSERT INTO question (grade_group_id, label, question, q_type) VALUES ";
		for(int i=0; i<types.size(); i++) {
			if(i>0) query += ", ";
			query += "('"+group_id+"', '"+labels.get(i)+"', '"+questions.get(i)+"', '"+types.get(i)+"')";
		}

		Query.update(query);
		createOptions(group_id, questions, types, request);
	}

	private static void createOptions(int group_id, ArrayList<String> questions, ArrayList<Integer> types, HttpServletRequest request) {
		String query = "INSERT INTO check_radio_option (photo_data_id, value, meta_grade) VALUES ";
		String postfix = "";

		for(int i=0; i<types.size(); i++) {
			if(types.get(i)!=MetaData.TEXT) {
				String questionId = ""+Query.getField("question","id",
						"question='"+questions.get(i)+"' AND grade_group_id='"+group_id+"'",null).get(0);
				if(postfix.length() > 0) postfix += ", ";

				int optionCount = Integer.parseInt(request.getParameter("option_count_"+i));
				//do this for each option of this question
				for(int j=0; j<optionCount-1; j++) {
					if(j>0) postfix += ", ";
					postfix += "('"+questionId+"', '"+request.getParameter("option_"+i+"_"+(j+1))+"', '"+MetaData.GRADE+"')";
				}
			}
		}

		if(postfix.length() > 0) Query.update(query+postfix);
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
			parameters += ", "+key;
			values += "', '"+photo.getField(key);
		}
		for(int i=0; i<group.questionSize(); i++) {
			parameters += ", "+group.getQuestion(i).getLabel();
			values += "', '"+getAnswer(request, group.getQuestion(i));
		}
		parameters += ") ";
		values += "')";

		Query.update(query+parameters+values);
	}

	public static String getAnswer(HttpServletRequest request, Question question) {
		if(question.getQ_type() == MetaData.CHECKBOX) {
		}
		//if its not a checkbox question then just get the value from the request
		return request.getParameter(question.getLabel());
	}

	public static ArrayList<Grade> getGrades(String grader, String grade_table) {
		String query = "SELECT * FROM "+grade_table+" WHERE grader='"+grader+"'";
		return (ArrayList)Query.getModel(query, new Grade());
	}

	public static String generateQuestion(Question question) {
		return "<div class='meta-col'>"+question.getHtml()+"</div>";
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
