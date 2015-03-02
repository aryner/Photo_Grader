/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SQL;

import java.sql.*;
import java.util.*; 
import javax.naming.Context; 
import javax.sql.DataSource;
import javax.naming.InitialContext; 
import model.*;

/**
 *
 * @author aryner
 */
public class Query {
	public static ArrayList<Model> getModel(String query, Model model) {
		ArrayList<Model> result = new ArrayList<Model>();
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;

		try {
			InitialContext initialContext = new InitialContext();
			Context context = (Context)initialContext.lookup("java:comp/env");
			DataSource dataSource = (DataSource)context.lookup("photo_grader");
			con = dataSource.getConnection();

			stmt = con.createStatement();

			resultSet = stmt.executeQuery(query); 

			while(resultSet.next()) {
				result.add(model.getModel(resultSet));
			}
		}
		catch (javax.naming.NamingException e) {
			e.printStackTrace(System.out);
		}
		catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		finally {
			close(con, stmt, resultSet);
		}

		return result;
	}

	public static void update(String query) {
		Connection con = null;
		Statement stmt = null;
		
		try {
			InitialContext initialContext = new InitialContext();
			Context context = (Context)initialContext.lookup("java:comp/env");
			DataSource dataSource = (DataSource)context.lookup("photo_grader");
			con = dataSource.getConnection();

			stmt = con.createStatement();

			stmt.executeUpdate(query);
		}
		catch (javax.naming.NamingException e) {
			e.printStackTrace(System.out);
		}
		catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		finally {
			close(con, stmt, null);
		}
	}

	private static void close(Connection con, Statement stmt, ResultSet resultSet) {
		try {
			if(resultSet != null) resultSet.close();
			if(stmt != null) stmt.close();
			if(con != null) con.close();
		}
		catch (SQLException e) {
			e.printStackTrace(System.out);
		}
	}
}
