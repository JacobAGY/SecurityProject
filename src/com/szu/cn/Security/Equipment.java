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

    private Equipmentenum substatus;

    //装备的结构组成
    private ArrayList<String> composition;

    //装备故障率
    private double failRate;

    //装备各组成单元及其可靠性指标
    private HashMap<String,Double> failMap;

    //LRU及其故障检测率
    private HashMap<String,Double> errorMap;

    //装备的LRU
    private List<String> LRU;

    //装备LRU的平均维修时间
    private HashMap<String,Integer> LRUrepairTime;

    //装备可靠性指标
    private double ReliabilityRate;

    //装备LRU的故障检测率
    private double faultDetectionRate;
    //标示哪个工序为检修工序
    private String fixprocess;

    //装备的工序序列<工序名,所需时间>
    private LinkedHashMap<String,Integer> processSeq;

    //装备,工序名，<资源名，需要资源数量>
    private LinkedHashMap<String,HashMap<String,Integer>> processAndResource;
    //装备,工序名，<资源名，优先级>
    private LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority;

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

    //change_Process记录每个装备中可换的工序
    private HashMap<String,ArrayList<String>> change_Process;

    //finished_Process记录已经完成的工序
    private ArrayList<String> finished_Process;
    public Equipment() {
    }
    public Equipment(String e1, int i, LinkedHashMap<String, Integer> processSeq, LinkedHashMap<String, HashMap<String, Integer>> processAndResource) {
        this.name = e1;
        this.num = i;
        this.processSeq_Origin = processSeq;
        this.processSeq=processSeq;
        this.processAndResource = processAndResource;
        this.processCur = processSeq.entrySet().iterator().next().getKey();
        this.status = Equipmentenum.WAIT;
        this.occSeq=new ArrayList<>();
        this.finished_Process = new ArrayList<>();
    }

    public Equipment(String e1, int i, LinkedHashMap<String, Integer> processSeq, LinkedHashMap<String, HashMap<String, Integer>> processAndResource,HashMap<String,ArrayList<String>> change_Process) {
        this.name = e1;
        this.num = i;
        this.processSeq_Origin = processSeq;
        this.processSeq=processSeq;
        this.processAndResource = processAndResource;
        this.processCur = processSeq.entrySet().iterator().next().getKey();
        this.status = Equipmentenum.WAIT;
        this.processSeq_Origin = processSeq;
        this.occSeq=new ArrayList<>();
        this.change_Process = change_Process;
        this.finished_Process = new ArrayList<>();
    }

    public Equipment(String e1, int i, LinkedHashMap<String, Integer> processSeq, LinkedHashMap<String, HashMap<String, Integer>> processAndResource,LinkedHashMap<String, HashMap<String, Integer>> prcessAndResoursePriy) {
        this.name = e1;
        this.num = i;
        this.processSeq_Origin = processSeq;
        this.processSeq=processSeq;
        this.processAndResource = processAndResource;
        this.processCur = processSeq.entrySet().iterator().next().getKey();
        this.status = Equipmentenum.WAIT;
        this.processSeq_Origin = processSeq;
        this.occSeq=new ArrayList<>();
        this.processAndResourcePriority=prcessAndResoursePriy;
    }
    public Equipment(String e1, int i, LinkedHashMap<String, Integer> processSeq, LinkedHashMap<String, HashMap<String, Integer>> processAndResource,LinkedHashMap<String, HashMap<String, Integer>> prcessAndResoursePriy,HashMap<String,ArrayList<String>> change_Process) {
        this.name = e1;
        this.num = i;
        this.processSeq_Origin = processSeq;
        this.processSeq=processSeq;
        this.processAndResource = processAndResource;
        this.processCur = processSeq.entrySet().iterator().next().getKey();
        this.status = Equipmentenum.WAIT;
        this.processSeq_Origin = processSeq;
        this.occSeq=new ArrayList<>();
        this.processAndResourcePriority=prcessAndResoursePriy;
        this.change_Process = change_Process;
        this.finished_Process = new ArrayList<>();
    }
    public Equipment(String e1, int i, LinkedHashMap<String, Integer> processSeq, LinkedHashMap<String, HashMap<String, Integer>> processAndResource,
                     LinkedHashMap<String, HashMap<String, Integer>> prcessAndResoursePriy,HashMap<String,Double> failmap,
                     HashMap<String,Double> errorMap,List<String> lru,HashMap<String,Integer> repairTime,String fixprocess) {
        this.name = e1;
        this.num = i;
        this.processSeq_Origin = processSeq;
        this.processSeq=processSeq;
        this.processAndResource = processAndResource;
        this.processCur = processSeq.entrySet().iterator().next().getKey();
        this.status = Equipmentenum.WAIT;
        this.processSeq_Origin = processSeq;
        this.occSeq=new ArrayList<>();
        this.processAndResourcePriority=prcessAndResoursePriy;
        this.failMap=failmap;
        this.errorMap=errorMap;
        this.LRU=lru;
        this.LRUrepairTime=repairTime;
        this.substatus=null;
        this.fixprocess=fixprocess;
        this.finished_Process = new ArrayList<>();
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
        FIX,
        AvailableAndKnown,
        UnavailableAndKnown,
        UnAvailableAndUnknown,
        FixtoAvailableAndKnown
    }

    //装备的状态序列
    private String[] stateSeq;

    //装备的占用信息
    private List<String> occSeq;

    //装备的故障时刻
    private int failMoment;

    //装备的真实状态却未被检测出的标志
    private int flag;

    //该装备当前工序完成时间（hgx）
    private int finishTime;
    //装备的当前状态（hgx）
    private String statusCur;

    public double getReliabilityRate() {
        return ReliabilityRate;
    }

    public void setReliabilityRate(double reliabilityRate) {
        ReliabilityRate = reliabilityRate;
    }

    public String getFixprocess() {
        return fixprocess;
    }

    public void setFixprocess(String fixprocess) {
        this.fixprocess = fixprocess;
    }

    public Equipmentenum getSubstatus() {
        return substatus;
    }

    public void setSubstatus(Equipmentenum substatus) {
        this.substatus = substatus;
    }

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

    public HashMap<String, Double> getFailMap() {
        return failMap;
    }

    public void setFailMap(HashMap<String, Double> failMap) {
        this.failMap = failMap;
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

    public String[] getStateSeq() {
        return stateSeq;
    }

    public void setStateSeq(String[] stateSeq) {
        this.stateSeq = stateSeq;
    }

    public LinkedHashMap<String, HashMap<String, Integer>> getProcessAndResourcePriority() {
        return processAndResourcePriority;
    }

    public void setProcessAndResourcePriority(LinkedHashMap<String, HashMap<String, Integer>> processAndResourcePriority) {
        this.processAndResourcePriority = processAndResourcePriority;
    }

    public List<String> getOccSeq() {
        return occSeq;
    }

    public void setOccSeq(List<String> occSeq) {
        this.occSeq = occSeq;
    }

    public HashMap<String, Double> getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(HashMap<String, Double> errorMap) {
        this.errorMap = errorMap;
    }

    public List<String> getLRU() {
        return LRU;
    }

    public void setLRU(List<String> LRU) {
        this.LRU = LRU;
    }

    public HashMap<String, Integer> getLRUrepairTime() {
        return LRUrepairTime;
    }

    public void setLRUrepairTime(HashMap<String, Integer> LRUrepairTime) {
        this.LRUrepairTime = LRUrepairTime;
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

    public HashMap<String, ArrayList<String>> getChange_Process() {
        return change_Process;
    }

    public void setChange_Process(HashMap<String, ArrayList<String>> change_Process) {
        this.change_Process = change_Process;
    }

    public ArrayList<String> getFinished_Process() {
        return finished_Process;
    }

    public void setFinished_Process(ArrayList<String> finished_Process) {
        this.finished_Process = finished_Process;
    }
}
