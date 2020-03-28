package org.m_flak.myblog.server.routes;

import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.handler.AbstractHandler;

import org.m_flak.myblog.server.App;

public abstract class RouteHandler extends AbstractHandler {
    protected void setupCORS(HttpServletResponse httpResponse) {
        var server = this.getServer();
        Object oOrigin = server.getAttribute("frontend_origin");

        if (Objects.nonNull(oOrigin) && oOrigin instanceof String) {
            httpResponse.addHeader("Access-Control-Allow-Origin", (String)oOrigin);
        }
        else {
            App.logger.warning("Access-Control-Allow-Origin set to \"*\"!");
        }
    }

    protected void sendCORSOptions(HttpServletResponse httpResponse, String[] allowedMethods) {
        StringBuilder allowBuilder = new StringBuilder("OPTIONS");
        int numCommas = allowedMethods.length;

        for (final String s : allowedMethods) {
            if (numCommas > 0) {
                allowBuilder.append(", ");
                numCommas -= 1;
            }
            allowBuilder.append(s);
        }

        // Add headers based off of `allowedMethods`
        httpResponse.addHeader("Allow", allowBuilder.toString());
        httpResponse.addHeader("Access-Control-Allow-Methods", allowBuilder.toString());

        // Add the CORS safelisted headers
        httpResponse.addHeader("Access-Control-Allow-Headers", "Accept, Accept-Language, Content-Language, Content-Type");
        
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }
}
