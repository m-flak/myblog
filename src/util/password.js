import NodeRSA from 'node-rsa';

export function encryptPassword (pubKey, password) {
    // The server does not send the key with the header & trailer.
    const formattedKey = (key) => {
        const head = '-----BEGIN PUBLIC KEY-----\n';
        const trail = '\n-----END PUBLIC KEY-----';
        return head + key + trail;
    };

    // The Java backend is SUPER picky; let's appease it.
    const publicKey = new NodeRSA(formattedKey(pubKey), 'pkcs8-public-pem', { encryptionScheme: 'pkcs1' });

    // Encrypt `password` into a format suitable for xfer over the wire.
    const encryptedPass = publicKey.encrypt(Buffer.from(password), 'base64');

    return encryptedPass;
}
