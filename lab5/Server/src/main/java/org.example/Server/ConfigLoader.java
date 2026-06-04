package org.example.Server;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
  private static final Properties properties = new Properties();

  static {
    try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        throw new RuntimeException("Unable to find config.properties");
      }
      properties.load(input);
    } catch (Exception e) {
      throw new RuntimeException("Error loading configuration", e);
    }
  }

  public static int getPort() {
    return Integer.parseInt(properties.getProperty("server.port", "8080"));
  }
}