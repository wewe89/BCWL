package cn.appem.bcwl.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.Locale;

import cn.appem.bcwl.AppConfig;

import static com.wewe.android.util.LogUtils.LOGI;
import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * Created by Administrator on 2014/11/10.
 */
public class RequestManager {
    private static final String TAG = makeLogTag(RequestManager.class);
    private static AsyncHttpClient client;

    static {
        client = new AsyncHttpClient();
//        client.setURLEncodingEnabled(true);
//        client.addHeader("Accept-Language", Locale.getDefault().toString());
//        client.addHeader("Content-Type", "application/json");
//        client.addHeader("Accept", "application/json");
//        client.addHeader("Host", h);
//        client.addHeader("Connection", "Keep-Alive");
//        setUserAgent(ClientHelper.getUserAgent());
    }
    public static AsyncHttpClient getHttpClient(){
        return client;
    }
    public static void setUserAgent(String userAgent) {
        client.setUserAgent(userAgent);
    }

    public static RequestHandle get(Context context, String url,  RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LOGI(TAG,new StringBuilder(url).append("?").append(params).toString());
        return client.get(context, url, params, responseHandler);
    }

    public static RequestHandle post(Context context, String url,  RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LOGI(TAG,new StringBuilder(url).append("?").append(params).toString());
        return client.post(context, url,params, responseHandler);
    }

    public static RequestHandle doRquest(Context context, String url,  RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return get(context, url,  params, responseHandler);
    }
}
