package jp.ne.jinoxst.mat.itg.util.https;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.GlobalRegistry;
import android.util.Log;

public class HttpsUrlConnector {
    public static HttpsURLConnection getConnection(String service) {
        HttpsURLConnection con = null;
        try {
            GlobalRegistry registry = GlobalRegistry.getInstance();
            _FakeX509TrustManager.allowAllSSL();
            String url = Constant.HTTPS + "://" +
                         Constant.SRVER_URL + "/" +
                         Constant.BASE_PATH + "/" +service + "/" +
                         Constant.DEFAULT_REQUEST + "/";
            String params = "?" + Constant.LOGIN_ID + "=" + registry.getString(Constant.LOGIN_ID) +
                            "&" + Constant.LOGIN_PW + "=" + registry.getString(Constant.LOGIN_PW) +
                            "&lastseqno=0";
            url += params;
            //"https://webpos.interpia.ne.jp/itg/webapi011/req?sid=itemgarage02&cypher_pass=945ed62d052c82195c30183e8cc00d5d&lastseqno=0";
            Log.d("",url);
            con = (HttpsURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setUseCaches(false);
            con.setDoOutput(true);
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        return con;
    }
}
