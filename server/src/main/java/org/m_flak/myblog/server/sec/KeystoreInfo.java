package org.m_flak.myblog.server.sec;

import java.util.Base64;
import java.security.cert.Certificate;
import java.security.KeyStoreException;

public class KeystoreInfo extends KeystoreUser {
    public KeystoreInfo() {
        super("jks.properties", "keystore.jks");
    }

    public String getEncodedPublicKey() throws KeyStoreException {
        Certificate ourCert = keyStore.getCertificate("myblog_server_alias");

        return Base64.getEncoder().encodeToString(ourCert.getPublicKey().getEncoded());
    }
}
