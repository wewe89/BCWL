package cn.appem.bcwl.domain;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * User:wewecn on 2015/6/29 11:14
 * Email:wewecn@qq.com
 */
public class WaybillState implements Serializable {
    private String state,waybill_id,consignor,consignee,station,cargo;

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsignor() {
        return consignor;
    }

    public void setConsignor(String consignor) {
        this.consignor = consignor;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getWaybill_id() {
        return waybill_id;
    }

    public void setWaybill_id(String waybill_id) {
        this.waybill_id = waybill_id;
    }

    public static WaybillState parse(JSONObject jsonStr){
        JSONObject item=null;
        Log.d("WaybillState", "parse " + jsonStr);
        try {
            item=jsonStr.optJSONArray("rows").optJSONObject(0);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        if(item==null){
            return null;
        }
        WaybillState state=new WaybillState();
        state.setWaybill_id(item.optString("waybill_id"));
        state.setState(item.optString("state"));
        state.setConsignor(item.optString("consignor"));
        state.setConsignee(item.optString("consignee"));
        state.setStation(item.optString("station"));
        state.setCargo(item.optString("cargo"));
        return state;
    }
}
