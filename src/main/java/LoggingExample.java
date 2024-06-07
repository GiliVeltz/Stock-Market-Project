import java.util.logging.Logger;

import java.util.logging.LogManager;

public class LoggingExample {

    private static final Logger logger = Logger.getLogger(LoggingExample.class.getName());

    public static void main(String[] args) {
        try {
            // Load the logging.properties file
            LogManager.getLogManager().readConfiguration(
                LoggingExample.class.getClassLoader().getResourceAsStream("logging.properties")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("This is an info message");
        logger.warning("This is a warning message");
        logger.severe("This is a severe message");
    }
}
