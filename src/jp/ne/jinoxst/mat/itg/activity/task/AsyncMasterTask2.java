package jp.ne.jinoxst.mat.itg.activity.task;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import jp.ne.jinoxst.mat.itg.util.https._FakeX509TrustManager;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncMasterTask2 extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... urls) {
        long l1 = System.currentTimeMillis();
        try {
            _FakeX509TrustManager.allowAllSSL();
            String url = "https://webpos.interpia.ne.jp/itg/webapi001/req?sid=itemgarage02&cypher_pass=945ed62d052c82195c30183e8cc00d5d&lastseqno=0";
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setUseCaches (false);
            con.setDoOutput(true);
            int statusCode = ((HttpsURLConnection) con).getResponseCode();
            Log.d("itemgarage","*** satusCode:"+statusCode);
//            AuthResult auth = AuthResultGen.get(con.getInputStream());
//            Log.d("itemgarage","*** satus:"+auth.getStatus()+",message:"+auth.getMessage());
            con.disconnect();
//        } catch (JsonFormatException e) {
//            Log.e("JsonFormatException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } catch (Exception e){
            Log.e("Exception", e.getMessage());
        }
        long l2 = System.currentTimeMillis();
        Log.d("AsyncMasterTask2","timelap:"+(l2-l1));
        return null;
    }
}
