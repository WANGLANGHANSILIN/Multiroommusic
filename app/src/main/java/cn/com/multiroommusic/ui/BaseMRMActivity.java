package cn.com.multiroommusic.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.multiroommusic.R;

/**
 * Created by wang l on 2017/5/19.
 */

public abstract class BaseMRMActivity extends AppCompatActivity implements View.OnClickListener {
    protected ImageView mBackView,mRightView;
    protected TextView mTitleView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = View.inflate(this, initRootViewLayoutID(), null);
        setContentView(inflate);
        getTitleView();
        inflate.setBackgroundResource(R.mipmap.theme_bg);
    }

    private void getTitleView() {
        View inflate = findViewById(getTitleViewlayoutID());
        mBackView = (ImageView) inflate.findViewById(R.id.bt_left_icon);
        mRightView = (ImageView) inflate.findViewById(R.id.bt_right_icon);
        mTitleView = (TextView) inflate.findViewById(R.id.tv_cent_title);

        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(this);
        mBackView.setImageResource(R.drawable.button_back);

        mRightView.setOnClickListener(this);

        mTitleView.setText(getIntent().getStringExtra("activityName"));
    }

    public abstract int getTitleViewlayoutID();


    public abstract int initRootViewLayoutID();

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
