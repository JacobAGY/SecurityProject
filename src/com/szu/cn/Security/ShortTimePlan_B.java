package com.szu.cn.Security;

//import org.apache.commons.lang3.SerializationUtils;

import com.szu.cn.Security.Pojo.Equipment;
import com.szu.cn.Security.Pojo.Resource;
import com.szu.cn.Security.Pojo.Result;
import com.szu.cn.Security.utils.BeanUtils;

import java.io.IOException;
import java.util.*;

public class ShortTimePlan_B {

    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    private List<Resource> resourceListDetail;

    public ShortTimePlan_B(List<Equipment> equipmentList, List<Resource> resourceList) {
        this.equipmentList = equipmentList;
        this.resourceList=resourceList;
        List<Resource> tempList=new ArrayList<>();
        for (int i=0;i<resourceList.size();i++){
            for (int j=1;j<=resourceList.get(i).getNum();j++){
                Resource resource=new Resource(resourceList.get(i).getName()+"-"+j,1);
                tempList.add(resource);
            }
        }
        this.resourceListDetail=tempList;
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

    public List<Resource> getResourceListDetail() {
        return resourceListDetail;
    }

    public void setResourceListDetail(List<Resource> resourceListDetail) {
        this.resourceListDetail = resourceListDetail;
    }

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
        int finishedEqi = 0;
        // Group the equipments by their current process,为所有工序进行排序
        LinkedHashMap<String, List<Equipment>> equipmentGroups = groupEquipmentsByCurrentProcess();

        List<String> equipmentOrder=new ArrayList<>();
        // 记录修改后的时间
        List<Equipment> records_equipments = new ArrayList<>();
        while (!equipmentList.isEmpty()) {

            for (Map.Entry<String, List<Equipment>> entry:equipmentGroups.entrySet()){
                List<Equipment> toRemove = new ArrayList<>();
                for (Equipment ep:entry.getValue()){
                    //若当前工序的装备资源充足则执行
                     if ((ep.getProcessCur() != null && ep.getProcessCur().equals(entry.getKey())) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            && (checkResourceAvailability(ep)||isChangeable(ep))){
                        //分配资源，更新资源列表状态
                        allocateResources(ep);
                        ep.setStatus(Equipment.Equipmentenum.RUN);
                        ep.setProcessSeqTime(totalTime);
                        equipmentOrder.add(ep.getName()+"-"+getOriginProcess(ep,ep.getProcessCur())+":"+ep.getOccSeq());

                        System.out.println("调度"+ep.getName()+"工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime);
                    }else if ((ep.getProcessCur() != null && ep.getProcessCur().equals(entry.getKey())) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            &&checkResourcePriority(ep)){
                        //若当前工序有优先级高的资源，则提前占用
                        allocatePriyResources(ep);
                        System.out.println(ep.getName()+"占用资源"+ep.getOccSeq().toString()+"占用时间"+totalTime);
                    }else if ((ep.getProcessCur() != null && ep.getProcessCur().equals(entry.getKey())) &&ep.getStatus().equals(Equipment.Equipmentenum.RUN)
                            && ep.getProcessSeq().get(entry.getKey())==totalTime){
                        // 判断当前时间是否等于当前工序完成的时间，是代表完成当前工序，需要更改状态
                        //工序完成
                        ep.getFinished_Process().add(ep.getProcessCur());
                        ep.setStatus(Equipment.Equipmentenum.WAIT);
                        System.out.println("调度"+ep.getName()+"工序结束"+getOriginProcess(ep,ep.getProcessCur())+"结束时间"+totalTime);
                        //当前工序完成,释放资源并更新装备工序进度,并从工序待处理列表中移除
                        releaseResources(ep);
                        toRemove.add(ep);
                        //若装备完成所有工序，则从待处理列表中移除
                        if (ep.getProcessCur() == null){
                            //更新状态为Finish
                            ep.setStatus(Equipment.Equipmentenum.FINISH);
                            ep.setFinishTime(totalTime);
                            records_equipments.add(ep);
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
        System.out.println("Total time: " + totalTime);
        this.equipmentList=tempequipmentList;
        Result result=new Result(equipmentOrder,totalTime,records_equipments,finishedEqi);
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

        List<String> equipmentOrder=new ArrayList<>();
        // 记录修改后的时间
        List<Equipment> records_equipments = new ArrayList<>();
        while (totalTime <= maxTime && !equipmentList.isEmpty()) {

            for (Map.Entry<String, List<Equipment>> entry:equipmentGroups.entrySet()){
                List<Equipment> toRemove = new ArrayList<>();
                for (Equipment ep:entry.getValue()){
                    //若当前工序的装备资源充足则执行
                    if ((ep.getProcessCur() != null && ep.getProcessCur().equals(entry.getKey())) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            && (checkResourceAvailability(ep)|| isChangeable(ep))){
                        //分配资源，更新资源列表状态
                        allocateResources(ep);
                        ep.setStatus(Equipment.Equipmentenum.RUN);
                        ep.setProcessSeqTime(totalTime);
                        equipmentOrder.add(ep.getName()+"-"+getOriginProcess(ep,ep.getProcessCur())+":"+ep.getOccSeq());
                        System.out.println("调度"+ep.getName()+"工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime+"占用资源"+
                                ep.getOccSeq().toString());
                    }else if ((ep.getProcessCur() != null && ep.getProcessCur().equals(entry.getKey())) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            &&checkResourcePriority(ep)){
                        //若当前工序有优先级高的资源，则提前占用
                        allocatePriyResources(ep);
                        System.out.println(ep.getName()+"占用资源"+ep.getOccSeq().toString()+"占用时间"+totalTime);
                    }
                    else if ((ep.getProcessCur() != null && ep.getProcessCur().equals(entry.getKey())) &&ep.getStatus().equals(Equipment.Equipmentenum.RUN)
                            && ep.getProcessSeq().get(entry.getKey())==totalTime){
                        // 判断当前时间是否等于当前工序完成的时间，是代表完成当前工序，需要更改状态
                        //工序完成
                        ep.getFinished_Process().add(ep.getProcessCur());
                        ep.setStatus(Equipment.Equipmentenum.WAIT);
                        System.out.print("调度"+ep.getName()+"工序结束"+getOriginProcess(ep,ep.getProcessCur())+"结束时间"+totalTime);
                        //当前工序完成,释放资源并更新装备工序进度,并从工序待处理列表中移除
                        releaseResources(ep);
                        toRemove.add(ep);
                        //若装备完成所有工序，则从待处理列表中移除
                        if (ep.getProcessCur() == null){
                            //更新状态为Finish
                            ep.setStatus(Equipment.Equipmentenum.FINISH);
                            ep.setFinishTime(totalTime);
                            records_equipments.add(ep);
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
        Result result=new Result(equipmentOrder,totalTime,records_equipments,finishedEqi);
        this.equipmentList=tempequipmentList;
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



    public static void main(String[] args) {

        //初始化资源
        Resource resource1=new Resource("R1",4);
        Resource resource2=new Resource("R2",4);
        Resource resource3=new Resource("R3",2);
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

        //设置装备工序资源优先级
        LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority=new LinkedHashMap<>();

        processAndResourcePriority.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResourcePriority.put("P4",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResourcePriority.put("P5",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResourcePriority.put("P6",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResourcePriority.put("P7",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResourcePriority.put("P8",new HashMap<String,Integer>(){{put("R3",1);}});

        HashMap<String,ArrayList<String>> change_Process = new HashMap<>();

        Equipment ep1=new Equipment("E1",1, processSeq,processAndResource,processAndResourcePriority,change_Process);

        LinkedHashMap<String,Integer> processSeq2=new LinkedHashMap<>();
        processSeq2.put("P1",4);
        processSeq2.put("P2",5);
        processSeq2.put("P3",15);
        processSeq2.put("P4",12);
        processSeq2.put("P5",16);
        processSeq2.put("P6",20);

        LinkedHashMap<String,HashMap<String,Integer>> processAndResource2=new LinkedHashMap<>();
        processAndResource2.put("P1",new HashMap<String,Integer>(){{put("R2",1);}});
        processAndResource2.put("P2",new HashMap<String,Integer>(){{put("R1",1);put("R3",1);}});
        processAndResource2.put("P3",new HashMap<String,Integer>(){{put("R4",1);}});
        processAndResource2.put("P4",new HashMap<String,Integer>(){{put("R3",1);put("R4",1);}});
        processAndResource2.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
        processAndResource2.put("P6",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});

        Equipment ep2=new Equipment("E2",1, processSeq2,processAndResource2,change_Process);

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

        Equipment ep3=new Equipment("E3",1, processSeq3,processAndResource3,change_Process);

        LinkedHashMap<String,Integer> processSeq4=new LinkedHashMap<>();
        processSeq4.put("P1",6);
        processSeq4.put("P2",10);
        processSeq4.put("P3",15);
        processSeq4.put("P4",5);
        processSeq4.put("P5",12);
        processSeq4.put("P6",12);
        processSeq4.put("P7",6);

        LinkedHashMap<String,HashMap<String,Integer>> processAndResource4=new LinkedHashMap<>();
        processAndResource4.put("P1",new HashMap<String,Integer>(){{put("R1",1);}});
        processAndResource4.put("P2",new HashMap<String,Integer>(){{put("R2",1);put("R4",1);}});
        processAndResource4.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResource4.put("P4",new HashMap<String,Integer>(){{put("R4",1);put("R5",1);}});
        processAndResource4.put("P5",new HashMap<String,Integer>(){{put("R5",1);}});
        processAndResource4.put("P6",new HashMap<String,Integer>(){{put("R4",1);put("R6",1);}});
        processAndResource4.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});

        Equipment ep4=new Equipment("E4",1, processSeq4,processAndResource4,change_Process);

        equipmentList.add(ep1);
        equipmentList.add(ep2);
        equipmentList.add(ep3);
        equipmentList.add(ep4);

        ShortTimePlan_B scheduler = new ShortTimePlan_B(equipmentList,resourceList);
//        ShortTimePlan scheduler = new ShortTimePlan(equipmentList, processList, resourceList);
        scheduler.schedule(100);
    }


}
