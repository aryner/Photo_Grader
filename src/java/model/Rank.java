/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.ArrayList;

import SQL.Query;

import metaData.grade.GroupBy;

/**
 *
 * @author aryner
 */
public class Rank {

	public static void createTable(int group_id) {
		String tableName = "rank_"+Query.getField("photo_grade_group","grade_name","id='"+group_id+"'",null).get(0);
		String query = "CREATE TABLE IF NOT EXISTS "+tableName+" ( "+
				"id int unsigned AUTO_INCREMENT,grader varchar(50),";
		String postfix = "PRIMARY KEY(id)) ENGINE=INnoDB";
		ArrayList<GroupBy> grouping = GroupBy.getGroup(group_id);

		for(GroupBy group : grouping) {
			query += group.getPhoto_attribute()+" varchar(40),";
		}
		//TODO add columns for ranking

		Query.update(query+postfix);
	}
}
