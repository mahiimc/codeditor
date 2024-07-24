package com.executor.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompiledClassLoader extends ClassLoader  {

	private final String directoryPath;

    public CompiledClassLoader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] b = loadClassFromFile(name);
            return defineClass(name, b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
            return super.findClass(name);
        }
    }

    private byte[] loadClassFromFile(String className) throws IOException {
        String classFilePath = directoryPath + "/" + className.replace('.', '/') + ".class";
        return Files.readAllBytes(Paths.get(classFilePath));
    }
}
