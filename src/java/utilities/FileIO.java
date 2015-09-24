/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.*;
import java.io.*;
import model.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.fileupload.FileItem; 
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload; 

/**
 *
 * @author aryner
 */
public class FileIO {
	public static final int PHOTO = 1;
	public static final int EXCEL = 2;
	public static final int CSV = 3;
	public static final int TABLE = 4;

	public static final int UPLOAD_SIZE_THRESHOLD = 100 * 1024;
	public static final int MAX_UPLOAD_SIZE = 4000000 * 1024;
	public static final String EXCEL_FILE = "excel.xls";
	public static final String CSV_FILE = "csv.csv";
	public static final String TEMP_DIR = ".."+Constants.FILE_SEP+"webapps"+Constants.FILE_SEP+"temp"+Constants.FILE_SEP;

	public static final String BASE_PICTURE_DIR = ".."+Constants.FILE_SEP+"webapps"+Constants.FILE_SEP+"Photo_Grader"+Constants.FILE_SEP+"pictures"+Constants.FILE_SEP;

	public static ArrayList<String> upload(HttpServletRequest request, int type, Study study) {
		ArrayList<String> errors = new ArrayList<String>();
		ArrayList<String> picNames = type == PHOTO ? new ArrayList<String>() : null;

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(UPLOAD_SIZE_THRESHOLD);
		new File(TEMP_DIR).mkdirs();
		factory.setRepository(new File(TEMP_DIR));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAX_UPLOAD_SIZE);

		try {
			List fileItems = upload.parseRequest(request);
			Iterator iterator = fileItems.iterator();

			while(iterator.hasNext()) {
				FileItem fileItem = (FileItem)iterator.next();
				String fileName = fileItem.getName();

				errors.addAll(
					type == PHOTO ? uploadPhotos(fileItem, fileName, picNames, study) :
					uploadTable(fileItem, fileName, study)
				);
			}
		} catch(org.apache.commons.fileupload.FileUploadException e) {
			e.printStackTrace(System.err);
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}

		errors.addAll(SQL.Helper.insertAndUpdateUploads(type, study, picNames));
		try {
			FileUtils.cleanDirectory(new File(TEMP_DIR));
		} catch(java.io.IOException e) {
			e.printStackTrace(System.err);
		}

		return errors;
	}

	private static ArrayList<String> uploadPhotos(FileItem fileItem, String fileName, ArrayList<String> picNames, Study study) 
			throws Exception {
		ArrayList<String> errors = new ArrayList<String>();

		if(hasPhotoExtension(fileName)) {
			String fileDir = BASE_PICTURE_DIR+Tools.getGeneratedNumber(study.getPhoto_attribute_table_name())+Constants.FILE_SEP;
			new File(fileDir).mkdirs();
			File file = new File(fileDir+fileName);
			fileItem.write(file);

			picNames.add(fileName);
		}
		else {
			errors.add(fileName+Constants.HAS_WRONG_EXTENSION);
		}

		return errors;
	}

	private static ArrayList<String> uploadTable(FileItem fileItem, String fileName, Study study) 
			throws Exception {
		ArrayList<String> errors = new ArrayList<String>();

		new File(TEMP_DIR).mkdirs();
		File file = new File(TEMP_DIR+(Tools.hasExcelExtension(fileName)?EXCEL_FILE : CSV_FILE));
		fileItem.write(file);

		TableReader.extractTableData(file, study, errors);

		return errors;
	}

	public static void createCSV(ArrayList<String> csv, String fileName) {
		int count = 0;
		String suffix = "";

		while(new File(Constants.HOME + Constants.FILE_SEP + "Desktop" + Constants.FILE_SEP + fileName + suffix + ".csv").exists()) {
			count++;
			suffix = "("+count+")";
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(Constants.HOME + Constants.FILE_SEP + "Desktop" + Constants.FILE_SEP + fileName + suffix + ".csv");

			for(String line : csv) {
				fileOut.write((line+"\n").getBytes());
			}

			fileOut.close();
		} 
		catch (java.io.FileNotFoundException e) {
			e.printStackTrace(System.err);
		}
		catch (java.io.IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public static String getExtension(String fileName) {
		return fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()) : "";
	}

	public static boolean hasPhotoExtension(String fileName) {
		String extension = getExtension(fileName).toLowerCase();

		return extension.equals("jpg") ? true : 
		       extension.equals("jpeg") ? true : 
		       extension.equals("png") ? true :
		       extension.equals("gif");
	}
}

