/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import SQL.*;
import java.sql.*;
import model.*;

/**
 *
 * @author aryner
 */
public class ManualMetaData extends Model implements MetaDataSource {
	private int id;
	private int study_id;
	private String name;
	private int input_type;
	private ArrayList<String> options;

	public static final int TEXT = MetaData.TEXT;
	public static final int RADIO = MetaData.RADIO;
	public static final int CHECKBOX = MetaData.CHECKBOX;

	public ManualMetaData() {
	}

	public ManualMetaData(int id, int study_id, String name, int input_type) {
		this.id = id;
		this.study_id = study_id;
		this.name = Helper.unprocess(name);
		this.input_type = input_type;
		setOptions();
	}

	@Override
	public void setFields(
		int study_id, HttpServletRequest request, 
		int position, String identifier, String identifier_col,
		int type
	) {
		this.setStudy_id(study_id);
		this.setName(request.getParameter("manual_name_"+position));
		this.setInput_type(Integer.parseInt(request.getParameter("manual_type_"+position)));
		this.setOptions(request, (position-1));
	}

	@Override
	public ArrayList<Model> getMetaDataSources(String where, String order){
		String query = "SELECT * FROM photo_data_by_manual "+(where.length()>0?"WHERE "+where:"")+(order.length()>0?" ORDER BY "+order:"");
		return Query.getModel(query, this);
	}

	@Override
	public ManualMetaData getModel(ResultSet resultSet) {
		try {
			ManualMetaData datum = new ManualMetaData(
				resultSet.getInt("id"),resultSet.getInt("study_id"),
				resultSet.getString("name"),resultSet.getInt("input_type")
			);

			return datum;
		} catch(SQLException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public static void updateDB(ArrayList<ManualMetaData> metaData) {
		if(metaData.isEmpty()) return;
		String query = "INSERT INTO photo_data_by_manual (id, study_id, name, input_type) VALUES ";

		for(int i=0; i<metaData.size(); i++) {
			if(i > 0) query += ", ";
			query += "('"+metaData.get(i).getId()+"', '"+metaData.get(i).getStudy_id()+
				 "', '"+Helper.process(metaData.get(i).getName())+"', '"+metaData.get(i).getInput_type()+"')";
		}
		Query.update(query);

		updateOptions(metaData, metaData.get(0).getStudy_id());
	}

	private static void updateOptions(ArrayList<ManualMetaData> metaData, int study_id) {
		ArrayList<String> names = (ArrayList)Query.getField("photo_data_by_manual", 
								    "name", 
							            "study_id='"+study_id+"'", 
								    "id");
		Helper.unprocess(names);
		ArrayList<Long> ids = (ArrayList)Query.getField("photo_data_by_manual", 
								    "id", 
							            "study_id='"+study_id+"'", 
								    "id");
		String query = "INSERT INTO check_radio_option (photo_data_id, value, meta_grade) VALUES ";
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
			postfix += "('"+id+"', '"+Helper.process(value)+"', '"+MetaData.META+"')";
		}
		return postfix;
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	private void setOptions(HttpServletRequest request, int position) {
		this.options = new ArrayList<String>();
		int col = 0;
		String option = request.getParameter((position)+"_option_"+col);
		while (option != null && !option.equals("")) {
			this.options.add(option);
			col++;
			option = request.getParameter((position)+"_option_"+col);
		}
	}

	private void setOptions() {
		this.options = (ArrayList)Query.getField("check_radio_option","value","photo_data_id='"+getId()+"'",null);
		Helper.unprocess(this.options);
	}

	public String getHtml(int index){
		//once the description field is implemented it will replace 'name' here
		String html = "<div class='meta-col question'><h3>"+name+"</h3>";

		int optionNumber = 0;
		switch(input_type) {
			case TEXT:
				html += "<input type='text' class='"+index+"' name='"+name+"' title='"+TEXT+"'>";
				break;
			case RADIO:
				for(String option : options) {
					html += "<span class='span_"+optionNumber+"_"+index+"'></span><input type='radio' class='"+index+"' id='"+optionNumber+"' name='"+name+"' title='"+RADIO+"' value='"+option+"'>"+option+"<br>";
					optionNumber++;
				}
				break;
			case CHECKBOX:
				for(String option : options) {
					html += "<span class='span_"+optionNumber+"_"+index+"'></span><input type='checkbox' class='"+index+"' id='"+optionNumber+"' name='"+name+"_"+option+"' title='"+CHECKBOX+"' value='"+option+"'>"+option+"<br>";
					optionNumber++;
				}
				break;
		}
		html += "</div>";

		return html;
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
	@Override
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
