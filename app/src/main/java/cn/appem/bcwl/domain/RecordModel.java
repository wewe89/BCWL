package cn.appem.bcwl.domain;

import com.wewe.android.domain.BaseBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.appem.bcwl.ui.waybill.WaybillRecordFragment;

/**
 * User:wewecn on 2015/7/20 09:55
 * Email:wewecn@qq.com
 */
public class RecordModel extends BaseBean {
    private String orderId, receiveBranch, postBranch, userName, mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPostBranch() {
        return postBranch;
    }

    public void setPostBranch(String postBranch) {
        this.postBranch = postBranch;
    }

    public String getReceiveBranch() {
        return receiveBranch;
    }

    public void setReceiveBranch(String receiveBranch) {
        this.receiveBranch = receiveBranch;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static List<RecordModel> parse(int type, JSONArray array) {
        List<RecordModel> data = new ArrayList<>();
        for (int index = 0; index < array.length(); index++) {
            JSONObject jsonObject = array.optJSONObject(index);
            RecordModel model = new RecordModel();
            model.setOrderId(jsonObject.optString("order_id"));
            model.setPostBranch(jsonObject.optString("f_store"));
            model.setReceiveBranch(jsonObject.optString("s_store"));
            if (type == WaybillRecordFragment.TYPE_POST) {
                model.setUserName(jsonObject.optString("s_linkman"));
                model.setMobile(jsonObject.optString("s_mobile"));
            } else {
                model.setUserName(jsonObject.optString("f_linkman"));
                model.setMobile(jsonObject.optString("f_mobile"));
            }
            data.add(model);
        }
        return data;
    }
}
