package org.m_flak.myblog.server.mode;

import java.util.Properties;
import java.util.logging.Logger;

import org.m_flak.myblog.server.WebServer;

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

        String host = this.config.getProperty("host");
        String port = this.config.getProperty("port");
        WebServer ourServer = new WebServer(host, Integer.parseInt(port));

        this.log.info(
            "SERVER NOW LISTENING AT: "+
            String.format("%s:%s", host, port)
        );
        // >.<
        // START THE SERVER!
        try {
            ourServer.start();
        }
        catch (Exception e) {
            this.log.severe("CRITICAL SERVER FAILURE!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
