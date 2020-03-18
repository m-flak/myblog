package org.m_flak.myblog.server.routes;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.api.list.MutableList;

import org.json.JSONArray;
import org.json.JSONObject;

import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.data.SummaryPostBean;
import org.m_flak.myblog.server.db.DatabaseDate;
import org.m_flak.myblog.server.db.ServerDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.m_flak.myblog.server.db.methods.PostMethods.getAllPosts;
import static org.m_flak.myblog.server.db.methods.PostMethods.getAllSummaryPosts;

public class PostsRoute extends AbstractHandler {
    public static final int MODE_FULL = 1;
    public static final int MODE_SUMMARY = 2;
    public static final int FILTER_NONE = 0;
    public static final int FILTER_MONTH = 2;
    public static final int FILTER_YEAR = 4;

    private static class ResponsePR implements RouteResponse.Response<JSONArray> {
        private String rErr;
        private JSONArray rData;

        @Override
        public String error() {
            return rErr;
        }

        @Override
        public JSONArray data() {
            return rData;
        }
    }

    private static class FullPosts {
        public MutableList<PostBean> posts;

        public FullPosts() {
            posts = null;
        }
    }

    private static class SummaryPosts {
        public MutableList<SummaryPostBean> posts;

        public SummaryPosts() {
            posts = null;
        }
    }

    private JSONObject[] getFullPosts(int filterMode, Pair<Integer, Integer> filter) {
        final FullPosts allDem = new FullPosts();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    allDem.posts = getAllPosts(db, con);
                } finally {
                    db.close(con);
                }
            }
        });

        MutableList<PostBean> fullPosts = allDem.posts;
        if (filterMode != FILTER_NONE) {
            if ((filterMode & FILTER_MONTH) == FILTER_MONTH) {
                fullPosts = fullPosts.select(post -> new DatabaseDate(post.getDatePosted()).isMonth(filter.getOne()));
            }
            if ((filterMode & FILTER_YEAR) == FILTER_YEAR) {
                fullPosts = fullPosts.select(post -> new DatabaseDate(post.getDatePosted()).isYear(filter.getTwo()));
            }
        }

        final int numFullPosts = fullPosts.size();
        if (numFullPosts <= 0) {
            return new JSONObject[]{ new JSONObject() };
        }

        JSONObject[] jsonPosts = new JSONObject[numFullPosts];
        for (int i=0; i < numFullPosts; i++) {
            jsonPosts[i] = new JSONObject(fullPosts.get(i));
        }

        return jsonPosts;
    }

    private JSONObject[] getSummaryPosts(int filterMode, Pair<Integer, Integer> filter) {
        final SummaryPosts allDem = new SummaryPosts();

        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                var db = ServerDatabase.inst().db().get();

                db.open(driv, con);

                try {
                    allDem.posts = getAllSummaryPosts(db, con);
                } finally {
                    db.close(con);
                }
            }
        });

        MutableList<SummaryPostBean> sumPosts = allDem.posts;
        if (filterMode != FILTER_NONE) {
            if ((filterMode & FILTER_MONTH) == FILTER_MONTH) {
                sumPosts = sumPosts.select(post -> new DatabaseDate(post.getDatePosted()).isMonth(filter.getOne()));
            }
            if ((filterMode & FILTER_YEAR) == FILTER_YEAR) {
                sumPosts = sumPosts.select(post -> new DatabaseDate(post.getDatePosted()).isYear(filter.getTwo()));
            }
        }

        final int numSumPosts = sumPosts.size();
        if (numSumPosts <= 0) {
            return new JSONObject[]{ new JSONObject() };
        }

        JSONObject[] jsonPosts = new JSONObject[numSumPosts];
        for (int i=0; i < numSumPosts; i++) {
            jsonPosts[i] = new JSONObject(sumPosts.get(i));
        }

        return jsonPosts;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws IOException, ServletException {
        request.setHandled(true);

        /* Make sure we have the `mode` parameter **/
        if (!request.getParameterMap().containsKey("mode")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* Determine the mode */
        int mode;
        try {
            mode = Integer.decode(request.getParameter("mode"));
        }
        catch (NumberFormatException nfe) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        /* Check for filter types, by month, by year, or both. */
        int filterBy = FILTER_NONE;
        Pair<Integer, Integer> monthYear = Tuples.pair(0, 0);

        if (request.getParameterMap().containsKey("m")) {
            filterBy |= FILTER_MONTH;
            monthYear = Tuples.pair(Integer.decode(request.getParameter("m")), 0);
        }
        if (request.getParameterMap().containsKey("y")) {
            filterBy |= FILTER_YEAR;
            monthYear = Tuples.pair(monthYear.getOne(), Integer.decode(request.getParameter("y")));
        }

        /* Get either full posts w/ contents or summarized ones w/o */
        JSONArray ourData = null;

        if (mode == MODE_FULL) {
            ourData = new JSONArray(getFullPosts(filterBy, monthYear));
        }
        else if (mode == MODE_SUMMARY) {
            ourData = new JSONArray(getSummaryPosts(filterBy, monthYear));
        }
        else {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var resp = new ResponsePR();
        resp.rErr = "OK";
        resp.rData = ourData;

        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.getWriter().print(new RouteResponse(resp));
    }
}
