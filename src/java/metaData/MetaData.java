/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import model.Study;

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

	public static final int START = 1;
	public static final int END = 2;
	public static final int NUMBER = 3;
	public static final int DELIMITER = 4;
	public static final int BEFORE = 5;
	public static final int AFTER = 6;
	public static final int NEXT_NUMBER = 7;
	public static final int NEXT_LETTER = 8;
	public static final int NEXT_NOT_NUMBER = 9;
	public static final int NEXT_NOT_LETTER = 10;
	
	public MetaData(String name, int type, int collection) {
		this.name = name;
		this.type = type;
		this.collection = collection;
	}

	public static void makeLists(HttpServletRequest request, ArrayList<MetaData> metaData) {
		int maxCount = Integer.parseInt(request.getParameter("maxCount"));

		for(int i=0; i<maxCount; i++) {
			String name = request.getParameter("name"+i);
			if(name != null && !name.equals("")) {
				int type = Integer.parseInt(request.getParameter("type"+i));
				int collect = Integer.parseInt(request.getParameter("collect"+i));
				metaData.add(new MetaData(name,type,collect));
			}
		}
	}

	public static void processDefinitions(Study study, HttpServletRequest request) {
		//get all the data types
		Map<String,String> types = createNameTypeMap(request);

		//get all specifications for all the name meta data
		ArrayList<PhotoNameMetaData> nameMeta = new ArrayList<PhotoNameMetaData>();
		int metaNameCount = Integer.parseInt(request.getParameter("sectionCount"));
		for(int i=1; i<=metaNameCount; i++) {
			PhotoNameMetaData metaData = new PhotoNameMetaData();
			setFields(metaData, study.getId(), request, i);

			nameMeta.add(metaData);
		}

		PhotoNameMetaData.updateDB(nameMeta);
	}

	private static void setFields(PhotoNameMetaData metaData, int study_id, HttpServletRequest request, int position) {
		metaData.setStudy_id(study_id);
		metaData.setName(request.getParameter("type_"+NAME+"_"+1));
		metaData.setPosition(position);
		metaData.setUsed(metaData.getName().equals("_not-meta_")?PhotoNameMetaData.FALSE:PhotoNameMetaData.TRUE);
		metaData.setStarts(Integer.parseInt(request.getParameter("start_"+position)));
		metaData.setEnds(Integer.parseInt(request.getParameter("end_"+position)));
		metaData.setStart_flag(getDelimiterFlag(request, metaData.getStarts(), position, START));
		metaData.setEnd_flag(getDelimiterFlag(request, metaData.getEnds(), position, END));
	}

	private static String getDelimiterFlag(HttpServletRequest request, int type, int index, int side) {
		if(type == DELIMITER || type == NUMBER) {
			return (side == START) ? request.getParameter("start_"+type+"_"+index) : request.getParameter("end_"+type+"_"+index);
		}
		return null;
	}

	private static Map<String,String> createNameTypeMap(HttpServletRequest request) {
		int metaDataCount = Integer.parseInt(request.getParameter("fieldsLength"));
		Map<String,String> dataTypes = new HashMap<String,String>();
		for(int i=0; i<metaDataCount; i++) {
			String name_type = request.getParameter("data_type_"+i);
			dataTypes.put(extractName(name_type),extractType(name_type));
		}

		return dataTypes;
	}
	private static String extractName(String name_type) {
		return name_type.substring(0,name_type.indexOf("_"));
	}
	private static String extractType(String name_type) {
		return name_type.substring(name_type.indexOf("_")+1);
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
