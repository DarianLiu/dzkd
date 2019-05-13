package com.dzkandian.common.uitls;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import timber.log.Timber;

/**
 * 相机相册工具类
 * Created by liuli on 2018/5/6.
 */
public class PhotoUtils {

    /**
     * 打开相机
     *
     * @param photoUri 指定拍照Uri，解决某些机型拍照后返回Uri为空的问题
     */
    @NonNull
    public static Intent openCamera(Uri photoUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        return intent;
    }

    /**
     * 打开相册
     */
    @Nullable
    public static Intent openAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return albumIntent;
//        activity.startActivityForResult(albumIntent, Constant.REQUEST_CODE_ALBUM);
    }

    /**
     * 调用系统照片的裁剪功能，修改编辑头像的选择模式(适配Android7.0)
     */
    @NonNull
    public static Intent invokeSystemCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        File out = new File(Constant.filePath);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }

        if (out.exists()){
            out.delete();
        }
        Timber.d("======文件写入地址：" + out.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }

}
