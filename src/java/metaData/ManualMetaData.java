/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import SQL.*;

/**
 *
 * @author aryner
 */
public class ManualMetaData implements MetaDataSource {
	private int id;
	private int study_id;
	private String name;
	private int input_type;
	private ArrayList<String> options;

	public static final int TEXT = MetaData.TEXT;
	public static final int RADIO = MetaData.RADIO;
	public static final int CHECKBOX = MetaData.CHECKBOX;

	@Override
	public void setFields(
		int study_id, HttpServletRequest request, 
		int position, String identifier, String identifier_col,
		int type
	) {
		this.setStudy_id(study_id);
		this.setName(request.getParameter("manual_name_"+position));
		this.setInput_type(Integer.parseInt(request.getParameter("manual_type_"+position)));
		this.setOptions(request, position);
	}

	public static void updateDB(ArrayList<ManualMetaData> metaData) {
		if(metaData.isEmpty()) return;
		String query = "INSERT INTO photo_data_by_manual (id, study_id, name, input_type) VALUES ";

		for(int i=0; i<metaData.size(); i++) {
			if(i > 0) query += ", ";
			query += "('"+metaData.get(i).getId()+"', '"+metaData.get(i).getStudy_id()+
				 "', '"+metaData.get(i).getName()+"', '"+metaData.get(i).getInput_type()+"')";
		}
		Query.update(query);

		updateOptions(metaData, metaData.get(0).getStudy_id());
	}

	private static void updateOptions(ArrayList<ManualMetaData> metaData, int study_id) {
		ArrayList<String> names = (ArrayList)Query.getField("photo_data_by_manual", 
								    "name", 
							            "study_id='"+study_id+"'", 
								    "id");
		ArrayList<Long> ids = (ArrayList)Query.getField("photo_data_by_manual", 
								    "id", 
							            "study_id='"+study_id+"'", 
								    "id");
		String query = "INSERT INTO check_radio_option (photo_data_id, value) VALUES ";
		String postfix = "";

		for(int i=0; i<names.size(); i++) {
			postfix = matchOptionsForUpdate(postfix, i, metaData, names, ids);
		}

		Query.update(query+postfix);
	}

	private static String matchOptionsForUpdate(String postfix, int index, 
						    ArrayList<ManualMetaData> metaData,
						    ArrayList<String> names, 
						    ArrayList<Long> ids
						   ) {
		for(ManualMetaData datum : metaData) {
			if(names.get(index).equals(datum.getName())) {
				postfix = matchOptionsToIds(postfix, ids.get(index), datum.getOptions());
				break;
			}
		}
		return postfix;
	}

	private static String matchOptionsToIds(String postfix, long id, ArrayList<String> values) {
		for(String value : values) {
			if(postfix.length() != 0) postfix += ", ";
			postfix += "('"+id+"', '"+value+"')";
		}
		return postfix;
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	private void setOptions(HttpServletRequest request, int position) {
		this.options = new ArrayList<String>();
		int col = 0;
		String option = request.getParameter((position+1)+"_option_"+col);
		while (option != null && !option.equals("")) {
			this.options.add(option);
			col++;
			option = request.getParameter((position+1)+"_option_"+col);
		}
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
	 * @return the input_type
	 */
	public int getInput_type() {
		return input_type;
	}

	/**
	 * @param input_type the input_type to set
	 */
	public void setInput_type(int input_type) {
		this.input_type = input_type;
	}
}
