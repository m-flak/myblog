package org.m_flak.myblog.server.sec;

import java.lang.Long;
import java.lang.IllegalArgumentException;
import java.lang.NumberFormatException;
import java.lang.StringBuilder;
import java.math.BigInteger;

import java.util.Random;
import java.util.stream.LongStream;

public class AccessToken {
    /* numericComponents is an array of length 4 */
    public final int numComponents = 4;
    protected final long[] numericComponents;

    protected AccessToken(long[] numericComponents) {
        this.numericComponents = numericComponents;
    }

    public static AccessToken generateAccessToken() {
        Random rando = new Random();
        final int numComponents = 4;
        final long[] components = rando.longs().limit(numComponents).toArray();

        return new AccessToken(components);
    }

    public static AccessToken generateFromString(String inputString) throws
                                                    IllegalArgumentException {
        if (inputString.length() != 64) {
            throw new IllegalArgumentException("'inputString' MUST == 64!");
        }

        // Split the string into 4 strings
        String[] strings = new String[]{ inputString.substring(0, 16),
                                          inputString.substring(16, 32),
                                          inputString.substring(32, 48),
                                          inputString.substring(48, 64)
                                       };

        var streamBuild = LongStream.builder();

        // turn those strings into longs
        for (String s : strings) {
            long parsedLong;

            /* If parseLong doesn't like the long, use BigInt instead... */
            try {
                parsedLong = Long.parseLong(s, 16);
            }
            catch (NumberFormatException nfe) {
                parsedLong = new BigInteger(s, 16).longValue();
            }

            streamBuild.add(parsedLong);
        }

        return new AccessToken(streamBuild.build().toArray());
    }

    @Override
    public String toString() {
        StringBuilder sbild = new StringBuilder(64);

        for (int i=0; i < numComponents; i++) {
            String appendMe = Long.toHexString(numericComponents[i]);

            // FOR EDGE CASES
            // when a generated number is not large enough as a hex string ...
            // ... then pad it with `difference` zeros.
            if (appendMe.length() < 16) {
                int difference = 16 - appendMe.length();
                sbild.append("0".repeat(Math.max(0, difference)));
            }

            sbild.append(appendMe);
        }

        return sbild.toString();
    }

    public final long[] toArray() {
        return numericComponents;
    }
}
