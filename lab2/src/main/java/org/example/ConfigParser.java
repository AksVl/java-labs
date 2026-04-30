package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {
  public static final String FAILED_TO_READ_FILE_MSG = "Failed to read config file: ";
  public static final String FAILED_TO_FIND_FILE_MSG = "Config file not found: ";

  private static final String CONFIG_FILE = "commands.config";

  public List<String> parse() {
    List<String> jarPaths = new ArrayList<>();
    File configFile = new File(CONFIG_FILE);

    if (!configFile.exists() || !configFile.isFile()) {
      throw new CalculatorException(FAILED_TO_FIND_FILE_MSG + CONFIG_FILE);
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
      parseLines(reader, jarPaths);
    } catch (IOException e) {
      throw new CalculatorException(FAILED_TO_READ_FILE_MSG + CONFIG_FILE);
    }
    return jarPaths;
  }

  private void parseLines(BufferedReader reader, List<String> jarPaths) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty() || line.startsWith("#")) continue;
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
}