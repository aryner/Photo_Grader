/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author aryner
 */
public class Compare extends Model {
	private int id;
	private int grade_group_id;
	private String value;
	private int position;
	private int high_low;

	private static final int HIGH = 0;
	private static final int LOW = 0;

	public Compare() {}

	public Compare(int id, int grade_group_id, String value, int position, int high_low) {
		this.id = id;
		this.grade_group_id = grade_group_id;
		this.value = value;
		this.position = position;
		this.high_low = high_low;
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
}
