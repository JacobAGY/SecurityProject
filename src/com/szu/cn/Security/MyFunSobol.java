package com.szu.cn.Security;

import com.szu.cn.Security.Test.TestPojo;

import java.util.*;

public class MyFunSobol {

    //应力水平数
    private int N;
    //资源种类数
    private int D;
    //每种资源最大数量
    private int maxNum;
    //待处理装备数量
    private int eq_num;

    public MyFunSobol(int n, int d, int maxNum, int eq_num) {
        N = n;
        D = d;
        this.maxNum = maxNum;
        this.eq_num = eq_num;
    }

    // Generate a random matrix with values from 1 to maxNum
    private static int[][] generateRandomMatrix(int rows, int cols, int maxNum) {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(maxNum) + 1;
            }
        }
        return matrix;
    }

    // Generate AB matrices based on A and B matrices
    private static int[][][] generateABMatrices(int[][] A, int[][] B) {
        int N = A.length;
        int D = A[0].length;
        int[][][] AB = new int[D][N][D];


        for (int i = 0; i < D; i++) {
            int[][] tempA=copyMatrix(A);
            int[][] tempB=copyMatrix(B);
            for (int j = 0; j < N; j++) {
                tempA[j][i] = tempB[j][i];
            }
            AB[i] = tempA;
        }
        return AB;
    }

    private static int[][] copyMatrix(int[][] A){
        int[][] tempA=new int[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j=0;j<A[0].length;j++){
                tempA[i][j]=A[i][j];
            }
        }
        return tempA;
    }


    // Calculate Sobol analysis for a given resource configuration
    private static int[] calculateSobol(ShortTimePlan shortTimePlan,int maxTime,int[][] resource, int ep_num) {

        int N = resource.length;
        int[] ep = new int[N];

        for (int i=0;i<resource.length;i++){
                shortTimePlan.setResourceListNum(resource[i]);
                Result result=shortTimePlan.schedule(maxTime);
                ep[i]=result.getFinishedEqi();
        }

        return ep;
    }

    // Generate a random number from 1 to maxNum
    private static int generateRandomNumber(int maxNum) {
        Random random = new Random();
        return random.nextInt(maxNum) + 1;
    }

    // Concatenate two arrays into a single array
    private static int[] concatenateArrays(int[] array1, int[] array2) {
        int[] result = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    // Calculate the variance of an array of values
    private static double calculateVariance(int[] values) {
        double mean = calculateMean(values);
        double variance = 0;

        for (int value : values) {
            variance += Math.pow(value - mean, 2);
        }

        return variance / values.length;
    }

    // Calculate the mean of an array of values
    private static double calculateMean(int[] values) {
        double sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    // Calculate the first-order sensitivity indices (s1)
    private static double[] calculateFirstOrderSensitivity(int[] YB, int[] YA, int[][] YAB, double varY) {
        int D = YAB.length;
        int N = YB.length;
        double[] s1 = new double[D];

        for (int i = 0; i < D; i++) {
            double sum = 0;
            for (int j = 0; j < N; j++) {
                sum += YB[j] * (YAB[i][j] - YA[j]);
            }
            s1[i] = sum / (N * varY);
        }

        return s1;
    }

    // Calculate the total sensitivity indices (tsc)
    private static double[] calculateTotalSensitivity(int[] YA, int[][] YAB, double varY) {
        int D = YAB.length;
        double[] tsc = new double[D];

        for (int i = 0; i < D; i++) {
            double sum = 0;
            for (int j = 0; j < YA.length; j++) {
                sum += Math.pow(YA[j] - YAB[i][j], 2);
            }
            tsc[i] = sum / (2 * YA.length * varY);
        }

        return tsc;
    }

    // Print the elements of an array
    private static void printArray(double[] array) {
        for (double value : array) {
            System.out.println(value);
        }
    }

    private static double[] getTsc(TestPojo testPojo){
        ShortTimePlan scheduler = new ShortTimePlan(testPojo.getEquiments(),testPojo.getResources());
        int N = 50; // Stress levels, obtained from reading
        int D = testPojo.getResources().size(); // Number of resource types, obtained from reading
        int maxNum = 5; // Maximum value for each resource type
        int eq_num = testPojo.getEquiments().size();
        int maxTime=100;

        // 首先定义两个随机矩阵A与B，矩阵的规模为：行数为应力水平数，可以理解为仿真次数，列数数为保障资源种类数
        //生成的数据为1~maxNum随机选择的矩阵，该数据表示对每一个应力水平下，对每种保障资源数量进行随机配置
        int[][] A = generateRandomMatrix(N, D, maxNum);
        int[][] B = generateRandomMatrix(N, D, maxNum);

        // 然后在A、B基础上，共计生成（2+D）个矩阵，分别是A、B、AB1、AB2、AB3、AB4、AB5、AB6、AB7
        //所谓ABi矩阵是指将B矩阵的第i列复制替换到A矩阵的第i列。以下代码为生成ABi的过程。
        int[][][] AB = generateABMatrices(A, B);

        // 通过构造矩阵，共计得到（2+D）*N组不同的保障资源方案，每一组资源方案都会输出在规定的时间内可以保障/出动的装备数。
        //先计算A矩阵的出动/保障装备数计算结果，得到YA，长度为N的向量
        int[] YA = calculateSobol(scheduler,maxTime,A, eq_num);

        // 再先计算B矩阵的出动/保障装备数计算结果，得到YB，长度为N的向量
        int[] YB = calculateSobol(scheduler,maxTime,B, eq_num);

        // 先计算ABi矩阵的出动/保障装备数计算结果，得到YABi，每一个YABi长度为N的向量
        int[][] YAB = new int[D][];
        for (int i = 0; i < D; i++) {
            YAB[i] = calculateSobol(scheduler,maxTime,AB[i], eq_num);
        }

        // YA、YB进行堆叠，形成新的矩阵Y
        int[] Y = concatenateArrays(YA, YB);

        // 计算矩阵Y的方差
        double varY = calculateVariance(Y);

        // 一阶灵敏度指数，向量格式，长度为D
        double[] s1 = calculateFirstOrderSensitivity(YB, YA, YAB, varY);

        // 全局灵敏度指数，向量格式，长度为D
        double[] tsc = calculateTotalSensitivity(YA,YAB, varY);

        // Print the results
        System.out.println("一阶灵敏度指数 (s1):");
        printArray(s1);

        System.out.println("全局灵敏度指数 (tsc):");
        printArray(tsc);
        return tsc;
    }

    public static void main(String[] args) {


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
        List<Equipment> equipmentList=new ArrayList<>();
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

        Equipment ep1=new Equipment("E1",1, processSeq,processAndResource);

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

        Equipment ep2=new Equipment("E2",1, processSeq2,processAndResource2);

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

        Equipment ep3=new Equipment("E3",1, processSeq3,processAndResource3);

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

        Equipment ep4=new Equipment("E4",1, processSeq4,processAndResource4);

        equipmentList.add(ep1);
        equipmentList.add(ep2);
        equipmentList.add(ep3);
        equipmentList.add(ep4);

        TestPojo testPojo = new TestPojo();
        testPojo.setEquiments(equipmentList);
        testPojo.setResources(resourceList);

        getTsc(testPojo);

    }
}
