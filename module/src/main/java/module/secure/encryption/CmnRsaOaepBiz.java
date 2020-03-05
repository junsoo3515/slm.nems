package module.secure.encryption;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

/**
 * 암복호화(RSA + OAEP 2048bit) Business 로직
 * <p/>
 * User: 현재호
 * Date: 2016.04.14
 * Time: 오후 3:09
 */
public class CmnRsaOaepBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnRsaOaepBiz.class); // SLF4J Logger

    // 암호화 관련 인자
    private static Key pubKey, privKey;
    private static Cipher cipher;

    /**
     * RSA + OAEP 2048bit 비밀 키 생성
     */
    private static SecureRandom createKey() {

        try {

            SecureRandom rKey = SecureRandom.getInstance("SHA1PRNG");
            rKey.setSeed(CmnEncrypBiz.secureKey.getBytes());

            KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
            kgen.initialize(2048, rKey);

            KeyPair pair = kgen.generateKeyPair();

            pubKey = pair.getPublic();
            privKey = pair.getPrivate();

            return rKey;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return null;
        }
    }

    /**
     * 암호화(RSA + OAEP)
     *
     * @param str 문자열
     * @return String 암호화 된 문자열
     */
    public static String encrypt(String str) {

        try {

            SecureRandom randomKey = createKey();

            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey, randomKey);

//            log.info("encrypt :" + str + " -> " + Hex.encodeHexString(cipher.doFinal(str.getBytes())));

            return Hex.encodeHexString(cipher.doFinal(str.getBytes()));

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return null;
        }
    }

    /**
     * 복호화(RSA + OAEP)
     *
     * @param str 암호화 된 문자열
     * @return String 복호화 된 문자열
     */
    public static String decrypt(String str) {

        try {

            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, privKey);

            byte[] original = cipher.doFinal(CmnEncrypBiz.hexToByteArray(str));

//            log.info("decrypt :" + str + " -> Original(" + new String(original) + "), encode(" + Hex.encodeHexString(original) + ")");

            return new String(original);

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return null;
        }
    }
}
