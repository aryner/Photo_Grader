package metaData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.sql.*;
import model.Model;

/**
 *
 * @author aryner
 */
public class PhotoNameMetaData extends Model {
	private int id;
	private int study_id;
	private String name;
	private int position;
	private int used;
	private int starts;
	private int ends;
	private String start_flag;
	private String end_flag;

	public PhotoNameMetaData() {
		id = 0;
		study_id = 0;
		name = "";
		position = 0;
		used = 0;
		starts = 0;
		ends = 0;
		start_flag = "";
		end_flag = "";
	}

	public PhotoNameMetaData(
			int id, int study_id, String name, int position,
			int used, int starts, int ends, String start_flag,
			String end_flag) {
		this.id = id;
		this.study_id = study_id;
		this.name = name;
		this.position = position;
		this.used = used;
		this.starts = starts;
		this.ends = ends;
		this.start_flag = start_flag;
		this.end_flag = end_flag;
	}

	@Override
	public PhotoNameMetaData getModel(ResultSet resultSet) {
		try {
			return new PhotoNameMetaData(
				resultSet.getInt("id"), resultSet.getInt("study_id"),
				resultSet.getString("name"), resultSet.getInt("position"),
				resultSet.getInt("used"), resultSet.getInt("starts"),
				resultSet.getInt("ends"), resultSet.getString("start_flag"),
				resultSet.getString("end_flag")
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
	 * @return the used
	 */
	public int getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed(int used) {
		this.used = used;
	}

	/**
	 * @return the starts
	 */
	public int getStarts() {
		return starts;
	}

	/**
	 * @param starts the starts to set
	 */
	public void setStarts(int starts) {
		this.starts = starts;
	}

	/**
	 * @return the ends
	 */
	public int getEnds() {
		return ends;
	}

	/**
	 * @param ends the ends to set
	 */
	public void setEnds(int ends) {
		this.ends = ends;
	}

	/**
	 * @return the start_flag
	 */
	public String getStart_flag() {
		return start_flag;
	}

	/**
	 * @param start_flag the start_flag to set
	 */
	public void setStart_flag(String start_flag) {
		this.start_flag = start_flag;
	}

	/**
	 * @return the end_flag
	 */
	public String getEnd_flag() {
		return end_flag;
	}

	/**
	 * @param end_flag the end_flag to set
	 */
	public void setEnd_flag(String end_flag) {
		this.end_flag = end_flag;
	}
}
