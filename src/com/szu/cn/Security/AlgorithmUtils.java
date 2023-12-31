package com.szu.cn.Security;

import com.szu.cn.Security.Pojo.Result;
import com.szu.cn.Security.Test.TestPojo;
import org.apache.commons.lang3.SerializationUtils;

public class AlgorithmUtils {
    //算法A：完成全部工序最短时间,无可变工序
    public static Result shortestTime1(TestPojo testPojo){
        Result result = null;

        TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo highResponse_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo sequential_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

        ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
        Result result1 = shortTime_scheduler.schedule();

        HighResponseRatioPlan highResponse_scheduler = new HighResponseRatioPlan(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
        Result result2 = highResponse_scheduler.schedule();

        SequentialPlan sequential_scheduler = new SequentialPlan(sequential_testPojo.getEquiments(),sequential_testPojo.getResources());
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

    //算法A：规定时间完成数量,无可变工序
    public static Result shortestTime1(TestPojo testPojo,int maxTime){
        Result result = null;
        TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo highResponse_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo sequential_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

        ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
        Result result1 = shortTime_scheduler.schedule(maxTime);

        HighResponseRatioPlan highResponse_scheduler = new HighResponseRatioPlan(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
        Result result2 = highResponse_scheduler.schedule(maxTime);

        SequentialPlan sequential_scheduler = new SequentialPlan(sequential_testPojo.getEquiments(),sequential_testPojo.getResources());
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

    //算法A：完成全部工序最短时间,有可变工序
    public static Result shortestTime2(TestPojo testPojo){
        Result result = null;

        TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo highResponse_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo sequential_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

        ShortTimePlan_B shortTime_scheduler = new ShortTimePlan_B(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
        Result result1 = shortTime_scheduler.schedule();

        HighResponseRatioPlan_B highResponse_scheduler = new HighResponseRatioPlan_B(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
        Result result2 = highResponse_scheduler.schedule();

        SequentialPlan_B sequential_scheduler = new SequentialPlan_B(sequential_testPojo.getEquiments(),sequential_testPojo.getResources());
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

    //算法A：规定时间完成数量,有可变工序
    public static Result shortestTime2(TestPojo testPojo,int maxTime){
        Result result = null;
        TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo highResponse_testPojo = (TestPojo) SerializationUtils.clone(testPojo);
        TestPojo sequential_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

        ShortTimePlan_B shortTime_scheduler = new ShortTimePlan_B(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
        Result result1 = shortTime_scheduler.schedule(maxTime);

        HighResponseRatioPlan_B highResponse_scheduler = new HighResponseRatioPlan_B(highResponse_testPojo.getEquiments(),highResponse_testPojo.getResources());
        Result result2 = highResponse_scheduler.schedule(maxTime);

        SequentialPlan_B sequential_scheduler = new SequentialPlan_B(sequential_testPojo.getEquiments(),sequential_testPojo.getResources());
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
