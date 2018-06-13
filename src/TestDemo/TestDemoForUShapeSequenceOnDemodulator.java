package TestDemo;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;
import Wireless.UShapeSequence;

public class TestDemoForUShapeSequenceOnDemodulator {

	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		UdpCommuncator udp=new UdpCommuncator();
		Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
		udp.Initializate(command);
	//	DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");  
	
	
		/*������*/
		byte[] data=new byte[1024]; 
		
		UShapeSequence uss=new UShapeSequence();
		double WL;
		double temperature=20;
		for (int i = 0; i < 20000; i++) {
			data=udp.recieve();
			WL=udp.getWaveLength(data);
			uss.add(WL);
		//	System.out.println("���յ��Ĳ�����"+WL);
			if (uss.isUpdatedWl) {
				
				System.out.println("�����˲���,"+"�����ǣ�"+uss.getTargetWl()+"nm;");
			
			
			
				temperature=Demodulator.getTemperature(WL, 10.2, 30.6, 1543.331);
				System.out.println("�������¶�,"+"�¶��ǣ�"+temperature+"��;");			
				Toolkit.getDefaultToolkit().beep(); 
			}	
			
		}
		
		
	}

}
