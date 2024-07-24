package com.executor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassNameExtractor {

	public static String extract(String code) {
		Pattern pattern = Pattern.compile("class\\s+([a-zA-Z_$][a-zA-Z\\d_$]*)");
		Matcher matcher = pattern.matcher(code);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

}
