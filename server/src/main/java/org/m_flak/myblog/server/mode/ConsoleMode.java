package org.m_flak.myblog.server.mode;

import java.util.Properties;
import java.util.logging.Logger;

public class ConsoleMode implements RunMode {
    private Properties config;
    private Logger log;

    public ConsoleMode(Properties config, Logger log) {
        this.config = config;
        this.log = log;
    }

    @Override
    public int enterMode() {
        this.run();
        return 0;
    }

    @Override
    public void run() {
        this.log.info("Running in Console Mode.");
        this.log.info("Starting interactive command shell...");
    }
}
