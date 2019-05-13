package com.dzkandian.common.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.dzkandian.R;

import java.util.Random;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by Administrator on 2018/7/11 0011.
 */

public class SoundPoolManager {
    private boolean loaded = false;
    private float actualVolume;
    private float maxVolume;
    private float volume;
    private AudioManager audioManager;
    private SoundPool soundPool;
    private int ringingSoundId2, ringingSoundId3;
    private int ringingStreamId;
    private static SoundPoolManager instance;
    /*Mny*/

    private SoundPoolManager(Context context) {
        // AudioManager audio settings for adjusting the volume
        //初始化音频管理器
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        // 获取设备当前音量
        actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //获取系统最大音量
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;

        // Load the sounds
        //因为在5.0上new SoundPool();被弃用 5.0上利用Builder
        //创建SoundPool
        int maxStreams = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> loaded = true);

        //加载资源ID
        ringingSoundId2 = soundPool.load(context, R.raw.rewardsound2, 1);
        ringingSoundId3 = soundPool.load(context, R.raw.rewardsound3, 1);

    }

    public static SoundPoolManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundPoolManager(context);
        }
        return instance;
    }

    //播放
    public void playRinging() {
        if (soundPool != null && loaded) {
            int randomInt = new Random().nextInt(2);
            if (randomInt == 0) {
                ringingStreamId = soundPool.play(ringingSoundId2, volume, volume, 1, 0, 1f);
            } else {
                ringingStreamId = soundPool.play(ringingSoundId3, volume, volume, 1, 0, 1f);
            }
        }
    }

    //Stop播放
    public void stopRinging() {
        if (soundPool != null) {
            soundPool.stop(ringingStreamId);
        }
    }

    //在退出 MainActivity 时清除；
    public void release() {
        if (soundPool != null) {
            soundPool.unload(ringingSoundId2);
            soundPool.unload(ringingSoundId3);
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}
