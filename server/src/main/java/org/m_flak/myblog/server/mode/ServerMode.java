package org.m_flak.myblog.server.mode;

import java.util.Properties;
import java.util.logging.Logger;

public class ServerMode implements RunMode {
    private Properties config;
    private Logger log;

    public ServerMode(Properties config, Logger log) {
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
        this.log.info("Running in Server Mode.");
        this.log.info(
            "SERVER NOW LISTENING AT: "+
            String.format("%s:%s", this.config.getProperty("host"), this.config.getProperty("port"))
        );
    }
}
