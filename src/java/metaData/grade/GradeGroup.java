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
import model.Study;
import model.Photo;
import model.Grade;
import model.Rank;
import model.Compare;

import SQL.Query;

import utilities.Tools;

/**
 *
 * @author aryner
 */
public class GradeGroup extends Model {
	private int id;
	private int study_id;
	private String grade_name;
	private String name;
	private int grade_rank;
	private int repeats;
	private ArrayList<GroupBy> groupBy;
	private ArrayList<Question> questions;

	public static final int GRADE = 0;
	public static final int RANK = 1;
	public static final int COMPARE = 2;

	public static final String TABLE_NAME = "photo_grade_group";
	public static final String FILENAME = "_photo_file_name";

	public GradeGroup() {}

	public GradeGroup(int id) {
		String query = "SELECT * FROM photo_grade_group WHERE id="+id;

		GradeGroup temp = (GradeGroup)Query.getModel(query,new GradeGroup()).get(0);

		this.id = temp.getId();
		this.study_id = temp.getStudy_id();
		this.grade_name = temp.getGrade_name();
		this.name = temp.getName();
		this.grade_rank = temp.getGrade_rank();
		this.repeats = temp.getRepeats();

		setGroupBy();
		setQuestions();
	}

	public GradeGroup(int id, int study_id, String grade_name, String name, int grade_rank, int repeats) {
		this.id = id;
		this.study_id = study_id;
		this.grade_name = grade_name;
		this.name = name;
		this.grade_rank = grade_rank;
		this.repeats = repeats;

		setGroupBy();
		setQuestions();
	}

	@Override
	public GradeGroup getModel(ResultSet resultSet) {
		try {
			return new GradeGroup(resultSet.getInt("id"),resultSet.getInt("study_id"),
					      resultSet.getString("grade_name"),resultSet.getString("name"),
					      resultSet.getInt("grade_rank"),resultSet.getInt("repeats"));
		}
		catch(SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static ArrayList<String> createGradeGroup(HttpServletRequest request, Study study, int type) {
		ArrayList<String> errors = new ArrayList<String>();

		String newName = request.getParameter("name");
		ArrayList<String> usedNames = (ArrayList)Query.getField(GradeGroup.TABLE_NAME, "name", "study_id="+study.getId()+" AND grade_rank="+type,null);
		if(Tools.contains(usedNames, newName)) {
			errors.add("That category name has already been used");
			return errors;
		}

		usedNames = (ArrayList)Query.getField(GradeGroup.TABLE_NAME, "grade_name", null,null);
		String grade_name = Tools.generateTableName(type==GRADE?"grade_":type==RANK?"rank_":"compare_", usedNames);
		String query = "INSERT INTO photo_grade_group (study_id, name, grade_name, grade_rank, repeats) VALUES ('"+study.getId()+
			        "', '"+newName+"', '"+grade_name+"', '"+(type==GRADE?GRADE:type==RANK?RANK:COMPARE)+
				"', '"+request.getParameter("repeats")+"')";
		Query.update(query);

		int groupId = Integer.parseInt(Query.getField(
			GradeGroup.TABLE_NAME,
			"id",
			"study_id="+study.getId()+" AND name='"+newName+"' AND grade_rank="+(type==GRADE?GRADE:type==RANK?RANK:COMPARE),null).get(0)+"");
		createGroup(groupId, study.getPhoto_attribute_table_name(), request);
		if(type==GRADE) {
			Question.createQuestions(groupId, request);
			Grade.createTable(groupId);
		} else if(type==RANK){
			Rank.createTable(groupId);
		} else {
			Compare.generateRankWithin(request,groupId);
			Compare.createTable(groupId);
		}

		return errors;
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

	public static ArrayList<String> getUsedNames(int studyId, int type) {
		String query = "SELECT * FROM photo_grade_group WHERE study_id='"+studyId+"' AND grade_rank='"+type+"'";
		ArrayList<GradeGroup> groups = (ArrayList)Query.getModel(query,new GradeGroup());
		ArrayList<String> usedNames = new ArrayList<String>();

		for(GradeGroup group : groups) {
			usedNames.add(group.getName());
		}

		return usedNames;
	}

	private void setGroupBy() {
		this.setGroupBy(GroupBy.getGroup(this.id));
	}

	private void setQuestions() {
		this.questions = Question.getQuestions(this.id);
	}

	public Question getQuestion(int index) {
		return questions.get(index);
	}

	public GroupBy getGroupBy(int index) {
		return getGroupBy().get(index);
	}

	public int groupBySize() {
		return getGroupBy().size();
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

	/**
	 * @return the grade_rank
	 */
	public int getGrade_rank() {
		return grade_rank;
	}

	/**
	 * @param grade_rank the grade_rank to set
	 */
	public void setGrade_rank(int grade_rank) {
		this.grade_rank = grade_rank;
	}

	/**
	 * @return the groupBy
	 */
	public ArrayList<GroupBy> getGroupBy() {
		return groupBy;
	}

	/**
	 * @param groupBy the groupBy to set
	 */
	public void setGroupBy(ArrayList<GroupBy> groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * @return the repeats
	 */
	public int getRepeats() {
		return repeats;
	}

	/**
	 * @param repeates the repeats to set
	 */
	public void setRepeats(int repeates) {
		this.repeats = repeates;
	}
	
}
