/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 * @author aryner
 */
public class ManualMetaData implements MetaDataSource {
	@Override
	public void setFields(
		int study_id, HttpServletRequest request, 
		int position, String identifier, String identifier_col,
		int type
	) {
	}

	public static void updateDB(ArrayList<ManualMetaData> metaData) {
	}
}
