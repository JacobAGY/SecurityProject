package com.szu.cn.Security;

import java.util.ArrayList;
import java.util.HashMap;

public class Process {

    //工序名
    private String name;

    //工序所需时间（分）
    private int time;

    //工序所需资源以及所需资源数量
    private HashMap<String,Integer> resourceSeq;

    public Process(String name, int time, HashMap<String, Integer> resourceSeq) {
        this.name = name;
        this.time = time;
        this.resourceSeq = resourceSeq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public HashMap<String, Integer> getResourceSeq() {
        return resourceSeq;
    }

    public void setResourceSeq(HashMap<String, Integer> resourceSeq) {
        this.resourceSeq = resourceSeq;
    }
}
