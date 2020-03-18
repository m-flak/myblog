package org.m_flak.myblog.server.db.methods;

import java.sql.Connection;

import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBRecord;
import org.apache.empire.db.exceptions.QueryNoResultException;

import org.m_flak.myblog.server.db.model.Database;
import org.m_flak.myblog.server.db.DatabaseDate;
import org.m_flak.myblog.server.sec.AccessToken;

import static org.m_flak.myblog.server.db.methods.UserMethods.getUserID;

public class AccessTokenMethods {
    protected AccessTokenMethods() {
    }

    public static long countAccessTokens(Database db, Connection con) {
        DBCommand countCmd = db.createCommand();
        countCmd.select(db.T_TOKENS.count());
        return db.querySingleLong(countCmd, 0L, con);
    }

    public static String createTokenForUser(String userName, Database db, Connection con) {
        // Acquire information to put in the new row
        long userId = getUserID(userName, db, con);
        AccessToken accToken = AccessToken.generateAccessToken();
        DatabaseDate genDate = new DatabaseDate();
        /*
         * ACCESS TOKENS ARE VALID FOR 24HRS
         */
        DatabaseDate expDate = new DatabaseDate(genDate);
        expDate.addDays(1L);

        DBRecord newToken = new DBRecord();
        newToken.create(db.T_TOKENS);
        newToken.setValue(db.T_TOKENS.C_USER_ID, userId);
        newToken.setValue(db.T_TOKENS.C_ACCESS_TOKEN, accToken.toString());
        newToken.setValue(db.T_TOKENS.C_GENERATED, genDate.toString());
        newToken.setValue(db.T_TOKENS.C_EXPIRE, expDate.toString());
        newToken.update(con);

        return accToken.toString();
    }

    public static boolean userHasToken(String userName, Database db, Connection con) {
        long userId = getUserID(userName, db, con);
        DBCommand tokenCmd = db.createCommand();
        tokenCmd.select(db.T_TOKENS.C_ACCESS_TOKEN);
        tokenCmd.where(db.T_TOKENS.C_USER_ID.is(userId));

        try {
            db.querySingleValue(tokenCmd, DataType.CHAR, con);
        }
        catch (QueryNoResultException qne) {
            return false;
        }

        return true;
    }

    public static boolean isTokenValid(String token, Database db, Connection con) {
        DBCommand dateCmd = db.createCommand();
        dateCmd.select(db.T_TOKENS.C_EXPIRE);
        dateCmd.where(db.T_TOKENS.C_ACCESS_TOKEN.is(token));

        DatabaseDate nowDate = new DatabaseDate();
        DatabaseDate expDate;

        try {
            expDate = new DatabaseDate(db.querySingleString(dateCmd, con));
        }
        catch (QueryNoResultException qne) {
            // not valid, non-existent
            return false;
        }

        if (nowDate.compareTo(expDate) == DatabaseDate.BEFORE) {

            return true;
        }

        return false;
    }

    public static boolean userTokenExpired(String userName, Database db, Connection con) {
        long userId = getUserID(userName, db, con);
        DBCommand dateCmd = db.createCommand();
        dateCmd.select(db.T_TOKENS.C_EXPIRE);
        dateCmd.where(db.T_TOKENS.C_USER_ID.is(userId));

        DatabaseDate nowDate = new DatabaseDate();
        DatabaseDate expDate;

        try {
            expDate = new DatabaseDate(db.querySingleString(dateCmd, con));
        }
        catch (QueryNoResultException qne) {
            // Not having one - might as well be expired.
            return true;
        }

        if (nowDate.compareTo(expDate) == DatabaseDate.BEFORE) {
            return false;
        }

        return true;
    }

    public static String updateUserToken(String userName, Database db, Connection con) {
        long userId = getUserID(userName, db, con);

        DBCommand tokenIndexCmd = db.createCommand();
        tokenIndexCmd.select(db.T_TOKENS.C_TOKEN_ID);
        tokenIndexCmd.where(db.T_TOKENS.C_USER_ID.is(userId));

        long tokenIndex = db.querySingleLong(tokenIndexCmd, con);

        AccessToken accToken = AccessToken.generateAccessToken();
        DatabaseDate genDate = new DatabaseDate();
        DatabaseDate expDate = new DatabaseDate(genDate);
        expDate.addDays(1L);

        DBRecord oldRecord = new DBRecord();
        oldRecord.read(db.T_TOKENS, tokenIndex, con);
        oldRecord.setValue(db.T_TOKENS.C_ACCESS_TOKEN, accToken.toString());
        oldRecord.setValue(db.T_TOKENS.C_GENERATED, genDate.toString());
        oldRecord.setValue(db.T_TOKENS.C_EXPIRE, expDate.toString());
        oldRecord.update(con);

        return accToken.toString();
    }

    public static String fetchUserToken(String userName, Database db, Connection con) {
        long userId = getUserID(userName, db, con);
        DBCommand tokenCmd = db.createCommand();
        tokenCmd.select(db.T_TOKENS.C_ACCESS_TOKEN);
        tokenCmd.where(db.T_TOKENS.C_USER_ID.is(userId));

        AccessToken fetchToken = AccessToken.generateFromString(db.querySingleString(tokenCmd, con));
        return fetchToken.toString();
    }

    public static long fetchIDForToken(String token, Database db, Connection con) {
        DBCommand usrCmd = db.createCommand();
        usrCmd.select(db.T_TOKENS.C_USER_ID);
        usrCmd.where(db.T_TOKENS.C_ACCESS_TOKEN.is(token));

        return db.querySingleLong(usrCmd, con);
    }

    public static void invalidateToken(String stringToken, Database db, Connection con) {
        DBCommand tokenIndexCmd = db.createCommand();
        tokenIndexCmd.select(db.T_TOKENS.C_TOKEN_ID);
        tokenIndexCmd.where(db.T_TOKENS.C_ACCESS_TOKEN.is(stringToken));

        long tokenIndex;
        try {
            tokenIndex = db.querySingleLong(tokenIndexCmd, con);
        }
        catch (QueryNoResultException qe) {
            // Nothing to invalidate. Return...
            return;
        }

        DatabaseDate nowDate = new DatabaseDate();

        DBRecord oldRecord = new DBRecord();
        oldRecord.read(db.T_TOKENS, tokenIndex, con);
        oldRecord.setValue(db.T_TOKENS.C_EXPIRE, nowDate.toString());
        oldRecord.update(con);
    }
}
