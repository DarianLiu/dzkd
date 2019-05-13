package com.dzkandian.mvp.news.ui.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.widget.recyclerview.GridSpacingItemDecoration;
import com.dzkandian.mvp.news.contract.ColumnManageContract;
import com.dzkandian.mvp.news.di.component.DaggerColumnManageComponent;
import com.dzkandian.mvp.news.di.module.ColumnManageModule;
import com.dzkandian.mvp.news.presenter.ColumnManagePresenter;
import com.dzkandian.mvp.news.ui.adapter.ColumnAdapter;
import com.dzkandian.mvp.news.ui.adapter.TouchMoveCallback;
import com.dzkandian.storage.ColumnBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ColumnManageActivity extends BaseActivity<ColumnManagePresenter> implements ColumnManageContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.tv_finish)
    TextView tvFinish;
    @Nullable
    @BindView(R.id.recyclerView2)
    RecyclerView recyclerView2;

    @Nullable
    private ColumnAdapter mAdapterDelete, mAdapterAdd;
    private String type;

    private boolean isChange;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerColumnManageComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .columnManageModule(new ColumnManageModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_column_manage; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_column_manage);
        toolbar.setNavigationOnClickListener(v -> columnDataComplete());

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);

        List<String> list = getIntent().getStringArrayListExtra("column");
        int addedSize = getIntent().getIntExtra("addedSize", 0);
        type = getIntent().getStringExtra("type");

        List<String> myColumnList = new ArrayList<>();
        List<String> canAddColumnList = new ArrayList<>();
        int totalSize = list.size();
        for (int i = 0; i < totalSize; i++) {
            String column = list.get(i);
            if (i < addedSize) {
                myColumnList.add(column);
            } else {
                canAddColumnList.add(column);
            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 40, true));
        mAdapterDelete = new ColumnAdapter(getApplicationContext(), 101,
                (column) -> {
                    if (mAdapterAdd != null) {
                        mAdapterAdd.addColumn(column);
                        isChange = true;
                    }

                }, type);
        recyclerView.setAdapter(mAdapterDelete);
        mAdapterDelete.setData(myColumnList);
        TouchMoveCallback callback = new TouchMoveCallback(mAdapterDelete);
        callback.setDragListener(new TouchMoveCallback.DragListener() {
            @Override
            public void dragState(boolean start) {
                if (!start) {
                    if (mAdapterDelete != null)
                        mAdapterDelete.setEnableDelete(true);
                    if (tvFinish != null)
                        tvFinish.setText("完成");
                    isChange = true;
                } else {
                    vibrate(ColumnManageActivity.this, 100);
//                    mAdapterDelete.setEnableDelete(true);
                }
            }

            @Override
            public void clearView() {

            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerView2.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.addItemDecoration(new GridSpacingItemDecoration(4, 40, true));
        mAdapterAdd = new ColumnAdapter(this, 102,
                (column) -> {
                    if (mAdapterDelete != null) {
                        if (mAdapterDelete != null)
                            mAdapterDelete.addColumn(column);
                        if (tvFinish != null)
                            tvFinish.setText("完成");
                        isChange = true;
                    }
                }, null);
        recyclerView2.setAdapter(mAdapterAdd);
        mAdapterAdd.setData(canAddColumnList);


        tvFinish.setOnClickListener(v -> {
            if (tvFinish != null && tvFinish.getText().toString().equals("编辑")) {
                isChange = true;
                tvFinish.setText("完成");
                if (mAdapterDelete != null)
                    mAdapterDelete.setEnableDelete(true);
            } else {
                columnDataComplete();
            }
        });

    }

    /**
     * 数据修改后返回数据  供3个地方调用(返回键/完成按钮/左上角返回键)
     */
    private void columnDataComplete() {
        mAdapterDelete.setEnableDelete(false);
        ArrayList<String> mLists = new ArrayList<>();//上面显示的数据
        ArrayList<String> totalList = new ArrayList<>();//所有的数据
        List<String> list1 = mAdapterDelete.getAddedColumnList();//获取上面显示的数据集合
        List<String> lists = mAdapterAdd.getColumnList();//获取下面的数据集合
        mLists.addAll(list1);

        totalList.addAll(list1);
        totalList.addAll(lists);
        Intent intent = new Intent();
        intent.putStringArrayListExtra("returnData", mLists);

        ColumnBean bean = new ColumnBean();
        bean.setAllColumn(totalList);
        bean.setViewSize(mLists.size());
        if (type.equals("news")) {
            DataHelper.saveDeviceData(getApplicationContext(), Constant.SP_KEY_NEWS_COLUMN, bean);
        } else if (type.equals("video")) {
            DataHelper.saveDeviceData(getApplicationContext(), Constant.SP_KEY_VIDEO_COLUMN, bean);
        }
        setResult(300, intent);
        if (isChange)
            showMessage("修改完成");
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            columnDataComplete();
        }
        return super.onKeyDown(keyCode, event);
    }

    //震动milliseconds毫秒
    public static void vibrate(@NonNull final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    //以pattern[]方式震动
    public static void vibrate(@NonNull final Activity activity, long[] pattern, int repeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.vibrate(pattern, repeat);
        }
    }

    //取消震动
    public static void virateCancle(@NonNull final Activity activity) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.cancel();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

}
