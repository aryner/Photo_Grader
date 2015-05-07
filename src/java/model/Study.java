/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import SQL.*;
import utilities.*;
import java.util.*;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;
import metaData.*;

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

	public ArrayList<String> createGradeGroup(HttpServletRequest request) {
		ArrayList<String> errors = new ArrayList<String>();

		String newName = request.getParameter("name");
		ArrayList<String> usedNames = (ArrayList)Query.getField("photo_grade_group", "name", "study_id="+this.id,null);
		if(Tools.contains(usedNames, newName)) {
			errors.add("That grade category name has already been used");
			return errors;
		}

		usedNames = (ArrayList)Query.getField("photo_grade_group", "grade_name", null,null);
		String grade_name = Tools.generateTableName("grade_", usedNames);
		String query = "INSERT INTO photo_grade_group (study_id, name, grade_name) VALUES ('"+this.id+
			       "', '"+newName+"', '"+grade_name+"')";
		Query.update(query);

		int groupId = Integer.parseInt(Query.getField("photo_grade_group","id","study_id="+this.id+" AND name='"+newName+"'",null).get(0)+"");
		Grade.createGroup(groupId, this.photo_attribute_table_name, request);
		Grade.createQuestions(groupId, request);
		Grade.createTable(groupId);

		return errors;
	}

	public String getPhotoNumber() {
		return photo_attribute_table_name.substring(photo_attribute_table_name.lastIndexOf("_")+1);
	}

	public int getGradeGroupId(String name) {
		return Integer.parseInt(""+Query.getField("photo_grade_group","id","study_id="+this.id+" AND name='"+name+"'",null).get(0));
	}

	public ArrayList<String> getGradeCategoryNames() {
		return (ArrayList)Query.getField("photo_grade_group","name","study_id="+this.id,null);
	}

	public ArrayList getExcelTableMetaData() {
		return new TableMetaData().getMetaDataSources("study_id='"+this.id+"' AND table_type='"+MetaData.EXCEL+"'","");
	}

	public boolean usesTableMetaData() {
		return !Query.getField("photo_data_by_table","name","study_id='"+this.id+"'",null).isEmpty();
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
