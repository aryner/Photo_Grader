/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData.grade;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Model;

import SQL.Query;

/**
 *
 * @author aryner
 */
public class GroupBy extends Model{
	private int id;
	private int grade_group_id;
	private String photo_attribute;

	public GroupBy(){}

	public GroupBy(int id, int grade_group_id, String photo_attribute) {
		this.id = id;
		this.grade_group_id = grade_group_id;
		this.photo_attribute = photo_attribute;
	}

	public static ArrayList<GroupBy> getGroup(int group_id) {
		String query = "SELECT * FROM group_by WHERE grade_group_id = "+group_id;

		return (ArrayList)Query.getModel(query, new GroupBy());
	}

	@Override
	public GroupBy getModel(ResultSet resultSet) {
		try {
			return new GroupBy(resultSet.getInt("id"),resultSet.getInt("grade_group_id"),
					   resultSet.getString("photo_attribute"));
		}
		catch (SQLException e) {
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
	 * @return the grade_group_id
	 */
	public int getGrade_group_id() {
		return grade_group_id;
	}

	/**
	 * @param grade_group_id the grade_group_id to set
	 */
	public void setGrade_group_id(int grade_group_id) {
		this.grade_group_id = grade_group_id;
	}

	/**
	 * @return the photo_attribute
	 */
	public String getPhoto_attribute() {
		return photo_attribute;
	}

	/**
	 * @param photo_attribute the photo_attribute to set
	 */
	public void setPhoto_attribute(String photo_attribute) {
		this.photo_attribute = photo_attribute;
	}

	
}
