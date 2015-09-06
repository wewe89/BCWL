package cn.appem.bcwl.net;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wewe.android.string.Md5Util;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.ZipUtils;
import com.wewe.android.widget.pullableview.PullableWebView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import cn.appem.bcwl.AppConfig;
import cn.appem.bcwl.Constant;
import cn.appem.bcwl.ui.waybill.WaybillRecordFragment;
import cn.appem.bcwl.util.UserPref;
import it.sauronsoftware.base64.Base64;

/**
 * User:wewecn on 2015/5/4 14:41
 * Email:wewecn@qq.com
 */
public class Api {
    public static void getAllCity(Context context, AsyncHttpResponseHandler handler) {
        final String url = "city";
        RequestManager.doRquest(context, AppConfig.SERVER_HOST + url, null, handler);
    }

    public static void getCityBranchInfo(Context context, int areaId, AsyncHttpResponseHandler handler) {
        final String url = "region";
        RequestParams params = new RequestParams();
        params.put("method", "city");
        params.put("id", areaId);
        RequestManager.doRquest(context, AppConfig.SERVER_HOST + url, params, handler);
    }

    public static void getBranchByCounty(Context context, int areaId, AsyncHttpResponseHandler handler) {
        final String url = "region";
        RequestParams params = new RequestParams();
        params.put("county_id", areaId);
        RequestManager.doRquest(context, AppConfig.SERVER_HOST + url, params, handler);
    }

    public static void searchState(Context context, String id, AsyncHttpResponseHandler handler) {
        final String url = "WaybillState";
        RequestParams params = new RequestParams();
        params.put("method", "state");
        params.put("waybill_id", id);
        RequestManager.doRquest(context, AppConfig.SERVER_HOST + url, params, handler);
    }

    public static void searchDetail(Context context, String id, AsyncHttpResponseHandler handler) {
        final String url = "WaybillState";
        RequestParams params = new RequestParams();
        params.put("method", "detail");
        params.put("waybill_id", id);
        RequestManager.doRquest(context, AppConfig.SERVER_HOST + url, params, handler);
    }

    public static void loadRegion(Context context, AsyncHttpResponseHandler handler) {
        final String url = "region";
        RequestManager.doRquest(context, AppConfig.SERVER_HOST + url, null, handler);
    }

    public static void register(Context context, String username, String pwd, String mobile, AsyncHttpResponseHandler handler) {
        final String url = "appUser/Register";
        RequestParams params = new RequestParams();
        params.put("login_name", username);
        params.put("password", pwd);
        params.put("mobile", mobile);
        RequestManager.getHttpClient().removeAllHeaders();
        RequestManager.getHttpClient().addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestManager.post(context, AppConfig.SERVER_HOST1 + url, params, handler);
    }

    public static void login(Context context, String username, String pwd, AsyncHttpResponseHandler handler) {
        final String url = "appUser/Login";
        RequestParams params = new RequestParams();
        params.put("_n", username);
        params.put("_p", pwd);
        RequestManager.doRquest(context, AppConfig.SERVER_HOST1 + url, params, handler);
    }

    public static void getAddressList(Context context, String username, int type, int pageIndex, AsyncHttpResponseHandler handler) {
        final String url = "appAddress/QueryAddress";
        RequestParams params = new RequestParams();
        params.put("_r", username);
        params.put("_m", "");
        params.put("_a", "");
        params.put("_f", type);
        params.put("_cp", pageIndex);
        params.put("_ps", AppConfig.PAGE_SIZE);
        params.put("_u", UserPref.getInstance(context).getUserId());

        setBasicAuth(UserPref.getInstance(context));

        RequestManager.doRquest(context, AppConfig.SERVER_HOST1 + url, params, handler);
    }

    private static void setBasicAuth(UserPref pref) {
        AsyncHttpClient client = RequestManager.getHttpClient();
        client.removeAllHeaders();
        client.addHeader("Content-Type", "application/json; charset=utf-8");
        client.setBasicAuth(pref.getUsername(), pref.getPassword());
    }

    public static void editAddress(Context context, JSONObject values, AsyncHttpResponseHandler handler) {
        final String url = "appAddress/PostAddress";
        setBasicAuth(UserPref.getInstance(context));
        RequestManager.getHttpClient().post(context, AppConfig.SERVER_HOST1 + url, encode(values), CONTENT_TYPE, handler);
    }

    public static void delAddress(Context context, String id, AsyncHttpResponseHandler handler) {
        final String url = "appAddress/DelAddress";

        RequestParams params = new RequestParams("_id", id);

        setBasicAuth(UserPref.getInstance(context));

        RequestManager.get(context, AppConfig.SERVER_HOST1 + url, params, handler);
    }

    public static void newOrder(Context context, JSONObject values, AsyncHttpResponseHandler handler) {
        final String url = "appOrder/NewOrder";

        setBasicAuth(UserPref.getInstance(context));
        RequestManager.getHttpClient().post(context, AppConfig.SERVER_HOST1 + url, encode(values), CONTENT_TYPE, handler);
    }

    public static void getMyOrder(Context context, int type, String where, String startTime, String endTime, int pageIndex, AsyncHttpResponseHandler handler) {
        String url = null;
        if (type == WaybillRecordFragment.TYPE_POST)
            url = "appOrder/QueryFOrder";
        else {
            url = "appOrder/QuerySOrder";
        }
        RequestParams params = new RequestParams();
        params.put("_u", UserPref.getInstance(context).getUserId());
        params.put("_w", where);
        params.put("_st", startTime);
        params.put("_et", endTime);
        params.put("_cp", pageIndex);
        params.put("_ps", AppConfig.PAGE_SIZE);
        setBasicAuth(UserPref.getInstance(context));
        RequestManager.get(context, AppConfig.SERVER_HOST1 + url, params, handler);
    }

    public static void modifyPwd(Context context, String oldPwd, String newPwd, AsyncHttpResponseHandler handler) {
        String url = "appuser/UpdatePassword";
        RequestParams params = new RequestParams();
        UserPref pref = UserPref.getInstance(context);
        params.put("user_id", pref.getUserId());
        params.put("o_password", Md5Util.strToMd5_32(oldPwd));
        params.put("password", Md5Util.strToMd5_32(newPwd));

        RequestManager.getHttpClient().removeAllHeaders();
        RequestManager.getHttpClient().addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestManager.getHttpClient().setBasicAuth(pref.getUsername(), pref.getPassword());

        RequestManager.post(context, AppConfig.SERVER_HOST1 + url, params, handler);
    }

    public static final String CONTENT_TYPE = "application/json";

    private static HttpEntity encode(JSONObject value) {
        try {
            String content = android.util.Base64.encodeToString(ZipUtils.byteCompress(value.toString().getBytes()), android.util.Base64.DEFAULT);
            Log.i("Api", content);
            return new StringEntity("\"" + content + "\"", "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
