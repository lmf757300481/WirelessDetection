package Temperature;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class TemperaturePoints implements Ialarming {
	
	
	private boolean isFull;
	private int maxBufferLength;//���û�������󳤶�
	
	public LinkedList<TemperaturePoint>  buffer =new LinkedList<TemperaturePoint>();
	
	
	public TemperaturePoints(int maxBufferLength, TemperaturePoint firstTemperaturePoint) {
		super();
		this.isFull=false;
		this.maxBufferLength = maxBufferLength;
		this.buffer.add(firstTemperaturePoint);
	}
	public TemperaturePoints( TemperaturePoint firstTemperaturePoint) {
		super();
		this.isFull=false;
		this.maxBufferLength = 60;
		this.buffer.add(firstTemperaturePoint);
	}
	public TemperaturePoints(int maxBufferLength) {
		super();
		this.isFull=false;
		this.maxBufferLength = maxBufferLength;
		
	}
	public TemperaturePoints() {//Ĭ��60����
		super();
		this.maxBufferLength = 60;
		this.isFull=false;
		
	}
    
	
	public void add(TemperaturePoint current)
	{
		if(this.isFull)
		{
			buffer.removeFirst();
			buffer.add(current);
		}
		else
		{
			buffer.add(current);
		}
	}
	
	@Override
 	public boolean isExceedThreshold(double temperatureThreshhold) {
		// TODO Auto-generated method stub
		
		if(buffer.isEmpty()) return false;
		else 
		{
			return buffer.getLast().getTemperature() >= temperatureThreshhold;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isRisingTooFast(double speedThreshhold) {
		// TODO Auto-generated method stub
		if(buffer.isEmpty()||buffer.size()==1) return false;
		else
		{ 
			
			double temperatureSpeed;
			int len=buffer.size();
			TemperaturePoint current=buffer.get(len-1);
			TemperaturePoint last=buffer.get(len-2);
			temperatureSpeed=(current.getTemperature()-last.getTemperature())/(current.getTime().getSeconds()-last.getTime().getSeconds());
			return temperatureSpeed >= speedThreshhold;	
		}
		
		
	}

	public boolean isFull() {
		return buffer.size()==this.maxBufferLength;
	}
	
	public void check(double temperatureThreshhold,double speedThreshhold)
	{
		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
	    String ctime = formatter.format(buffer.getLast().getTime()); 
	    String alarmingInfo=ctime+"�¶��ǣ�"+buffer.getLast().getTemperature();
		if(isExceedThreshold(temperatureThreshhold))
		{
			alarmingInfo+="      �¶ȳ�����ֵ"+"("+temperatureThreshhold+")��";
			System.out.println(alarmingInfo);
		}
		if(isRisingTooFast(speedThreshhold))
		{
			alarmingInfo+="      �¶�����̫�쳬���ٶ���ֵ"+"("+speedThreshhold+")��/s";
			System.out.println(alarmingInfo);
		}
		else
		{
			System.out.println(alarmingInfo);
		}
		
	}
	

}
