package org.m_flak.myblog.server;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.m_flak.myblog.server.sec.*;

public class AccTokenTest {
    @Test
    public void testAccTokenLength() {
        AccessToken aToken = AccessToken.generateAccessToken();
        String sToken = aToken.toString();
        assertEquals(64, sToken.length());
    }

    @Test
    public void testAccTokenFromString() {
        String mockToken = "1ABCDEF2ABCDEF111ABCDEF2ABCDEF111ABCDEF2ABCDEF111ABCDEF2ABCDEF11";
        long aPartOfIt = new BigInteger("1ABCDEF2ABCDEF11", 16).longValue();

        AccessToken aToken = AccessToken.generateFromString(mockToken);
        assertEquals(mockToken, aToken.toString().toUpperCase());
        assertEquals(aPartOfIt, aToken.toArray()[0]);
    }

    @Test
    public void testPaddedAccTokenFromString() {
        String mockToken = "0000000000000001000000000000000100000000000000010000000000000001";
        long aPartOfIt = 1L;

        AccessToken aToken = AccessToken.generateFromString(mockToken);
        assertEquals(mockToken, aToken.toString());
        assertEquals(aPartOfIt, aToken.toArray()[0]);
    }
}
