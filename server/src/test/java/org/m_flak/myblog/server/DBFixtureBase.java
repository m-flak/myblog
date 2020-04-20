package org.m_flak.myblog.server;

import java.sql.Connection;

import org.apache.empire.db.DBDatabaseDriver;

import org.m_flak.myblog.server.db.model.Database;

public abstract class DBFixtureBase {
    protected DBDatabaseDriver ourDriver;
    protected Connection ourConn;
    protected Database ourDB;

    public DBFixtureBase(DBDatabaseDriver driver, Connection connection, Database db) {
        ourDriver = driver;
        ourConn = connection;
        ourDB = db;
    }
}
