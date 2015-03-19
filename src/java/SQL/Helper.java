/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SQL;
import metaData.*;

/**
 *
 * @author aryner
 */
public class Helper {
	public static String javaToSQLType(String javaType) {
		if(javaType.equals(MetaData.INTEGER+"")) {
			return "int";
		}
		return "varchar(40)";
	}
}
