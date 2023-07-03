package com.szu.cn.Security;

//import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.*;

public class ShortTimePlan {

    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    public ShortTimePlan(List<Equipment> equipmentList, List<Resource> resourceList) {
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

    public void setResourceListNum(int[] resourceList) {
        for (int i = 0; i < resourceList.length; i++) {
            this.resourceList.get(i).setNum(resourceList[i]);
        }
    }

    //所有装备完成时间
    public Result schedule() {

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
        initialEqi(equipmentList);
        int totalTime = 0;
        // Group the equipments by their current process,为所有工序进行排序
        LinkedHashMap<String, List<Equipment>> equipmentGroups = groupEquipmentsByCurrentProcess();

//        System.out.println("--------------调度顺序----------------");
//        List<List<Equipment>> templist=new ArrayList<>(equipmentGroups.values());
//        for(int i=0;i<templist.size();i++){
//            List<Equipment> temp=templist.get(i);
//            for (int j = 0; j < temp.size(); j++) {
//                System.out.print(temp.get(j).getName()+" ");
//            }
//            System.out.println("");
//        }
        List<String> equipmentOrder=new ArrayList<>();
        while (!equipmentList.isEmpty()) {

            for (Map.Entry<String, List<Equipment>> entry:equipmentGroups.entrySet()){
                List<Equipment> toRemove = new ArrayList<>();
                for (Equipment ep:entry.getValue()){
                    //若当前工序的装备资源充足则执行
                    if (ep.getProcessCur().equals(entry.getKey()) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            && checkResourceAvailability(ep)){
                        //分配资源，更新资源列表状态
                        allocateResources(ep);
                        ep.setStatus(Equipment.Equipmentenum.RUN);
                        ep.setProcessSeqTime(totalTime);
                        equipmentOrder.add(ep.getName()+"-"+getOriginProcess(ep,ep.getProcessCur()));
                        System.out.println("调度"+ep.getName()+"工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime);
                    }else if (ep.getProcessCur().equals(entry.getKey()) &&ep.getStatus().equals(Equipment.Equipmentenum.RUN)
                            && ep.getProcessSeq().get(entry.getKey())==totalTime){
                        // 判断当前时间是否等于当前工序完成的时间，是代表完成当前工序，需要更改状态
                        //工序完成
                        ep.setStatus(Equipment.Equipmentenum.WAIT);
                        System.out.println("调度"+ep.getName()+"工序结束"+getOriginProcess(ep,ep.getProcessCur())+"结束时间"+totalTime);
                        //当前工序完成,释放资源并更新装备工序进度,并从工序待处理列表中移除
                        releaseResources(ep);
                        toRemove.add(ep);
                        //若装备完成所有工序，则从待处理列表中移除
                        if (ep.getProcessCur() == null){
                            //更新状态为Finish
                            ep.setStatus(Equipment.Equipmentenum.FINISH);
                            equipmentList.remove(ep);
                        }
                    }
                }
                entry.getValue().removeAll(toRemove);
            }
            totalTime++;
        }

        totalTime--;
        System.out.println("Total time: " + totalTime);
        this.equipmentList=tempequipmentList;
        Result result=new Result(equipmentOrder,totalTime);
        return result;
    }
    //规定时间内完成装备数量
    public Result schedule(int maxTime){
        //对对象进行深拷贝
        //通过clone方式，把list01拷贝给list02
        List<Equipment> tempequipmentList = null;
        try {
            tempequipmentList = BeanUtils.deepCopy(equipmentList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        initialEqi(equipmentList);
        int totalTime = 0;
        //记录完成的装备数量
        int finishedEqi = 0;
        // Group the equipments by their current process,为所有工序进行排序
        LinkedHashMap<String, List<Equipment>> equipmentGroups = groupEquipmentsByCurrentProcess();

//        System.out.println("--------------调度顺序----------------");
//        List<List<Equipment>> templist=new ArrayList<>(equipmentGroups.values());
//        for(int i=0;i<templist.size();i++){
//            List<Equipment> temp=templist.get(i);
//            for (int j = 0; j < temp.size(); j++) {
//                System.out.print(temp.get(j).getName()+" ");
//            }
//            System.out.println("");
//        }
        List<String> equipmentOrder=new ArrayList<>();
        while (totalTime <= maxTime && !equipmentList.isEmpty()) {

            for (Map.Entry<String, List<Equipment>> entry:equipmentGroups.entrySet()){
                List<Equipment> toRemove = new ArrayList<>();
                for (Equipment ep:entry.getValue()){
                    //若当前工序的装备资源充足则执行
                    if (ep.getProcessCur().equals(entry.getKey()) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            && checkResourceAvailability(ep)){
                        //分配资源，更新资源列表状态
                        allocateResources(ep);
                        ep.setStatus(Equipment.Equipmentenum.RUN);
                        ep.setProcessSeqTime(totalTime);
                        equipmentOrder.add(ep.getName()+"-"+getOriginProcess(ep,ep.getProcessCur()));
                        System.out.println("调度"+ep.getName()+"工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime);
                    }else if (ep.getProcessCur().equals(entry.getKey()) &&ep.getStatus().equals(Equipment.Equipmentenum.RUN)
                            && ep.getProcessSeq().get(entry.getKey())==totalTime){
                        // 判断当前时间是否等于当前工序完成的时间，是代表完成当前工序，需要更改状态
                        //工序完成
                        ep.setStatus(Equipment.Equipmentenum.WAIT);
                        System.out.println("调度"+ep.getName()+"工序结束"+getOriginProcess(ep,ep.getProcessCur())+"结束时间"+totalTime);
                        //当前工序完成,释放资源并更新装备工序进度,并从工序待处理列表中移除
                        releaseResources(ep);
                        toRemove.add(ep);
                        //若装备完成所有工序，则从待处理列表中移除
                        if (ep.getProcessCur() == null){
                            //更新状态为Finish
                            ep.setStatus(Equipment.Equipmentenum.FINISH);
                            equipmentList.remove(ep);
                            finishedEqi++;
                        }
                    }
                }
                entry.getValue().removeAll(toRemove);
            }
            totalTime++;
        }

        totalTime--;
        System.out.println(maxTime + "min之内完成的装备个数为：" + finishedEqi);
        Result result=new Result(equipmentOrder,totalTime,finishedEqi);
        this.equipmentList=tempequipmentList;
        return result;
    }

    public void initialEqi(List<Equipment> equipmentList){
        //初始化装备工序（考虑工序顺序变化）
        for (Equipment epi: equipmentList) {
            int i=1;
            //工序映射P2->P1 P3->P2
            LinkedHashMap<String,Integer> tempProcessSeq=new LinkedHashMap<>();
            for (Map.Entry<String,Integer> entry:epi.getProcessSeq().entrySet()){
                tempProcessSeq.put("P"+i,entry.getValue());
                i++;
            }
            //保留最初顺序
            epi.setProcessSeq_Origin(epi.getProcessSeq());
            //设置算法工序顺序
            epi.setProcessSeq(tempProcessSeq);
            //设置装备当前工序
            epi.setProcessCur(tempProcessSeq.entrySet().iterator().next().getKey());
        }
    }

    //获取原始工序信息
    public String getOriginProcess(Equipment epi,String processcur){
        int index= Integer.parseInt(processcur.split("")[1]);
        int i=1;
        for (Map.Entry<String,Integer> entry:epi.getProcessSeq_Origin().entrySet()){
            if (index==i){return entry.getKey();}
            i++;
        }
        return "";
    }


    private LinkedHashMap<String, List<Equipment>> groupEquipmentsByCurrentProcess() {
        LinkedHashMap<String, List<Equipment>> equipmentGroups = new LinkedHashMap<>();

        int pSize=0;
        List<String> longestProcess=new ArrayList<>();
        //获取最长工序长度
        for (Equipment equipment : equipmentList) {
            if (equipment.getProcessSeq().size()>pSize){
                pSize=equipment.getProcessSeq().size();
                Set<String> temp=equipment.getProcessSeq().keySet();
                longestProcess=new ArrayList<>(temp);
            }
        }
        //
        for (String pName:longestProcess){
            List<Equipment> templist=new ArrayList<>();
            for (Equipment equipment : equipmentList){
                if (equipment.getProcessSeq().containsKey(pName)){
                    templist.add(equipment);
                }
            }
            equipmentGroups.put(pName,templist);
        }

        // 遍历equipmentGroups中的每个工序
        for (Map.Entry<String, List<Equipment>> entry : equipmentGroups.entrySet()) {
            // 使用自定义的比较器对每个工序的装备列表进行排序
            Collections.sort(entry.getValue(), new Comparator<Equipment>() {
                @Override
                public int compare(Equipment e1, Equipment e2) {
                    // 获取工序时间
                    int time1 = e1.getProcessSeq().get(entry.getKey());
                    int time2 = e2.getProcessSeq().get(entry.getKey());

                    // 按照工序时间升序排序
                    return Integer.compare(time1, time2);
                }
            });
        }
        return equipmentGroups;
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



    public static void main(String[] args) {

        //初始化资源
        Resource resource1=new Resource("R1",4);
        Resource resource2=new Resource("R2",4);
        Resource resource3=new Resource("R3",5);
        Resource resource4=new Resource("R4",5);
        Resource resource5=new Resource("R5",3);
        Resource resource6=new Resource("R6",2);
        Resource resource7=new Resource("R7",3);
        List<Resource> resourceList=new ArrayList<>();
        resourceList.add(resource1);
        resourceList.add(resource2);
        resourceList.add(resource3);
        resourceList.add(resource4);
        resourceList.add(resource5);
        resourceList.add(resource6);
        resourceList.add(resource7);

        //初始化装备
        List<Equipment> equipmentList=new ArrayList<>();
        LinkedHashMap<String,Integer> processSeq=new LinkedHashMap<>();
        processSeq.put("P1",5);
        processSeq.put("P2",4);
        processSeq.put("P3",6);
        processSeq.put("P4",10);
        processSeq.put("P5",15);
        processSeq.put("P6",8);
        processSeq.put("P7",15);
        processSeq.put("P8",6);
        LinkedHashMap<String,HashMap<String,Integer>> processAndResource=new LinkedHashMap<>();
        processAndResource.put("P1",new HashMap<String,Integer>(){{put("R1",1);}});
        processAndResource.put("P2",new HashMap<String,Integer>(){{put("R2",1);}});
        processAndResource.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResource.put("P4",new HashMap<String,Integer>(){{put("R3",1);put("R4",1);}});
        processAndResource.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
        processAndResource.put("P6",new HashMap<String,Integer>(){{put("R3",1);put("R6",1);}});
        processAndResource.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});
        processAndResource.put("P8",new HashMap<String,Integer>(){{put("R3",1);}});

        Equipment ep1=new Equipment("E1",1, processSeq,processAndResource);

        LinkedHashMap<String,Integer> processSeq2=new LinkedHashMap<>();
        processSeq2.put("P6",20);
        processSeq2.put("P5",16);
        processSeq2.put("P4",12);
        processSeq2.put("P3",15);
        processSeq2.put("P2",5);
        processSeq2.put("P1",4);

        LinkedHashMap<String,HashMap<String,Integer>> processAndResource2=new LinkedHashMap<>();
        processAndResource2.put("P1",new HashMap<String,Integer>(){{put("R2",1);}});
        processAndResource2.put("P2",new HashMap<String,Integer>(){{put("R1",1);}});
        processAndResource2.put("P3",new HashMap<String,Integer>(){{put("R4",1);}});
        processAndResource2.put("P4",new HashMap<String,Integer>(){{put("R3",1);put("R4",1);}});
        processAndResource2.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
        processAndResource2.put("P6",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});

        Equipment ep2=new Equipment("E2",1, processSeq2,processAndResource2);

        LinkedHashMap<String,Integer> processSeq3=new LinkedHashMap<>();
        processSeq3.put("P1",4);
        processSeq3.put("P2",8);
        processSeq3.put("P3",12);
        processSeq3.put("P4",5);
        processSeq3.put("P5",10);
        processSeq3.put("P6",12);
        processSeq3.put("P7",6);
        processSeq3.put("P8",8);
        processSeq3.put("P9",10);
        LinkedHashMap<String,HashMap<String,Integer>> processAndResource3=new LinkedHashMap<>();
        processAndResource3.put("P1",new HashMap<String,Integer>(){{put("R2",1);}});
        processAndResource3.put("P2",new HashMap<String,Integer>(){{put("R1",1);put("R4",1);}});
        processAndResource3.put("P3",new HashMap<String,Integer>(){{put("R4",1);put("R5",1);}});
        processAndResource3.put("P4",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResource3.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});
        processAndResource3.put("P6",new HashMap<String,Integer>(){{put("R4",1);put("R6",1);}});
        processAndResource3.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
        processAndResource3.put("P8",new HashMap<String,Integer>(){{put("R5",1);put("R7",1);}});
        processAndResource3.put("P9",new HashMap<String,Integer>(){{put("R3",1);put("R6",1);}});

        Equipment ep3=new Equipment("E3",1, processSeq3,processAndResource3);

        LinkedHashMap<String,Integer> processSeq4=new LinkedHashMap<>();
        processSeq4.put("P3",6);
        processSeq4.put("P6",10);
        processSeq4.put("P7",15);
        processSeq4.put("P1",5);
        processSeq4.put("P2",12);
        processSeq4.put("P5",12);
        processSeq4.put("P4",6);

        LinkedHashMap<String,HashMap<String,Integer>> processAndResource4=new LinkedHashMap<>();
        processAndResource4.put("P1",new HashMap<String,Integer>(){{put("R1",1);}});
        processAndResource4.put("P2",new HashMap<String,Integer>(){{put("R2",1);put("R4",1);}});
        processAndResource4.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResource4.put("P4",new HashMap<String,Integer>(){{put("R4",1);put("R5",1);}});
        processAndResource4.put("P5",new HashMap<String,Integer>(){{put("R5",1);}});
        processAndResource4.put("P6",new HashMap<String,Integer>(){{put("R4",1);put("R6",1);}});
        processAndResource4.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});

        Equipment ep4=new Equipment("E4",1, processSeq4,processAndResource4);

        equipmentList.add(ep1);
        equipmentList.add(ep2);
        equipmentList.add(ep3);
        equipmentList.add(ep4);

        ShortTimePlan scheduler = new ShortTimePlan(equipmentList,resourceList);
//        ShortTimePlan scheduler = new ShortTimePlan(equipmentList, processList, resourceList);
        scheduler.schedule(100);
    }


}