package org.m_flak.myblog.server;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.m_flak.myblog.server.db.ServerDatabase;
import org.m_flak.myblog.server.routes.PostsRoute;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

public class PostsTest extends TestUsingWebDB {
    private PostBeanTestsFixture pbtFixture;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        pbtFixture = new PostBeanTestsFixture(
                ServerDatabase.inst().driv(),
                ServerDatabase.inst().conn(),
                ServerDatabase.inst().db().get()
        );
    }

    @Test
    public void postsTest() throws Exception {
        pbtFixture.createMockUsers();
        pbtFixture.createManyMockPosts();

        final String postsFullURL =
                new URIBuilder("http://127.0.0.1:8188"+webServer.getWebAppRoot()+"posts")
                .addParameter("mode", Integer.toString(PostsRoute.MODE_FULL))
                .build()
                .toString();

        final String postsSumURL =
                new URIBuilder("http://127.0.0.1:8188"+webServer.getWebAppRoot()+"posts")
                .addParameter("mode", Integer.toString(PostsRoute.MODE_SUMMARY))
                .build()
                .toString();

        final String postsFullInMarchURL =
                new URIBuilder("http://127.0.0.1:8188"+webServer.getWebAppRoot()+"posts")
                .addParameter("mode", Integer.toString(PostsRoute.MODE_FULL))
                .addParameter("m", Integer.toString(3))
                .build()
                .toString();

        var httpClient = HttpClients.createDefault();
        try {
            HttpGet getFull = new HttpGet(postsFullURL);
            HttpGet getSum = new HttpGet(postsSumURL);
            HttpGet getFullMarch = new HttpGet(postsFullInMarchURL);

            //
            // MODE_FULL
            //
            var fullResponse = httpClient.execute(getFull);
            try {
                assertEquals(200, fullResponse.getStatusLine().getStatusCode());
                assertThat(
                        fullResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(fullResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    // GET THE POSTS
                    JSONArray postsJSON = respJSON.getJSONArray("data");
                    assertEquals(pbtFixture.mockManyPostBeans.size(), postsJSON.length());

                    // THEY SHOULD CONTAIN STUFF
                    for (final Object objJson : postsJSON) {
                        JSONObject jo = (JSONObject) objJson;
                        assertTrue(jo.getString("title").contains("Mock Post"));
                    }
                } catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            } finally {
                fullResponse.close();
            }

            //
            // MODE_SUMMARY
            //
            var sumResponse = httpClient.execute(getSum);
            try {
                assertEquals(200, sumResponse.getStatusLine().getStatusCode());
                assertThat(
                        sumResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(sumResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    // GET THE POSTS
                    JSONArray postsJSON = respJSON.getJSONArray("data");
                    assertEquals(pbtFixture.mockManyPostBeans.size(), postsJSON.length());

                    // THEY SHOULD NOT CONTAIN A CONTENTS FIELD
                    for (final Object objJson : postsJSON) {
                        JSONObject jo = (JSONObject) objJson;
                        assertFalse(jo.has("contents"));
                    }
                } catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            } finally {
                sumResponse.close();
            }

            var fullMarchResponse = httpClient.execute(getFullMarch);
            try {
                assertEquals(200, fullMarchResponse.getStatusLine().getStatusCode());
                assertThat(
                        fullMarchResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(fullMarchResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    // SINCE THE DATE ON ALL MOCK POSTS IS SAME, THE LENGTH SHOULD BE SAME
                    JSONArray postsJSON = respJSON.getJSONArray("data");
                    assertEquals(pbtFixture.mockManyPostBeans.size(), postsJSON.length());
                } catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            } finally {
                fullMarchResponse.close();
            }
        }
        finally {
            httpClient.close();
        }
    }

    @Test
    public void postsNoPostsTest() throws Exception {
        final String postsFullURL =
                new URIBuilder("http://127.0.0.1:8188"+webServer.getWebAppRoot()+"posts")
                .addParameter("mode", Integer.toString(PostsRoute.MODE_FULL))
                .build()
                .toString();

        final String postsSumURL =
                new URIBuilder("http://127.0.0.1:8188"+webServer.getWebAppRoot()+"posts")
                .addParameter("mode", Integer.toString(PostsRoute.MODE_SUMMARY))
                .build()
                .toString();

        var httpClient = HttpClients.createDefault();
        try {
            HttpGet getFull = new HttpGet(postsFullURL);
            HttpGet getSum = new HttpGet(postsSumURL);

            //
            // MODE_FULL
            //
            var fullResponse = httpClient.execute(getFull);
            try {
                assertEquals(200, fullResponse.getStatusLine().getStatusCode());
                assertThat(
                        fullResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(fullResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    // GET THE NOTHING
                    JSONArray postsJSON = respJSON.getJSONArray("data");
                    assertEquals(1,  postsJSON.length());
                    JSONObject blank = new JSONObject();
                    assertEquals(blank.toString(), postsJSON.getJSONObject(0).toString());
                } catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            }
            finally {
                fullResponse.close();
            }

            //
            // MODE_SUMMARY
            //
            var sumResponse = httpClient.execute(getSum);
            try {
                assertEquals(200, sumResponse.getStatusLine().getStatusCode());
                assertThat(
                        sumResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(sumResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    // GET THE NOTHING
                    JSONArray postsJSON = respJSON.getJSONArray("data");
                    assertEquals(1,  postsJSON.length());
                    JSONObject blank = new JSONObject();
                    assertEquals(blank.toString(), postsJSON.getJSONObject(0).toString());
                } catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            }
            finally {
                sumResponse.close();
            }
        }
        finally {
            httpClient.close();
        }
    }
}
