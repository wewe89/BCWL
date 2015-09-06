package cn.appem.bcwl.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User:wewecn on 2015/6/29 15:58
 * Email:wewecn@qq.com
 */
public class WaybillDetail {
    private String state, create_date, js, linkman;

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static List<WaybillDetail> parse(JSONObject jsonObject) {
        List<WaybillDetail> data = null;
        try {
            JSONArray list = jsonObject.getJSONArray("rows");
            data = new ArrayList<>();
            for (int index = 0; index < list.length(); index++) {
                JSONObject object = list.getJSONObject(index);
                WaybillDetail item = new WaybillDetail();
                item.setCreate_date(object.optString("create_date"));
                item.setJs(object.optString("js"));
                item.setLinkman(object.optString("linkman"));
                item.setState(object.optString("state"));
                data.add(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return data;
    }
}
