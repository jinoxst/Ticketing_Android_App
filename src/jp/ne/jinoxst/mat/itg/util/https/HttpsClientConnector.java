package jp.ne.jinoxst.mat.itg.util.https;

import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.GlobalRegistry;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.net.Uri;

public class HttpsClientConnector {
    private Uri.Builder builder;

    public HttpsClientConnector(String service){
        builder = new Uri.Builder();
        builder.scheme(Constant.HTTPS);
        builder.encodedAuthority(Constant.SRVER_URL);
        builder.path(Constant.BASE_PATH + "/" + service + "/" + Constant.DEFAULT_REQUEST);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        builder.appendQueryParameter(Constant.LOGIN_ID, registry.getString(Constant.LOGIN_ID));
        builder.appendQueryParameter(Constant.LOGIN_PW, registry.getString(Constant.LOGIN_PW));
        builder.appendQueryParameter(Constant.MACADDRESS, registry.getString(Constant.MACADDRESS));
    }

    public HttpGet getRequest(){
        return new HttpGet(builder.build().toString());
    }

    public void setParameter(String key, String value){
        builder.appendQueryParameter(key, value);
    }

    public DefaultHttpClient getHttpClient(){
        SchemeRegistry schreg = new SchemeRegistry();
        SocketFactory socketFactory = new EasySSLSocketFactory();
        schreg.register(new Scheme(Constant.HTTPS, socketFactory, Constant.HTTPS_PORT));
        HttpParams params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        //        HttpConnectionParams.setSocketBufferSize(params, 4096);//ソケットバッファサイズ 4KB
        //        HttpConnectionParams.setSoTimeout(params, 20000);//ソケット通信タイムアウト20秒
        //        HttpConnectionParams.setConnectionTimeout(params, 20000);//HTTP通信タイムアウト20秒
        //        HttpProtocolParams.setContentCharset(params, "UTF-8");//文字コードをUTF-8と明示
        //        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);//HTTP1.1

        ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, schreg);
        DefaultHttpClient httpClient = new DefaultHttpClient(connManager, params);

        return httpClient;
    }
}
