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

import SQL.Query;

import metaData.grade.GroupBy;

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

	public static Pair getPairToRank(int group_id, int ranker_id) {
		return null;
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

	public class Pair {
		private Rank parent;
		private Rank child;

		public Pair(Rank parent, Rank child) {
			this.parent = parent;
			this.child = child;
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
