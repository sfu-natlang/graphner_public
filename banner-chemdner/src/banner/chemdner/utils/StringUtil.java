/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banner.chemdner.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tsendee
 */
public class StringUtil {
    
    
    	/**
         * Copied from BioLemmatiser tool
	 * Convert special unicode characters into modern English spelling
	 * 
	 * @param input
	 *            an input string
	 * @return modern English spelling
	 */
	public static String unicodeHandler(String input) {
		// define the mapping between special unicode characters and modern
		// English spelling
		Map<String, String> specialUnicodeCharToModernEnglishMapping = new HashMap<String, String>();

		specialUnicodeCharToModernEnglishMapping.put("u00E6", "ae");
		specialUnicodeCharToModernEnglishMapping.put("u0153", "oe");
		specialUnicodeCharToModernEnglishMapping.put("u00E4", "a");
		specialUnicodeCharToModernEnglishMapping.put("u00E0", "a");
		specialUnicodeCharToModernEnglishMapping.put("u00E1", "a");
		specialUnicodeCharToModernEnglishMapping.put("u0113", "e");
		specialUnicodeCharToModernEnglishMapping.put("u00E9", "e");
		specialUnicodeCharToModernEnglishMapping.put("u00E8", "e");
		specialUnicodeCharToModernEnglishMapping.put("u00EB", "e");
		specialUnicodeCharToModernEnglishMapping.put("u00EF", "i");
		specialUnicodeCharToModernEnglishMapping.put("u00F1", "n");
		specialUnicodeCharToModernEnglishMapping.put("u014D", "o");
		specialUnicodeCharToModernEnglishMapping.put("u00F6", "o");
		specialUnicodeCharToModernEnglishMapping.put("u00F4", "o");
		specialUnicodeCharToModernEnglishMapping.put("u016B", "u");
		specialUnicodeCharToModernEnglishMapping.put("u00FA", "u");

		String output = input;
		for (String unicode : specialUnicodeCharToModernEnglishMapping.keySet()) {
			String regex = "\\" + unicode;
			output = output.replaceAll(regex, specialUnicodeCharToModernEnglishMapping.get(unicode));
		}

		return output;
	}
}
