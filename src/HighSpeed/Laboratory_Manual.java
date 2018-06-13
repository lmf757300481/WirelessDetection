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

		/* ������ */
		byte[] data = new byte[1024];
		UShapeSequence uss = new UShapeSequence();
		double temperature;

		System.out.println("��ʼ����ѭ��");	
		
		int count=0; //����������¼�Թ����
		for (int j = 0; j < 16800; j++) {
			data = udp.recieve();
			double wl = udp.getWaveLength(data);			
			uss.add(wl);

			if (uss.isUpdatedWl) {
				count++;
				System.out.println(count+",ʱ��," +sdf2.format(System.currentTimeMillis()));
				System.out.println(count+",����," + uss.getTargetWl() );
				System.out.println(count+",U�ͳ���," + uss.getLastWlSequence().size());
				System.out.println(count+",��������," + uss.getLastWlSequence().toString());				
				temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.2, 30.6, 1543.331);
				//temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.6, 40, 1534.214); //������FBG-1534
				//temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.7, 40, 1536.308); //������FBG-1536
				//temperature = Demodulator.getTemperature(uss.getTargetWl(), 10.6, 40, 1538.284); //������FBG-1538
				System.out.println(count+",�¶�," + temperature + "��;");
				System.out.println();
			}

		}
		
		System.out.println("----------����-----------");

	}

}
