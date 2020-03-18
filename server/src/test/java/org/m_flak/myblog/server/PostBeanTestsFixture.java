package org.m_flak.myblog.server;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.empire.db.DBDatabaseDriver;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;

import org.m_flak.myblog.server.data.PostBean;
import org.m_flak.myblog.server.db.model.Database;
import org.m_flak.myblog.server.db.DatabaseDate;

import static org.m_flak.myblog.server.db.methods.UserMethods.getUserID;
import static org.m_flak.myblog.server.db.methods.PostMethods.createPostFromBean;

public class PostBeanTestsFixture extends WebAuthTestsFixture {
    public PostBean mockPostBean;
    public PostBean mockPostBeanInDB;
    public List<PostBean> mockManyPostBeans;

    public static final String DATETIME_MANY_POSTS = "2020-03-16 14:05:59.0";

    public PostBeanTestsFixture(DBDatabaseDriver driver, Connection connection, Database db) {
        super(driver, connection, db);
    }

    /** Call create createMockUsers() prior! **/
    public void createMockPost() {
        ourDB.open(ourDriver, ourConn);

        final long pid = 5L;
        final long uid = getUserID(mockUser1[0], ourDB, ourConn);
        final String posted = new DatabaseDate().toString();

        PostBean postBean =
            new PostBean(
                pid,
                uid,
                "Mock Post",
                posted,
                "This post could be complete bologna!"
            );

        mockPostBean = postBean;

        createPostFromBean(postBean, ourDB, ourConn);
        ourDB.commit(ourConn);

        DBCommand cmdSelect = ourDB.createCommand();
        cmdSelect.select(ourDB.T_POSTS.C_POST_ID,
                         ourDB.T_POSTS.C_POSTER_ID,
                         ourDB.T_POSTS.C_TITLE,
                         ourDB.T_POSTS.C_DATE_POSTED,
                         ourDB.T_POSTS.C_CONTENTS
                        );

        DBReader reader = new DBReader();
        try {
            reader.open(cmdSelect, ourConn);
            mockPostBeanInDB = (PostBean) reader.getBeanList(PostBean.class).toArray()[0];
        }
        finally {
            reader.close();
        }

        ourDB.close(ourConn);
    }

    /** Call create createMockUsers() prior! **/
    public void createManyMockPosts() {
        ourDB.open(ourDriver, ourConn);

        final long pid = 5L;
        final long uid = getUserID(mockUser1[0], ourDB, ourConn);
        final DatabaseDate posted = new DatabaseDate(DATETIME_MANY_POSTS);

        PostBean[] manyPosts = new PostBean[5];
        for (int i=0; i < 5; i++) {
            manyPosts[i] = new PostBean(
                                    pid+i,
                                    uid,
                                    "Mock Post "+1+i,
                                    posted.toString(),
                                    "BOLOGNA!"
                            );
            createPostFromBean(manyPosts[i], ourDB, ourConn);

            posted.addDays(1L);
        }

        ourDB.commit(ourConn);

        mockManyPostBeans = Arrays.asList(manyPosts);

        ourDB.close(ourConn);
    }
}
