package org.m_flak.myblog.server;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.m_flak.myblog.server.db.ServerDatabase;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

public class PostCountTest extends TestUsingWebDB {
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
    public void postCountTest() throws Exception {
        pbtFixture.createMockUsers();
        pbtFixture.createManyMockPosts();

        final String postCountURL =
            "http://127.0.0.1:8188"+webServer.getWebAppRoot()+"postcount";

        var httpClient = HttpClients.createDefault();
        try {
            HttpGet getPC = new HttpGet(postCountURL);

            var pcResponse = httpClient.execute(getPC);
            try {
                assertEquals(200, pcResponse.getStatusLine().getStatusCode());
                assertThat(
                        pcResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(pcResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    assertEquals("5", respJSON.getString("data"));
                }
                catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            }
            finally {
                pcResponse.close();
            }
        }
        finally {
            httpClient.close();
        }
    }
}
