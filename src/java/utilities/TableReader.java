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
		
		ArrayList<TableMetaData> meta = study.getExcelTableMetaData();
		int[] columns = getExcelColumns(sheet, meta);

		ArrayList<String> updates = new ArrayList<String>();
		for(int i=1; i<columns.length; i++) {
			updates.add(getUpdate(sheet, columns, meta, errors, i));
		}

		String queryPrefix = "UPDATE "+Helper.process(study.getPhoto_attribute_table_name())+" SET ";
		for(String update : updates) {
			Query.update(queryPrefix+update);
		}
	}
	
	private static void extractCSVData(Study study, ArrayList<String> errors) {
		try {
			ArrayList<TableMetaData> meta = study.getCSVTableMetaData();
			Scanner lineScanner = new Scanner(new File(FileIO.TEMP_DIR+FileIO.CSV_FILE));
			Scanner cellScanner = new Scanner(lineScanner.nextLine());
			ArrayList<String> fieldNames = new ArrayList<String>();
			int identifierIndex = csvHeaders(cellScanner, meta, fieldNames);
			ArrayList<String> updates = new ArrayList<String>();
			for(String fieldName : fieldNames) updates.add("");

			while (lineScanner.hasNextLine()) {
				updateUpdateLines(lineScanner, fieldNames, identifierIndex, updates);
			}
			endCSVUpdate(fieldNames, identifierIndex, updates);

			String queryPrefix = "UPDATE "+Helper.process(study.getPhoto_attribute_table_name())+ " SET ";
			for(String update : updates)  {
				if(update.length() > 0) {
					Query.update(queryPrefix+update);
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			errors.add("The file was not found");
		}
	}

	private static void updateUpdateLines(Scanner lineScanner, ArrayList<String> fieldNames, int index, ArrayList<String> updates) {
		Scanner cellScanner = new Scanner(lineScanner.nextLine());
		cellScanner.useDelimiter(",");
		String currentUpdate;
		ArrayList<String> values = getCSVLine(cellScanner);

		for(int i=0; i<fieldNames.size(); i++) {
			if (i != index) {
				currentUpdate = updates.get(i);
				if (currentUpdate.length() == 0) {
					currentUpdate += Helper.process(fieldNames.get(i)) + " = case "+Helper.process(fieldNames.get(index));
				}

				currentUpdate += " WHEN '"+values.get(index)+"' THEN '"+values.get(i)+"'";
				updates.set(i,currentUpdate);
			}
		}
	}

	private static void endCSVUpdate(ArrayList<String> fieldNames, int index, ArrayList<String> updates){
		for(int i=0; i<fieldNames.size(); i++) {
			if(i != index) {
				String currentUpdate = updates.get(i);
				currentUpdate += " ELSE "+Helper.process(fieldNames.get(i))+" END";
				updates.set(i,currentUpdate);
			}
		}
	}

	private static ArrayList<String> getCSVLine(Scanner line) {
		line.useDelimiter(",");
		ArrayList<String> values = new ArrayList<String>();
		while (line.hasNext()) {
			values.add(line.next());
		}

		return values;
	}

	private static int csvHeaders(Scanner headerLine, ArrayList<TableMetaData> meta, ArrayList<String> fieldNames) {
		headerLine.useDelimiter(",");
		int index = 0;
		boolean identifierFound = false;

		while(headerLine.hasNext()) {
			String colName = headerLine.next();
			for(TableMetaData datum : meta) {
				if(Helper.unprocess(datum.getCol_name()).equals(colName)) {
					fieldNames.add(datum.getName());
					break;
				}
				else if(Helper.unprocess(datum.getIdentifier_col()).equals(colName)) {
					fieldNames.add(datum.getIdentifier());
					identifierFound = true;
					break;
				}
			}
			if(!identifierFound) index++;
		}
		return index;
	}

	private static String getUpdate(XSSFSheet sheet, int [] columns, ArrayList<TableMetaData> meta, ArrayList<String> errors, int col) {
		String update = Helper.process(meta.get(col-1).getName()) + " = case "+Helper.process(meta.get(0).getIdentifier());

		for(int i=1; i<sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			update += " WHEN '"+getCellContents(row.getCell(columns[0]))+"' THEN '"+getCellContents(row.getCell(columns[col]))+"'";
		}
		update += " ELSE "+Helper.process(meta.get(col-1).getName()) + " END";

		return update;
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
			throw new UploadException(UploadException.MISSING_COLUMNS);
		}

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

	private static String getCellContents(Cell cell) {
		if(cell == null) return "";
		if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
			return cell.getRichStringCellValue().toString();
		}
		return cell.getNumericCellValue() + "";
	}
}
