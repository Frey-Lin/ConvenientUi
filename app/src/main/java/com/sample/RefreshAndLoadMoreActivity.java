package com.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scorpio.ui.widget.RefreshLayout;

public class RefreshAndLoadMoreActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_and_load_more);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(new MyAdapter());
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setRefreshStatusCallback(new RefreshLayout.RefreshStatusCallback() {
            @Override
            public void onStartPullDownRefresh(View headerView, float scrollY, int headerHeight, float deltaY) {

            }

            @Override
            public void onReadyToRefresh(View headerView) {

            }

            @Override
            public void onRefreshing(View headerView) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finish();
                    }
                }, 1000);
            }

            @Override
            public void onEndRefresh(View headerView) {

            }
        });

        mRefreshLayout.setLoadMoreStatusCallback(new RefreshLayout.LoadMoreStatusCallback() {
            @Override
            public void onStartLoadMore(View footerView, float scrollY, int footViewHeight, float deltaY) {

            }

            @Override
            public void onReadyToLoadMore(View footerView) {

            }

            @Override
            public void onLoading(View footerView) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finish();
                    }
                }, 1000);
            }

            @Override
            public void onEndLoading(View footerView) {

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                Log.e("activity", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("activity", "onScrolled");
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View item = LayoutInflater.from(RefreshAndLoadMoreActivity.this).inflate(R.layout.item_recyclerview, viewGroup, false);
            return new MyHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            myHolder.content.setText("" + (i + 1));
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        TextView content;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }
    }
}
