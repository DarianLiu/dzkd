package com.dzkandian.mvp.common.ui.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.widget.MobOneKeyShare;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.contract.WebViewContract;
import com.dzkandian.mvp.common.di.component.DaggerWebViewComponent;
import com.dzkandian.mvp.common.di.module.WebViewModule;
import com.dzkandian.mvp.common.presenter.WebViewPresenter;
import com.dzkandian.storage.bean.ShareBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.Subscriber;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 通用WebView
 */
public class WebViewActivity extends BaseActivity<WebViewPresenter> implements WebViewContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_progressbar)
    ProgressBar webProgressbar;
    @BindView(R.id.web_view)
    WebView webView;

    private String userId = "";
    private MobOneKeyShare oneKeyShare;
    private IWXAPI api; // IWXAPI 是第三方app和微信通信的openapi接口
    private String imageUserPath; // 分享本地图片

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private String mPushUpApp;         //推送打开APP进入新闻详情页

    private String publicName;   //微信公众号名称
    private boolean isUpdate;    //是否复制剪贴板
    private String shareUrl;     //活动分享URL
    private String shareTitle;     //活动分享标题
    private String mUrl;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerWebViewComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .webViewModule(new WebViewModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_web_view; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        tvToolbarTitle.setText(R.string.title_back);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        //初始化一键分享页面
        oneKeyShare = new MobOneKeyShare(this);
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);

        mUrl = getIntent().getStringExtra("URL");
        String title = getIntent().getStringExtra("title");
        mPushUpApp = getIntent().getStringExtra("mPushUpApp");
//        Timber.d("==================url  ：" + mUrl);
//        Timber.d("==================title  ：" + title);
//        Timber.d("==================mPushUpApp  ：" + mPushUpApp);

        queryUserInfo();
        if (TextUtils.isEmpty(userId)) {
            launchActivity(new Intent(this, LoginActivity.class));
        }
        init();
        if (!TextUtils.isEmpty(title)) {
            tvToolbarTitle.setText(title);
        }
        webView.loadUrl(mUrl);//加载需要显示的网页
        webView.setWebViewClient(new MWebViewClient());//设置Web视图
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        webView.setEnabled(true);
        webView.setVerticalScrollBarEnabled(false);//去掉垂直滚动条
        WebSettings webSettings = webView.getSettings();//设置WebView属性
        webSettings.setJavaScriptEnabled(true);//设置WebView属性，能够执行Javascript脚本
        webSettings.setAllowFileAccess(false);//设置可以访问文件
        webSettings.setBuiltInZoomControls(false); //设置支持缩放

        /*设置自适应屏幕，两者合用*/
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式：默认加载方式
        webSettings.setDomStorageEnabled(true); //启用或禁用DOM缓存。
        webSettings.setBlockNetworkImage(true);//先加载文字，再加载图片

        webView.addJavascriptInterface(new JavaScripObject(), "android");

        /* 添加网页进度条事件：*/
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    webSettings.setBlockNetworkImage(false);
                    if (webProgressbar != null) {
                        webProgressbar.setVisibility(View.GONE);//加载完网页进度条消失
                        webView.evaluateJavascript("$('.ad-width-btn').remove();$('.page-info > .element:first').remove();$('.detail-bottom-ad').remove();$('#recommend-wrap').remove();$('.copyright').remove();$('.detail-source').remove();$('.jump-index').remove();", null);
                    }
                }
//                else {
//                    if (webProgressbar != null) {
//                        webProgressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
//                        webProgressbar.setProgress(newProgress);//设置进度值
//                    }
//                }
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                onenFileChooseImpleForAndroid(filePathCallback);
                return true;
            }
        });



        /*WebView文件下载监听事件：*/
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    public ValueCallback<Uri> mUploadMessage;

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);
    }

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == 2) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    //Web视图    刚进入Web时调用
    private class MWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (webView != null && tvToolbarTitle != null && !TextUtils.isEmpty(webView.getTitle())) {
                if (webView.getTitle().startsWith("http")) {
                    tvToolbarTitle.setText("");
                } else {
                    tvToolbarTitle.setText(webView.getTitle());
                }
            }
        }
    }

    /**
     * 6、获取到关注公众号绑定关系
     */
    @Override
    public void binding(String data) {
//        Timber.d("=======================获取到关注公众号绑定关系" + data);
        webView.evaluateJavascript("window.binding(" + data + ");", null);
    }

    /**
     * 5、获取用户是否关注公众号接口
     */
    @Override
    public void isfoucs(String data) {
        try {
            webView.evaluateJavascript("window.isfoucs(" + data + ");", null);
            JSONObject jsonObject = new JSONObject(data);
            publicName = jsonObject.getString("wxAccountName");
//            Timber.d("=======================获取用户是否关注公众号接口" + data + "publicName ::" + publicName);
            if (isUpdate) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    cm.setPrimaryClip(ClipData.newPlainText(null, publicName));
                    showMessage("复制成功");
                    if (webView != null) {
                        webView.evaluateJavascript("window.skipWeChat(" + "跳转微信" + ");", null);
                        isUpdate = false;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 4、获取到返回前端数据
     */
    @Override
    public void redPacketList(String data) {
//        Timber.d("=======================邀请好友拆红包接口请求成功" + data);
        webView.evaluateJavascript("window.redPacketList(" + data + ");", null);
    }

    /**
     * 3、发消息给徒弟接口请求成功并把数据传给前端
     */
    @Override
    public void messageToApprentice(String messageBean) {
//        Timber.d("=======================请求提现徒弟提现或赚金币接口数据加载成功" + messageBean);
        webView.evaluateJavascript("window.getMessageToApprentice(" + messageBean + ");", null);
    }

    /**
     * 3、发消息给徒弟接口请求失败把错误码发给前端
     */
    @Override
    public void messageToError() {
        webView.evaluateJavascript("window.getMessageToApprentice(" + 404 + ");", null); //接口请求错误回调
    }


    /**
     * 2、获取拆红包数据
     */
    @Override
    public void openRedPacket(String redPacketBean) {
//        Timber.d("=====================请求拆金币接口成功" + redPacketBean);
        webView.evaluateJavascript("window.openRedPacket(" + redPacketBean + ");", null);
    }

    /**
     * 1、前端点击分享按钮拉起分享
     */
    @Override
    public void updateShareData(WeChatShareBean weChatShareBean) {
//        Timber.d("=====================请求分享数据成功 显示分享九宫格");
        //显示一键分享页(九宫格)

        String[] imgArray = new String[]{};
        imgArray = weChatShareBean.getInviteImages().toArray(imgArray);

        if (!TextUtils.isEmpty(shareUrl) && !TextUtils.isEmpty(shareTitle)) {
            oneKeyShare.setShareContent(new ShareBean.Builder()
                    .title(shareTitle)
                    .content(weChatShareBean.getInviteContent())
                    .imageUrl(weChatShareBean.getInviteImage())
                    .imagePath(imageUserPath)
                    .pageUrl(shareUrl)
                    .imgUrls(imgArray)
                    .weChatShareType(weChatShareBean.getSharingWechat())
                    .weChatMomentsShareType(weChatShareBean.getSharingWechatCircle())
                    .qqShareType(weChatShareBean.getSharingQQ())
                    .qZoneShareType(weChatShareBean.getSharingQqZone())
                    .sinaShareType(weChatShareBean.getSharingWebo())
                    .isUserHead(true)
                    .inviteCode(weChatShareBean.getInviteCode())
                    .create());
        } else {
            oneKeyShare.setShareContent(new ShareBean.Builder()
                    .title(weChatShareBean.getInviteTitle())
                    .content(weChatShareBean.getInviteContent())
                    .imageUrl(weChatShareBean.getInviteImage())
                    .imagePath(imageUserPath)
                    .pageUrl(weChatShareBean.getInviteURL())
                    .imgUrls(imgArray)
                    .weChatShareType(weChatShareBean.getSharingWechat())
                    .weChatMomentsShareType(weChatShareBean.getSharingWechatCircle())
                    .qqShareType(weChatShareBean.getSharingQQ())
                    .qZoneShareType(weChatShareBean.getSharingQqZone())
                    .sinaShareType(weChatShareBean.getSharingWebo())
                    .isUserHead(true)
                    .inviteCode(weChatShareBean.getInviteCode())
                    .create());
        }
        oneKeyShare.show(api);
    }

    @Override
    public void downloadCallBack(String filePath) {
        imageUserPath = filePath;
    }

    public class JavaScripObject {

        /**
         * 传给前端用户信息
         */
        @JavascriptInterface
        public String getAndroidData(int type) {
            switch (type) {
                case 1:
                    return TextUtils.isEmpty(userId) ? "" : userId;
                case 3:   //上传版本  如:2.2.2
//                    Timber.d("================获取JS数据：" + DeviceUtils.getVersionName(WebViewActivity.this));
                    return DeviceUtils.getVersionName(WebViewActivity.this);
                case 4:
                    // 判断是否安装了微信客户端
                    if (api != null && !api.isWXAppInstalled()) {
                        showMessage("您还未安装微信客户端");
                        return "false";
                    }
                    return "true";
                default:
                    return TextUtils.isEmpty(userId) ? "" : userId;
            }
        }

        @JavascriptInterface
        public void getJsData(int type, String json) throws JSONException {
            switch (type) {
                case 1:     //前端点击分享按钮 拉起分享
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        shareUrl = jsonObject.getString("shareUrl");
                        shareTitle = jsonObject.getString("shareTitle");

                        if (mPresenter != null)
                            mPresenter.inviteShare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:     //传给前端红包列表
//                    Timber.d("前端调用redPacketList()去请求传给前端红包列表接口");
                    if (mPresenter != null)
                        mPresenter.invitationRedPacket(TextUtils.isEmpty(userId) ? "" : userId); //邀请好友拆红包列表接口
                    break;
                case 3:     //获取前端数据去请求邀请徒弟接口
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String apprenticeId = jsonObject.getString("PupilId");
                        String userId = jsonObject.getString("userId");
                        String msgType = jsonObject.getString("msgType");
//                        Timber.d("前端调用getMessageToApprentice()去请求提现徒弟提现或赚金币接口");
                        if (mPresenter != null)
                            mPresenter.messageToApprentice(userId, apprenticeId, msgType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 4:     //获取前端请求   请求拆红包接口
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String userId = jsonObject.getString("userId");
                        String redPacketId = jsonObject.getString("redPacketId");
//                        Timber.d("前端调用disRedPacket()去请求拆红包接口");

                        if (mPresenter != null)
                            mPresenter.openRedPacket(userId, redPacketId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case 5: // 跳转到微信
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");

                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setComponent(cmp);
                        launchActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        showMessage("检查到您手机没有安装微信，请安装后使用该功能");
                    }
                    break;
                case 6: //复制公众号给前端
//                    Timber.d("=======================获取用户是否关注公众号接口" + "publicName ::" + publicName);
                    if (TextUtils.isEmpty(publicName)) {
                        if (mPresenter != null) {
                            isUpdate = true;
                            mPresenter.isFoucs(userId);
                        }
                    } else {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            cm.setPrimaryClip(ClipData.newPlainText(null, publicName));
                            showMessage("复制成功");
                            if (webView != null)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (webView != null)
                                            webView.evaluateJavascript("window.skipWeChat(" + "跳转微信" + ");", null);
                                    }
                                });

                        }
                    }

                    break;
                case 7: // 是否关注公众号给前端
                    if (mPresenter != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            String userId = jsonObject.getString("userId");
                            userId = TextUtils.isEmpty(userId) ? "" : userId;
                            mPresenter.isFoucs(userId);
                            if (mPresenter != null)
                                mPresenter.isFoucs(userId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 8: // 关注公众号绑定关系接口
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String code = jsonObject.getString("code");
                        String userId = jsonObject.getString("userId");
                        code = TextUtils.isEmpty(code) ? "" : code;
                        userId = TextUtils.isEmpty(userId) ? "" : userId;
                        if (mPresenter != null)
                            mPresenter.binding(userId, code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 9: //销毁当前界面
                    killMyself();
                    break;
                case 10: //直接拉起微信,分享好友 分享形式前端可配
                    setWeChatShareContent(json);

                    //分享小程序  传邀请code收徒
                    //分享体验版小程序测试
//                    JSONObject jsonObject = new JSONObject(json);
//                    String url = jsonObject.getString("url");  //这个字段用来区分小程序分享路径
//                    WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
//                    miniProgramObj.webpageUrl = "http://www.qq.com"; // 兼容低版本的网页链接
//                    miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;// 正式版:0，测试版:1，体验版:2
//                    miniProgramObj.userName = "gh_e8b3dcc04714";     // 小程序原始id
//                    miniProgramObj.path = url;            //小程序页面路径
//                    WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
//                    msg.title = "小程序消息Title";                    // 小程序消息title
//                    msg.description = "小程序消息Desc";               // 小程序消息desc
//                    Bitmap bitmap = BitmapFactory.decodeResource(WebViewActivity.this.getResources(), R.drawable.icon_error_empty_disciple_earning); // 间接调用
//                    msg.thumbData = Bitmap2Bytes(bitmap);                      // 小程序消息封面图片，小于128k
//
//                    SendMessageToWX.Req req = new SendMessageToWX.Req();
//                    req.transaction = buildTransaction("webpage");
//                    req.message = msg;
//                    req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
//                    api.sendReq(req);

                    break;
                case 11:  //直接拉起朋友圈,分享好友 分享形式前端可配
                    setWeChatMomentsShareContent(json);
                    break;
                case 12:  //拉起九宫格 全由前端可配
                    setShareShow(json);
                    break;

                default:
                    break;
            }
        }

    }


    /**
     * 10 直接拉起微信,分享好友 分享形式前端可配
     */
    public void setWeChatShareContent(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String weChatShareType = jsonObject.getString("weChatShareType");
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("content");
            String imageUrl = jsonObject.getString("imageUrl");
            String pageUrl = jsonObject.getString("pageUrl");
            String inviteCode = jsonObject.getString("inviteCode");

            String url = jsonObject.getString("url");  //这个字段用来区分小程序分享路径
            oneKeyShare.setWXApp(url);  //分享小程序 跳至指定页
//            Timber.d("====前端传的分享形式                            "+weChatShareType);
            oneKeyShare.setShareContent(new ShareBean.Builder()
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)
                    .imagePath(imageUserPath)
                    .pageUrl(pageUrl)
                    .inviteCode(inviteCode)
                    .weChatShareType(weChatShareType)
                    .create());

            Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
            oneKeyShare.shareSimple(weChat, api);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 11 直接拉起微信,分享朋友圈 分享形式前端可配
     */
    public void setWeChatMomentsShareContent(String json) {
        try {
            String[] imgUrls = new String[]{};
            JSONObject jsonObject = new JSONObject(json);
            String weChatMomentsShareType = jsonObject.getString("weChatMomentsShareType");
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("content");
            String imageUrl = jsonObject.getString("imageUrl");
            String pageUrl = jsonObject.getString("pageUrl");
            String imgUrl =  jsonObject.getString("imgUrls");
//            Timber.d("====前端传的分享形式                            "+weChatMomentsShareType);
//            Timber.d("====前端传的数组图片 imgUrl                            "+imgUrl);
            if (!TextUtils.isEmpty(imageUrl)){
                imgUrls = imgUrl.split(";");
//                for (int i = 0; i <imgUrls.length; i ++) {
//                    Timber.d("====前端传的数组图片 截取后 imgUrls           " + imgUrls[i]);
//                }
            }

            oneKeyShare.setShareContent(new ShareBean.Builder()
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)
                    .imagePath(imageUserPath)
                    .pageUrl(pageUrl)
                    .imgUrls(imgUrls)
                    .weChatMomentsShareType(weChatMomentsShareType)
                    .create());

            Platform weChat = ShareSDK.getPlatform(WechatMoments.NAME);
            oneKeyShare.shareSimple(weChat, api);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 12   拉起九宫格 全由前端可配
     */
    public void setShareShow(String json) {
        try {
            String[] imgUrls = new String[]{};
            JSONObject jsonObject = new JSONObject(json);
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("content");
            String imageUrl = jsonObject.getString("imageUrl");
            String pageUrl = jsonObject.getString("pageUrl");
            String imgUrl =  jsonObject.getString("imgUrls");
            String inviteCode = jsonObject.getString("inviteCode");
            String qqShareType = jsonObject.getString("qqShareType");
            String qZoneShareType = jsonObject.getString("qZoneShareType");
            String weChatShareType = jsonObject.getString("weChatShareType");
            String weChatMomentsShareType = jsonObject.getString("weChatMomentsShareType");
            String sinaShareType = jsonObject.getString("sinaShareType");

            if (!TextUtils.isEmpty(imageUrl)){
                imgUrls = imgUrl.split(";");
//                for (int i = 0; i <imgUrls.length; i ++) {
//                    Timber.d("====前端传的数组图片 截取后 imgUrls           " + imgUrls[i]);
//                }
            }

            oneKeyShare.setShareContent(new ShareBean.Builder()
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)
                    .imagePath(imageUserPath)
                    .pageUrl(pageUrl)
                    .imgUrls(imgUrls)
                    .weChatShareType(weChatShareType)
                    .weChatMomentsShareType(weChatMomentsShareType)
                    .qqShareType(qqShareType)
                    .qZoneShareType(qZoneShareType)
                    .sinaShareType(sinaShareType)
                    .isUserHead(true)
                    .inviteCode(inviteCode)
                    .create());
            oneKeyShare.show(api);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    protected void onPause() {
        try {
            webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        try {
            webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        //销毁WebView
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            //清理Webview缓存数据库
            try {
                deleteDatabase("webview.db");
                deleteDatabase("webviewCache.db");
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
        if (api != null) {
            api.detach();
        }
        oneKeyShare.destroy();
    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        loadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
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
        if (!TextUtils.isEmpty(mPushUpApp)) {
            Intent intent = new Intent(this, MainActivity.class);
            launchActivity(intent);
        }
        finish();
    }

    /**
     * 从登录界面返回
     *
     * @param isLogin
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin) {
            if (webView != null)
                webView.loadUrl(mUrl);//加载需要显示的网页
        }
    }

    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack(); // goBack()表示返回WebView的上一页面
                return true;
            } else {
                killMyself();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            Timber.d("=db=    WebViewActivity - UserInfo - query 成功");
            dbUpdateUserInfo(list.get(0));
        } else {
            Timber.d("=db=    WebViewActivity - UserInfo - query 失败");
        }
    }

    /**
     * @param userInfoBean 获取到数据库的用户信息后，更新界面
     */
    public void dbUpdateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            userId = String.valueOf(userInfoBean.getUserId());
            if (!TextUtils.isEmpty(userInfoBean.getAvatar())) {
                if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME) && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
//                    Timber.d("==shareMine  :" + "有登录，有图片：" + Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME);
                    imageUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
                } else {
//                    Timber.d("==shareMine  :" + "有登录，无图片");
                    assert mPresenter != null;
                    mPresenter.requestPermission(userInfoBean.getAvatar());
                }
            }
        }
    }
}
