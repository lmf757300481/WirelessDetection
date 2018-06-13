package HighSpeed;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;
import Wireless.UShapeSequence;

public class Laboratory_Manual {

	public static void main(String[] args) throws SocketException, IOException {
		
		UdpCommuncator udp = new UdpCommuncator();
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");
		DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		Command_OPM102L command = Command_OPM102L.Start_Control_Get_Peak_WaveLength;
		udp.Initializate(command);

		/* 缓冲器 */
		byte[] data = new byte[1024];
		UShapeSequence uss = new UShapeSequence();
		double temperature;

		System.out.println("开始进入循环");	
		
		int count=0; //计数器，记录对光次数
		for (int j = 0; j < 16800; j++) {
			data = udp.recieve();
			double wl = udp.getWaveLength(data);			
			uss.add(wl);

			if (uss.isUpdatedWl) {
				count++;
				System.out.println(count+",时间," +sdf2.format(System.currentTimeMillis()));
				System.out.println(count+",波长," + uss.getTargetWl() );
				System.out.println(count+",U型长度," + uss.getLastWlSequence().size());
				System.out.println(count+",波长序列," + uss.getLastWlSequence().toString());				
				temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.2, 30.6, 1543.331);
				//temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.6, 40, 1534.214); //传感器FBG-1534
				//temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.7, 40, 1536.308); //传感器FBG-1536
				//temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.6, 40, 1538.284); //传感器FBG-1538
				System.out.println(count+",温度," + temperature + "℃;");
				System.out.println();
			}

		}
		
		System.out.println("----------结束-----------");

	}

}
