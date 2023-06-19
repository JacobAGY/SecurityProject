package com.szu.cn.Security;

import java.util.*;

public class ShortTimePlan {
    private List<Equipment> equipmentList;
    private List<Process> processList;
    private List<Resource> resourceList;

    public ShortTimePlan(List<Equipment> equipmentList, List<Process> processList, List<Resource> resourceList) {
        this.equipmentList = equipmentList;
        this.processList = processList;
        this.resourceList = resourceList;
    }

    public void schedule() {
        int totalTime = 0;

        while (!equipmentList.isEmpty()) {
            // Group the equipments by their current process
            Map<String, List<Equipment>> equipmentGroups = groupEquipmentsByCurrentProcess();

            // Select a group of equipments to process based on the shortest process time
            List<Equipment> selectedEquipments = selectEquipmentsWithShortestProcessTime(equipmentGroups);

            // Process the selected equipments
            for (Equipment equipment : selectedEquipments) {
                Process currentProcess = getCurrentProcess(equipment);

                // Check if the required resources are available

                if (checkResourceAvailability(currentProcess.getResourceSeq())) {
                    // Allocate the resources
                    allocateResources(currentProcess.getResourceSeq());

                    // Process the equipment
                    processEquipment(equipment, currentProcess);

                    // Release the allocated resources
                    releaseResources(currentProcess.getResourceSeq());

                    // Update the total time
                    totalTime += currentProcess.getTime();
                }
            }
        }

        System.out.println("Total time: " + totalTime);
    }

    private Map<String, List<Equipment>> groupEquipmentsByCurrentProcess() {
        Map<String, List<Equipment>> equipmentGroups = new HashMap<>();

        for (Equipment equipment : equipmentList) {
            String currentProcess = equipment.getProcessCur();
            equipmentGroups.putIfAbsent(currentProcess, new ArrayList<>());
            equipmentGroups.get(currentProcess).add(equipment);
        }

        return equipmentGroups;
    }

    private List<Equipment> selectEquipmentsWithShortestProcessTime(Map<String, List<Equipment>> equipmentGroups) {
        List<Equipment> selectedEquipments = new ArrayList<>();
        int shortestTime = Integer.MAX_VALUE;

        for (List<Equipment> group : equipmentGroups.values()) {
            int groupTime = getCurrentProcess(group.get(0)).getTime();
            if (groupTime < shortestTime) {
                shortestTime = groupTime;
                selectedEquipments = group;
            }
        }

        return selectedEquipments;
    }

    private Process getCurrentProcess(Equipment equipment) {
        String currentProcessName = equipment.getProcessCur();

        for (Process process : processList) {
            if (process.getName().equals(currentProcessName)) {
                return process;
            }
        }

        return null;
    }

    private boolean checkResourceAvailability(Map<String, Integer> resourceSeq) {
        for (Map.Entry<String, Integer> entry : resourceSeq.entrySet()) {
            String resourceName = entry.getKey();
            int requiredNum = entry.getValue();

            for (Resource resource : resourceList) {
                if (resource.getName().equals(resourceName) && resource.getNum() < requiredNum) {
                    return false;
                }
            }
        }

        return true;
    }

    private void allocateResources(Map<String, Integer> resourceSeq) {
        for (Map.Entry<String, Integer> entry : resourceSeq.entrySet()) {
            String resourceName = entry.getKey();
            int requiredNum = entry.getValue();

            for (Resource resource : resourceList) {
                if (resource.getName().equals(resourceName)) {
                    resource.setNum(resource.getNum() - requiredNum);
                    break;
                }
            }
        }
    }

    private void releaseResources(Map<String, Integer> resourceSeq) {
        for (Map.Entry<String, Integer> entry : resourceSeq.entrySet()) {
            String resourceName = entry.getKey();
            int requiredNum = entry.getValue();

            for (Resource resource : resourceList) {
                if (resource.getName().equals(resourceName)) {
                    resource.setNum(resource.getNum() + requiredNum);
                    break;
                }
            }
        }
    }

    private void processEquipment(Equipment equipment, Process currentProcess) {
        // Simulate the processing of the equipment
        System.out.println("Processing equipment: " + equipment.getName() + ", Process: " + currentProcess.getName());

        // Move to the next process
        ArrayList<String> processSeq = equipment.getProcessSeq();
        String currentProcessName = equipment.getProcessCur();
        int currentProcessIndex = Arrays.asList(processSeq).indexOf(currentProcessName);
        int nextProcessIndex = currentProcessIndex + 1;

        if (nextProcessIndex < processSeq.size()) {
            equipment.setProcessCur(processSeq.get(nextProcessIndex));
        } else {
            // Remove the equipment from the list if it has completed all processes
            equipmentList.remove(equipment);
        }
    }

    public static void main(String[] args) {
        // Create resources
        Resource resource1 = new Resource("Resource1", 3);
        Resource resource2 = new Resource("Resource2", 3);
        Resource resource3 = new Resource("Resource3", 3);

        // Create processes
        Process process1 = new Process("Process1", 5, new HashMap<>());
        process1.getResourceSeq().put("Resource1", 1);
        Process process2 = new Process("Process2", 8, new HashMap<>());
        process2.getResourceSeq().put("Resource2", 1);
        Process process3 = new Process("Process3", 4, new HashMap<>());
        process3.getResourceSeq().put("Resource2", 1);
        process3.getResourceSeq().put("Resource3", 1);


        // Create equipments
        Equipment equipment1 = new Equipment("Equipment1", 3, new String[]{"Process1", "Process2","Process3"});
        Equipment equipment2 = new Equipment("Equipment2", 2, new String[]{"Process1", "Process2"});
        Equipment equipment3 = new Equipment("Equipment3", 1, new String[]{"Process1", "Process2"});

        // Create equipment scheduler
        List<Equipment> equipmentList = new ArrayList<>();
        equipmentList.add(equipment1);
        equipmentList.add(equipment2);
        equipmentList.add(equipment3);

        List<Process> processList = new ArrayList<>();
        processList.add(process1);
        processList.add(process2);

        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(resource1);
        resourceList.add(resource2);

        ShortTimePlan scheduler = new ShortTimePlan(equipmentList, processList, resourceList);
        scheduler.schedule();
    }


}
