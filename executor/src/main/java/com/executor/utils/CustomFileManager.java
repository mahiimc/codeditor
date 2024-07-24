package com.executor.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

public class CustomFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	
	    private final String directoryPath;

	    public CustomFileManager(StandardJavaFileManager fileManager, String directoryPath) {
	        super(fileManager);
	        this.directoryPath = directoryPath;
	    }

	    @Override
	    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
	        File file = new File(directoryPath + "/" + className.replace('.', '/') + kind.extension);
	        file.getParentFile().mkdirs();
	        return new SimpleJavaFileObject(file.toURI(), kind) {
	            @Override
	            public OutputStream openOutputStream() throws IOException {
	                return Files.newOutputStream(file.toPath());
	            }
	        };
	    }

}
