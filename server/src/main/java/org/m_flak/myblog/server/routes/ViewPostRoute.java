package org.m_flak.myblog.server.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.eclipse.jetty.server.Request;

import org.json.JSONObject;
import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.db.ServerDatabase;

import static org.m_flak.myblog.server.db.methods.PostMethods.getPostById;

public class ViewPostRoute extends RouteHandler {
    private static class ResponseVPR implements RouteResponse.Response<JSONObject> {
        private String rErr;
        private JSONObject rData;

        @Override
        public String error() {
            return rErr;
        }

        @Override
        public JSONObject data() {
            return rData;
        }
    }

    private static class FetchedPost {
        public PostBean thePost;

        public FetchedPost() {
            thePost = null;
        }
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {
        setupCORS(httpResponse);
        request.setHandled(true);

        /* Make sure we have the `id` parameter **/
        if (!request.getParameterMap().containsKey("id")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* Attempt to retrieve the id parameter */
        long paramId;
        try {
            paramId = Long.parseLong(request.getParameter("id"));
        }
        catch (NumberFormatException ne) {
            paramId = -1;
        }

        /* 404 if malformed not a number or whatever */
        if (paramId == -1) {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final long postId = paramId;
        final FetchedPost thePost = new FetchedPost();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    thePost.thePost = getPostById(postId, db, con);
                }
                finally {
                    db.close(con);
                }
            }
        });

        var resp = new ResponseVPR();
        resp.rErr = "OK";
        resp.rData = new JSONObject(thePost.thePost);

        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
