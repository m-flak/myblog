package org.m_flak.myblog.server;

import java.io.InputStream;
import java.io.IOException;
import java.lang.Thread;
import java.lang.ref.SoftReference;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import org.m_flak.myblog.server.routes.*;

public class WebServer {
    private Server webServer;
    private ContextHandlerCollection webRootContext;
    private final Properties webProperties = new Properties();

    // Inner class implementing a shutdown hook
    private class CleanupThread extends Thread {
        private SoftReference<WebServer> refServer;

        public CleanupThread(WebServer server) {
            super();
            refServer = new SoftReference<WebServer>(server);
        }

        @Override
        public void run() {
            if (refServer.get().webServer.isStarted() || refServer.get().webServer.isRunning()) {
                try {
                    refServer.get().webServer.stop();
                }
                catch (Exception e) {
                    return;
                }
            }
        }
    }

    public WebServer(String bindIP, int bindPort) {
        // Create Jetty instance
        webServer = new Server(new InetSocketAddress(bindIP, bindPort));

        // Add an accept queue so we're not overloaded
        for (var c : webServer.getConnectors()) {
            if (c instanceof ServerConnector) {
                ((ServerConnector) c).setAcceptQueueSize(ServerConstants.MAX_THREADS);
            }
        }

        try {
            // Load the `web.properties` file
            InputStream propStream =
                WebServer.class.getClassLoader().getResourceAsStream("web.properties");
            webProperties.load(propStream);

            // Create the root context handler
            webRootContext = new ContextHandlerCollection();
        }
        catch (IOException ie) {
            webRootContext = new ContextHandlerCollection();
        }
    }

    private void setAttributes() {
        final String attrFrontendOrigin =
            webProperties.getProperty("web.frontend_origin", "*");

        webServer.setAttribute("frontend_origin", attrFrontendOrigin);
    }

    public final boolean isStartedAndRunning() {
        return (webServer.isStarted() && webServer.isRunning());
    }

    public final String getWebAppRoot() {
        return webProperties.getProperty("web.app_root");
    }

    public void start() throws Exception {
        start(true);
    }

    public void start(boolean doJoin) throws Exception {
        // Assign attributes to the server instance
        setAttributes();

        // Register all routes (or contexts as jetty calls them)
        // We set them as children to the root context to allow any application root

        // AboutRoute - Get the blog poster's `About Me`
        ContextHandler aboutMeContext = webRootContext.addContext(getWebAppRoot()+"about", "");
        aboutMeContext.setAllowNullPathInfo(true);        // <-- PREVENT HTTP 302's
        aboutMeContext.setHandler(new AboutRoute());

        // PostStoryRoute - Posts a story
        ContextHandler postStoryContext = webRootContext.addContext(getWebAppRoot()+"poststory", "");
        postStoryContext.setAllowNullPathInfo(true);    // <-- Required for POST requests
        postStoryContext.setHandler(new PostStoryRoute());

        // PostsRoute - Aggregates many posts
        ContextHandler postsContext = webRootContext.addContext(getWebAppRoot()+"posts", "");
        postsContext.setAllowNullPathInfo(true);        // <-- PREVENT HTTP 302's
        postsContext.setHandler(new PostsRoute());

        // ViewPostRoute - Fetches a post from the DB into json
        ContextHandler viewPostContext = webRootContext.addContext(getWebAppRoot()+"viewpost", "");
        viewPostContext.setAllowNullPathInfo(true);        // <-- PREVENT HTTP 302's
        viewPostContext.setHandler(new ViewPostRoute());

        // LogoutRoute - Performs a 'log out', invalidating an access token
        ContextHandler logoutContext = webRootContext.addContext(getWebAppRoot()+"logout", "");
        logoutContext.setAllowNullPathInfo(true);        // <-- PREVENT HTTP 302's
        logoutContext.setHandler(new LogoutRoute());

        // LoginRoute - Performs login & generates access_tokens
        ContextHandler loginContext = webRootContext.addContext(getWebAppRoot()+"login", "");
        loginContext.setAllowNullPathInfo(true);    // <-- Required for POST requests
        loginContext.setHandler(new LoginRoute());

        // RequesterRoute - Sends the public key to client
        ContextHandler requesterContext = webRootContext.addContext(getWebAppRoot()+"request", "");
        requesterContext.setAllowNullPathInfo(true);        // <-- PREVENT HTTP 302's
        requesterContext.setHandler(new RequesterRoute());

        // Assign root context
        webServer.setHandler(webRootContext);

        /* Allow graceful shutdowns */
        Runtime.getRuntime().addShutdownHook(new CleanupThread(this));

        /* Start & join the web server threads */
        webServer.start();

        if (doJoin) {
            webServer.join();
        }
    }

    public void stop() throws Exception {
        webServer.stop();
    }
}
