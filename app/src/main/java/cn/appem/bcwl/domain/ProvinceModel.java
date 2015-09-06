package cn.appem.bcwl.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User:wewecn on 2015/7/6 14:35
 * Email:wewecn@qq.com
 */
public class ProvinceModel {
    private int id;
    private String text, state;

    public static class CountyModel {
        int id;

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

        String text;

        @Override
        public String toString() {
            return text;
        }
    }

    private List<CountyModel> mCounties;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CountyModel> getmCounties() {
        return mCounties;
    }

    public void setmCounties(List<CountyModel> mCounties) {
        this.mCounties = mCounties;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static final List<ProvinceModel> parse(JSONArray jsonArray) {
        List<ProvinceModel> data = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.optJSONObject(index);
            ProvinceModel model = new ProvinceModel();
            model.setId(jsonObject.optInt("id"));
            model.setState(jsonObject.optString("state"));
            model.setText(jsonObject.optString("text"));
            JSONArray county = jsonObject.optJSONArray("children");
            if (county != null && county.length() != 0) {
                List<CountyModel> countyModels = new ArrayList<>();
                for (int pos = 0; pos < county.length(); pos++) {
                    JSONObject object = county.optJSONObject(pos);
                    CountyModel item = new CountyModel();
                    item.setId(object.optInt("id"));
                    item.setText(object.optString("text"));
                    countyModels.add(item);
                }
                model.setmCounties(countyModels);
            }
            data.add(model);
        }
        return data;
    }
}
