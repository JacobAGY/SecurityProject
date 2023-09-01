package com.szu.cn.Security;
import java.util.*;

public class SecurityPlan {
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
        List<Equipment> equipmentList = new ArrayList<>();
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

        HashMap<String,ArrayList<String>> change_Process = new HashMap<>();

        Equipment ep1=new Equipment("E1",1, processSeq,processAndResource,change_Process);

        LinkedHashMap<String,Integer> processSeq2=new LinkedHashMap<>();
        processSeq2.put("P1",4);
        processSeq2.put("P2",5);
        processSeq2.put("P3",15);
        processSeq2.put("P4",12);
        processSeq2.put("P5",16);
        processSeq2.put("P6",20);

        LinkedHashMap<String,HashMap<String,Integer>> processAndResource2=new LinkedHashMap<>();
        processAndResource2.put("P1",new HashMap<String,Integer>(){{put("R2",1);}});
        processAndResource2.put("P2",new HashMap<String,Integer>(){{put("R1",1);}});
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
        processSeq4.put("P1",5);
        processSeq4.put("P2",12);
        processSeq4.put("P3",6);
        processSeq4.put("P4",8);
        processSeq4.put("P5",12);
        processSeq4.put("P6",10);
        processSeq4.put("P7",15);

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

        List<Equipment> shortTime_list = new ArrayList<>();
        List<Equipment> highResponse_list = new ArrayList<>();
        LinkedHashMap<String,Integer> shortTime_processSeq1 = processSeq;
        LinkedHashMap<String,Integer> shortTime_processSeq2 = processSeq2;
        LinkedHashMap<String,Integer> shortTime_processSeq3 = processSeq3;
        LinkedHashMap<String,Integer> shortTime_processSeq4 = processSeq4;
        LinkedHashMap<String,HashMap<String,Integer>> shortTime_processAndResource1 = processAndResource;
        LinkedHashMap<String,HashMap<String,Integer>> shortTime_processAndResource2 = processAndResource2;
        LinkedHashMap<String,HashMap<String,Integer>> shortTime_processAndResource3 = processAndResource3;
        LinkedHashMap<String,HashMap<String,Integer>> shortTime_processAndResource4 = processAndResource4;
        Equipment shortTime_e1 = new Equipment("E1",1, shortTime_processSeq1,shortTime_processAndResource1,change_Process);
        Equipment shortTime_e2 = new Equipment("E2",1, shortTime_processSeq2,shortTime_processAndResource2,change_Process);
        Equipment shortTime_e3 = new Equipment("E3",1, shortTime_processSeq3,shortTime_processAndResource3,change_Process);
        Equipment shortTime_e4 = new Equipment("E4",1, shortTime_processSeq4,shortTime_processAndResource4,change_Process);
        shortTime_list.add(shortTime_e1);
        shortTime_list.add(shortTime_e2);
        shortTime_list.add(shortTime_e3);
        shortTime_list.add(shortTime_e4);

        LinkedHashMap<String,Integer> highResponse_processSeq1 = processSeq;
        LinkedHashMap<String,Integer> highResponse_processSeq2 = processSeq2;
        LinkedHashMap<String,Integer> highResponse_processSeq3 = processSeq3;
        LinkedHashMap<String,Integer> highResponse_processSeq4 = processSeq4;
        LinkedHashMap<String,HashMap<String,Integer>> highResponse_processAndResource1 = processAndResource;
        LinkedHashMap<String,HashMap<String,Integer>> highResponse_processAndResource2 = processAndResource2;
        LinkedHashMap<String,HashMap<String,Integer>> highResponse_processAndResource3 = processAndResource3;
        LinkedHashMap<String,HashMap<String,Integer>> highResponse_processAndResource4 = processAndResource4;
        Equipment highResponse_e1 = new Equipment("E1",1, highResponse_processSeq1,highResponse_processAndResource1,change_Process);
        Equipment highResponse_e2 = new Equipment("E2",1, highResponse_processSeq2,highResponse_processAndResource2,change_Process);
        Equipment highResponse_e3 = new Equipment("E3",1, highResponse_processSeq3,highResponse_processAndResource3,change_Process);
        Equipment highResponse_e4 = new Equipment("E4",1, highResponse_processSeq4,highResponse_processAndResource4,change_Process);
        highResponse_list.add(highResponse_e1);
        highResponse_list.add(highResponse_e2);
        highResponse_list.add(highResponse_e3);
        highResponse_list.add(highResponse_e4);

        ShortTimePlan_B shortTime_scheduler = new ShortTimePlan_B(shortTime_list,resourceList);
        Result result1 = shortTime_scheduler.schedule();
        System.out.println(result1.getList());
        HighResponseRatioPlan highResponse_scheduler = new HighResponseRatioPlan(highResponse_list,resourceList);
//        ShortTimePlan scheduler = new ShortTimePlan(equipmentList, processList, resourceList);
        Result result2 = highResponse_scheduler.schedule();
//        System.out.println(result2.getList());
//        if(result1.getTime() <= result2.getTime()){
//            System.out.println("经过对比，短作业算法时间更短，执行顺序为：" + result1.getList());
//        }else{
//            System.out.println("经过对比，高响应比算法时间更短，执行顺序为：" + result2.getList());
//        }
    }

}
