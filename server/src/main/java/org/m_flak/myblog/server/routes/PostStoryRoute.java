package org.m_flak.myblog.server.routes;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.db.DatabaseDate;
import org.m_flak.myblog.server.db.ServerDatabase;
import org.m_flak.myblog.server.sec.AccessToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.fetchIDForToken;
import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.isTokenValid;
import static org.m_flak.myblog.server.db.methods.PostMethods.createPostFromBean;

public class PostStoryRoute extends AbstractHandler {
    private static class ResponsePSR implements RouteResponse.Response<String> {
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

    private static class UserToken {
        public AccessToken token;
        public boolean isValid;

        public UserToken() {
            token = null;
            isValid = false;
        }
    }

    private void giveResponse(HttpServletResponse httpResponse, String code, int postID)
                                                                            throws IOException {
        var resp = new ResponsePSR();
        resp.rErr = code;
        resp.rData = Integer.toString(postID);

        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate");
        httpResponse.getWriter().print(new RouteResponse(resp));
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {
        request.setHandled(true);

        // Perform validation of the request method
        if (!Objects.equals(request.getMethod(), "POST")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // Perform validation of the request content type
        if (!request.getContentType().matches("^application\\/json.+")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JSONObject reqJSON;
        final UserToken uToken = new UserToken();
        final PostBean thePost = new PostBean();

        try {
            reqJSON = new JSONObject(new JSONTokener(request.getInputStream()));
            uToken.token = AccessToken.generateFromString(reqJSON.getString("token"));
            thePost.setTitle(reqJSON.getString("title"));
            thePost.setContents(reqJSON.getString("contents"));
        }
        catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    if (isTokenValid(uToken.token.toString(), db, con)) {
                        uToken.isValid = true;
                        thePost.setPosterID(fetchIDForToken(uToken.token.toString(), db, con));
                    }
                    else {
                        uToken.isValid = false;
                    }
                }
                finally {
                    db.close(con);
                }
            }
        });

        if (!uToken.isValid) {
            giveResponse(httpResponse, "FAIL", -1);
            return;
        }

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    // won't matter but we should still set it
                    thePost.setPostID(0L);
                    thePost.setDatePosted(new DatabaseDate().toString());

                    long actualPostID = createPostFromBean(thePost, db, con);
                    thePost.setPostID(actualPostID);

                    db.commit(con);
                }
                finally {
                    db.close(con);
                }
            }
        });

        giveResponse(httpResponse, "OK", (int) thePost.getPostID());
    }
}
