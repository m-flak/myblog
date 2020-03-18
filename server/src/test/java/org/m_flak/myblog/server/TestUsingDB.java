package org.m_flak.myblog.server;

import org.junit.Before;
import org.junit.After;

import org.apache.empire.db.DBSQLScript;

import org.m_flak.myblog.server.db.ServerDatabase;

public abstract class TestUsingDB {
    @Before
    public void setUp() throws Exception {
        // Create tables in test database
        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                DBSQLScript script = new DBSQLScript();

                ServerDatabase.inst().db().get().getCreateDDLScript(driv, script);
                script.executeAll(driv, con);
                ServerDatabase.inst().db().get().commit(con);
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        // Remove Tables from Test Database
        ServerDatabase.inst().runOnDB(new Runnable() {
            @Override
            public void run() {
                var con = ServerDatabase.inst().conn();
                var driv = ServerDatabase.inst().driv();
                DBSQLScript script = new DBSQLScript();

                script.addStmt("DROP TABLE access_tokens, posts, users");
                script.executeAll(driv, con);
                ServerDatabase.inst().db().get().commit(con);
            }
        });
    }
}
