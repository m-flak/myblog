package org.m_flak.myblog.server.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.eclipse.jetty.server.Request;

import org.m_flak.myblog.server.db.ServerDatabase;

import static org.m_flak.myblog.server.db.methods.PostMethods.countPosts;

public class PostCountRoute extends RouteHandler {
    private static class ResponsePCR implements RouteResponse.Response<String> {
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

    private static class PostCount {
        public long count;

        public PostCount() {
            count = 0L;
        }
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {
        setupCORS(httpResponse);
        request.setHandled(true);

        final PostCount ourCount = new PostCount();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    ourCount.count = countPosts(db, con);
                }
                finally {
                    db.close(con);
                }
            }
        });

        var resp = new ResponsePCR();
        resp.rErr = "OK";
        resp.rData = Long.toString(ourCount.count);

        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
