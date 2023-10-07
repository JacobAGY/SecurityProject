package com.szu.cn.Security.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Drawgraph extends JFrame {

	int numofEquipments = 0;  //供给车位数量
	double timePeriod = 0;//总时长
	int numOfProcess = 0;
	double[][] equipmentsTimeWindows; //装备时间窗
	List<double[][]> processesTimeWindows;  //所有装备工序时间窗

	List<String> equipmentsList; //装备名称集合
	HashMap<String,Integer> process_color; // 工序对应颜色
	List<List<String>> processesList; //工序名称集合
	// 颜色数组，以区分不同的原油类型
	private final static Color[] colors = {new Color(0, 0, 0), new Color(127, 127, 127),
			new Color(195, 195, 195), new Color(136, 0, 21), new Color(185, 122, 87), new Color(237, 28, 36),
			new Color(255, 174, 201), new Color(255, 127, 39), new Color(255, 242, 0), new Color(239, 228, 176),
			new Color(34, 117, 76), new Color(181, 230, 29), new Color(0, 162, 232), new Color(153, 217, 234),
			new Color(63, 72, 204), new Color(112, 146, 190), new Color(163, 73, 164), new Color(200, 191, 231),
			new Color(89, 173, 154), new Color(8, 193, 194), new Color(9, 253, 76), new Color(153, 217, 234),
			new Color(199, 73, 4)};



	public Drawgraph(int timePeriod, List<String> equipmentsList,double[][] equipmentsTimeWindows,
					 int numOfProcess,List<List<String>> processesList,List<double[][]> processesTimeWindows) {
		this.numofEquipments = equipmentsList.size();
		this.timePeriod = timePeriod;
		this.equipmentsList = equipmentsList;
		this.numOfProcess = numOfProcess;
		this.processesList = processesList;
		this.equipmentsTimeWindows = equipmentsTimeWindows;
		this.processesTimeWindows = processesTimeWindows;

		this.process_color = new HashMap<>();
		int i = 0;
		for (List<String> processes:processesList) {
			for (String name:processes) {
				if(!this.process_color.containsKey(name)){
					this.process_color.put(name,i++);
				}
			}
		}
	}
	
	public void draw() {
		String url = "src/com/szu/cn/Security/Gantt";
		BufferedImage image = new BufferedImage(2200,1200,BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();//获得一个图形类

		try {

			g.setColor(new Color(0xf5f5f5));
			g.setColor(Color.white);
		    g.fillRect(0,0,2200,1200);
			g.setColor(Color.black);
			// drawRect画一个方框，向下画
			g.drawRect(100, 100, 2000, 1000);
			
			//画横坐标轴
			for (int i = 0; i < (int)(timePeriod+1); i+=5) {
				// drawLine画线
				g.drawLine((int)(200+1800/timePeriod*i), 90, (int)(200+1800/timePeriod*i), 100);
				// drawString填充信息，e.g.数字
				g.drawString(""+i, (int)(200+1800/timePeriod*i-3), 80);
			}
//
			//画纵坐标轴
			for (int k = 0; k < numofEquipments; k++) {

				g.drawRect((int) (200+1800/timePeriod*equipmentsTimeWindows[k][0]),
						1000-40*k,
						(int) (1800/timePeriod*(equipmentsTimeWindows[k][1]-equipmentsTimeWindows[k][0])),
						20);
				g.drawString(equipmentsList.get(k), 140, 1000-40*k+15);
			}
//
			//画工序的时间窗
			for (int k = 0; k < numofEquipments; k++) {
				double[][] processTimeWindows = processesTimeWindows.get(k);
				for (int i = 0; i < processesList.get(k).size(); i++) {
					int color_index = process_color.get(processesList.get(k).get(i));
					g.setColor(colors[color_index]);
					g.fillRect((int)(200+1800/timePeriod*processTimeWindows[i][0]),
							1000-40*k,
							(int) (1800/timePeriod*(processTimeWindows[i][1]-processTimeWindows[i][0])),
							20);
					g.drawString(processesList.get(k).get(i),
							(int)(200+1800/timePeriod*processTimeWindows[i][0]),
							1000-40*k);

				}
			}

		    ImageIO.write(image, "png", new File(url + "/gantt.png"));
         } catch (Exception e) {
             e.printStackTrace();
         }
	}

	public void draw(String url) {
		String url1 = "src/com/szu/cn/Security/Gantt";
		BufferedImage image = new BufferedImage(2200,1200,BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();//获得一个图形类

		try {

			g.setColor(new Color(0xf5f5f5));
			g.setColor(Color.white);
			g.fillRect(0,0,2200,1200);
			g.setColor(Color.black);
			// drawRect画一个方框，向下画
			g.drawRect(100, 100, 2000, 1000);

			//画横坐标轴
			for (int i = 0; i < (int)(timePeriod+1); i+=5) {
				// drawLine画线
				g.drawLine((int)(200+1800/timePeriod*i), 90, (int)(200+1800/timePeriod*i), 100);
				// drawString填充信息，e.g.数字
				g.drawString(""+i, (int)(200+1800/timePeriod*i-3), 80);
			}
//
			//画纵坐标轴
			for (int k = 0; k < numofEquipments; k++) {

				g.drawRect((int) (200+1800/timePeriod*equipmentsTimeWindows[k][0]),
						1000-40*k,
						(int) (1800/timePeriod*(equipmentsTimeWindows[k][1]-equipmentsTimeWindows[k][0])),
						20);
				g.drawString(equipmentsList.get(k), 140, 1000-40*k+15);
			}
//
			//画工序的时间窗
			for (int k = 0; k < numofEquipments; k++) {
				double[][] processTimeWindows = processesTimeWindows.get(k);
				for (int i = 0; i < processesList.get(k).size(); i++) {
					int color_index = process_color.get(processesList.get(k).get(i));
					g.setColor(colors[color_index]);
					g.fillRect((int)(200+1800/timePeriod*processTimeWindows[i][0]),
							1000-40*k,
							(int) (1800/timePeriod*(processTimeWindows[i][1]-processTimeWindows[i][0])),
							20);
					g.drawString(processesList.get(k).get(i),
							(int)(200+1800/timePeriod*processTimeWindows[i][0]),
							1000-40*k);

				}
			}

			ImageIO.write(image, "png", new File(url1 +  "/" + url + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}