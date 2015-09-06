package cn.appem.bcwl.net;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.wewe.android.util.LogUtils.LOGD;
import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * Created by Administrator on 2014/11/10.
 */
public abstract class JsonHandler extends JsonHttpResponseHandler {
    private static final String TAG = makeLogTag(JsonHandler.class);

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        LOGD(TAG, statusCode + "-String->" + responseString);
        super.onSuccess(statusCode, headers, responseString);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        LOGD(TAG, statusCode + "String->" + responseString);
        onFailure(statusCode, responseString);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        LOGD(TAG, statusCode + "JSONArray->" + throwable.toString());
        onFailure(statusCode, throwable.toString());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        LOGD(TAG, statusCode + "JSONObject->" + throwable.toString());
        onFailure(statusCode, throwable.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        LOGD(TAG, statusCode + "-JSONArray->" + response.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        LOGD(TAG, statusCode + "-JSONObject->" + response.toString());
    }

    abstract public void onFailure(int statusCode, String msg);
}
