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

/**
 *
 * @author aryner
 */
public class Photo {
	private int id;
	private String name;
	private String path;
	private Map fields;

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
