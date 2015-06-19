package com.team.hbase.model;

import java.io.Serializable;

import android.text.TextUtils;


public class UpdateInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String version;//版本号
	private String size;//APK大小
	private String updateContent;//更新内容
	private String downloadUrl;//更新地址
	private String force_flag;//是否强制更新
	private String status;//获取信息结果
	private String remark;//标记
	
	public boolean isEmpty(){
		if(TextUtils.isEmpty(version)||TextUtils.isEmpty(downloadUrl)) return true;
		return false;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getUpdateContent() {
		return updateContent;
	}
	public void setUpdateContent(String updateContent) {
		this.updateContent = updateContent;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	@Override
	public String toString() {
		return "ZHCG_UpdateInfo [version=" + version + ", size=" + size
				+ ", updateContent=" + updateContent + ", downloadUrl="
				+ downloadUrl + "]";
	}
	public String getForce_flag() {
		return force_flag;
	}
	public void setForce_flag(String force_flag) {
		this.force_flag = force_flag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
