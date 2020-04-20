package org.m_flak.myblog.server;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.m_flak.myblog.server.db.ServerDatabase;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

public class AboutMeTest extends TestUsingWebDB {
    private AboutMeTestFixture abtFixture;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        abtFixture = new AboutMeTestFixture(
                ServerDatabase.inst().driv(),
                ServerDatabase.inst().conn(),
                ServerDatabase.inst().db().get()
        );
    }

    @Test
    public void testAboutMe() throws Exception {
        abtFixture.createBlogPoster();

        final String expectedAboutMe = abtFixture.blogPoster[2];
        final String aboutMeURL =
                "http://127.0.0.1:8188"+webServer.getWebAppRoot()+"about";

        var httpClient = HttpClients.createDefault();
        try {
            HttpGet getAbout = new HttpGet(aboutMeURL);

            var aboutResponse = httpClient.execute(getAbout);
            try {
                assertEquals(200, aboutResponse.getStatusLine().getStatusCode());
                assertThat(
                        aboutResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject respJSON =
                        new JSONObject(new JSONTokener(aboutResponse.getEntity().getContent()));

                try {
                    assertThat(
                            respJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    final String actualAboutMe = respJSON.getString("data");

                    assertEquals(expectedAboutMe, actualAboutMe);
                } catch (JSONException je) {
                    fail("MALFORMED JSON!\n" + je.getMessage());
                }
            }
            finally {
                aboutResponse.close();
            }
        }
        finally {
            httpClient.close();
        }
    }
}
