package com.dzkandian.storage.bean.mine;

import java.io.Serializable;
import java.util.List;

/**
 * 提现
 *
 * Created by Administrator on 2018/4/27.
 */

public class CoinExchangeBean implements Serializable{

    /**
     * tip : 正常最慢3天内到账，请耐心等待，体谅一下客服妹子哟~
     * weixinPayName : null
     * surplus : 950
     * weixinPayPhone : null
     * alipayName : null
     * list : [{"rmb":100,"gold":10000,"surplus":89099534,"createTime":"2018-01-08 11:13:42","createUser":1,"modifyTime":"2018-01-08 14:51:18","modifyUser":1,"delFlag":0}]
     * alipayAccount : null
     * weixinPayAppid	string	绑定微信钱包授权使用的appid
     */

    private String tip;
    private String weixinPayName;
    private int surplus;
    private String weixinPayPhone;
    private String alipayName;
    private String alipayAccount;
    private List<ListBean> list;
    private String weixinPayAppid;

    public String getWeixinPayAppid() {
        return weixinPayAppid;
    }

    public void setWeixinPayAppid(String weixinPayAppid) {
        this.weixinPayAppid = weixinPayAppid;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getWeixinPayName() {
        return weixinPayName;
    }

    public void setWeixinPayName(String weixinPayName) {
        this.weixinPayName = weixinPayName;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public String getWeixinPayPhone() {
        return weixinPayPhone;
    }

    public void setWeixinPayPhone(String weixinPayPhone) {
        this.weixinPayPhone = weixinPayPhone;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * rmb : 100
         * gold : 10000
         * surplus : 89099534
         * createTime : 2018-01-08 11:13:42
         * createUser : 1
         * modifyTime : 2018-01-08 14:51:18
         * modifyUser : 1
         * delFlag : 0
         */

        private int rmb;
        private int gold;
        private int surplus;
        private String createTime;
        private int createUser;
        private String modifyTime;
        private int modifyUser;
        private int delFlag;

        public int getRmb() {
            return rmb;
        }

        public void setRmb(int rmb) {
            this.rmb = rmb;
        }

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }

        public int getSurplus() {
            return surplus;
        }

        public void setSurplus(int surplus) {
            this.surplus = surplus;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCreateUser() {
            return createUser;
        }

        public void setCreateUser(int createUser) {
            this.createUser = createUser;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        public int getModifyUser() {
            return modifyUser;
        }

        public void setModifyUser(int modifyUser) {
            this.modifyUser = modifyUser;
        }

        public int getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(int delFlag) {
            this.delFlag = delFlag;
        }
    }
}
