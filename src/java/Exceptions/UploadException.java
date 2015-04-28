/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Exceptions;

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

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
}
