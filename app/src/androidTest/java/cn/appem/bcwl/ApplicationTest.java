package cn.appem.bcwl;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Base64;

import com.facebook.HttpMethod;
import com.loopj.android.http.AsyncHttpClient;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.ZipUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.net.RequestManager;
import cn.appem.bcwl.util.UserPref;

import static com.wewe.android.util.LogUtils.*;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    private static final String TAG = makeLogTag(ApplicationTest.class);

    public void testNet() {
//        //创建一个http客户端
//
//        HttpClient client = new DefaultHttpClient();
//        //创建一个POST请求
//        HttpPost httpPost = new HttpPost("http://www.store.com/product");
//        //组装数据放到HttpEntity中发送到服务器
//        final List dataList = new ArrayList();
//        dataList.add(new BasicNameValuePair("productName", "cat"));
//        dataList.add(new BasicNameValuePair("price", "14.87"));
//        HttpEntity entity = new UrlEncodedFormEntity(dataList, "UTF-8");
//        httpPost.setEntity(entity);
//
////向服务器发送POST请求并获取服务器返回的结果，可能是增加成功返回商品ID，或者失败等信息
//
//        HttpResponse response = client.execute(httpPost);
        sendHTTPRequest();
    }

    public void sendHTTPRequest() {
        String result = "";
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = null;
            final String url=AppConfig.SERVER_HOST1+"appAddress/PostAddress";
            LOGI(TAG,url);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json; charset=utf-8");

            UserPref pref=UserPref.getInstance(mContext);
            final String auth="Basic "+ cn.appem.bcwl.Base64.encode((pref.getUsername() + ":" + pref.getPassword()).getBytes());
            LOGI(TAG,auth);
//            httpPost.addHeader("Authorization","Basic d2V3ZWNuOjhkZGNmZjNhODBmNDE4OWNhMWM5ZDRkOTAyYzNjOTA5");

            JSONObject object = new JSONObject();
            try {
                object.put("address_id", 0);
                object.put("linkman", "测试");
                object.put("mobile", "13086690000");
                object.put("region", "111222");
                object.put("address", "dddd");
                object.put("type", 0);
                object.put("user_id", UserPref.getInstance(mContext).getUserId());
                object.put("stroe", "ddddd");
                object.put("region_id", 22121);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String content = Base64.encodeToString(ZipUtils.byteCompress(object.toString().getBytes()), Base64.DEFAULT);
            LOGI(TAG,content);
            httpPost.setEntity(new StringEntity("\"" + content + "\"", "UTF-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                result = EntityUtils.toString(entity);
            }
            LOGI(TAG, result);
        } catch (Exception e) {
            result = "-1";
            throw new RuntimeException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}