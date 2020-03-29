package org.m_flak.myblog.server.routes;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import org.m_flak.myblog.server.sec.AccessToken;
import org.m_flak.myblog.server.db.ServerDatabase;

import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.invalidateToken;

public class LogoutRoute extends RouteHandler {
    private static class ResponseLOR implements RouteResponse.Response<String> {
        private String rErr;
        private String rData;

        @Override
        public String error() {
            return rErr;
        }

        @Override
        public String data() {
            return rData;
        }
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                    HttpServletResponse httpResponse) throws IOException, ServletException {
        setupCORS(httpResponse);
        request.setHandled(true);

        /* Make sure we have the `tok` parameter **/
        if (!request.getParameterMap().containsKey("tok")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* Get the Access Token sent via the `tok` parameter */
        AccessToken logMeOutToken;
        try {
            logMeOutToken = AccessToken.generateFromString(request.getParameter("tok"));
        }
        catch (IllegalArgumentException ie) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Attempt to log out user based on the token
        final String logMeOutTokString = logMeOutToken.toString();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    invalidateToken(logMeOutTokString, db, con);
                    db.commit(con);
                }
                finally {
                    db.close(con);
                }
            }
        });

        var resp = new ResponseLOR();
        resp.rErr = "OK";
        resp.rData = "";

        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
