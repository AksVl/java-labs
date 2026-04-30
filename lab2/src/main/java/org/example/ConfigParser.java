package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure configuration parser.
 * Reads commands.config from the same package and returns a list of JAR paths.
 * Does NOT load any classes or build command maps.
 */
public class ConfigParser {

  private static final String CONFIG_FILE = "org/example/commands.config";

  /**
   * @return List of JAR file paths (relative or absolute) as written in the config file.
   * @throws CalculatorException if the config file cannot be read.
   */
  public List<String> parse() {
    List<String> jarPaths = new ArrayList<>();
    try (InputStream is = getClass().getResourceAsStream("/" + CONFIG_FILE)) {
      if (is == null) {
        throw new CalculatorException("Config file not found: " + CONFIG_FILE + " in package " + getClass().getPackage().getName());
      }
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
        String line;
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.isEmpty() || line.startsWith("#")) continue;
          // Support "jars = a.jar, b.jar" format
          if (line.toLowerCase().startsWith("jars=") || line.toLowerCase().startsWith("jars =")) {
            String value = line.substring(line.indexOf('=') + 1).trim();
            String[] parts = value.split(",");
            for (String part : parts) {
              String jar = part.trim();
              if (!jar.isEmpty()) jarPaths.add(jar);
            }
          } else {
            jarPaths.add(line);
          }
        }
      }
    } catch (IOException e) {
      throw new CalculatorException("Failed to read config file: " + CONFIG_FILE);
    }
    return jarPaths;
  }
}