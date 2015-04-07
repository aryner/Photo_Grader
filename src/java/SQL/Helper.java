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
		return type == FileIO.PHOTO ? insertPhotos(study, picNames) : null;
	}

	private static ArrayList<String> insertPhotos(Study study, ArrayList<String> picNames) {
		ArrayList<String> errors = new ArrayList<String>();
		ArrayList<PhotoNameMetaData> metaData = (ArrayList)new PhotoNameMetaData().getMetaDataSources("study_id='"+study.getId()+"'","position");

		String query = "INSERT INTO "+study.getPhoto_attribute_table_name()+" (";

		for(String picName : picNames) {
		}

		if(picNames.isEmpty()) errors.add(Constants.NO_FILES_SELECTED);
		else Query.update(query);

		return errors;
	}
}
