package TestDemo;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import CrossheadBush.RotationalSpeed;
import OMP.UdpCommuncator;


public class Main_WriteWavelengthAndPower {
	

	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		UdpCommuncator udp=new UdpCommuncator();
		//System.out.println(Arrays.toString(command.getIndex()));	    
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");  
		DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		//Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
	   //	udp.Initializate(command);
		
		
		/*缓冲器*/
		byte[] data=new byte[1024]; 
		
		/*每运行一次一个文件夹*/
		File f_folder = new File("Data"+File.separator+"1_K_Hz"+File.separator+sdf1.format(new Date()));
		if (!f_folder.exists()) {//如果文件夹不存在
			f_folder.mkdirs();//创建文件夹
			}
		int recordCount=600;//大约是1s一次
		
		/*每运行一次，一个文件夹下，存储多个文件*/
		for (int i = 0; i < recordCount; i++) {			
			
			String fileName=sdf2.format(new Date())+""+".txt";//现场去，用这个
			//String fileName="out.txt";//在实验室用这个
			RotationalSpeed rs=new RotationalSpeed();
			File f = new File(f_folder,fileName);
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
				FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream(f);
				PrintStream printStream = new PrintStream(fileOutputStream);
			System.setOut(printStream);		
			
				for (int j = 0; j <1500; j++) {
					data=udp.recieve();
					System.out.println(j+","+sdf2.format(new Date())+","+Arrays.toString(data));
					//System.out.println(Arrays.toString(udp.getMutilWaveLength(data)));
					//System.out.println(Arrays.toString(udp.getMutilPower(data)));	
					double[] temp=udp.getMutilData(data);
					System.out.println(j+","+Arrays.toString(udp.getMutilData(data)));
					if(temp!=null&&temp.length>0)
					{
						for (int k = 0; k < temp.length; k++) {
							if(temp[k]<=1565&&temp[k]>=1530)
							{
								 Toolkit.getDefaultToolkit().beep(); 
								 rs.refreshSpeed(new Date());
								 System.out.println(j+","+rs.refreshSpeed(new Date()));
							}
						}
					}
					System.gc();
					// 或者下面，两者等价
					//Runtime.getRuntime().gc();
					
				}
		}
		
		
		
		System.out.println("----------结束-----------");			

	}




}
