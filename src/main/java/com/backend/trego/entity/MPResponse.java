package com.backend.trego.entity;

public class MPResponse {
    private String orderid;
    private String init_point;
    private String sandbox_init_point;
    
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public void setInit_point(String init_point) {
        this.init_point = init_point;
    }

    public void setSandbox_init_point(String sandbox_init_point) {
        this.sandbox_init_point = sandbox_init_point;
    }

    public String getOrderid() {
        return orderid;
    }

    public String getInit_point() {
        return init_point;
    }

    public String getSandbox_init_point() {
        return sandbox_init_point;
    }

    public MPResponse(String orderid, String init_point, String sandbox_init_point) {
        this.orderid = orderid;
        this.init_point = init_point;
        this.sandbox_init_point = sandbox_init_point;
    }
}
