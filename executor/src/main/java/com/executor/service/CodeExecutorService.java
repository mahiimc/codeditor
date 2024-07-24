package com.executor.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.springframework.stereotype.Service;

import com.executor.dto.Result;
import com.executor.parser.CodeParser;
import com.executor.utils.ClassNameExtractor;
import com.executor.utils.CompiledClassLoader;
import com.executor.utils.CustomFileManager;
import com.github.javaparser.ast.CompilationUnit;

@Service
public class CodeExecutorService {

	public Result execute(String code) {
		Result result = new Result();
		code = unescapeJsonString(code);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String mainClass = ClassNameExtractor.extract(code);

		if (mainClass == null) {
			throw new RuntimeException("Unable to find the class name.");
		}

		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(new Date());

		String directoryPath = "src/main/resources/compiled/" + timeStamp;

		File directory = new File(directoryPath);

		if (!directory.mkdirs()) {
			throw new RuntimeException("Error while creating directory");
		}
		

		try (StringWriter output = new StringWriter()) {

			StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);

			CustomFileManager fileManager = new CustomFileManager(stdFileManager, directoryPath);
			
			CompilationUnit compilationUnit = CodeParser.parse(code);
			code = compilationUnit.toString();
			JavaSourceFromString file = new JavaSourceFromString(mainClass, code);
			Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(file);
			JavaCompiler.CompilationTask task = compiler.getTask(output, fileManager, null,
					Arrays.asList("-d", directoryPath), null, compilationUnits);

			boolean success = task.call();
			if (!success) {
				result.setError(output.toString());
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);

			PrintStream oldOut = System.out;
			PrintStream oldErr = System.err;

			System.setOut(ps);
			System.setErr(ps);
			try {
				CompiledClassLoader classLoader = new CompiledClassLoader(directoryPath);
				Class<?> clazz = classLoader.loadClass(mainClass);
				Method main = clazz.getMethod("main", String[].class);
				main.setAccessible(true);
				Object out = main.invoke(null, (Object) new String[] {});
				result.setConsoleOutput(baos.toString());
				if (out != null) {
					result.setOutput(out);
				}
			} catch (ClassNotFoundException e) {
			} finally {
				System.setOut(oldOut);
				System.setErr(oldErr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String unescapeJsonString(String jsonString) {
        if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }
        
        jsonString = jsonString.replace("\\\"", "\"")
                               .replace("\\r\\n", "\r\n")
                               .replace("\\n", "\n");
        
        return jsonString;
    }

	class JavaSourceFromString extends SimpleJavaFileObject {

		final String code;

		JavaSourceFromString(String name, String code) {
			super(java.net.URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

}
