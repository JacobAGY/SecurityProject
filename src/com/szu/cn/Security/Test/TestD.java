package com.szu.cn.Security.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestD {
    public static void main(String[] args) {
        int[][] Matrix = {{1,0,0,0,0},{0,1,1,1,0},{0,0,1,1,0},{0,0,0,1,1},{0,0,0,0,1}};
        String[] processes = {"P1","P2","P3","P4","P5","P6","P7","P8"};
        List<HashMap<Integer,Integer>> result = new ArrayList<>();

        // 用map记录值与下标，因为都是唯一的，key为值，value为下标
        HashMap<Integer,Integer> map = new HashMap<>();
        map.put(0,0);
        map.put(1,1);
        map.put(2,2);
        map.put(3,3);
        map.put(4,4);
        result.add(map);
        int len2 = Matrix[0].length;
        for (int i = len2-1; i >= 0; i--) {
            ArrayList<HashMap> tmp_result = new ArrayList<>();
            for(int j = i+1;j < len2;j++){
                if(Matrix[i][j] == 1) {
                    // 记录交换后的list
                    for (HashMap<Integer,Integer> m: result) {
                        HashMap<Integer,Integer> replace_m = (HashMap<Integer, Integer>) m.clone();
                        int tmp1 = replace_m.get(i);
                        int tmp2 = replace_m.get(j);
                        replace_m.replace(i,tmp2);
                        replace_m.replace(j,tmp1);
                        tmp_result.add(replace_m);
                    }
                }
            }
            for (HashMap<Integer,Integer> tmps:tmp_result) {
                result.add(tmps);
            }
        }
        for (HashMap<Integer,Integer> r:result) {
            ArrayList<Integer> arr = new ArrayList<>();
            for (int i = 0; i < len2; i++) {
                arr.add(r.get(i));
            }
            for (Integer a:arr) {
                System.out.println(a);
            }
            System.out.println("==========");
        }
    }
}
