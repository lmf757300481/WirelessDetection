package TestDemo;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import FBG.OpticSpectrum;
import OMP.Command_OPM102L;
import OMP.UdpCommuncator;

public class Main_Test_Spectrum {
	
	
	
	public static void main(String[] args) throws SocketException, IOException {
		System.out.println(Thread.currentThread() .getStackTrace()[1].getClassName()+"开始");
		UdpCommuncator udp=new UdpCommuncator();
		//Command_OPM102L command_Get_Optic_Spectrum=Command_OPM102L.Get_Optic_Spectrum;
		//udp.Initializate(command_Get_Optic_Spectrum);
		
		
//		//测试一下，启停控制，波长和强度返回
//		Command_OPM102L command_Start_Control_Get_Peak_WaveLength_And_Power=Command_OPM102L.Start_Control_Get_Peak_WaveLength_And_Power;
//		udp.Initializate(command_Start_Control_Get_Peak_WaveLength_And_Power);
		
//		//测试一下，启停控制，只有波长返回
//				Command_OPM102L command_Start_Control_Get_Peak_WaveLength=Command_OPM102L.Start_Control_Get_Peak_WaveLength;
//				udp.Initializate(command_Start_Control_Get_Peak_WaveLength);
		
		


//		
//		Command_OPM102L command=Command_OPM102L.Get_WaveLength_Start_And_Interval_And_Count_And_ChannelNo_And_Data_Frames;
//		udp.Initializate(command);


		Command_OPM102L command=Command_OPM102L.Get_Optic_Spectrum;   //返回的帧数序号也是从0-->N-1   
		udp.Initializate(command);
		System.out.println(new Date());
		
		/*缓冲器*/
		byte[] data=new byte[1024]; 
		for (int m = 0; m < 5; m++) {
			
			udp.Initializate(command);
			System.out.println("给一个");
		
		for (int i = 0; i < 10; i++) {
			
			data=udp.recieve();			
			System.out.println(new Date()+"  "+  Arrays.toString(data));
			
			if(data[0]==0x01&&data[1]==0x16)
			{
				double[] temp=new double[512];
				int count=0;
				for (int j= 0; j < data.length; j=j+2) {
					
					temp[count]=OpticSpectrum.parsePwer(data[j], data[j+1]);
					count++;
				}
				System.out.println(new Date()+" 光谱强度 "+  Arrays.toString(temp));
			}

			
			//System.out.println(new Date()+"  "+ Arrays.toString(UdpCommuncator.getWaveLengthStartAndIntervalAndCountAndChannelNoAndDataFrames(data)));
		}
		
		}	
	}

	
}
