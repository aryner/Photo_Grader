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

	public static ArrayList<Grade> getGrades(GradeGroup group, User user) {
		String query = "SELECT * FROM "+group.getGrade_name();
		if(user != null && !user.isStudy_coordinator()) {
			query += " WHERE grader='"+user.getName()+"'";
		}

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
			query += group.getPhoto_attribute()+" varchar(100),";
		for(Question question : questions) 
			query += question.getLabel()+" varchar(100),";

		Query.update(query+postfix);
	}

	public static void grade(HttpServletRequest request, Study study, GradeGroup group, User user) {
		Photo photo = new Photo(study, request.getParameter("photo"));
		boolean repeat = request.getParameter("repeat").equals("true");
		ArrayList update = hasBeenGraded(study,group,user,photo,repeat);

		if(update.isEmpty()) {
			newGrade(request, study, group, user, photo);
		} else {
			updateGrade(request, group, (Grade)update.get(0));
		}
	}

	private static void updateGrade(HttpServletRequest request, GradeGroup group, Grade grade) {
		String query = "UPDATE "+group.getGrade_name()+" SET ";
		for(int i=0; i<group.questionSize(); i++) {
			if(i>0) { query += ", "; }
			query += group.getQuestion(i).getLabel()+"='"+Helper.escape(getAnswer(request, group.getQuestion(i)))+"'";
		}
		query += " WHERE id="+grade.getId();

		Query.update(query);
	}
	
	private static void newGrade(HttpServletRequest request, Study study, GradeGroup group, User user, Photo photo) {
		//if request notes repeat, take appropriate actions
		String query = "INSERT INTO "+group.getGrade_name()+" ";

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

	public static class GradeCounts {
		private int total_grades;
		private int graded;

		public GradeCounts(String photo_table, String grader, GradeGroup group) {
			String query = "SELECT * FROM "+photo_table+" GROUP BY ";
			for(int i=0; i<group.groupBySize(); i++) {
				if(i>0) query += ", ";
				String attribute = group.getGroupBy(i).getPhoto_attribute();
				query += attribute.equals(Grade.FILENAME) ? "name" : attribute;
			}
			this.total_grades = Query.getModel(query,new Photo()).size();

			query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+grader+"'";
			this.graded= Query.getModel(query,new Grade()).size();
		}

		/**
		 * @return the total_grades
		 */
		public int getTotal_grades() {
			return total_grades;
		}

		/**
		 * @param total_grades the total_grades to set
		 */
		public void setTotal_grades(int total_grades) {
			this.total_grades = total_grades;
		}

		/**
		 * @return the graded
		 */
		public int getGraded() {
			return graded;
		}

		/**
		 * @param graded the graded to set
		 */
		public void setGraded(int graded) {
			this.graded = graded;
		}
	}

	public static ArrayList hasBeenGraded(Study study, GradeGroup group, User user, Photo photo,boolean repeat) {
		if(repeat) { return new ArrayList(); }
		String query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+user.getName()+"'";
		for(int i=0; i<group.groupBySize(); i++) {
			String key = group.getGroupBy(i).getPhoto_attribute();
			String value = photo.getField(key);
			if(key.equals(FILENAME)) value = photo.getName();
			query += " AND "+key+"='"+value+"'";
		}

		return Query.getModel(query,new Grade());
	}

	public static void removeCategory(HttpServletRequest request, Study study) {
		String query = "SELECT * FROM photo_grade_group WHERE grade_rank=0 AND study_id="+study.getId()+
				" AND name='"+request.getParameter("category")+"'";
		GradeGroup group = (GradeGroup)Query.getModel(query,new GradeGroup()).get(0);
		query = "SELECT * FROM question WHERE grade_group_id="+group.getId();
		ArrayList<Question> questions = (ArrayList)Query.getModel(query,new Question());
		query = "DELETE FROM check_radio_option WHERE ";
		String postfix = "";
		for(Question q : questions) {
			if(postfix.length() > 0) { postfix += " OR "; }
			postfix += "photo_data_id="+q.getId();
		}
		if(postfix.trim().length() > 0) { 
			Query.update(query+postfix); 
		}
		query = "DELETE FROM question WHERE grade_group_id="+group.getId();
		Query.update(query);
		query = "DELETE FROM group_by WHERE grade_group_id="+group.getId();
		Query.update(query);
		query = "DROP TABLE "+group.getGrade_name();
		Query.update(query);
		query = "DELETE FROM photo_grade_group WHERE id="+group.getId();
		Query.update(query);
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

	public static ArrayList<String> getCSVLines(GradeGroup category, Study study, User user) {
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<Grade> grades = getGrades(category, user);
		ArrayList<String> fields = new ArrayList<String>();
		if(grades.isEmpty()) { return null; }
		fields.addAll(grades.get(0).getMetaKeys());
		fields.addAll(grades.get(0).getQuestionKeys());

		String currLine = "Grader";
		for(String key : fields) {
			currLine += ", "+key;
		}
		for(int i=0; i<category.questionSize(); i++) {
			Question question = category.getQuestion(i);
			if(question.getQ_type() == MetaData.CHECKBOX) {
				currLine+= " ("+question.getLabel()+" : ";
				int optionSize = question.optionSize();
				for(int j=0; j<optionSize; j++) {
					if(j>0) { currLine += " | "; }
					currLine += (j+1)+"="+question.getOption(j);
				}
				currLine += ")";
			}
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
