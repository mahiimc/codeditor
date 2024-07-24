package com.executor.parser;

import com.executor.exception.CodeSyntaxException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

public class CodeParser {

	public static CompilationUnit parse(String code) {
		ParseResult<CompilationUnit> parseResult = new JavaParser().parse(code);
		if (!parseResult.isSuccessful()) {
			throw new CodeSyntaxException("Error occurred while parsing rhe code");
		}
		return parseResult.getResult().get();
	}

}
