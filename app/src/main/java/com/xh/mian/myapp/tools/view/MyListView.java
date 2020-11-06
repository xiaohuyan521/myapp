package com.xh.mian.myapp.tools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.xh.mian.myapp.R;


/**
 * Created by hasee on 2019/3/4.
 * PatientList.setPullRefreshEnable(false);
 *         PatientList.setOkListener(new MyListView.OnViewListener() {
 *             @Override
 *             public void onRefresh() {
 *             }
 *             @Override
 *             public void onLoadMore() {//加载更多
 *                 new Handler().postDelayed(new Runnable() {
 *                     public void run() {
 *                         updateData();
 *                     }
 *                 }, 1000);
 *             }
 *         });
 */

public class MyListView extends ListView implements AbsListView.OnScrollListener {
    private TextView tv_head;//头部提示语
    private ProgressBar bar_head;//头部加载控件
    private ImageView arrow;//头部箭头
    private LinearLayout lv_head;//头部view
    private int mHeaderViewHeight; // 头部View的高
    //head箭头动画
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;


    private TextView tv_bottom;//底部提示语
    private ProgressBar bar_bottom;//底部加载控件
    public LinearLayout lv_bottom;//底部view
    private final static int PULL_LOAD_MORE_DELTA = 50; // 当大于50PX的时候，加载更多

    //加载状态
    private int mState;
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    // 总列表项，用于检测列表视图的底部
    public int mTotalItemCount;

    private float mLastY = -1; //
    private boolean mEnablePullRefresh = true;// 是否禁用头部
    public boolean mEnablePullLoad = true;    // 是否禁用底部
    private boolean mPullRefreshing = false;  // 头部是否刷新.
    private boolean mPullLoading = false;     // 底部是否刷新.


    //滚动页眉或者页脚
    private Scroller mScroller; // 用于回滚
    private int mScrollBack;// 回滚状态
    private final static int SCROLLBACK_HEADER = 0; // 顶部
    private final static int SCROLLBACK_FOOTER = 1; // 下部
    private final static int SCROLL_DURATION = 400; // 滚动回时间
    private final static float OFFSET_RADIO = 1.8f; //滚动速度

    private String head;
    private String footer;
    public MyListView(Context context) {
        super(context);
        //initView(context);
    }
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyListView);
        head = array.getString(R.styleable.MyListView_vhead);
        footer = array.getString(R.styleable.MyListView_vfooter);
        initView(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyListView);
        head = array.getString(R.styleable.MyListView_vhead);
        footer = array.getString(R.styleable.MyListView_vfooter);
        initView(context);
    }
    private void initView(Context context){
        //加载滚动
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        //加载箭头动画
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(180);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mRotateDownAnim.setDuration(180);
        mRotateDownAnim.setFillAfter(true);

        //添加到listview底部
        View vfoot = getBottomView(context);
        if("1".equals(footer)){
            addFooterView(vfoot);
        }

        //添加到listview头部
        View vheader = getHeadView(context);
        if("1".equals(head)){
            this.addHeaderView(vheader);
        }

        lv_head.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    mHeaderViewHeight = getHeightParams();
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    resetHeaderHeight(0);

                }
            });
        //mHeaderViewHeight = 67;

    }


    /**
     * 停止刷新, 重置头视图 外部加载完数据调用
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            setHeadState(STATE_NORMAL);
            resetHeaderHeight(SCROLL_DURATION);
        }
    }

    /**
     * 停止加载更多 外部加载完数据调用
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            setBottomState(STATE_NORMAL);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
    }


    //触摸监听
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (getHeightParams() > 0 || deltaY > 0)) {
                    // 第一项显示,标题显示或拉下来.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }else if (getLastVisiblePosition() == mTotalItemCount - 1 && (getBottomMargin() > 0 || deltaY < 0)) {
                    // 最后一页，已停止或者想拉起
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }

                break;
            default://switch语句里,所有的case语句都不满足条件时执行
                mLastY = -1; // 重置
                if (getFirstVisiblePosition() == 0) {
                    // 调用刷新
                    if (mEnablePullRefresh && getHeightParams() > mHeaderViewHeight) {
                        //如果头部视图高度大于设定高度。那么刷新
                        mPullRefreshing = true;
                        setHeadState(STATE_REFRESHING);
                        if (null!=okListener) {
                            okListener.onRefresh();
                        }
                    }
                    resetHeaderHeight(SCROLL_DURATION);// 刷新完毕，重置头部高度，也就是返回上不
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // 调用加载更多
                    if (mEnablePullLoad && getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        // 如果底部视图高度大于可以加载高度，那么就开始加载
                        mPullLoading = true;
                        setBottomState(STATE_REFRESHING);
                        if (okListener != null) {
                            okListener.onLoadMore();
                        }
                    }
                    resetFooterHeight();// 重置加载更多视图高度
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 更新头部高度
     */
    private void updateHeaderHeight(float delta) {
        int height = (int) delta + getHeightParams();
        setHeightParams(height);
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (getHeightParams() > mHeaderViewHeight) {
                setHeadState(STATE_READY);
            } else {
                setHeadState(STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }
    //重置头视图的高度
    private void resetHeaderHeight(int duratton) {
        int height = getHeightParams();
        if (height == 0) // 不显示.
            return;
        // 不显示刷新和标题的时候，什么都不显示
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // 默认：滚动回头.
        // 当滚动回显示所有头标题时候，刷新
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,duratton);
        // 触发刷新
        invalidate();
    }

    /**
     * 改变底部视图高度
     */
    private void updateFooterHeight(float delta) {
        int height = getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // 高度足以调用加载更多
                setBottomState(STATE_READY);
            } else {
                setBottomState(STATE_NORMAL);
            }
        }
        setBottomMargin(height);
    }
    //复原底部高度
    private void resetFooterHeight() {
        int bottomMargin = getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,SCROLL_DURATION);
            invalidate();
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                setHeightParams(mScroller.getCurrY());
            } else {
                setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }


    /**
     *  动态添加头部
     *  也可以lv_head=LinearLayout.inflate(context, R.layout.head, null);
     */
    private View getHeadView(Context context){
        LinearLayout lv= new LinearLayout(context);
        lv = new LinearLayout(context);
        lv.setGravity(Gravity.CENTER);
        lv.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams wrap = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        wrap.topMargin = 5;
        wrap.bottomMargin = 5;
        wrap.gravity = Gravity.CENTER;
        param.gravity = Gravity.CENTER;

        lv_head = new LinearLayout(context);
        lv_head.setOrientation(LinearLayout.HORIZONTAL);
        lv_head.setGravity(Gravity.CENTER);

        arrow = new ImageView(context);
        arrow.setImageResource(R.drawable.arrow);
        arrow.setLayoutParams(param);
        arrow.setVisibility(GONE);
        lv_head.addView(arrow);

        bar_head = new ProgressBar(context);
        bar_head.setVisibility(GONE);
        lv_head.addView(bar_head,wrap);

        tv_head = new TextView(context);
        tv_head.setText("下拉刷新");
        tv_head.setTextSize(context.getResources().getDimension(R.dimen.dp6));
        tv_head.setGravity(Gravity.CENTER);
        lv_head.addView(tv_head,wrap);

        lv.addView(lv_head,wrap);

        return lv;
    }
    public void setHeightParams(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv_head.getLayoutParams();
        lp.height = height;
        tv_head.setLayoutParams(lp);
    }

    public int getHeightParams() {
        return tv_head.getHeight();
    }
    //头部状态设置
    private void setHeadState(int state){
        lv_head.setVisibility(VISIBLE);
        if (state == STATE_REFRESHING) { // 显示进度
            arrow.clearAnimation();
            arrow.setVisibility(View.GONE);// 不显示图片
            bar_head.setVisibility(View.VISIBLE);// 显示进度条
        } else { // 显示箭头图片
            arrow.setVisibility(View.VISIBLE);
            bar_head.setVisibility(View.GONE);
        }
        switch (state) {
            case STATE_NORMAL:
                tv_head.setVisibility(View.VISIBLE);
                if (mState == STATE_READY) {// 当状态时准备的时候，显示动画
                    arrow.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {// 当状态显示进度条的时候，清除动画
                    arrow.clearAnimation();
                }
                tv_head.setText("下拉刷新");// 文字提示：下拉刷新
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    arrow.clearAnimation();
                    arrow.startAnimation(mRotateUpAnim);
                    tv_head.setText("松开刷新数据");// 松开刷新数据
                }
                break;
            case STATE_REFRESHING:
                tv_head.setText("加载中...");
                break;
            default:
        }

        mState = state;
    }


    /**
     * 动态添加底部
     */
    private View getBottomView(Context context){
        LinearLayout lv = new LinearLayout(context);
        lv.setGravity(Gravity.CENTER);
        lv.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams wraplp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wraplp.addRule(RelativeLayout.CENTER_VERTICAL);

        lv_bottom = new LinearLayout(context);
        lv_bottom.setOrientation(LinearLayout.HORIZONTAL);
        lv_bottom.setGravity(Gravity.CENTER);
        lv.addView(lv_bottom,wraplp);


        bar_bottom = new ProgressBar(context);
        bar_bottom.setVisibility(View.GONE);
        lv_bottom.addView(bar_bottom);

        tv_bottom = new TextView(context);
        tv_bottom.setText("上拉加载更多");
        tv_bottom.setTextSize(context.getResources().getDimension(R.dimen.dp6));
        lv_bottom.addView(tv_bottom,wraplp);
        return lv;
    }
    public void setBottomMargin(int height) {
        if (height < 0)
            return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lv_bottom
                .getLayoutParams();
        lp.bottomMargin = height;
        lv_bottom.setLayoutParams(lp);
    }
    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lv_bottom
                .getLayoutParams();
        return lp.bottomMargin;
    }
    public void setBottomState(int state) {
        //tv_bottom.setVisibility(View.GONE);// 开始底部控件都隐藏
        bar_bottom.setVisibility(View.GONE);
        if (state == STATE_READY) {// 如果是第一页状态，那么“查看更多”显示
            //tv_bottom.setVisibility(View.VISIBLE);
            tv_bottom.setText("松开显示更多");// 松开显示更多
        } else if (state == STATE_REFRESHING) {// 当加载的时候
            //tv_bottom.setVisibility(View.VISIBLE);
            bar_bottom.setVisibility(View.VISIBLE);//加载进度条显示
            tv_bottom.setText("正在加载...");// 松开显示更多
        } else {
            //tv_bottom.setVisibility(View.VISIBLE);
            tv_bottom.setText("上拉查看更多");// 查看更多
        }
    }


    //设置一个接口 提供外部数据 类似handle功能
    private OnViewListener okListener;
    public interface OnViewListener{
        void onRefresh();
        void onLoadMore();
    }
    public void setOkListener(OnViewListener okListener) {
        this.okListener = okListener;
    }

}