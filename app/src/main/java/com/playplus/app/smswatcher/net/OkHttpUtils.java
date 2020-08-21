package com.playplus.app.smswatcher.net;

import android.os.Looper;

import com.playplus.app.smswatcher.ApiCallBackInterface;
import com.playplus.app.smswatcher.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class OkHttpUtils {

    private static OkHttpUtils mOkHttpUtils;
    private  OkHttpClient okHttpClient ;
    private final int TIME_OUT_SEC = 10;
    private final String TAG = getClass().getSimpleName();

    public OkHttpUtils(){
        okHttpClient = getClient();
    }

    public static OkHttpUtils getInstance(){
        if(mOkHttpUtils == null) {
            synchronized (OkHttpUtils.class){
                if( mOkHttpUtils == null){
                    mOkHttpUtils = new OkHttpUtils();
                }
            }
        }
        return mOkHttpUtils;
    }

    private OkHttpClient getClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT_SEC, TimeUnit.SECONDS);
        builder.writeTimeout(TIME_OUT_SEC, TimeUnit.SECONDS);
        builder.sslSocketFactory(MySSLSocketClient.INSTANCE.getSslSocketFactory());
        builder.hostnameVerifier(MySSLSocketClient.INSTANCE.getHostnameVerifier());
        return builder.build();
    }

    public void post(int tag , String url , Request request , ApiCallBackInterface callBackInterface){
        LogUtils.d("/**********************************************/" + "\n" +
                "OkHttp Post" + "\n" +
                "OkHttp Tag : " + tag + "\n" +
                "OkHttp url : " + url + "\n" +
                "OkHttp header : " + request.headers() +
                "OkHttp request body : " + getRequestBodyString(request.body()).replace("\\n", "") + "\n" +
                "/**********************************************/");
        okHttpClient.newCall(request).enqueue(new OkHttpCallBack(tag , url ,callBackInterface));
    }


    public void get(int tag , String url , Request request , ApiCallBackInterface callBackInterface){

        LogUtils.d("/**********************************************/" + "\n" +
                "OkHttp Get" + "\n" +
                "OkHttp Tag : " + tag + "\n" +
                "OkHttp url : " + url + "\n" +
                "OkHttp header : " + request.headers() +
                "OkHttp request body : " + getRequestBodyString(request.body()).replace("\\n", "") + "\n" +
                "/**********************************************/");
        okHttpClient.newCall(request).enqueue(new OkHttpCallBack(tag , url ,callBackInterface));
    }


    public void put(int tag , String url , Request request , ApiCallBackInterface callBackInterface){
        LogUtils.d("/**********************************************/" + "\n" +
                "OkHttp Put" + "\n" +
                "OkHttp Tag : " + tag + "\n" +
                "OkHttp url : " + url + "\n" +
                "OkHttp header : " + request.headers() +
                "OkHttp request body : " + getRequestBodyString(request.body()).replace("\\n", "") + "\n" +
                "/**********************************************/");
        okHttpClient.newCall(request).enqueue(new OkHttpCallBack(tag , url ,callBackInterface));
    }

    private class OkHttpCallBack implements Callback {

        private ApiCallBackInterface okHttpCallBackInterface ;
        private int tag;
        private String url;
        OkHttpCallBack(int tag , String url , ApiCallBackInterface okHttpCallBackInterface){
            this.okHttpCallBackInterface = okHttpCallBackInterface;
            this.tag = tag;
            this.url = url;
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String responseString =  response.body().string();

            LogUtils.d("/**********************************************/" + "\n" +
                    "OkHttp Tag : " + tag + "\n" +
                    "OkHttp url : " + url + "\n" +
                    "OkHttp response code : " + response.code() + "\n" +
                    "OkHttp response body : " + responseString + "\n" +
                    "OkHttp response delay : " + String.valueOf((response.receivedResponseAtMillis() - response.sentRequestAtMillis())/1000.0) +"s \n" +
                    "/**********************************************/");

            if(response.code() == 200){
                //連線正常
                try{
                    okHttpCallBackInterface.onSuccess(tag,responseString);
                }catch (Exception e){
                    //反序列化失敗 資料格式不對
                    LogUtils.d("/**********************************************/" + "\n" +
                            "Error Tag : " + tag + "\n" +
                            "Error message : " + e.toString() + "\n" +
                            "/**********************************************/");
                    okHttpCallBackInterface.onFail(tag, e.toString());
                }
            } else if (response.code() == 403) {
                //token expire
                okHttpCallBackInterface.onFail(tag, "tokenExpire");
            }else{
                //連線超時
                okHttpCallBackInterface.onFail(tag,"連線失敗");
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            LogUtils.d("/**********************************************/" + "\n" +
                    "OkHttp Tag : " + tag + "\n" +
                    "OkHttp url : " + url + "\n" +
                    "OkHttp onFailure , e : " + e + "\n" +
                    "/**********************************************/");
            okHttpCallBackInterface.onFail(tag,e.toString());
        }
    }

    /**
     * 取得request body string
     * @param requestBody request body
     * @return body string
     */
    private String getRequestBodyString(RequestBody requestBody){
        RequestBody temp = requestBody;
        Buffer buffer = new Buffer();
        if(temp != null){
            try {
                temp.writeTo(buffer);
                return buffer.readUtf8();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }
}
