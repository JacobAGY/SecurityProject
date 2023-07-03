package com.szu.cn.Security.utils;

import com.szu.cn.Security.Equipment;
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
}
