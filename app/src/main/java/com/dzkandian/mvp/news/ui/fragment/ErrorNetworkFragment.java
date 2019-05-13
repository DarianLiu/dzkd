package com.dzkandian.mvp.news.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dzkandian.R;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.storage.event.NetworkRetryEvent;

import org.simple.eventbus.EventBus;


/**
 * 无网状态重新加载布局
 * Created by Administrator on 2018/5/8 0008.
 */

public class ErrorNetworkFragment extends Fragment {

    private Button btnRetry;
    private int event;
    private long btnRetryLastTimes;//点击重新加载按钮的上一次时间；

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_error_network, container, false);
        view.setPadding(0, 0, 0, 50);
        btnRetry = view.findViewById(R.id.btn_retry);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        event = getArguments().getInt("event", 0);


        btnRetry.setOnClickListener(view1 -> {
            if (System.currentTimeMillis() - btnRetryLastTimes > 2000) {
                btnRetryLastTimes = System.currentTimeMillis();
                if (isInternet()) {
                    EventBus.getDefault().post(new NetworkRetryEvent.Builder().event(event).build());
                } else {
                    Toast.makeText(getContext(), "网络请求失败，请连网后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getContext().getApplicationContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("error_fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("error_fragment");
    }
}
