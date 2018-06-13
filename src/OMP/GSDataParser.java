package OMP;

public class GSDataParser {
	public static float getPower(byte[] data)////此时传入一个长度为2的数组，低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{		
		byte[] dataRec=data;		
		short Df=TypeConvert.getShort(dataRec,true,2);		
		return (float) (Df/10.0);		
	}
	
	public static float getPower(byte dataL,byte dataH)//低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{				
		byte[] dataRec= new byte[2];		
		dataRec[0]=dataL;
		dataRec[1]=dataH;
		short Df=TypeConvert.getShort(dataRec,true,2);
		return (float) (Df/10.0);		
	}
	
	public static float getgetWaveLength(byte[] data)//此时传入一个长度为2的数组，低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{		
		byte[] dataRec=data;		
		short Df=TypeConvert.getShort(dataRec,true,2);
		return (float) (1520.0+Df/1000.0);		
	}
	
	public static float getWaveLength(byte dataL,byte dataH) //低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{				
		byte[] dataRec= new byte[2];		
		dataRec[0]=dataL;
		dataRec[1]=dataH;
		short Df=TypeConvert.getShort(dataRec,true,2);
		return (float) (1520.0+Df/1000.0);		
	}
	
	public static short getInterval(byte[] data)////此时传入一个长度为2的数组，低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{		
		byte[] dataRec=data;		
		short Df=TypeConvert.getShort(dataRec,true,2);		
		return Df;		
	}
	
	public static short getInterval(byte dataL,byte dataH)//低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{				
		byte[] dataRec= new byte[2];		
		dataRec[0]=dataL;
		dataRec[1]=dataH;
		short Df=TypeConvert.getShort(dataRec,true,2);
		return Df;		
	}
	
	public static short getDataAmount(byte[] data)////此时传入一个长度为2的数组，低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{		
		byte[] dataRec=data;		
		short dataAmount=TypeConvert.getShort(dataRec,true,2);		
		return dataAmount;		
	}
	
	public static short getDataAmount(byte dataL,byte dataH)//低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{				
		byte[] dataRec= new byte[2];		
		dataRec[0]=dataL;
		dataRec[1]=dataH;
		short dataAmount=TypeConvert.getShort(dataRec,true,2);
		return dataAmount;		
	}
	
	public static short getChannelAmount(byte[] data)////此时传入一个长度为2的数组，低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{		
		byte[] dataRec=data;		
		short dataChannelAmount=TypeConvert.getShort(dataRec,true,2);		
		return dataChannelAmount;		
	}
	
	public static short getChannelAmount(byte dataL,byte dataH)//低字节在前，先data[4]（低字节）,后data[5]（高字节）
	{				
		byte[] dataRec= new byte[2];		
		dataRec[0]=dataL;
		dataRec[1]=dataH;
		short dataChannelAmount=TypeConvert.getShort(dataRec,true,2);
		return dataChannelAmount;		
	}
	
	public static int getCount(byte data)
	{
		return data;
	}
	
	public static int getNo(byte data)
	{
		return data;
	}

}
