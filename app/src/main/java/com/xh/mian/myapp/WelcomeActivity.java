package com.xh.mian.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xh.mian.myapp.appui.login.LoginActivity;
import com.xh.mian.myapp.tools.db.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    private List<View> mViewList; //ViewPager 视图集合
    private ViewPager viewPager;
    private int mDistance;
    private ImageView mOne_dot;
    private ImageView mTwo_dot;
    private ImageView mThree_dot;
    private ImageView mLight_dots;
    private LinearLayout mIn_ll;
    private Button mBtn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if(SharedPreferences.getBoolean("welcome")){
            onNextClick(null);
            return;
        }

        initView();
        setOnClick(); //添加点击事件
    }
    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager = null;
        mOne_dot = null;
        mTwo_dot = null;
        mThree_dot = null;
        mLight_dots = null;
        mIn_ll = null;
        mBtn_next = null;
        if(null!=mViewList)mViewList.clear();
    }
    private void initView(){
        initViewPageList();
        viewPager = null;
        viewPager = findViewById(R.id.login_viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(mViewList));

        mBtn_next = findViewById(R.id.bt_next);
        addDots();
    }
    /**
     * 初始化ViewPage中的View视图
     */
    private void initViewPageList() {
        if(null!=mViewList)mViewList.clear();
        mViewList = new ArrayList<>();
        for(int i = 1;i<=3;i++){
            View v = new View(WelcomeActivity.this);
            v.setBackgroundResource(this.getResources().getIdentifier("mmain_main_bg_vp"+i, "drawable", getPackageName()));
            mViewList.add(v);
        }


    }
    /**
     * 添加圆点
     */
    private void addDots() {
        mLight_dots = findViewById(R.id.iv_light_dots);
        mOne_dot = new ImageView(this);
        mOne_dot.setImageResource(R.drawable.mmain_main_vp_light_dot);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 30, 0);

        mIn_ll = findViewById(R.id.in_ll);
        mIn_ll.removeAllViews();
        mIn_ll.addView(mOne_dot, layoutParams);
        mTwo_dot = new ImageView(this);
        mTwo_dot.setImageResource(R.drawable.mmain_main_vp_light_dot);
        mIn_ll.addView(mTwo_dot, layoutParams);
        mThree_dot = new ImageView(this);
        mThree_dot.setImageResource(R.drawable.mmain_main_vp_light_dot);
        mIn_ll.addView(mThree_dot, layoutParams);
    }
    private void setOnClick(){
        mOne_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });
        mTwo_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });
        mThree_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });
        mLight_dots.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获得两点之间距离
                mDistance = mIn_ll.getChildAt(1).getLeft() - mIn_ll.getChildAt(0).getLeft();
                mLight_dots.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //移动距离
                float leftMargin = mDistance * (position + positionOffset);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
                params.leftMargin = (int) leftMargin;
                mLight_dots.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    mBtn_next.setVisibility(View.VISIBLE);
                }
                if (position != 2 && mBtn_next.getVisibility() == View.VISIBLE) {
                    mBtn_next.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void onNextClick(View view) {
        //页面跳转
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
        finish();
    }

    /**
     * 进入APP后的ViewPagerAdapter
     * Author: kx
     * Date: 2018-07-18 10:44
     */
    public class ViewPagerAdapter extends PagerAdapter {

        private List<View> mViewList;

        public ViewPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }
}
