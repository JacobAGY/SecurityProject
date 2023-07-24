package com.szu.cn.Security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequentialPlan {
    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    public SequentialPlan(List<Equipment> equipmentList, List<Resource> resourceList) {
        this.equipmentList = equipmentList;
        this.resourceList = resourceList;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    /**
     * 常规方法，
     * 判断当前装备工序是否资源充足，充足则执行；
     * 不充足则跳到下一个装备。
     */

    //所有装备完成的时间
    public Result schedule() {
        int totalTime = 0;
        List<String> equipmentOrder = new ArrayList<>();

        //对对象进行深拷贝
        //通过clone方式，把list01拷贝给list02
        List<Equipment> tempequipmentList = null;
        try {
            tempequipmentList = BeanUtils.deepCopy(this.equipmentList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        /**
         * run_flag用于判断是否当前时间有完成的工序，若有，时间需要回溯
         * 因为原文中两道工序是可以接着做的，如第一道工序在第5秒完成后，第五秒可以作为第二道工序的开始时间
          */

        boolean run_flag = false;
        int finish_flag = 0;
        while (finish_flag < tempequipmentList.size()) {
            //遍历tempequipmentList，判断每个装备的当前工序资源是否满足，满足则执行
            for (Equipment e:tempequipmentList) {
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e)) {
                        /**
                         * 如果资源充足，执行
                         * 1.修改工序状态
                         */
                        allocateResources(e);
                        e.setStatus(Equipment.Equipmentenum.RUN);
                        e.setProcessSeqTime(totalTime);
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    }else{
                        /**
                         * 如果资源不充足，跳出到下一个装备
                         */
                        continue;
                    }
                }else if (e.getStatus().equals(Equipment.Equipmentenum.RUN) && e.getProcessSeq().get(e.getProcessCur()) == totalTime) {
                    /*
                        当前工序已完成
                        1.修改状态为等待状态或完成状态
                        2.将run_flag置为true，表示有工序完成
                     */
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    releaseResources(e);
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        finish_flag += 1;
                    }
                    run_flag = true;
                }
            }
            if(run_flag){
                totalTime--;
                run_flag = false;
            }
            totalTime++;
        }
        totalTime--;
        System.out.println("完成装备的数量为: " + finish_flag);
        Result result = new Result(equipmentOrder, totalTime);
        return result;
    }

    //规定时间内完成的装备
    public Result schedule(int maxTime) {
        int totalTime = 0;
        List<String> equipmentOrder = new ArrayList<>();

        //对对象进行深拷贝
        //通过clone方式，把list01拷贝给list02
        List<Equipment> tempequipmentList = null;
        try {
            tempequipmentList = BeanUtils.deepCopy(this.equipmentList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int finish_flag = 0;
        while (totalTime <= maxTime && finish_flag < tempequipmentList.size()) {
            //遍历tempequipmentList，判断每个装备的当前工序资源是否满足，满足则执行
            for (Equipment e:tempequipmentList) {
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e)) {
                        /**
                         * 如果资源充足，执行
                         * 1.修改工序状态
                         */
                        allocateResources(e);
                        e.setStatus(Equipment.Equipmentenum.RUN);
                        e.setProcessSeqTime(totalTime);
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    }
                }else if (e.getStatus().equals(Equipment.Equipmentenum.RUN) && e.getProcessSeq().get(e.getProcessCur()) == totalTime) {
                    /*
                        当前工序已完成
                        1.修改状态为等待状态或完成状态
                        2.需要预判下一工序是否还需要相同资源，需要的话则不释放该资源
                     */
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    releaseResources(e);
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        finish_flag += 1;
                    }
                    totalTime--;
                    break;
                }
            }
            totalTime++;
        }
        totalTime--;
        System.out.println(maxTime + "min之内完成的装备个数为：" + finish_flag);
        Result result=new Result(equipmentOrder,totalTime,finish_flag);
//        this.equipmentList=tempequipmentList;
        return result;
    }
    private boolean checkResourceAvailability(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        Map<String,Integer> resources=equipment.getProcessAndResource().get(curProcess);
        for (Map.Entry<String,Integer> entry: resources.entrySet()){
            for(Resource resource:resourceList){
                if (resource.getName().equals(entry.getKey())&&resource.getNum()<entry.getValue()){
                    return false;
                }
            }
        }
        return true;
    }

    private void allocateResources(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(curProcess);
        //为工序分配资源，资源数量减少
        for (Map.Entry<String,Integer> entry:pr.entrySet()){
            Resource resource=findResource(entry.getKey());
            resource.setNum(resource.getNum()-entry.getValue());
        }
    }
    public Resource findResource(String name){
        for (Resource resource : resourceList) {
            if (resource.getName().equals(name)){
                return resource;
            }
        }
        return null;
    }

    private void releaseResources(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(curProcess);
        //工序释放资源，资源数量增加
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

}
