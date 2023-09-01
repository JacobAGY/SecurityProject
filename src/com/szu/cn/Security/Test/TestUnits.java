package com.szu.cn.Security.Test;

import com.szu.cn.Security.*;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Scanner;

public class TestUnits {
    // 测试算法A,完成所有工序所需时间，无可变工序
    public static Result A(){
        Test test = new Test();
        TestPojo testPojo = test.testCase1();
        testPojo = test.parameter_TestPojo(testPojo);
        Result result = AlgorithmUtils.shortestTime1(testPojo);
        return result;
    }
    // 测试算法A,完成所有工序所需时间，无可变工序
    public static Result A(int maxTime){
        Test test = new Test();
        TestPojo testPojo = test.testCase1();
        testPojo = test.parameter_TestPojo(testPojo);
        Result result = AlgorithmUtils.shortestTime1(testPojo,maxTime);
        return result;
    }
    // 测试算法C
    public static void C(){
        Test test = new Test();
        TestPojo testPojo = test.testCase1();
        testPojo = test.parameter_TestPojo(testPojo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < testPojo.getResources().size(); i++) {
            System.out.println("请输入" + testPojo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            testPojo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        resourceOptimizationPlan.optimalSoulution(testPojo);
    }

    // 测试算法D1，只测可变工序，没有加入算法C
    public static Result D1(){
        Test test = new Test();
        TestPojo testPojo = test.testCase2();
        testPojo = test.parameter_TestPojo(testPojo);
        Result result = AlgorithmUtils.shortestTime2(testPojo);
        return result;
    }
    // 测试算法D2，只测可变工序，加入算法C
    public static void D2(){
        Test test = new Test();
        TestPojo testPojo = test.testCase2();
        testPojo = test.parameter_TestPojo(testPojo);
        Scanner scanner = new Scanner(System.in);
        //设置资源价格
        for (int i = 0; i < testPojo.getResources().size(); i++) {
            System.out.println("请输入" + testPojo.getResources().get(i).getName() + " 的价格：");
            int next = Integer.parseInt(scanner.next());
            testPojo.getResources().get(i).setPrice(next);
        }

        ResourceOptimizationPlan resourceOptimizationPlan = new ResourceOptimizationPlan();
        resourceOptimizationPlan.optimalSoulution(testPojo);
    }

    public static void main(String[] args) {
        Result result = null;
        //算法A
        result = A();
//        result = A(250);
        // 算法C，ResourceOptimizationPlan.java使用108行代码，注释109行代码
//        C();
        // 算法D1，测试可变工序(无算法C)
//        result = D1();
        // 算法D2，测试可变工序+算法C，ResourceOptimizationPlan.java使用109行代码，注释108行代码
//        D2();

    }
}
