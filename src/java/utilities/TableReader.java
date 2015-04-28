/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.*;
import model.*;
import metaData.*;
import Exceptions.*;
import java.io.*;
import org.apache.poi.xssf.usermodel.*; 

/**
 *
 * @author aryner
 */
public class TableReader {
	public static void extractTableData(File file, Study study, ArrayList<String> errors) {
		try {
			if(file.getName().equals(FileIO.EXCEL_FILE)) {
				extractExcelData(study, errors);
			}
			else {
				extractCSVData(study, errors);
			}
		} catch (FileNotFoundException e) {
			errors.add("There was a problem uploading the table data, please try again");
		} catch (IOException e) {
			errors.add("There was a problem uploading the table data, please try again");
		} catch (UploadException e) {
			switch(e.getType()) {
				case UploadException.MISSING_COLUMNS:
					errors.add("The uploaded table was missing columns, check that all columns are in the table you are uploading");
					break;
			}
		}
	}

	private static void extractExcelData(Study study, ArrayList<String> errors) 
			throws FileNotFoundException, IOException, UploadException  {
		FileInputStream file = new FileInputStream(FileIO.TEMP_DIR+FileIO.EXCEL_FILE);
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		int rowEnd = sheet.getLastRowNum();
		int colEnd = sheet.getRow(0).getLastCellNum();

		int[] columns = getExcelColumns(sheet, colEnd, study.getExcelTableMetaData());
		//TOFINISH
	}
	
	private static void extractCSVData(Study study, ArrayList<String> errors) {
		//TODO
	}

	private static int[] getExcelColumns(XSSFSheet sheet, int colEnd, ArrayList<TableMetaData> meta) 
			throws UploadException {
		int [] columns = new int[meta.size()];

		//TOFINISH
		return columns;
	}
}
