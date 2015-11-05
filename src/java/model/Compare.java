/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;

import SQL.Query;

import metaData.grade.GroupBy;
import metaData.grade.Ranked_within;

/**
 *
 * @author aryner
 */
public class Compare extends Model {
	private int id;
	private String grader;
	private Map<String,String> group_meta_data;
	private String comparison;
	private String high;
	private String low;
	private String compare_field;

	public static final String ID = "id";
	public static final String GRADER = "grader";
	public static final String GROUP_META_DATA = "group_meta_data";
	public static final String COMPARISON = "comparison";
	public static final String HIGH = "high";
	public static final String LOW = "low";

	public Compare() {}

	public Compare(int id, String grader, Map group_meta_data, String comparison, String high, String low) {
		this.id = id;
		this.grader = grader;
		this.group_meta_data = group_meta_data;
		this.comparison = comparison;
		this.high = high;
		this.low = low;
	}

	@Override
	public Compare getModel(ResultSet resultSet) {
		try {
			Map<String,String> groupMeta = new HashMap<String,String>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colCount = metaData.getColumnCount();
			for(int i=3; i<=colCount; i++) {
				String colName = metaData.getColumnLabel(i);
				if(colName.indexOf("_") == 0) 
					groupMeta.put(colName,resultSet.getString(colName));
			}

			return new Compare(
				resultSet.getInt(ID),resultSet.getString(GRADER),
				groupMeta,resultSet.getString(COMPARISON),
				resultSet.getString(HIGH),resultSet.getString(LOW)
			);
		}
		catch(SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static void createTable(int group_id) {
		String tableName = "compare_"+Query.getField("photo_grade_group","grade_name","id='"+group_id+"'",null).get(0);
		String query = "CREATE TABLE IF NOT EXISTS "+tableName+" ("+
			"id int unsigned AUTO_INCREMENT, grader varchar(50),"+
			"comparison varchar(40), high varchar(40), low varchar(40),";
			
		String postfix = "PRIMARY KEY(id)) ENGINE=INnoDB";
		ArrayList<GroupBy> grouping = GroupBy.getGroup(group_id);

		for(GroupBy group : grouping) {
			query += group.getPhoto_attribute()+" varchar(40),";
		}

		Query.update(query+postfix);
	}

	public static void generateRankWithin(HttpServletRequest request, int group_id) {
		String query = "INSERT INTO ranked_within (grade_group_id, value, position, high_low, compare_field) VALUES ";

		ArrayList<String> highs = getExtremes(request, HIGH);
		ArrayList<String> lows = getExtremes(request, LOW);

		String compare_field = request.getParameter("compare_between");
		for(int i=0; i<highs.size(); i++) {
			if(i>0) { query += ", "; }
			query += "('"+group_id+"', '"+highs.get(i)+"', '"+i+"', '"+Ranked_within.HIGH+"', '"+compare_field+"')";
		}
		for(int i=0; i<lows.size(); i++) {
			query += ", ('"+group_id+"', '"+lows.get(i)+"', '"+i+"', '"+Ranked_within.LOW+"', '"+compare_field+"')";
		}

		Query.update(query);
	}

	private static ArrayList<String> getExtremes(HttpServletRequest request, String extreme) {
		ArrayList<String> results = new ArrayList<String>();

		int count = Integer.parseInt(request.getParameter(extreme+"_count"));
		for(int i=0; i<=count; i++) {
			results.add(request.getParameter(extreme+"_"+i));
		}

		return results;
	}

	public static class Compare_photos {
		private Compare compare;
		private ArrayList<Photo> low_photos;
		private ArrayList<Photo> high_photos;

		public Compare_photos(Compare compare) {
			this.compare = compare;
		}

		public void assign_photos(String photo_table, int group_id) {
			String query = "SELECT * FROM "+photo_table+" WHERE ";
			String where = "";
			Set<String> keys = compare.getGroup_meta_data().keySet();
			for(String key : keys) {
				where += key+"='"+compare.getGroup_meta_data().get(key)+" AND ";
			}
			low_photos = (ArrayList)Query.getModel(query+where+" AND "+compare.getCompare_field(group_id)+"='"+compare.getLow()+"'",new Photo());
			low_photos = (ArrayList)Query.getModel(query+where+" AND "+compare.getCompare_field(group_id)+"='"+compare.getHigh()+"'",new Photo());
		}

		/**
		 * @return the compare
		 */
		public Compare getCompare() {
			return compare;
		}

		/**
		 * @param compare the compare to set
		 */
		public void setCompare(Compare compare) {
			this.compare = compare;
		}

		/**
		 * @return the low_photos
		 */
		public ArrayList<Photo> getLow_photos() {
			return low_photos;
		}

		/**
		 * @param low_photos the low_photos to set
		 */
		public void setLow_photos(ArrayList<Photo> low_photos) {
			this.low_photos = low_photos;
		}

		/**
		 * @return the high_photos
		 */
		public ArrayList<Photo> getHigh_photos() {
			return high_photos;
		}

		/**
		 * @param high_photos the high_photos to set
		 */
		public void setHigh_photos(ArrayList<Photo> high_photos) {
			this.high_photos = high_photos;
		}
	}

	public String getCompare_field(int group_id) {
		if(compare_field == null) {
			String query = "SELECT * FROM ranked_within WHERE grade_group_id="+group_id;
			compare_field = ((Ranked_within)Query.getModel(query,new Ranked_within()).get(0)).getCompare_field();
		}

		return compare_field;
	}

	public String getCompare_field() {
		return compare_field;
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
	 * @return the group_meta_data
	 */
	public Map<String,String> getGroup_meta_data() {
		return group_meta_data;
	}

	/**
	 * @param group_meta_data the group_meta_data to set
	 */
	public void setGroup_meta_data(Map<String,String> group_meta_data) {
		this.group_meta_data = group_meta_data;
	}

	/**
	 * @return the comparison
	 */
	public String getComparison() {
		return comparison;
	}

	/**
	 * @param comparison the comparison to set
	 */
	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	/**
	 * @return the high
	 */
	public String getHigh() {
		return high;
	}

	/**
	 * @param high the high to set
	 */
	public void setHigh(String high) {
		this.high = high;
	}

	/**
	 * @return the low
	 */
	public String getLow() {
		return low;
	}

	/**
	 * @param low the low to set
	 */
	public void setLow(String low) {
		this.low = low;
	}

	/**
	 * @return the grader
	 */
	public String getGrader() {
		return grader;
	}

	/**
	 * @param grader the grader to set
	 */
	public void setGrader(String grader) {
		this.grader = grader;
	}

}
