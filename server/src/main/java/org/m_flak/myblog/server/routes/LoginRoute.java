package org.m_flak.myblog.server.routes;

import java.util.Base64;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.exceptions.QueryNoResultException;

import org.m_flak.myblog.server.db.ServerDatabase;
import org.m_flak.myblog.server.sec.EncryptedPassword;
import org.m_flak.myblog.server.sec.PasswordEncryptor;

import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.countAccessTokens;
import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.createTokenForUser;
import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.userHasToken;
import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.userTokenExpired;
import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.updateUserToken;
import static org.m_flak.myblog.server.db.methods.AccessTokenMethods.fetchUserToken;

public class LoginRoute extends RouteHandler {
    private static class ResponseLR implements RouteResponse.Response<String> {
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

    private static class AuthBool {
        public boolean isAuthed;

        public AuthBool() {
            isAuthed = false;
        }
    }

    private static class TokenString {
        public String token;

        public TokenString() {
            token = "";
        }
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                    HttpServletResponse httpResponse) throws IOException, ServletException {
        setupCORS(httpResponse);
        request.setHandled(true);

        // Perform validation of the request method
        if (!Objects.equals(request.getMethod(), "POST")) {
            /*
             * Since we're accepting JSON as input, we must use OPTIONS!
             *
             * The things you learn when you build stuff from scratch!
             */
            if (Objects.equals(request.getMethod(), "OPTIONS")) {
                sendCORSOptions(httpResponse, new String[] {"POST"});
                return;
            }

            httpResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        // Perform validation of the request content type
        if (!request.getContentType().matches("^application\\/json.*")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JSONObject reqJSON;
        String userName;
        boolean userAuthed = false;

        // Attempt to authenticate a user
        try {
            reqJSON = new JSONObject(new JSONTokener(request.getInputStream()));

            final String user = reqJSON.getString("user");
            final AuthBool authBool = new AuthBool();
            final EncryptedPassword password =
                new EncryptedPassword(Base64.getDecoder().decode(reqJSON.getString("pass")));

            ServerDatabase.inst().runOnDB(new Runnable() {
                @Override
                public void run() {
                    var con = ServerDatabase.inst().conn();
                    var driv = ServerDatabase.inst().driv();
                    var db = ServerDatabase.inst().db().get();

                    db.open(driv, con);

                    DBCommand cmd = db.createCommand();
                    cmd.select(db.T_USERS.C_PASSWORD);
                    cmd.where(db.T_USERS.C_NAME.is(user));

                    try {
                        EncryptedPassword realPass =
                            new EncryptedPassword((byte[]) db.querySingleValue(cmd, DataType.BLOB, con));

                        PasswordEncryptor pency = new PasswordEncryptor();

                        if (pency.decrypt(realPass).compareTo(pency.decrypt(password)) == 0) {
                            authBool.isAuthed = true;
                        }
                    }
                    finally {
                        db.close(con);
                    }
                }
            });

            userName = user;
            userAuthed = authBool.isAuthed;
        }
        catch (JSONException je) {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        catch (QueryNoResultException qe) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!userAuthed) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        /** Generate / Fetch / Update the user's access token *****/
        final String user = userName;
        final TokenString theToken = new TokenString();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    if (countAccessTokens(db, con) == 0 || !userHasToken(user, db, con)) {
                        theToken.token = createTokenForUser(user, db, con);
                        db.commit(con);
                    }
                    else {
                        if (userTokenExpired(user, db, con)) {
                            // gen new token & update
                            theToken.token = updateUserToken(user, db, con);
                            db.commit(con);
                        }
                        else {
                            // merely fetch token
                            theToken.token = fetchUserToken(user, db, con);
                        }
                    }
                }
                finally {
                    db.close(con);
                }
            }
        });

        var resp = new ResponseLR();
        resp.rErr = "OK";
        resp.rData = theToken.token;

        // Set Response Type as JSON
        httpResponse.setContentType("application/json;charset=utf-8");
        // The frontend will put this in session storage or something.
        httpResponse.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate");

        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
