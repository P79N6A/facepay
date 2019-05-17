package com.tencent.wxpay.bean;

/**
 * @author zengfeng
 * @date 2017年8月11日 下午3:02:46
 * @des		request的ZHEAD
 */
public class ZHEAD {

	private String bcode ="02";

	private String iend ="1";

	private String iflag ="1";

	private String istart ="1";

	private String tcode;

	public ZHEAD(String tcode) {
		this.tcode = tcode;
	}

	public void setBcode(String bcode) {
		this.bcode = bcode;
	}

	public String getBcode() {
		return this.bcode;
	}

	public void setIend(String iend) {
		this.iend = iend;
	}

	public String getIend() {
		return this.iend;
	}

	public void setIflag(String iflag) {
		this.iflag = iflag;
	}

	public String getIflag() {
		return this.iflag;
	}

	public void setIstart(String istart) {
		this.istart = istart;
	}

	public String getIstart() {
		return this.istart;
	}

	public void setTcode(String tcode) {
		this.tcode = tcode;
	}

	public String getTcode() {
		return this.tcode;
	}
}
