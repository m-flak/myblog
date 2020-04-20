package org.m_flak.myblog.server.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.eclipse.jetty.server.Request;

import org.json.JSONObject;
import org.m_flak.myblog.server.db.ServerDatabase;

import static org.m_flak.myblog.server.db.methods.UserMethods.getPostersAboutMe;

public class AboutRoute extends RouteHandler {
    private static class ResponseAR implements RouteResponse.Response<String> {
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

    private static class AboutMe {
        public String aboutMe;

        public AboutMe() {
            aboutMe = "";
        }
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {
        setupCORS(httpResponse);
        request.setHandled(true);

        final AboutMe theAboutMe = new AboutMe();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    theAboutMe.aboutMe = getPostersAboutMe(db, con);
                }
                finally {
                    db.close(con);
                }
            }
        });

        var resp = new ResponseAR();
        resp.rErr = "OK";
        resp.rData = theAboutMe.aboutMe;

        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
