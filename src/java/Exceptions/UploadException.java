/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Exceptions;

import java.util.List;

/**
 *
 * @author aryner
 */
public class UploadException extends Exception {
	private int type;

	public static final int MISSING_COLUMNS = 1;

	public UploadException(String message) {
		super(message);
	}

	public UploadException(int type) {
		this.type = type;
	}

	public void populateErrorList(List<String> errors) {
		switch(this.getType()) {
			case UploadException.MISSING_COLUMNS:
				errors.add("The uploaded table was missing columns, check that all columns are in the table you are uploading");
				break;
		}
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
}
