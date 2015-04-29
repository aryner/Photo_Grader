/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SQL;

import model.*;
import java.util.*;
import metaData.*;
import utilities.*;
import java.io.*;

/**
 *
 * @author aryner
 */
public class Helper {
	public static String javaToSQLType(String javaType) {
		if(javaType.equals(MetaData.INTEGER+"")) {
			return "int";
		}
		return "varchar(40)";
	}

	public static ArrayList<String> insertAndUpdateUploads(int type, Study study, ArrayList<String> picNames) {
		return type == FileIO.PHOTO ? insertPhotos(study, picNames) : updateTableData(study);
	}

	private static ArrayList<String> insertPhotos(Study study, ArrayList<String> picNames) {
		ArrayList<String> errors = new ArrayList<String>();
		ArrayList<PhotoNameMetaData> metaData = (ArrayList)new PhotoNameMetaData().getMetaDataSources("study_id='"+study.getId()+"'","position");

		String query = "INSERT INTO "+study.getPhoto_attribute_table_name()+" (name, path";
		for(String name : PhotoNameMetaData.extractNames(metaData)) {
			query += ", "+Helper.process(name);
		}
		query += ") VALUES ";

		String postfix = "";
		for(String picName : picNames) {
			if(postfix.length() != 0) postfix += ", ";
			postfix += "('"+picName+"', '"+FileIO.BASE_PICTURE_DIR+Tools.getGeneratedNumber(study.getPhoto_attribute_table_name())+Constants.FILE_SEP+"'";
			for(String attribute : PhotoNameMetaData.extractAttributes(picName, metaData, errors)) {
				postfix += ", '"+attribute+"'";
			}
			postfix += ")";
		}
		query += postfix;

		System.out.println(query);
		if(picNames.isEmpty()) errors.add(Constants.NO_FILES_SELECTED);
		else Query.update(query);

		return errors;
	}

	private static ArrayList<String> updateTableData(Study study) {
		ArrayList<String> errors = new ArrayList<String>();

		File dir = new File(FileIO.TEMP_DIR);
		File [] fileList = dir.listFiles();
		for(File file : fileList) {
			if(file.isFile()) {
				if(Tools.hasExcelExtension(file.getName())) {
				}
				else if(Tools.hasCSVExtension(file.getName())) {
				}
			}
		}

		return errors;
	}

	public static String process(String string) {
		if(string.equals("photo_name")) return "name";
		return string.contains("_") ? string : "_"+string;
	}

	public static void process(ArrayList<String> list) {
		for(int i=0; i<list.size(); i++) {
			list.set(i, process(list.get(i)));
		}
	}

	public static String unprocess(String string) {
		int index = string.indexOf("_");
		return index >= 0 ? string.substring(index+1,string.length()) : string;
	}

	public static void unprocess(ArrayList<String> list) {
		for(int i=0; i<list.size(); i++) {
			list.set(i, unprocess(list.get(i)));
		}
	}
}
