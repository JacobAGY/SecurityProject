package com.szu.cn.Security.Test;

import com.szu.cn.Security.Equipment;
import com.szu.cn.Security.Resource;

import java.io.Serializable;
import java.util.List;

public class TestPojo implements Serializable {
    private List<Equipment> equipmentTypeSeq; //装备种类序列
    private List<Equipment> equiments; //装备序列 A1 A2 B1 B2
    private List<Resource> resources;

    public List<Equipment> getEquipmentTypeSeq() {
        return equipmentTypeSeq;
    }

    public void setEquipmentTypeSeq(List<Equipment> equipmentTypeSeq) {
        this.equipmentTypeSeq = equipmentTypeSeq;
    }

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
