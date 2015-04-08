package metaData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.*;
import java.sql.*;
import SQL.*;
import model.*;
import utilities.*;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aryner
 */
public class PhotoNameMetaData extends Model implements MetaDataSource {
	private int id;
	private int study_id;
	private String name;
	private int position;
	private int used;
	private int starts;
	private int ends;
	private String start_flag;
	private String end_flag;

	public static final int START = 1;
	public static final int END = 2;
	public static final int NUMBER = 3;
	public static final int DELIMITER = 4;
	public static final int BEFORE = 5;
	public static final int AFTER = 6;
	public static final int NEXT_NUMBER = 7;
	public static final int NEXT_LETTER = 8;
	public static final int NEXT_NOT_NUMBER = 9;
	public static final int NEXT_NOT_LETTER = 10;

	public static final int TRUE = 2;
	public static final int FALSE = 1;

	public static final int USED = 2;
	public static final int NOT_USED = 1;

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
	public ArrayList<Model> getMetaDataSources(String where, String order){
		String query = "SELECT * FROM photo_data_by_name "+(where.length()>0?"WHERE "+where:"")+(order.length()>0?" ORDER BY "+order:"");
		return Query.getModel(query, this);
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

	public static void updateDB(ArrayList<PhotoNameMetaData> metaData) {
		if(metaData.isEmpty()) return;
		String query = "INSERT INTO photo_data_by_name (study_id, name, "+
				"position, used, starts, ends, start_flag, end_flag) "+
				"VALUES ";
		String postfix = "";
		for(PhotoNameMetaData data : metaData) {
			if(postfix.length() != 0) postfix += ", ";
			postfix += "('"+data.getStudy_id()+"', '"+data.getName()+"', '"+
				   data.getPosition()+"', '"+data.getUsed()+"', '"+
				   data.getStarts()+"', '"+data.getEnds()+"', '"+
				   data.getStart_flag()+"', '"+data.getEnd_flag()+"')";
		}

		Query.update(query+postfix);
	}

	@Override
	public void setFields(
		int study_id, HttpServletRequest request, int position, 
		String identifier, String identifier_col, int type
	) {
		this.setStudy_id(study_id);
		this.setName(request.getParameter("type_"+MetaData.NAME+"_"+position));
		this.setPosition(position);
		this.setUsed(this.getName().equals("_not-meta_")?PhotoNameMetaData.FALSE:PhotoNameMetaData.TRUE);
		this.setStarts(Integer.parseInt(request.getParameter("start_"+position)));
		this.setEnds(Integer.parseInt(request.getParameter("end_"+position)));
		this.setStart_flag(getDelimiterFlag(request, this.getStarts(), position, MetaData.START));
		this.setEnd_flag(getDelimiterFlag(request, this.getEnds(), position, MetaData.END));
	}

	private static String getDelimiterFlag(HttpServletRequest request, int type, int index, int side) {
		if(type == MetaData.DELIMITER || type == MetaData.NUMBER) {
			return (side == MetaData.START) ? request.getParameter("start_"+type+"_"+index) : request.getParameter("end_"+type+"_"+index);
		}
		return null;
	}

	public static ArrayList<String> extractNames(ArrayList<PhotoNameMetaData> data) {
		ArrayList<String> result = new ArrayList<String>();

		for(PhotoNameMetaData datum : data) {
			if(datum.isUsed()) {
				result.add(datum.getName());
			}
		}

		return result;
	}

	public static ArrayList<String> extractAttributes(String name, ArrayList<PhotoNameMetaData> data) {
		ArrayList<String> result = new ArrayList<String>();
		int currIndex = 0;
		int endIndex = 0;
		boolean used = false;

		//-1 is an error, -2 is not
		for(PhotoNameMetaData datum : data) {
			int nextIndex = getNextIndex(name, currIndex, datum, START);
			if(nextIndex < currIndex) {
				//-1 is an error for sure
				//-2 should be an error because this is the start index
			}
			if(endIndex == -2 && used) result.add(name.substring(currIndex,nextIndex));
			used = datum.isUsed();

			currIndex = nextIndex;
			endIndex = getNextIndex(name, currIndex, datum, END);
			if(endIndex >= currIndex) {
				if(used) {
					result.add(name.substring(currIndex,endIndex));
				}
				currIndex = endIndex;
			}
			else if(endIndex != -2) {
				//error
			}
		}

		return result;
	}

	private static int getNextIndex(String name, int currIndex, PhotoNameMetaData datum, int side) {
		switch(side == START ? datum.getStarts() : datum.getEnds()) {
			case START:
			case AFTER:
				return currIndex;
			case BEFORE:
				return -2;
			case NUMBER:
				return currIndex + Integer.parseInt(datum.getStart_flag());
			case DELIMITER:
				return getDelimIndex(currIndex, name, datum, side);
			case END:
				int end = name.lastIndexOf(".");
				return end > 0 ? end : name.length();
			case NEXT_NUMBER:
				return Tools.getRegexIndex(name, "[0-9]", currIndex);
			case NEXT_LETTER:
				return Tools.getRegexIndex(name, "[a-zA-Z]", currIndex);
			case NEXT_NOT_NUMBER:
				return Tools.getRegexIndex(name, "[^0-9]", currIndex);
			case NEXT_NOT_LETTER:
				return Tools.getRegexIndex(name, "[^a-zA-Z]", currIndex);
			default:
				return -1;
		}
	}

	private static int getDelimIndex(int currIndex, String name, PhotoNameMetaData datum, int side) {
		String delimiter = side == START ? datum.getStart_flag() : datum.getEnd_flag();
		int offset = side == END ? 0 : 1;
		return name.indexOf(delimiter, currIndex) + offset;
	}

	public boolean isUsed() {
		return this.used == USED;
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
