package Temperature;

public interface Ialarming {
	
	boolean isExceedThreshold(double  temperatureThreshhold);//�Ƿ�����ֵ
	boolean isRisingTooFast(double speedThreshhold);
	

}
