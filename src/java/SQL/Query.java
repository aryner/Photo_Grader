/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SQL;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList; 

import javax.naming.Context; 
import javax.sql.DataSource;
import javax.naming.InitialContext; 
import javax.naming.NamingException;

import model.Model;

/**
 *
 * @author aryner
 */
public class Query {
	private static final String CONTEXT = "java:comp/env";
	private static final String DATASOURCE = "photo_grader";
	private static final int UPDATE = 0;
	private static final int QUERY = 1;

	public static ArrayList<Model> getModel(String query, Model model) {
		ArrayList<Model> result = new ArrayList<Model>();
		SQLResult sqlResult = null;

		try {
			sqlResult = new Query().getResult(query,QUERY);
			ResultSet resultSet = sqlResult.getResultSet();

			while(resultSet.next()) {
				result.add(model.getModel(resultSet));
			}
		}
		catch (NamingException e) {
			System.err.println(query);
			e.printStackTrace(System.err);
		}
		catch (SQLException e) {
			System.err.println(query);
			e.printStackTrace(System.err);
		}
		finally {
			if (sqlResult != null) sqlResult.close();
		}

		return result;
	}

	public static ArrayList<Object> getField(String table, String field, String where, String order) {
		ArrayList<Object> result = new ArrayList<Object>();
		SQLResult sqlResult = null;

		try {
			String query = "SELECT "+field+" FROM "+table+(where != null ? " WHERE "+where : "")+
					(order != null ? " ORDER BY "+order : "");
			sqlResult = new Query().getResult(query,QUERY);
			ResultSet resultSet = sqlResult.getResultSet();

			while(resultSet.next()) {
				Object obj = resultSet.getObject(field);
				if(obj instanceof String) result.add(Helper.unprocess((String)obj));
				else result.add(obj);
			}
		}
		catch (NamingException e) {
			e.printStackTrace(System.err);
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		finally {
			if (sqlResult != null) sqlResult.close();
		}

		return result;
	}

	public static int getCount(String query, Model model) {
		return getModel(query,model).size();
	}

	public static void update(String query) {
		SQLResult sqlResult = null;
		
		try {
			sqlResult = new Query().getResult(query,UPDATE);
		}
		catch (NamingException e) {
			System.err.println(query);
			e.printStackTrace(System.err);
		}
		catch (SQLException e) {
			System.err.println(query);
			e.printStackTrace(System.err);
		}
		finally {
			if (sqlResult != null) sqlResult.close();
		}
	}

	public static ArrayList<String> getColumnNames(String query) {
		ArrayList<String> names = new ArrayList<String>();
		SQLResult sqlResult = null;

		try {
			sqlResult = new Query().getResult(query,QUERY);
			ResultSet resultSet = sqlResult.getResultSet();
			ResultSetMetaData metaData = resultSet.getMetaData();

			for(int i=1; i<=metaData.getColumnCount(); i++) {
				names.add(metaData.getColumnLabel(i));
			}
		}
		catch (NamingException e) {
			System.err.println(query);
			e.printStackTrace(System.err);
		}
		catch (SQLException e) {
			System.err.println(query);
			e.printStackTrace(System.err);
		}
		finally {
			if (sqlResult != null) sqlResult.close();
		}

		return names;
	}

	private static void close(Connection con, Statement stmt, ResultSet resultSet) {
		try {
			if(resultSet != null) resultSet.close();
			if(stmt != null) stmt.close();
			if(con != null) con.close();
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
	}

	private SQLResult getResult(String query, int method) throws SQLException, NamingException {
		query = query.replace("\\","\\\\");
		InitialContext initialContext = new InitialContext();
		Context context = (Context)initialContext.lookup(CONTEXT);
		DataSource dataSource = (DataSource)context.lookup(DATASOURCE);
		Connection con = dataSource.getConnection();

		Statement stmt = con.createStatement();

		if (method == QUERY) {
			ResultSet resultSet = stmt.executeQuery(query); 
			return new SQLResult(resultSet, stmt, con);
		}
		else {
			stmt.executeUpdate(query);
			return new SQLResult(null, stmt, con);
		}
	}

	private class SQLResult {
		private final ResultSet resultSet;
		private final Statement stmt;
		private final Connection con;

		public SQLResult(ResultSet resultSet, Statement stmt, Connection con) {
			this.resultSet = resultSet;
			this.stmt = stmt;
			this.con = con;
		}

		public ResultSet getResultSet() {
			return resultSet;
		}

		public void close() {
			Query.close(con,stmt,resultSet);
		}
	}
}
