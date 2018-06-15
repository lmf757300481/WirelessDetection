package Spectrum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import OMP.Command_OPM102L;
import OMP.UdpCommuncator;

public class ReadSpectrum2  {
	
	public static int timer;
		
	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub		
		
		UdpCommuncator udp=new UdpCommuncator();
		Command_OPM102L command=Command_OPM102L.Get_Optic_Spectrum;
	    udp.Initializate(command);	
	    timer=750;
	    DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");
		DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		/* ÿ����һ��һ���ļ��� */
		
		for (int j = 0; j < 20; j++) {
			
		timer=timer+j*10;
		File f_folder = new File("Data" + File.separator + "������Ѷ����׵�˯��ʱ��" );
		if (!f_folder.exists()) {// ����ļ��в�����
			f_folder.mkdirs();// �����ļ���
		}
		String fileName = timer + "ms" + "���.txt";// �ֳ�ȥ�������
		File f = new File(f_folder, fileName);
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
		
		
		
	    Spectrum spectrum=new Spectrum();
		new Thread() {
			public void run() {
				int sendCount=0;
				while(true) {
					Command_OPM102L command=Command_OPM102L.Get_Optic_Spectrum;
			    	 try {  
			    		 sendCount++;
			    		System.out.println("���ͼ�����"+sendCount+":���Ͷ�ȡ��������-���̣߳�"+Thread.currentThread().getPriority());
						udp.Initializate(command);
						try {
							Thread.sleep(timer);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					//	System.out.println(Thread.currentThread().getName() + "--" );
				}				
			}
		}.start();
		
		/*������*/
		byte[] data=new byte[1024]; 
		int recieveCount=0;
		long startTime=System.currentTimeMillis();
		for (int i = 0; i < 200; i++) {
			data=udp.recieve();			
			recieveCount++;
			System.out.println("���ܼ�����"+recieveCount+"   "+Arrays.toString(data));
			spectrum.recieveData(data);
			if(spectrum.isUpdateSpectrum)
			{
				System.out.println("�����ǣ�"+Arrays.toString(spectrum.power_Sequence));
				System.out.println("���׵ĳ����ǣ�"+spectrum.power_Sequence.length);
			}
		}
		long endTime=System.currentTimeMillis();
		System.out.println("����ʱ��"+(endTime-startTime)/1000+"s");
		System.out.println("------end------");
		
	}
	}
}
