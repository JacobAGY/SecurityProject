package com.szu.cn.Security;

import java.util.List;

public class Result {
    private List<String> list;
    private int time;

    public Result(List<String> list, int time) {
        this.list = list;
        this.time = time;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
