package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {
  public static final String FAILED_TO_READ_FILE_MSG = "Failed to read config file: ";
  public static final String FAILED_TO_FIND_FILE_1_MSG = "Config file not found: ";
  public static final String FAILED_TO_FIND_FILE_2_MSG = " in package ";

  private static final String CONFIG_FILE = "org/example/commands.config";

  public List<String> parse() {
    List<String> jarPaths = new ArrayList<>();
    try (InputStream is = getClass().getResourceAsStream("/" + CONFIG_FILE)) {
      if (is == null) {
        throw new CalculatorException(FAILED_TO_FIND_FILE_1_MSG + CONFIG_FILE + FAILED_TO_FIND_FILE_2_MSG + getClass().getPackage().getName());
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
      throw new CalculatorException(FAILED_TO_READ_FILE_MSG + CONFIG_FILE);
    }
    return jarPaths;
  }
}