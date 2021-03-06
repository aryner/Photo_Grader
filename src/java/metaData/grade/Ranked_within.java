/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData.grade;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import SQL.Query;

import model.Model;

/**
 *
 * @author aryner
 */
public class Ranked_within extends Model implements Comparable<Ranked_within>{
	private int id;
	private int grade_group_id;
	private String value;
	private int position;
	private int high_low;
	private String compare_field;

	public static final int LOW = 0;
	public static final int HIGH = 1;

	public Ranked_within() {}

	public Ranked_within(int id, int grade_group_id, String value, int position, int high_low, String compare_field) {
		this.id = id;
		this.grade_group_id = grade_group_id;
		this.value = value;
		this.position = position;
		this.high_low = high_low;
		this.compare_field = compare_field;
	}

	@Override
	public Ranked_within getModel(ResultSet resultSet) {
		try {
			return new Ranked_within(
				resultSet.getInt("id"),resultSet.getInt("grade_group_id"),resultSet.getString("value"),
				resultSet.getInt("position"),resultSet.getInt("high_low"),resultSet.getString("compare_field")
			);
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static ArrayList<Ranked_within> getHighs(int group_id) {
		String query = "SELECT * FROM ranked_within WHERE grade_group_id="+group_id+" AND high_low="+HIGH;
		return (ArrayList)Query.getModel(query,new Ranked_within());
	}

	public static ArrayList<Ranked_within> getLows(int group_id) {
		String query = "SELECT * FROM ranked_within WHERE grade_group_id="+group_id+" AND high_low="+LOW;
		return (ArrayList)Query.getModel(query,new Ranked_within());
	}

	@Override
	public int compareTo(Ranked_within ranked_within) {
		return ranked_within.position - this.position;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + this.id;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Ranked_within other = (Ranked_within) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the high_low
	 */
	public int getHigh_low() {
		return high_low;
	}

	/**
	 * @param high_low the high_low to set
	 */
	public void setHigh_low(int high_low) {
		this.high_low = high_low;
	}

	/**
	 * @return the compare_field
	 */
	public String getCompare_field() {
		return compare_field;
	}

	/**
	 * @param compare_field the compare_field to set
	 */
	public void setCompare_field(String compare_field) {
		this.compare_field = compare_field;
	}
}
