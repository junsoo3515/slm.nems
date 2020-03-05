package module.secure.encryption;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * 암복호화(AES 128bit) Business 로직
 * <p/>
 * User: 현재호
 * Date: 2016.04.22
 * Time: 오전 9:54
 */
public class CmnAesBiz {
    // SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(CmnAesBiz.class);

    // 암호화 관련 인자
    private static SecretKey sKey;
    private static SecretKeySpec keySpec;
    private static SecureRandom randomKey;
    private static Cipher cipher;

    /**
     * AES 128bit 비밀 키 생성
     */
    private static void createKey() {

        try {

            randomKey = SecureRandom.getInstance("SHA1PRNG");
            randomKey.setSeed(CmnEncrypBiz.secureKey.getBytes());

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, randomKey);

            sKey = kgen.generateKey();

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * 암호화(AES)
     *
     * @param str 문자열
     * @return String 암호화 된 문자열
     */
    public static String encrypt(String str) {

        try {

            if (sKey == null) createKey();

            keySpec = new SecretKeySpec(sKey.getEncoded(), "AES");

            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

//            log.info("encrypt :" + str + " -> " + Hex.encodeHexString(cipher.doFinal(str.getBytes())));

            return Hex.encodeHexString(cipher.doFinal(str.getBytes()));

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return null;
        }
    }

    /**
     * 복호화(AES)
     *
     * @param str 암호화 된 문자열
     * @return String 복호화 된 문자열
     */
    public static String decrypt(String str) {

        try {

            if (sKey == null) createKey();

            keySpec = new SecretKeySpec(sKey.getEncoded(), "AES");

            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] original = cipher.doFinal(CmnEncrypBiz.hexToByteArray(str));

//            log.info("decrypt :" + str + " -> Original(" + new String(original) + "), encode(" + Hex.encodeHexString(original) + ")");

            return new String(original);

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return null;
        }
    }
}
