package cn.appem.bcwl.ui.fragemnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.chatuidemo.activity.ChatAllHistoryFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.ChatActivity;

/**
 * User:wewecn on 2015/4/30 16:08
 * Email:wewecn@qq.com
 */
public class MessageFragment extends ChatAllHistoryFragment {
    @InjectView(R.id.navbar_middle)
    TextView title;
    @InjectView(R.id.navbar_left)
    View mNavLeft;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container,false);
    }

    @Override
    public void goChat(String username) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", username);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        title.setText("消息");
        mNavLeft.setVisibility(View.GONE);
    }
}
