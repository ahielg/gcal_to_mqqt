package util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Ahielg
 * @date 29/05/2016
 */
class LocalEncryptor {
    private static SecretKey key;
    private static sun.misc.BASE64Encoder base64encoder;
    private static sun.misc.BASE64Decoder base64decoder;

    static {
        try {
            DESKeySpec keySpec = new DESKeySpec("beezratHashem".getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException ignored) {
        }
        base64encoder = new BASE64Encoder();
        base64decoder = new BASE64Decoder();
    }

    static String encrypt(String plainTextPassword) throws Exception {
        byte[] cleartext = plainTextPassword.getBytes("UTF8");

        Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return base64encoder.encode(cipher.doFinal(cleartext));
    }

    static String decrypt(String encryptedPwd) throws Exception {
        byte[] encrypedPwdBytes = base64decoder.decodeBuffer(encryptedPwd);

        Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
        return new String(plainTextPwdBytes);
    }
}