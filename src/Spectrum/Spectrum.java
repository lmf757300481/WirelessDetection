package Spectrum;

import java.util.ArrayList;

import OMP.GSDataParser;

public class Spectrum {
	
	int pixelCount;
	double start_WL;//nm为单位
	double interval_WL;//pm为单位
	double[] wL_Sequence; //nm为单位
	boolean isUpdateSpectrum=false;
	
	/*首要任务是把这个解决*/ 	//double[] power_Sequence;
	//ArrayList<Double> power_Sequence=new ArrayList<Double>();
	double[] power_Sequence=new double[2000];

	public  byte[] recievedAllData=new byte[1000*4];
	
	public void recieveData(byte[] data) 
	{
		
		if(data[0]==1&&data[1]==22)
		{
			if(data[3]==0) //data[3]是包号
			{
				isUpdateSpectrum=false;
				for (byte b : recievedAllData) { 
					b=0x00;//遍历的方法，接收的数组清零
				}
				//System.arraycopy(src, srcPos, dest, destPos, length)
				System.arraycopy(data, 5, recievedAllData, (0+0)*1000, 1000);
			}else if(data[3]==1)
			{
				System.arraycopy(data, 5, recievedAllData, (0+1)*1000, 1000);
			}else if(data[3]==2)
			{
				System.arraycopy(data, 5, recievedAllData, (0+2)*1000, 1000);
			}else if(data[3]==3)
			{
				System.arraycopy(data, 5, recievedAllData, (0+3)*1000, 1000);
				power_Sequence=transfer_Power(recievedAllData);
				isUpdateSpectrum=true;
			}					
		}
		
		
	}
	
	public static double[] transfer_Power(byte[] data) {
		double[] power_Sequence_Tranfered=new double[2000];
		int count=0;
		for (int i = 0; i <= (data.length-2); i=i+2) {
			power_Sequence_Tranfered[count]=GSDataParser.getPower(data[i+1], data[i]);
			count++;			
		}
		return power_Sequence_Tranfered;
	}
}
