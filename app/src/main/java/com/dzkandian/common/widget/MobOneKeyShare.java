package com.dzkandian.common.widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.dzkandian.R;
import com.dzkandian.storage.bean.ShareBean;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import timber.log.Timber;

/**
 * ShareSDK 一键分享封装
 * Created by liuli on 2018/7/13.
 */
public class MobOneKeyShare {

    private OnekeyShare onekeyShare;

    private Context mContext;//上下文

    private Bitmap bitmapShortMessage;//短信
    private Bitmap bitmapCopy;//复制
    private Bitmap bitmapMore;//系统分享
    private Bitmap bitmapUser;//用户默认头像
    private Bitmap bitmapHead;//用户真实头像

    private ShareBean builder;//微信API
    private String wxPath; //分享小程序不同活动路径

    /**
     * 指定分享平台
     *
     * @param context   上下文
     * @param shareBean 分享数据
     */
    public MobOneKeyShare(Context context, ShareBean shareBean) {
        this.mContext = context;
        this.builder = shareBean;
        Resources resources = mContext.getApplicationContext().getResources();
        bitmapUser = BitmapFactory.decodeResource(resources, R.drawable.icon_mine_head);//用户默认头像
    }

    /**
     * 初始化一键分享页面
     *
     * @param context 上下文
     */
    public MobOneKeyShare(Context context) {
        this.mContext = context;

        Resources resources = mContext.getApplicationContext().getResources();
        Timber.d("=========MobOneKeyShare");
        bitmapShortMessage = BitmapFactory.decodeResource(resources, R.drawable.icon_share_shortmessage);//短信
        bitmapCopy = BitmapFactory.decodeResource(resources, R.drawable.icon_share_copy_link);//复制链接
        bitmapMore = BitmapFactory.decodeResource(resources, R.drawable.icon_share_more);//系统分享
        bitmapUser = BitmapFactory.decodeResource(resources, R.drawable.icon_mine_head);//用户默认头像

        onekeyShare = new OnekeyShare();
        onekeyShare.setTheme(OnekeyShareTheme.CLASSIC);
        onekeyShare.setCustomerLogo(bitmapShortMessage, "短信", v -> {
            Uri uri = Uri.parse("smsto:");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra("sms_body", builder.content + builder.pageUrl);
            intent.setType("vnd.android-dir/mms-sms");
            mContext.startActivity(intent);
        });

        onekeyShare.setCustomerLogo(bitmapCopy, "复制链接", v -> {
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            assert cm != null;
            cm.setPrimaryClip(ClipData.newPlainText(null, (!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                    + builder.pageUrl));
            Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
        });

        onekeyShare.setCustomerLogo(bitmapMore, "更多", v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, (!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                    + builder.pageUrl);
            mContext.startActivity(Intent.createChooser(intent, "分享到"));
        });

        onekeyShare.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
    }

    /**
     * 设置分享数据
     *
     * @param builder 分享所需基本信息
     */
    public void setShareContent(ShareBean builder) {
        this.builder = builder;
    }

    /**
     * 设置分享类型
     *
     * @param shareType 分享类型
     */
    public void setShareType(String shareType) {
        if (this.builder != null) {
            this.builder.qqShareType = shareType;
            this.builder.qZoneShareType = shareType;
            this.builder.weChatShareType = shareType;
            this.builder.weChatMomentsShareType = shareType;
            this.builder.sinaShareType = shareType;
        }

    }

    /**
     * 设置分享链接地址
     *
     * @param webUrl 分享链接地址
     */
    public void setShareWebUrl(String webUrl) {
        if (this.builder != null) {
            this.builder.pageUrl = webUrl;
        }
    }

    /**
     * 设置分享图片本地地址
     *
     * @param imagePath 图片本地地址
     */
    public void setShareImagePath(String imagePath) {
        if (this.builder != null) {
            this.builder.imagePath = imagePath;
        }
    }

    /**
     * 设置分享图片网络地址
     *
     * @param imageUrl 图片网络地址
     */
    public void setShareImageUrl(String imageUrl) {
        if (this.builder != null) {
            this.builder.imageUrl = imageUrl;
        }
    }

    /**
     * 获取分享数据
     */
    public ShareBean getShareContent() {
        return this.builder;
    }

    /**
     * 显示一键分享页(九宫格)
     */
    public void show(IWXAPI iwxapi) {
        //设置分渠道分享方式
        onekeyShare.setShareContentCustomizeCallback((Platform platform, Platform.ShareParams shareParams) -> {
            if (TextUtils.equals(platform.getName(), QQ.NAME)) {
                //QQ分享
                setQQShareContent(shareParams);
            } else if (TextUtils.equals(platform.getName(), QZone.NAME)) {
                //QQ空间分享
                setQZoneShareContent(shareParams);
            } else if (TextUtils.equals(platform.getName(), Wechat.NAME)) {
                //微信好友分享
                setWeChatShareContent(shareParams);
            } else if (TextUtils.equals(platform.getName(), WechatMoments.NAME)) {
                //微信朋友圈分享
                setWeChatMomentsShareContent(shareParams, iwxapi);
            } else if (TextUtils.equals(platform.getName(), SinaWeibo.NAME)) {
                //新浪微博分享
                setSinaWeiBoShareContent(shareParams);
            }
        });
        onekeyShare.show(mContext);
    }

    /**
     * 单独指定分享平台
     */
    public void shareSimple(Platform platform, IWXAPI iwxapi) {
        if (builder == null) {
            return;
        }

        Platform.ShareParams shareParams = new Platform.ShareParams();

        if (platform == null) {
            platform = ShareSDK.getPlatform(Wechat.NAME);
        }

        if (TextUtils.equals(platform.getName(), Wechat.NAME)) {
            setWeChatShareContent(shareParams);
        } else if (TextUtils.equals(platform.getName(), WechatMoments.NAME)) {
            setWeChatMomentsShareContent(shareParams, iwxapi);
        }

        platform.share(shareParams);
    }

    public void setWXApp(String wxPath) {
        this.wxPath = wxPath;
    }

    /**
     * 分享小程序
     */
    public void shareWXApp(Platform.ShareParams shareParams) {
        shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);
        shareParams.setWxUserName("gh_e8b3dcc04714");  //小程序原始ID
        if (!TextUtils.isEmpty(wxPath)) {  //如果这个值不为空  则由前端传进来小程序路径
            shareParams.setWxPath(wxPath);//分享小程序页面路径
        } else {
            shareParams.setWxPath("pages/new/new?inviteCode=" + builder.inviteCode);//分享小程序页面路径
        }
        shareParams.setTitle(!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？");
        shareParams.setText(!TextUtils.isEmpty(builder.content) ? builder.content : "");
        if (!TextUtils.isEmpty(builder.wxAppInviteImage)){
            shareParams.setImageUrl(builder.wxAppInviteImage);
        }else {
            shareParams.setImageUrl(builder.imageUrl);
        }
        shareParams.setUrl(builder.pageUrl);
    }

    /**
     * 设置QQ分享数据(无多图分享方式)
     */
    private void setQQShareContent(Platform.ShareParams shareParams) {
        switch (builder.qqShareType) {
            case "text"://纯文本分享（需绕过审核）
                HashMap<String, Object> optionMap = new HashMap<>();
                optionMap.put("Id", "1");
                optionMap.put("SortId", "1");
                optionMap.put("AppId", "1106900489");
                optionMap.put("AppKey", "k9MYCNxTU0Uoj0o8");
                optionMap.put("BypassApproval", true);//是否绕过审核
                optionMap.put("Enable", true);
                ShareSDK.setPlatformDevInfo(QQ.NAME, optionMap);
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + builder.pageUrl);
                break;
            case "image":
                shareParams.setShareType(Platform.SHARE_IMAGE);
                shareParams.setImageUrl(builder.imageUrl);
                break;
//            case "images":
//                break;
            case "web":
                setWebShareContent(shareParams, builder);
                break;
            default:
                shareParams.setShareType(Platform.SHARE_IMAGE);
                shareParams.setImageUrl(builder.imageUrl);
                break;
        }
    }

    /**
     * 设置QQ空间分享数据(多图分享方式需绕过审核)
     */
    private void setQZoneShareContent(Platform.ShareParams shareParams) {
        switch (builder.qZoneShareType) {
            case "text":
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + builder.pageUrl);
                break;
            case "image":
                shareParams.setShareType(Platform.SHARE_IMAGE);
                shareParams.setImageUrl(builder.imageUrl);
                break;
            case "images":
                HashMap<String, Object> optionMap = new HashMap<>();
                optionMap.put("Id", "2");
                optionMap.put("SortId", "2");
                optionMap.put("AppId", "1106900489");
                optionMap.put("AppKey", "k9MYCNxTU0Uoj0o8");
                optionMap.put("BypassApproval", true);//是否绕过审核
                optionMap.put("Enable", true);
                ShareSDK.setPlatformDevInfo(QZone.NAME, optionMap);
                shareParams.setShareType(Platform.SHARE_IMAGE);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + builder.pageUrl);
                shareParams.setImageArray(builder.imgUrls);
                shareParams.setSite("大众看点");
                shareParams.setSiteUrl("www.dzkandian.com");
                break;
            case "web":
                setWebShareContent(shareParams, builder);
                break;
            default:
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + builder.pageUrl);
                break;
        }
    }

    /**
     * 设置微信好友分享数据(无多图分享方式)
     */
    private void setWeChatShareContent(Platform.ShareParams shareParams) {
        switch (builder.weChatShareType) {
            case "text":
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + builder.pageUrl);
                break;
            case "image":
                shareParams.setShareType(Platform.SHARE_IMAGE);
                shareParams.setImageUrl(builder.imageUrl);
                break;
//            case "images":
//                break;
            case "web":
                setWebShareContent(shareParams, builder);
                break;
            case "wxApp":
                shareWXApp(shareParams);
                break;
            default:
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText(builder.content + builder.pageUrl);
                break;
        }
    }

    /**
     * 设置微信朋友圈分享数据
     */
    private void setWeChatMomentsShareContent(Platform.ShareParams shareParams, IWXAPI iwxapi) {
        switch (builder.weChatMomentsShareType) {
            case "text":
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + builder.pageUrl);
                break;
            case "image":
                shareParams.setShareType(Platform.SHARE_IMAGE);
                shareParams.setImageUrl(builder.imageUrl);
                break;
            case "images":
                   /*朋友圈多图分享*/
                HashMap<String, Object> optionMap = new HashMap<>();
                optionMap.put("Id", "4");
                optionMap.put("SortId", "4");
                optionMap.put("AppId", "wx56bca4b9873b6694");
                optionMap.put("AppSecret", "32fea59714c67636362d1a9eb0a1ba4d");
                optionMap.put("BypassApproval", true);
                optionMap.put("Enable", true);
                ShareSDK.setPlatformDevInfo(WechatMoments.NAME, optionMap);
                shareParams.setTitle(!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？");
                shareParams.setText(!TextUtils.isEmpty(builder.content) ? builder.content : "大众看点，快乐分享，了解一下？");
                shareParams.setImageArray(builder.imgUrls);
                shareParams.setShareType(Platform.SHARE_IMAGE);
                break;
            case "web": /*网页链接*/
//                Timber.d("==MobOneKeyShare imageUrl:" + builder.imageUrl);
//                if (!TextUtils.isEmpty(builder.imagePath) && new File(builder.imagePath).exists()) {
//                    File file = new File(builder.imagePath);
//                    Timber.d("==shareThing 文件大小:" + file.length());
//                    Timber.d("==shareThing 文件比例:" + file.length() / 32000 + 1);
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = (int) (file.length() / 32000 + 1);
//                    bitmapHead = BitmapFactory.decodeFile(builder.imagePath, options);//用户头像
////                    if (bitmapHead != null) {
////                        Timber.d("==shareThing bitmapHead像素大小:" + bitmapHead.getByteCount());
////                    }
//                }
//
//                /*调用微信开放平台方式分享*/
//                //初始化一个WXWebpageObject对象，填写url;
//                WXWebpageObject webPageObject = new WXWebpageObject();
//                webPageObject.webpageUrl = builder.pageUrl;
//
//                //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题，描述；
//                WXMediaMessage wxMediaMessage = new WXMediaMessage(webPageObject);
//                wxMediaMessage.title = !TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？";
//                wxMediaMessage.description = !TextUtils.isEmpty(builder.content) ? builder.content : "大众看点，快乐分享，了解一下？";
//                if (bitmapHead != null) {
//                    wxMediaMessage.setThumbImage(bitmapHead);
//                } else {
//                    Timber.d("==shareThing 朋友圈:使用默认图片");
//                    wxMediaMessage.setThumbImage(bitmapUser);
//                }
//
//                //构造一个Req;
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = "web_page_share";
//                req.message = wxMediaMessage;
//                req.scene = SendMessageToWX.Req.WXSceneTimeline;
//                iwxapi.sendReq(req);

//                  /*分享标题链接：微信分享可以没有*/
//                shareParams.setTitleUrl(!TextUtils.isEmpty(builder.pageUrl) ?
//                        builder.pageUrl : "https://m.o6od.cn/i?1000013");
//                /*分享链接 必须项*/
//                shareParams.setUrl(!TextUtils.isEmpty(builder.pageUrl) ?
//                        builder.pageUrl : "https://m.o6od.cn/i?1000013");/*微信分享必须要有的参数*/
//                /*分享标题 必须项*/
//                shareParams.setTitle(!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点");
//                /*分享文本: 微信分享可以没有*/
//                shareParams.setText(!TextUtils.isEmpty(builder.content) ? builder.content :
//                        "大众看点，快乐分享，了解一下？");
//                shareParams.setImageUrl(builder.imageUrl);
//                shareParams.setShareType(Platform.SHARE_WEBPAGE);

                setWebShareContent(shareParams, builder);
                break;
            default:
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText(builder.content + builder.pageUrl);
                break;
        }
    }

    /**
     * 设置新浪微博分享数据(无多图分享方式)
     */
    private void setSinaWeiBoShareContent(Platform.ShareParams shareParams) {
        switch (builder.sinaShareType) {
            case "text":
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title :
                        "大众看点，快乐分享，了解一下？") + (!TextUtils.isEmpty(builder.pageUrl) ?
                        builder.pageUrl : "https://m.o6od.cn/i?1000013"));
                break;
            case "image":
                shareParams.setImageUrl(builder.imageUrl);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title :
                        "大众看点，快乐分享，了解一下？") + (!TextUtils.isEmpty(builder.pageUrl) ?
                        builder.pageUrl : "https://m.o6od.cn/i?1000013"));
                shareParams.setShareType(Platform.SHARE_IMAGE);
                break;
            case "images":
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + (!TextUtils.isEmpty(builder.pageUrl) ? builder.pageUrl : "https://m.o6od.cn/i?1000013"));
                shareParams.setImageArray(builder.imgUrls);
                shareParams.setShareType(Platform.SHARE_IMAGE);
                break;
            case "web":
                setWebShareContent(shareParams, builder);
                break;
            default:
                shareParams.setShareType(Platform.SHARE_TEXT);
                shareParams.setText((!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点，快乐分享，了解一下？")
                        + (!TextUtils.isEmpty(builder.pageUrl) ? builder.pageUrl : "https://m.o6od.cn/i?1000013"));
                break;
        }
    }

    /**
     * 设置网页链接方式分享内容
     */
    private void setWebShareContent(Platform.ShareParams shareParams, ShareBean builder) {
        if (builder.isUserHead) {
            if (!TextUtils.isEmpty(builder.imagePath) && new File(builder.imagePath).exists()) {
                shareParams.setImagePath(builder.imagePath);/*微信分享 图片没有的话就显示APP图标*/
            } else {
//                    Timber.d("==shareThing 微信:使用默认图片");
                shareParams.setImageData(bitmapUser);
            }
        } else {
            shareParams.setImageUrl(builder.imageUrl);
        }

        /*分享标题链接：微信分享可以没有*/
        shareParams.setTitleUrl(!TextUtils.isEmpty(builder.pageUrl) ?
                builder.pageUrl : "https://m.o6od.cn/i?1000013");
                /*分享链接 必须项*/
        shareParams.setUrl(!TextUtils.isEmpty(builder.pageUrl) ?
                builder.pageUrl : "https://m.o6od.cn/i?1000013");/*微信分享必须要有的参数*/
                /*分享标题 必须项*/
        shareParams.setTitle(!TextUtils.isEmpty(builder.title) ? builder.title : "大众看点");
                /*分享文本: 微信分享可以没有*/
        shareParams.setText(!TextUtils.isEmpty(builder.content) ? builder.content :
                "大众看点，快乐分享，了解一下？");
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
    }

    /**
     * 回收资源
     */
    public void destroy() {
        if (bitmapCopy != null && !bitmapCopy.isRecycled())
            bitmapCopy.recycle();

        if (bitmapMore != null && !bitmapMore.isRecycled())
            bitmapMore.recycle();

        if (bitmapShortMessage != null && !bitmapShortMessage.isRecycled())
            bitmapShortMessage.recycle();

        if (bitmapUser != null && !bitmapUser.isRecycled())
            bitmapUser.recycle();

        if (bitmapHead != null && !bitmapHead.isRecycled())
            bitmapHead.recycle();

        if (mContext != null)
            mContext = null;

        if (onekeyShare != null)
            onekeyShare = null;

        if (builder != null)
            builder = null;

    }

}
