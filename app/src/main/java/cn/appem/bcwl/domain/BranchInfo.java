package cn.appem.bcwl.domain;

import com.baidu.mapapi.model.LatLng;
import com.wewe.android.domain.BaseBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.wewe.android.util.LogUtils.*;

/**
 * User:wewecn on 2015/6/24 14:34
 * Email:wewecn@qq.com
 */
public class BranchInfo extends BaseBean {
    private String station_name, address, mobile, station_full_name, city_name, county_name;
    private double lat, lng;
    private int countyId;

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countryId) {
        this.countyId = countryId;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }

    public String getStation_full_name() {
        return station_full_name;
    }

    public void setStation_full_name(String station_full_name) {
        this.station_full_name = station_full_name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public static ArrayList<BranchInfo> parse(JSONArray postsArray) {
        ArrayList<BranchInfo> datas = new ArrayList<>();
        for (int i = 0; i < postsArray.length(); i++) {
            BranchInfo freshNews = new BranchInfo();
            JSONObject jsonObject = postsArray.optJSONObject(i);

            freshNews.setAddress(jsonObject.optString("address"));
            freshNews.setStation_name(jsonObject.optString("station_name"));
            freshNews.setMobile(jsonObject.optString("mobile"));
            freshNews.setStation_full_name(jsonObject.optString("station_full_name"));
            freshNews.setCity_name(jsonObject.optString("city_name"));
            freshNews.setCounty_name(jsonObject.optString("county_name"));
            freshNews.setCountyId(jsonObject.optInt("county_id"));
            try {
                freshNews.setLat(Double.parseDouble(jsonObject.optString("lat")));
                freshNews.setLng(Double.parseDouble(jsonObject.optString("lng")));
            } catch (Exception ex) {

            }
            datas.add(freshNews);
        }
        return datas;
    }
}
