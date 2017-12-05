package jp.ne.jinoxst.mat.itg.util;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.exception.ExceptionUtils;

import android.content.Context;
import android.util.Log;

public class FileDownload {
    private String fileAbsolutePath;
    private Context context;

    public FileDownload(Context context, String path){
        this.context = context;
        this.fileAbsolutePath = path;
    }

    public void save(){
        try{
            URL imgUrl = new URL(fileAbsolutePath);
            String fileName = getFilenameFromURL(imgUrl);
            URLConnection conn = imgUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            int response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK){
                InputStream in = httpConn.getInputStream();
                FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                DataInputStream dataInStream = new DataInputStream(in);
                DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(outStream));
                byte[] b = new byte[4096];
                int readByte = 0;
                while(-1 != (readByte = dataInStream.read(b))){
                    dataOutStream.write(b, 0, readByte);
                }

                dataInStream.close();
                dataOutStream.close();
            }
        }catch(Exception e){
            Log.e("HttpsException", ExceptionUtils.getStackTrace(e));
        }
    }

    private String getFilenameFromURL(URL url) {
        String[] p = url.getFile().split("/");
        String s = p[p.length - 1];
        if (s.indexOf("?") > -1) {
            return s.substring(0, s.indexOf("?"));
        }
        return s;
    }
}
