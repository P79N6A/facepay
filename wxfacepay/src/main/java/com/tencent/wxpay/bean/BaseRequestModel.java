package com.tencent.wxpay.bean;

import java.io.Serializable;

/**
 * @author zengfeng
 * @date 2017年8月11日 下午2:20:39
 * @des
 */
public class BaseRequestModel implements Serializable {

    private ZMSG ZMSG;

    public void setZMSG(ZMSG ZMSG) {
        this.ZMSG = ZMSG;
    }

    public ZMSG getZMSG() {
        return this.ZMSG;
    }

}
