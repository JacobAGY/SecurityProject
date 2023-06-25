package com.szu.cn.Security;

import java.util.*;
import java.util.stream.Collectors;

public class HighResponseRatioPlan {

    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    private int[] currentWaitTime;
    private double[] currentRatio;
    private int[] timeLeft;

    public HighResponseRatioPlan(List<Equipment> equipmentList, List<Resource> resourceList) {
        this.equipmentList = equipmentList;
        this.resourceList = resourceList;

        //所有装备当前等待时间
        currentWaitTime = new int[equipmentList.size()];

        //所有装备当前响应比
        currentRatio = new double[equipmentList.size()];

        //所有装备剩余时间
        timeLeft = new int[equipmentList.size()];
        initial();

    }

    //初始化装备剩余时间
    public void initial() {
        for (int i = 0; i < currentWaitTime.length; i++) {
            //所有工序所需时间总和
            int temp = 0;
            for (Integer process_time : equipmentList.get(i).getProcessSeq().values()) {
                temp += process_time;
            }
            currentWaitTime[i] = 0;
            timeLeft[i] = temp;

        }
    }

    //所有装备完成的时间
    public Result schedule() {
        int totalTime = 0;
        List<String> equipmentOrder = new ArrayList<>();

        int finish_flag = 0;
        while (finish_flag < currentRatio.length) {
            updateRatio();
            //返回当前响应比由高到低的顺序，响应比相同返回剩余时间短的,返回结果为一个数组（每个装备对应的下标）
            int[] arr = groupEquipmentByHighResponse(currentRatio, timeLeft);
            //根据剩余时间高响应比返回当前装备排序
            for (int i = 0; i < arr.length; i++) {
                int index = arr[i];//index是下标
                Equipment e = equipmentList.get(index);
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e)) {
                        /*
                            如果当前响应比最高的资源充足
                            1.分配资源，修改资源的数量
                            2.将装备状态修改为RUN
                            3.修改SeqTime，作为工序完成标志
                            4.将该下标等待时间修改为0，以及修改该下标响应比为1
                         */
                        allocateResources(e);
                        e.setStatus(Equipment.Equipmentenum.RUN);
                        e.setProcessSeqTime(totalTime);
                        currentWaitTime[index] = 0;
                        currentRatio[index] = 1;
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    } else {
                        /*
                            如果当前资源不足
                            1.等待时间+1
                         */
                        currentWaitTime[index] += 1;
                    }
                } else if (e.getStatus().equals(Equipment.Equipmentenum.RUN) && e.getProcessSeq().get(e.getProcessCur()) == totalTime) {
                    /*
                        当前工序已完成
                        1.修改状态为等待状态
                        2.更新剩余时间,减去当前工序所需时间
                        3.释放资源有一个特殊要求（若同一装备两个工序可以紧连着做且需要同一资源，不可重新调配该资源的其他机器做下一工序）
                            1）预评估下一个工序是否需要用到该资源，不需要则释放资源
                            2）需要则进行判断下一工序是否会在下一个totalTime执行，不会则释放资源
                            3）会则需要锁定资源，即将该下一工序所需资源--
                     */
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    releaseResources(e);
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        finish_flag += 1;
                    }
                }
            }

            totalTime++;
        }
        totalTime--;
        System.out.println("Total time: " + totalTime);
        Result result = new Result(equipmentOrder, totalTime);
        return result;
    }

    //规定时间完成的装备数量
    public Result schedule(int maxTime) {
        //记录完成的装备数量
        int finishedEqi = 0;
        int totalTime = 0;
        List<String> equipmentOrder = new ArrayList<>();

        int finish_flag = 0;
        while (totalTime <= maxTime && finish_flag < currentRatio.length) {
            updateRatio();
            //返回当前响应比由高到低的顺序，响应比相同返回剩余时间短的,返回结果为一个数组（每个装备对应的下标）
            int[] arr = groupEquipmentByHighResponse(currentRatio, timeLeft);
            //根据剩余时间高响应比返回当前装备排序
            for (int i = 0; i < arr.length; i++) {
                int index = arr[i];//index是下标
                Equipment e = equipmentList.get(index);
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e)) {
                        /*
                            如果当前响应比最高的资源充足
                            1.分配资源，修改资源的数量
                            2.将装备状态修改为RUN
                            3.修改SeqTime，作为工序完成标志
                            4.将该下标等待时间修改为0，以及修改该下标响应比为1
                         */
                        allocateResources(e);
                        e.setStatus(Equipment.Equipmentenum.RUN);
                        e.setProcessSeqTime(totalTime);
                        currentWaitTime[index] = 0;
                        currentRatio[index] = 1;
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    } else {
                        /*
                            如果当前资源不足
                            1.等待时间+1
                         */
                        currentWaitTime[index] += 1;
                    }
                } else if (e.getStatus().equals(Equipment.Equipmentenum.RUN) && e.getProcessSeq().get(e.getProcessCur()) == totalTime) {
                    /*
                        当前工序已完成
                        1.修改状态为等待状态
                        2.更新剩余时间,减去当前工序所需时间
                        3.释放资源有一个特殊要求（若同一装备两个工序可以紧连着做且需要同一资源，不可重新调配该资源的其他机器做下一工序）
                            1）预评估下一个工序是否需要用到该资源，不需要则释放资源
                            2）需要则进行判断下一工序是否会在下一个totalTime执行，不会则释放资源
                            3）会则需要锁定资源，即将该下一工序所需资源--
                     */
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    releaseResources(e);
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        finish_flag += 1;
                        finishedEqi++;
                    }
                }
            }

            totalTime++;
        }
        totalTime--;
        System.out.println("完成装备的数量为: " + finishedEqi);
        Result result = new Result(equipmentOrder, totalTime);
        return result;
    }

    /**
     * 根据剩余时间高响应比返回集合
     * 判断响应比，响应比大的在前面，如果响应比相同，以剩余时间少的为先
     * @return
     */
    private int[] groupEquipmentByHighResponse (double[] currentRatio,int[] timeLeft) {
        List<Integer> index_list = new ArrayList<>();
        for (int i = 0; i < currentRatio.length; i++) {
            index_list.add(i);
        }
        int[] arr = new int[currentRatio.length];
        int tmp = 0;
        while(index_list.size() > 0){
            int min = 0;
            for (int i = 1; i < index_list.size(); i++) {
                int index1 = index_list.get(min);
                int index2 = index_list.get(i);
                if(currentRatio[index1] < currentRatio[index2]){
                    //如果当前元素响应比小，则交换
                    min = i;
                } else if (currentRatio[index1] == currentRatio[index2]) {
                    //如果当前元素响应比一样大，比较剩余时间，剩余时间少则交换
                    if(timeLeft[index1] > timeLeft[index2]){
                        min = i;
                    }
                }
            }
            //记录arr
            arr[tmp++] = index_list.get(min);
            index_list.remove(min);
        }

        return arr;
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

    private void updateRatio(){
        //更新响应比
        for (int i = 0; i < currentWaitTime.length; i++) {
            //更新响应比
            if(timeLeft[i] > 0){
                currentRatio[i] = (currentWaitTime[i] + timeLeft[i])/timeLeft[i];
            }

        }
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
//    private void releaseResources(Equipment equipment) {
//        /*
//            释放资源有一个特殊要求（若同一装备两个工序可以紧连着做且需要同一资源，不可重新调配该资源的其他机器做下一工序）
//            1）预评估下一个工序是否需要用到该资源，不需要则释放资源
//            2）需要则进行判断下一工序是否会在下一个totalTime执行，不会则释放资源
//            3）会则需要锁定资源，即将该下一工序所需资源--
//         */
//        String curProcess=equipment.getProcessCur();
//        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(curProcess);
//        //工序释放资源，资源数量增加
////        for (Map.Entry<String,Integer> entry:pr.entrySet()){
////            Resource resource=findResource(entry.getKey());
////            resource.setNum(resource.getNum()+entry.getValue());
////        }
//
//        //设置装备的当前工序为下一道工序
//        String last = "";
//        for (Map.Entry<String,Integer>entry:equipment.getProcessSeq().entrySet()){
//            if (last.equals(equipment.getProcessCur())){
//                String next = entry.getKey();
//                equipment.setProcessCur(next);
//                /*
//                 1）预评估下一个工序是否需要用到该资源，不需要则释放资源
//                 */
//                HashMap<String,Integer> processAndResource1 = equipment.getProcessAndResource().get(last);
//                HashMap<String,Integer> processAndResource2 = equipment.getProcessAndResource().get(next);
//                //工序释放资源，资源数量增加
//                for (Map.Entry<String,Integer> entry1:processAndResource1.entrySet()){
//                    String resource1 = entry1.getKey();
//                    if(!processAndResource2.containsKey(resource1)){
//                        //如果下一工序不需要当前工序该资源，释放
//                        Resource resource=findResource(resource1);
//                        resource.setNum(resource.getNum()+entry1.getValue());
//                    }else{
//                        //如果需要，先判断会不会在下一个totalTime执行
//
//                    }
//
//                }
//
//
////                entry.setValue(entry.getValue()+curTime);
//                return;
//            }
//            last=entry.getKey();
//        }
//        equipment.setProcessCur(null);
//    }

    public Resource findResource(String name){
        for (Resource resource : resourceList) {
            if (resource.getName().equals(name)){
                return resource;
            }
        }
        return null;
    }

}