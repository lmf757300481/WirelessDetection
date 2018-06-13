package HighSpeed;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;
import Wireless.UShapeSequence;

public class HighSpeedDmeo01 {


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
				UdpCommuncator udp=new UdpCommuncator();
				
				DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");  
				DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
			 	udp.Initializate(command);
				
				/*缓冲器*/
				byte[] data=new byte[1024]; 
			
				
				/*每运行一次一个文件夹*/
				File f_folder = new File("Data"+File.separator+"1_K_Hz"+File.separator+"Laboratory_Manual"+File.separator+sdf1.format(new Date()));
				if (!f_folder.exists()) {//如果文件夹不存在
					f_folder.mkdirs();//创建文件夹
					}
			
				UShapeSequence uss=new UShapeSequence();
				double temperature;
					File f = new File(f_folder,"out.txt");
					try {
						f.createNewFile();
					} catch (IOException e) {
						
						e.printStackTrace();
					}		
					FileOutputStream fileOutputStream;
					fileOutputStream = new FileOutputStream(f);
					PrintStream printStream = new PrintStream(fileOutputStream);
					System.setOut(printStream);		
					SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS"); 
						for (int j = 0; j <16800; j++) {
							data=udp.recieve();
							
							double wl=udp.getWaveLength(data);
							if (wl<=1565&&wl>=1530) {
								Toolkit.getDefaultToolkit().beep(); 
							}
							//System.out.println(wl);
							uss.add(wl);
							 
							if (uss.isUpdatedWl) {
								//System.out.print(sdf2.format(System.currentTimeMillis()+"---"));
								System.out.println("更新了波长,"+"波长是："+uss.getTargetWl()+"nm;");
								
								//System.out.print(sdf2.format(System.currentTimeMillis()+"---"));
								temperature=Demodulator.getTemperature(uss.getTargetWl(), 10.2, 30.6, 1543.331);
								//System.out.println("更新了温度,"+"温度是："+temperature+"℃;");			
								Toolkit.getDefaultToolkit().beep(); 
							}	
				System.out.println("----------结束-----------");		
				}
		}
}