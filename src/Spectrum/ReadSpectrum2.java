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
		/* 每运行一次一个文件夹 */
		
		for (int j = 0; j < 20; j++) {
			
		timer=timer+j*10;
		File f_folder = new File("Data" + File.separator + "分析最佳读光谱的睡眠时间" );
		if (!f_folder.exists()) {// 如果文件夹不存在
			f_folder.mkdirs();// 创建文件夹
		}
		String fileName = timer + "ms" + "输出.txt";// 现场去，用这个
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
			    		System.out.println("发送计数："+sendCount+":发送读取光谱命令-子线程："+Thread.currentThread().getPriority());
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
		
		/*缓冲器*/
		byte[] data=new byte[1024]; 
		int recieveCount=0;
		long startTime=System.currentTimeMillis();
		for (int i = 0; i < 200; i++) {
			data=udp.recieve();			
			recieveCount++;
			System.out.println("接受计数："+recieveCount+"   "+Arrays.toString(data));
			spectrum.recieveData(data);
			if(spectrum.isUpdateSpectrum)
			{
				System.out.println("光谱是："+Arrays.toString(spectrum.power_Sequence));
				System.out.println("光谱的长度是："+spectrum.power_Sequence.length);
			}
		}
		long endTime=System.currentTimeMillis();
		System.out.println("共用时："+(endTime-startTime)/1000+"s");
		System.out.println("------end------");
		
	}
	}
}
