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


import OMP.Demodulator;
import OMP.UdpCommuncator;
import Wireless.UShapeSequence;


public class TestWrite {
	
 
	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		
	
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss"); 
		//Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
	   //	udp.Initializate(command);
		
		
	
		
	
		
		/*ÿ����һ��һ���ļ���*/
		File f_folder = new File("Data"+File.separator+"1_K_Hz"+File.separator+"Laboratory_Manual"+File.separator+sdf.format(new Date()));
		if (!f_folder.exists()) {//����ļ��в�����
			f_folder.mkdirs();//�����ļ���
			}
	
		
	
		
			
			
			String fileName=sdf.format(new Date())+""+".txt";//�ֳ�ȥ�������
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
			
				for (int j = 0; j <168; j++) {
				
					//System.out.println(j+","+sdf2.format(new Date())+","+Arrays.toString(data));
					//System.out.println(Arrays.toString(udp.getMutilWaveLength(data)));
					//System.out.println(Arrays.toString(udp.getMutilPower(data)));	
					//double[] temp=udp.getMutilData(data);
				
					 
					
						System.out.println("�����˲���,"+"�����ǣ�"+1520.001+"nm;");
						System.out.print(sdf.format(System.currentTimeMillis())+"---");
				System.out.println("����������"+1520.001);//+"nm;");
						System.out.print(System.currentTimeMillis()+"---");
						
						System.out.println("�������¶�,"+"�¶��ǣ�"+20+"��;");			
						Toolkit.getDefaultToolkit().beep(); 
					}	
					
				
		System.out.println("----------����-----------");			

	}

	}
	



