package com.dzkandian.common.uitls.update;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.update.listener.ExceptionHandler;
import com.dzkandian.common.uitls.update.listener.ExceptionHandlerHelper;
import com.dzkandian.common.uitls.update.utils.AppUpdateUtils;
import com.dzkandian.common.uitls.update.utils.ColorUtil;
import com.dzkandian.common.uitls.update.view.NumberProgressBar;

import org.simple.eventbus.EventBus;

import java.io.File;

/**
 * App更新Dialog 2018/08/23
 */
public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TIPS = "更新失败，请到设置中允许存储权限后再尝试更新";
    public static boolean isShow = false;
    private TextView mContentTextView;
    private Button mUpdateOkButton;
    private UpdateAppBean mUpdateApp;
    private NumberProgressBar mNumberProgressBar;
    private ImageView mIvClose;
    private TextView mTitleTextView;
    private LinearLayout mLlClose;
    //默认色
    private int mDefaultColor = 0xFFC70000;
    private int mDefaultPicResId = R.drawable.update_app_top_bg;
    private ImageView mTopIv;
    private TextView mIgnore;
    private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;
    private DownloadService.DownloadBinder mDownloadBinder;
    private long setClickTimes;//点击更新按钮的上一次时间；

    /**
     * 回调
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            startDownloadApp((DownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public UpdateDialogFragment setUpdateDialogFragmentListener(IUpdateDialogFragmentListener updateDialogFragmentListener) {
        this.mUpdateDialogFragmentListener = updateDialogFragmentListener;
        return this;
    }

    public static UpdateDialogFragment newInstance(Bundle args) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.UpdateAppDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false); //点击window外的区域 是否消失
//        //window外可以点击,不拦截窗口外的事件
//        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //禁用
                if (mUpdateApp != null && mUpdateApp.isConstraint()) {
                    //返回桌面
                    startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        });

        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.9f);
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        //提示内容
        mContentTextView = view.findViewById(R.id.tv_update_info);
        mContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        //标题
        mTitleTextView = view.findViewById(R.id.tv_title);
        //更新按钮
        mUpdateOkButton = view.findViewById(R.id.btn_ok);
        //进度条
        mNumberProgressBar = view.findViewById(R.id.npb);
        //关闭按钮
        mIvClose = view.findViewById(R.id.iv_close);
        //关闭按钮+线 的整个布局
        mLlClose = view.findViewById(R.id.ll_close);
        //顶部图片
        mTopIv = view.findViewById(R.id.iv_top);
        //忽略
        mIgnore = view.findViewById(R.id.tv_ignore);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mUpdateApp = (UpdateAppBean) getArguments().getSerializable("UpdateAppBean");
        //设置主题色
        setDialogTheme(mDefaultColor, mDefaultPicResId);

        if (mUpdateApp != null) {
            String dialogTitle = mUpdateApp.getUpdateDefDialogTitle();
            String newVersion = mUpdateApp.getNewVersion();
            String updateLog = mUpdateApp.getUpdateLog();
            //更新内容
            mContentTextView.setText(updateLog);
            //标题
            mTitleTextView.setText(TextUtils.isEmpty(dialogTitle) ? String.format("发现新版本") : dialogTitle);
            //强制更新
            if (mUpdateApp.isConstraint()) {
                mLlClose.setVisibility(View.GONE);
            } else {
                //不是强制更新时，才生效
                if (mUpdateApp.isShowIgnoreVersion()) {
                    mIgnore.setVisibility(View.VISIBLE);
                }
            }

            //设置点击事件
            initEvents();
        }
    }

    /**
     * 设置
     *
     * @param color    主色
     * @param topResId 图片
     */
    private void setDialogTheme(int color, int topResId) {
        mTopIv.setImageResource(topResId);
//        mUpdateOkButton.setBackgroundDrawable(DrawableUtil.getDrawable(AppUpdateUtils.dip2px(4, getActivity()), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        //随背景颜色变化
        mUpdateOkButton.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
    }

    /**
     * 设置点击事件
     */
    private void initEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mIgnore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {
            if (System.currentTimeMillis() - setClickTimes > 2000) {
                setClickTimes = System.currentTimeMillis();
                if (isInternet()) {
                    //权限判断是否有访问外部存储空间权限
                    int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (flag != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                            Toast.makeText(getContext(), TIPS, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null));
                            startActivity(intent);
                        } else {
                            // 申请授权。
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    } else {
                        installApp();
                    }
                } else {
                    Toast.makeText(getContext(), "请连接网络后更新版本", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (i == R.id.iv_close) {
            //@WVector 这里是否要对UpdateAppBean的强制更新做处理？不会重合，当强制更新时，就不会显示这个按钮，也不会调这个方法。
            cancelDownloadService();
            if (mUpdateDialogFragmentListener != null) {
                // 通知用户
                mUpdateDialogFragmentListener.onUpdateNotifyDialogCancel(mUpdateApp);
            }
            dismiss();
        } else if (i == R.id.tv_ignore) {
            AppUpdateUtils.saveIgnoreVersion(getActivity(), mUpdateApp.getNewVersion());
            dismiss();
        }
    }

    public void cancelDownloadService() {
        if (mDownloadBinder != null) {
            // 标识用户已经点击了更新，之后点击取消
            mDownloadBinder.stop("取消下载");
            getContext().unbindService(conn);
        }
    }

    private void installApp() {
        if (AppUpdateUtils.appIsDownloaded(mUpdateApp)) {
            AppUpdateUtils.installApp(UpdateDialogFragment.this, AppUpdateUtils.getAppFile(mUpdateApp));
            //安装完自杀
            //如果上次是强制更新，但是用户在下载完，强制杀掉后台，重新启动app后，则会走到这一步，所以要进行强制更新的判断。
            if (!mUpdateApp.isConstraint()) {
                dismiss();
            } else {
                showInstallBtn(AppUpdateUtils.getAppFile(mUpdateApp));
            }
        } else {
            downloadApp();
            if (isInternet() && !isWifi()) {
                Toast.makeText(getContext(), "您正在用移动数据下载更新", Toast.LENGTH_SHORT).show();
            }
            //这里的隐藏对话框会和强制更新冲突，导致强制更新失效，所以当强制更新时，不隐藏对话框。
            if (mUpdateApp.isHideDialog() && !mUpdateApp.isConstraint()) {
                dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //升级
                installApp();
            } else {
                //提示，并且关闭
                Toast.makeText(getContext(), TIPS, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null));
                startActivity(intent);
//                dismiss();
            }
        }
    }

    /**
     * 开启后台服务下载
     */
    private void downloadApp() {
        Intent intent = new Intent(getContext(), DownloadService.class);
        getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        DownloadService.bindService();
    }

    /**
     * 回调监听下载
     */
    private void startDownloadApp(DownloadService.DownloadBinder binder) {
        // 开始下载，监听下载进度，可以用对话框显示
        if (mUpdateApp != null) {
            this.mDownloadBinder = binder;
            binder.start(mUpdateApp, new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        mNumberProgressBar.setVisibility(View.VISIBLE);
                        mUpdateOkButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        mNumberProgressBar.setProgress(Math.round(progress * 100));
                        mNumberProgressBar.setMax(100);
                    }
                }

                @Override
                public void setMax(long total) {

                }

                //TODO 这里的 onFinish 和 onInstallAppAndAppOnForeground 会有功能上的重合，后期考虑合并优化。
                @Override
                public boolean onFinish(final File file) {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        if (mUpdateApp.isConstraint()) {
                            showInstallBtn(file);
                        } else {
                            dismissAllowingStateLoss();
                        }
                    }
                    return true;
                }

                @Override
                public void onError(String msg) {
                    if (!UpdateDialogFragment.this.isRemoving()) {
                        EventBus.getDefault().post(true, EventBusTags.TAG_UPDATE_DIALOG);
                        dismissAllowingStateLoss();
                    }
                }

                @Override
                public boolean onInstallAppAndAppOnForeground(File file) {
                    if (!mUpdateApp.isConstraint()) {
                        dismiss();
                    }
                    if (getActivity().getApplicationContext() != null) {
                        AppUpdateUtils.installApp(getActivity().getApplicationContext(), file);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    private void showInstallBtn(final File file) {
        mNumberProgressBar.setVisibility(View.GONE);
        mUpdateOkButton.setText("安装");
        mUpdateOkButton.setVisibility(View.VISIBLE);
        mUpdateOkButton.setOnClickListener(v -> AppUpdateUtils.installApp(UpdateDialogFragment.this, file));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (manager.isDestroyed()) {
                return;
            }
        }

        try {
            super.show(manager, tag);
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
    }

    @Override
    public void onDestroyView() {
        isShow = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getContext().getApplicationContext());
    }

    /*判断是否为WIFI网络*/
    private boolean isWifi() {
        return NetworkUtils.checkIsWIFI(getContext().getApplicationContext());
    }
}