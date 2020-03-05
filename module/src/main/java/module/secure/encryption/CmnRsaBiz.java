package module.secure.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.*;
import java.security.spec.RSAPublicKeySpec;

/**
 * 암복호화(AES 128bit) Business 로직
 * <p/>
 * User: 현재호
 * Date: 2016.04.22
 * Time: 오전 9:54
 */
public class CmnRsaBiz {
    // SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(CmnRsaBiz.class);

    /**
     * rsa 공개키, 개인키 생성
     *
     * @param request
     */
    public static void initRsa(HttpServletRequest request) {

        HttpSession session = request.getSession();

        KeyPairGenerator generator;

        try {

            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);

            KeyPair keyPair = generator.genKeyPair();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            session.setAttribute("_RSA_WEB_Key_", privateKey); // session에 RSA 개인키를 세션에 저장

            RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            String publicKeyModulus = publicSpec.getModulus().toString(16);
            String publicKeyExponent = publicSpec.getPublicExponent().toString(16);

            request.setAttribute("RSAModulus", publicKeyModulus); // rsa modulus 를 request 에 추가
            request.setAttribute("RSAExponent", publicKeyExponent); // rsa exponent 를 request 에 추가
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * 복호화
     *
     * @param privateKey
     * @param securedValue
     * @return
     * @throws Exception
     */
    public static String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        
        byte[] encryptedBytes = CmnEncrypBiz.hexToByteArray(securedValue);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
        return decryptedValue;
    }
}
