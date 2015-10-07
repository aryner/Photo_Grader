/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;

import SQL.Query;

import metaData.grade.GroupBy;
import metaData.grade.GradeGroup;

/**
 *
 * @author aryner
 */
public class Rank extends Model {
	private int id;
	private String grader;
	private Map<String,String> group_meta_data;
	private int parent_id;
	private int child_id;
	private int main_chain;
	private int rank;

	public static final String PARENT = "parent_id";
	public static final String CHILD = "child_id";
	public static final String MAIN_CHAIN = "main_chain";
	public static final String RANK = "rank";

	public static final int ON_CHAIN = 1;
	public static final int OFF_CHAIN = 0;

	public Rank() {}

	public Rank(int id, String grader, Map<String,String> group_meta_data, int parent_id, int child_id, int main_chain, int rank) {
		this.id = id;
		this.grader = grader;
		this.group_meta_data = group_meta_data;
		this.parent_id = parent_id;
		this.child_id = child_id;
		this.main_chain = main_chain;
		this.rank = rank;
	}

	@Override
	public Rank getModel(ResultSet resultSet) {
		try {
			Map<String,String> groupMeta = new HashMap<String,String>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colCount = metaData.getColumnCount();
			for(int i=3; i<=colCount; i++) {
				String colName = metaData.getColumnLabel(i);
				if(colName.indexOf("_") == 0) 
					groupMeta.put(colName,resultSet.getString(colName));
			}

			return new Rank(resultSet.getInt("id"),resultSet.getString("grader"),groupMeta,resultSet.getInt(PARENT),
					resultSet.getInt(CHILD),resultSet.getInt(MAIN_CHAIN),resultSet.getInt(RANK));
		}
		catch(SQLException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static void createTable(int group_id) {
		String tableName = "rank_"+Query.getField("photo_grade_group","grade_name","id='"+group_id+"'",null).get(0);
		String query = "CREATE TABLE IF NOT EXISTS "+tableName+" ( "+
				"id int unsigned AUTO_INCREMENT,grader varchar(50),";
		String postfix = "PRIMARY KEY(id)) ENGINE=INnoDB";
		ArrayList<GroupBy> grouping = GroupBy.getGroup(group_id);

		for(GroupBy group : grouping) {
			query += group.getPhoto_attribute()+" varchar(40),";
		}
		query += PARENT+" int DEFAULT -1, "+CHILD+" int DEFAULT -1, "+MAIN_CHAIN+" int DEFAULT "+OFF_CHAIN+", "+RANK+" int DEFAULT -1,";

		Query.update(query+postfix);
	}

	public static void processRanking(HttpServletRequest request, GradeGroup group, User user) {
		int right_id = Integer.parseInt(request.getParameter("right_rank"));
		int left_id = Integer.parseInt(request.getParameter("left_rank"));
		String compare = request.getParameter("compare");

		assignParentChildRelationship(right_id, left_id, compare, group.getGrade_name());
	}

	public static void assignParentChildRelationship(int right_id, int left_id, String compare, String category) {
		if(compare.equals("right")) {
			String query = "UPDATE "+category+" SET child_id="+left_id+" WHERE id="+right_id;
			Query.update(query);
			query = "UPDATE "+category+" SET parent_id="+right_id+" WHERE id="+left_id;
			Query.update(query);
		} else { //treating 'equal' the same as choosing left
			String query = "UPDATE "+category+" SET child_id="+right_id+" WHERE id="+left_id;
			Query.update(query);
			query = "UPDATE "+category+" SET parent_id="+left_id+" WHERE id="+right_id;
			Query.update(query);
		}
	}

	public static Pair getPairToRank(int group_id, int ranker_id, String photo_table) {
		//TODO
		GradeGroup grade_group = new GradeGroup(group_id);
		User user = new User(ranker_id);
		generateRanks(grade_group, user, photo_table);

		Pair pair = new Pair(grade_group,user.getName(),photo_table);
		if (pair.isFull()) { return pair; }

		//if only one has no head make compairisons build the main chain
		//else clear children from those with no parents and get recursive
		//TODO

		return null;
	}

	private static void generateRanks(GradeGroup grade_group, User user, String photo_table) {
		String query = "SELECT * FROM "+grade_group.getGrade_name()+" WHERE grader='"+user.getName()+"'";
		ArrayList ranks = Query.getModel(query, new Rank());
		
		if (ranks.isEmpty()) {
			ArrayList<Photo> photos = Photo.getPossibleCombinations(grade_group, photo_table);

			query = "INSERT INTO "+grade_group.getGrade_name()+" (grader";
			for (Object field : photos.get(0).getFields().keySet()) {
				query += ", "+field.toString();
			}
			query += ") VALUES ";
			int count = 0;

			for(Photo photo : photos) {
				if (count != 0) { query += ","; }
				count++;

				query += "(";
				String temp = "'"+user.getName()+"'";
				for (Object field : photo.getFields().keySet()) {
					temp += ",'"+photo.getField(field.toString())+"'";
				}
				query += temp + ")";
			}

			Query.update(query);
		}
	}

	public static class Pair {
		private Rank parent;
		private Rank child;
		private ArrayList<Photo> parent_photos;
		private ArrayList<Photo> child_photos;

		public Pair(GradeGroup group, String ranker, String photo_table) {
			String query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+ranker+"' "+
					" AND "+CHILD+"=-1 AND "+PARENT+"=-1 LIMIT 2";
			ArrayList<Rank> ranks = (ArrayList) Query.getModel(query,new Rank());

			this.parent = ranks.get(0);
			this.child = ranks.get(1);

			this.setPhotos(photo_table,group);
		}

		public Pair(Rank parent, Rank child) {
			this.parent = parent;
			this.child = child;
		}

		public final void setPhotos(String table_name, GradeGroup group) {
			String query = "SELECT * FROM "+table_name;
			Photo photo = (Photo)Query.getModel(query,new Photo()).get(0);

			if (parent != null) {
				query = "SELECT * FROM "+table_name+" WHERE ";
				String postfix = "";

				for(GroupBy grouped_by : group.getGroupBy()) {
					if (postfix.length() > 0) { postfix += " AND "; }
					String key = grouped_by.getPhoto_attribute(); 
					postfix += key+"='"+parent.getGroup_meta_value(key)+"'";
				}
				this.parent_photos = (ArrayList)Query.getModel(query+postfix,new Photo());
			}
			if (child != null) {
				query = "SELECT * FROM "+table_name+" WHERE ";
				String postfix = "";

				for(GroupBy grouped_by : group.getGroupBy()) {
					if (postfix.length() > 0) { postfix += " AND "; }
					String key = grouped_by.getPhoto_attribute(); 
					postfix += key+"='"+child.getGroup_meta_value(key)+"'";
				}
				this.child_photos = (ArrayList)Query.getModel(query+postfix,new Photo());
			}
		}

		public boolean contains(Rank needle) {
			return parent.equals(needle) || child.equals(needle);
		}

		public boolean hasParent() {
			return parent != null;
		}

		public boolean hasChild() {
			return child != null;
		}

		public boolean isFull() {
			return hasParent() && hasChild();
		}

		public boolean isEmpty() {
			return !hasParent() && !hasChild();
		}

		/**
		 * @return the parent
		 */
		public Rank getParent() {
			return parent;
		}

		/**
		 * @param parent the parent to set
		 */
		public void setParent(Rank parent) {
			this.parent = parent;
		}

		/**
		 * @return the child
		 */
		public Rank getChild() {
			return child;
		}

		/**
		 * @param child the child to set
		 */
		public void setChild(Rank child) {
			this.child = child;
		}

		/**
		 * @return the parent_photos
		 */
		public ArrayList<Photo> getParent_photos() {
			return parent_photos;
		}

		/**
		 * @param parent_photos the parent_photos to set
		 */
		public void setParent_photos(ArrayList<Photo> parent_photos) {
			this.parent_photos = parent_photos;
		}

		/**
		 * @return the child_photos
		 */
		public ArrayList<Photo> getChild_photos() {
			return child_photos;
		}

		/**
		 * @param child_photos the child_photos to set
		 */
		public void setChild_photos(ArrayList<Photo> child_photos) {
			this.child_photos = child_photos;
		}

	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 61 * hash + this.id;
		hash = 61 * hash + (this.grader != null ? this.grader.hashCode() : 0);
		hash = 61 * hash + (this.group_meta_data != null ? this.group_meta_data.hashCode() : 0);
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
		final Rank other = (Rank) obj;
		if (this.id != other.id) {
			return false;
		}
		if ((this.grader == null) ? (other.grader != null) : !this.grader.equals(other.grader)) {
			return false;
		}
		if (this.group_meta_data != other.group_meta_data && (this.group_meta_data == null || !this.group_meta_data.equals(other.group_meta_data))) {
			return false;
		}
		return true;
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
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the main_chain
	 */
	public int getMain_chain() {
		return main_chain;
	}

	/**
	 * @param main_chain the main_chain to set
	 */
	public void setMain_chain(int main_chain) {
		this.main_chain = main_chain;
	}

	/**
	 * @return the child_id
	 */
	public int getChild_id() {
		return child_id;
	}

	/**
	 * @param child_id the child_id to set
	 */
	public void setChild_id(int child_id) {
		this.child_id = child_id;
	}

	/**
	 * @return the parent_id
	 */
	public int getParent_id() {
		return parent_id;
	}

	/**
	 * @param parent_id the parent_id to set
	 */
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
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

	public Object getGroup_meta_value(String key) {
		return group_meta_data.get(key);
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
}
