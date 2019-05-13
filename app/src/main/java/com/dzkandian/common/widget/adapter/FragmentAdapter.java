package com.dzkandian.common.widget.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.dzkandian.mvp.news.ui.fragment.ErrorNetworkFragment;

import java.util.List;

/**
 * Created by yihao on 2017/9/28 002816:32
 * 说明：Viewpager的fragment适配器
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private List<Class> mFragments;
    private List<String> mTitles;
    private int mType;
    private String mTextSize;
//    private FragmentManager fragmentManager;

    public FragmentAdapter(FragmentManager fm, List<Class> fragments, List<String> stringList, int type, String textSize) {
        super(fm);
        this.mTitles = stringList;
        this.mFragments = fragments;
        this.mType = type;
        this.mTextSize = textSize;
//        this.fragmentManager = fm;
    }

//    public void setNewFragments(List<Class> fragments, List<String> titles) {
//        if (this.mFragments != null) {
//            fragments.clear();
//            fragments.addAll(fragments);
//        }
//
//        mTitles.clear();
//        mTitles.addAll(titles);
//        notifyDataSetChanged();
////        if (this.mFragments != null) {
////            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////            for (int i = 0; i < mFragments.size(); i++) {
////                fragmentTransaction.remove(mFragments.get(i));
////            }
////            fragmentTransaction.commit();
////            fragmentManager.executePendingTransactions();
////            mFragments.clear();
////        }
////        mTitles.clear();
////        this.mFragments.addAll(fragments);
////        this.mTitles.addAll(titles);
////        notifyDataSetChanged();
//    }

    @Nullable
    @Override
    public Fragment getItem(int position) {
        try {
            Fragment fragment = (Fragment) mFragments.get(position).newInstance(); //反射加载Fragment
            if (fragment instanceof ErrorNetworkFragment) {
                Bundle bundle = new Bundle();
                bundle.putInt("event", mType);
                bundle.putString("textSize", mTextSize);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("type", isWaterfall(position) ? mTitles.get(position).replace("#", "") : mTitles.get(position));
                bundle.putBoolean("listType", isWaterfall(position));
                bundle.putString("textSize", mTextSize);
                fragment.setArguments(bundle);
                return fragment;
            }
        } catch (@NonNull InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否为瀑布流布局（是否存在 # 特殊符号）
     *
     * @param position 当前位置
     */
    private boolean isWaterfall(int position) {
        return mTitles.get(position).contains("#");
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return isWaterfall(position) ? mTitles.get(position).replace("#", "") : mTitles.get(position);
    }

}