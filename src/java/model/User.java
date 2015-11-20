/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import SQL.Query;
import SQL.Helper;

import utilities.Constants;

/**
 *
 * @author aryner
 */
public class User extends Model {
	private int id;
	private String name;
	private String password;
	private int access_level;

	private static final int GRADER = 1;
	private static final int STUDY_COORDINATOR = 2;
	private static final int ADMIN = 4;

	public User() {}

	public User(int id) {
		String query = "SELECT * FROM user WHERE id="+id;
		
		User user = (User) Query.getModel(query,new User()).get(0);

		this.id = id;
		this.name = user.getName();
		this.password = user.getPassword();
		this.access_level = user.getAccess_level();
	}

	public User(int id, String name, String password, int access_level) {
		this.id = id;
		this.name = Helper.unprocess(name);
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

	public static String createUser(String name, String password, String rePassword, String type, HttpSession session) {
		if(password.equals(rePassword)) {
			User user = User.register(name, password);

			if (user == null) {
				session.setAttribute("error", Constants.TAKEN_USERNAME);
				return "/Photo_Grader/register";
			}
			else if (user.isAdmin()) {
				session.setAttribute("user", user); 
				return "/Photo_Grader/admin_page";
			}
			else {
				session.setAttribute("user", user); 
				return "/Photo_Grader/select_study";
			}
		}
		else {
			session.setAttribute("error", Constants.PASSWORDS_DONT_MATCH);
			return "/Photo_Grader/register";
		}
	}

	public static User register(String name, String password) {
		String getQuery = "SELECT * FROM user WHERE name='"+Helper.process(name)+"'";
		ArrayList<Model> users = Query.getModel(getQuery, new User());

		if(!users.isEmpty()) return null;

		String insertQuery = "INSERT INTO user (name, password, access_level) VALUES ("+
			       "'"+Helper.process(name)+"', MD5('"+password+"'), '"+(adminCount()>0?1:4)+"')";
		Query.update(insertQuery);
		
		return (User)Query.getModel(getQuery, new User()).get(0);
	}

	public static User login(String name, String password) {
		String query = "SELECT * FROM user WHERE name='"+Helper.process(name)+"' AND "+
				"password=MD5('"+password+"')";
		ArrayList<Model> user = Query.getModel(query, new User());

		return (User)(user.isEmpty() ? null : user.get(0));
	}

	public void updatePrivileges(HttpServletRequest request) {
		String query = "UPDATE user SET access_level = CASE id";

		int user_count = userCount();
		for(int i=0; i<user_count; i++) {
			int level = 0;
			if(request.getParameter("grader_"+i)!=null) {
				level += 1;
			}
			if(request.getParameter("study_coordinator_"+i)!=null) {
				level += 2;
			}
			if(request.getParameter("admin_"+i)!=null) {
				level += 4;
			}
			int currId = Integer.parseInt(request.getParameter("id_"+i));
			if(currId == this.id) { this.access_level = level; }
			query += " WHEN "+ currId + " THEN " + level;
		}
		query += " END";

		Query.update(query);
	}

	public static int adminCount() {
		String query = "SELECT * FROM user";
		ArrayList<User> users = (ArrayList)Query.getModel(query, new User());
		int count = 0;
		for(User user : users) {
			if(user.isAdmin()) { count++; }
		}

		return count;
	}

	public static ArrayList getUsers() {
		return Query.getModel("SELECT * FROM user", new User());
	}

	public static int userCount() {
		String query = "SELECT * FROM user";
		return Query.getModel(query, new User()).size();
	}

	public boolean isGrader() {
		return (this.access_level & GRADER) == GRADER;
	}

	public boolean isStudy_coordinator() {
		return (this.access_level & STUDY_COORDINATOR) == STUDY_COORDINATOR;
	}

	public boolean isAdmin() {
		return (this.access_level & ADMIN) == ADMIN;
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
