package com.scorpio.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.scorpio.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RefreshLayout extends LinearLayout {

    private final static String TAG = "RefreshLayout";

    private List<View> mChildInXml = new ArrayList<>();

    private boolean mPullToRefreshEnable;
    private boolean mLoadMoreEnable;
    private int mHeaderLayoutId;
    private int mBottomLayoutId;

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;


    private View mHeaderView;
    private View mFooterView;
    private View mScrollableView;
    private int mScrollableViewInitTop;

    private int mStatus = STATUS_NORMAL;

    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_PULL_DOWN_REFRESH = 2;
    private static final int STATUS_READY_REFRESH = 3;
    private static final int STATUS_REFRESHING = 4;
    private static final int STATUS_SLIDE_UP_LOADMORE = 5;
    private static final int STATUS_READY_LOADMORE = 6;
    private static final int STATUS_LOADING = 7;

    private static final int MODE_REFRESH = 1;
    private static final int MODE_LOADMORE = 2;

    private int mMode = -1;

    private float mLastInterceptX;
    private float mLastInterceptY;
    private float mLastTouchX;
    private float mLastTouchY;

    private Scroller mScroller;

    private RefreshStatusCallback mRefreshStatusCallback;
    private LoadMoreStatusCallback mLoadMoreStatusCallback;


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        Log.e(TAG, "childcount = " + getChildCount());
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
        mPullToRefreshEnable = array.getBoolean(R.styleable.RefreshLayout_pullToRefreshEnable, true);
        mLoadMoreEnable = array.getBoolean(R.styleable.RefreshLayout_loadMoreEnable, true);
        mHeaderLayoutId = array.getResourceId(R.styleable.RefreshLayout_headerLayout, 0);
        mBottomLayoutId = array.getResourceId(R.styleable.RefreshLayout_bottomLayout, 0);
        if (mPullToRefreshEnable && mHeaderLayoutId > 0) {
            mHeaderView = LayoutInflater.from(context).inflate(mHeaderLayoutId, this, false);
        }

        if (mLoadMoreEnable && mBottomLayoutId > 0) {
            mFooterView = LayoutInflater.from(context).inflate(mBottomLayoutId, this, false);
        }
        array.recycle();

        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e(TAG, "onFinishInflate  childcount = " + getChildCount());
        saveAllChild();
        removeAllViews();
        if (mHeaderView != null) {
            addView(mHeaderView);
            LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
            measureChild(mHeaderView, mWidthMeasureSpec, mHeightMeasureSpec);
            int headerViewHeight = mHeaderView.getMeasuredHeight();
            params.topMargin = -headerViewHeight;
        }
        for (View view : mChildInXml) {
            addView(view);
        }

        if (mFooterView != null) {
            addView(mFooterView);
            measureChild(mFooterView, mWidthMeasureSpec, mHeightMeasureSpec);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
    }

    private void saveAllChild() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            setScrollableView(child);
            mChildInXml.add(child);
        }
    }

    private void setScrollableView(View view) {
        if (view instanceof RecyclerView || view instanceof ListView || view instanceof ScrollView) {
            mScrollableView = view;
            mScrollableView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mScrollableViewInitTop = mScrollableView.getTop();
                    mScrollableView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            return;
        } else if (view instanceof ViewGroup) {
            for (View v : getChildren((ViewGroup) view)) {
                setScrollableView(v);
            }
        }

    }

    private List<View> getChildren(ViewGroup viewGroup) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            views.add(viewGroup.getChildAt(i));
        }
        return views;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    //mScroller.abortAnimation();
                }
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getX() - mLastInterceptX;
                float deltaY = ev.getY() - mLastInterceptY;
                //如果向下滑并且当前mScrollableView的top比初始top值小，说明mScrollableView向上偏移了，此时不应该拦截，交给Parent处理
                if (deltaY > 0 && mScrollableView.getTop() < mScrollableViewInitTop)
                    break;
                if (mScrollableView instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) mScrollableView;
                    int itemCount = 0;
                    if (recyclerView.getAdapter() != null) {
                        itemCount = recyclerView.getAdapter().getItemCount();
                    }
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int firstPosition = -1;//第一个完全显示的item
                    int lastPosition = -1;//最后一个完全显示的item
                    if (layoutManager instanceof LinearLayoutManager) {
                        firstPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                        Log.e(TAG, "firstPosition = " + firstPosition);
                        lastPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        int pos[] = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        ((StaggeredGridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPositions(pos);
                        firstPosition = pos[0];

                        pos = null;
                        pos = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        ((StaggeredGridLayoutManager) layoutManager).findLastCompletelyVisibleItemPositions(pos);
                        lastPosition = findMax(pos);
                    } else {
                        throw new RuntimeException("Unsupported LayoutManager, can only be LinearLayoutManager, GridLayoutManager or StaggeredGridLayoutManager");
                    }

                    if (((firstPosition == 0 || itemCount <= 0) && deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX)) ||
                            (getScrollY() < 0 && Math.abs(deltaY) > Math.abs(deltaX))) {
                        //滑至最顶部并且向下滑，或者RefreshLayout的内容已经向下滑了一段距离
                        mMode = MODE_REFRESH;
                        Log.e(TAG, "onInterceptTouchEvent mode = refresh");
                        intercept = true;
                    } else if (deltaY < 0 && Math.abs(deltaY) > Math.abs(deltaX) && lastPosition == layoutManager.getItemCount() - 1
                            || getScrollY() > 0 && Math.abs(deltaY) > Math.abs(deltaX)) {
                        //滑至最底部并且向上滑，或者RefreshLayout的内容已经向上滑了一段距离
                        mMode = MODE_LOADMORE;
                        intercept = true;
                    } else {
                        intercept = false;
                    }
                } else if (mScrollableView instanceof ListView) {
                    ListView listView = (ListView) mScrollableView;
                    int firstPosition = listView.getFirstVisiblePosition();
                    int lastPosition = listView.getLastVisiblePosition();
                    if ((firstPosition <= 0 && deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX) && listView.getChildAt(0).getTop() >= 0) ||
                            (getScrollY() < 0 && Math.abs(deltaY) > Math.abs(deltaX))) {
                        mMode = MODE_REFRESH;
                        intercept = true;
                    } else if ((deltaY < 0 && Math.abs(deltaY) > Math.abs(deltaX) && lastPosition == listView.getCount() - 1 &&
                            listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight())
                            || (getScrollY() > 0 && Math.abs(deltaY) > Math.abs(deltaX))) {
                        mMode = MODE_LOADMORE;
                        intercept = true;
                    } else {
                        intercept = false;
                    }
                } else if (mScrollableView instanceof ScrollView) {
                    ScrollView scrollView = (ScrollView) mScrollableView;
                    View contentView = scrollView.getChildAt(0);
                    if (scrollView.getScrollY() <= 0 && deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX)) {
                        mMode = MODE_REFRESH;
                        intercept = true;
                    } else if (deltaY < 0 && Math.abs(deltaY) > Math.abs(deltaX)
                            && contentView.getMeasuredHeight() == scrollView.getScrollY() + scrollView.getHeight()) {
                        mMode = MODE_LOADMORE;
                        intercept = true;
                    } else {
                        intercept = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;

        }
        mLastInterceptX = ev.getX();
        mLastInterceptY = ev.getY();
        mLastTouchX = ev.getX();
        mLastTouchY = ev.getY();
        return intercept;
    }

    private int findMax(int[] pos) {
        if (pos.length <= 0)
            return -1;
        int max = pos[0];
        for (int a : pos) {
            if (a > max) {
                max = a;
            }
        }
        return max;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mStatus != STATUS_NORMAL || mStatus != STATUS_REFRESHING) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }
                Log.e(TAG, "onTouchEvent action down");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onTouchEvent action move");
                float deltaY = event.getY() - mLastTouchY;
                //下拉刷新模式
                if (mMode == MODE_REFRESH) {
                    if (mStatus != STATUS_REFRESHING) {
                        if (getScrollY() <= 0 && getScrollY() >= -mHeaderView.getHeight() * 5) {//如果RefreshLayout的内容已经向下滑动了一段距离，并且滑动的距离小于mHeaderView高度的5倍
                            //如何滑动才不会超过上边界，即滑动之后再次getScrollY()依然<=0;
                            if (deltaY < 0 && getScrollY() > deltaY) {//向上滑动,并且getScrollY()的距离小于手指滑动的距离
                                scrollBy(0, -getScrollY());
                            } else {//向下滑避免滑动后的getScrollY()的距离大于mHeaderView.getHeight() * 5
                                scrollBy(0, -(int) Math.min(deltaY, mHeaderView.getHeight() * 5 + getScrollY()) / 2);
                            }
                        }
                        if (getScrollY() > -mHeaderView.getHeight()) {//向下滑动的距离小于mHeaderView的高度
                            mStatus = STATUS_PULL_DOWN_REFRESH;
                            if (mRefreshStatusCallback != null) {
                                mRefreshStatusCallback.onStartPullDownRefresh(mHeaderView, getScrollY(), mHeaderView.getHeight(), deltaY);
                            }
                        }

                        if (getScrollY() <= -mHeaderView.getHeight()) {//向下滑动的距离大于等于mHeaderView的高度，可以准备刷新
                            mStatus = STATUS_READY_REFRESH;
                            if (mRefreshStatusCallback != null) {
                                mRefreshStatusCallback.onReadyToRefresh(mHeaderView);
                            }
                        }
                    }
                } else if (mMode == MODE_LOADMORE) {//加载更多模式
                    //如果已经向上滑动了一段距离，并且getScrollY()的距离小于mFooterView的高度，并且mScroller结束滑动，并且
                    if (getScrollY() >= 0 && getScrollY() < mFooterView.getHeight() && mScroller.isFinished()) {
                        if (deltaY > 0 && getScrollY() < deltaY) {//向下滑动
                            scrollBy(0, -getScrollY());
                        } else {
                            scrollBy(0, (int) -Math.max(deltaY, getScrollY() - mFooterView.getHeight()));
                        }
                    }
                    if (getScrollY() < mFooterView.getHeight()) {
                        mStatus = STATUS_SLIDE_UP_LOADMORE;
                        if (mLoadMoreStatusCallback != null) {
                            mLoadMoreStatusCallback.onStartLoadMore(mFooterView, getScrollY(), mFooterView.getHeight(), deltaY);
                        }
                    }

                    if (getScrollY() >= mFooterView.getHeight()) {
                        mStatus = STATUS_READY_LOADMORE;
                        if (mLoadMoreStatusCallback != null) {
                            mLoadMoreStatusCallback.onReadyToLoadMore(mFooterView);
                        }
                    }
                } else {

                }
                break;

            case MotionEvent.ACTION_UP:
                if (mStatus == STATUS_PULL_DOWN_REFRESH) {
                    mStatus = STATUS_NORMAL;
                    smoothScroll(-getScrollY());
                } else if (mStatus == STATUS_READY_REFRESH) {
                    mStatus = STATUS_REFRESHING;
                    smoothScroll(-getScrollY() - mHeaderView.getHeight());
                    if (mRefreshStatusCallback != null) {
                        mRefreshStatusCallback.onRefreshing(mHeaderView);
                    }
                } else if (mStatus == STATUS_SLIDE_UP_LOADMORE) {
                    mStatus = STATUS_NORMAL;
                    smoothScroll(-getScrollY());
                } else if (mStatus == STATUS_READY_LOADMORE) {
                    mStatus = STATUS_LOADING;
                    if (mLoadMoreStatusCallback != null) {
                        mLoadMoreStatusCallback.onLoading(mFooterView);
                    }
                }
                break;
            default:
                break;

        }

        mLastTouchX = event.getX();
        mLastTouchY = event.getY();
        return true;
    }

    private void smoothScroll(int dy) {
        mScroller.startScroll(0, getScrollY(), 0, dy, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void finishRefresh() {
        mStatus = STATUS_NORMAL;
        smoothScroll(-getScrollY());
        if (mRefreshStatusCallback != null) {
            mRefreshStatusCallback.onEndRefresh(mHeaderView);
        }
    }

    public void finishLoadMore() {
        mStatus = STATUS_NORMAL;
        smoothScroll(-getScrollY());
        if (mLoadMoreStatusCallback != null) {
            mLoadMoreStatusCallback.onEndLoading(mFooterView);
        }
    }

    public void finish() {
        if (mStatus == STATUS_REFRESHING) {
            finishRefresh();
        } else if (mStatus == STATUS_LOADING) {
            finishLoadMore();
        }
    }


    public void setRefreshStatusCallback(RefreshStatusCallback callback) {
        mRefreshStatusCallback = callback;
    }

    public void setLoadMoreStatusCallback(LoadMoreStatusCallback callback) {
        mLoadMoreStatusCallback = callback;
    }

    public interface RefreshStatusCallback {
        void onStartPullDownRefresh(View headerView, float scrollY, int headerHeight, float deltaY);

        void onReadyToRefresh(View headerView);

        void onRefreshing(View headerView);

        void onEndRefresh(View headerView);
    }

    public interface LoadMoreStatusCallback {
        void onStartLoadMore(View footerView, float scrollY, int footViewHeight, float deltaY);

        void onReadyToLoadMore(View footerView);

        void onLoading(View footerView);

        void onEndLoading(View footerView);
    }
}
