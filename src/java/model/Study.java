/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.File;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import SQL.Query;
import SQL.Helper;

import utilities.Tools;

import metaData.TableMetaData;
import metaData.ManualMetaData;
import metaData.MetaData;

import metaData.grade.GradeGroup;
import metaData.grade.Question;

/**
 *
 * @author aryner
 */
public class Study extends Model {
	private int id;
	private String name;
	private String photo_attribute_table_name;

	public Study() {}

	public Study(
		int id, String name, 
		String photo_attribute_table_name
	) {
		this.id = id;
		this.name = name;
		this.photo_attribute_table_name = photo_attribute_table_name;
	}

	public static Study getStudyByName(String name) {
		String query = "SELECT * FROM STUDY WHERE name='"+Helper.process(name)+"'";
		return (Study)Query.getModel(query,new Study()).get(0);
	}

	@Override
	public Study getModel(ResultSet resultSet) {
		try {
			return new Study(
				resultSet.getInt("id"),Helper.unprocess(resultSet.getString("name")),
				resultSet.getString("photo_attribute_table_name")
			);
		} catch(SQLException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public static Study createStudy(HttpServletRequest request) {
		String name = request.getParameter("studyName");

		ArrayList<String> usedNames = (ArrayList)Query.getField("study", "photo_attribute_table_name", null,null);
		String photo_attribute_table_name = Tools.generateTableName("photo_attribute_table_name_", usedNames);

		String newStudy = "INSERT INTO study (name, photo_attribute_table_name) VALUES ('"+Helper.process(name)+
				  "', '"+photo_attribute_table_name+"')";
		Query.update(newStudy);

		newStudy = "SELECT * FROM study WHERE name='"+Helper.process(name)+"'";
		return (Study)Query.getModel(newStudy, new Study()).get(0);
	}

	public static String removeStudy(HttpServletRequest request, User user) {
		//check for password match
		String query = "SELECT * FROM user WHERE name='"+Helper.process(request.getParameter("userName"))+"' AND "+
				"password=MD5('"+request.getParameter("password")+"')";
		ArrayList<User> users = (ArrayList)Query.getModel(query, new User());
		if(users.isEmpty()) { return "That user name and password combination was not correct"; }
		if(user.getId() != users.get(0).getId()) { return "That user name and password combination was not correct"; }
		//get study
		query = "SELECT * FROM study WHERE name='"+Helper.process(request.getParameter("studyName"))+"'";
		Study study = (Study)Query.getModel(query,new Study()).get(0);

		//delete all meta data (name,manual,table)
		query = "DELETE FROM photo_data_by_manual WHERE study_id="+study.getId();
		Query.update(query);
		query = "DELETE FROM photo_data_by_name WHERE study_id="+study.getId();
		Query.update(query);
		query = "DELETE FROM photo_data_by_table WHERE study_id="+study.getId();
		Query.update(query);
		//delete all photos
		query = "SELECT * FROM "+study.getPhoto_attribute_table_name();
		ArrayList<Photo> photos = (ArrayList)Query.getModel(query,new Photo());
		String path = "";
		for (Photo photo : photos) {
			new File(photo.getPath()+photo.getName()).delete();
			path = photo.getPath();
		}
		new File(path).delete();
		//drop photo table
		query = "DROP TABLE "+study.getPhoto_attribute_table_name();
		Query.update(query);
		//get all groups
		query = "SELECT * FROM photo_grade_group WHERE study_id="+study.getId();
		ArrayList<GradeGroup> groups = (ArrayList)Query.getModel(query, new GradeGroup());
		//delete all group by and ranked_within, get all questions and drop all categories
		String groupQuery = "";
		ArrayList<Question> questions = new ArrayList<Question>();
		for(GradeGroup group : groups) {
			query = "DROP TABLE "+group.getGrade_name();
			Query.update(query);
			query = "DELETE FROM group_by WHERE grade_group_id="+group.getId();
			Query.update(query);
			query = "DELETE FROM ranked_within WHERE grade_group_id="+group.getId();
			Query.update(query);
			query = "SELECT * FROM question WHERE grade_group_id="+group.getId();
			questions.addAll((ArrayList)Query.getModel(query,new Question()));
			if(!groupQuery.equals("")) { groupQuery += " OR "; }
			groupQuery += " id = "+group.getId();
		}
		//delete check radios
		String questionQuery = "";
		for(Question question : questions) {
			query = "DELETE FROM check_radio_option WHERE photo_data_id="+question.getId();
			Query.update(query);
			if(!questionQuery.equals("")) { questionQuery += " OR "; }
			questionQuery += "id = "+question.getId();
		}
		//delete questions
		if(!questionQuery.equals("")) {
			Query.update("DELETE FROM question WHERE "+questionQuery);
		}
		//delete all groups
		if(!groupQuery.equals("")) {
			Query.update("DELETE FROM photo_grade_group WHERE "+groupQuery);
		}
		//delete study
		query = "DELETE FROM study WHERE id="+study.getId();
		Query.update(query);
		return null;
	}

	public String getPhotoNumber() {
		return photo_attribute_table_name.substring(photo_attribute_table_name.lastIndexOf("_")+1);
	}

	public int getGradeGroupId(String name) {
		return Integer.parseInt(""+Query.getField(GradeGroup.TABLE_NAME,"id","study_id="+this.id+" AND name='"+name+"' AND grade_rank="+GradeGroup.GRADE,null).get(0));
	}

	public ArrayList<String> getGradeCategoryNames() {
		return (ArrayList)Query.getField(GradeGroup.TABLE_NAME,"name","study_id="+this.id+" AND grade_rank="+GradeGroup.GRADE,null);
	}

	public int getRankGroupId(String name) {
		return Integer.parseInt(""+Query.getField(GradeGroup.TABLE_NAME,"id","study_id="+this.id+" AND name='"+name+"' AND grade_rank="+GradeGroup.RANK,null).get(0));
	}

	public ArrayList<String> getRankCategoryNames() {
		return (ArrayList)Query.getField(GradeGroup.TABLE_NAME,"name","study_id="+this.id+" AND grade_rank="+GradeGroup.RANK,null);
	}

	public int getCompareGroupId(String name) {
		return Integer.parseInt(""+Query.getField(GradeGroup.TABLE_NAME,"id","study_id="+this.id+" AND name='"+name+"' AND grade_rank="+GradeGroup.COMPARE,null).get(0));
	}

	public ArrayList<String> getCompareCategoryNames() {
		return (ArrayList)Query.getField(GradeGroup.TABLE_NAME,"name","study_id="+this.id+" AND grade_rank="+GradeGroup.COMPARE,null);
	}

	public ArrayList getExcelTableMetaData() {
		return new TableMetaData().getMetaDataSources("study_id='"+this.id+"' AND table_type='"+MetaData.EXCEL+"'","");
	}

	public ArrayList getCSVTableMetaData() {
		return new TableMetaData().getMetaDataSources("study_id='"+this.id+"' AND table_type='"+MetaData.CSV+"'","");
	}

	public boolean usesTableMetaData() {
		return !Query.getField("photo_data_by_table","name","study_id='"+this.id+"'",null).isEmpty();
	}

	public boolean hasManualMetaData() {
		String query = "SELECT * FROM photo_data_by_manual WHERE study_id='"+id+"'";
		return Query.getCount(query,new ManualMetaData()) > 0;
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
	 * @return the photo_attribute_table_name
	 */
	public String getPhoto_attribute_table_name() {
		return photo_attribute_table_name;
	}

	/**
	 * @param photo_attribute_table_name the photo_attribute_table_name to set
	 */
	public void setPhoto_attribute_table_name(String photo_attribute_table_name) {
		this.photo_attribute_table_name = photo_attribute_table_name;
	}
}
