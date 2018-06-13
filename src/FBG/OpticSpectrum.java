package FBG;

import java.sql.Date;
import java.util.ArrayList;
import OMP.UdpCommuncator;

public class OpticSpectrum {
	
	//Tue Mar 06 16:00:25 CST 2018  [1529.3, 20.0, 1800.0, 8.0, 5.0]
			Date  OpticSpectrumTime;  //光谱产生的时间
			double waveLengthStart;   //波长起始值 单位nm  默认1529.3
			double waveLengthInterval;   //波长间隔值 单位pm  默认20.0
			int count=1800;        //波长点数  默认1800个
			int channelNo;    //通道数  默认0
			int dataFramesCount;   //数据帧数  默认5
			
			double centralWavelength;  //中心波长， 在最后一帧数据中
			double centralPower;  //中心波长的强度， 在最后一帧数据中
			
			byte[] dataFramesSplice=new byte[4*1024];  //第一次默认为5的长度
			
			ArrayList<Double> listWavelengths=new ArrayList<Double>();
			ArrayList<Double> listPowers=new ArrayList<Double>();
			

	public OpticSpectrum() {
		// TODO Auto-generated constructor stub		
	}
	
	public static double parsePwer(byte dataL,byte dataH) //低字节在前
	{
		byte[] dataRec= new byte[2];		
		dataRec[0]=dataL;
		dataRec[1]=dataH;
		short Df=(short) (OMP.UdpCommuncator.byteToInt(dataL)+OMP.UdpCommuncator.byteToInt(dataH)*256);//TypeConvert.getShort(dataRec,true,2);
		return Df/10.0;
//		short dus=(short) (dataL+dataH*256);		
//		return  dus*1.0/10.0;
	}
	

}
