package org.m_flak.myblog.server;

import org.apache.empire.db.DBDatabaseDriver;
import org.json.JSONObject;
import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.db.model.Database;

import java.sql.Connection;
import java.util.Objects;

import static org.m_flak.myblog.server.db.methods.PostMethods.getPostById;

public class PostStoryTestFixture extends WebAuthTestsFixture {
    public static final String POST_TITLE = "This is a Title";
    public static final String POST_CONTENTS = "This is its contents.";

    public PostStoryTestFixture(DBDatabaseDriver driver, Connection connection, Database db) {
        super(driver, connection, db);
    }

    public JSONObject createPostJSON() {
        if (Objects.isNull(tokenUser1) || tokenUser1.isEmpty()) {
            createMockUsers();
        }

        return new JSONObject()
                .put("token", tokenUser1)
                .put("title", POST_TITLE)
                .put("contents", POST_CONTENTS);
    }

    public PostBean getCreatedPost(long postId) {
        ourDB.open(ourDriver, ourConn);

        PostBean createdPost = getPostById(postId, ourDB, ourConn);

        ourDB.close(ourConn);

        return createdPost;
    }
}
