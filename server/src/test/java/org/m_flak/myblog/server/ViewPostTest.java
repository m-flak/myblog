package org.m_flak.myblog.server;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.db.ServerDatabase;

public class ViewPostTest extends TestUsingWebDB {
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
    public void viewPostTest() throws Exception {
        pbtFixture.createMockUsers();
        pbtFixture.createMockPost();

        final PostBean postInDB = pbtFixture.mockPostBeanInDB;

        final String viewPostURL =
                new URIBuilder("http://127.0.0.1:8188"+webServer.getWebAppRoot()+"viewpost")
                .addParameter("id", Long.toString(postInDB.getPostID()))
                .build()
                .toString();

        var httpClient = HttpClients.createDefault();
        try {
            HttpGet getThatPost = new HttpGet(viewPostURL);
            var postResponse = httpClient.execute(getThatPost);
            try {
                assertEquals(200, postResponse.getStatusLine().getStatusCode());
                assertThat(
                        postResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(postResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    JSONObject postJSON = respJSON.getJSONObject("data");

                    // ENSURE THAT the response conforms to the Bean.
                    Field[] fieldKeys = PostBean.class.getFields();
                    for (final Field fk : fieldKeys) {
                        if (Modifier.isStatic(fk.getModifiers())) {
                            continue;
                        }

                        if (fk.getType() == Long.TYPE) {
                            assertTrue(postJSON.getLong(fk.getName()) != -1);
                        }
                        else if (fk.getType() == String.class) {
                            assertTrue(postJSON.getString(fk.getName()).length() > 0);
                        }
                    }

                    // ENSURE THAT everything is the same
                    assertEquals(postInDB.getTitle(), postJSON.getString("title"));
                    assertEquals(postInDB.getDatePosted(), postJSON.getString("datePosted"));
                    assertEquals(postInDB.getContents(), postJSON.getString("contents"));
                }
                catch (JSONException je) {
                    fail("MALFORMED JSON!\n"+je.getMessage());
                }
            }
            finally {
                postResponse.close();
            }
        }
        finally {
            httpClient.close();
        }
    }
}
