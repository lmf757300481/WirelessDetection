package HighSpeed;

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
import OMP.Command_OPM102L;
import OMP.Demodulator;
import OMP.UdpCommuncator;


public class Three_FBG_Record {
	

	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		UdpCommuncator udp=new UdpCommuncator();
		//System.out.println(Arrays.toString(command.getIndex()));	    
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");  
		DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
	  udp.Initializate(command);
		
		
		/*������*/
		byte[] data=new byte[1024]; 
		
		
		
		/*ÿ����һ��һ���ļ���*/
		File f_folder = new File("Data"+File.separator+"1_K_Hz"+File.separator+"SINOPEC"+File.separator+"Three_FBGs"+File.separator+"Low_Speed"+File.separator+sdf1.format(new Date()));
		//File f_folder = new File("Data"+File.separator+"1_K_Hz"+File.separator+"SINOPEC"+File.separator+"Three_FBGs"+File.separator+"High_Speed"+File.separator+sdf1.format(new Date()));
		
		if (!f_folder.exists()) {//����ļ��в�����
			f_folder.mkdirs();//�����ļ���
			}
		int recordCount=10;//��Լ��1sһ��
		double temperature;
		
		for (int i = 0; i < recordCount; i++) {
			
		
		/*ÿ����һ�Σ�һ���ļ����£��洢����ļ�*/
			
			
			String fileName=sdf2.format(new Date())+""+".txt";//�ֳ�ȥ�������
			//String fileName="out.txt";//��ʵ���������
			
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
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS"); 
				for (int j = 0; j <1680; j++) {
					data=udp.recieve();
					//System.out.println(j+","+sdf2.format(new Date())+","+Arrays.toString(data));
					//System.out.println(Arrays.toString(udp.getMutilWaveLength(data)));
					//System.out.println(Arrays.toString(udp.getMutilPower(data)));	
					double[] temp=udp.getMutilData(data);
					
					
					//�ȴ�
					if(temp!=null&&temp.length>0)
					{
						System.out.println(j+","+sdf.format(System.currentTimeMillis())+","+Arrays.toString(udp.getMutilData(data)));
						Toolkit.getDefaultToolkit().beep(); 
						for (int k = 0; k < temp.length; k++) {
							if(temp[k]<=1565&&temp[k]>=1530)
							{
								if(k%3==0) {
									temperature = Demodulator.getTemperature(temp[k], 10.6, 40, 1534.214); //������FBG-1534
									System.out.println("FBG1,����,"+temp[k]);
									System.out.println("FBG1,�¶�,"+temperature);
								}else if(k%3==1) {
									temperature = Demodulator.getTemperature(temp[k], 10.7, 40, 1536.308); //������FBG-1536
									System.out.println("FBG2,����,"+temp[k]);
									System.out.println("FBG2,�¶�,"+temperature);
								}
								else if(k%3==2) {
									temperature = Demodulator.getTemperature(temp[k], 10.6, 40, 1538.284); //������FBG-1538
									System.out.println("FBG3,����,"+temp[k]);
									System.out.println("FBG3,�¶�,"+temperature);
								}					
								
								
							}
						}
						System.out.println();
					}
					
				}
		
		
		
		
		System.out.println("----------����-----------");			

	}

	}
	


}
