package com.szu.cn.Security;

import com.szu.cn.Security.Pojo.Equipment;
import com.szu.cn.Security.Pojo.Resource;
import com.szu.cn.Security.Pojo.Result;

import java.util.*;

public class HighResponseRatioPlan_B {

    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    private List<Resource> resourceListDetail;

    private int[] currentWaitTime;
    private double[] currentRatio;
    private int[] timeLeft;

    public void setResourceListNum(int[] resourceList) {
        for (int i = 0; i < resourceList.length; i++) {
            this.resourceList.get(i).setNum(resourceList[i]);
        }
        List<Resource> tempList=new ArrayList<>();
        for (int i=0;i<this.resourceList.size();i++){
            for (int j=1;j<=this.resourceList.get(i).getNum();j++){
                Resource resource=new Resource(this.resourceList.get(i).getName()+"-"+j,1);
                tempList.add(resource);
            }
        }
        this.resourceListDetail=tempList;
    }

    public HighResponseRatioPlan_B(List<Equipment> equipmentList, List<Resource> resourceList) {
        this.equipmentList = equipmentList;
        this.resourceList = resourceList;
        List<Resource> tempList=new ArrayList<>();
        for (int i=0;i<resourceList.size();i++){
            for (int j=1;j<=resourceList.get(i).getNum();j++){
                Resource resource=new Resource(resourceList.get(i).getName()+"-"+j,1);
                tempList.add(resource);
            }
        }
        this.resourceListDetail=tempList;
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

    //所有装备完成的时间
    public Result schedule() {
        int totalTime = 0;
        List<String> equipmentOrder = new ArrayList<>();

        int finishedEqi = 0;
        // 记录修改后的时间
        List<Equipment> records_equipments = new ArrayList<>();
        while (finishedEqi < currentRatio.length) {
            updateRatio();
            //返回当前响应比由高到低的顺序，响应比相同返回剩余时间短的,返回结果为一个数组（每个装备对应的下标）
            int[] arr = groupEquipmentByHighResponse(currentRatio, timeLeft);
            //根据剩余时间高响应比返回当前装备排序
            for (int i = 0; i < arr.length; i++) {
                int index = arr[i];//index是下标
                Equipment e = equipmentList.get(index);
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e) || isChangeable(e)) {
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
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur()+":"+e.getOccSeq());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    } else {
                        if(checkResourcePriority(e)){
                            allocatePriyResources(e);
                            System.out.println(e.getName()+"占用资源"+e.getOccSeq().toString()+"占用时间"+totalTime);
                        }
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
                    e.getFinished_Process().add(e.getProcessCur());
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    releaseResources(e);
                    i--;
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        records_equipments.add(e);
                        finishedEqi += 1;
                    }
                }
            }

            totalTime++;
        }
        totalTime--;
        System.out.println("Total time: " + totalTime);
        Result result = new Result(equipmentOrder, totalTime,records_equipments,finishedEqi);
        return result;
    }

    //规定时间完成的装备数量
    public Result schedule(int maxTime) {
        //记录完成的装备数量
        int finishedEqi = 0;
        int totalTime = 0;
        List<String> equipmentOrder = new ArrayList<>();
        // 记录修改后的时间
        List<Equipment> records_equipments = new ArrayList<>();
        while (totalTime <= maxTime && finishedEqi < currentRatio.length) {
            updateRatio();
            //返回当前响应比由高到低的顺序，响应比相同返回剩余时间短的,返回结果为一个数组（每个装备对应的下标）
            int[] arr = groupEquipmentByHighResponse(currentRatio, timeLeft);
            //根据剩余时间高响应比返回当前装备排序
            for (int i = 0; i < arr.length; i++) {
                int index = arr[i];//index是下标
                Equipment e = equipmentList.get(index);
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e) || isChangeable(e)) {
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
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur()+":"+e.getOccSeq());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    } else {
                        if(checkResourcePriority(e)){
                            allocatePriyResources(e);
                            System.out.println(e.getName()+"占用资源"+e.getOccSeq().toString()+"占用时间"+totalTime);
                        }
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
                    e.getFinished_Process().add(e.getProcessCur());
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    timeLeft[index] -= e.getProcessSeq_Origin().get(e.getProcessCur());
                    releaseResources(e);
                    i--;
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        records_equipments.add(e);
                        finishedEqi++;
                    }
                }
            }

            totalTime++;
        }
        totalTime--;
        System.out.println("完成装备的数量为: " + finishedEqi);
        Result result = new Result(equipmentOrder, totalTime,records_equipments,finishedEqi);
        return result;
    }
    /**
     * 本方法用于检查可变工序是否资源充足
     * @param equipment
     * @param change_Process
     * @return
     */
    private boolean checkResourceAvailability(Equipment equipment,String change_Process) {
        String curProcess = change_Process;
        Map<String,Integer> resources=equipment.getProcessAndResource().get(getOriginProcess(equipment,curProcess));
        for (Map.Entry<String,Integer> entry: resources.entrySet()){
            for(Resource resource:resourceList){
                //若为所需要的资源种类
                if (resource.getName().equals(entry.getKey())){
                    int needNum=entry.getValue();
                    //检查是否已经占用该资源种类
                    if (equipment.getOccSeq().size()>0){
                        for (String r:equipment.getOccSeq()){
                            if (r.split("-")[0].equals(entry.getKey())){
                                needNum--;
                            }
                        }
                    }
                    //检查资源是否足够
                    if (resource.getNum()<needNum){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * isChangeable，若当前工序资源不足，且可交换，交换后资源充足，则重新制定顺序。
     * @param equipment
     * @return
     */
    private boolean isChangeable(Equipment equipment) {
        // 如果equipment的change_Process找不到对应的process名称 代表没有可换工序
        if(equipment.getChange_Process().containsKey(equipment.getProcessCur())){
            // 遍历当前可换工序，判断该工序是否已经做过
            ArrayList<String> change_list = equipment.getChange_Process().get(equipment.getProcessCur());
            for (String change_process: change_list) {
                if(equipment.getFinished_Process().contains(change_process)){
                    // 该工序做过，则跳出
                    continue;
                }else{
                    // 没做过，则判断资源是否充足，充足则更换当前工序，包括processSeq（装备顺序）
                    // 高响应比算法比较特殊，需要修改timeLeft
                    if(checkResourceAvailability(equipment,change_process)){
                        // LinkedHashMap没有内置的替代key的方法，只能新建一个LinkedHashMap代替
                        LinkedHashMap<String,Integer> change_processSeq=new LinkedHashMap<>();
                        LinkedHashMap<String,Integer> processSeq =  equipment.getProcessSeq();
                        String cur_Process = equipment.getProcessCur();

                        for (String key_process:processSeq.keySet()) {
                            if(key_process.equals(cur_Process)){
                                change_processSeq.put(change_process,processSeq.get(change_process));
                            }else if(key_process.equals(change_process)){
                                change_processSeq.put(cur_Process,processSeq.get(cur_Process));
                            }else{
                                change_processSeq.put(key_process,processSeq.get(key_process));
                            }
                        }
                        equipment.setProcessSeq(change_processSeq);
                        equipment.setProcessCur(change_process);
                        return true;

                    }
                }
            }
        }
        return false;
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
                //若为所需要的资源种类
                if (resource.getName().equals(entry.getKey())){
                    int needNum=entry.getValue();
                    //检查是否已经占用该资源种类
                    if (equipment.getOccSeq().size()>0){
                        for (String r:equipment.getOccSeq()){
                            if (r.split("-")[0].equals(entry.getKey())){
                                needNum--;
                            }
                        }
                    }
                    //检查资源是否足够
                    if (resource.getNum()<needNum){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void allocateResources(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(getOriginProcess(equipment,curProcess));
        //为工序分配资源，资源数量减少
        for (Map.Entry<String,Integer> entry:pr.entrySet()){
            //获取所需资源种类
            Resource resource=findResource(entry.getKey());
            //获取确定的资源
            Resource r=findDetailResource(entry.getKey());

            //检查已有资源并更新需求数量
            if (equipment.getOccSeq().size()>0){
                boolean Have=false;
                for (String temp:equipment.getOccSeq()){
                    if (temp.split("-")[0].equals(resource.getName())) Have=true;
                }
                if (Have) continue;
            }
            //将资源种类的数量-1
            if (resource!=null){
                resource.setNum(resource.getNum()-entry.getValue());
            }
            if (r!=null){
                //分配资源给装备
                equipment.getOccSeq().add(r.getName());
                //设置资源状态
                r.setState(Resource.status.running);
            }
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

    public Resource findResource(String name){
        for (Resource resource : resourceList) {
            if (resource.getName().equals(name)){
                return resource;
            }
        }
        return null;
    }

    public Resource findResourcebyName(String name){
        for (Resource resource : resourceListDetail) {
            if (resource.getName().equals(name)){
                return resource;
            }
        }
        return null;
    }
    public Resource findDetailResource(String name){
        for (Resource resource : resourceListDetail) {
            if (resource.getName().split("-")[0].equals(name)&&resource.getState().equals(Resource.status.wait)){
                return resource;
            }
        }
        return null;
    }

    private void releaseResources(Equipment equipment) {

        //设置装备的当前工序为下一道工序(当前程序下的工序顺序)
        String last="";
        for (Map.Entry<String,Integer>entry:equipment.getProcessSeq().entrySet()){
            if (last.equals(equipment.getProcessCur())){
                String nextProcess=entry.getKey();
                equipment.setProcessCur(nextProcess);
//                entry.setValue(entry.getValue()+curTime);
                //若下一道工序使用相同的资源则不释放
                List<String> toremove=new ArrayList<>();
                for (String r:equipment.getOccSeq()){
                    boolean enable=false;
                    for (Map.Entry<String,Integer>entry1:equipment.getProcessAndResource().get(getOriginProcess(equipment,nextProcess)).entrySet()){
                        if (r.split("-")[0].equals(entry1.getKey())){
                            enable=true;
                        }
                    }
                    //若下一道工序不同资源则释放资源
                    if (!enable){
                        //在占用序列中清理该资源
                        toremove.add(r);
                        //设置资源状态
                        Resource resource=findResourcebyName(r);
                        resource.setState(Resource.status.wait);
                        //设置资源种类数量
                        Resource resourcetype=findResource(r.split("-")[0]);
                        //TODO 需要扩展资源数大于1时的资源变更
                        resourcetype.setNum(resourcetype.getNum()+1);
                    }
                }
                equipment.getOccSeq().removeAll(toremove);
                System.out.println("释放资源 "+toremove.toString());
                return;
            }
            last=entry.getKey();
        }
        //结束工序并释放所有资源
        HashMap<String,Integer> out_resources=equipment.getProcessAndResource().get(equipment.getProcessCur());
        for (String r:equipment.getOccSeq()){
            Resource resource=findResourcebyName(r);
            resource.setState(Resource.status.wait);
        }
        for (Map.Entry<String,Integer> entry:out_resources.entrySet()){
            Resource resourcetype=findResource(entry.getKey());
            resourcetype.setNum(resourcetype.getNum()+entry.getValue());
        }
        System.out.println("释放资源 "+equipment.getOccSeq().toString());
        equipment.getOccSeq().clear();
        equipment.setProcessCur(null);
    }
    private boolean checkResourcePriority(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        //若当前工序有资源优先级，则获取<资源名，优先级>
        if (equipment.getProcessAndResource().containsKey(curProcess)){
            Map<String,Integer> resourceAndPriy=equipment.getProcessAndResource().get(curProcess);
            for (Map.Entry<String,Integer> entry: resourceAndPriy.entrySet()){
                //判断是否有该优先级资源
                for(Resource resource:resourceList){
                    if (resource.getName().equals(entry.getKey())&&entry.getValue()>0&&resource.getNum()<1){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    private void allocatePriyResources(Equipment equipment) {
        String curProcess=equipment.getProcessCur();
        HashMap<String,Integer> prp=equipment.getProcessAndResourcePriority().get(getOriginProcess(equipment,curProcess));
        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(getOriginProcess(equipment,curProcess));
        //为工序分配资源，资源数量减少
        for (Map.Entry<String,Integer> entry:prp.entrySet()){
            if (entry.getValue()>1){
                //获取所需资源种类
                Resource resource=findResource(entry.getKey());
                //获取确定的资源
                Resource r=findDetailResource(entry.getKey());

                if (equipment.getOccSeq().size()>0){
                    boolean Have=false;
                    for (String temp:equipment.getOccSeq()){
                        if (temp.split("-")[0].equals(resource.getName())) Have=true;
                    }
                    if (Have) continue;
                }

                if (resource!=null&&r!=null){
                    //将资源种类的数量-1
                    resource.setNum(resource.getNum()-pr.get(entry.getKey()));
                    //分配资源给装备
                    equipment.getOccSeq().add(r.getName());
                    //设置资源状态
                    r.setState(Resource.status.running);
                }
            }
        }
    }


}