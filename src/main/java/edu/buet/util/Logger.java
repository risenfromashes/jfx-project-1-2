package edu.buet.util;

public class Logger {
    public enum Config { DEBUG, RELEASE };
    private static Config config = Config.DEBUG;
    public static void setConfig(Config config) {
        Logger.config = config;
    }
    public static synchronized void log(Exception ex) {
        if (config == config.DEBUG) {
            var stacktrace = ex.getStackTrace();
            for (var s : stacktrace) {
                System.out.print("[EXCEPTION] ");
                System.out.println(s);
            }
        }
    }
    public static synchronized void info(String message) {
        if (config == config.DEBUG) {
            System.out.print("[INFO] ");
            System.out.println(message);
        }
    }
}
