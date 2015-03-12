/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aryner
 */
public class MetaData {
	private String name;
	private int type;
	private int collection;

	public static final int INTEGER = 1;
	public static final int DECIMAL = 2;
	public static final int STRING = 3;

	public static final int NAME = 1;
	public static final int EXCEL = 2;
	public static final int CSV = 3;
	public static final int MANUAL = 4;

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
	
	public MetaData(String name, int type, int collection) {
		this.name = name;
		this.type = type;
		this.collection = collection;
	}

	public static void makeLists(HttpServletRequest request, ArrayList<MetaData> metaData) {
		int maxCount = Integer.parseInt(request.getParameter("maxCount"));

		for(int i=0; i<maxCount; i++) {
			String name = request.getParameter("name"+i);
			if(name != null && !name.equals("")) {
				int type = Integer.parseInt(request.getParameter("type"+i));
				int collect = Integer.parseInt(request.getParameter("collect"+i));
				metaData.add(new MetaData(name,type,collect));
			}
		}
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the collection
	 */
	public int getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(int collection) {
		this.collection = collection;
	}
}
