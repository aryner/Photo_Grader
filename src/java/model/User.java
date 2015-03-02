/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.*;
import java.util.*;
import SQL.*;

/**
 *
 * @author aryner
 */
public class User extends Model {
	private int id;
	private String name;
	private String password;
	private int access_level;

	public User() {
	}

	public User(int id, String name, String password, int access_level) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.access_level = access_level;
	}

	@Override
	public User getModel(ResultSet resultSet) {
		try {
			return new User(
					resultSet.getInt("id"),resultSet.getString("name"),
					resultSet.getString("password"),resultSet.getInt("access_level")
					);
		} catch(SQLException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public static User register(String name, String password) {
		String getQuery = "SELECT * FROM user WHERE name='"+name+"'";
		ArrayList<Model> users = Query.getModel(getQuery, new User());

		if(!users.isEmpty()) return null;

		String insertQuery = "INSERT INTO user (name, password) VALUES ("+
			       "'"+name+"', MD5('"+password+"'))";
		Query.update(insertQuery);
		
		return (User)Query.getModel(getQuery, new User()).get(0);
	}

	public static User login(String name, String password) {
		String query = "SELECT * FROM user WHERE name='"+name+"' AND "+
				"password=MD5('"+password+"')";
		ArrayList<Model> user = Query.getModel(query, new User());

		return (User)(user.isEmpty() ? null : user.get(0));
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the access_level
	 */
	public int getAccess_level() {
		return access_level;
	}

	/**
	 * @param access_level the access_level to set
	 */
	public void setAccess_level(int access_level) {
		this.access_level = access_level;
	}
	
}
