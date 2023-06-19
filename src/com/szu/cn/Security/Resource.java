package com.szu.cn.Security;

import java.util.ArrayList;

public class Resource {
    //资源名
    private String name;

    //资源数量
    private int num;

    //资源状态集合
    public enum status{
        wait,running
    };

    //资源状态序列
    private ArrayList<String> statusSeq;

    //资源服务序列
    private ArrayList<String> serviceSeq;

    public Resource(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ArrayList<String> getStatusSeq() {
        return statusSeq;
    }

    public void setStatusSeq(ArrayList<String> statusSeq) {
        this.statusSeq = statusSeq;
    }

    public ArrayList<String> getServiceSeq() {
        return serviceSeq;
    }

    public void setServiceSeq(ArrayList<String> serviceSeq) {
        this.serviceSeq = serviceSeq;
    }
}
