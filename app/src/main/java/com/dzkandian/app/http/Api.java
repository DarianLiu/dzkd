package com.dzkandian.app.http;

/**
 * 服务器地址配置
 * Created by LiuLi on 2018/2/1.
 */
public interface Api {

    /**
     * 测试环境与生产环境切换
     **/
    boolean RELEASE_VERSION = true;

//    公网测试服地址：https://a.dzkandian.com 用于外网演示使用，完成一个功能会同步更新

//    内网测试服地址：http://test.frp.dzkandian.com 客户端开发默认使用此地址，与文档同步更新

//    开发者地址(TC)：http://192.168.1.200:8080 当需要排查问题、断点调试时使用

//    开发者地址(MGH)：http://192.168.1.201:8080 当需要排查问题、断点调试时使用

//    开发者地址(XWL)：http://192.168.1.202:8080 当需要排查问题、断点调试时使用

//    开发者地址(XWL)：http://192.168.1.34:8080 当需要排查问题、断点调试时使用

    /**
     * 服务器IP地址
     */
    String IP = RELEASE_VERSION ? "a.dzkandian.com" : "test.frp.dzkandian.com";//test.frp.dzkandian.com    192.168.1.200   hhb.frp.dzkandian.com    192.168.1.34:8080

    /**
     * 端口号
     */
    String PORT = RELEASE_VERSION ? "" : "";//   :8080

    /**
     * 关于用户的服务器URL
     */
    String BASE_URL = (RELEASE_VERSION ? "https://" : "http://") + IP + PORT;

    /**
     * 新闻/视频的服务器URL
     */
    String NEWS_URL = (RELEASE_VERSION ? "https://" : "http://") + (RELEASE_VERSION ? "lua.dzkandian.com" : "lua.frp.dzkandian.com");
    //        String NEWS_URL = "https://lua.dzkandian.com"; 
    String NEWS_DOMAIN_NAME = "news";

    /**
     * 评论服务器URL   还有收藏功能的接口；
     */
    String COMMENT_URL = (RELEASE_VERSION ? "http://" : "http://") + (RELEASE_VERSION ? "cmt.dzkandian.com" : "192.168.1.253:8888");
    //内网 192.168.1.254:8888 cmt.frp.dzkandian.com  192.168.1.31:8080(惠斌)  192.168.1.214:8081(林研)
    String COMMENT_DOMAIN_NAME = "comment";

    /**
     * 广告服务器URL；
     */
    String AD_URL = (RELEASE_VERSION ? "http://" : "http://") + (RELEASE_VERSION ? "ad.yrdsp.com" : "ad.yrdsp.com");
    String AD_DOMAIN_NAME = "ad";

    /**
     * 搜索服务器URL；
     */
    String SEARCH_URL = (RELEASE_VERSION ? "http://" : "http://") + (RELEASE_VERSION ? "4g.dzkandian.com" : "lua.frp.dzkandian.com");
    String SEARCH_DOMAIN_NAME = "Search";

    /**
     * 惠彬接口调用；
     */
    String HHB_URL = (RELEASE_VERSION ? "http://" : "http://") + (RELEASE_VERSION ? "192.168.1.31:8081" : "hhb.frp.dzkandian.com");
    String HHB = "hhb";
}