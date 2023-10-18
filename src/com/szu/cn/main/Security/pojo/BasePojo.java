package com.szu.cn.main.Security.pojo;

import com.szu.cn.main.Security.utils.BeanUtils;

import java.io.Serializable;
import java.util.*;


/**
 * 共同类（配置所需的属性）
 */
public class BasePojo implements Serializable {

    private List<Equipment> equipmentTypeSeq; //装备种类序列 E1 E2 E3

    private List<Equipment> equipmentList; //装备序列 E1-1 E1-2 E2-1

    private List<Resource> resourcesTypeSeq; //资源种类序列 R1 R2 R3

    private List<Resource> resourceList; //资源序列 R1-1 R1-2 R2-1 R3-1

    private HashMap<String,Integer> unitList; //单元组件序列 单元1 单元2 单元3

    private int Q; // 费用约束

    public BasePojo() {
        this.equipmentTypeSeq = new ArrayList<>();
        this.equipmentList=new ArrayList<>();
        this.resourcesTypeSeq=new ArrayList<>();
        this.resourceList=new ArrayList<>();
        this.unitList=new HashMap<>();
    }

    public List<Equipment> getEquipmentTypeSeq() {
        return equipmentTypeSeq;
    }

    public void setEquipmentTypeSeq(List<Equipment> equipmentTypeSeq) {
        this.equipmentTypeSeq = equipmentTypeSeq;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentTypeSeq) {
        List<Equipment> tempEqiList=new ArrayList<>();
        for (int i=0;i<equipmentTypeSeq.size();i++){
            for (int j = 1; j <=equipmentTypeSeq.get(i).getNum(); j++) {
                Equipment equipment= BeanUtils.copy(equipmentTypeSeq.get(i));
                Objects.requireNonNull(equipment).setName(equipmentTypeSeq.get(i).getName()+"-"+j);
                Objects.requireNonNull(equipment).setNum(1);
                tempEqiList.add(equipment);
            }
        }
        this.equipmentList = tempEqiList;
    }

    public List<Resource> getResourcesTypeSeq() {
        return resourcesTypeSeq;
    }

    public void setResourcesTypeSeq(List<Resource> resourcesTypeSeq) {
        this.resourcesTypeSeq = resourcesTypeSeq;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourcesTypeSeq) {
        List<Resource> tempList=new ArrayList<>();
        for (int i=0;i<resourcesTypeSeq.size();i++){
            for (int j=1;j<=resourcesTypeSeq.get(i).getNum();j++){
                Resource resource=new Resource(resourcesTypeSeq.get(i).getName()+"-"+j,1);
                tempList.add(resource);
            }
        }
        this.resourceList = tempList;
    }

    public HashMap<String, Integer> getUnitList() {
        return unitList;
    }

    public void setUnitList(HashMap<String, Integer> unitList) {
        this.unitList = unitList;
    }

    public int getQ() {
        return Q;
    }

    public void setQ(int q) {
        Q = q;
    }

    public static final String EQUIPMENT_PREFIX="E"; //装备名称前缀

    public static final String RESOURSE_PREFIX="R";  //资源名称前缀

    public static final String PROCESS_PREFIX="P";  //工序名称前缀

    public static final String UNIT_PREFIX="单元";  //单元组件前缀

    /**
     * 扩展初始化方法
     * 可以在该类中扩展要用的初始化方式
     */

    /**
     * 初始化方法-1自定义初始化
     */
    public void initForRMST(){
        //初始化资源
        Resource resource1=new Resource(RESOURSE_PREFIX+"1",5);
        Resource resource2=new Resource(RESOURSE_PREFIX+"2",5);
        Resource resource3=new Resource(RESOURSE_PREFIX+"3",5);
        Resource resource4=new Resource(RESOURSE_PREFIX+"4",5);
        Resource resource5=new Resource(RESOURSE_PREFIX+"5",5);
        Resource resource6=new Resource(RESOURSE_PREFIX+"6",5);
        Resource resource7=new Resource(RESOURSE_PREFIX+"7",5);
        List<Resource> resourcesTypeList=new ArrayList<>();
        resourcesTypeList.add(resource1);
        resourcesTypeList.add(resource2);
        resourcesTypeList.add(resource3);
        resourcesTypeList.add(resource4);
        resourcesTypeList.add(resource5);
        resourcesTypeList.add(resource6);
        resourcesTypeList.add(resource7);
        this.resourcesTypeSeq=resourcesTypeList;

        setResourceList(resourcesTypeList);

        //初始化装备
        List<Equipment> equipmentList=new ArrayList<>();
        LinkedHashMap<String,Integer> processSeq=new LinkedHashMap<>();
        processSeq.put(PROCESS_PREFIX+"1",5);
        processSeq.put(PROCESS_PREFIX+"2",4);
        processSeq.put(PROCESS_PREFIX+"3",6);
        processSeq.put(PROCESS_PREFIX+"4",10);
        processSeq.put(PROCESS_PREFIX+"5",15);
        processSeq.put(PROCESS_PREFIX+"6",8);
        processSeq.put(PROCESS_PREFIX+"7",15);
        processSeq.put(PROCESS_PREFIX+"8",6);
        LinkedHashMap<String,HashMap<String,Integer>> processAndResource=new LinkedHashMap<>();
        processAndResource.put(PROCESS_PREFIX+"1",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"1",1);}});
        processAndResource.put(PROCESS_PREFIX+"2",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"2",1);}});
        processAndResource.put(PROCESS_PREFIX+"3",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});
        processAndResource.put(PROCESS_PREFIX+"4",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"4",1);}});
        processAndResource.put(PROCESS_PREFIX+"5",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"5",1);}});
        processAndResource.put(PROCESS_PREFIX+"6",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"6",1);}});
        processAndResource.put(PROCESS_PREFIX+"7",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"7",1);}});
        processAndResource.put(PROCESS_PREFIX+"8",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});

        //设置装备工序资源优先级
        LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority=new LinkedHashMap<>();

        processAndResourcePriority.put(PROCESS_PREFIX+"3",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});
        processAndResourcePriority.put(PROCESS_PREFIX+"4",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});
        processAndResourcePriority.put(PROCESS_PREFIX+"5",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});
        processAndResourcePriority.put(PROCESS_PREFIX+"6",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});
        processAndResourcePriority.put(PROCESS_PREFIX+"7",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});
        processAndResourcePriority.put(PROCESS_PREFIX+"8",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);}});

        HashMap<String,Double> failmap=new HashMap<>();
        failmap.put(UNIT_PREFIX+"1",0.95);
        failmap.put(UNIT_PREFIX+"2",0.975);
        failmap.put(UNIT_PREFIX+"3",0.96);
        failmap.put(UNIT_PREFIX+"4",0.93);

        HashMap<String,Double> errormap=new HashMap<>();
        errormap.put(UNIT_PREFIX+"1",0.82);
        errormap.put(UNIT_PREFIX+"3",0.78);

        List<String> Lru=new ArrayList<String>(){{
            add(UNIT_PREFIX+"1");
            add(UNIT_PREFIX+"3");
        }};

        HashMap<String,Integer> repairTime=new HashMap<>();
        repairTime.put(UNIT_PREFIX+"1",10);
        repairTime.put(UNIT_PREFIX+"3",15);


        String fixProcess=PROCESS_PREFIX+"6";
        Equipment ep1=new Equipment(EQUIPMENT_PREFIX+"1",2, processSeq,processAndResource,processAndResourcePriority,failmap,errormap,Lru,repairTime,fixProcess);

        LinkedHashMap<String,Integer> processSeq2=new LinkedHashMap<>();
        processSeq2.put(PROCESS_PREFIX+"1",4);
        processSeq2.put(PROCESS_PREFIX+"2",5);
        processSeq2.put(PROCESS_PREFIX+"3",15);
        processSeq2.put(PROCESS_PREFIX+"4",12);
        processSeq2.put(PROCESS_PREFIX+"5",16);
        processSeq2.put(PROCESS_PREFIX+"6",20);

        LinkedHashMap<String,HashMap<String,Integer>> processAndResource2=new LinkedHashMap<>();
        processAndResource2.put(PROCESS_PREFIX+"1",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"2",1);}});
        processAndResource2.put(PROCESS_PREFIX+"2",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"1",1);put(RESOURSE_PREFIX+"3",1);}});
        processAndResource2.put(PROCESS_PREFIX+"3",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"4",1);}});
        processAndResource2.put(PROCESS_PREFIX+"4",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"4",1);}});
        processAndResource2.put(PROCESS_PREFIX+"5",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"5",1);}});
        processAndResource2.put(PROCESS_PREFIX+"6",new HashMap<String,Integer>(){{put(RESOURSE_PREFIX+"3",1);put(RESOURSE_PREFIX+"7",1);}});

        //设置装备工序资源优先级
        LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority2=new LinkedHashMap<>();

        HashMap<String,Double> failmap2=new HashMap<>();
        failmap2.put(UNIT_PREFIX+"1",0.95);
        failmap2.put(UNIT_PREFIX+"2",0.975);
        failmap2.put(UNIT_PREFIX+"3",0.96);
        failmap2.put(UNIT_PREFIX+"4",0.93);

        HashMap<String,Double> errormap2=new HashMap<>();
        errormap2.put(UNIT_PREFIX+"2",0.86);
        errormap2.put(UNIT_PREFIX+"4",0.88);

        List<String> Lru2=new ArrayList<String>(){{
            add(UNIT_PREFIX+"2");
            add(UNIT_PREFIX+"4");
        }};

        HashMap<String,Integer> repairTime2=new HashMap<>();
        repairTime2.put(UNIT_PREFIX+"2",10);
        repairTime2.put(UNIT_PREFIX+"4",18);

        String fixProcess2=PROCESS_PREFIX+"5";

        Equipment ep2=new Equipment(EQUIPMENT_PREFIX+"2",3, processSeq2,processAndResource2,processAndResourcePriority2,failmap2,errormap2
                ,Lru2,repairTime2,fixProcess2);

        this.equipmentTypeSeq.add(ep1);
        this.equipmentTypeSeq.add(ep2);

        this.unitList= new HashMap<String, Integer>(){{
            put(UNIT_PREFIX+"1",10);
            put(UNIT_PREFIX+"2",10);
            put(UNIT_PREFIX+"3",10);
        }};

        setEquipmentList(equipmentTypeSeq);

    }
    /**
     * 初始化方法2-输入
     */
    public void initForShortTime(){
        System.out.println("输入资源种类数量：");
        Scanner scanner1 = new Scanner(System.in);
        int resourceNum = scanner1.nextInt();

        // 4 4 1 1 3 2 3
        List<Resource> resourceList=new ArrayList<>();
        for (int i = 1; i <=resourceNum ; i++) {
            System.out.println("输入资源"+ RESOURSE_PREFIX+i+"数量：");
            Scanner resNum = new Scanner(System.in);
            Resource resource=new Resource(RESOURSE_PREFIX+i,resNum.nextInt());
            resourceList.add(resource);
        }
        resourcesTypeSeq=resourceList;
        setResourceList(resourcesTypeSeq);

        System.out.println("输入装备种类数量：");
        Scanner scanner2 = new Scanner(System.in);
        int equipmentNum = scanner2.nextInt();
        //初始化装备
        List<Equipment> equipmentList=new ArrayList<>();

        for (int i = 1; i <=equipmentNum; i++) {
            System.out.println("装备"+EQUIPMENT_PREFIX+i+"请输入装备数量：");
            Scanner scanner3 = new Scanner(System.in);
            int equiNum=scanner3.nextInt();
            System.out.println("装备"+EQUIPMENT_PREFIX+i+"需要多少次工序，请输入：");

            int processNum= scanner3.nextInt();
            //初始化工序
            LinkedHashMap<String,Integer> processSeq=new LinkedHashMap<>();
            LinkedHashMap<String,HashMap<String,Integer>> processAndResource=new LinkedHashMap<>();
            //设置装备工序资源优先级
            LinkedHashMap<String,HashMap<String,Integer>> processAndResourcePriority=new LinkedHashMap<>();
            for (int j = 1; j <=processNum ; j++) {
                //设置工序与工作时间
                Scanner scanner4 = new Scanner(System.in);
                System.out.println("请输入工序"+PROCESS_PREFIX+j+"的作业时间：");
                int ProcessTime=scanner4.nextInt();
                processSeq.put(PROCESS_PREFIX+j,ProcessTime);
                //设置工序与资源关系
                System.out.println("需为工序"+PROCESS_PREFIX+j+"分配几种资源：");
                int resType=scanner4.nextInt();
                HashMap<String,Integer> prMap=new HashMap<>();
                System.out.println("需为工序"+PROCESS_PREFIX+j+"分配的资源以及资源数量：");
                for (int k = 0; k < resType; k++) {
                    int resIndex=scanner4.nextInt();
                    int resNum=scanner4.nextInt();
                    prMap.put(RESOURSE_PREFIX+resIndex,resNum);
                }
                processAndResource.put(PROCESS_PREFIX+j,prMap);
                System.out.println("是否为工序"+PROCESS_PREFIX+j+"设置资源优先级？(Y/N)");
                String flag=scanner4.next();
                if (flag.equals("Y")){
                    System.out.println("需为工序"+PROCESS_PREFIX+j+"分配几种资源的优先级：");
                    int resPriType=scanner4.nextInt();
                    HashMap<String,Integer> pprMap=new HashMap<>();
                    System.out.println("需为工序"+PROCESS_PREFIX+j+"设置的资源以及资源优先级：");
                    for (int k = 0; k < resPriType; k++) {
                        int resIndex=scanner4.nextInt();
                        int resPriority=scanner4.nextInt();
                        pprMap.put(RESOURSE_PREFIX+resIndex,resPriority);
                    }
                    processAndResourcePriority.put(PROCESS_PREFIX+j,pprMap);
                }
            }
            Equipment ep=new Equipment(EQUIPMENT_PREFIX+i,equiNum, processSeq,processAndResource,processAndResourcePriority);
            equipmentList.add(ep);
        }
        equipmentTypeSeq=equipmentList;
        setEquipmentList(equipmentTypeSeq);
    }
}

