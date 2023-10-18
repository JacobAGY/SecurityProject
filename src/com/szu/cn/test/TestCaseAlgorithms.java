package com.szu.cn.test;

import com.szu.cn.main.Security.TestCase;
import com.szu.cn.main.Security.algorithms.MyFunSobol;
import com.szu.cn.main.Security.algorithms.ResourceOptimizationPlan;
import com.szu.cn.main.Security.algorithms.SupportEquipmentPlan;
import com.szu.cn.main.Security.pojo.Equipment;
import com.szu.cn.main.Security.pojo.Result;
import com.szu.cn.main.Security.utils.Drawgraph;
import com.szu.cn.main.Security.vo.EquipmentSupportVo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class TestCaseAlgorithms {
    private TestCase testCase = new TestCase();
    // testCase1 无可变工序
    private EquipmentSupportVo testCase1 = testCase.testCase1();
    // testCase2 有可变工序
    private EquipmentSupportVo testCase2 = testCase.testCase2();


    /**
     * 测试A算法，不可变工序，完成所有设备的最短时间
     */
    @Test
    public void testShortestTime(){
        // 不可变工序
        EquipmentSupportVo equipmentSupportVo = testCase1;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        SupportEquipmentPlan supportEquipmentPlan = new SupportEquipmentPlan();
        Result result = supportEquipmentPlan.shortestTime(equipmentSupportVo);

        String url = "gantt_shortestTime";
        plotGantt(result,url);
    }

    /**
     * 测试A算法，可变工序，完成所有设备的最短时间
     */
    @Test
    public void testShortestTime_Changeable(){
        // 可变工序
        EquipmentSupportVo equipmentSupportVo = testCase2;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        SupportEquipmentPlan supportEquipmentPlan = new SupportEquipmentPlan();
        Result result = supportEquipmentPlan.shortestTime(equipmentSupportVo);
        String url = "gantt_shortestTime_changeable";
        plotGantt(result,url);
    }

    /**
     * 测试A算法，不可变工序，规定时间内完成设备数量
     */
    @Test
    public void testShortestTime_maxTime(){
        // 不可变工序
        int maxTime = 250;
        EquipmentSupportVo equipmentSupportVo = testCase1;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        SupportEquipmentPlan supportEquipmentPlan = new SupportEquipmentPlan();
        Result result = supportEquipmentPlan.shortestTime(equipmentSupportVo,maxTime);
        String url = "gantt_shortestTime_maxTime";
        plotGantt(result,url);

    }

    /**
     * 测试A算法，可变工序，规定时间内完成设备数量
     */
    @Test
    public void testShortestTime_Changeable_maxTime(){
        // 可变工序
        int maxTime = 250;
        EquipmentSupportVo equipmentSupportVo = testCase2;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        SupportEquipmentPlan supportEquipmentPlan = new SupportEquipmentPlan();
        Result result = supportEquipmentPlan.shortestTime(equipmentSupportVo,maxTime);
        String url = "gantt_shortestTime_changeable_maxTime";
        plotGantt(result,url);
    }

    /**
     * 测试B算法
     */
    @Test
    public void testMyFunSobol(){
        MyFunSobol myFunSobol = new MyFunSobol();
        EquipmentSupportVo equipmentSupportVo = testCase1;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        double[] tsc = myFunSobol.getTsc(equipmentSupportVo);
    }

    /**
     * 测试C算法：资源优化，完成所有设备最短时间的资源优化矩阵
     */
    @Test
    public void testResourceOprimizationPlan(){
        EquipmentSupportVo equipmentSupportVo = testCase1;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < equipmentSupportVo.getResources().size(); i++) {
            System.out.println("请输入" + equipmentSupportVo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            equipmentSupportVo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        Result result = resourceOptimizationPlan.optimalSoulution(equipmentSupportVo);
        System.out.println("总花费：" + result.getAllcost());
        String url = "gantt_resourceOprimizationPlan";
        plotGantt(result,url);
    }

    /**
     * 测试C算法：资源优化，规定时间内完成设备个数
     */
    @Test
    public void testResourceOprimizationPlan_maxTime(){
        int maxTime = 250;
        EquipmentSupportVo equipmentSupportVo = testCase1;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < equipmentSupportVo.getResources().size(); i++) {
            System.out.println("请输入" + equipmentSupportVo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            equipmentSupportVo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        Result result = resourceOptimizationPlan.optimalSoulution(equipmentSupportVo,maxTime);
        System.out.println("总花费：" + result.getAllcost());
        String url = "gantt_resourceOprimizationPlan_maxTime";
        plotGantt(result,url);
    }

    /**
     * 测试D算法：可变矩阵+资源优化,完成所有设备所需时间
     */
    @Test
    public void testResourceOprimizationPlan_Changeable(){
        EquipmentSupportVo equipmentSupportVo = testCase2;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < equipmentSupportVo.getResources().size(); i++) {
            System.out.println("请输入" + equipmentSupportVo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            equipmentSupportVo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        Result result = resourceOptimizationPlan.optimalSoulution(equipmentSupportVo);
        System.out.println("总花费：" + result.getAllcost());

        String url = "gantt_resourceOprimizationPlan_Changeable";
        plotGantt(result,url);
    }

    /**
     * 测试D算法：可变矩阵+资源优化,规定时间内完成设备个数
     */
    @Test
    public void testResourceOprimizationPlan_Changeable_maxTime(){
        int maxTime = 250;
        EquipmentSupportVo equipmentSupportVo = testCase2;
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < equipmentSupportVo.getResources().size(); i++) {
            System.out.println("请输入" + equipmentSupportVo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            equipmentSupportVo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        Result result = resourceOptimizationPlan.optimalSoulution(equipmentSupportVo,maxTime);
        System.out.println("总花费：" + result.getAllcost());
        String url = "gantt_resourceOprimizationPlan_Changeable_maxTime";
        plotGantt(result,url);
    }

    // 甘特图
    public static void plotGantt(Result result, String url){

        // 传入纵坐标最大装备数量
        List<String> equipmentsList = new ArrayList<>();
        // 传入横坐标最大时间
        List<Equipment> records = result.getRecords_equiments();

        // 传入工序
        List<List<String>> processesList = new ArrayList<>();

        // 装备时间窗
        double[][] equipmentsTimeWindows = new double[records.size()][2];

        // 工序时间窗
        List<double[][]> processesTimeWindows = new ArrayList<>();

        int i = 0;
        int timePeriod = 0;
        int numOfProcess = 0;
        for (Equipment record:records) {
            if(record.getFinishTime() > timePeriod){
                timePeriod = record.getFinishTime();
            }
            equipmentsList.add(record.getName());
            LinkedHashMap<String,Integer> processSeq = record.getProcessSeq();
            LinkedHashMap<String,Integer> processSeq_Origin = record.getProcessSeq_Origin();

            equipmentsTimeWindows[i][0] = processSeq.values().stream().findFirst().get()-processSeq_Origin.values().stream().findFirst().get();
            equipmentsTimeWindows[i][1] = record.getFinishTime();
            i++;

            List<String> processSeqList = new ArrayList<>();
            processSeqList.addAll(processSeq.keySet());
            processesList.add(processSeqList);
            double[][] tmp_processTimeWindows =  new double[processSeqList.size()][2];

            if(processSeqList.size() > numOfProcess){
                numOfProcess = processSeqList.size();
            }
            int j = 0;
            for (String processName:processSeqList) {
                tmp_processTimeWindows[j][0] = processSeq.get(processName)-processSeq_Origin.get(processName);
                tmp_processTimeWindows[j][1] = processSeq.get(processName);
                j++;

            }

            processesTimeWindows.add(tmp_processTimeWindows);


        }

        Drawgraph drawgraph = new Drawgraph(timePeriod,equipmentsList,equipmentsTimeWindows,numOfProcess,processesList,processesTimeWindows);
        drawgraph.draw(url);

    }

}
