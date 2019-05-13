package com.dzkandian.common.widget.laoding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dzkandian.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 头像dialog
 * Created by Administrator on 2018/5/9.
 */

public class HeadPortrailDialog extends DialogFragment {
    @Nullable
    @BindView(R.id.tv_camera)
    TextView tvCamera;
    @Nullable
    @BindView(R.id.tv_album)
    TextView tvAlbum;
    @Nullable
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    Unbinder unbinder;
    @Nullable
    private PhotoSelectListener mPhotoSelectListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(R.style.PopBottomDialogStyle);
        View view = inflater.inflate(R.layout.popwindow_photo, null);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCamera.setOnClickListener(v -> {
            mPhotoSelectListener.onCameraListener();
            dismiss();
        });
        tvAlbum.setOnClickListener(v -> {
            mPhotoSelectListener.onAlbumListener();
            dismiss();
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setOnClickListener(PhotoSelectListener photoSelectListener) {
        this.mPhotoSelectListener = photoSelectListener;
    }

    public interface PhotoSelectListener {
        void onCameraListener();

        void onAlbumListener();
    }

    @Override
    public void onDestroyView() {
        if (mPhotoSelectListener != null)
            mPhotoSelectListener = null;
        super.onDestroyView();
        unbinder.unbind();
    }
}
