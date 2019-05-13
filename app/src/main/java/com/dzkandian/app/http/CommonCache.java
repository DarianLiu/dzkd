package com.dzkandian.app.http;

import android.support.annotation.NonNull;

import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.BaseResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;

/**
 * 数据缓存
 * Created by LiuLi on 2018/4/11.
 */

public interface CommonCache {


    /**
     * 获取资讯分类列表缓存
     * @param newsTitles      新闻标题列表
     * @param dynamicKey      读取缓存的Key值
     * @param evictDynamicKey 当参数为true时，RxCache会直接驱逐该Dynamic的缓存数据，
     */
    @NonNull
    @ProviderKey(Constant.CACHE_PK_NEW)
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<BaseResponse<List<String>>>  getNewsTitles(Observable<BaseResponse<List<String>>> newsTitles,
                                                          DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取视频栏目列表缓存
     *  @param videoTitles     视频标题列表
     * @param dynamicKey      读取缓存的Key值
     * @param evictDynamicKey 当参数为true时，RxCache会直接驱逐该Dynamic的缓存数据，
     */
    @NonNull
    @ProviderKey(Constant.CACHE_PK_NEW)
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<BaseResponse<List<String>>> getVideoTitles(Observable<BaseResponse<List<String>>> videoTitles,
                                                           DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

}
