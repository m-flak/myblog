package org.m_flak.myblog.server.sec;

import java.lang.Comparable;

public class DecryptedPassword implements Password<String>, Comparable<DecryptedPassword> {
    private final String decPassword;

    public DecryptedPassword(String decryptedPassword) {
        decPassword = decryptedPassword;
    }

    @Override
    public String getPassword() {
        return this.decPassword;
    }

    @Override
    public int getSize() {
        return this.decPassword.length();
    }

    @Override
    public int compareTo(DecryptedPassword other) {
        return this.decPassword.compareTo(other.getPassword());
    }
}
