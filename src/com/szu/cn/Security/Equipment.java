package com.szu.cn.Security;

import java.util.ArrayList;
import java.util.HashMap;

public class Equipment {
    //装备名称
    private String name;

    //装备数量
    private int num;

    //装备状态集合
    private enum status{
        wait,running,finish,repair
    };

    //装备的结构组成
    private ArrayList<String> composition;

    //装备故障率
    private double failRate;

    //装备各组成单元的故障模式名称及其故障率
    private HashMap<String,Double> map;

    //装备的LRU
    private String LRU;

    //装备LRU的平均维修时间
    private double repairTime;

    //装备LRU的故障检测率
    private double faultDetectionRate;

    //装备的工序序列
    private ArrayList<String> processSeq;


    //装备每个工序所需的资源以及该资源的优先级
    private HashMap<String,ArrayList<HashMap<String,Integer>>> processToResource;

    public HashMap<String, ArrayList<HashMap<String, Integer>>> getProcessToResource() {
        return processToResource;
    }

    public void setProcessToResource(HashMap<String, ArrayList<HashMap<String, Integer>>> processToResource) {
        this.processToResource = processToResource;
    }

    //装备的当前工序
    private String processNow;

    //装备的状态序列
    private String[] stateSeq;

    //装备的占用信息
    private String[] occSeq;

    //装备的故障时刻
    private int failMoment;

    //装备的真实状态却未被检测出的标志
    private int flag;

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

    public ArrayList<String> getComposition() {
        return composition;
    }

    public void setComposition(ArrayList<String> composition) {
        this.composition = composition;
    }

    public double getFailRate() {
        return failRate;
    }

    public void setFailRate(double failRate) {
        this.failRate = failRate;
    }

    public HashMap<String, Double> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Double> map) {
        this.map = map;
    }

    public String getLRU() {
        return LRU;
    }

    public void setLRU(String LRU) {
        this.LRU = LRU;
    }

    public double getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(double repairTime) {
        this.repairTime = repairTime;
    }

    public double getFaultDetectionRate() {
        return faultDetectionRate;
    }

    public void setFaultDetectionRate(double faultDetectionRate) {
        this.faultDetectionRate = faultDetectionRate;
    }

    public ArrayList<String> getProcessSeq() {
        return processSeq;
    }

    public void setProcessSeq(ArrayList<String> processSeq) {
        this.processSeq = processSeq;
    }

    public String getProcessNow() {
        return processNow;
    }

    public void setProcessNow(String processNow) {
        this.processNow = processNow;
    }

    public String[] getStateSeq() {
        return stateSeq;
    }

    public void setStateSeq(String[] stateSeq) {
        this.stateSeq = stateSeq;
    }

    public String[] getOccSeq() {
        return occSeq;
    }

    public void setOccSeq(String[] occSeq) {
        this.occSeq = occSeq;
    }

    public int getFailMoment() {
        return failMoment;
    }

    public void setFailMoment(int failMoment) {
        this.failMoment = failMoment;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
