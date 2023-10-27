package com.szu.cn.main.Security;

import com.szu.cn.main.Security.algorithms.ResourceOptimizationPlan;
import com.szu.cn.main.Security.pojo.Equipment;
import com.szu.cn.main.Security.pojo.Result;
import com.szu.cn.main.Security.utils.AlgorithmUtils;
import com.szu.cn.main.Security.utils.Drawgraph;
import com.szu.cn.main.Security.utils.ShortTimePlan;
import com.szu.cn.main.Security.vo.EquipmentSupportVo;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class TestUnits {
    // 测试算法A,完成所有工序所需时间，无可变工序
    public static Result A(){
        TestCase testCase = new TestCase();
        EquipmentSupportVo equipmentSupportVo = testCase.testCase1();
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Result result = AlgorithmUtils.shortestTime(equipmentSupportVo);

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
        String url = "gantt_A";
        drawgraph.draw(url);

        return result;
    }
    // 测试算法A,完成所有工序所需时间，无可变工序
    public static Result A(int maxTime){
        TestCase testCase = new TestCase();
        EquipmentSupportVo equipmentSupportVo = testCase.testCase1();
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Result result = AlgorithmUtils.shortestTime(equipmentSupportVo,maxTime);

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
        String url = "gantt_A(maxTime)";
        drawgraph.draw(url);
        return result;
    }
    // 测试算法C
    public static void C(){
        TestCase testCase = new TestCase();
        EquipmentSupportVo equipmentSupportVo = testCase.testCase1();
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < equipmentSupportVo.getResources().size(); i++) {
            System.out.println("请输入" + equipmentSupportVo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            equipmentSupportVo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        resourceOptimizationPlan.optimalSoulution(equipmentSupportVo);
    }

    // 测试算法D1，只测可变工序，没有加入算法C
    public static Result D1(){
        TestCase testCase = new TestCase();
        EquipmentSupportVo equipmentSupportVo = testCase.testCase2();
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Result result = AlgorithmUtils.shortestTime(equipmentSupportVo);

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
        String url = "gantt_D1";
        drawgraph.draw(url);
        return result;
    }
    // 测试算法D2，只测可变工序，加入算法C
    public static void D2(){
        TestCase testCase = new TestCase();
        EquipmentSupportVo equipmentSupportVo = testCase.testCase2();
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < equipmentSupportVo.getResources().size(); i++) {
            System.out.println("请输入" + equipmentSupportVo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            equipmentSupportVo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        resourceOptimizationPlan.optimalSoulution(equipmentSupportVo,250);
    }

    // 测试甘特图,文件保存至Gantt包下
    public static void plotGantt(){

        TestCase testCase = new TestCase();
        EquipmentSupportVo equipmentSupportVo = testCase.testCase1();
        equipmentSupportVo = testCase.parameter_TestPojo(equipmentSupportVo);
        EquipmentSupportVo shortTime_equipmentSupportVo = (EquipmentSupportVo) SerializationUtils.clone(equipmentSupportVo);

        ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_equipmentSupportVo.getEquiments(), shortTime_equipmentSupportVo.getResources());
        Result result = shortTime_scheduler.schedule();

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
                System.out.println(tmp_processTimeWindows.length);
                System.out.println(record.getName());
                tmp_processTimeWindows[j][0] = processSeq.get(processName)-processSeq_Origin.get(processName);
                tmp_processTimeWindows[j][1] = processSeq.get(processName);
                j++;

            }

            processesTimeWindows.add(tmp_processTimeWindows);


        }

        Drawgraph drawgraph = new Drawgraph(timePeriod,equipmentsList,equipmentsTimeWindows,numOfProcess,processesList,processesTimeWindows);
        drawgraph.draw();

    }

    public static void main(String[] args) {

        Result result = null;
        //算法A
        result = A();
//        result = A(250);
        // 算法C，资源优化策略
//        C();
        // 算法D1，测试可变工序(无算法C)
//        result = D1();
//         算法D2，测试可变工序+算法C
//        D2();
//        plonxtGantt()绘制甘特图
//        plotGantt();
    }
}
