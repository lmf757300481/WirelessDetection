package TestDemo;

import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import OMP.Command_OPM102L;
import OMP.UdpCommuncator;
public class Test_If_Get_WL_Ans_Power_Can_Stop {
	public static boolean isEnding=false;
	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		UdpCommuncator udp=new UdpCommuncator(); //û��ʲô������ȥ	 
	  
		byte[] data=new byte[1024]; 
		long startTime=System.currentTimeMillis();
	    for (int i = 0; i < 6; i++) {
	    	
	    Command_OPM102L command=Command_OPM102L.Get_Optic_Spectrum;
	    udp.Initializate(command);
	    					
	    	       
	    	for (int j = 0; j <= 4; j++) {/*����5������*/						
	    	System.out.print("��"+j+"�ν���");		
			data=udp.recieve();
			System.out.println(Arrays.toString(data));	
			
				}
	    	       System.out.println("--------------------");	
	    		
		}	
	    long endTime=System.currentTimeMillis();
	    System.out.println("����ʱ��"+(endTime-startTime)/1000.0+"s");
	    isEnding=true;
	    System.out.println("end");		
	}
}
