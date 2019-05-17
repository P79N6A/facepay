package com.tencent.wxpay.bean;

import java.io.Serializable;

/**
 * @author zengfeng
 * @date 2017年8月11日 下午3:02:19
 * @des		request的ZBODY
 */
public class ZBODY implements Serializable {

	public String auth_id = AppConstants.AUTH_ID;

	public String auth_name = AppConstants.AUTH_NAME;

	public String getAuth_id() {
		return auth_id;
	}

	public void setAuth_id(String auth_id) {
		this.auth_id = auth_id;
	}

	public String getAuth_name() {
		return auth_name;
	}

	public void setAuth_name(String auth_name) {
		this.auth_name = auth_name;
	}

}
