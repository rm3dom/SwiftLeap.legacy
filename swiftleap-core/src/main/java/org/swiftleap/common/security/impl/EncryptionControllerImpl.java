/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.common.security.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.swiftleap.common.config.PropKeys;
import org.swiftleap.common.security.EncryptionController;
import org.swiftleap.common.security.Obs;
import org.swiftleap.common.util.Base64;
import org.swiftleap.common.util.HexUtil;
import org.swiftleap.common.util.IOUtil;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * keytool -keystore keystore.jks -genkey -alias swiftleap  -keyalg RSA -deststoretype jks
 * keytool -list -v -keystore keystore.jks
 * Created by ruan on 8/18/14.
 */
@Service
public class EncryptionControllerImpl implements EncryptionController {
    private static final int MAX_DATA_BLOCK_SIZE = 117;
    private final IvParameterSpec ivParameters = new IvParameterSpec(new byte[]{12, 34, 56, 78, 90, 87, 65, 43});
    private final byte[] publicSalt = HexUtil.fromHex("9096e09204da6e12df90ddf61efb66886e12df90ddf61efb");
    @Value(value = PropKeys._SECURITY_KEYSTORE)
    private String keyStoreUrl = "classpath:keystore.jks";
    @Value(value = PropKeys._SECURITY_TRUSTSTORE)
    private String trustStoreUrl = "classpath:keystore.jks";
    private String storeType = "jks";
    @Value(value = PropKeys._SECURITY_KEYSTORE_PASSWORD)
    private String keyStorePassword = "password";
    @Value(value = PropKeys._SECURITY_TRUSTSTORE_PASSWORD)
    private String trustStorePassword = "password";
    @Value(value = PropKeys._SECURITY_PRIVATEKEY_ALIAS)
    private String privateKeyAlias = "swiftleap";
    @Value(value = PropKeys._SECURITY_PRIVATEKEY_PASSWORD)
    private String privateKeyPassword = "password";
    //DES key calculated from private key and public salt.
    private SecretKey private3DESKey;
    private SecretKey privateAESKey;
    private boolean initCalled = false;

    @PostConstruct
    void init() throws Exception {
        KeyStore ks = getKeyStore();
        Key key = ks.getKey(privateKeyAlias, Obs.decryptIfObs(privateKeyPassword).toCharArray());
        key.getEncoded();
        PrivateKey pkey = null;
        if (key instanceof PrivateKey) {
            pkey = (PrivateKey) key;
        } else {
            if (key.getFormat().startsWith("PKCS")) {
                PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(key.getEncoded());
                KeyFactory kf = KeyFactory.getInstance(key.getAlgorithm());
                pkey = kf.generatePrivate(kspec);
            }
        }

        if (pkey == null || !pkey.getAlgorithm().contains("RSA")) {
            throw new Exception("Invalid data key type, expected RSA key type.");
        }

        //Generate DES salt from
        byte[] privateSalt = Arrays.copyOf(encryptRSABlock(publicSalt, pkey), 24);
        private3DESKey = new SecretKeySpec(privateSalt, "DESede");
        privateAESKey = new SecretKeySpec(privateSalt, "AES");
        initCalled = true;
    }

    @Override
    public Cipher getDataCipher(int mode) throws GeneralSecurityException, IOException {
        Cipher c = Cipher.getInstance("AES");
        c.init(mode, privateAESKey);
        return c;
    }


    @Override
    public boolean isInitialized() {
        return initCalled;
    }


    private byte[] encryptRSABlock(byte[] data, Key key) throws GeneralSecurityException, IOException {
        if (data.length > MAX_DATA_BLOCK_SIZE) {
            throw new RuntimeException("Invalid block size!");
        }
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }


    private String getStoreType() {
        return storeType;
    }

    private InputStream getStream(String path) throws IOException {
        return IOUtil.getFileStream(path);
    }

    @Override
    public byte[] encryptDatabase(byte[] data) throws GeneralSecurityException, IOException {
        final Cipher cipher = getDataCipher(Cipher.ENCRYPT_MODE);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decryptDatabase(byte[] data) throws GeneralSecurityException, IOException {
        final Cipher cipher = getDataCipher(Cipher.DECRYPT_MODE);
        return cipher.doFinal(data);
    }

    private byte[] encrypt3DES(byte[] data, final SecretKey key) throws GeneralSecurityException, IOException {
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameters);
        return cipher.doFinal(data);
    }

    private byte[] encrypt3DES(byte[] data, byte[] salt) throws GeneralSecurityException, IOException {
        final SecretKey key = new SecretKeySpec(salt, "DESede");
        return encrypt3DES(data, key);
    }

    @Override
    public byte[] encrypt3DES(byte[] data) throws GeneralSecurityException, IOException {
        return encrypt3DES(data, private3DESKey);
    }

    @Override
    public byte[] decrypt3DES(byte[] data) throws GeneralSecurityException, IOException {
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, private3DESKey, ivParameters);
        return decipher.doFinal(data);
    }

    @Override
    public String encryptPassword(String password) throws GeneralSecurityException, IOException {
        byte[] passBytes = password.getBytes("UTF-8");
        byte[] passSalt = Arrays.copyOf(passBytes, 24);
        for (int i = password.length(), j = 0; i < 24; ) {
            passSalt[i++] = passSalt[j++];
        }
        byte[] enc = encrypt3DES(passBytes, private3DESKey);
        enc = encrypt3DES(enc, passSalt);
        if (enc.length > 64) {
            enc = Arrays.copyOf(enc, 64);
        }
        return new String(Base64.encode(enc));
    }

    @Override
    public String obfuscatePassword(String password) throws GeneralSecurityException, IOException {
        DESKeySpec keySpec = new DESKeySpec(publicSalt);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        byte[] cleartext = password.getBytes("UTF8");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameters);
        return "obf:" + new String(Base64.encode(cipher.doFinal(cleartext)));
    }

    @Override
    public String unObfuscatePassword(String obfuscated) throws GeneralSecurityException, IOException {
        if (obfuscated.startsWith("obs:") || obfuscated.startsWith("OBS:")) {
            return Obs.decrypt(obfuscated);
        }

        if (obfuscated.startsWith("obf:") || obfuscated.startsWith("OBF:")) {
            obfuscated = obfuscated.substring(4);
        } else {
            //It is not obfuscated
            return obfuscated;
        }
        DESKeySpec keySpec = new DESKeySpec(publicSalt);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameters);
        byte[] bytes = cipher.doFinal(Base64.decode(obfuscated));
        return new String(bytes, "UTF8");
    }

    private InputStream getKeyStoreInput() throws IOException {
        return getStream(keyStoreUrl);
    }

    private InputStream getTrustStoreInput() throws IOException {
        return getStream(trustStoreUrl);
    }

    private String getKeyStorePassword() throws GeneralSecurityException, IOException {
        return unObfuscatePassword(keyStorePassword);
    }


    private String getTrustStorePassword() throws GeneralSecurityException, IOException {
        return unObfuscatePassword(trustStorePassword);
    }

    @Override
    public KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(getStoreType());
        try (InputStream storeIn = getKeyStoreInput()) {
            ks.load(storeIn, getKeyStorePassword().toCharArray());
        }
        return ks;
    }

    @Override
    public KeyStore getTrustStore() throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(getStoreType());
        try (InputStream storeIn = getTrustStoreInput()) {
            ks.load(storeIn, getTrustStorePassword().toCharArray());
        }
        return ks;
    }
}
