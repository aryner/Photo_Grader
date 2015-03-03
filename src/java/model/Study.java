/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.*;

/**
 *
 * @author aryner
 */
public class Study extends Model {
	private int id;
	private String name;
	private String photo_table_name;
	private String photo_attribute_table_name;
	private String photo_grade_group_name;

	public Study(
		int id, String name, String photo_table_name, 
		String photo_attribute_table_name,
		String photo_grade_group_name
	) {
		this.id = id;
		this.name = name;
		this.photo_table_name = photo_table_name;
		this.photo_attribute_table_name = photo_attribute_table_name;
		this.photo_grade_group_name = photo_grade_group_name;
	}

	@Override
	public Study getModel(ResultSet resultSet) {
		try {
			return new Study(
				resultSet.getInt("id"),resultSet.getString("name"),
				resultSet.getString("photo_table_name"),
				resultSet.getString("photo_attribute_table_name"),
				resultSet.getString("photo_grade_group_name")
			);
		} catch(SQLException e) {
			e.printStackTrace(System.err);
		}

		return null;
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
	 * @return the photo_table_name
	 */
	public String getPhoto_table_name() {
		return photo_table_name;
	}

	/**
	 * @param photo_table_name the photo_table_name to set
	 */
	public void setPhoto_table_name(String photo_table_name) {
		this.photo_table_name = photo_table_name;
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

	/**
	 * @return the photo_grade_group_name
	 */
	public String getPhoto_grade_group_name() {
		return photo_grade_group_name;
	}

	/**
	 * @param photo_grade_group_name the photo_grade_group_name to set
	 */
	public void setPhoto_grade_group_name(String photo_grade_group_name) {
		this.photo_grade_group_name = photo_grade_group_name;
	}
	
}
