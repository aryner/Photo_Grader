/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.*;
import SQL.*;
import utilities.*;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import metaData.grade.*;

/**
 *
 * @author aryner
 */
public class Photo extends Model{
	private int id;
	private String name;
	private String path;
	private Map<String,Object> fields;

	public Photo() {}

	public Photo(int id, String name, String path, Map fields) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.fields = fields;
	}

	@Override
	public Photo getModel(ResultSet resultSet) {
		try{
			fields = new HashMap<String,Object>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colCount = metaData.getColumnCount();
			for(int i=4; i<colCount; i++) {
				String colName = metaData.getColumnLabel(i);
				fields.put(colName, resultSet.getObject(colName));
			}

			return new Photo(resultSet.getInt("id"),resultSet.getString("name"),
					 resultSet.getString("path"),fields);
		}
		catch(SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static void generateAttributes(Study study, Map<String,String> name_types) {
		String query = "CREATE TABLE IF NOT EXISTS "+study.getPhoto_attribute_table_name()+" ("+
				"id int unsigned AUTO_INCREMENT, "+
				"name varchar(40), "+
				"path varchar(50), ";
		String postfix = "PRIMARY KEY(id)) ENGINE=INnoDB";
		String fields = "";

		Iterator<String> keys = name_types.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			fields += Helper.process(key)+" "+Helper.javaToSQLType(name_types.get(key))+", ";
		}

		Query.update(query+fields+postfix);
	}

	public static ArrayList<String> upload(HttpServletRequest request, Study study) {
		return FileIO.upload(request, FileIO.PHOTO, study);
	}

	public static ArrayList<String> getMetaDataKeys(String table_name) {
		String query = "SELECT * FROM "+table_name;
		ArrayList<String> keys = Query.getColumnNames(query);
		for(int i=0; i<3; i++) keys.remove(0);

		return keys;
	}

	public static ArrayList<Photo> getUngradedCombinations(GradeGroup category, String photoTable, String gradeTable, String grader) {
		ArrayList<Photo> combinations = getPossibleCombinations(category, photoTable);
		ArrayList<Grade> graded = Grade.getGrades(grader, gradeTable);

		for(int i=combinations.size()-1; i>=0; i--) {
			boolean match = false;
			for(int j=0; j<graded.size() && !match; j++) {
				if(combinationMatchesGrade(combinations.get(i),graded.get(j), category)) {
					match = true;
					combinations.remove(i);
					graded.remove(j);
				}
			}
		}

		return combinations;
	}

	public static ArrayList<Photo> getPossibleCombinations(GradeGroup category, String tableName) {
		String query = "SELECT * FROM "+tableName+" GROUP BY ";
		for(int i=0; i<category.groupBySize(); i++) {
			if(i>0) query += ", ";
			String attribute = category.getGroupBy(i).getPhoto_attribute();
			query += attribute.equals(Grade.FILENAME) ? "name" : attribute;
		}

		return (ArrayList)Query.getModel(query, new Photo());
	}

	private static boolean combinationMatchesGrade(Photo combination, Grade grade, GradeGroup category) {
		for(int i=0; i<category.groupBySize(); i++) {
			String attr = category.getGroupBy(i).getPhoto_attribute();
			if(!grade.getGroupMetaData(attr).equals(combination.getField(attr)))
				return false;
		}

		return true;
	}

	public String getField(String key) {
		return ""+fields.get(key);
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
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the fields
	 */
	public Map getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Map fields) {
		this.fields = fields;
	}
}
