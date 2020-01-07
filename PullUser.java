package com.xja.wechat.entity;

public class PullUser {
    private Integer id;

    private String scan_id;

    private String share_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getScan_id() {
        return scan_id;
    }

    public void setScan_id(String scan_id) {
        this.scan_id = scan_id == null ? null : scan_id.trim();
    }

    public String getShare_id() {
        return share_id;
    }

    public void setShare_id(String share_id) {
        this.share_id = share_id == null ? null : share_id.trim();
    }
}