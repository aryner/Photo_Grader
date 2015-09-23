/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData.grade;

import java.util.*;
import model.*;
import java.sql.*;
import SQL.*;

/**
 *
 * @author aryner
 */
public class GradeGroup extends Model {
	private int id;
	private int study_id;
	private String grade_name;
	private String name;
	private ArrayList<GroupBy> groupBy;
	private ArrayList<Question> questions;

	public static final String TABLE_NAME = "photo_grade_group";

	public GradeGroup() {}

	public GradeGroup(int id) {
		String query = "SELECT * FROM photo_grade_group WHERE id="+id;

		GradeGroup temp = (GradeGroup)Query.getModel(query,new GradeGroup()).get(0);

		this.id = temp.getId();
		this.study_id = temp.getStudy_id();
		this.grade_name = temp.getGrade_name();
		this.name = temp.getName();

		setGroupBy();
		setQuestions();
	}

	public GradeGroup(int id, int study_id, String grade_name, String name) {
		this.id = id;
		this.study_id = study_id;
		this.grade_name = grade_name;
		this.name = name;

		setGroupBy();
		setQuestions();
	}

	@Override
	public GradeGroup getModel(ResultSet resultSet) {
		try {
			return new GradeGroup(resultSet.getInt("id"),resultSet.getInt("study_id"),
					      resultSet.getString("grade_name"),resultSet.getString("name"));
		}
		catch(SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static ArrayList<String> getUsedNames(int studyId) {
		String query = "SELECT * FROM photo_grade_group WHERE study_id='"+studyId+"'";
		ArrayList<GradeGroup> groups = (ArrayList)Query.getModel(query,new GradeGroup());
		ArrayList<String> usedNames = new ArrayList<String>();

		for(GradeGroup group : groups) {
			usedNames.add(group.getName());
		}

		return usedNames;
	}

	private void setGroupBy() {
		this.groupBy = GroupBy.getGroup(this.id);
	}

	private void setQuestions() {
		this.questions = Question.getQuestions(this.id);
	}

	public Question getQuestion(int index) {
		return questions.get(index);
	}

	public GroupBy getGroupBy(int index) {
		return groupBy.get(index);
	}

	public int groupBySize() {
		return groupBy.size();
	}

	public int questionSize() {
		return questions.size();
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
	 * @return the study_id
	 */
	public int getStudy_id() {
		return study_id;
	}

	/**
	 * @param study_id the study_id to set
	 */
	public void setStudy_id(int study_id) {
		this.study_id = study_id;
	}

	/**
	 * @return the grade_name
	 */
	public String getGrade_name() {
		return grade_name;
	}

	/**
	 * @param grade_name the grade_name to set
	 */
	public void setGrade_name(String grade_name) {
		this.grade_name = grade_name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
