package org.m_flak.myblog.server.db.methods;

import java.sql.Connection;

import org.apache.empire.db.DBRecord;
import org.apache.empire.db.DBCommand;

import org.m_flak.myblog.server.db.model.Database;

public class UserMethods {
    protected UserMethods() {
    }

    public static DBRecord createUser(String name, String email, String about,
                                byte[] password, boolean poster, Database db,
                                Connection con) {
        DBRecord newUser = new DBRecord();
        newUser.create(db.T_USERS);
        newUser.setValue(db.T_USERS.C_NAME, name);
        newUser.setValue(db.T_USERS.C_EMAIL, email);
        newUser.setValue(db.T_USERS.C_ABOUT, about);
        newUser.setValue(db.T_USERS.C_PASSWORD, password);
        newUser.setValue(db.T_USERS.C_IS_POSTER, Boolean.valueOf(poster));
        newUser.update(con);

        return newUser;
    }

    public static DBRecord createUser(String name, String email, String about,
                                byte[] password, Database db, Connection con) {
        return createUser(name, email, about, password, false, db, con);
    }

    public static long getUserID(String name, Database db, Connection con) {
        DBCommand getIdCmd = db.createCommand();
        getIdCmd.select(db.T_USERS.C_USER_ID);
        getIdCmd.where(db.T_USERS.C_NAME.is(name));

        return db.querySingleLong(getIdCmd, con);
    }

    public static String getPostersAboutMe(Database db, Connection con) {
        DBCommand aboutMeCmd = db.createCommand();
        aboutMeCmd.select(db.T_USERS.C_ABOUT);
        aboutMeCmd.where(db.T_USERS.C_IS_POSTER.is(Boolean.valueOf(true)));

        return db.querySingleString(aboutMeCmd, "Mysterious Individual.", con);
    }
}
