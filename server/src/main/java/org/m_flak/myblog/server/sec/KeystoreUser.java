package org.m_flak.myblog.server.sec;

import java.io.InputStream;
import java.util.Properties;
import java.security.KeyStore;

public abstract class KeystoreUser {
    protected final Properties keyStoreProps = new Properties();
    protected KeyStore keyStore;

    public KeystoreUser(String jksProps, String jksFile) {
        InputStream propStream =
            KeystoreUser.class.getClassLoader().getResourceAsStream(jksProps);
        InputStream ksStream =
            KeystoreUser.class.getClassLoader().getResourceAsStream(jksFile);

        try {
            keyStore = KeyStore.getInstance("jks");
            keyStoreProps.load(propStream);
            keyStore.load(ksStream, keyStoreProps.getProperty("jks.storepass").toCharArray());
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
