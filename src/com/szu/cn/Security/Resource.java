package com.szu.cn.Security;

import java.io.Serializable;
import java.util.ArrayList;

public class Resource implements Serializable {
    //资源名
    private String name;

    //资源数量
    private int num;

    private status state;

    //资源价格
    private int price;

    //资源的全局灵敏度
    private double globalSensitivity;
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
        this.state=status.wait;
    }

    public status getState() {
        return state;
    }

    public void setState(status state) {
        this.state = state;
    }
    public double getGlobalSensitivity() {
        return globalSensitivity;
    }

    public void setGlobalSensitivity(double globalSensitivity) {
        this.globalSensitivity = globalSensitivity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
