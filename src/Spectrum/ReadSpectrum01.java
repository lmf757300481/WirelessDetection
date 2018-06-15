package Spectrum;

import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import OMP.Command_OPM102L;
import OMP.UdpCommuncator;

public class ReadSpectrum01 {
	
	
	
	public static  void 	recieve(UdpCommuncator udp)
	{
		try {
			
			System.out.println(Arrays.toString(udp.recieve()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		UdpCommuncator udp=new UdpCommuncator();
		
	    
	    Thread thread = new Thread();
	 // 继承 Thread 类来实现多线程
        new Thread(){        	
            
        	@Override
            public void run(){
        		this.setPriority(MAX_PRIORITY);
            	Command_OPM102L command=Command_OPM102L.Get_Optic_Spectrum;
            	 try {            		 
            		System.out.println("子线程ID："+Thread.currentThread().getPriority());
					udp.Initializate(command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
            }
        }.start();
	  
       
		/*缓冲器*/
		byte[] data=new byte[1024]; 
		
		for (int i = 0; i < 100; i++) {
			recieve(udp);
		}
		
		System.out.println("------end------");
			
			
			
		

	}

	

}
