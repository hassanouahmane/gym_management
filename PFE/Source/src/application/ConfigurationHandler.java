package application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class ConfigurationHandler {
    private static final String CONFIG_FILE_PATH = "./configuration/config.json";
    private Configuration configuration;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ConfigurationHandler() {
        loadConfiguration();
    }

    public void saveConfiguration(Configuration configuration) {
        try {
            this.configuration = configuration;
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File(CONFIG_FILE_PATH), configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration loadConfiguration() { 
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (configFile.exists()) {
                configuration = objectMapper.readValue(configFile, Configuration.class);
            } else {
                File configDir = new File(CONFIG_FILE_PATH).getParentFile();
                if (!configDir.exists()) {
                    configDir.mkdirs();
                }

                configuration = new Configuration();
                configuration.setDbUrl("jdbc:mysql://localhost:3306/fitpro");
                configuration.setDbUsername("root");
                configuration.setDbPassword("");
                configuration.setNotificationsEnabled(true);
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.writeValue(configFile, configuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }


    public Configuration getConfiguration() {
        return configuration;
    }
}
