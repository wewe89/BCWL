package cn.appem.bcwl.view;

import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.List;

import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.ProvinceModel;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import static com.wewe.android.util.LogUtils.LOGD;
import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * User:wewecn on 2015/7/30 15:43
 * Email:wewecn@qq.com
 */
public class SimpleDataDialog extends PopupWindow implements View.OnClickListener{
    private Button btn_cancel;
    private Button btn_sure;
    private OnChangedListener listener;
    private final Context mContext;
    private List<ProvinceModel> provinces;

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private int flag=0;

    public SimpleDataDialog(Context paramContext) {
        super(paramContext);
        this.mContext = paramContext;
        initView();
    }

    public void setData(List<ProvinceModel> paramArrayList) {
        this.provinces = paramArrayList;
        String[] val = list2arry(provinces);
        LOGD(TAG, val[0]);
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext, val);
        mCityView.setViewAdapter(adapter);
    }

    WheelView mCityView, mCountyView;

    void findView(View root) {
        btn_cancel = (Button) root.findViewById(R.id.btn_data_cancel);
        btn_sure = (Button) root.findViewById(R.id.btn_data_sure);
        mCityView = (WheelView) root.findViewById(R.id.city);
        mCountyView = (WheelView) root.findViewById(R.id.county);
    }

    protected void initView() {
        View localView = LayoutInflater.from(mContext).inflate(R.layout.dialog_date_picker, null);
        setContentView(localView);

        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.home_popupmenu_animation);
        setFocusable(true);
        setBackgroundDrawable(new PaintDrawable());
//        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.white));
        setOutsideTouchable(true);
        findView(localView);

        String[] val = new String[]{""};
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mContext, val);
        mCityView.setViewAdapter(adapter);
        mCityView.setVisibleItems(3);
        mCityView.setCurrentItem(1);
        mCityView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) updateCounty(newValue);
            }
        });

        mCityView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateCounty(wheel.getCurrentItem());
            }
        });

        mCountyView.setVisibleItems(5);
        updateCounty(-1);

        btn_cancel.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data_cancel:
                dismiss();
                break;
            case R.id.btn_data_sure:
                if (onChangedListener != null) {
                    onChangedListener.onChanged(flag,mCityView.getCurrentItem(), mCountyView.getCurrentItem());
                }
                dismiss();
                break;
        }
    }

    final static String TAG = makeLogTag(DataPickWheelDialog.class);

    void updateCounty(int index) {
        String[] v = null;
        if (index == -1) {
            v = new String[]{""};
        } else {
            List<ProvinceModel.CountyModel> data = provinces.get(index).getmCounties();
            v = list2arry(data);
            if (v == null) {
                v = new String[]{""};
            }
        }
        mCountyView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, v));
    }

    OnChangedListener onChangedListener;

    public void setOnChangedListener(OnChangedListener listener) {
        onChangedListener = listener;
    }

    boolean scrolling = false;

    String[] list2arry(List data) {
        if (data == null || data.size() == 0) return null;
        Object[] val = data.toArray();
        String v[] = new String[data.size()];
        for (int index = 0; index < v.length; index++) {
            v[index] = val[index].toString();
        }
        return v;
    }

    public static abstract interface OnChangedListener {
        public abstract void onChanged(int flag,int city, int county);
    }
}
