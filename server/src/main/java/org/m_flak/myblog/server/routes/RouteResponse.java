package org.m_flak.myblog.server.routes;

import org.json.JSONObject;

import java.util.Objects;

public class RouteResponse {
    public interface Response<T> {
        String error();
        T data();
    }

    private JSONObject responseObject;

    public RouteResponse(Response<?> theResponse) {
        responseObject = new JSONObject()
                         .put("errorCode", theResponse.error())
                         .put("data", theResponse.data());
    }

    @Override
    public String toString() {
        if (Objects.isNull(responseObject)) {
            return "{\"errorCode\": \"FAIL\", \"data\": null}";
        }

        return responseObject.toString();
    }
}
