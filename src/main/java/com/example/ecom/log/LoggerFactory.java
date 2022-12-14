package com.example.ecom.log;

import java.util.Map;
import org.apache.logging.log4j.ThreadContext;

public class LoggerFactory {
    public static synchronized AppLogger getLogger(LoggerType type) {
        return getLogger(type, null);
    }

    public static synchronized AppLogger getLogger(LoggerType type,
            Map<String, String> parameters) {
        if (parameters != null) {
            ThreadContext.putAll(parameters);
        }
        return new AppLogger(type);
    }
}
