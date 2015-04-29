/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.*;
import model.*;
import SQL.*;
import metaData.*;
import Exceptions.*;
import java.io.*;
import org.apache.poi.xssf.usermodel.*; 
import org.apache.poi.ss.usermodel.*; 

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
			e.populateErrorList(errors);
		}
	}

	private static void extractExcelData(Study study, ArrayList<String> errors) 
			throws FileNotFoundException, IOException, UploadException  {
		FileInputStream file = new FileInputStream(FileIO.TEMP_DIR+FileIO.EXCEL_FILE);
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		int[] columns = getExcelColumns(sheet, study.getExcelTableMetaData());
		//TOFINISH
	}
	
	private static void extractCSVData(Study study, ArrayList<String> errors) {
		//TODO
	}

	private static int[] getExcelColumns(XSSFSheet sheet, ArrayList<TableMetaData> meta) 
			throws UploadException {
		int [] columns = Tools.defaultIntArray(meta.size()+1);

		Row row = sheet.getRow(0);
		for(int i=row.getFirstCellNum(); i<row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			String colName = cell.getRichStringCellValue().toString();

			checkColumn(columns, colName, i, meta);
		}

		if(Tools.contains(columns, -1)) {
			throw new UploadException(1);
		}

		//TOFINISH
		return columns;
	}

	private static void checkColumn(int [] columns, String colName, int colIndex, ArrayList<TableMetaData> meta) {
		if (colName.equals(Helper.unprocess(meta.get(0).getIdentifier_col()))) {
			columns[0] = colIndex;
		}
		else {
			for(int i=0; i<meta.size(); i++) {
				if(colName.equals(Helper.unprocess(meta.get(i).getCol_name()))) {
					columns[i+1] = colIndex;
					return;
				}
			}
		}
	}
}
