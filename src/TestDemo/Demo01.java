package TestDemo;

import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import FBG.FBGSensor;
import FBG.FBGSignal;
import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;
import Temperature.TemperaturePoint;
import Temperature.TemperaturePoints;

public class Demo01 {

	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		FBGSignal fbgSignal=new FBGSignal();
		//Command_OPM102L command=Command_OPM102L.Get_Peak_WaveLength;
		UdpCommuncator udp=new UdpCommuncator();
		//System.out.println(Arrays.toString(command.getIndex()));
	
		
		 FBGSensor fbgSensor=new FBGSensor();
		 double temperature=0;
	     //	 fbgSensor.read();
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
		Command_OPM102L command_OPM102L=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
		udp.Initializate(command_OPM102L);
		
		TemperaturePoints tps=new TemperaturePoints();
		for (int i = 0; i < 12; i++) {
			
			byte[] data = udp.recieve();
			double wavelength=udp.getWaveLength(data);
			fbgSignal.setPeakWavelength(wavelength);
			fbgSignal.setDate(new Date());
			System.out.println(""+sdf2.format(fbgSignal.getDate())+"   "+Arrays.toString(data));			
			temperature=Demodulator.getTemperature(fbgSensor, fbgSignal);
			System.out.println("当前的时间是："+" "+sdf2.format(fbgSignal.getDate())+"      "+"FBG的当前波长是："+fbgSignal.getPeakWavelength()+"    "+"FBG的当前温度是:"+temperature);
			TemperaturePoint current=new TemperaturePoint(temperature,new Date());
			tps.add(current);
			tps.check(30, 1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("结束");
		
		
//		Iterator iter =fbgSensor.standard.entrySet().iterator();
//		while (iter.hasNext()) {
//		Map.Entry entry = (Map.Entry) iter.next();
//		Object key = entry.getKey();
//		Object val = entry.getValue();
		
		
//		System.out.println(key.toString()+"  "+val.toString());
//		}
		
		
		
	}

}
