package com.szu.cn.main.Security.algorithms;

import com.szu.cn.main.Security.pojo.Result;
import com.szu.cn.test.HighResponseRatioPlan_test;
import com.szu.cn.test.SequentialPlan_test;
import com.szu.cn.test.ShortTimePlan_test;
import com.szu.cn.main.Security.vo.EquipmentSupportVo;
import org.apache.commons.lang3.SerializationUtils;

/**
 *  A算法，保障装备规划算法
 *  对比短作业、高响应比算法、原论文三种算法
 *  @author AGY
 */
public class SupportEquipmentPlan {

    /**
     *     用途：完成全部工序最短时间
     *     输入：EquipmentSupportVo
     *     输出：Result
     */
    public static Result shortestTime(EquipmentSupportVo equipmentSupportVo){
        Result result = null;

        EquipmentSupportVo shortTime_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);
        EquipmentSupportVo highResponse_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);
        EquipmentSupportVo sequential_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);

        ShortTimePlan_test shortTime_scheduler = new ShortTimePlan_test(shortTime_equipmentSupportVo.getEquiments(), shortTime_equipmentSupportVo.getResources());
        Result result1 = shortTime_scheduler.schedule();

        HighResponseRatioPlan_test highResponse_scheduler = new HighResponseRatioPlan_test(highResponse_equipmentSupportVo.getEquiments(), highResponse_equipmentSupportVo.getResources());
        Result result2 = highResponse_scheduler.schedule();

        SequentialPlan_test sequential_scheduler = new SequentialPlan_test(sequential_equipmentSupportVo.getEquiments(), sequential_equipmentSupportVo.getResources());
        Result result3 = sequential_scheduler.schedule();

        System.out.println("==================================");
        System.out.println("短作业算法时间为：" + result1.getTime());
        System.out.println("高响应比算法时间为：" + result2.getTime());
        System.out.println("原论文算法时间为：" + result3.getTime());
        if(result1.getTime() <= result2.getTime()){
            if(result1.getTime() <= result3.getTime()){
                System.out.println("经过对比，短作业算法时间更短，执行顺序为：" + result1.getList());
                result = result1;
            }else{
                System.out.println("经过对比，原论文算法时间更短，执行顺序为：" + result3.getList());
                result = result3;
            }

        }else{
            if(result2.getTime() <= result3.getTime()){
                System.out.println("经过对比，高响应比算法时间更短，执行顺序为：" + result2.getList());
                result = result2;
            }else{
                System.out.println("经过对比，原论文算法时间更短，执行顺序为：" + result3.getList());
                result = result3;
            }
        }
        return result;
    }

    /**
     *  用途：规定时间完成数量
     *  输入：EquipmentSupportVo，maxTime
     *  输出：Result
     */
    public static Result shortestTime(EquipmentSupportVo equipmentSupportVo, int maxTime){
        Result result = null;
        EquipmentSupportVo shortTime_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);
        EquipmentSupportVo highResponse_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);
        EquipmentSupportVo sequential_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);

        ShortTimePlan_test shortTime_scheduler = new ShortTimePlan_test(shortTime_equipmentSupportVo.getEquiments(), shortTime_equipmentSupportVo.getResources());
        Result result1 = shortTime_scheduler.schedule(maxTime);

        HighResponseRatioPlan_test highResponse_scheduler = new HighResponseRatioPlan_test(highResponse_equipmentSupportVo.getEquiments(), highResponse_equipmentSupportVo.getResources());
        Result result2 = highResponse_scheduler.schedule(maxTime);

        SequentialPlan_test sequential_scheduler = new SequentialPlan_test(sequential_equipmentSupportVo.getEquiments(), sequential_equipmentSupportVo.getResources());
        Result result3 = sequential_scheduler.schedule(maxTime);

        if(result1.getFinishedEqi() >= result2.getFinishedEqi()){
            if(result1.getFinishedEqi() >= result3.getFinishedEqi()){
                System.out.println("经过对比，短作业算法在"+maxTime+"时间内完成更多，为"+result1.getFinishedEqi()+"个，执行顺序为：" + result1.getList());
                result = result1;
            }else{
                System.out.println("经过对比，原论文算法在"+maxTime+"时间内完成更多，为"+result3.getFinishedEqi()+"个，执行顺序为：" + result3.getList());
                result = result3;
            }
        }else{
            if(result2.getFinishedEqi() >= result3.getFinishedEqi()){
                System.out.println("经过对比，高响应比算法在"+maxTime+"时间内完成更多，为"+result2.getFinishedEqi()+"个，执行顺序为："+ result2.getList());
                result = result2;
            }else{
                System.out.println("经过对比，原论文算法在"+maxTime+"时间内完成更多，为"+result3.getFinishedEqi()+"个，执行顺序为：" + result3.getList());
                result = result3;
            }
        }
        return result;
    }

}
