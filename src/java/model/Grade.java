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
		ArrayList<String> questions = new ArrayList<String>();
		ArrayList<Integer> types = new ArrayList<Integer>();

		for(int i=0; i<questionCount; i++) {
			questions.add(request.getParameter("question_"+i));
			types.add(Integer.parseInt(request.getParameter("type_"+i)));
		}

		String query = "INSERT INTO question (grade_group_id, question, q_type) VALUES ";
		for(int i=0; i<types.size(); i++) {
			if(i>0) query += ", ";
			query += "('"+group_id+"', '"+questions.get(i)+"', '"+types.get(i)+"')";
		}

		Query.update(query);
		createOptions(group_id, questions, types, request);
	}

	private static void createOptions(int group_id, ArrayList<String> questions, ArrayList<Integer> types, HttpServletRequest request) {
	}

	public static void createTable(int group_id) {
	}
}
