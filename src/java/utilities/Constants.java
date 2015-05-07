/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author aryner
 */
public class Constants {
	public static final String FILE_SEP = System.getProperty("file.separator");
	public static final String PORT = System.getProperty("os.name").contains("Windows") ? "8084" : "8080";
	public static final String HOME = System.getProperty("user.home");
	public static final String SRC = "http://localhost:"+PORT+"/Photo_Grader/";
	public static final String PIC_PATH = ".."+FILE_SEP+"webapps"+FILE_SEP+"Photo_Grader"+FILE_SEP+"pictures"+FILE_SEP;

	public static final String TAKEN_USERNAME = "That username has areadly been taken";
	public static final String PASSWORDS_DONT_MATCH = "Those passwords do not match";
	public static final String INCORRECT_NAME_PASS = "That username and password combination is not recognized";
	public static final String STUDY_NAME_TAKEN = "That study name is already being used";
	public static final String MISSING_NAME_NUMBER = "You must enter both a study name and an amount of meta-data";
	public static final String REPEAT_NAME = "That study name is already being used";
	public static final String HAS_WRONG_EXTENSION = " has the wrong file extension";
	public static final String NO_FILES_SELECTED = "No files were selected";
}
