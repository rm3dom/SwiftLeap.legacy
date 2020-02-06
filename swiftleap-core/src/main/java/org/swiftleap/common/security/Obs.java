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

import lombok.val;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Obs {
    private static final String EncryptionKey = "5y5adm3tt3r_D@t@EnDeCrypt";

    private static byte[] createSecretKey() {
        val passBytes = EncryptionKey.getBytes(StandardCharsets.UTF_8);
        val encryptionKeyBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

        int len = passBytes.length;
        if (len > encryptionKeyBytes.length)
            len = encryptionKeyBytes.length;
        for (int i = 0; i < len; i++) {
            encryptionKeyBytes[i] = passBytes[i];
        }
        return encryptionKeyBytes;
    }

    public static String encrypt(String strToEncrypt) {
        try {
            val encryptionKeyBytes = createSecretKey();
            IvParameterSpec ivspec = new IvParameterSpec(encryptionKeyBytes);
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decryptIfObs(String strToDecrypt) {
        if (!strToDecrypt.startsWith("obs:") && !strToDecrypt.startsWith("OBS:"))
            return strToDecrypt;
        return decrypt(strToDecrypt);
    }

    public static String decrypt(String strToDecrypt) {
        try {
            if (strToDecrypt.startsWith("obs:") || strToDecrypt.startsWith("OBS:"))
                strToDecrypt = strToDecrypt.substring(4);

            val encryptionKeyBytes = createSecretKey();
            IvParameterSpec ivspec = new IvParameterSpec(encryptionKeyBytes);
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKeyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
