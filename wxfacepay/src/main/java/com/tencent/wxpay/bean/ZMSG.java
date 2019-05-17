package com.tencent.wxpay.bean;

import java.io.Serializable;

/**
 * @author zengfeng
 * @date 2017年8月11日 下午3:00:24
 * @des request的ZMSG
 */
public class ZMSG implements Serializable {

    private ZBODY ZBODY;

    private ZHEAD ZHEAD;

    public void setZBODY(ZBODY ZBODY) {
        this.ZBODY = ZBODY;
    }

    public ZBODY getZBODY() {
        return this.ZBODY;
    }

    public void setZHEAD(ZHEAD ZHEAD) {
        this.ZHEAD = ZHEAD;
    }

    public ZHEAD getZHEAD() {
        return this.ZHEAD;
    }
}
