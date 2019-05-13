package com.dzkandian.common.uitls;

import android.support.annotation.NonNull;

import com.dzkandian.storage.ColumnBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字符串List快速比较工具类
 * Created by Administrator on 2018/5/14.
 */

public class ListCompareUtils {
    /**
     *
     * 对比服务器栏目列表整理本地列表（本地栏目List被后台删除移除本栏目，后台添加栏目就添加到本地栏目）
     *
     *  ColumnBean.getAllColumn()  本地栏目列表（本地栏目List）
     * @param serverList 后台栏目列表（后台栏目List）
     *  viewSize   展示栏目数
     */
    @NonNull
    public static ColumnBean getServerAddList(@NonNull ColumnBean bean, @NonNull List<String> serverList) {
        boolean serverListIsMax = false;

        int viewSize = bean.getViewSize();
        Map<String, Integer> map = new HashMap<String, Integer>(bean.getAllColumn().size() + serverList.size());
        List<String> result = new ArrayList<>();
        List<String> maxList = bean.getAllColumn();
        List<String> minList = serverList;
        if (serverList.size() > bean.getAllColumn().size()) {
            maxList = serverList;
            minList = bean.getAllColumn();
            serverListIsMax = true;
        }
        for (String string : maxList) {
            //遍历最大的list列表，并以string元素为K值，1为value值存入map
            map.put(string, 1);
            if (!serverListIsMax) {
                //如果本地列表最大，把所有的本地列表数据添加到result（添加动作只在遍历本地List时发生）
                result.add(string);
            }
        }
        for (int i = 0; i < minList.size(); i++) {
            //遍历最小的list列表，并以string元素为K值，取出map中的value值
            String string = minList.get(i);
            Integer cc = map.get(string);
            if (cc != null) {
                //如果value不为空则存在，将value值加1
                map.put(string, ++cc);
                if (serverListIsMax) {
                    //如果后台列表最大，且该字符串在后台存在时，添加到result中
                    map.put(string, 2);
                    result.add(string);
                }
                //如果本地列表最大，且该字符串共有，无需从result中移除

            } else {
                //如果value为空则在该列表不存在，将value值赋1
                if (serverListIsMax) {
                    //如果后台列表最大，该字符串在后台不存在时，不需要添加到result中
                    map.put(string, 3);
                    if (i < viewSize) {
                        viewSize--;
                    }
                } else {
                    //如果本地列表最大，该字符串在后台不存在时，需要添加到result中
                    map.put(string, 2);
                    result.add(string);
                }
            }

        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                if (serverListIsMax) {
                    result.add(entry.getKey());
                } else {
                    int i = result.indexOf(entry.getKey());
                    result.remove(entry.getKey());
                    if (i < viewSize) {
                        viewSize--;
                    }
                }

            }
        }
        bean.setViewSize(viewSize);
        bean.setAllColumn(result);
        return bean;

    }


}
