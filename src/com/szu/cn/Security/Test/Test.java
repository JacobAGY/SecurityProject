package com.szu.cn.Security.Test;

import com.szu.cn.Security.*;
import com.szu.cn.Security.utils.Utils;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;

public class Test {
    public static TestPojo testCase(){
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
        List<Equipment> equipmentTypeList = new ArrayList<>();
        LinkedHashMap<String,Integer> processSeq=new LinkedHashMap<>();
        processSeq.put("P1",5);
        processSeq.put("P2",4);
        processSeq.put("P3",6);
        processSeq.put("P4",10);
        processSeq.put("P5",15);
        processSeq.put("P6",8);
        processSeq.put("P7",15);
        processSeq.put("P8",6);
        LinkedHashMap<String, HashMap<String,Integer>> processAndResource=new LinkedHashMap<>();
        processAndResource.put("P1",new HashMap<String,Integer>(){{put("R1",1);}});
        processAndResource.put("P2",new HashMap<String,Integer>(){{put("R2",1);}});
        processAndResource.put("P3",new HashMap<String,Integer>(){{put("R3",1);}});
        processAndResource.put("P4",new HashMap<String,Integer>(){{put("R3",1);put("R4",1);}});
        processAndResource.put("P5",new HashMap<String,Integer>(){{put("R3",1);put("R5",1);}});
        processAndResource.put("P6",new HashMap<String,Integer>(){{put("R3",1);put("R6",1);}});
        processAndResource.put("P7",new HashMap<String,Integer>(){{put("R3",1);put("R7",1);}});
        processAndResource.put("P8",new HashMap<String,Integer>(){{put("R3",1);}});

        Equipment ep1=new Equipment("A",1, processSeq,processAndResource);

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

        Equipment ep2=new Equipment("B",1, processSeq2,processAndResource2);

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

        Equipment ep3=new Equipment("C",1, processSeq3,processAndResource3);

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

        Equipment ep4=new Equipment("D",1, processSeq4,processAndResource4);

        equipmentTypeList.add(ep1);
        equipmentTypeList.add(ep2);
        equipmentTypeList.add(ep3);
        equipmentTypeList.add(ep4);
        TestPojo testPojo = new TestPojo();
        testPojo.setEquipmentTypeSeq(equipmentTypeList);
        testPojo.setResources(resourceList);
        return testPojo;
    }
    public static void main(String[] args) {
        //测试用例
        TestPojo testPojo = testCase();
        List<Resource> resources = testPojo.getResources();
        List<Equipment> equipmentments = testPojo.getEquipmentTypeSeq();
        List<Equipment> equipmentTypeSeq = testPojo.getEquipmentTypeSeq();
        //展示资源数量
        for (Resource resource : resources) {
            System.out.println("资源" + resource.getName() + "的数量为" + resource.getNum());
        }

        //是否更改
        System.out.println("是否更改资源数量：(Y/N)");

        Scanner scanner = new Scanner(System.in);
        String s = scanner.next().toLowerCase();

        while (!s.equals("n")){
            //需要更改
            if (s.equals("y")){
                //输入更改后的int序列
                System.out.println("请输入更改后的资源数量序列：");
                int[] ResourceNums = new int[resources.size()];
                int i = 0;
                //读取资源数量
                while (scanner.hasNext()){
                    int num = Integer.parseInt(scanner.next());
                    ResourceNums[i++] = num;
                    if (i == resources.size()) break;
                }
                //更改resources
                testPojo = changeResourcesNum(testPojo,ResourceNums);
                System.out.println("更改后资源数量为：");
                //展示资源数量
                for (Resource resource : resources) {
                    System.out.println("资源" + resource.getName() + "的数量为" + resource.getNum());
                }
                System.out.println("是否更改资源数量：(Y/N)");
                s = scanner.next();

            }else {
                System.out.println("输入格式错误，请重新输入：");
                s = scanner.next();
            }
        }


        //展示各装备process序列
        for (Equipment equiment : equipmentTypeSeq) {
            LinkedHashMap<String, Integer> processSeq = equiment.getProcessSeq();
            System.out.println("装备" + equiment.getName() + "的操作序列为" + equiment.getProcessSeq());
        }
        System.out.println("是否更改装备的操作序列：(Y/N)");
        s = scanner.next().toLowerCase();
        while (!s.equals("n")){
            //更改装备操作序列
            if (s.equals("y")){
                System.out.println("请输入需要更改的装备名称：");
                String equipmentName = scanner.next();
                //找到装备
                Equipment temp = null;
                while (temp == null){
                    for (Equipment equiment : equipmentTypeSeq) {
                        if (equipmentName.equals(equiment.getName())){
                            temp = equiment;
                        }
                    }
                    if (temp != null) break;
                    System.out.println("输入名称错误，请重新输入");
                    equipmentName = scanner.next();
                }
                System.out.println("请输入更改后的操作序列顺序(1,2,3...)");
                int i = 0;
                int[] orders = new int[temp.getProcessSeq().size()];
                while (i != orders.length && scanner.hasNext()){
                    orders[i++] = Integer.parseInt(scanner.next()) -1;
                }
                testPojo = changeEquipmentProcessSeq(testPojo,equipmentName,orders);
                for (Equipment equiment : equipmentTypeSeq) {
                    LinkedHashMap<String, Integer> processSeq = equiment.getProcessSeq();
                    System.out.println("装备" + equiment.getName() + "的操作序列为" + equiment.getProcessSeq());
                }
                System.out.println("是否更改装备的操作序列：(Y/N)");
                s = scanner.next().toLowerCase();
            }else {
                System.out.println("输入格式错误，请重新输入：");
                s = scanner.next();
            }
        }

        //展示装备数量
        for (Equipment equipment : equipmentTypeSeq) {
            System.out.println("请输入装备" + equipment.getName() + "的数量：");
            int equipmentNum = Integer.parseInt(scanner.next());
            equipment.setNum(equipmentNum);
        }

        Utils.generateEquipmentSeq(testPojo);

//        //是否更改
//        System.out.println("是否更改装备数量：(Y/N)");
//        s = scanner.next().toLowerCase();
//
//        while (!s.equals("n")){
//            //需要更改
//            if (s.equals("y")){
//                //输入更改后的int序列
//                System.out.println("请输入更改后的装备数量序列：");
//                int[] equipmentNums = new int[equiments.size()];
//                int i = 0;
//                //读取装备数量
//                while (scanner.hasNext()){
//                    int num = Integer.parseInt(scanner.next());
//                    equipmentNums[i++] = num;
//                    if (i == equiments.size()) break;
//                }
//                //更改equipments
//                testPojo = changeEquipmentNum(testPojo,equipmentNums);
//                System.out.println("更改后装备数量为：");
//                //展示资源数量
//                for (Equipment equipment : equiments) {
//                    System.out.println("装备" + equipment.getName() + "的数量为" + equipment.getNum());
//                }
//                System.out.println("是否更改装备数量：(Y/N)");
//                s = scanner.next();
//
//            }else {
//                System.out.println("输入格式错误，请重新输入：");
//                s = scanner.next();
//            }
//        }

//        shortestTime(testPojo);
        shortestTime(testPojo,255);
//        ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
//        Result result1 = shortTime_scheduler.schedule();
////        System.out.println(result1.getList());
//
//        HighResponseRatioPlan highResponse_scheduler = new HighResponseRatioPlan(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
//        Result result2 = highResponse_scheduler.schedule();
////        System.out.println(result2.getList());
//        System.out.println("==================================");
//        System.out.println("短作业算法时间为：" + result1.getTime());
//        System.out.println("高响应比算法时间为：" + result2.getTime());
//        if(result1.getTime() <= result2.getTime()){
//            System.out.println("经过对比，短作业算法时间更短，执行顺序为：" + result1.getList());
//        }else{
//            System.out.println("经过对比，高响应比算法时间更短，执行顺序为：" + result2.getList());
//        }
    }

    //更改resouces数量，返回TestPojo
    public static TestPojo changeResourcesNum(TestPojo testPojo, int[] resourcesNums){
        List<Resource> resources = testPojo.getResources();
        for (int i = 0; i < resources.size(); i++) {
            resources.get(i).setNum(resourcesNums[i]);
        }
        return testPojo;
    }

    public static TestPojo changeEquipmentNum(TestPojo testPojo,int[] equipmentNums){
        List<Equipment> equiments = testPojo.getEquiments();
        for (int i = 0; i < equiments.size(); i++) {
            equiments.get(i).setNum(equipmentNums[i]);
        }
        return testPojo;
    }

    //更改装备操作序列
    public static TestPojo changeEquipmentProcessSeq(TestPojo testPojo,String name,int[] orders){
        List<Equipment> equiments = testPojo.getEquipmentTypeSeq();
        //找到装备
        for (Equipment equiment : equiments) {
            if (equiment.getName().equals(name)){
                LinkedHashMap<String, Integer> processSeq = equiment.getProcessSeq();
                LinkedHashMap<String, Integer> processSeqNew = new LinkedHashMap<String, Integer>();
                Object[] objects = processSeq.keySet().toArray(new String[0]);
                for (int i = 0; i < orders.length; i++) {
                    processSeqNew.put((String)objects[orders[i]],processSeq.get((String)objects[orders[i]]));
                }
                equiment.setProcessSeq(processSeqNew);
            }
        }
        return testPojo;
    }

    //最短时间测试
    public static void shortestTime(TestPojo testPojo){

        TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo highResponse_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

        ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
        Result result1 = shortTime_scheduler.schedule();
//        System.out.println(result1.getList());

        HighResponseRatioPlan highResponse_scheduler = new HighResponseRatioPlan(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
        Result result2 = highResponse_scheduler.schedule();
//        System.out.println(result2.getList());
        System.out.println("==================================");
        System.out.println("短作业算法时间为：" + result1.getTime());
        System.out.println("高响应比算法时间为：" + result2.getTime());
        if(result1.getTime() <= result2.getTime()){
            System.out.println("经过对比，短作业算法时间更短，执行顺序为：" + result1.getList());
        }else{
            System.out.println("经过对比，高响应比算法时间更短，执行顺序为：" + result2.getList());
        }
    }

    //最短时间测试
    public static void shortestTime(TestPojo testPojo,int maxTime){
        TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo highResponse_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

        ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
        Result result1 = shortTime_scheduler.schedule(maxTime);
//        System.out.println(result1.getList());

        HighResponseRatioPlan highResponse_scheduler = new HighResponseRatioPlan(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
        Result result2 = highResponse_scheduler.schedule(maxTime);
//        System.out.println(result2.getList());
        System.out.println("==================================");
        System.out.println("短作业算法时间为：" + result1.getTime());
        System.out.println("高响应比算法时间为：" + result2.getTime());
        if(result1.getFinishedEqi() >= result2.getFinishedEqi()){
            System.out.println("经过对比，短作业算法在"+maxTime+"时间内完成更多，为"+result1.getFinishedEqi()+"个，执行顺序为：" + result1.getList());
        }else{
            System.out.println("经过对比，短作业算法在"+maxTime+"时间内完成更多，为"+result2.getFinishedEqi()+"个，执行顺序为："+ result2.getList());
        }
    }
}
