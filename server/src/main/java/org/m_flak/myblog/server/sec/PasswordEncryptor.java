package org.m_flak.myblog.server.sec;

import java.lang.reflect.Array;
import java.security.KeyPair;
import java.security.KeyStore;
import javax.crypto.Cipher;

public class PasswordEncryptor extends KeystoreUser {
    public PasswordEncryptor() {
        super("jks.properties", "keystore.jks");
    }

    private KeyPair getOurKeyPair() {
        KeyStore.PrivateKeyEntry privKey;

        try {
            var passParam =
                new KeyStore.PasswordProtection(keyStoreProps.getProperty("jks.keypass").toCharArray());
            privKey =
                (KeyStore.PrivateKeyEntry) keyStore.getEntry("myblog_server_alias", passParam);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new KeyPair(privKey.getCertificate().getPublicKey(),
                           privKey.getPrivateKey()
        );
    }

    public final EncryptedPassword encrypt(String password) {
        final KeyPair ourKeyPair = getOurKeyPair();
        byte[] encryptedMess;

        try {
            Cipher theCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            theCipher.init(Cipher.ENCRYPT_MODE, ourKeyPair.getPublic());

            // load pw string into buffer
            byte[] buffer = password.getBytes();
            theCipher.update(buffer, 0, Array.getLength(buffer));

            //do encryption
            encryptedMess = theCipher.doFinal();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new EncryptedPassword(encryptedMess);
    }

    public final DecryptedPassword decrypt(EncryptedPassword password) {
        final KeyPair ourKeyPair = getOurKeyPair();
        byte[] decryptedMess;

        try {
            Cipher theCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            theCipher.init(Cipher.DECRYPT_MODE, ourKeyPair.getPrivate());

            // decrypt the password
            decryptedMess = theCipher.doFinal(password.getPassword());
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new DecryptedPassword(new String(decryptedMess));
    }
}
