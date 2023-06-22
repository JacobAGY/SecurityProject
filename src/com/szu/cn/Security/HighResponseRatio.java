package com.szu.cn.Security;

import java.util.*;

public class HighResponseRatio {
    private List<Equipment> equipmentList;
    private List<Process> processList;
    private List<Resource> resourceList;

    private int[] currentWaitTime;
    private double[] currentRatio;
    private int[] timeLeft;

    public HighResponseRatio(List<Equipment> equipmentList, List<Resource> resourceList) {
        this.equipmentList = equipmentList;
        this.resourceList = resourceList;

        //所有装备当前等待时间
        currentWaitTime =new int[equipmentList.size()];

        //所有装备当前响应比
        currentRatio = new double[equipmentList.size()];

        //所有装备剩余时间
        timeLeft = new int[equipmentList.size()];
        initiaTimeLeft();
    }

    //规定时间内能完成的最多装备数量
    public void schedule(int maxTime){
        int totalTime = 0;
        //记录时间
//        int currentTime = 0;

        //记录完成的装备数量
        int finishedEqi = 0;

        for (int i = 0; i < maxTime; i++) {

            //更新资源和装备状态
            finishedEqi += updateResource(i);
            if(finishedEqi == equipmentList.size()){
                //全部完成，返回
                System.out.println("完成时间为" + i + "分钟,完成装备数量为" + finishedEqi + "个");
            }

            //更新所有装备当前响应比
            updateRatio();

            //找到当前可处理的最高响应比的装备
            Equipment temp = null;
            while((temp = findHighestRatioEqi()) != null){

                //进行处理
                //等待时间置为0
                currentWaitTime[i] = 0;
                //更改资源数量
                Process process = findProcess(temp.getProcessCur());
                for (Map.Entry<String,Integer> entry : process.getResourceSeq().entrySet()) {
                    //找到匹配资源，更改数量
                    Resource resource = findResource(entry.getKey());
                    resource.setNum(resource.getNum()-entry.getValue());
                }
                //更改装备状态
                temp.setStatusCur("running");
                //记录何时完成,当前时间加所需时间
                temp.setFinishTime(i + process.getTime());
            }
        }

        System.out.println("完成时间为" + maxTime + "分钟,完成装备数量为" + finishedEqi + "个");
    }

    //更新等待时间以及响应比
    public void updateRatio(){

        for (int i = 0; i < currentWaitTime.length; i++) {
            //如果该装备当前状态为等待，等待时间+1
            if (equipmentList.get(i).getStatusCur().equals("wait")){
                currentWaitTime[i] += 1;
                //更新响应比
                currentRatio[i] = (currentWaitTime[i] + timeLeft[i])/timeLeft[i];
            }
        }
    }

    //初始化装备剩余时间
    public void initiaTimeLeft(){
        for (int i = 0; i < currentWaitTime.length; i++) {
            //所有工序所需时间总和
            int temp = 0;
            for (Integer process_time : equipmentList.get(i).getProcessSeq().values()) {
                temp += process_time;
            }
            timeLeft[i] = temp;
        }
    }

    //找到当前可处理的最高响应比的装备
    public Equipment findHighestRatioEqi(){

        double maxRatio = -1;
        Equipment equipment = null;

        //遍历装备列表
        for (int i = 0; i < equipmentList.size(); i++) {
            Equipment temp = equipmentList.get(i);

            //该装备状态非等待
            if (!temp.getStatusCur().equals("wait")) continue;

            //响应比大于最大响应比时，判断是否更新
            if(currentRatio[i] >= maxRatio){

                //如果响应比相同，对比工序时长
                if (currentRatio[i] == maxRatio){
                    //找到process
                    Process tempProcess = findProcess(temp.getProcessCur());
                    Process reProcess = findProcess(equipment.getProcessCur());

                    if(tempProcess.getTime() >= reProcess.getTime()) continue;

                }

                //如果可处理，更新equipment
                boolean flag = true;
                String processCur = temp.getProcessCur();
                for (Process process : processList) {
                    if (process.getName().equals(processCur)){
                        for (Map.Entry<String,Integer> entry : process.getResourceSeq().entrySet()) {
                            //如果所有资源需求可以满足，表示可处理
                            //找到匹配资源
                            for (int j = 0; j < resourceList.size(); j++) {
                                if(resourceList.get(j).getName().equals(entry.getKey())){
                                    int avaNum = resourceList.get(j).getNum();

                                    //数量不足，标志置位false
                                    if(avaNum < entry.getValue()) {
                                        flag = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (flag) {
                            //满足条件，更新equipment
                            equipment = temp;
                            break;
                        } else{
                            //不满足，直接break
                            break;
                        }
                    }
                }
            }
        }

        //没有找到 返回空
        return equipment;
    }

    public Process findProcess(String name){
        for (Process process : processList) {
            if(process.getName().equals(name)){
                return  process;
            }
        }
        return null;
    }

    public Resource findResource(String name){
        for (Resource resource : resourceList) {
            if (resource.getName().equals(name)){
                return resource;
            }
        }
        return null;
    }

    //释放资源
    private void releaseResources(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(curProcess);
        //为工序分配资源，资源数量减少
        for (Map.Entry<String,Integer> entry:pr.entrySet()){
            Resource resource=findResource(entry.getKey());
            resource.setNum(resource.getNum()+entry.getValue());
        }

        //设置装备的当前工序为下一道工序
        String last="";
        for (Map.Entry<String,Integer>entry:equipment.getProcessSeq().entrySet()){
            if (last.equals(equipment.getProcessCur())){
                equipment.setProcessCur(entry.getKey());
//                entry.setValue(entry.getValue()+curTime);
                return;
            }
            last=entry.getKey();
        }
        equipment.setProcessCur(null);
    }

    public int updateResource(int currentTime){

        int finished = 0;

        //遍历装备，寻找是否有完成当前工序的装备
        for (Equipment equipment : equipmentList) {

            //工序完成
            if (equipment.getFinishTime() == currentTime){
                releaseResources(equipment);
                //释放资源
//                Process process = findProcess(equipment.getProcessCur());
                //找到所有工序所需资源
                LinkedHashMap<String,HashMap<String,Integer>> processAndResource = equipment.getProcessAndResource();
                for (Map.Entry<String,HashMap<String,Integer>> entry : processAndResource.entrySet()) {
                    //找到匹配资源，更改数量
                    for (Map.Entry<String,Integer> resource_entry : entry.getValue().entrySet()) {
                        Resource resource = findResource(resource_entry.getKey());
                        resource.setNum(resource.getNum() + resource_entry.getValue());
                    }
                }
                //更改processCur
                LinkedHashMap<String,Integer> processSeq = equipment.getProcessSeq();
                Iterator<Map.Entry<String, Integer>> it = processSeq.entrySet().iterator();
                while (it.hasNext()){
                    equipment.setProcessCur(it.next().getKey());
                }
                finished += 1;
                equipment.setStatusCur("finish");
//                for (int i = 0; i < processSeq.size(); i++) {
//                    if (i == processSeq.size()-1){
//                        //已完成
//                        finished += 1;
//                        equipment.setStatusCur("finish");
//                        break;
//                    }
//                    if (processSeq.get(i).equals(equipment.getProcessCur())){
//                        equipment.setProcessCur(processSeq.get(i+1));
//                    }
//                }
            }
        }
        return finished;
    }
}