package org.m_flak.myblog.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.m_flak.myblog.server.db.DatabaseDate;

public class DBDateTest {
    @Test
    public void testNowTimeFormat() {
        String nowTime = new DatabaseDate().toString();
        assertTrue(nowTime.matches("^\\d+-\\d+-\\d+\\s\\d+:\\d+:\\d{2}\\.[0-9]+$"));
    }

    @Test
    public void testFromString() {
        String strDate = "2020-03-01 11:09:59";
        // DatabaseDate conforms to MySQL DATETIME type
        // so the following variable is what it should contain...
        String expectedDate = "2020-03-01 11:09:59.0";
        DatabaseDate fromStrDate = new DatabaseDate(strDate);
        assertEquals(expectedDate, fromStrDate.toString());
    }

    @Test
    public void testDateCompare() {
        DatabaseDate generatedDate = new DatabaseDate("2020-02-28 13:37:01");
        DatabaseDate expiresDate = new DatabaseDate("2020-03-01 01:01:01");
        DatabaseDate expiredDate = new DatabaseDate("2020-03-01 11:40:40");

        // Useless comparisons only to test functionality
        assertEquals(DatabaseDate.BEFORE, generatedDate.compareTo(expiresDate));
        assertEquals(DatabaseDate.AFTER, expiresDate.compareTo(generatedDate));
        assertEquals(DatabaseDate.EQUAL, generatedDate.compareTo(generatedDate));

        // Useful comparison =)
        assertEquals(DatabaseDate.AFTER, expiredDate.compareTo(expiresDate));
    }
}
