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
package org.swiftleap.common.security;


import javax.crypto.Cipher;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Created by ruan on 8/18/14.
 */
public interface EncryptionController extends ObsService {

    Cipher getDataCipher(int mode) throws GeneralSecurityException, IOException;

    boolean isInitialized();

    byte[] encryptDatabase(byte[] data) throws GeneralSecurityException, IOException;

    byte[] decryptDatabase(byte[] data) throws GeneralSecurityException, IOException;

    byte[] encrypt3DES(byte[] data) throws GeneralSecurityException, IOException;

    byte[] decrypt3DES(byte[] data) throws GeneralSecurityException, IOException;

    String encryptPassword(String password) throws GeneralSecurityException, IOException;

    KeyStore getKeyStore() throws GeneralSecurityException, IOException;

    KeyStore getTrustStore() throws GeneralSecurityException, IOException;
}
