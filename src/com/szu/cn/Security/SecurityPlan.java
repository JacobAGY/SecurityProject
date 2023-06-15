package com.szu.cn.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SecurityPlan {
    public static void main(String[] args) {
        String[] resource_Name = {"资源1","资源2","资源3","资源4","资源5"};
        ArrayList<Equipment> equipments = new ArrayList<>();
        Random rd = new Random();
        //异质装备的数量，范围为[0,20）
        int temp = rd.nextInt(20) + 1;

        for (int i = 0; i < temp; i++) {
            String name = "Equiment" + i;
            //equiment_tmp表示每个装备有多少个，范围为[1,10]
            int equiment_tmp = rd.nextInt(10) + 1;
            Equipment e = new Equipment();
            e.setName(name);
            e.setNum(equiment_tmp);

            //装备的工序序列
            ArrayList<String> process_list = new ArrayList();
            //process_Name预设有5道工序，每个装备可以从中随机抽取出不同的工序
            String[] process_Name = {"工序1","工序2","工序3","工序4","工序5"};
            //process_tmp表示该装备有几道工序,取值范围为[1,5）
            int process_tmp = rd.nextInt(4) + 1;
            for (int j = 0; j < process_tmp; j++) {
                //process_part表示从process_Name数组中抽取出哪道工序
                int process_part = rd.nextInt(5);
                process_list.add(process_Name[process_part]);
            }
            e.setProcessSeq(process_list);

            HashMap<String,ArrayList<HashMap<String,Integer>>> processToResource = new HashMap<>();


            for (String processName: process_list) {
                ArrayList<HashMap<String,Integer>> resouceList = new ArrayList<>();
                HashMap<String,Integer> resource_priority = new HashMap<>();
                //resource_tmp代表有几个资源，resource_part代表从resource_Name中获取第几个值
                int resource_tmp = rd.nextInt(4) + 1;
                //每道工序包含的资源，通过生成不重复的随机数来判断
                ArrayList<String> resources = new ArrayList<>();
                for (int j = 0; j < resource_tmp; j++) {
                    int resource_part = rd.nextInt(5);
                    if(!resources.contains(resource_Name[resource_part])){
                        resources.add(resource_Name[resource_part]);
                        int priority = rd.nextInt(2);
                        resource_priority.put(resource_Name[resource_part],priority);

                    }
                }
                resouceList.add(resource_priority);
                processToResource.put(processName,resouceList);
                e.setProcessToResource(processToResource);
            }
            equipments.add(e);
        }
        //资源对应的数量
        HashMap<String,Integer> resource_available_amount_map = new HashMap<>();
        for (String available_resource: resource_Name) {
            //资源数量取值范围为[1,5]
            int available_amount = rd.nextInt(5) + 1;
            resource_available_amount_map.put(available_resource,available_amount);
        }
        System.out.println(equipments);

        /**
         *  这里开始写算法，参数为equiments和resource_available_amount_map
         *  equiments包含了若干个equiment集合
         *  resource_available_amount_map是资源对应的可用数量，资源为resource_Name
         */

    }
}
