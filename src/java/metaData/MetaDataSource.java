/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package metaData;

import java.util.*;
import model.*;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author aryner
 */
public interface MetaDataSource {
	public void setFields(
		int study_id, HttpServletRequest request, 
		int position, String identifier, String identifier_col,
		int type
	);

	public ArrayList<Model> getMetaDataSources(String where, String order);
	public String getName();
}
