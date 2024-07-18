package Server.Configuration;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerConfig {

    private static final Logger logger = Logger.getLogger(LoggerConfig.class.getName());

    public LoggerConfig() {
        configureLogging();
    }

    private void configureLogging() {
        try {
            // Load the logging.properties file
            LogManager.getLogManager().readConfiguration(
                LoggerConfig.class.getClassLoader().getResourceAsStream("logging.properties")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Logging configuration loaded");
    }

    public static Logger getLogger(String className) {
        return Logger.getLogger(className);
    }
}
