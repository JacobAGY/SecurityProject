package com.szu.cn.Security.utils;

import com.szu.cn.Security.Equipment;
import com.szu.cn.Security.HighResponseRatioPlan;
import com.szu.cn.Security.Result;
import com.szu.cn.Security.ShortTimePlan;
import com.szu.cn.Security.Test.TestPojo;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    //由装备的种类序列以及装备的数量生成装备序列
    public static TestPojo generateEquipmentSeq(TestPojo testPojo){

        List<Equipment> equipmentTypeSeq = testPojo.getEquipmentTypeSeq();
        List<Equipment> equipments = new ArrayList<Equipment>();

        //根据各装备的数量生成序列
        for (Equipment equipment : equipmentTypeSeq) {
            String name = equipment.getName();
            int num = 0; //装备编号，从1开始
            for (int i = 0; i < equipment.getNum(); i++) {
                //深拷贝母装备并更改name
                Equipment clone = SerializationUtils.clone(equipment);
                String newName = name + "-" + (i+1);
                clone.setName(newName);
                equipments.add(clone);
            }
        }
        testPojo.setEquiments(equipments);

        return testPojo;
    }

    public static int shortestTime(TestPojo testPojo,int maxTime){
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
            return result1.getFinishedEqi();
        }else{
            System.out.println("经过对比，短作业算法在"+maxTime+"时间内完成更多，为"+result2.getFinishedEqi()+"个，执行顺序为："+ result2.getList());
            return result2.getFinishedEqi();
        }
    }
}
