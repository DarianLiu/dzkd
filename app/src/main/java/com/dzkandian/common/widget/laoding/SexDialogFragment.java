package com.dzkandian.common.widget.laoding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dzkandian.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 性别选择
 * Created by Administrator on 2018/3/6.
 */
public class SexDialogFragment extends DialogFragment {

    @Nullable
    @BindView(R.id.rb_boy)
    RadioButton rbBoy;
    @Nullable
    @BindView(R.id.rb_girl)
    RadioButton rbGirl;
    @Nullable
    @BindView(R.id.sex_group)
    RadioGroup sexGroup;
    Unbinder unbinder;

    private SexSelectListener sexSelectListener;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.sex_dialog, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int sexType = getArguments().getInt("sex");
        if (sexType == 1) {
            rbBoy.setChecked(true);
        } else if (sexType == 2) {
            rbGirl.setChecked(true);
        }
        sexGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_boy:
                    if (sexSelectListener != null) {
                        sexSelectListener.onSexChanged(1, "男");
                        dismiss();
                    }
                    break;
                case R.id.rb_girl:
                    if (sexSelectListener != null) {
                        sexSelectListener.onSexChanged(2, "女");
                        dismiss();
                    }
                    break;
            }
        });

    }

    public void setOnCheckedChangeListener(SexSelectListener sexSelectListener) {
        this.sexSelectListener = sexSelectListener;
    }

    public interface SexSelectListener {
        void onSexChanged(int sexType, String sex);
    }

    @Override
    public void onDestroyView() {
        if (sexSelectListener != null)
            sexSelectListener = null;
        super.onDestroyView();
        unbinder.unbind();
    }
}