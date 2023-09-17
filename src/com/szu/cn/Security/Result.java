package com.szu.cn.Security;

import java.util.List;

public class Result {
    private List<String> list;
    private int time;

    private int finishedEqi;

    //RMST指标-可用度
    private double usability;
    private int faiedEqi;

    private List<Equipment> equipmentList;

    private List<Equipment> records_equiments;

    public Result(List<String> list, int time) {
        this.list = list;
        this.time = time;
    }


    public Result(List<String> list,  List<Equipment> records_equiments, int time) {
        this.list = list;
        this.time = time;
        this.records_equiments = records_equiments;
    }

    public Result(List<String> list, int time, int finishedEqi) {
        this.list = list;
        this.time = time;
        this.finishedEqi = finishedEqi;
    }

    public Result(List<String> list, int time, int finishedEqi, int faiedEqi, double usability) {
        this.list = list;
        this.time = time;
        this.finishedEqi = finishedEqi;
        this.faiedEqi=faiedEqi;
        this.usability=usability;
    }

    public double getUsability() {
        return usability;
    }

    public void setUsability(double usability) {
        this.usability = usability;
    }

    public int getFaiedEqi() {
        return faiedEqi;
    }

    public void setFaiedEqi(int faiedEqi) {
        this.faiedEqi = faiedEqi;
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

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public List<Equipment> getRecords_equiments() {
        return records_equiments;
    }

    public void setRecords_equiments(List<Equipment> records_equiments) {
        this.records_equiments = records_equiments;
    }
}
