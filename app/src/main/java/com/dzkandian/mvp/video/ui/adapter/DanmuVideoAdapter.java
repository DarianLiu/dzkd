package com.dzkandian.mvp.video.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.barrageview.XAdapter;
import com.dzkandian.storage.bean.news.NewsDanmuBean;
import com.dzkandian.storage.event.DanmuEvent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;

import java.util.Random;


/**
 * Created by Administrator on 2017/4/17.
 */

public class DanmuVideoAdapter extends XAdapter<NewsDanmuBean> {

    final int ICON_RESOURCES[] = {R.drawable.danmu_item_backgrount, R.drawable.danmu_item_backgrount1,
            R.drawable.danmu_item_backgrount2, R.drawable.danmu_item_backgrount3, R.drawable.danmu_item_backgrount4, R.drawable.danmu_item_backgrount5};

    final int HEAD_RESOURCES[] = {R.drawable.danmu_item_head_backgrount, R.drawable.danmu_item_head_backgrount1,
            R.drawable.danmu_item_head_backgrount2, R.drawable.danmu_item_head_backgrount3, R.drawable.danmu_item_head_backgrount4, R.drawable.danmu_item_head_backgrount5};
    Random random;


    private ViewHolder2 holder2;
    private NewsDanmuBean mDanmuEntity;
    private Context context;
    private boolean isAllClick = true;
    private long mCurrClickTime = 0; //当前点击时间

    public DanmuVideoAdapter(Context c) {
        super();
        context = c;
        random = new Random();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(NewsDanmuBean danmuEntity, View convertView) {
        ViewHolder2 holder2 = null;
        if (convertView == null) {
            //点赞效果的item
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news_danmu, null);
            holder2 = new ViewHolder2();
            holder2.content = (TextView) convertView.findViewById(R.id.tv_content);
            holder2.image = (ImageView) convertView.findViewById(R.id.iv_danmu);
            holder2.ivFabulous = (ImageView) convertView.findViewById(R.id.iv_fabulous);
            holder2.tvFabulous = (TextView) convertView.findViewById(R.id.tv_fabulous);
            holder2.ll_danmu = (RelativeLayout) convertView.findViewById(R.id.ll_danmu);


            ViewHolder2 finalHolder = holder2;

            holder2.ll_danmu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int[] location = new int[2];
                    finalHolder.image.getLocationInWindow(location);
                    int viewX = location[0] + (finalHolder.ll_danmu.getWidth() / 2);
                    int viewY = location[1] - (finalHolder.ll_danmu.getHeight()) / 2;

                    EventBus.getDefault().post(new DanmuEvent
                            .Builder()
                            .viewX(viewX)                            //点击屏幕的X坐标
                            .viewY(viewY)                            //点击屏幕的Y坐标
                            .viewPosition(danmuEntity.getPosition()) //当前点击的下标
                            .build(), EventBusTags.TAG_VIDEO_ANIMATION_THUBMS_UP);

                    if (!NetworkUtils.checkNetwork(context.getApplicationContext())) { // 如果没有网
                        if (System.currentTimeMillis() - mCurrClickTime > 3000) {
                            mCurrClickTime = System.currentTimeMillis();
                            ArmsUtils.makeText(context, "网络请求失败，请连网后重试");
                        }
                        return;
                    }


                    if (!TextUtils.isEmpty(danmuEntity.getThumbsUpCount()) && danmuEntity.getCanThumbsUp()
                            && !TextUtils.isEmpty(danmuEntity.getPosition()) && isAllClick) { //是否可以点赞 1：可以；0：不可以
                        DanmuVideoAdapter.this.mDanmuEntity = danmuEntity;
                        DanmuVideoAdapter.this.holder2 = finalHolder;

                        EventBus.getDefault().post(danmuEntity.getPosition(), EventBusTags.TAG_VIDEO_COMMENT_THUBMS_UP);


                        danmuEntity.setCanThumbsUp(false);
                        isAllClick = false;
                    }
                }
            });


            convertView.setTag(holder2);
        } else {
            holder2 = (ViewHolder2) convertView.getTag();
        }

        //设置圆角
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(true);//不做内存缓存

        int index = random.nextInt(6);
        //弹幕头像
        if (!TextUtils.isEmpty(danmuEntity.getHeadImg())) {
            Glide.with(context).load(danmuEntity.getHeadImg()).apply(mRequestOptions).into(holder2.image);
        } else {
            Glide.with(context).load(R.drawable.icon_mine_head).apply(mRequestOptions).into(holder2.image);
        }
        holder2.image.setBackgroundResource(HEAD_RESOURCES[index]);
        //弹幕内容
        holder2.content.setText(danmuEntity.content);
        holder2.ll_danmu.setBackgroundResource(ICON_RESOURCES[index]);

        //弹幕点赞数量
        if (!danmuEntity.getCanThumbsUp()) {
            holder2.ivFabulous.setSelected(true);
        }
        if (!TextUtils.isEmpty(danmuEntity.getThumbsUpCount())) {
            holder2.tvFabulous.setText(danmuEntity.getThumbsUpCount() + "   ");
        } else {
            holder2.tvFabulous.setText("   ");
        }


//                holder2.content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))); //随机颜色
//                holder2.tvFabulous.setText(String.valueOf(1));

        return convertView;
    }


    @Override
    public int[] getViewTypeArray() {
        int type[] = {0, 1};
        return type;
    }

    @Override
    public int getSingleLineHeight() {

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_news_danmu, null);
        //指定行高
        view2.measure(0, 0);

//        return Math.max(view.getMeasuredHeight(),view2.getMeasuredHeight());
        return view2.getMeasuredHeight(); //相距控件的高度  后面是间距
    }

    /**
     * 弹幕是否可以点击 更新弹幕界面
     * count: 当前点赞次数
     * isClick： 当前弹幕是否可点击 (如果没点击次数则不可点击)
     * isSuccess:当前弹幕是否点赞成功
     * isAllClick: 判断全部点击事件  防止同时点不同item请求多个事件
     */
    public void updateDanmuView(int count, boolean isClick, boolean isSuccess) {
        if (mDanmuEntity != null && holder2 != null) {
            holder2.tvFabulous.setText(String.valueOf(count));
            if (!holder2.ivFabulous.isSelected()) //如果当前是未点赞状态才设置    已经点赞变色则不执行
                holder2.ivFabulous.setSelected(isSuccess);

            mDanmuEntity.setCanThumbsUp(isClick);
            isAllClick = true;
        }
    }


    class ViewHolder2 {
        public TextView content;
        public ImageView image;
        public TextView tvFabulous;
        public ImageView ivFabulous;
        public RelativeLayout ll_danmu;
    }


}
