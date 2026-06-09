package factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHandler {
    private static Properties config = new Properties();
    public static Properties readConfigFile(){
        try {
            InputStream input = ConfigHandler.class.getResourceAsStream("/config.properties");
            config.load(input);
        } catch (NullPointerException e){
            throw new RuntimeException("Invalid configuration, " + e.getMessage());
        } catch (IOException e){
            throw new RuntimeException("Failed to load cfg file, " + e.getMessage());
        }
        return config;
    }
}
