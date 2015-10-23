/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import SQL.Query;
import SQL.Helper;

import utilities.FileIO;

import metaData.MetaDataSource;

import metaData.grade.GradeGroup;

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

	public Photo(Study study, String name) {
		String query = "SELECT * FROM "+study.getPhoto_attribute_table_name()+" WHERE name='"+name+"'";
		Photo temp = (Photo)Query.getModel(query,new Photo()).get(0);

		this.id = temp.getId();
		this.name = temp.getName();
		this.path = temp.getPath();
		this.fields = temp.getFields();
	}

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
			for(int i=4; i<colCount+1; i++) {
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
				"name varchar(100), "+
				"path varchar(100), ";
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

	public static ArrayList<Photo> getUngradedGroup(GradeGroup category, String photoTable, String grader) {
		ArrayList<Photo> choices = null;
		if(Math.random() < 0.1) {
			choices = getRegradeCombinations(category, photoTable, grader);
		} 
		if (choices == null) {
			choices = getUngradedCombinations(category, photoTable, grader);
		}
		if(choices.isEmpty()) return new ArrayList<Photo>();

		Random rand = new Random(System.currentTimeMillis());
		Photo choice = choices.get(rand.nextInt(choices.size()));

		String query = "SELECT * FROM "+photoTable+" WHERE ";
		for(int i=0; i<category.groupBySize(); i++) {
			if(i>0) query += " AND ";
			String key = category.getGroupBy(i).getPhoto_attribute();
			String value = choice.getField(key);
			if(key.equals(Grade.FILENAME)) {
				key = "name";
				value = choice.getName();
			}

			if(value.toLowerCase().equals("null"))
				query += key+" IS NULL";
			else 
				query += key+"='"+value+"'";
		}

		return (ArrayList)Query.getModel(query, new Photo());
	}

	public static ArrayList<Photo> getUngradedCombinations(GradeGroup category, String photoTable, String grader) {
		ArrayList<Photo> combinations = getPossibleCombinations(category, photoTable);
		ArrayList<Grade> graded = Grade.getGrades(grader, category.getGrade_name());

		for(int i=combinations.size()-1; i>=0; i--) {
			boolean match = false;
			for(int j=0; j<graded.size() && !match; j++) {
				if(combinationMatchesGrade(combinations.get(i),graded.get(j), category)) {
					match = true;
					combinations.remove(i);
				}
			}
		}

		return combinations;
	}

	public static ArrayList<Photo> getRegradeCombinations(GradeGroup category, String photoTable, String grader) {
		String query = "SELECT * FROM "+category.getGrade_name()+" WHERE grader='"+grader+"'";
		ArrayList<Grade> grades = (ArrayList)Query.getModel(query,new Grade());
		if(grades.isEmpty()) { return null; }

		Random rand = new Random(System.currentTimeMillis());
		Grade reGrade = grades.get(rand.nextInt(grades.size()));
		query = "SELECT * FROM "+photoTable+" WHERE ";
		for(int i=0; i<category.groupBySize(); i++) {
			if(i>0) { query += " AND "; }
			String field = category.getGroupBy(i).getPhoto_attribute();
			if(field.equals(Grade.FILENAME)) { 
				field = "name"; 
				query += field+"='"+reGrade.getGroupMetaData(Grade.FILENAME)+"'";
			} else {
				query += field+"='"+reGrade.getGroupMetaData(field)+"'";
			}
		}

		return (ArrayList)Query.getModel(query,new Photo());
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
			String field = attr.equals(Grade.FILENAME) ? combination.getName() : combination.getField(attr);

			if(!grade.getGroupMetaData(attr).equals(field))
				return false;
		}

		return true;
	}

	public boolean hasMissingMetaData(ArrayList<MetaDataSource> meta) {
		ArrayList<String> names = new ArrayList<String>();
		for(MetaDataSource datum : meta) {
			names.add(datum.getName());
		}

		return hasMissingMetaDataByName(names);
	}

	public boolean hasMissingMetaData(ArrayList<MetaDataSource> meta, ArrayList<String> fromList) {
		ArrayList<String> names = new ArrayList<String>();
		for(MetaDataSource datum : meta) {
			if(fromList.contains(datum.getName())) {
				names.add(datum.getName());
			}
		}

		return hasMissingMetaDataByName(names);
	}

	public static int deletePhoto(HttpServletRequest request, Study study) {
		int index = Integer.parseInt(request.getParameter("index"));
		String photoId = request.getParameter("photo_id");
		String query = "SELECT * FROM "+study.getPhoto_attribute_table_name()+" WHERE id="+photoId;
		Photo photo = (Photo)Query.getModel(query,new Photo()).get(0);
		String photoTable = study.getPhoto_attribute_table_name();
		String dir = photoTable.substring(photoTable.lastIndexOf("_")+1);
		FileIO.deletePhoto(photo.getName(),dir);
		query = "DELETE FROM "+study.getPhoto_attribute_table_name()+" WHERE id="+photoId;
		Query.update(query);

		return index;
	}

	public boolean hasMissingMetaDataByName(ArrayList<String> columnNames) {
		boolean result = false;
		for(String columnName : columnNames) {
			if(fields.get(Helper.process(columnName)) == null) {
				result = true;
			}
		}

		return result;
	}

	public static String getSubmitLink(HttpServletRequest request, String table_name, Photo photo) {

		ArrayList<Photo> prevNext = photo.getPrevNext(table_name);

		if(request.getParameter("prev") != null) {
			return "manually_assign_meta-data?id="+prevNext.get(0).getId();
		}
		if(request.getParameter("next") != null) {
			return "manually_assign_meta-data?id="+prevNext.get(1).getId();
		}

		return "manually_assign_meta-data?id="+photo.getId();
	}

	public static ArrayList<Photo> getViewGroups(ArrayList<String> groupOptions, Study study) {
		//query the group to get a list of photos, each representing a differnet group;
		String query = "SELECT * FROM "+study.getPhoto_attribute_table_name()+" GROUP BY ";
		String postfix = "";
		for(String option : groupOptions) {
			if(postfix.length() > 0) postfix += ", ";
			String attribute = option;
			postfix += attribute.equals(Grade.FILENAME) ? "name" : attribute;
		}
		return (ArrayList)Query.getModel(query+postfix,new Photo());
	}

	public static ArrayList<String> getViewGroupOptions(HttpServletRequest request, Study study) {
		ArrayList<String> result = new ArrayList<String>(); 
		ArrayList<String> columns = Photo.getMetaDataKeys(study.getPhoto_attribute_table_name());
		int optionsCount = Integer.parseInt(request.getParameter("groupOptionCount"));
		for(int i=-1; i<optionsCount; i++) {
			if(request.getParameter("groupBy_"+i)!=null) {
				if(i>=0) {
					result.add(columns.get(i));
				}
				else {
					result.add(GradeGroup.FILENAME);
				}
			}
		}
		if (result.isEmpty()) { result.add(GradeGroup.FILENAME); }
		return result;
	}

	public ArrayList<Photo> getPrevNext(String tableName) {
		ArrayList<Photo> result = new ArrayList<Photo>();
		String query = "SELECT * FROM "+tableName+" WHERE id < "+id+" ORDER BY id DESC LIMIT 1";
		ArrayList<Photo> temp = (ArrayList)Query.getModel(query, new Photo());
		result.add(temp.isEmpty()?null:temp.get(0));
		query = "SELECT * FROM "+tableName+" WHERE id > "+id+" ORDER BY id LIMIT 1";
		temp = (ArrayList)Query.getModel(query, new Photo());
		result.add(temp.isEmpty()?null:temp.get(0));

		return result;
	}

	public ArrayList<Photo> getGroup(ArrayList<String> groupOptions, String tableName) {
		String query = "SELECT * FROM "+tableName+" WHERE ";

		for(int i=0; i<groupOptions.size(); i++) {
			if(i>0) { query += " AND "; }

			String key = groupOptions.get(i);
			if(key.equals(Grade.FILENAME)) {
				query += "name='"+name+"'";
			} else {
				query += key+"='"+getField(key)+"'";
			}
		}

		return (ArrayList)Query.getModel(query,new Photo());
	}

	public static int getPhotoCount(String tableName) {
		String query = "SELECT * FROM "+tableName;
		ArrayList photos = Query.getModel(query,new Photo());

		return photos.size();
	}

	public static int getUnassignedCount(String tableName) {
		String query = "SELECT * FROM "+tableName;
		Photo photo = (Photo)Query.getModel(query,new Photo()).get(0);

		query = "SELECT * FROM "+tableName+" WHERE ";
		String postfix = "";
		for(Object field : photo.getFields().keySet()) {
			if (postfix.length() > 0 ) { postfix += " OR "; }
			postfix += field.toString() + " IS NULL";
		}

		return Query.getModel(query+postfix,new Photo()).size();
	}

	public String getOptionValues(ArrayList<String> options) {
		String result = "";

		for(String option : options) {
			if(result.length() > 0) result += ", ";
			if(option.equals(Grade.FILENAME)) { 
				result += "Filename = <b>"+this.name+"</b>";
			} else {
				result += Helper.unprocess(option)+" = "+"<b>"+getField(option)+"</b>";
			}
		}
		return result;
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
