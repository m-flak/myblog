package org.m_flak.myblog.server;

import org.apache.empire.db.DBDatabaseDriver;
import java.sql.Connection;

import org.m_flak.myblog.server.db.model.Database;

import static org.m_flak.myblog.server.db.methods.UserMethods.createUser;

public class AboutMeTestFixture extends DBFixtureBase {
    public final String[] blogPoster = new String[]{
                                                 "blogposter",
                                                 "poster@blog.com",
                                                 "Hello and welcome to my blog!",
                                                 "password"
                                               };

    public AboutMeTestFixture(DBDatabaseDriver driver, Connection connection, Database db) {
        super(driver, connection, db);
    }

    public void createBlogPoster() {
        ourDB.open(ourDriver, ourConn);

        // Password is irrelevant, just get bytes
        final byte[] password = blogPoster[3].getBytes();
        createUser(
            blogPoster[0],
            blogPoster[1],
            blogPoster[2],
            password,
            true,       // yes, we are the poster.
            ourDB,
            ourConn
        );

        ourDB.commit(ourConn);
        ourDB.close(ourConn);
    }

}
