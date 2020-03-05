package module.secure.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * 암복호화 공통 Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 14
 * Time: 오후 3:10
 */
public class CmnEncrypBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnEncrypBiz.class); // SLF4J Logger

    public static String secureKey = ResourceBundle.getBundle("config").getString("secure.instance.key");

    /**
     * hex to byte[] : 16진수 문자열을 바이트 배열로 변환한다.
     *
     * @param hex hex string
     * @return
     */
    public static byte[] hexToByteArray(String hex) {

        if (hex == null || hex.length() == 0) {

            return null;
        }

        byte[] ba = new byte[hex.length() / 2];

        for (int i = 0; i < ba.length; i++) {

            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return ba;
    }
}
