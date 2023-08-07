package com.szu.cn.Security;

import java.io.Serializable;
import java.util.*;

public class Equipment implements Serializable {
    //装备名称
    private String name;

    //装备数量
    private int num;


    //装备状态集合
    private Equipmentenum status;


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

    //装备的工序序列<工序名,所需时间>
    private LinkedHashMap<String,Integer> processSeq;

    //装备,工序名，<资源名，需要资源数量>
    private LinkedHashMap<String,HashMap<String,Integer>> processAndResource;
    //装备,工序名，<资源名，优先级>
    private LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority;

    public LinkedHashMap<String, HashMap<String, Integer>> getProcessAndResourcePriority() {
        return processAndResourcePriority;
    }

    public void setProcessAndResourcePriority(LinkedHashMap<String, HashMap<String, Integer>> processAndResourcePriority) {
        this.processAndResourcePriority = processAndResourcePriority;
    }

    //装备当前工序
    private String processCur;

    //装备的状态序列
    private ArrayList<String> statusSeq;

    //装备的占用序列
    private ArrayList<String> usedSeq;

    //装备的故障时刻
    private int errorTime;

    //装备的真实故障却未被检出的标志
    private int errorBut;

    //processSeq_Origin是原始的processSeq
    private LinkedHashMap<String, Integer> processSeq_Origin;

    public Equipment() {
    }

    public Equipment(String e1, int i, LinkedHashMap<String, Integer> processSeq, LinkedHashMap<String, HashMap<String, Integer>> processAndResource) {
        this.name = e1;
        this.num = i;
        this.processSeq = processSeq;
        this.processAndResource = processAndResource;
        this.processCur = processSeq.entrySet().iterator().next().getKey();
        this.status = Equipmentenum.WAIT;
        this.processSeq_Origin = processSeq;
    }

    public LinkedHashMap<String, Integer> getProcessSeq_Origin() {
        return processSeq_Origin;
    }


    public void setProcessSeq_Origin(LinkedHashMap<String, Integer> processSeq_Origin) {
        this.processSeq_Origin = processSeq_Origin;
    }

    public void setProcessSeqTime(int totalTime) {
        for (Map.Entry<String,Integer> entry:this.processSeq.entrySet() ){
            if (entry.getKey().equals(this.processCur)){
                entry.setValue(entry.getValue()+totalTime);
            }
        }
    }

    public enum Equipmentenum {
        WAIT,
        RUN,
        FINISH,
        FIX
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

    //该装备当前工序完成时间（hgx）
    private int finishTime;
    //装备的当前状态（hgx）
    private String statusCur;

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public String getStatusCur() {
        return statusCur;
    }

    public void setStatusCur(String statusCur) {
        this.statusCur = statusCur;
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

    public LinkedHashMap<String, Integer> getProcessSeq() {
        return processSeq;
    }

    public void setProcessSeq(LinkedHashMap<String, Integer> processSeq) {
        this.processSeq = processSeq;
    }

    public LinkedHashMap<String, HashMap<String, Integer>> getProcessAndResource() {
        return processAndResource;
    }

    public void setProcessAndResource(LinkedHashMap<String, HashMap<String, Integer>> processAndResource) {
        this.processAndResource = processAndResource;
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

    public Equipmentenum getStatus() {
        return status;
    }

    public void setStatus(Equipmentenum status) {
        this.status = status;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getProcessCur() {
        return processCur;
    }

    public void setProcessCur(String processCur) {
        this.processCur = processCur;
    }

    public ArrayList<String> getStatusSeq() {
        return statusSeq;
    }

    public void setStatusSeq(ArrayList<String> statusSeq) {
        this.statusSeq = statusSeq;
    }

    public ArrayList<String> getUsedSeq() {
        return usedSeq;
    }

    public void setUsedSeq(ArrayList<String> usedSeq) {
        this.usedSeq = usedSeq;
    }

    public int getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(int errorTime) {
        this.errorTime = errorTime;
    }

    public int getErrorBut() {
        return errorBut;
    }

    public void setErrorBut(int errorBut) {
        this.errorBut = errorBut;
    }
}
