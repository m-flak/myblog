package org.m_flak.myblog.server;

import java.lang.Thread;

import org.junit.Before;
import org.junit.After;

import org.m_flak.myblog.server.db.ServerDatabase;
import org.m_flak.myblog.server.WebServer;

public abstract class TestUsingWebDB extends TestUsingDB {
    public final WebServer webServer = new WebServer("127.0.0.1", 8188);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        webServer.start(false);

        while (!webServer.isStartedAndRunning()) {
            Thread.sleep(100L);
        }
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        webServer.stop();
    }
}
