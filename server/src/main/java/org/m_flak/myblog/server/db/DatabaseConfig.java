package org.m_flak.myblog.server.db;

import java.io.FileNotFoundException;

import org.apache.empire.xml.XMLConfiguration;

public class DatabaseConfig extends XMLConfiguration {
    private String configPath = "";

    private String databaseProvider = "";
    private String jdbcURL = "";
    private String jdbcUser = "";
    private String jdbcPwd = "";
    /* Ugh... I can't seem to load this variable from the config file. *
     * No matter, I'm really just following the examples here -.-      */
    private String empireDBDriverClass = "org.apache.empire.db.mysql.DBDatabaseDriverMySQL";

    private final boolean fromClassPath;

    public DatabaseConfig() {
        super();

        this.configPath =
            DatabaseConfig.class.getClassLoader().getResource("database.xml").getFile();

        /* This is retarded, and I shouldn't have copied the example code when I
         * started learning empire-db.
         * * *
         * Anyways, the following is required to allow empire-db to read the
         * configuration XML from within a shaded jar.
         */
        if (this.configPath.matches("^.+\\.jar!\\/.+")) {
            fromClassPath = true;
            this.configPath = "database.xml";
        }
        else {
            fromClassPath = false;
        }
    }

    // filename is unused
    public void init(String filename) {
        super.init(this.configPath, this.fromClassPath);
        this.readProperties(this, "properties");
        this.readProperties(this, "properties-mysql");
    }

    // Getters
    public String getDatabaseProvider() {
        return this.databaseProvider;
    }
    public String getJdbcURL() {
        return this.jdbcURL;
    }
    public String getJdbcUser() {
        return this.jdbcUser;
    }
    public String getJdbcPwd() {
        return this.jdbcPwd;
    }
    public String getDriverClass() {
        return this.empireDBDriverClass;
    }

    // Setters
    public void setJdbcURL(String value) {
        this.jdbcURL = value;
    }
    public void setJdbcUser(String value) {
        this.jdbcUser = value;
    }
    public void setJdbcPwd(String value) {
        this.jdbcPwd = value;
    }
}
