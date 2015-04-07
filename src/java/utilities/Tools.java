/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.*;

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
}
