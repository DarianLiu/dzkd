package com.dzkandian.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.news.NewsRecordBean;
import com.dzkandian.storage.bean.SearchHistoryBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.dzkandian.storage.bean.video.VideoRecordBean;

import com.dzkandian.db.DeviceInfoBeanDao;
import com.dzkandian.db.NewsRecordBeanDao;
import com.dzkandian.db.SearchHistoryBeanDao;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.db.VideoBeanDao;
import com.dzkandian.db.VideoRecordBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig deviceInfoBeanDaoConfig;
    private final DaoConfig newsRecordBeanDaoConfig;
    private final DaoConfig searchHistoryBeanDaoConfig;
    private final DaoConfig userInfoBeanDaoConfig;
    private final DaoConfig videoBeanDaoConfig;
    private final DaoConfig videoRecordBeanDaoConfig;

    private final DeviceInfoBeanDao deviceInfoBeanDao;
    private final NewsRecordBeanDao newsRecordBeanDao;
    private final SearchHistoryBeanDao searchHistoryBeanDao;
    private final UserInfoBeanDao userInfoBeanDao;
    private final VideoBeanDao videoBeanDao;
    private final VideoRecordBeanDao videoRecordBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        deviceInfoBeanDaoConfig = daoConfigMap.get(DeviceInfoBeanDao.class).clone();
        deviceInfoBeanDaoConfig.initIdentityScope(type);

        newsRecordBeanDaoConfig = daoConfigMap.get(NewsRecordBeanDao.class).clone();
        newsRecordBeanDaoConfig.initIdentityScope(type);

        searchHistoryBeanDaoConfig = daoConfigMap.get(SearchHistoryBeanDao.class).clone();
        searchHistoryBeanDaoConfig.initIdentityScope(type);

        userInfoBeanDaoConfig = daoConfigMap.get(UserInfoBeanDao.class).clone();
        userInfoBeanDaoConfig.initIdentityScope(type);

        videoBeanDaoConfig = daoConfigMap.get(VideoBeanDao.class).clone();
        videoBeanDaoConfig.initIdentityScope(type);

        videoRecordBeanDaoConfig = daoConfigMap.get(VideoRecordBeanDao.class).clone();
        videoRecordBeanDaoConfig.initIdentityScope(type);

        deviceInfoBeanDao = new DeviceInfoBeanDao(deviceInfoBeanDaoConfig, this);
        newsRecordBeanDao = new NewsRecordBeanDao(newsRecordBeanDaoConfig, this);
        searchHistoryBeanDao = new SearchHistoryBeanDao(searchHistoryBeanDaoConfig, this);
        userInfoBeanDao = new UserInfoBeanDao(userInfoBeanDaoConfig, this);
        videoBeanDao = new VideoBeanDao(videoBeanDaoConfig, this);
        videoRecordBeanDao = new VideoRecordBeanDao(videoRecordBeanDaoConfig, this);

        registerDao(DeviceInfoBean.class, deviceInfoBeanDao);
        registerDao(NewsRecordBean.class, newsRecordBeanDao);
        registerDao(SearchHistoryBean.class, searchHistoryBeanDao);
        registerDao(UserInfoBean.class, userInfoBeanDao);
        registerDao(VideoBean.class, videoBeanDao);
        registerDao(VideoRecordBean.class, videoRecordBeanDao);
    }
    
    public void clear() {
        deviceInfoBeanDaoConfig.clearIdentityScope();
        newsRecordBeanDaoConfig.clearIdentityScope();
        searchHistoryBeanDaoConfig.clearIdentityScope();
        userInfoBeanDaoConfig.clearIdentityScope();
        videoBeanDaoConfig.clearIdentityScope();
        videoRecordBeanDaoConfig.clearIdentityScope();
    }

    public DeviceInfoBeanDao getDeviceInfoBeanDao() {
        return deviceInfoBeanDao;
    }

    public NewsRecordBeanDao getNewsRecordBeanDao() {
        return newsRecordBeanDao;
    }

    public SearchHistoryBeanDao getSearchHistoryBeanDao() {
        return searchHistoryBeanDao;
    }

    public UserInfoBeanDao getUserInfoBeanDao() {
        return userInfoBeanDao;
    }

    public VideoBeanDao getVideoBeanDao() {
        return videoBeanDao;
    }

    public VideoRecordBeanDao getVideoRecordBeanDao() {
        return videoRecordBeanDao;
    }

}
