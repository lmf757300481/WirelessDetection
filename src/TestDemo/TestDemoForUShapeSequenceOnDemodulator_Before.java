package TestDemo;

import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;
import Wireless.UShapeSequence;

public class TestDemoForUShapeSequenceOnDemodulator_Before {

	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		UdpCommuncator udp=new UdpCommuncator();
		Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
		udp.Initializate(command);
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");  
		/*������*/
		byte[] data=new byte[1024]; 
		double WL;
		double temperature=20;
		UShapeSequence uss=new UShapeSequence();
		for (int i = 0; i < 3000000; i++) {
			data=udp.recieve();
		//	System.out.println(sdf2.format(System.currentTimeMillis())+","+Arrays.toString(data));
			 WL=udp.getWaveLength(data);
		//	System.out.println();
		//	System.out.println(WL);
			uss.add(WL);
			if (uss.isUpdatedWl) {
				System.out.println(sdf2.format(System.currentTimeMillis()));
				System.out.println("�����˲���,"+"�����ǣ�"+uss.getTargetWl()+"nm;");
				temperature=Demodulator.getTemperature(uss.getTargetWl(), 10.2, 39.7, 1543.418);
				System.out.println("�������¶�,"+"�¶��ǣ�"+temperature+"��;");
				System.out.println();
				
			}
		
			
		}
		
		
	}

}
