package com.dzkandian.common.uitls;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 圆形图片工具类
 */
public class CompressUtil {

//    public static String bitmapToString(String filePath) {
//
//        Bitmap bm = getSmallBitmap(filePath, 480, 800);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
//        byte[] b = baos.toByteArray();
//
//        return Base64.encodeToString(b, Base64.DEFAULT);
//
//    }

    private static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 根据路径获得突破并尺寸压缩返回bitmap用于显示
     *
     * @param uri       图片Uri
     * @param reqWidth  要求的图片的像素
     * @param reqHeight 要求的图片的像素
     * @return
     */
    public static Bitmap getSmallBitmap(@NonNull Activity ac, @NonNull Uri uri, int reqWidth, int reqHeight) {
        InputStream input = null;
        Bitmap bitmap = null;
        try {
            input = ac.getContentResolver().openInputStream(uri);

            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            if (input != null) {
                input.close();
            }
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;

            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (originalWidth > originalHeight && originalWidth > reqWidth) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (originalWidth / reqWidth);
            } else if (originalWidth < originalHeight && originalHeight > reqHeight) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (originalHeight / reqHeight);
            }
            if (be <= 0)
                be = 1;
            //比例压缩
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = be;//设置缩放比例
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = ac.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressPic(bitmap);
    }

    /**
     * 根据路径获得突破并尺寸压缩返回bitmap用于显示
     *
     * @param filePath  图片的路径
     * @param reqWidth  要求的图片的像素
     * @param reqHeight 要求的图片的像素
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;

        return compressPic(BitmapFactory.decodeFile(filePath, options));
    }

    /**
     * 质量压缩方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressPic(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
    }

}
