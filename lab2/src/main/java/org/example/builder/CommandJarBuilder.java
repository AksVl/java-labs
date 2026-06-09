package org.example.builder;

import org.example.Command;

import java.io.*;
import java.nio.file.*;
import java.util.jar.*;
import java.util.stream.Stream;

public class CommandJarBuilder {
  public static void main(String[] args) throws Exception {
    Path classesDir = Paths.get("target/classes");
    Path commandsDir = Paths.get("target/commands");

    Files.createDirectories(commandsDir);

    Path commandsPackage = classesDir.resolve("org/example/commands");
    if (!Files.exists(commandsPackage)) {
      System.err.println("No commands package found");
      return;
    }

    try (Stream<Path> paths = Files.walk(commandsPackage)) {
      paths.filter(p -> p.toString().endsWith(".class"))
              .forEach(classFile -> {
                String className = classesDir.relativize(classFile).toString()
                        .replace(File.separator, ".")
                        .replace(".class", "");
                try {
                  Class<?> clazz = Class.forName(className);
                  if (clazz.isAnnotationPresent(Command.class)) {
                    Command annotation = clazz.getAnnotation(Command.class);
                    String commandName = annotation.name();

                    Path jarFile = commandsDir.resolve(commandName + ".jar");
                    try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile.toFile()))) {
                      String entryName = classFile.toString()
                              .replace(classesDir.toString(), "")
                              .substring(1)
                              .replace(File.separator, "/");
                      jos.putNextEntry(new JarEntry(entryName));
                      Files.copy(classFile, jos);
                      jos.closeEntry();
                    }
                    System.out.println("Created: " + jarFile);
                  }
                } catch (Exception e) {
                  System.err.println("Skipping " + className + ": " + e.getMessage());
                }
              });
    }
  }
}