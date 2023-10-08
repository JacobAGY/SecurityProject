package com.szu.cn.Security;

import com.szu.cn.Security.Pojo.Equipment;
import com.szu.cn.Security.Pojo.Resource;
import com.szu.cn.Security.Pojo.Result;
import com.szu.cn.Security.utils.BeanUtils;

import java.io.IOException;
import java.util.*;

public class SequentialPlan {
    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    private List<Resource> resourceListDetail;

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

    public SequentialPlan(List<Equipment> equipmentList, List<Resource> resourceList) {
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

        // 记录修改后的时间
        List<Equipment> records_equipments = new ArrayList<>();
        while (finish_flag < tempequipmentList.size()) {
            int len = tempequipmentList.size();
            //遍历tempequipmentList，判断每个装备的当前工序资源是否满足，满足则执行
//            for (Equipment e:tempequipmentList) {
            for (int i = 0; i < len; i++) {
                Equipment e = tempequipmentList.get(i);
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e)) {
                        /**
                         * 如果资源充足或工序可交换且资源充足，执行
                         * 1.修改工序状态
                         */
                        allocateResources(e);
                        e.setStatus(Equipment.Equipmentenum.RUN);
                        e.setProcessSeqTime(totalTime);
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur()+":"+e.getOccSeq());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);

                    }else{
                        if(checkResourcePriority(e)){
                            allocatePriyResources(e);
                            System.out.println(e.getName()+"占用资源"+e.getOccSeq().toString()+"占用时间"+totalTime);
                        }
                        /**
                         * 如果资源不充足，跳出到下一个装备
                         */
                        continue;
                    }
                }else if (e.getStatus().equals(Equipment.Equipmentenum.RUN) && e.getProcessSeq().get(e.getProcessCur()) == totalTime) {
                    /*
                        当前工序已完成
                        1.将已完成的工序加入Finished_process
                        2.修改状态为等待状态或完成状态
                        3.将run_flag置为true，表示有工序完成
                     */
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    releaseResources(e);
                    i--;
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        e.setFinishTime(totalTime);
                        records_equipments.add(e);
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
//        totalTime--;
        System.out.println("完成装备的数量为: " + finish_flag);
        this.equipmentList = tempequipmentList;
        Result result = new Result(equipmentOrder,records_equipments, totalTime);
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
        // 记录修改后的时间
        List<Equipment> records_equipments = new ArrayList<>();
        while (totalTime <= maxTime && finish_flag < tempequipmentList.size()) {
            int len = tempequipmentList.size();
            //遍历tempequipmentList，判断每个装备的当前工序资源是否满足，满足则执行
//            for (Equipment e:tempequipmentList) {
            for (int i = 0; i < len; i++) {
                Equipment e = tempequipmentList.get(i);
                if (e.getStatus().equals(Equipment.Equipmentenum.WAIT)) {
                    if (checkResourceAvailability(e)) {
                        /**
                         * 如果资源充足或者可变换的工序资源充足，执行
                         * 1.修改工序状态
                         * 2.修改工序需要完成的时间
                         */
                        allocateResources(e);
                        e.setStatus(Equipment.Equipmentenum.RUN);
                        e.setProcessSeqTime(totalTime);
                        equipmentOrder.add(e.getName() + "-" + e.getProcessCur()+":"+e.getOccSeq());
                        System.out.println("调度" + e.getName() + "工序开始" + e.getProcessCur() + "开始时间" + totalTime);
                    }else{
                        if(checkResourcePriority(e)){
                            allocatePriyResources(e);
                            System.out.println(e.getName()+"占用资源"+e.getOccSeq().toString()+"占用时间"+totalTime);
                        }
                        /**
                         * 如果资源不充足，跳出到下一个装备
                         */
                        continue;
                    }
                }else if (e.getStatus().equals(Equipment.Equipmentenum.RUN) && e.getProcessSeq().get(e.getProcessCur()) == totalTime) {
                    /*
                        当前工序已完成
                        1.修改状态为等待状态或完成状态
                        2.需要预判下一工序是否还需要相同资源，需要的话则不释放该资源
                        3.增加已完成的工序
                     */
                    e.setStatus(Equipment.Equipmentenum.WAIT);
                    releaseResources(e);
                    i--;
                    //若该装备完成所有工序
                    if (e.getProcessCur() == null) {
                        //更新状态为Finish
                        e.setStatus(Equipment.Equipmentenum.FINISH);
                        records_equipments.add(e);
                        e.setFinishTime(totalTime);
                        finish_flag += 1;
                    }
                }
            }
            totalTime++;
        }
//        totalTime--;
        System.out.println(maxTime + "min之内完成的装备个数为：" + finish_flag);
        Result result=new Result(equipmentOrder,totalTime,records_equipments,finish_flag);
//        this.equipmentList=tempequipmentList;
        return result;
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


    public Resource findDetailResource(String name){
        for (Resource resource : resourceListDetail) {
            if (resource.getName().split("-")[0].equals(name)&&resource.getState().equals(Resource.status.wait)){
                return resource;
            }
        }
        return null;
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
            if (resource!=null&&r!=null){
                resource.setNum(resource.getNum()-entry.getValue());
                //分配资源给装备
                equipment.getOccSeq().add(r.getName());
                //设置资源状态
                r.setState(Resource.status.running);
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
    public Resource findResourcebyName(String name){
        for (Resource resource : resourceListDetail) {
            if (resource.getName().equals(name)){
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
