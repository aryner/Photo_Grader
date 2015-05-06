/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author aryner
 */
public class Tools {
	public static String generateTableName(String base, ArrayList<String> usedNames) {
		Random rand = new Random();
		int postfix = rand.nextInt(10000000);
		while (usedNames.contains(base+postfix)) {
			postfix = rand.nextInt(10000000);
		}

		return base+postfix;
	}

	public static String getGeneratedNumber(String generatedName) {
		return generatedName.contains("_") ? 
		       generatedName.substring(generatedName.lastIndexOf("_")+1, 
			       		       generatedName.length()
		       ) : "";
	}

	public static int getRegexIndex(String haystack, String regex, int index) {
		int offset = 0;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(haystack);
		if(!matcher.find()) return -1;

		int position = matcher.start();
		while((position+offset) <= index) {
			offset += position+1;
			haystack = haystack.substring(position+1,haystack.length());
			matcher = pattern.matcher(haystack);
			if(!matcher.find()) return -1;
			position = matcher.start();
		}

		return position+offset;
	}

	public static String getQuestionHtml(String question, String input) {
		return "<div class='meta-col'><h3>"+question+"</h3>"+input+"</div>";
	}

	public static boolean hasExcelExtension(String fileName) {
		String ext = getExtension(fileName).toLowerCase();

		return  ext.equals("xls") ? true :
			ext.equals("xlsx") ? true :
			ext.equals("xlsm") ? true :
			ext.equals("xlt") ? true :
			ext.equals("xlm") ? true :
			ext.equals("xltx") ? true :
			ext.equals("xltm") ? true :
			ext.equals("xlsb") ? true :
			ext.equals("xla") ? true :
			ext.equals("xlam") ? true :
			ext.equals("xll") ? true :
			ext.equals("xlw");
	}

	public static boolean hasCSVExtension(String fileName) {
		String ext = getExtension(fileName).toLowerCase();

		return ext.equals("csv");
	}

	public static String getExtension(String fileName) {
		if(fileName.contains("."))
			return fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		return "";
	}

	public static int [] defaultIntArray(int size) {
		int [] result = new int[size];
		for(int i=0; i<size; i++) result[i] = -1;
		return result;
	}

	public static boolean contains(int [] haystack, int needle) {
		for(int check : haystack) 
			if(check == needle) return true;
		return false;
	}

	public static boolean contains(ArrayList<String> haystack, String needle) {
		for(String straw : haystack) 
			if(needle.equals(straw))
				return true;
		return false;
	}
}
