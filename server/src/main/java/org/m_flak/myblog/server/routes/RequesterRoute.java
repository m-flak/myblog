package org.m_flak.myblog.server.routes;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import org.eclipse.jetty.server.Request;

import org.m_flak.myblog.server.sec.KeystoreInfo;

public class RequesterRoute extends RouteHandler {
    private static class ResponseRR implements RouteResponse.Response<String> {
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
        // Set Response Type as JSON
        httpResponse.setContentType("application/json;charset=utf-8");
        // Do not cache in browser.
        // The frontend will put this in session storage or something.
        httpResponse.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate");
        request.setHandled(true);

        KeystoreInfo ksKI = new KeystoreInfo();
        String errorCode = "OK";
        String publicKey = "";

        try {
            publicKey = ksKI.getEncodedPublicKey();
        }
        catch(Exception e) {
            errorCode = "FAIL";
        }

        var resp = new ResponseRR();
        resp.rErr = errorCode;
        resp.rData = publicKey;

        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
