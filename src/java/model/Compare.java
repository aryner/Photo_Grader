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
import java.util.Random;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;

import SQL.Query;
import SQL.Helper;

import metaData.grade.GradeGroup;
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

	private ArrayList<Photo> low_photos;
	private ArrayList<Photo> high_photos;

	public static final String ID = "id";
	public static final String GRADER = "grader";
	public static final String GROUP_META_DATA = "group_meta_data";
	public static final String COMPARISON = "comparison";
	public static final String HIGH = "high";
	public static final String LOW = "low";

	public Compare() {}

	public Compare(String table, int id) {
		String query = "SELECT * FROM "+table+" WHERE id="+id;
		Compare compare = (Compare)Query.getModel(query,new Compare()).get(0);
		this.id = compare.getId();
		this.grader = compare.getGrader();
		this.group_meta_data = compare.getGroup_meta_data();
		this.comparison = compare.getComparison();
		this.high = compare.getHigh();
		this.low = compare.getLow();
	}

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

	public static Compare getCompare(GradeGroup group, User user, String photoTable) {
		ArrayList<Compare> compares = getCompares(group, user, photoTable);
		
		Random rand = new Random();
		if(compares.isEmpty()) { return null; }
		Compare compare = compares.get(rand.nextInt(compares.size()));
		compare.assign_photos(photoTable,group.getId());
		return compare;
	}

	private static ArrayList getCompares(GradeGroup group, User user, String photo_table) {
		String query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+user.getName()+"'";
		ArrayList<Compare> compares = (ArrayList)Query.getModel(query, new Compare());

		if(compares.isEmpty()) { generateCompares(group, user, photo_table); }

		query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+user.getName()+"' AND comparison IS NULL";
		return (ArrayList)Query.getModel(query,new Compare());
	}

	private static void generateCompares(GradeGroup group, User user, String photo_table) {
		ArrayList<GroupBy> groupedBy = GroupBy.getGroup(group.getId());
		String query = "INSERT INTO "+group.getGrade_name()+" (grader, high, low";
		for(GroupBy grouped : groupedBy) {
			query += ", "+grouped.getPhoto_attribute();
		}
		query += ") VALUES ";

		ArrayList<Photo> patientSplitPhotos = Photo.getPhotosGroupedBy(photo_table,groupedBy);
		ArrayList<Ranked_within> highs = Ranked_within.getHighs(group.getId());
		ArrayList<Ranked_within> lows = Ranked_within.getLows(group.getId());

		Random rand = new Random(System.currentTimeMillis());
		while(!patientSplitPhotos.isEmpty()) {
			int random = rand.nextInt(patientSplitPhotos.size());
			Photo currPatient = patientSplitPhotos.remove(random);
			//query photos for this patient
			String photo_query = "SELECT * FROM "+photo_table+" WHERE ";
			String postfix = "";
			for(GroupBy grouped :groupedBy) {
				if(postfix.length() > 0) { postfix += " AND "; }
				postfix += grouped.getPhoto_attribute() + "='"+currPatient.getField(grouped.getPhoto_attribute())+"'";
			}
			ArrayList<Photo> patientPhotos = (ArrayList)Query.getModel(photo_query+postfix,new Photo());
			//find extremes
			Photo high = findExtreme(patientPhotos, highs, Ranked_within.HIGH);
			Photo low = findExtreme(patientPhotos, lows, Ranked_within.LOW);
			//if found 
			if(low != null && high != null) {
			  //add compare to insert query
				String attribute = Helper.process(lows.get(0).getCompare_field());
				query += "('"+user.getName()+"', '"+high.getField(attribute)+"', '"+low.getField(attribute)+"'";
				for(GroupBy grouped : groupedBy) {
					query += ", '"+low.getField(grouped.getPhoto_attribute())+"'";
				}
				query += "),";
			}
			//else
			else {
			  //add filled compare that has some signal for the misisng extreme
			  //and marks it as compared in query
				String attribute = Helper.process(lows.get(0).getCompare_field());
				query += "('"+user.getName()+"', '"+(high!=null?high.getField(attribute):"_missing_")+
					"', '"+(low!=null?low.getField(attribute):"_missing_")+"'";
				for(GroupBy grouped : groupedBy) {
					query += ", '"+currPatient.getField(grouped.getPhoto_attribute())+"'";
				}
				query += "),";
			}
		}
		query = query.substring(0,query.length()-1);
		//update query
		Query.update(query);
		updateMissingCompares(user.getName(),group.getGrade_name());
	}

	private static void updateMissingCompares(String grader, String table_name) {
		String query = "UPDATE "+table_name+" SET comparison='equal' WHERE low='_missing_' OR high='_missing_'";
		Query.update(query);
	}

	private static Photo findExtreme(ArrayList<Photo> photos, ArrayList<Ranked_within> extremes, int high_low) {
		String attribute = Helper.process(extremes.get(0).getCompare_field());
		Photo extreme = null;

		Collections.sort(extremes);
		Collections.reverse(extremes); 
		int i=0;
		while(i<extremes.size() && extreme == null) {
			extreme = selectPhotoWith(photos, extremes.get(i).getValue(), attribute);
			i++;
		}

		return extreme;
	}

	private static Photo selectPhotoWith(ArrayList<Photo> photos, String value, String key) {
		for(Photo photo : photos) {
			if(photo.getField(key).equals(value)) {
				return photo;
			}
		}
		return null;
	}

	private static ArrayList<String> getExtremes(HttpServletRequest request, String extreme) {
		ArrayList<String> results = new ArrayList<String>();

		int count = Integer.parseInt(request.getParameter(extreme+"_count"));
		for(int i=0; i<=count; i++) {
			results.add(request.getParameter(extreme+"_"+i));
		}

		return results;
	}

	public void assign_photos(String photo_table, int group_id) {
		String query = "SELECT * FROM "+photo_table+" WHERE ";
		String where = "";
		Set<String> keys = this.getGroup_meta_data().keySet();
		for(String key : keys) {
			where += key+"='"+this.getGroup_meta_data().get(key)+"' AND ";
		}
		setLow_photos((ArrayList<Photo>) (ArrayList)Query.getModel(query+where+Helper.process(this.getCompare_field(group_id))+"='"+this.getLow()+"'",new Photo()));
		setHigh_photos((ArrayList<Photo>) (ArrayList)Query.getModel(query+where+Helper.process(this.getCompare_field(group_id))+"='"+this.getHigh()+"'",new Photo()));
	}

	public static void processCompare(HttpServletRequest request, GradeGroup group) {
		int id = Integer.parseInt(request.getParameter("compare_id"));
		String compare = request.getParameter("compare");
		String query = "UPDATE "+group.getGrade_name()+" SET comparison='"+compare+"' WHERE id="+id;
		Query.update(query);
	}

	public static ArrayList<String> getCSVLines(GradeGroup group, Study study, User user) {
		ArrayList<String> lines = new ArrayList<String>();
		String query = "SELECT * FROM ranked_within WHERE grade_group_id="+group.getId();
		String compare_field = ((Ranked_within)Query.getModel(query,new Ranked_within()).get(0)).getCompare_field();
		ArrayList<Compare> compares = getAllCompares(group.getGrade_name(),user);
		String line = "Grader, Comparision, high "+compare_field+", low "+compare_field;
		Set<String> meta_keys = compares.get(0).getGroup_meta_data().keySet();
		for(String key : meta_keys) {
			line += ", "+key;
		}
		lines.add(line);
		for(Compare compare : compares) {
			if(compare.getComparison() != null) {
				line = compare.getGrader()+", "+compare.getComparison()+", "+compare.getHigh()+", "+compare.getLow();
				for(String key : meta_keys) {
					line += ", "+compare.getMeta_data(key);
				}
				lines.add(line);
			}
		}

		return lines;
	}

	public static ArrayList<Compare> getAllCompares(String userName, String group) {
		String query = "SELECT * FROM "+group+" WHERE grader='"+userName+"'";
		return (ArrayList)Query.getModel(query,new Compare());
	}

	public static ArrayList<Compare> getAllCompares(String group, User user) {
		String query = "SELECT * FROM "+group;
		if(user != null & !user.isStudy_coordinator()) {
			query += " WHERE grader='"+user.getName()+"'";
		}

		return (ArrayList)Query.getModel(query,new Compare());
	}

	public static ArrayList<Compare> getAllCompares(String group) {
		String query = "SELECT * FROM "+group;

		return (ArrayList)Query.getModel(query,new Compare());
	}

	public static class CompareCounts{
		private int total_compares;
		private int compared;

		public CompareCounts(String grader, GradeGroup group) {
			String query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+grader+"' AND comparison IS NOT NULL";
			this.compared = Query.getModel(query,new Compare()).size();
			query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+grader+"'";
			this.total_compares = Query.getModel(query,new Compare()).size();
		}

		/**
		 * @return the total_compares
		 */
		public int getTotal_compares() {
			return total_compares;
		}

		/**
		 * @param total_compares the total_compares to set
		 */
		public void setTotal_compares(int total_compares) {
			this.total_compares = total_compares;
		}

		/**
		 * @return the compared
		 */
		public int getCompared() {
			return compared;
		}

		/**
		 * @param compared the compared to set
		 */
		public void setCompared(int compared) {
			this.compared = compared;
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

	public String getMeta_data(String key) {
		return group_meta_data.get(key);
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
