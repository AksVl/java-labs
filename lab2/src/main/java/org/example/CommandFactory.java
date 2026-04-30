package org.example;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Factory that loads commands from JARs listed in the configuration.
 * It takes a list of JAR paths (pre‑parsed by CommandConfigParser) and
 * builds the internal command map.
 */
public class CommandFactory {

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
      System.err.println("Warning: JAR file not found – " + jarPath);
      return;
    }
    try (JarFile jar = new JarFile(jarFile)) {
      URL jarUrl = jarFile.toURI().toURL();
      try (URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl},
              Thread.currentThread().getContextClassLoader())) {
        scanJar(jar, loader);
      }
    } catch (IOException e) {
      System.err.println("Failed to load JAR " + jarPath + ": " + e.getMessage());
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
          // skip unloadable classes
        }
      }
    }
  }

  public Class<? extends Function> getCommand(String name) {
    Class<? extends Function> cmdClass = commandMap.get(name);
    if (cmdClass == null) {
      throw new CalculatorException("Unknown command: " + name);
    }
    return cmdClass;
  }
}