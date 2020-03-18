package org.m_flak.myblog.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.m_flak.myblog.server.sec.*;

public class PwTest {
    @Test
    public void testEncryptedEqual() {
        PasswordEncryptor pency = new PasswordEncryptor();
        EncryptedPassword pw1 = pency.encrypt("YOLO");
        EncryptedPassword pw2 = pency.encrypt("YOLO");

        assertTrue(pw1.compareTo(pw2) == 0);
    }

    @Test
    public void testEncryptDecrypt() {
        PasswordEncryptor pency = new PasswordEncryptor();
        EncryptedPassword pwEnc = pency.encrypt("YOLO");
        DecryptedPassword pwDec = pency.decrypt(pwEnc);
        DecryptedPassword pwMock = new DecryptedPassword("YOLO");

        assertTrue(pwDec.compareTo(pwMock) == 0);
    }
}
