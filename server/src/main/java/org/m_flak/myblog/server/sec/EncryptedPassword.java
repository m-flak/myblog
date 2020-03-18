package org.m_flak.myblog.server.sec;

import java.lang.Comparable;
import java.lang.reflect.Array;
import java.util.Arrays;


public class EncryptedPassword implements Password<byte[]>, Comparable<EncryptedPassword> {
    private final byte[] encPassword;

    public EncryptedPassword(byte[] encryptedPass) {
        encPassword = encryptedPass;
    }

    @Override
    public byte[] getPassword() {
        return this.encPassword;
    }

    @Override
    public int getSize() {
        return Array.getLength(this.encPassword);
    }

    @Override
    public int compareTo(EncryptedPassword other) {
        int result = 0;

        // If contents not identical, their size will still be.
        if (!Arrays.equals(this.encPassword, other.getPassword())) {
            result = Math.abs(this.getSize() - other.getSize());
        }

        return result;
    }
}
