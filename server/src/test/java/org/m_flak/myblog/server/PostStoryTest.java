package org.m_flak.myblog.server;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.db.DatabaseDate;
import org.m_flak.myblog.server.db.ServerDatabase;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.containsString;

public class PostStoryTest extends TestUsingWebDB {
    private PostStoryTestFixture pstFixture;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        pstFixture = new PostStoryTestFixture(
                ServerDatabase.inst().driv(),
                ServerDatabase.inst().conn(),
                ServerDatabase.inst().db().get()
        );
    }

    @Test
    public void testPostingStory() throws Exception {
        pstFixture.createMockUsers();

        final String postStoryURL =
                "http://127.0.0.1:8188"+webServer.getWebAppRoot()+"poststory";

        StringEntity dataPost =
                new StringEntity(pstFixture.createPostJSON().toString(), ContentType.APPLICATION_JSON);

        var httpClient =
                HttpClientBuilder.create()
                        .useSystemProperties()
                        .setRedirectStrategy(new LaxRedirectStrategy())
                        .build();
        try {
            HttpPost postStoryPost = new HttpPost(postStoryURL);
            postStoryPost.setEntity(dataPost);

            var postStoryResponse = httpClient.execute(postStoryPost);
            try {
                assertThat(
                        postStoryResponse.getEntity().getContentType().getValue(),
                        containsString("application/json")
                );

                JSONObject postIdJSON =
                        new JSONObject(new JSONTokener(postStoryResponse.getEntity().getContent()));

                try {
                    assertThat(
                            postIdJSON.getString("errorCode"),
                            containsString("OK")
                    );

                    long postId = Long.parseLong(postIdJSON.getString("data"));

                    PostBean theCreatedPost = pstFixture.getCreatedPost(postId);
                    var fuckingNow = LocalDateTime.now();
                    var postedDate = new DatabaseDate(theCreatedPost.getDatePosted());

                    assertNotEquals(-1, theCreatedPost.getPostID());
                    assertEquals(pstFixture.POST_TITLE, theCreatedPost.getTitle());
                    assertEquals(pstFixture.POST_CONTENTS, theCreatedPost.getContents());
                    assertTrue(postedDate.isMonth(fuckingNow.getMonthValue()));
                    assertTrue(postedDate.isDay(fuckingNow.getDayOfMonth()));
                    assertTrue(postedDate.isYear(fuckingNow.getYear()));
                }
                catch (JSONException je) {
                    fail("Malformed JSON!: "+je.getMessage());
                }
                catch (NumberFormatException nfe) {
                    fail("Unable to parse the returned Post ID!: "+nfe.getMessage());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            finally {
                postStoryResponse.close();
            }
        }
        finally {
            httpClient.close();
        }
    }
}
