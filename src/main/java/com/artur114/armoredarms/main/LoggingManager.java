package com.artur114.armoredarms.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class LoggingManager {
    public final Logger AA_LOG = LogManager.getLogger("ARMOREDARMS");
    protected static final Map<String, Logger> loggers = new HashMap<>();

    public Logger logger(String name) {
        return loggers.computeIfAbsent(name, LogManager::getLogger);
    }
}
