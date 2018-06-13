package OMP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class UdpCommuncator{
	
	private DatagramSocket client = null;//new DatagramSocket(4010);	
	private DatagramPacket packet_send = null;//new DatagramPacket(data_send, data_send.length,
			//InetAddress.getByName("192.168.0.119"), 4010);
	private DatagramPacket packet_recieve =null;// new DatagramPacket(container, container.length);	
	private byte[] command_send = new byte[4];
	private byte[] container = new byte[1024];	
	private double peakWaveLength=0;	
	
	public UdpCommuncator(Command_OPM102L command) throws SocketException, IOException 
	{
		this.client=new DatagramSocket(4010); 
		command_send=Arrays.copyOf(command.getIndex(),command.getIndex().length);
		//System.out.println(Arrays.toString(command_send));	
		packet_send = new DatagramPacket(command_send, command_send.length,
				InetAddress.getByName("192.168.0.119"), 4010);		
		packet_recieve= new DatagramPacket(container, container.length);	
		this.client.send(packet_send);
		
	}
	
	
	
	public UdpCommuncator() throws SocketException, IOException {
		super();
		// TODO Auto-generated constructor stub
		this.client=new DatagramSocket(4010); 
		Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength_And_Power;
		command_send=Arrays.copyOf(command.getIndex(),command.getIndex().length);
		System.out.println(Arrays.toString(command_send));		
		packet_send = new DatagramPacket(command_send, command_send.length,
				InetAddress.getByName("192.168.0.119"), 4010);	
		packet_recieve= new DatagramPacket(container, container.length);
		this.client.send(packet_send);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void send() throws IOException
	{
		this.client.send(packet_send);
	}
	
	public byte[] recieve() throws IOException
	{
		client.receive(packet_recieve);
		byte[] data = packet_recieve.getData();
		return data;
	}
	
	public void Initializate(OMP.Command_OPM102L command) throws IOException
	{
		//Command_OPM102L command=Command_OPM102L.Start_Control_Get_Peak_WaveLength_And_Power;
		command_send=Arrays.copyOf(command.getIndex(),command.getIndex().length);
		//System.out.println(Arrays.toString(command_send));		
		packet_send = new DatagramPacket(command_send, command_send.length,
				InetAddress.getByName("192.168.0.119"), 4010);	
		packet_recieve= new DatagramPacket(container, container.length);
		this.client.send(packet_send);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public double getWaveLength(byte[] data)
	{		
		if(data[0]==1&&data[1]==12)
		{
			int dataH=byteToInt(data[5]);
			int dataL=byteToInt(data[4]);
			this.peakWaveLength=(dataL+dataH*256.0)/1000.0+1520;
			return this.peakWaveLength;
		}	
		else
		{
			return this.peakWaveLength;
		}
		
	}
	
	public double[] getMutilWaveLength(byte[] data)
	{		
		
		if(data[0]==1&&data[1]==12)
		{
			int len=data[3];
			double[] mutilWaveLength=new double[len];
//			int dataH=0;//byteToInt(data[5]);
//			int dataL=0;//byteToInt(data[4]);
			
			for (int i = 0; i < mutilWaveLength.length; i++) {
				mutilWaveLength[i]=(byteToInt(data[4+i*2])+byteToInt(data[5+i*2])*256.0)/1000.0+1520;
			}
			
			return mutilWaveLength;
			
		}	
		else
		{
		return (double[]) null;
		}
	}

	public double[] getMutilPower(byte[] data)
	{		
		
		if(data[0]==1&&data[1]==16)
		{
			int len=data[3];
			double[] mutilPower=new double[len];
			//int dataH=0;//byteToInt(data[5]);
			//int dataL=0;//byteToInt(data[4]);
			short temp=0;
			for (int i = 0; i < mutilPower.length; i++) {
				//mutilPower[i]=(byteToInt(data[4+i])+byteToInt(data[5+i])*256.0)/1000.0+1520;
				temp=(short)getUnsignedShort((short)(byteToShort(data[4+i*2])+byteToShort(data[5+i*2])*(short)256));
				mutilPower[i]=(float)temp;
				mutilPower[i]= mutilPower[i]/(double)10.0;
				
			}
			
			return mutilPower;
			
		}	
		else
		{
		return (double[]) null;
		}
	}
	
	
	
	

	
	@SuppressWarnings("null")
	public double getPower(byte[] data) {

		if (data[0] == 1 && data[1] == 16) {
			short temp = 0;
			temp = (short) (byteToShort(data[4]) + byteToShort(data[5]) * (short)256);
			double power = (float) temp;
			power = power / (float) 10.0;
			return power;
		}
		else
		{
			return (Double) null;
		}
		
	}
	
	@SuppressWarnings("null")
	public double getSingleData(byte[] data)
	{
		double singleData;
		if(data[0] == 1 && data[1] == 16)
		{
			singleData=this.getPower(data);
			return singleData;
		}
		else if(data[0] == 1 && data[1] == 12)
		{
			singleData=this.getWaveLength(data);
			return singleData;
		}
		else //if(data[0] == 1 && data[1] == 10)
		{
			return (Double) null;
		}		
	}
	
	public double[] getMutilData(byte[] data)
	{
		double[] mutilData;
		if(data[0] == 1 && data[1] == 16)
		{
			mutilData=this.getMutilPower(data);
			return mutilData;
		}
		else if(data[0] == 1 && data[1] == 12)
		{
			mutilData=this.getMutilWaveLength(data);
			return mutilData;
		}
		else //if(data[0] == 1 && data[1] == 10)
		{
			return (double[]) null;
		}		
	}
	




	public static int byteToInt(byte b )
	{
		return b>=0?b:256+b;		
	}	
	public static short byteToShort(byte b )
	{
		return (short) (b>=0?b:256+b);		
	}	
	
	public int getUnsignedShort(short data){ //将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。    
		return data&0x0FFFF ; 
		}
	
	
	
}
