package com.tencent.wxpay.bean;

public class FacePayRequestZbody extends ZBODY {

    private String corp_id; //公司id
    private String site_id; //站点id
    private String pay_type; //支付类型   00：支付宝 01：微信

    public String getCorp_id() {
        return corp_id;
    }

    public void setCorp_id(String corp_id) {
        this.corp_id = corp_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }
}
