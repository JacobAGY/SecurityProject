package com.szu.cn.Security.utils;

import com.szu.cn.Security.Pojo.Equipment;
import com.szu.cn.Security.Pojo.Result;
import com.szu.cn.Security.ShortTimePlan;
import com.szu.cn.Security.Test.Test;
import com.szu.cn.Security.Test.TestPojo;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class functionMain {

	public static void main(String[] args) throws IOException {

		Test test = new Test();
		TestPojo testPojo = test.testCase1();
		testPojo = test.parameter_TestPojo(testPojo);
		TestPojo shortTime_testPojo = (TestPojo) SerializationUtils.clone(testPojo);

		ShortTimePlan shortTime_scheduler = new ShortTimePlan(shortTime_testPojo.getEquiments(),shortTime_testPojo.getResources());
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

			double[][] tmp_processTimeWindows = new double[processSeq.size()][2];
			for (List<String> processesName:processesList) {
				if(processesName.size() > numOfProcess){
					numOfProcess = processesName.size();
				}
				int j = 0;
				for (String processName:processesName) {
					tmp_processTimeWindows[j][0] = processSeq.get(processName)-processSeq_Origin.get(processName);
					tmp_processTimeWindows[j][1] = processSeq.get(processName);
					j++;

				}

			}

			processesTimeWindows.add(tmp_processTimeWindows);

		}




		Drawgraph drawgraph = new Drawgraph(timePeriod,equipmentsList,equipmentsTimeWindows,numOfProcess,processesList,processesTimeWindows);
		drawgraph.draw();


	}
}
