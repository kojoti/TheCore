package me.esshd.api.utils.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class AES {

    public static String decrypt(String encryptedTextBytes, UUID key) throws GeneralSecurityException {
        String encryptionKey = key.toString().replaceAll("-", "");
        byte[] b = new byte[encryptionKey.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(encryptionKey.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        SecretKeySpec sks = new SecretKeySpec(b, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        b = new byte[encryptedTextBytes.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(encryptedTextBytes.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        byte[] decrypted = cipher.doFinal(b);
        return new String(decrypted);
    }

}
