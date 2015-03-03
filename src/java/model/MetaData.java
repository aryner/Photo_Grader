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
	
	public MetaData(String name, int type, int collection) {
		this.name = name;
		this.type = type;
		this.collection = collection;
	}

	public static void makeLists(
				HttpServletRequest request,
				ArrayList<MetaData> name, ArrayList<MetaData> excel,
				ArrayList<MetaData> csv, ArrayList<MetaData> manual
	) {
		
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
