package com.dzkandian.mvp.news.ui.adapter;

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

public class DanmuAdapter extends XAdapter<NewsDanmuBean> {

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

    public DanmuAdapter(Context c) {
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


            convertView.setTag(holder2);
        } else {
            holder2 = (ViewHolder2) convertView.getTag();
        }

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
                        .build(), EventBusTags.TAG_NEWS_ANIMATION_THUBMS_UP);

//                Timber.d("================点击的X坐标"+viewX+"         Y坐标"+viewY);
                if (!NetworkUtils.checkNetwork(context.getApplicationContext())) { // 如果没有网
                    if (System.currentTimeMillis() - mCurrClickTime > 3000) {
                        mCurrClickTime = System.currentTimeMillis();
                        ArmsUtils.makeText(context, "网络请求失败，请连网后重试");
                    }
                    return;
                }
                if (!TextUtils.isEmpty(danmuEntity.getThumbsUpCount()) && danmuEntity.getCanThumbsUp()
                        && !TextUtils.isEmpty(danmuEntity.getPosition()) && isAllClick) { //是否可以点赞 ：true可以；false：不可以
//                    Timber.d("============="+danmuEntity.getPosition());
                    DanmuAdapter.this.mDanmuEntity = danmuEntity;
                    DanmuAdapter.this.holder2 = finalHolder;

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) finalHolder.tvFabulous.getLayoutParams();
                    params.width = 120;//设置当前控件布局的高度width是屏幕宽度
                    finalHolder.tvFabulous.setLayoutParams(params);//将设置好的布局参数应用到控件中


                    EventBus.getDefault().post(danmuEntity.getPosition(), EventBusTags.TAG_NEWS_COMMENT_THUBMS_UP);

                    danmuEntity.setCanThumbsUp(false);
                    isAllClick = false;
                }
            }
        });

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

//    //点击弹幕显示红心动画
//    public void updateDanmuAnimator() {
//
//    }

    /**
     * 弹幕是否可以点击 更新弹幕界面
     * count: 当前点赞次数
     * isClick： 当前弹幕是否可点击 (如果没点击次数则不可点击)
     * isSuccess:当前弹幕是否点赞成功
     * isAllClick: 判断全部点击事件  防止同时点不同item请求多个事件
     */
    public void updateDanmuView(int count, boolean isClick, boolean isSuccess) {
        if (mDanmuEntity != null && holder2 != null && holder2.ivFabulous != null && holder2.tvFabulous != null) {
//            Timber.d("===点赞增加后的数量" + count + "         当前位置" + mDanmuEntity.getPosition() + "             是否能点击" + isClick);
            holder2.tvFabulous.setText(String.valueOf(count));
            if (!holder2.ivFabulous.isSelected()) //如果当前是未点赞状态才设置    已经点赞变色则不执行
                holder2.ivFabulous.setSelected(isSuccess);
            mDanmuEntity.setCanThumbsUp(isClick);
            isAllClick = true;
        }
    }


    //    @Override
//    public View getView(final NewsDanmuBean danmuEntity, View convertView) {
//
//        ViewHolder1 holder1 = null;
//        ViewHolder2 holder2 = null;
//
//        if(convertView == null){
//            switch (danmuEntity.getType()) {
//                case 0:
//                    //图片加弹幕效果
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
//                    holder1 = new ViewHolder1();
//                    holder1.content = (TextView) convertView.findViewById(R.id.content);
//                    holder1.image = (ImageView) convertView.findViewById(R.id.image);
//                    convertView.setTag(holder1);
//                    break;
//                case 1:
//                    //点赞效果的item
//                    convertView = LayoutInflater.from(context).inflate(R.layout.item_super_danmu, null);
//                    holder2 = new ViewHolder2();
//                    holder2.content = (TextView) convertView.findViewById(R.id.content);
//                    holder2.image = (ImageView) convertView.findViewById(R.id.image);
//                    holder2.ivFabulous = (ImageView) convertView.findViewById(R.id.image_fabulous);
//                    holder2.tvFabulous = (TextView) convertView.findViewById(R.id.tv_fabulous);
//                    holder2.ll_danmu = (LinearLayout) convertView.findViewById(R.id.ll_danmu);
//                    final ViewHolder2 finalHolder = holder2;
//                    holder2.ll_danmu.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            finalHolder.tvFabulous.setText(String.valueOf(2));
//                        }
//                    });
//                    convertView.setTag(holder2);
//                    break;
//            }
//        }
//        else{
//            switch (danmuEntity.getType()) {
//                case 0:
//                    holder1 = (ViewHolder1)convertView.getTag();
//                    break;
//                case 1:
//                    holder2 = (ViewHolder2)convertView.getTag();
//                    break;
//            }
//        }
//
//        switch (danmuEntity.getType()) {
//            case 0:
//                Glide.with(context).load(R.drawable.icon_comment_head).into(holder1.image);
//                holder1.content.setText(danmuEntity.content);
//                holder1.content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
//                break;
//            case 1:
//                Glide.with(context).load(ICON_RESOURCES[random.nextInt(5)]).into(holder2.image);
//                holder2.content.setText(danmuEntity.content);
//                holder2.content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
//                holder2.tvFabulous.setText(String.valueOf(1));
//                holder2.ivFabulous.setBackgroundResource(R.drawable.icon_heart);
//                break;
//        }
//
//        return convertView;
//    }
    @Override
    public int[] getViewTypeArray() {
        int type[] = {0, 1};
        return type;
    }

    @Override
    public int getSingleLineHeight() {
//        //将所有类型弹幕的布局拿出来，找到高度最大值，作为弹道高度
//        View view = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
//        //指定行高
//        view.measure(0, 0);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_news_danmu, null);
        //指定行高
        view2.measure(0, 0);

//        return Math.max(view.getMeasuredHeight(),view2.getMeasuredHeight());
        return view2.getMeasuredHeight(); //相距控件的高度  后面是间距
    }


//    class ViewHolder1{
//        public TextView content;
//        public ImageView image;
//    }

    class ViewHolder2 {
        TextView content;
        ImageView image;
        TextView tvFabulous;
        ImageView ivFabulous;
        RelativeLayout ll_danmu;
    }


}
