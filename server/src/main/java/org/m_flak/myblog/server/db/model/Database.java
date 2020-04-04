package org.m_flak.myblog.server.db.model;

import java.sql.Connection;

import org.apache.empire.data.DataType;
import org.apache.empire.db.DBColumn;
import org.apache.empire.db.DBDatabase;
import org.apache.empire.db.DBTable;
import org.apache.empire.db.DBTableColumn;

import org.apache.empire.db.DBDatabaseDriver;
import org.apache.empire.db.exceptions.DatabaseNotOpenException;

public class Database extends DBDatabase {
    private final static long serialVersionUID = 1L;

    public static class Users extends DBTable {
        private final static long serialVersionUID = 1L;

        public final DBTableColumn C_USER_ID;
        public final DBTableColumn C_NAME;
        public final DBTableColumn C_EMAIL;
        public final DBTableColumn C_ABOUT;
        public final DBTableColumn C_PASSWORD;


        public Users(DBDatabase db) {
            super("USERS", db);

            C_USER_ID = addColumn("USER_ID", DataType.AUTOINC, 0, true, "USER_ID_SEQUENCE");
            C_NAME = addColumn("NAME", DataType.CHAR, 16, true);
            C_EMAIL = addColumn("EMAIL", DataType.CHAR, 32, true);
            C_ABOUT = addColumn("ABOUT_ME", DataType.CLOB, 0, false);
            C_PASSWORD = addColumn("PASSWORD", DataType.BLOB, 0, false);

            setPrimaryKey(C_USER_ID);
            addIndex("USER_ID_IDX", true, new DBColumn[] { C_USER_ID });
        }
    }

    public static class AccessTokens extends DBTable {
        private final static long serialVersionUID = 1L;

        public final DBTableColumn C_TOKEN_ID;
        public final DBTableColumn C_USER_ID;
        public final DBTableColumn C_ACCESS_TOKEN;
        public final DBTableColumn C_GENERATED;
        public final DBTableColumn C_EXPIRE;

        public AccessTokens(DBDatabase db) {
            super("ACCESS_TOKENS", db);

            C_TOKEN_ID = addColumn("TOKEN_ID", DataType.AUTOINC, 0, true, "TOKEN_ID_SEQUENCE");
            C_USER_ID = addColumn("USER_ID", DataType.INTEGER, 0, true);
            C_ACCESS_TOKEN = addColumn("ACCESS_TOKEN", DataType.CHAR, 64, true);
            C_GENERATED = addColumn("GENERATED", DataType.DATETIME, 0, false);
            C_EXPIRE = addColumn("EXPIRES", DataType.DATETIME, 0, false);

            setPrimaryKey(C_TOKEN_ID);
            addIndex("TOKEN_ID_IDX", true, new DBColumn[] { C_TOKEN_ID });
        }

    }

    public static class Posts extends DBTable {
        private final static long serialVersionUID = 1L;

        public final DBTableColumn C_POST_ID;
        public final DBTableColumn C_POSTER_ID;
        public final DBTableColumn C_TITLE;
        public final DBTableColumn C_DATE_POSTED;
        public final DBTableColumn C_CONTENTS;

        public Posts(DBDatabase db) {
            super("POSTS", db);

            C_POST_ID = addColumn("POST_ID", DataType.AUTOINC, 0, true, "POST_ID_SEQUENCE");
            C_POST_ID.setBeanPropertyName("postID");

            C_POSTER_ID = addColumn("POSTER_ID", DataType.INTEGER, 0, true);
            C_POSTER_ID.setBeanPropertyName("posterID");

            C_TITLE = addColumn("TITLE", DataType.CHAR, 128, true);
            C_TITLE.setBeanPropertyName("title");

            C_DATE_POSTED = addColumn("DATE_POSTED", DataType.DATETIME, 0, true);
            C_DATE_POSTED.setBeanPropertyName("datePosted");

            C_CONTENTS = addColumn("CONTENTS", DataType.CLOB, 0, true);
            C_CONTENTS.setBeanPropertyName("contents");

            setPrimaryKey(C_POST_ID);
            addIndex("POST_ID_IDX", true, new DBColumn[] { C_POST_ID });
        }
    }

    public final Users T_USERS = new Users(this);
    public final AccessTokens T_TOKENS = new AccessTokens(this);
    public final Posts T_POSTS = new Posts(this);

    public Database() {
        addRelation(T_TOKENS.C_USER_ID.referenceOn(T_USERS.C_USER_ID)).onDeleteCascade();
        addRelation(T_POSTS.C_POSTER_ID.referenceOn(T_USERS.C_USER_ID)).onDeleteCascade();
    }

    @Override
    public void open(DBDatabaseDriver driver, Connection conn) {
        super.open(driver, conn);

        final int maxTries = 3;
        for (int i = 0; i < maxTries; i++) {
            try {
                checkOpen();
                break;
            }
            catch (DatabaseNotOpenException done) {
                super.open(driver, conn);
            }
        }
    }
}
