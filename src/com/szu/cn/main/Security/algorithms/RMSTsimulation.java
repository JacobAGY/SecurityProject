package com.szu.cn.main.Security.algorithms;



import com.szu.cn.main.Security.pojo.BasePojo;
import com.szu.cn.main.Security.pojo.Equipment;
import com.szu.cn.main.Security.pojo.Resource;
import com.szu.cn.main.Security.pojo.Result;
import com.szu.cn.main.Security.utils.BeanUtils;

import java.io.IOException;
import java.util.*;

import static com.szu.cn.main.Security.pojo.BasePojo.PROCESS_PREFIX;


public class RMSTsimulation {

    private List<Equipment> equipmentListType;
    private List<Equipment> equipmentList;
    private List<Resource> resourceList;

    private HashMap<String,Integer> unitList;
    private List<Resource> resourceListDetail;

    public RMSTsimulation(List<Equipment> equipmentList, List<Resource> resourceList,HashMap<String,Integer> unitList) {
        this.equipmentList=equipmentList;
        this.resourceList=resourceList;
        List<Resource> tempList=new ArrayList<>();
        for (int i=0;i<resourceList.size();i++){
            for (int j=1;j<=resourceList.get(i).getNum();j++){
                Resource resource=new Resource(resourceList.get(i).getName()+"-"+j,1);
                tempList.add(resource);
            }
        }
        this.resourceListDetail=tempList;
        this.unitList=unitList;
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

    public HashMap<String, Integer> getUnitList() {
        return unitList;
    }

    public void setUnitList(HashMap<String, Integer> unitList) {
        this.unitList = unitList;
    }

    public void setResourceListDetail(List<Resource> resourceListDetail) {
        this.resourceListDetail = resourceListDetail;
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
        //保存整体装备数量
        initialEqi(equipmentList);
        int totalTime = 0;
        //记录
        int finishedEqi = 0;
        int failedEqi=0;

        // Group the equipments by their current process,为所有工序进行排序
        LinkedHashMap<String, List<Equipment>> equipmentGroups = groupEquipmentsByCurrentProcess();

//        System.out.println("--------------调度顺序----------------");
//        List<List<Equipment>> templist=new ArrayList<>(equipmentGroups.values());
//        for(int i=0;i<templist.size();i++){"P3" -> {ArrayList@859}  size = 2
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
                        equipmentOrder.add(ep.getName()+"-"+getOriginProcess(ep,ep.getProcessCur()));
                        //进入检修工序
                        if (getOriginProcess(ep,ep.getProcessCur()).equals(ep.getFixprocess())){
                            String repairLRU=epFixProcess(ep);
                            if (repairLRU!=null){
                                //存在需要维修的组件
                                int errortime=RandomInteger(ep.getProcessSeq().get(ep.getProcessCur()));
                                //设置维修时间
                                ep.getProcessSeq().put(ep.getProcessCur(),ep.getLRUrepairTime().get(repairLRU)+totalTime+errortime);
                                System.out.println("调度"+ep.getName()+"检修工序开始"+getOriginProcess(ep,ep.getProcessCur())+"故障单元"+repairLRU+"开始时间"+totalTime+"占用资源"+
                                        ep.getOccSeq().toString());
                            }else if (ep.getSubstatus().equals(Equipment.Equipmentenum.UnavailableAndKnown) ||
                                    ep.getSubstatus().equals(Equipment.Equipmentenum.UnAvailableAndUnknown))
                            {//发现故障但是无法修复
                                int errortime=RandomInteger(ep.getProcessSeq().get(ep.getProcessCur()));
                                //设置装备状态
                                ep.setErrorBut(1);
                                failedEqi++;
                                ep.getProcessSeq().put(ep.getProcessCur(),totalTime+errortime);
                                ep.setErrorTime(totalTime+errortime);
                                System.out.println("调度"+ep.getName()+"检修工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime+"占用资源"+ ep.getOccSeq().toString());
                            }else {
                                //无故障
                                ep.getProcessSeq().put(ep.getProcessCur(),ep.getProcessSeq().get(ep.getProcessCur())+totalTime);
                                System.out.println("调度"+ep.getName()+"检修工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime+"占用资源"+
                                        ep.getOccSeq().toString());
                            }
                        }else {
                            ep.getProcessSeq().put(ep.getProcessCur(),ep.getProcessSeq().get(ep.getProcessCur())+totalTime);
                            System.out.println("调度"+ep.getName()+"工序开始"+getOriginProcess(ep,ep.getProcessCur())+"开始时间"+totalTime+"占用资源"+
                                    ep.getOccSeq().toString());
                        }
                    }else if (ep.getProcessCur().equals(entry.getKey()) &&ep.getStatus().equals(Equipment.Equipmentenum.WAIT)
                            &&checkResourcePriority(ep)){
                        //若当前工序有优先级高的资源，则提前占用
                        allocatePriyResources(ep);
                        System.out.println(ep.getName()+"占用资源"+ep.getOccSeq().toString()+"占用时间"+totalTime);
                    }
                    else if (ep.getProcessCur().equals(entry.getKey()) &&ep.getStatus().equals(Equipment.Equipmentenum.RUN)
                            && ep.getProcessSeq().get(entry.getKey())==totalTime){
                        // 判断当前时间是否等于当前工序完成的时间，是代表完成当前工序，需要更改状态
                        //工序完成
                        ep.setStatus(Equipment.Equipmentenum.WAIT);
                        System.out.print("调度"+ep.getName()+"工序结束"+getOriginProcess(ep,ep.getProcessCur())+"结束时间"+totalTime);
                        //当前工序完成,释放资源并更新装备工序进度,并从工序待处理列表中移除
                        releaseResources(ep);
                        toRemove.add(ep);
                        //若装备完成所有工序，则从待处理列表中移除
                        if (ep.getProcessCur() == null){
                            //更新状态为Finish
                            ep.setStatus(Equipment.Equipmentenum.FINISH);
                            equipmentList.remove(ep);
                            if (ep.getErrorBut() != 1){
                                finishedEqi++;
                            }
                        }
                        if (ep.getSubstatus()!=null&&ep.getSubstatus().equals(Equipment.Equipmentenum.FixtoAvailableAndKnown)){
                            //维修好的装备需重新加入eqigroup中
                            insertFixEpi(ep,equipmentGroups);
                            //不从工序中移除该装备
                            toRemove.remove(ep);
                            //重置substatus状态
                            ep.setSubstatus(null);
                        }
                    }
                }
                entry.getValue().removeAll(toRemove);
            }
            totalTime++;
        }
        totalTime--;
        double usability= (finishedEqi-failedEqi)*1.0/finishedEqi;
        System.out.println(maxTime + "min之内失败的装备个数为：" + failedEqi);
        System.out.println(maxTime + "min之内完成的装备个数为：" + finishedEqi);
        System.out.println(maxTime+"min之内的可用度为："+ usability);
        Result result=new Result(equipmentOrder,totalTime,finishedEqi,failedEqi,usability);
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
                tempProcessSeq.put(PROCESS_PREFIX+i,entry.getValue());
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
        String[] indexs=processcur.split("");
        int index= Integer.parseInt(indexs[indexs.length-1]);
        int i=1;
        for (Map.Entry<String,Integer> entry:epi.getProcessSeq_Origin().entrySet()){
            if (index==i){return entry.getKey();}
            i++;
        }
        return "";
    }

    private void insertFixEpi(Equipment ep,LinkedHashMap<String, List<Equipment>> epsgroup){
        //将ep工序（非origin）的时间还原
        List<Integer> valuesFrom_origin = new ArrayList<>(ep.getProcessSeq_Origin().values());
        int index = 0;
        for (String key : ep.getProcessSeq().keySet()) {
            if (index < valuesFrom_origin.size()) {
                ep.getProcessSeq().put(key, valuesFrom_origin.get(index));
                index++;
            }
        }
        //将ep重新插入调度队列并根据工作时间排序
        for (Map.Entry<String,Integer> entry:ep.getProcessSeq().entrySet()){
            String key=entry.getKey();
            if (key.equals(ep.getFixprocess())){
                break;
            }
            if(epsgroup.containsKey(key)){
                epsgroup.get(key).add(ep);
                Collections.sort(epsgroup.get(key), new Comparator<Equipment>(){
                    @Override
                    public int compare(Equipment ep1, Equipment ep2) {
                        int value1 = ep1.getProcessSeq().getOrDefault(key, 0);
                        int value2 = ep2.getProcessSeq().getOrDefault(key, 0);
                        return Integer.compare(value1, value2);
                    }
                });
            }
        }
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
            if (resource!=null&&r!=null){
                resource.setNum(resource.getNum()-entry.getValue());
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
            if (entry.getValue()>0){
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
        HashMap<String,Integer> pr=equipment.getProcessAndResource().get(getOriginProcess(equipment,equipment.getProcessCur()));
        if (getOriginProcess(equipment,equipment.getProcessCur()).equals(equipment.getFixprocess())&&!
                equipment.getSubstatus().equals(Equipment.Equipmentenum.AvailableAndKnown)){
            //TODO
            //维修完毕后释放所有资源
            for (Map.Entry<String,Integer> entry:pr.entrySet()){
                Resource resourcetype=findResource(entry.getKey());
                resourcetype.setNum(resourcetype.getNum()+entry.getValue());
            }
            //修改资源状态
            for (String r:equipment.getOccSeq()){
                Resource resource=findResourcebyName(r);
                resource.setState(Resource.status.wait);
            }
            System.out.println("释放资源 "+equipment.getOccSeq().toString());
            equipment.getOccSeq().clear();
            //设置装备的工序从头开始
            equipment.setProcessCur(equipment.getProcessSeq().entrySet().iterator().next().getKey());
            return;
        }

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

    private String epFixProcess(Equipment equipment){
        //可靠性检测->是否发生故障
        for (Map.Entry<String,Double> unit:equipment.getFailMap().entrySet()){
            double failRate=RandomDouble();
            if (failRate>unit.getValue()) {
                //故障检测->是否能检测出故障
                //发生故障，检测故障单元
                if (equipment.getErrorMap().containsKey(unit.getKey())) {
                    double errorRate=RandomDouble();
                    String lru=unit.getKey();
                    if (errorRate>equipment.getErrorMap().get(lru)){
                        //检测出故障且可替换
                        if (unitList.get(unit.getKey())!=null&&unitList.get(unit.getKey())>0){
                            //如果有备件替换
                            unitList.put(lru,unitList.get(lru)-1);
                            equipment.setSubstatus(Equipment.Equipmentenum.FixtoAvailableAndKnown);
                            System.out.println("故障装备:"+equipment.getName()+" 故障单元:"+lru+"进入维修状态");
                            return lru;
                        }else {
                            //无法更换备件
                            equipment.setSubstatus(Equipment.Equipmentenum.UnavailableAndKnown);
                            System.out.println("故障装备:"+equipment.getName()+" 故障单元:"+lru+" 无法替换部件");
                            return null;
                        }
                    }else {
                        //未检测出故障->不可用且未知
                        equipment.setSubstatus(Equipment.Equipmentenum.UnAvailableAndUnknown);
                        return null;
                    }
                }else {
                    //出了故障但无法替换->不可用且未知
                    equipment.setSubstatus(Equipment.Equipmentenum.UnAvailableAndUnknown);
                    return null;
                }
            }
        }
        //未发生故障->可用且已知模式
        equipment.setSubstatus(Equipment.Equipmentenum.AvailableAndKnown);
        return null;
    }

    private double RandomDouble(){
        //生成[0,1)内的伪随机数
        Random random=new Random();
        return random.nextDouble();
    }

    private int RandomInteger(int t){
        //生成[1,t]内的伪随机数
        Random random=new Random();
        return random.nextInt(t)+1;
    }

    public static void main(String[] args) {

        //初始化资源
//        Resource resource1=new Resource("R1",5);
//        Resource resource2=new Resource("R2",5);
//        Resource resource3=new Resource("R3",5);
//        Resource resource4=new Resource("R4",5);
//        Resource resource5=new Resource("R5",5);
//        Resource resource6=new Resource("R6",5);
//        Resource resource7=new Resource("R7",5);
//        List<Resource> resourceList=new ArrayList<>();
//        resourceList.add(resource1);
//        resourceList.add(resource2);
//        resourceList.add(resource3);
//        resourceList.add(resource4);
//        resourceList.add(resource5);
//        resourceList.add(resource6);
//        resourceList.add(resource7);
//
//        //初始化装备
//        List<Equipment> equipmentList=new ArrayList<>();
//        LinkedHashMap<String,Integer> processSeq=new LinkedHashMap<>();
//        processSeq.put("P1",5);
//        processSeq.put("P2",4);
//        processSeq.put("P3",6);
//        processSeq.put("P4",10);
//        processSeq.put("P5",15);
//        processSeq.put("P6",8);
//        processSeq.put("P7",15);
//        processSeq.put("P8",6);
//        LinkedHashMap<String,HashMap<String,Integer>> processAndResource=new LinkedHashMap<>();
//        processAndResource.put("P1",new HashMap<String,Integer>(){{put("R1",1);}});
//        processAndResource.put("P2",new HashMap<String,Integer>(){{put("R2",1);}});
//        processAndResource.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
//        processAndResource.put("P4",new HashMap<String,Integer>(){{put("R3",1);put("R4",1);}});
//        processAndResource.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
//        processAndResource.put("P6",new HashMap<String,Integer>(){{put("R3",1);put("R6",1);}});
//        processAndResource.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});
//        processAndResource.put("P8",new HashMap<String,Integer>(){{put("R3",1);}});
//
//        //设置装备工序资源优先级
//        LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority=new LinkedHashMap<>();
//
//        processAndResourcePriority.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
//        processAndResourcePriority.put("P4",new HashMap<String,Integer>(){{put("R3",1);}});
//        processAndResourcePriority.put("P5",new HashMap<String,Integer>(){{put("R3",1);}});
//        processAndResourcePriority.put("P6",new HashMap<String,Integer>(){{put("R3",1);}});
//        processAndResourcePriority.put("P7",new HashMap<String,Integer>(){{put("R3",1);}});
//        processAndResourcePriority.put("P8",new HashMap<String,Integer>(){{put("R3",1);}});
//
//        HashMap<String,Double> failmap=new HashMap<>();
//        failmap.put("单元1",0.95);
//        failmap.put("单元2",0.975);
//        failmap.put("单元3",0.96);
//        failmap.put("单元4",0.93);
//
//        HashMap<String,Double> errormap=new HashMap<>();
//        errormap.put("单元1",0.82);
//        errormap.put("单元3",0.78);
//
//        List<String> Lru=new ArrayList<String>(){{
//            add("单元1");
//            add("单元3");
//        }};
//
//        HashMap<String,Integer> repairTime=new HashMap<>();
//        repairTime.put("单元1",10);
//        repairTime.put("单元3",15);
//
//        String fixProcess="P6";
//        Equipment ep1=new Equipment("E1",2, processSeq,processAndResource,processAndResourcePriority,failmap,errormap,Lru,repairTime,fixProcess);
//
//        LinkedHashMap<String,Integer> processSeq2=new LinkedHashMap<>();
//        processSeq2.put("P1",4);
//        processSeq2.put("P2",5);
//        processSeq2.put("P3",15);
//        processSeq2.put("P4",12);
//        processSeq2.put("P5",16);
//        processSeq2.put("P6",20);
//
//        LinkedHashMap<String,HashMap<String,Integer>> processAndResource2=new LinkedHashMap<>();
//        processAndResource2.put("P1",new HashMap<String,Integer>(){{put("R2",1);}});
//        processAndResource2.put("P2",new HashMap<String,Integer>(){{put("R1",1);put("R3",1);}});
//        processAndResource2.put("P3",new HashMap<String,Integer>(){{put("R4",1);}});
//        processAndResource2.put("P4",new HashMap<String,Integer>(){{put("R3",1);put("R4",1);}});
//        processAndResource2.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
//        processAndResource2.put("P6",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});
//
//        //设置装备工序资源优先级
//        LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority2=new LinkedHashMap<>();
//
//
//        HashMap<String,Double> failmap2=new HashMap<>();
//        failmap2.put("单元1",0.95);
//        failmap2.put("单元2",0.975);
//        failmap2.put("单元3",0.96);
//        failmap2.put("单元4",0.93);
//
//        HashMap<String,Double> errormap2=new HashMap<>();
//        errormap2.put("单元2",0.86);
//        errormap2.put("单元4",0.88);
//
//        List<String> Lru2=new ArrayList<String>(){{
//            add("单元2");
//            add("单元4");
//        }};
//
//        HashMap<String,Integer> repairTime2=new HashMap<>();
//        repairTime2.put("单元2",10);
//        repairTime2.put("单元4",18);
//
//        String fixProcess2="P5";
//
//        Equipment ep2=new Equipment("E2",3, processSeq2,processAndResource2,processAndResourcePriority2,failmap2,errormap2
//                ,Lru2,repairTime2,fixProcess2);
////
////        LinkedHashMap<String,Integer> processSeq3=new LinkedHashMap<>();
////        processSeq3.put("P1",4);
////        processSeq3.put("P2",8);
////        processSeq3.put("P3",12);
////        processSeq3.put("P4",5);
////        processSeq3.put("P5",10);
////        processSeq3.put("P6",12);
////        processSeq3.put("P7",6);
////        processSeq3.put("P8",8);
////        processSeq3.put("P9",10);
////        LinkedHashMap<String,HashMap<String,Integer>> processAndResource3=new LinkedHashMap<>();
////        processAndResource3.put("P1",new HashMap<String,Integer>(){{put("R2",1);}});
////        processAndResource3.put("P2",new HashMap<String,Integer>(){{put("R1",1);put("R4",1);}});
////        processAndResource3.put("P3",new HashMap<String,Integer>(){{put("R4",1);put("R5",1);}});
////        processAndResource3.put("P4",new HashMap<String,Integer>(){{put("R3",1);}});
////        processAndResource3.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});
////        processAndResource3.put("P6",new HashMap<String,Integer>(){{put("R4",1);put("R6",1);}});
////        processAndResource3.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
////        processAndResource3.put("P8",new HashMap<String,Integer>(){{put("R5",1);put("R7",1);}});
////        processAndResource3.put("P9",new HashMap<String,Integer>(){{put("R3",1);put("R6",1);}});
////
////        Equipment ep3=new Equipment("E3",1, processSeq3,processAndResource3);
////
////        LinkedHashMap<String,Integer> processSeq4=new LinkedHashMap<>();
////        processSeq4.put("P1",6);
////        processSeq4.put("P2",10);
////        processSeq4.put("P3",15);
////        processSeq4.put("P4",5);
////        processSeq4.put("P5",12);
////        processSeq4.put("P6",12);
////        processSeq4.put("P7",6);
////
////        LinkedHashMap<String,HashMap<String,Integer>> processAndResource4=new LinkedHashMap<>();
////        processAndResource4.put("P1",new HashMap<String,Integer>(){{put("R1",1);}});
////        processAndResource4.put("P2",new HashMap<String,Integer>(){{put("R2",1);put("R4",1);}});
////        processAndResource4.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
////        processAndResource4.put("P4",new HashMap<String,Integer>(){{put("R4",1);put("R5",1);}});
////        processAndResource4.put("P5",new HashMap<String,Integer>(){{put("R5",1);}});
////        processAndResource4.put("P6",new HashMap<String,Integer>(){{put("R4",1);put("R6",1);}});
////        processAndResource4.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
////
////        Equipment ep4=new Equipment("E4",1, processSeq4,processAndResource4);
//
//        equipmentList.add(ep1);
//        equipmentList.add(ep2);
////        equipmentList.add(ep3);
////        equipmentList.add(ep4);
//        HashMap<String,Integer> unitList=new HashMap<String, Integer>(){{
//            put("单元1",10);
//            put("单元2",10);
//            put("单元3",10);
//        }};
//
//        RMSTsimulation rmsTsimulation = new RMSTsimulation(equipmentList,resourceList,unitList);
//        List<Equipment> tempEqiList=new ArrayList<>();
//        for (int i=0;i<equipmentList.size();i++){
//            for (int j = 1; j <=equipmentList.get(i).getNum(); j++) {
//                Equipment equipment=BeanUtils.copy(equipmentList.get(i));
//                Objects.requireNonNull(equipment).setName(equipmentList.get(i).getName()+"-"+j);
//                Objects.requireNonNull(equipment).setNum(1);
//                tempEqiList.add(equipment);
//            }
//        }
//        equipmentList=tempEqiList;
        BasePojo basePojo=new BasePojo();
        basePojo.initForRMST();
        List<Equipment> equipmentList=basePojo.getEquipmentList();
        List<Resource> resourceList=basePojo.getResourcesTypeSeq();
        HashMap<String,Integer> unitList=basePojo.getUnitList();
        HashMap<String,Integer> originUnitList=new HashMap<>(unitList);
        RMSTsimulation rmsTsimulation = new RMSTsimulation(equipmentList,resourceList,unitList);
        int mc=100;
        int failedEqi=0;
        int finishedEqi=0;
        double usabilityTotal=0.0;
        System.out.println("-------------RMST模拟开始---------------");
        for (Equipment ep: basePojo.getEquipmentTypeSeq()) {
            System.out.println(ep.getName()+"单元可靠性指标:");
            for (Map.Entry<String,Double> entry:ep.getFailMap().entrySet()){
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
            System.out.println(ep.getName()+"LRU及故障检测率:");
            for (Map.Entry<String,Double> entry:ep.getErrorMap().entrySet()){
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
            System.out.println(ep.getName()+"LRU及平均修复时间:");
            for (Map.Entry<String,Integer> entry:ep.getLRUrepairTime().entrySet()){
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
            System.out.println("-----------分割线-----------");
        }
        for (int i=0;i<mc;i++){
            //深拷贝资源文件，使得过程原子化
            try {
                rmsTsimulation.equipmentList=BeanUtils.deepCopy(equipmentList);
                rmsTsimulation.resourceList=BeanUtils.deepCopy(resourceList);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            for (Resource r:rmsTsimulation.resourceListDetail) {
                r.setState(Resource.status.wait);
            }
            Result result=rmsTsimulation.schedule(200);
            failedEqi+=result.getFaiedEqi();
            finishedEqi+=result.getFinishedEqi();
            usabilityTotal+=result.getUsability();
        }
        usabilityTotal=usabilityTotal/mc;
        System.out.println("-------------模拟结束---------------");
        System.out.println("模拟次数mc:"+mc+" 故障装备个数:"+failedEqi+" 成功保障装备个数:"+finishedEqi+" 全局可用度为："+usabilityTotal);
        for (Map.Entry<String,Integer> unit:unitList.entrySet()) {
            for (Map.Entry<String,Integer> originUnit:originUnitList.entrySet()) {
                if (unit.getKey().equals(originUnit.getKey())){
                    double unitUtilization=(originUnit.getValue()-unit.getValue())*1.0/(originUnit.getValue()*mc);
                    System.out.println(unit.getKey()+"的利用率为："+unitUtilization);
                }
            }

        }


//        ShortTimePlanB scheduler = new ShortTimePlanB(equipmentList, processList, resourceList);

    }
}
