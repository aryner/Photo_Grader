/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 * @author aryner
 */
public class TableMetaData implements MetaDataSource {
	private int id;
	private int study_id;
	private String name;
	private String col_name;
	private String identifier; //name of the photo attribute used as an identifier
	private String identifier_col;
	private int table_type;

	public TableMetaData() { }

	public TableMetaData(
		int id, int study_id, String name, String col_name,
	        String identifier, String identifier_col, 
		int table_type
	) {
		this.id = id;
		this.study_id = study_id;
		this.name = name;
		this.col_name = col_name;
		this.identifier = identifier;
		this.identifier_col = identifier_col;
		this.table_type = table_type;
	}

	@Override
	public void setFields(
		int study_id, HttpServletRequest request, 
		int position, String identifier, String identifier_col,
		int type
	) {
		this.setId(study_id);
		this.setName(request.getParameter((type == MetaData.EXCEL?"excel":"csv")+"_"+position));
		this.setCol_name(request.getParameter((type == MetaData.EXCEL?"excel":"csv")+"_column_"+position));
		this.setIdentifier(identifier);
		this.setIdentifier_col(identifier_col);
		this.setTable_type(type);
	}

	public static void updateDB(ArrayList<TableMetaData> metaData) {
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
	 * @return the study_id
	 */
	public int getStudy_id() {
		return study_id;
	}

	/**
	 * @param study_id the study_id to set
	 */
	public void setStudy_id(int study_id) {
		this.study_id = study_id;
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
	 * @return the col_name
	 */
	public String getCol_name() {
		return col_name;
	}

	/**
	 * @param col_name the col_name to set
	 */
	public void setCol_name(String col_name) {
		this.col_name = col_name;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the identifier_col
	 */
	public String getIdentifier_col() {
		return identifier_col;
	}

	/**
	 * @param identifier_col the identifier_col to set
	 */
	public void setIdentifier_col(String identifier_col) {
		this.identifier_col = identifier_col;
	}

	/**
	 * @return the table_type
	 */
	public int getTable_type() {
		return table_type;
	}

	/**
	 * @param table_type the table_type to set
	 */
	public void setTable_type(int table_type) {
		this.table_type = table_type;
	}
}
