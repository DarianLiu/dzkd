package com.dzkandian.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.event.WeChatBindEvent;
import com.dzkandian.wxapi.contract.WXEntryContract;
import com.dzkandian.wxapi.di.component.DaggerWXEntryComponent;
import com.dzkandian.wxapi.di.module.WXEntryModule;
import com.dzkandian.wxapi.presenter.WXEntryPresenter;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class WXEntryActivity extends WXBaseActivity<WXEntryPresenter> implements WXEntryContract.View {

    private IWXAPI api; // IWXAPI 是第三方app和微信通信的openapi接口

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerWXEntryComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .wXEntryModule(new WXEntryModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return 0; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().registerSticky(this);
        //注册api
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (api!=null){
            api.detach();
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

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(@NonNull BaseResp baseResp) {

        int errorCode = baseResp.errCode;
        switch (errorCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case 1://微信授权
                        //用户同意
                        final String code = ((SendAuth.Resp) baseResp).code;
                        if (code != null) {
                            assert mPresenter != null;
                            if (((SendAuth.Resp) baseResp).state.startsWith(Constant.WX_LOGIN)) {
                                //微信登录
                                EventBus.getDefault().post(code, EventBusTags.WeChat_Login);
                            } else if (((SendAuth.Resp) baseResp).state.startsWith(Constant.WX_WARRANT)) {
                                //微信授权
                                EventBus.getDefault().post(code, EventBusTags.WeChat_Warrant);
                            } else if (((SendAuth.Resp) baseResp).state.startsWith(Constant.WX_BIND)) {
                                //微信绑定
                                String position = ((SendAuth.Resp) baseResp).state.substring(8, ((SendAuth.Resp) baseResp).state.indexOf("/"));
                                WeChatBindEvent event = new WeChatBindEvent.Builder()
                                        .code(code).position(Integer.parseInt(position)).build();
                                EventBus.getDefault().post(event, EventBusTags.WeChat_Bind);
                            }
                        }
                        break;
                    case 2:
                        SendMessageToWX.Resp resp = (SendMessageToWX.Resp) baseResp;
                        break;
                    default:
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝
//                showMessage("用户拒绝");
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
//                showMessage("用户取消");
                finish();
                break;
            default:
                break;
        }
    }
}
