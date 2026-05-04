package org.example;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommandFactory {
  public static final String UNKNOWN_COMMAND_MSG = "Unknown command: ";
  public static final String JAR_NOT_FOUND_MSG = "JAR file not found – ";
  public static final String JAR_NOT_LOADED_MSG = "Failed to load JAR ";

  private final Map<String, Class<? extends Function>> commandMap = new HashMap<>();

  public CommandFactory() {
    ConfigParser parser = new ConfigParser();
    List<String> jarPaths = parser.parse();
    for (String jarPath : jarPaths) {
      loadJar(jarPath);
    }
  }

  private void loadJar(String jarPath) {
    File jarFile = new File(jarPath);
    if (!jarFile.exists()) {
      System.err.println(JAR_NOT_FOUND_MSG + jarPath);
      return;
    }
    try (JarFile jar = new JarFile(jarFile)) {
      URL jarUrl = jarFile.toURI().toURL();
      try (URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl},
              Thread.currentThread().getContextClassLoader())) {
        scanJar(jar, loader);
      }
    } catch (IOException e) {
      System.err.println(JAR_NOT_LOADED_MSG + jarPath + ": " + e.getMessage());
    }
  }

  private void scanJar(JarFile jarFile, ClassLoader loader) {
    Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String entryName = entry.getName();
      if (entryName.endsWith(".class")) {
        String className = entryName.replace('/', '.').replace(".class", "");
        try {
          Class<?> clazz = loader.loadClass(className);
          if (clazz.isAnnotationPresent(Command.class) && Function.class.isAssignableFrom(clazz)) {
            Command annotation = clazz.getAnnotation(Command.class);
            commandMap.put(annotation.name(), clazz.asSubclass(Function.class));
          }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
          //
        }
      }
    }
  }

  public Class<? extends Function> getCommand(String name) {
    Class<? extends Function> cmdClass = commandMap.get(name);
    if (cmdClass == null) {
      throw new CalculatorException(UNKNOWN_COMMAND_MSG + name);
    }
    return cmdClass;
  }
}