package com.szu.cn.Security.Test;

import com.szu.cn.Security.Equipment;
import com.szu.cn.Security.Resource;

import java.util.List;

public class TestPojo {
    private List<Equipment> equiments;
    private List<Resource> resources;

    public List<Equipment> getEquiments() {
        return equiments;
    }

    public void setEquiments(List<Equipment> equiments) {
        this.equiments = equiments;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
