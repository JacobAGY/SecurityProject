package com.szu.cn.Security;

import com.szu.cn.Security.Test.TestPojo;
import com.szu.cn.Security.utils.Utils;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;

public class ResourceOptimizationPlan {

    public static void optimalSoulution(TestPojo testPojo) {

        Scanner in = new Scanner(System.in);

        //得到经济规模约束Q
        int Q = 0; //经济规模约束

        //若未设置Q，输入Q
        if (testPojo.getQ() == 0) {
            System.out.println("请输入经济规模约束Q：");
            Q = in.nextInt();
            testPojo.setQ(Q);
        }

        //1.生成初始解
        testPojo = getInitSolution(testPojo, Q);

        //输出初始解（测试）
        int allCost = 0;
        System.out.println("初始解为：");
        List<Resource> resources = testPojo.getResources();
        for (int i = 0; i < resources.size(); i++) {
            System.out.println(" 资源 " + resources.get(i).getName() + " 的数量为" + resources.get(i).getNum());
            allCost += resources.get(i).getPrice() * resources.get(i).getNum();
        }

        System.out.println("总花费为：" + allCost);

        //变邻域参数设定,A B 暂取10
        System.out.println("请输入A的值：");
        int A = Integer.parseInt(in.next());
        System.out.println("请输入B的值");
        int B = Integer.parseInt(in.next());

        //初始化参数 a b
        int a = 1;
        int b = 1;

        double beta = 0.1;

        //变邻域算法流程
        testPojo = variableFieldDrop(testPojo, Q, beta);

        System.out.println("经过变邻域下降：");
        allCost = 0;
        for (int i = 0; i < resources.size(); i++) {
            System.out.println(" 资源 " + resources.get(i).getName() + " 的数量为" + resources.get(i).getNum());
            allCost += resources.get(i).getPrice() * resources.get(i).getNum();
        }
        System.out.println("总花费为：" + allCost);
        System.out.println("下降");
        String next = in.next();

        while (true) {
            if (a > A) {
                break;
            } else {
                b = 1;
                while (b < B) {
                    // 为了避免陷入局部最优，在算法中加入扰动过程。任意选择执行邻域结构，不需要评估得到的邻域解的质量，将其直接赋值为当前解。
                    Random random = new Random();
                    //扰动过程
                    TestPojo testPojoOri = (TestPojo) SerializationUtils.clone(testPojo);
                    for (int i = 0; i < b; i++) {
                        int temp = random.nextInt(4);
                        if (temp > 1) {
                            TestPojo tempNd2 = neighborND1(testPojo, Q, beta);
                            if (tempNd2 != null) {
                                testPojo = tempNd2;
                            }
                        } else {
                            TestPojo tempNd1 = neighborND2(testPojo, Q, beta);
                            if (tempNd1 != null) {
                                testPojo = tempNd1;
                            }
                        }
                    }
                    testPojo = variableFieldDrop(testPojo, Q, beta);
                    if (compareSolutions(testPojoOri, testPojo, Q, beta) > 0) {
                        testPojo = testPojoOri;

                    } else {
                        a = 1;
                        b = 0;
                    }
                    b += 1;
                    beta += 1;
                }
                a += 1;
            }
        }



        allCost = 0;
        resources = testPojo.getResources();
//        Utils.shortestTime(testPojo, 250);
//        AlgorithmUtils.shortestTime1(testPojo, 250);
        AlgorithmUtils.shortestTime2(testPojo,250);
        System.out.println("↑↑↑↑↑↑↑↑最优↑↑↑↑↑↑↑↑");
        System.out.println("经过变邻域搜索算法 得到解为：");
        resources = testPojo.getResources();
        for (int i = 0; i < resources.size(); i++) {
            System.out.println(" 资源 " + resources.get(i).getName() + " 的数量为" + resources.get(i).getNum());
            allCost += resources.get(i).getNum() * resources.get(i).getPrice();
        }
        System.out.println("总花费为：" + allCost);
    }

    //得到初始解
    public static TestPojo getInitSolution(TestPojo testPojo, int Q) {

        int lastQ = Q; //剩余费用

        //2.所有资源数量大于0，因此首先设定所有资源的数量为1
        List<Resource> resources = testPojo.getResources();
        for (int i = 0; i < resources.size(); i++) {
            resources.get(i).setNum(1);
            //剩余费用
            lastQ = lastQ - (resources.get(i).getNum() * resources.get(i).getPrice());
        }

        //3.根据各项保障资源的全局灵敏度，按照从小到大依次编号
//        MyFunSobol myFunSobol = new MyFunSobol();
        MyFunSobol_B myFunSobol = new MyFunSobol_B();

        System.out.println("start getTsc");

        TestPojo testPojoTemp = (TestPojo) SerializationUtils.clone(testPojo);

        double[] tsc = myFunSobol.getTsc(testPojoTemp);
        for (int i = 0; i < resources.size(); i++) {
            resources.get(i).setGlobalSensitivity(tsc[i]);
        }

        //按照从小到大排序
        Collections.sort(resources, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                if (o1.getGlobalSensitivity() > o2.getGlobalSensitivity()) return 1;
                else return -1;
            }
        });

        for (int i = 0; i < resources.size(); i++) {
            System.out.println(" 资源 " + resources.get(i).getName() + " 的灵敏度为" + resources.get(i).getGlobalSensitivity());
        }

        //4.采用轮盘赌策略来选择需要购置的资源
        // 生成概率数组
        double[] p = new double[resources.size()];
        // 总灵敏度
        double sensetiveAll = 0;

        for (int i = 0; i < resources.size(); i++) {
            sensetiveAll += tsc[i];
        }

        for (int i = 0; i < resources.size(); i++) {
            p[i] = resources.get(i).getGlobalSensitivity() / sensetiveAll;
        }
        for (int i = 0; i < resources.size(); i++) {
            p[i] = p[i] + (i == 0 ? 0 : p[i - 1]);
        }

        Random random = new Random();
        while (lastQ > 0) {
            //生成0-1之间随机数
            double u = random.nextDouble();
            //遍历概率数组 找到选中资源
            for (int i = 0; i < resources.size(); i++) {
                if ((i == 0 ? 0 : p[i - 1]) < u && u < p[i]) {
                    lastQ -= resources.get(i).getPrice();
                    //如果剩余大于0 重复执行 如果小于0 返回初始解
                    if (lastQ > 0) {
                        int temp = resources.get(i).getNum();
                        resources.get(i).setNum(temp + 1);
                        System.out.println("选择资源" + resources.get(i).getName());
                        System.out.println("剩余金钱 ：" + lastQ);
                    } else {
                        break;
                    }
                }
            }
        }

        return testPojo;
    }

    //变邻域下降过程
    public static TestPojo variableFieldDrop(TestPojo testPojo, int Q, double beta) {
        while (true) {
            TestPojo tempNd1 = neighborND1(testPojo, Q, beta);
            if (tempNd1 != null && compareSolutions(tempNd1,testPojo,Q,beta)>0) {
                //Nd1找到更优解
                testPojo = tempNd1;
            } else {
                //若没有更优解，寻找Nd2是否有更优解
                TestPojo tempNd2 = neighborND2(testPojo, Q, beta);
                if (tempNd2 != null) {
                    if (compareSolutions(tempNd2,testPojo,Q,beta) > 0) {
                        testPojo = tempNd2;
                    }else{
                        //若没有找到，返回初始解
                        return testPojo;
                    }
                } else {
                    //若没有找到，返回初始解
                    return testPojo;
                }
            }
        }
    }

    //邻域结构ND1
    public static TestPojo neighborND1(TestPojo testPojo, int Q, double beta) {
        List<Resource> resources = testPojo.getResources();
        //记录每个解的质量
        double fn[] = new double[resources.size()];
        //当在保障时间内完成的装备数量相同时，进行fc的对比
        double fc[] = new double[resources.size()];
        //暂定 beta初始值
        //记录最优解数值
        double bestfc = 0;
        double bestfn = 0;
        int bestsolution = -1;

        //记录原始testPojo
        TestPojo clone = SerializationUtils.clone(testPojo);

        //遍历每个资源数量+1选取其中的最优解作为当前领域解
        for (int i = 0; i < resources.size(); i++) {
            //资源数量+1
            int num = resources.get(i).getNum();

            resources.get(i).setNum(num + 1);
            testPojo.setResources(resources);
            //获得最大保障装备数量 最大时间设定为250
            double gs = Utils.shortestTime(testPojo, 250);
            //记录花费
            double fcNow = 0;
            for (int j = 0; j < resources.size(); j++) {
                fcNow += (resources.get(j).getPrice() * resources.get(j).getNum());
            }
            if ((fcNow - Q) > 0) {
                //如果花费大于经济约束
                //撤回变化
                resources.get(i).setNum(num);
                testPojo = SerializationUtils.clone(clone);
                continue;
            }
            double fnNow = gs;

            //寻找最优解
            if (bestfn < fnNow) {
                //当fn大于最大fn 为更优解
                bestfn = fnNow;
                bestfc = fcNow;
                bestsolution = i;
            } else if (bestfn == fnNow) {
                //如果fn相等，对比fc
                if (fcNow > bestfc) {
                    //撤回变化
                    resources.get(i).setNum(num);
                    testPojo = SerializationUtils.clone(clone);
                    continue;
                } else {
                    //为更优解
                    bestfn = fnNow;
                    bestfc = fcNow;
                    bestsolution = i;
                }
            }
            //撤回变化
            resources.get(i).setNum(num);
            testPojo = SerializationUtils.clone(clone);
        }

        if (bestsolution == -1) {
            //若没找到更优解,返回null
            return null;
        }
        //若找到,变更resources的num值
        resources.get(bestsolution).setNum(resources.get(bestsolution).getNum() + 1);
        testPojo.setResources(resources);
        return testPojo;
    }

    //邻域结构ND2
    public static TestPojo neighborND2(TestPojo testPojo, int Q, double beta) {
        List<Resource> resources = testPojo.getResources();
        //记录每个解的质量
        double fn[] = new double[resources.size()];
        //当在保障时间内完成的装备数量相同时，进行fc的对比
        double fc[] = new double[resources.size()];
        //暂定 beta初始值
        //记录最优解数值
        double bestfc = 0;
        double bestfn = 0;
        int bestsolution = -1;

        //记录原始testPojo
        TestPojo clone = SerializationUtils.clone(testPojo);

        //遍历每个资源数量+1选取其中的最优解作为当前领域解
        for (int i = 0; i < resources.size(); i++) {
            //资源数量-1
            int num = resources.get(i).getNum();
            //下降过程 防止资源为负数
            if (num == 0) continue;
            resources.get(i).setNum(num - 1);
            testPojo.setResources(resources);
            //获得最大保障装备数量 最大时间设定为250
            double gs = Utils.shortestTime(testPojo, 250);
            //记录花费
            double fcNow = 0;
            for (int j = 0; j < resources.size(); j++) {
                fcNow += (resources.get(j).getPrice() * resources.get(j).getNum());
            }
            if ((fcNow - Q) > 0) {
                //如果花费大于经济约束
                //撤回变化
                resources.get(i).setNum(num);
                testPojo = SerializationUtils.clone(clone);

                continue;
            }
            double fnNow = gs;

            //寻找最优解
            if (bestfn < fnNow) {
                //当fn大于最大fn 为更优解
                bestfn = fnNow;
                bestfc = fcNow;
                bestsolution = i;
            } else if (bestfn == fnNow) {
                //如果fn相等，对比fc
                if (fcNow > bestfc) {
                    //撤回变化
                    resources.get(i).setNum(num);
                    testPojo = SerializationUtils.clone(clone);

                    continue;
                } else {
                    //为更优解
                    bestfn = fnNow;
                    bestfc = fcNow;
                    bestsolution = i;
                }
            }
            //撤回变化
            resources.get(i).setNum(num);
            testPojo = SerializationUtils.clone(clone);

        }

        if (bestsolution == -1) {
            //若没找到更优解,返回null
            return null;
        }
        //若找到,变更resources的num值
        resources.get(bestsolution).setNum(resources.get(bestsolution).getNum() - 1);
        testPojo.setResources(resources);
        return testPojo;
    }

    //对比两个解的质量
    public static int compareSolutions(TestPojo testPojo1, TestPojo testPojo2, int Q, double beta) {
        double fc1 = 0;
        double fc2 = 0;
        List<Resource> resources1 = testPojo1.getResources();
        List<Resource> resources2 = testPojo2.getResources();
        for (int i = 0; i < resources2.size(); i++) {
            fc1 += resources1.get(i).getPrice() * resources1.get(i).getNum();
            fc2 += resources2.get(i).getPrice() * resources2.get(i).getNum();
        }

        double fn1 = Utils.shortestTime(testPojo1, 250) - ((fc1 - Q) > 0 ? beta * (fc1 - Q) : 0);
        double fn2 = Utils.shortestTime(testPojo2, 250) - ((fc2 - Q) > 0 ? beta * (fc2 - Q) : 0);

        if (fn1 > fn2) {
            return 1;
        } else if (fn1 == fn2) {
            if (fc1 < fc2) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }

    }
}
