package cn.appem.bcwl.domain;

import com.wewe.android.domain.BaseBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User:wewecn on 2015/7/9 14:09
 * Email:wewecn@qq.com
 */
public class StationModel extends BaseBean {
    private String text,station_full_name;
    private int id;

    public String getStation_full_name() {
        return station_full_name;
    }

    public void setStation_full_name(String station_full_name) {
        this.station_full_name = station_full_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static final List<StationModel> parse(JSONArray jsonArray) {
        List<StationModel> data = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.optJSONObject(index);
            StationModel model = new StationModel();
            model.setId(jsonObject.optInt("id"));
            model.setText(jsonObject.optString("text"));
            model.setStation_full_name(jsonObject.optString("station_full_name"));
            data.add(model);
        }
        return data;
    }
}
