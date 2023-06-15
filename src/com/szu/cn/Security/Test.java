package com.szu.cn.Security;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Random rd = new Random();

        for (int i = 0; i < 20; i++) {
            int fail_rate = rd.nextInt(6);
            System.out.println(fail_rate);
        }
    }
}
