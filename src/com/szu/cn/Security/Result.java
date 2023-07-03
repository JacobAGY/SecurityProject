package com.szu.cn.Security;

import java.util.List;

public class Result {
    private List<String> list;
    private int time;

    private int finishedEqi;

    public Result(List<String> list, int time) {
        this.list = list;
        this.time = time;
    }

    public Result(List<String> list, int time, int finishedEqi) {
        this.list = list;
        this.time = time;
        this.finishedEqi = finishedEqi;
    }

    public int getFinishedEqi() {
        return finishedEqi;
    }

    public void setFinishedEqi(int finishedEqi) {
        this.finishedEqi = finishedEqi;
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
