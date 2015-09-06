package cn.appem.bcwl.domain;

import com.baidu.mapapi.model.LatLng;
import com.wewe.android.domain.BaseBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.wewe.android.util.LogUtils.LOGD;
import static com.wewe.android.util.LogUtils.LOGI;

/**
 * User:wewecn on 2015/7/13 10:01
 * Email:wewecn@qq.com
 */
public class AddressInfo extends BaseBean {
    private String name,address,phone,branch,region;
private String address_id,region_id;
    public String getAddress() {
        return address;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static ArrayList<AddressInfo> parse(JSONArray postsArray) {
        ArrayList<AddressInfo> datas = new ArrayList<>();
        for (int i = 0; i < postsArray.length(); i++) {
            AddressInfo freshNews = new AddressInfo();
            JSONObject jsonObject = postsArray.optJSONObject(i);

            freshNews.setAddress_id(jsonObject.optString("address_id"));
            freshNews.setAddress(jsonObject.optString("address"));
            freshNews.setName(jsonObject.optString("linkman"));
            freshNews.setPhone(jsonObject.optString("mobile"));
            freshNews.setRegion(jsonObject.optString("region"));
            freshNews.setBranch(jsonObject.optString("stroe"));
            freshNews.setRegion_id(jsonObject.optString("region_id"));
            datas.add(freshNews);
        }
        return datas;
    }
}
