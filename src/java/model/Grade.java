/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import SQL.*;

/**
 *
 * @author aryner
 */
public class Grade {
	private static final String FILENAME = "[FILENAME]";

	public static void createGroup(int group_id, String attr_table_name, HttpServletRequest request) {
		int groupOptionCount = Integer.parseInt(request.getParameter("groupOptionCount"));
		ArrayList<String> columns = Photo.getMetaDataKeys(attr_table_name);
		ArrayList<String> attributes = new ArrayList<String>();

		for(int i=-1; i<groupOptionCount; i++) {
			if(request.getParameter("groupBy_"+i)!=null) {
				if(i>=0) 
					attributes.add(columns.get(i));
				else 
					attributes.add(FILENAME);
			}
		}

		String query = "INSERT INTO group_by (grade_group_id, photo_attribute) VALUES ";
		for(int i=0; i<attributes.size(); i++) {
			if(i>0) query += ", ";
			query += "('"+group_id+"', '"+attributes.get(i)+"')";
		}

		Query.update(query);
	}
	public static void createQuestions(int group_id, HttpServletRequest request) {
		int questionCount = Integer.parseInt(request.getParameter("questionCount"));
	}
	public static void createTable(int group_id) {
	}
}
