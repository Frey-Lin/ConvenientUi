package com.sample;

import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            public void onReadyToRefresh() {

            }

            @Override
            public void onRefreshing() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                    }
                }, 1500);
            }

            @Override
            public void onEndRefresh() {
                Toast.makeText(RefreshAndLoadMoreActivity.this, "finish refreshing", Toast.LENGTH_SHORT).show();
            }
        });

        mRefreshLayout.setLoadMoreStatusCallback(new RefreshLayout.LoadMoreStatusCallback() {
            @Override
            public void onStartLoadMore(View footerView, float scrollY, int footViewHeight, float deltaY) {

            }

            @Override
            public void onReadyToLoadMore() {

            }

            @Override
            public void onLoading() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishLoadMore();
                    }
                }, 1500);
            }

            @Override
            public void onEndLoading() {
                Toast.makeText(RefreshAndLoadMoreActivity.this, "finish load more", Toast.LENGTH_SHORT).show();
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
