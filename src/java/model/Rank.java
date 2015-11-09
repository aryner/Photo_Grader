/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Random;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import SQL.Query;

import metaData.grade.GroupBy;
import metaData.grade.GradeGroup;

/**
 *
 * @author aryner
 */
public class Rank extends Model implements Comparable<Rank>{
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

	public static final int GREATER = 0;
	public static final int LESS = 1;
	public static final int EQUAL = 2;

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
		query += PARENT+" int DEFAULT 0, "+CHILD+" int DEFAULT 0, "+MAIN_CHAIN+" int DEFAULT "+OFF_CHAIN+", "+RANK+" int DEFAULT 0,";

		Query.update(query+postfix);
	}

	public static void processRanking(HttpServletRequest request, GradeGroup group, User user) {
		int right_id = Integer.parseInt(request.getParameter("right_rank"));
		int left_id = Integer.parseInt(request.getParameter("left_rank"));
		request.getSession().setAttribute("right_rank",right_id);
		request.getSession().setAttribute("left_rank",left_id);
		String compare = request.getParameter("compare");

		if (parentlessCount(group.getGrade_name(), user.getName()) > 0 ) {
			assignParentChildRelationship(right_id, left_id, compare, group.getGrade_name());
		} else {
			addToMainChain(request, group, user);
		}
	}

	public static void addToMainChain(HttpServletRequest request, GradeGroup group, User user) {
		int last_compared_rank = Integer.parseInt(request.getParameter("last_compared_rank"));
		int high_rank = Integer.parseInt(request.getParameter("high_rank")+"");
		int low_rank = Integer.parseInt(request.getParameter("low_rank")+"");
		String compare = request.getParameter("compare");

		int comparison = compare.equals("left")?LESS:compare.equals("right")?GREATER:EQUAL;
		switch (comparison) {
			case LESS:
				high_rank = last_compared_rank - 1;
				request.getSession().setAttribute("high_rank",high_rank);
				break;
			case GREATER:
				low_rank = last_compared_rank + 1;
				request.getSession().setAttribute("low_rank",low_rank);
				break;
			case EQUAL:
				low_rank = last_compared_rank;
				high_rank = low_rank-1;
		}
		HttpSession session = request.getSession();
		session.setAttribute("last_compared_rank",last_compared_rank);
		session.setAttribute("high_rank",high_rank);
		session.setAttribute("low_rank",low_rank);
		if (high_rank < low_rank) {
			int to_insert_id = Integer.parseInt(request.getParameter("right_rank"));
			insertRank(group.getGrade_name(),low_rank,to_insert_id,user.getName());
			session.removeAttribute("last_compared_rank");
		}
	}

	private static void insertRank(String table_name, int bottom_of_shift, int insert_id, String grader) {
		String query = "UPDATE "+table_name+" SET rank=rank-1 WHERE grader='"+grader+"' AND rank<"+bottom_of_shift+" AND rank>0";
		Query.update(query);
		query = "UPDATE "+table_name+" SET rank="+(bottom_of_shift-1)+" WHERE id="+insert_id;
		Query.update(query);
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

	public static Pair getPairToRank(int group_id, int ranker_id, String photo_table, HttpServletRequest request) {
		GradeGroup grade_group = new GradeGroup(group_id);
		User user = new User(ranker_id);
		generateRanks(grade_group, user, photo_table);

		Pair pair = Pair.getUncomparedPair(grade_group,user.getName());
		if (pair.isFull()) { 
			pair.setPhotos(photo_table,grade_group);
			return pair;
		}

		if (parentlessCount(grade_group.getGrade_name(),user.getName()) > 0) {
			int level = clearChildren(grade_group.getGrade_name(),user.getName());
			if (!pair.isEmpty()) {
				pair.getParent().setOddRankOut(grade_group.getGrade_name(), level);
			}
			return getPairToRank(group_id,ranker_id,photo_table,request);
		}

		//if we got this far, we are building the main chain
		pair.getPairForMainChain(grade_group,user.getName(),request);
		if (pair.isFull()) { 
			pair.setPhotos(photo_table,grade_group);
		}

		return pair;
	}

	private static void generateRanks(GradeGroup grade_group, User user, String photo_table) {
		String query = "SELECT * FROM "+grade_group.getGrade_name()+" WHERE grader='"+user.getName()+"'";
		ArrayList ranks = Query.getModel(query, new Rank());
		
		if (ranks.isEmpty()) {
			ArrayList<Photo> photos = Photo.getPossibleCombinations(grade_group, photo_table);

			query = "INSERT INTO "+grade_group.getGrade_name()+" (grader";
			/*
			for (Object field : photos.get(0).getFields().keySet()) {
				query += ", "+field.toString();
			}
			*/
			for(int i=0; i<grade_group.groupBySize(); i++) {
				query += ", "+grade_group.getGroupBy(i).getPhoto_attribute();
			}
			query += ") VALUES ";
			int count = 0;

			for(Photo photo : photos) {
				if (count != 0) { query += ","; }
				count++;

				query += "(";
				String temp = "'"+user.getName()+"'";
				/*
				for (Object field : photo.getFields().keySet()) {
					temp += ",'"+photo.getField(field.toString())+"'";
				}
				*/
				for(int i=0; i<grade_group.groupBySize(); i++) {
					String attribute = grade_group.getGroupBy(i).getPhoto_attribute();
					if(attribute.equals(Grade.FILENAME)) {
						temp += ",'"+photo.getName()+"'";
					} else {
						temp += ",'"+photo.getField(attribute)+"'";
					}
				}
				query += temp + ")";
			}

			Query.update(query);
		}
	}

	public static void removeCategory(HttpServletRequest request, Study study) {
		String query = "SELECT * FROM photo_grade_group WHERE grade_rank=1 AND study_id="+study.getId()+
				" AND name='"+request.getParameter("category")+"'";
		GradeGroup group = (GradeGroup)Query.getModel(query,new GradeGroup()).get(0);
		query = "DELETE FROM group_by WHERE grade_group_id="+group.getId();
		Query.update(query);
		query = "DROP TABLE "+group.getGrade_name();
		Query.update(query);
		query = "DELETE FROM photo_grade_group WHERE id="+group.getId();
		Query.update(query);
	}

	public void setOddRankOut(String table_name, int low) {
		String query = "UPDATE "+table_name+" SET parent_id=-1 WHERE id="+this.id;
		Query.update(query);
		query = "UPDATE "+table_name+" SET child_id="+low+" WHERE id="+this.id;
		Query.update(query);
	}

	public static int clearChildren(String table_name, String grader) {
		int low = (Integer)Query.getField(table_name,"child_id","grader='"+grader+"'","child_id asc").get(0);
		low--;
		String query = "UPDATE "+table_name+" SET child_id=0 WHERE parent_id=0 AND child_id >= 0 AND grader='"+grader+"'";
		Query.update(query);
		query = "UPDATE "+table_name+" SET child_id="+low+" WHERE parent_id>0 AND child_id=0 AND grader='"+grader+"'";
		Query.update(query);
		return low;
	}

	public static int parentlessCount(String table_name, String grader) {
		String query = "SELECT * FROM "+table_name+" WHERE parent_id=0 AND grader='"+grader+"'";
		return Query.getModel(query,new Rank()).size();
	}

	public static ArrayList<Rank> getAllRanks(String userName, String group) {
		String query = "SELECT * FROM "+group+" WHERE grader='"+userName+"' ORDER BY rank DESC";
		return (ArrayList)Query.getModel(query,new Rank());
	}

	public static ArrayList<Rank> getAllRanks(String group, User user) {
		String query = "SELECT * FROM "+group;
		if(user != null & !user.isStudy_coordinator()) {
			query += " WHERE grader='"+user.getName()+"'";
		}

		return (ArrayList)Query.getModel(query+" ORDER BY rank DESC",new Rank());
	}

	public static ArrayList<Rank> getAllRanks(String group) {
		String query = "SELECT * FROM "+group;

		return (ArrayList)Query.getModel(query+" ORDER BY rank DESC",new Rank());
	}

	public static void startChain(ArrayList<Rank> ranks, String group) {
		int head = 0;
		int head_id = 0;
		Rank headRank = null;

		for(Rank rank : ranks) {
			if (rank.getChild_id() < head) {
				head = rank.getChild_id();
				head_id = rank.getId();
				headRank = rank;
			}
		}
		Rank child = getChild(ranks,headRank);

		if (headRank != null) {
			String query = "UPDATE "+group+" SET rank = "+ranks.size()+
				", main_chain="+ON_CHAIN+", parent_id="+ranks.size()+
				" WHERE id="+head_id;
			Query.update(query);
			query = "UPDATE "+group+" SET rank = "+(ranks.size()-1)+
				", main_chain="+ON_CHAIN+", parent_id="+(ranks.size()-1)+
				" WHERE id="+child.getId();
			Query.update(query);
			headRank.setRank(ranks.size());
			child.setRank(ranks.size()-1);
			headRank.setMain_chain(ON_CHAIN);
			child.setMain_chain(ON_CHAIN);
			headRank.setParent_id(ranks.size());
			child.setParent_id(ranks.size()-1);
		}
	}

	public static Rank getChild(ArrayList<Rank> ranks, Rank parent) {
		for (Rank rank : ranks) {
			if (rank.getParent_id() == parent.getId() && rank.getChild_id() == parent.getChild_id()+1) {
				return rank;
			}
		}
		return null;
	}

	public static int findCurrentLevel(ArrayList<Rank> onChain, ArrayList<Rank> offChain, ArrayList<Rank> currLevel, int level) {
		int rankedCount = 0;
		for(int i=offChain.size()-1; i>=0; i--) {
			if (offChain.get(i).getChild_id() == -level) {
				currLevel.add(offChain.get(i));
				if (offChain.get(i).getRank() > 0) {
					rankedCount++;
					onChain.add(offChain.get(i));
				}
			}
		}
		return rankedCount;
	}

	private static Pair compareForChain(ArrayList<Rank> onChain, ArrayList<Rank> offChain, int level, String tableName, HttpServletRequest request, String grader) {
		if (offChain.isEmpty()) {
			//this user has finished ranking this set
			return null;
		}

		ArrayList<Rank> currLevel = new ArrayList<Rank>();
		int rankedCount = findCurrentLevel(onChain,offChain,currLevel,level);
		if(rankedCount == currLevel.size()) { 
			//set main chain
			setMainChain(tableName,grader);
			//return method to get pair on next level (recuse with next level?)
			offChain.removeAll(onChain);
			return compareForChain(onChain,offChain, level-1, tableName,request,grader);
		}

		int parentRank;
		do {
			parentRank = getNextParentRank(rankedCount,offChain.size());
			Collections.sort(onChain);
			parentRank = Math.min(parentRank,onChain.get(onChain.size()-1).getRank());
			int parentIndex = -1;

			int tempRank = parentRank;
			while (tempRank > 1) {
				parentIndex = -1;
				for(int i=0; i<onChain.size() && parentIndex <0; i++) {
					if (onChain.get(i).getParent_id() == tempRank && onChain.get(i).getMain_chain() == ON_CHAIN) {
						parentIndex = i;
					}
				}
				if (parentIndex != -1) {
					for (Rank needle: currLevel) {
						if(needle.getParent_id() == onChain.get(parentIndex).getId() && needle.getRank() == 0) {
							//needle is the next element to be inserted
							//return a binary insert compare method with this as an argument 
							//insertRanked(onChain,currLevel);
							//Collections.sort(onChain);
							return binaryInsertionComparison(onChain,needle,onChain.get(parentIndex).getRank(),onChain.get(0).getRank(),request);
						}
					}
				}
				tempRank--;
			}
			rankedCount++;
		} while (offChain.size() > 1 && parentRank < (onChain.size()+offChain.size()));

		//We only get to this loop if it is the odd rank out to be inserted (no parent)
		for(Rank rank : currLevel) {
			if(rank.getRank() == 0){
				//insertRanked(onChain,currLevel);
				//Collections.sort(onChain);
				return binaryInsertionComparison(onChain,currLevel.get(0), onChain.get(onChain.size()-1).getRank(),onChain.get(0).getRank(),request);
			}
		}
		//Should not get this far
		System.err.println("went too far in compareForChain");
		return null;
	}

	private static void insertRanked(ArrayList<Rank> onChain, ArrayList<Rank> toBeAdded) {
		for(Rank rank : toBeAdded) {
			if(rank.getRank()>0) { onChain.add(rank); }
		}
	}

	private static Pair binaryInsertionComparison(ArrayList<Rank> onChain, Rank toInsert, int high, int low, HttpServletRequest request) {
		int compare = ((high - low) / 2) + low;
		Pair toCompare = null;
		for(Rank check : onChain) {
			if(check.getRank() == compare) {
				toCompare = new Pair(check,toInsert);
				break;
			}
		}
		request.setAttribute("high_rank",high);
		request.setAttribute("low_rank",low);
		return toCompare;
	}

	public static ArrayList<String> getCSVLines(GradeGroup group, Study study, User user) {
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<String> fields = new ArrayList<String>();
		String currline = "Grader";
		for(int i=0; i<group.groupBySize();i++) {
			fields.add(group.getGroupBy(i).getPhoto_attribute());
			currline += ", "+group.getGroupBy(i).getPhoto_attribute();
		}
		currline += ", rank";
		lines.add(currline);

		ArrayList<Rank> ranks = getAllRanks(group.getGrade_name(),user);
		for(Rank rank : ranks) {
			if(rank.getRank() > 0) {
				currline = rank.getGrader();
				for(String field : fields) {
					currline += ", "+rank.getGroup_meta_value(field);
				}
				currline += ", "+rank.getRank();
				lines.add(currline);
			}
		}

		return lines;
	}

	//lowBound is the lowest assigned ranking on the main chain (-1?)
	private static int getNextParentRank(int k, int lowBound) {
		int currIndex = (int)(Math.pow(2,k+1)+Math.pow(-1,k))/3;
		return lowBound + currIndex;
	}

	private static void setMainChain(String tableName, String grader) {
		String query = "SELECT * FROM "+tableName+" WHERE rank > 0 AND grader='"+grader+"' ORDER BY rank ASC";
		Rank tail = (Rank)Query.getModel(query,new Rank()).get(0);
		if(tail.getRank() == 1) { return; }
		query = "SELECT * FROM "+tableName+" WHERE main_chain="+OFF_CHAIN+" AND parent_id="+tail.getId()+" AND grader='"+grader+"' ORDER BY child_id ASC";
		Rank newTail = (Rank)Query.getModel(query,new Rank()).get(0);
		query = "UPDATE "+tableName+" SET rank="+(tail.getRank()-1)+" WHERE id="+newTail.getId();
		Query.update(query);
		query = "UPDATE "+tableName+" SET main_chain="+ON_CHAIN+" WHERE rank > 0 AND grader='"+grader+"'";
		Query.update(query);
		query = "UPDATE "+tableName+" SET parent_id=rank WHERE main_chain="+ON_CHAIN+" AND grader='"+grader+"'";
		Query.update(query);
	}

	public static boolean startedChain(ArrayList<Rank> ranks) {
		for (Rank rank : ranks) {
			if (rank.getRank() > 0) { return true; }
		}
		return false;
	}

	public static void groupOnOffChain(ArrayList<Rank> ranks, ArrayList<Rank> onChain, ArrayList<Rank> offChain) {
		for(Rank rank : ranks) {
			if(rank.getMain_chain() == ON_CHAIN) {
				onChain.add(rank);
			} else {
				offChain.add(rank);
			}
		}
	}

	public static int getLevel(ArrayList<Rank> onChain, ArrayList<Rank> offChain) {
		int level = -onChain.get(0).getChild_id();
		for (Rank rank : onChain) {
			level = Math.min(-rank.getChild_id(),level);
		}
		boolean nextLevel = true;
		for(Rank rank : offChain) {
			if (rank.getChild_id() == -level) {
				nextLevel = false;
				break;
			}
		}

		return nextLevel?level-1:level;
	}

	@Override
	public int compareTo(Rank r1) {
		return this.rank - r1.getRank();
	}

	public static class Pair {
		private Rank parent;
		private Rank child;
		private ArrayList<Photo> parent_photos;
		private ArrayList<Photo> child_photos;

		public Pair(Rank parent, Rank child) {
			this.parent = parent;
			this.child = child;
		}

		public static Pair getUncomparedPair(GradeGroup group, String ranker) {
			String query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+ranker+"' "+
					" AND "+CHILD+"=0 AND "+PARENT+"=0";
			ArrayList<Rank> AllRanks = (ArrayList) Query.getModel(query,new Rank());
			ArrayList<Rank> ranks = new ArrayList<Rank>();
			Random rand = new Random(System.currentTimeMillis());
			ranks.add(AllRanks.size()>0?AllRanks.remove(rand.nextInt(AllRanks.size())):null);
			ranks.add(AllRanks.size()>0?AllRanks.remove(rand.nextInt(AllRanks.size())):null);

			return new Pair(ranks.get(0),ranks.get(1));
		}

		public void getPairForMainChain(GradeGroup group, String userName, HttpServletRequest request) {
			//Check for request variables
			if(request.getSession().getAttribute("last_compared_rank") != null) {
				Pair temp = continueBinaryInsertion(request, group, userName);
				setParent(temp.getParent());
				setChild(temp.getChild());
				return;
			}

			ArrayList<Rank> ranks = getAllRanks(userName, group.getGrade_name());
			boolean started_chain = startedChain(ranks);
			if (!started_chain) { startChain(ranks, group.getGrade_name()); }
			ArrayList<Rank> onChain = new ArrayList<Rank>();
			ArrayList<Rank> offChain = new ArrayList<Rank>();
			groupOnOffChain(ranks, onChain, offChain);
			int level = getLevel(onChain, offChain);

			Pair temp = compareForChain(onChain, offChain, level, group.getGrade_name(),request,userName);
			if (temp != null) {
				setParent(temp.getParent());
				setChild(temp.getChild());
			}
		}

		private Pair continueBinaryInsertion(HttpServletRequest request, GradeGroup group, String userName) {
				//a rank is in the process of being inserted to the main chain
				int high_rank = Integer.parseInt(request.getSession().getAttribute("high_rank")+"");
				int low_rank = Integer.parseInt(request.getSession().getAttribute("low_rank")+"");
				int to_insert_id = Integer.parseInt(request.getSession().getAttribute("right_rank")+"");
				String query = "SELECT * FROM "+group.getGrade_name()+" WHERE (rank>0 AND grader='"+userName+
						"') OR (id="+to_insert_id+" AND grader='"+userName+"')";
				ArrayList<Rank> main_chain = (ArrayList)Query.getModel(query,new Rank());
				Collections.sort(main_chain);
				//to_insert should have rank 0 so should be in position 0
				Rank to_insert = main_chain.remove(0);
				return binaryInsertionComparison(main_chain, to_insert, high_rank, low_rank, request);
		}

		public final void setPhotos(String table_name, GradeGroup group) {
			String query;

			if (parent != null) {
				query = "SELECT * FROM "+table_name+" WHERE ";
				String postfix = "";

				for(GroupBy grouped_by : group.getGroupBy()) {
					if (postfix.length() > 0) { postfix += " AND "; }
					String key = grouped_by.getPhoto_attribute(); 
					if(key.equals(Grade.FILENAME)) {
						postfix += "name='"+parent.getGroup_meta_value(key)+"'";
					} else {
						postfix += key+"='"+parent.getGroup_meta_value(key)+"'";
					}
				}
				this.parent_photos = (ArrayList)Query.getModel(query+postfix,new Photo());
			}
			if (child != null) {
				query = "SELECT * FROM "+table_name+" WHERE ";
				String postfix = "";

				for(GroupBy grouped_by : group.getGroupBy()) {
					if (postfix.length() > 0) { postfix += " AND "; }
					String key = grouped_by.getPhoto_attribute(); 
					if(key.equals(Grade.FILENAME)) {
						postfix += "name='"+child.getGroup_meta_value(key)+"'";
					} else {
						postfix += key+"='"+child.getGroup_meta_value(key)+"'";
					}
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

		public final boolean isFull() {
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

	public static class RankCounts {
		private int total_ranks;
		private int ranks;

		public RankCounts(String grader, GradeGroup group) {
			String query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+grader+"'";
			this.total_ranks = Query.getModel(query,new Rank()).size() * 2;

			query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+grader+"' AND parent_id != 0";
			this.ranks = Query.getModel(query,new Rank()).size();
			query = "SELECT * FROM "+group.getGrade_name()+" WHERE grader='"+grader+"' AND rank > 0";
			this.ranks += Query.getModel(query,new Rank()).size();
		}

		/**
		 * @return the total_grades
		 */
		public int getTotal_ranks() {
			return total_ranks;
		}

		/**
		 * @param total_grades the total_grades to set
		 */
		public void setTotal_ranks(int total_grades) {
			this.total_ranks = total_grades;
		}

		/**
		 * @return the graded
		 */
		public int getRanks() {
			return ranks;
		}

		/**
		 * @param graded the graded to set
		 */
		public void setRanked(int graded) {
			this.ranks = graded;
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
