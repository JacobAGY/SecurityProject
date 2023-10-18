package com.szu.cn.main.Security.utils;

import com.szu.cn.main.Security.pojo.Equipment;
import com.szu.cn.main.Security.pojo.Result;
import com.szu.cn.main.Security.vo.EquipmentSupportVo;
import com.szu.cn.test.HighResponseRatioPlan_test;
import com.szu.cn.test.ShortTimePlan_test;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用途：修改输入参数
 * @author hgx
 */
public class Utils {

    //由装备的种类序列以及装备的数量生成装备序列
    public static EquipmentSupportVo generateEquipmentSeq(EquipmentSupportVo equipmentSupportVo){

        List<Equipment> equipmentTypeSeq = equipmentSupportVo.getEquipmentTypeSeq();
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
        equipmentSupportVo.setEquiments(equipments);

        return equipmentSupportVo;
    }

    public static int shortestTime(EquipmentSupportVo equipmentSupportVo, int maxTime){
        EquipmentSupportVo shortTime_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);
        EquipmentSupportVo highResponse_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);

        ShortTimePlan_test shortTime_scheduler = new ShortTimePlan_test(shortTime_equipmentSupportVo.getEquiments(), shortTime_equipmentSupportVo.getResources());
        Result result1 = shortTime_scheduler.schedule(maxTime);
//        System.out.println(result1.getList());

        HighResponseRatioPlan_test highResponse_scheduler = new HighResponseRatioPlan_test(highResponse_equipmentSupportVo.getEquiments(), highResponse_equipmentSupportVo.getResources());
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
