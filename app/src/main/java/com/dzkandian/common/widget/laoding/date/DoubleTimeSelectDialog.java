package com.dzkandian.common.widget.laoding.date;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;

import java.util.Arrays;
import java.util.List;


/**
 * 日期选择
 */

public class DoubleTimeSelectDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    /**
     * 开始、结束年份
     */
    private static int START_YEAR = 1990, END_YEAR = 2100;
    /**
     * 年
     */
    private WheelView mYearView;
    /**
     * 月
     */
    private WheelView mMonthView;
    /**
     * 日
     */
    private WheelView mDayView;
    /**
     * list列表(大月份)
     */
    private List<String> mListBig;
    /**
     * list列表(小月份)
     */
    private List<String> mListLittle;

    /* 是否只选择本年 */
    private boolean isOnlyThisYear = false;

    /* 是否只选择本月 */
    private boolean isOnlyThisMonth = false;

    private int year;
    private int month;
    private int day;

    private int curYear;
    private int curMonth;
    private int curDay;


    /**
     * 选择的开始时间
     */
    private String mSelectStartTime;

    /**
     * 当前选择时间模式
     */
    @NonNull
    private TIME_TYPE mTimeType = TIME_TYPE.TYPE_START;

    /**
     * 最小时间
     */
    private String allowedSmallestTime;
    /**
     * 最大时间
     */
    private String allowedBiggestTime;
    private DateSelectListener dateSelectListener;

    private enum TIME_TYPE {
        TYPE_START
    }

    public DoubleTimeSelectDialog(@NonNull Context context, String earliestTime, String startTime, String endTime) {
        super(context, R.style.PopBottomDialogStyle);
        this.mContext = context;
        this.allowedSmallestTime = earliestTime;
        this.allowedBiggestTime = endTime;
        setContentView(R.layout.popwindow_bottom_layout);
        setCanceledOnTouchOutside(true);

        init(startTime, false);

        String monthS = String.format("%02d", curMonth);
        String dayS = String.format("%02d", curDay);
        String yearS = String.format("%02d", curYear);
        if (!TextUtils.isEmpty(startTime)) {
            mSelectStartTime = startTime;
        } else {
            mSelectStartTime = yearS + "-" + monthS + "-" + dayS;
        }

        TextView ok = findViewById(R.id.tv_tclOk);
        ok.setOnClickListener(this);
    }


    /*恢复起始时间按钮的点击状态*/
    public void recoverButtonState() {
        mTimeType = TIME_TYPE.TYPE_START;
        init(mSelectStartTime, false);
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.tv_tclOk:
                dateSelectListener.onSexChanged(mSelectStartTime);
                this.dismiss();
        }
    }

    public void setOnClickListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
    }

    public interface DateSelectListener {

        void onSexChanged(String date);
    }

    public void init(@Nullable String date, boolean isShowHour) {
        if (date != null) {
            String[] ymd = date.split("-");
            if (ymd.length > 2) {
                curYear = Integer.parseInt(ymd[0]);
                curMonth = Integer.parseInt(ymd[1]) - 1;
                String[] dhm = ymd[2].split(" ");
                curDay = Integer.parseInt(dhm[0]);
            }
        }
        mYearView = findViewById(R.id.year);
        mMonthView = findViewById(R.id.month);
        mDayView = findViewById(R.id.day);

        mYearView.addChangingListener(yearWheelListener);
        mMonthView.addChangingListener(monthWheelListener);
        mDayView.addChangingListener(dayWheelListener);

        initDatePicker();

    }

    /**
     * 弹出日期时间选择器
     */
    private void initDatePicker() {
        String[] ymd = allowedSmallestTime.split("-");
        if (TextUtils.isEmpty(allowedBiggestTime))
            allowedBiggestTime = TimeUtil.getCurData();
        String[] ymdEnd = allowedBiggestTime.split("-");

        if (ymd.length > 2) {
            START_YEAR = Integer.parseInt(ymd[0]);
        }
        if (ymdEnd.length > 2) {
            END_YEAR = Integer.parseInt(ymdEnd[0]);
            month = Integer.parseInt(ymdEnd[1]) - 1;
            day = Integer.parseInt(ymdEnd[2]);
        }

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] monthsBig = {"1", "3", "5", "7", "8", "10", "12"};
        String[] monthsLittle = {"4", "6", "9", "11"};

        mListBig = Arrays.asList(monthsBig);
        mListLittle = Arrays.asList(monthsLittle);

        // 年
        mYearView.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        mYearView.setLabel("");// 添加文字
        int yearPos = isOnlyThisYear ? END_YEAR - START_YEAR : curYear != 0 ? curYear - START_YEAR : END_YEAR - START_YEAR;

        mYearView.setCurrentItem(yearPos);// 初始化时显示的数据 START_YEAR - END_YEAR
        mYearView.setCyclic(true);// 循环滚动


//        // 月
//        int startMonth = 1;
//
//        //初始年份最大值应该是当年最大月
        mMonthView.setAdapter(new NumericWheelAdapter(1,12));// 设置"月"的显示数据
        mMonthView.setCurrentItem(isOnlyThisMonth ? 0 : curMonth != 0 ? curMonth : month + 1);
        mMonthView.setCyclic(true);

        // 日
        mDayView.setAdapter(new NumericWheelAdapter(1,31));// 设置"日"的显示数据
        mDayView.setCurrentItem(curDay == 0 ? day - 1 : curDay - 1);
        mDayView.setCyclic(true);

        // 选择器字体的大小
        int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.p16);
        mDayView.TEXT_SIZE = textSize;
        mMonthView.TEXT_SIZE = textSize;
        mYearView.TEXT_SIZE = textSize;

    }


    /**
     * 添加对"年"监听
     */
    @NonNull
    private OnWheelChangedListener yearWheelListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            int year_num = newValue + START_YEAR;
            if (year_num < year) {
                mMonthView.setAdapter(new NumericWheelAdapter(1, 12));
            } else if (year_num >= year) {
                mMonthView.setAdapter(new NumericWheelAdapter(1, month + 1));
            }
            // 判断大小月及是否闰年,用来确定"日"的数据
            if (mListBig.contains(String.valueOf(mMonthView.getCurrentItem() + 1))) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 31));
            } else if (mListLittle.contains(String.valueOf(mMonthView.getCurrentItem() + 1))) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 30));
            } else {
                if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
                    mDayView.setAdapter(new NumericWheelAdapter(1, 29));
                else
                    mDayView.setAdapter(new NumericWheelAdapter(1, 28));
            }
            onScroll();
            mMonthView.setCurrentItem(mMonthView.getCurrentItem());
            mDayView.setCurrentItem(mDayView.getCurrentItem());

        }
    };

    /**
     * 添加对"月"监听
     */
    @NonNull
    private OnWheelChangedListener monthWheelListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            int month_num = newValue + 1;
            if (month_num == (month + 1)) {
                mDayView.setAdapter(new NumericWheelAdapter(1, day));
            } else {
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (mListBig.contains(String.valueOf(month_num))) {
                    mDayView.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (mListLittle.contains(String.valueOf(month_num))) {
                    mDayView.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((mYearView.getCurrentItem() + START_YEAR) % 4 == 0 && (mYearView.getCurrentItem() + START_YEAR) % 100 != 0)
                            || (mYearView.getCurrentItem() + START_YEAR) % 400 == 0)
                        mDayView.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        mDayView.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
            onScroll();
            mDayView.setCurrentItem(mDayView.getCurrentItem());

        }
    };

    /**
     * 添加对 日滚动控件 的添加
     */
    @NonNull
    private OnWheelChangedListener dayWheelListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            onScroll();
            mDayView.setCurrentItem(newValue);
        }
    };

    private void onScroll() {

        int year = isOnlyThisYear ? Integer.parseInt(mYearView.getAdapter().getItem(0))
                : mYearView.getCurrentItem() + START_YEAR;
        int month = isOnlyThisMonth ? Integer.parseInt(mMonthView.getAdapter().getItem(0))
                : mMonthView.getCurrentItem() + 1;
        int day = mDayView.getCurrentItem() + 1;

        String monthS = String.format("%02d", month);
        String dayS = String.format("%02d", day);
        String yearS = String.format("%02d", year);

        if (mTimeType == TIME_TYPE.TYPE_START) {
            mSelectStartTime = yearS + "-" + monthS + "-" + dayS;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mYearView.removeChangingListener(yearWheelListener);
        mMonthView.removeChangingListener(monthWheelListener);
        mDayView.removeChangingListener(dayWheelListener);
    }
}
